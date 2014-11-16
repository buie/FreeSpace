<?php

// Require
require_once('Config.php');
require_once('helper.php');
require_once(Config::AR_INCLUDE_DIR . '/ActiveRecord.php');

// Init Active Record
ActiveRecord\Config::initialize(function($cfg) {
    $cfg->set_model_directory(Config::AR_MODEL_DIR);
    $cfg->set_connections(array(
        'development' => Config::SQL_CONNECTION));
});

// Horrible practices, but too tired:
$data = array();

$data['phone'] = $_GET['phone'];
$data['room_id'] = $_GET['room_id'];

$listener = new Listeners($data);
$listener->save();