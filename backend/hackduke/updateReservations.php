<?php

// Require
require_once('Config.php');
require_once('NewFirebase.php');
require_once('helper.php');
require_once('TwentyFive.php');
require_once(Config::AR_INCLUDE_DIR . '/ActiveRecord.php');
require_once(Config::FB_TOKEN_DIR . '/FirebaseToken.php');
require_once(Config::FB_JWT_DIR . '/Authentication/JWT.php');


// Init Active Record
ActiveRecord\Config::initialize(function($cfg) {
    $cfg->set_model_directory('/var/www/public_html/hackduke/php-activerecord/models');
    $cfg->set_connections(array(
        'development' => Config::SQL_CONNECTION));
});

$asdf = new TwentyFive();
$asdf->update_database();