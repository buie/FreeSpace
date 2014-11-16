char server[] = "hackduke.my.to";
char url[] = "/update.php?room=8";
int redLed = D0;
int greenLed = D1;
int yellowLed = D2;
int pir1 = D4;
int pir2 = D5;

TCPClient client;

void setup() {
    Serial.begin(9600);
    //init pinmodes
    pinMode(redLed, OUTPUT);
    pinMode(greenLed, OUTPUT);
    pinMode(yellowLed, OUTPUT);
    pinMode(pir1, INPUT);
    pinMode(pir2, INPUT);
    //write green high by default
    digitalWrite(greenLed, HIGH);
}

void loop() {
    
    if((digitalRead(pir1) == HIGH) || (digitalRead(pir2) == HIGH)){    //motion detected
        Serial.println("Starting request");
        int retval = ping();
        Serial.print("Returns ");
        Serial.println(retval);
        digitalWrite(yellowLed, HIGH);
        delay(10000);
    }
    else{
        digitalWrite(yellowLed, LOW);
        delay(100);
    }
}

int ping(){ //updates room state on server
    client.connect(server, 80);
    if (client.connected()) {
        Serial.println("Connected to server.");
        client.print("GET ");
        client.print(url);
        client.println(" HTTP/1.1");
        client.print("Host: ");
        client.println(server);
        client.println("Connection: close");
        client.println();

        unsigned int count = 0;
        unsigned long lastTime = millis();
        while( client.available()==0 && millis()-lastTime<10000) { //ten second timeout
          }  //do nothing
        lastTime = millis();
        while( client.available() && millis()-lastTime<10000 ) {  //ten seconds
          client.read();  //flush data
          count++;
        }
        client.flush();  //for safety
        delay(400);
        client.stop();
        Serial.print("sent and closed-bytes: ");
        Serial.println(count);
    return 1;
     }
    else {
        client.flush();
        client.stop();
        Serial.println("Not connected");
        return 0;
    }
}
