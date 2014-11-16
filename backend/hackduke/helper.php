<?php

/*
 * Basically it's strtotime but assuming the input string is in GMT, as in SQL
 */
function gmt_string_to_unix($string) {
    $timezone = date_default_timezone_get();
    date_default_timezone_set('GMT');
    $unix = strtotime($string);
    date_default_timezone_set($timezone);
    return $unix;
}

function date_to_sql($date) {
    return date_convert(new DateTime($date), '', Config::TIME_FORMAT_SQL);
}


/*
 * Does pretty much what it says
 */
function date_convert($date, $fromFormat, $toFormat) {
    
    // False, NULL, zero, string zero, or empty string
    if ($date == FALSE) {
        return NULL;
    }
    
    // Optionally use DateTime object
    if ($date instanceof DateTime) {
        $isDT = TRUE;
    } else {
        $isDT = FALSE;
    }
    
    // If all the numbers in the date string are zero, i.e. 0000-00-00 or 00/00/0000
    if (!$isDT && ((int)preg_replace("[^0-9]", "", $date) === 0)) {
        return NULL;
    }
    
    try {
        
        // Easy if we are already DateTime
        if ($isDT) {
            return $date->format($toFormat); 
        } else {

            // Try to parse
            $date = DateTime::createFromFormat($fromFormat, $date);
            if ($date === FALSE) {
                throw new Exception('DateTime::createFromFormat returned FALSE, parsing failed');
            }
            $errors = DateTime::getLastErrors();

            // If parsing successful
            if (($errors['warning_count'] == 0) && ($errors['error_count'] == 0) && (is_a($date, 'DateTime'))) {
                return $date->format($toFormat);
            } else {
                throw new Exception('DateTime not FALSE but still gave errors. Parsing failed');
            }
        }

    } catch (Exception $ex) {
        
        return null;
        
    }        
}