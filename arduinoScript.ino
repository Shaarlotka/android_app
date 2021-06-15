const int Pin1 = 2; // up
const int Pin2 = 3; // down
const int Pin3 = 4; // left
const int Pin4 = 5; // right
String message = "";; // Command message from Android

void setup() {
  pinMode(Pin1, OUTPUT);
  pinMode(Pin2, OUTPUT);
  digitalWrite(Pin1, HIGH);
  digitalWrite(Pin2, HIGH);
  pinMode(Pin3, OUTPUT);
  pinMode(Pin4, OUTPUT);
  digitalWrite(Pin3, HIGH);
  digitalWrite(Pin4, HIGH);
  Serial.begin(9600);
}

void loop() {
  if (Serial.available() > 0) {
    message += (char)Serial.read();
    Serial.println(message);
  }

  //turn left
  if (message == "l"){
    digitalWrite(Pin3, LOW);
    digitalWrite(Pin4, HIGH);
  }

  //turn right
  if (message == "r"){
    digitalWrite(Pin3, HIGH);
    digitalWrite(Pin4, LOW);
  }

  //stop turning
  if (message == "s"){
    digitalWrite(Pin3, HIGH);
    digitalWrite(Pin4, HIGH);
  }
    
  //go ahead
  if (message == "u"){
    digitalWrite(Pin1, LOW);
    digitalWrite(Pin2, HIGH);
  }

  //go back 
  if (message == "d"){
    digitalWrite(Pin1, HIGH);
    digitalWrite(Pin2, LOW);
  }

  //stop driving 
  if (message == "m"){
    digitalWrite(Pin1, HIGH);
    digitalWrite(Pin2, HIGH);
  }
}