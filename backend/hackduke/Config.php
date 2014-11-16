<?php

/*
 * Do some config
 */

ini_set('display_errors',1);
ini_set('display_startup_errors',1);
error_reporting(-1);

/*
 * Time Zone
 */
date_default_timezone_set('America/New_York');

/*
 * Global Configuration object
 */
class Config {
    const AR_MODEL_DIR = 'php-activerecord/models';
    const AR_INCLUDE_DIR = 'php-activerecord';
    
    const FB_BASE_URL = 'https://dazzling-heat-6582.firebaseio.com/';
    const FB_TOKEN_DIR = 'firebase-token-generator-php';
    const FB_JWT_DIR = 'php-jwt';
    
    const SQL_CONNECTION = 'mysql://root:bitnami@localhost/hackduke';
    const SITE_BASE_HTTP = 'http://hackduke.my.to/';
    
    const TIME_FORMAT_SQL = 'Y-m-d H:i:s';
    const DATE_FORMAT_SQL = 'Y-m-d';
    
    // Time to become inactive
    public static $inactivePeriod = 10;
}