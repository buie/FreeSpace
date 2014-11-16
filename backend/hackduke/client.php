<?php

// Require
require_once('Config.php');
require_once('helper.php');
require_once(Config::AR_INCLUDE_DIR . '/ActiveRecord.php');

// What we will return
$json = array();
$json['timestamp'] = time();
$json['error'] = "";

// Init Active Record
ActiveRecord\Config::initialize(function($cfg) {
    $cfg->set_model_directory(Config::AR_MODEL_DIR);
    $cfg->set_connections(array(
        'development' => Config::SQL_CONNECTION));
});

// What are they asking for?
if (!isset($_GET['request'])) {
    $json['error'] = "Please specify a request";
    echo json_encode($json);
    exit;
} else {
    $request = preg_replace('/[^a-zA-Z]/', '', $_GET['request']);
}

switch ($request) {
    case 'rooms':
        
        // Die on no department
        if (!isset($_GET['department'])) {
                $json['error'] = "Please give a department";
                echo json_encode($json);
            exit;
        }  
        
        // Die on invalid department
        try {
            $department = (int)$_GET['department'];
        } catch (Exception $ex) {
            $json['error'] = "Bad department";
                echo json_encode($json);
            exit;
        }
        
        // Joins R cool
        $join = 'LEFT JOIN detections AS t1 ON(latest_time = t1.id)';
        
        // Get rooms
        $rooms = Rooms::find('all', array(
            'conditions' => array(
                'department = ?',
                $department
            ),
            'joins' => $join,
            'select' => 'rooms.*, t1.timestamp'
        ));
        
        // Make some nice data
        $json['rooms'] = array();
        foreach ($rooms as $sqlRoom) {
            $room = array();
            $room['id'] = $sqlRoom->id;
            $room['name'] = $sqlRoom->name;
            $room['description'] = $sqlRoom->description;
            $room['capacity'] = $sqlRoom->capacity;
            $room['occupied'] = false;
            
            if (is_null($sqlRoom->timestamp)) {
                $room['latest_timestamp'] = null;
            } else {
                $room['latest_timestamp'] = gmt_string_to_unix($sqlRoom->timestamp);
                if (time() - $room['latest_timestamp'] > Config::$inactivePeriod) {
                    $room['occupied'] = true;
                }
            }
            $json['rooms'][] = $room;
        }
}

echo json_encode($json);
