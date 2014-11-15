<?php

ini_set('display_errors',1);
ini_set('display_startup_errors',1);
error_reporting(-1);
// Require
require_once('Config.php');
require_once(Config::AR_INCLUDE_DIR . '/ActiveRecord.php');
require_once(Config::FB_INCLUDE_DIR . '/firebaseLib.php');


// Init Active Record
ActiveRecord\Config::initialize(function($cfg) {
    $cfg->set_model_directory(Config::AR_MODEL_DIR);
    $cfg->set_connections(array(
        'development' => Config::SQL_CONNECTION));
});

// Make sure data is passed
if (!isset($_GET['room'])) {
    exit;
}

// Room number
$data = array(
    'room_id' => $_GET['room'],
);

// Record new detection, die on bad data
$detection = new Detections($data);
if ($detection->is_valid()) {
    $detection->save();
} else {
    exit;
}

// Push to firebase
$firebase = new Firebase(Config::FB_BASE_URL);
$firebase->push(Config::FB_BASE_URL . 'detections.json', $data);

// Say something
$date = date(DATE_COOKIE);
echo("Successfully recorded time for Room $data[room_id] at time $date");