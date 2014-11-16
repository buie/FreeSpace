<?php

require_once('Config.php');
require_once('helper.php');
require_once(Config::TW_INCLUDE_DIR . '/Services/Twilio.php');

// set your AccountSid and AuthToken from www.twilio.com/user/account
$AccountSid = "AC2c69dbe2f2426c032dc2791a53395e5a";
$AuthToken = "ee84cbb54d0a652bae01fe5ca3df8a16";
$client = new Services_Twilio($AccountSid, $AuthToken);
for ($i=0; $i<5; $i++) {
$message = $client->account->messages->create(array(
    "From" => "-240-667-7894",
    "To" => "423-972-3753",
    "Body" => "Test message!",
));
}
// Display a confirmation message on the screen
echo "Sent message {$message->sid}";