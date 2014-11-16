<?php

// Require
require_once('Config.php');
require_once('NewFirebase.php');
require_once(Config::AR_INCLUDE_DIR . '/ActiveRecord.php');
require_once(Config::FB_TOKEN_DIR . '/FirebaseToken.php');
require_once(Config::FB_JWT_DIR . '/Authentication/JWT.php');


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

// Make a firebase token
$tokenGen = new Services_FirebaseTokenGenerator('FFGDc51oHQj2gaDsMBy0lj6ADZ5hbFxPWuz9Sppn');
$token = $tokenGen->createToken(array("uid" => "1"));

// Push to firebase
$data['timestamp'] = time();
$firebase = new NewFirebase('https://dazzling-heat-6582.firebaseio.com/detections.json', $token);
$firebase->push($data);

// Say something
$date = date(DATE_COOKIE);
echo("Successfully recorded time for Room $data[room_id] at time $date");