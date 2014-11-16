char server[] = "hackduke.my.to";
char url[] = "/update.php?room=8";
int redLed = D0;
int yellowLed = D2;
int greenLed = D1;
int pir1 = D4;
int pir2 = D5;

int pir1State = LOW;
int pir2State = LOW;

int val1 = 0;
int val2 = 0;

TCPClient client;

void setup() {
    // Serial.begin(9600);
    //init pinmodes
    pinMode(redLed, OUTPUT);
    pinMode(greenLed, OUTPUT);
    pinMode(yellowLed, OUTPUT);
    pinMode(pir1, INPUT);
    pinMode(pir2, INPUT);
    pinMode(D6, INPUT);
    
    digitalWrite(redLed, LOW);
    digitalWrite(greenLed, LOW);
    digitalWrite(yellowLed, LOW);
}

void loop() {
    // if(digitalRead(D6)==HIGH){
    //     digitalWrite(D0, HIGH);
    //     ping();
    //     delay(10000);
    //     digitalWrite(D0, LOW);
    //     delay(1000);
    // }
    
    digitalWrite(greenLed, HIGH);
    delay(2000);
    digitalWrite(greenLed, LOW);
    
    val1 = digitalRead(pir1);
    val2 = digitalRead(pir2);
    
    if ((val1 == HIGH) || (val2 == HIGH)){
        digitalWrite(yellowLed, HIGH);
        ping();
        delay(10000);
        if(pir1State == LOW){
            pir1State = HIGH;
        }
        if(pir2State == LOW){
            pir2State = HIGH;
        }
    }
    else{
        digitalWrite(yellowLed, LOW);
        if(pir1State == HIGH){
            pir1State = LOW;
        }
        if(pir2State == HIGH){
            pir2State = LOW;
        }
    }
}
void ping(){ //updates room state on server
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
        // Serial.print("sent and closed-bytes: ");
        // Serial.println(count);
        return;
     }
    else {
        client.flush();
        client.stop();
        // Serial.println("Not connected");
        return;
    }
}
