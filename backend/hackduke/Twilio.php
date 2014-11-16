<?php

require_once('Config.php');
require_once('helper.php');
require_once(Config::AR_INCLUDE_DIR . '/ActiveRecord.php');
require_once(Config::TW_INCLUDE_DIR . '/Services/Twilio.php');

class Twilio {
    
    private $client;
    
    public function __construct() {
        $AccountSid = "AC2c69dbe2f2426c032dc2791a53395e5a";
        $AuthToken = "ee84cbb54d0a652bae01fe5ca3df8a16";
        $this->client = new Services_Twilio($AccountSid, $AuthToken);
    }
    
    public function send_text($to, $message) {
        $message = $this->client->account->messages->create(array(
            "From" => "240-667-7894",
            "To" => $to,
            "Body" => $message,
        ));
        $id = $message->sid;
    }
    
    public function check_listeners() {
        $join = 'JOIN rooms AS t1 ON (listeners.room_id = t1.id) LEFT JOIN detections AS t2 ON (t1.latest_time = t2.id)';
        $listeners = Listeners::find('all', array(
            'select' => 'listeners.`id`, listeners.`phone`, t2.`timestamp`,t1.`name`',
            'conditions' => array(
                '(TIMESTAMPDIFF(MINUTE, `timestamp`, CURRENT_TIMESTAMP)) > ' . Config::$inactivePeriod
            ),
            'joins' => $join
        ));
        foreach ($listeners as $listener) {
            $this->send_text($listener->phone, "$listener->name is now open!");
            $listener->delete();
            echo "Sent text to $listener->phone: $listener->name is now open!";
        }
    }
}

// Init Active Record
ActiveRecord\Config::initialize(function($cfg) {
    $cfg->set_model_directory(Config::AR_MODEL_DIR);
    $cfg->set_connections(array(
        'development' => Config::SQL_CONNECTION));
});

$twilio = new Twilio();
$time = time();

while (true) {
    $twilio->check_listeners();
    sleep(5);
    if (time() - $time > 58) {
        exit;
    }
}