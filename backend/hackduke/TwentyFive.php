<?php

/**
 * Description of 25Live
 *
 * @author andale
 */
class TwentyFive {
    
    private $liveRoomIDs = array();
    private $curl;
    
    private $headers = array(
        'Host: 25live.collegenet.com',
        'User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:33.0) Gecko/20100101 Firefox/33.0',
        'Accept: application/xml, text/xml, */*; q=0.01',
        'Accept-Language: en-US,en;q=0.5',
        'DNT: 1',
        'X-Requested-With: XMLHttpRequest',
        'Referer: https://25live.collegenet.com/duke/mobile.html',
        'Connection: keep-alive'
    );
    
    public function __construct() {
        $this->curl = curl_init();
        curl_setopt_array($this->curl, array(
            CURLOPT_RETURNTRANSFER => true,
            CURLOPT_HTTPHEADER => $this->headers,
            CURLOPT_ENCODING => 'gzip,deflate'
        ));
    }
    
    private function fetch_room_numbers() {
        $rooms = Rooms::find('all');
        $this->liveRoomIDs = array();
        foreach ($rooms as $room) {
            $this->liveRoomIDs[] = (int)$room->live_id;
        }
    }
    
    private function fetch_page($url) {
        curl_setopt($this->curl, CURLOPT_URL, $url);
        return curl_exec($this->curl);
    }
    
    public function reserved_now($roomID) {
        $room = Rooms::find($roomID);
        $nowSQL = date_to_sql('now');
        $liveID = $room->live_id;
        $reservations = Reservations::find('all', array(
            'conditions' => array(
                'room_id = ? AND begin_time < ? AND end_time > ?',
                $liveID,
                $nowSQL,
                $nowSQL
            )
        ));
        if (count($reservations) < 1) {
            return false;
        } else {
            return true;
        }
    }
    
    public function upcoming_reservations($roomID, $time = 'now') {
        $room = Rooms::find($roomID);
        
        // This is temporary for hack duke presentation
        
        
        $nowSQL = date_to_sql('now');
        $futureSQL = date_to_sql('+5 hours');
        $liveID = $room->live_id;
        $reservations = Reservations::find('all', array(
            'conditions' => array(
                'room_id = ? AND begin_time >= ? AND begin_time < ?',
                $liveID,
                $nowSQL,
                $futureSQL
            )
        ));
        
        $json = array();
        if ($this->reserved_now($roomID)) {
            $message = array(
                'time' => 'now',
                'message' => 'This room is currently reserved.'
            );
        } else {
            $message = array(
                'time' => 'now',
                'message' => 'This room is not currently reserved.'
            );
        }
        $json[] = $message;
        foreach ($reservations as $reservation) {
            $message = array();
            $message['time'] = date_convert($reservation->begin_time, Config::TIME_FORMAT_SQL, 'g:i');
            $message['time'] .= '-';
            $message['time'] .= date_convert($reservation->end_time, Config::TIME_FORMAT_SQL, 'g:ia');
            
            $message['message'] = $reservation->title . ', ' . $reservation->name;
            
            $json[] = $message;
        }
        
        return $json;
    }
    
    public function update_database() {
        $this->fetch_room_numbers();
        //$this->liveRoomIDs = array(964);
        
        $startDate = date('Ymd');
        $endDate = date('Ymd', time() + 60 * 60 * 24 * 6);
        
        foreach ($this->liveRoomIDs as $liveRoomID) {
            $url = "https://25live.collegenet.com/25live/data/duke/run/rm_reservations.xml?space_id=$liveRoomID&scope=extended&include=blackouts+closed+pending+related+text+attributes&start_dt=$startDate&end_dt=$endDate";
            
            $raw = $this->fetch_page($url);
            file_put_contents("raw.txt", $raw);
            $xml = simplexml_load_string($raw, null, null, 'r25', true);
            
            $previousSQL = Reservations::find('all', array('select' => 'id'));
            $previous = array();
            foreach ($previousSQL as $entry) {
                $previous[$entry->id] = false;
            }
         
            foreach ($xml->children('r25', true) as $spaceReservation) {
                
                $id = (int)$spaceReservation->reservation_id;
                if (!isset($previous[$id])) {
                    $reservation = new Reservations();
                    $reservation->id = $id;
                } else {
                    $reservation = Reservations::find($id);
                }
                
                $reservation->begin_time = date_to_sql($spaceReservation->reservation_start_dt);
                $reservation->end_time = date_to_sql($spaceReservation->reservation_end_dt);
                $reservation->registered_count = (int)$spaceReservation->act_head_count;
                
                $space = $spaceReservation->spaces[0];
                $reservation->room_id = (int)$space->space_id;
                
                $event = $spaceReservation->event[0];
                $reservation->name = (string)$event->event_name;
                $reservation->title = (string)$event->event_title;
                
                $reservation->save();
            } 
        }
    }
}

// Require
require_once('Config.php');
require_once('NewFirebase.php');
require_once('helper.php');
require_once(Config::AR_INCLUDE_DIR . '/ActiveRecord.php');
require_once(Config::FB_TOKEN_DIR . '/FirebaseToken.php');
require_once(Config::FB_JWT_DIR . '/Authentication/JWT.php');


// Init Active Record
ActiveRecord\Config::initialize(function($cfg) {
    $cfg->set_model_directory(Config::AR_MODEL_DIR);
    $cfg->set_connections(array(
        'development' => Config::SQL_CONNECTION));
});

$asdf = new TwentyFive();
//$asdf->update_database();
var_dump($asdf->upcoming_reservations(8));