int led = D0;
int led2 = D7;
int pirState = LOW;
int val = 0;
int inputPin = D2;

void setup() {
  pinMode(led, OUTPUT);
  pinMode(led2, OUTPUT);
  pinMode(inputPin, INPUT);
}

void loop() {
  val = digitalRead(inputPin);
  if (val == HIGH) {
    digitalWrite(led, HIGH);
    digitalWrite(led2, HIGH);
    if (pirState == LOW) {
      pirState = HIGH;
    }
  } else {
    digitalWrite(led, LOW);
    digitalWrite(led2, LOW);
    if (pirState == HIGH){
      pirState = LOW;
    }
  }
}
