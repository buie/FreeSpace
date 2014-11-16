//init server
char server[] = "hackduke.my.to";
char url[] = "/update.php?room=8";

//init led pins
int redLed = D0;
int yellowLed = D2;
int greenLed = D1;

//init pir sensor pins
int pir1 = D4;
int pir2 = D5;

//init pir motion states
int pir1State = LOW;
int pir2State = LOW;

//init read data to 0
int val1 = 0;
int val2 = 0;

TCPClient client;

void setup() {
    //init outputs
    pinMode(redLed, OUTPUT);
    pinMode(greenLed, OUTPUT);
    pinMode(yellowLed, OUTPUT);
    
    //init inputs
    pinMode(pir1, INPUT);
    pinMode(pir2, INPUT);
    pinMode(D6, INPUT);
    
    //init led states
    digitalWrite(redLed, LOW);
    digitalWrite(greenLed, LOW);
    digitalWrite(yellowLed, LOW);
}

void loop() {
    val1 = digitalRead(pir1); // reads sensor 1
    val2 = digitalRead(pir2); // reads sensor 2
    
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
        return;
     }
    else {
        client.flush();
        client.stop();
        return;
    }
}
