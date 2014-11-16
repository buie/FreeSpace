<?php

/**
 * Description of firebase
 *
 * @author andale
 */
class NewFirebase {
    
    public $url, $token;
    
    private $curl;
    
    public function __construct($url = "", $token = "") {
        $this->url = $url;
        $this->token = $token;
        
        $this->curl = curl_init($url . '?auth=' . $token);
        curl_setopt($this->curl, CURLOPT_RETURNTRANSFER, true);
    }
    
    public function push($data) {
        curl_setopt_array($this->curl, array(
            CURLOPT_CUSTOMREQUEST => 'POST',
            CURLOPT_POSTFIELDS => json_encode($data),
            CURLOPT_RETURNTRANSFER => true
        ));
        return curl_exec($this->curl);
    }
}
