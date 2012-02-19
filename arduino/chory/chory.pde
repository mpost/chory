#include <Wire.h>
#include <Servo.h>

#include <Max3421e.h>
#include <Usb.h>
#include <AndroidAccessory.h>

#define SERVO_HEAD 2
#define SERVO_LEFT_ARM 3
#define SERVO_RIGHT_ARM 4
#define SERVO_TORSO 5

#define SIZE_OF_SERVOS 4

AndroidAccessory acc("Moritz Post",
"Chory",
"Mechanical Chory",
"1.0",
"http://gplus.to/mpost",
"0000000012345678");

Servo servos[SIZE_OF_SERVOS];

void setup()
{
  Serial.begin(115200);

  servos[0].attach(SERVO_HEAD);
  servos[1].attach(SERVO_LEFT_ARM);
  servos[2].attach(SERVO_RIGHT_ARM);
  servos[3].attach(SERVO_TORSO);

  resetServos();

  acc.powerOn();
}

void loop()
{
  /* A communication message consists of three parts: 
   * component: the type of phyisical element to adress (eg servo)
   * target: which specific instance of a component to address
   * value: the value to set on the target
   */
  byte msg[3];
  
  if (acc.isConnected()) {
    int len = acc.read(msg, sizeof(msg), 1);
    if (len > 0) {
      // assumes only one command per packet
      if (msg[0] == 0x0) {
        if (msg[1] == 0x2) {
          char s[4];
          snprintf(s, 4, "%d", msg[2]);
          Serial.println(s);
        }
        servos[msg[1]].write(msg[2]);
      } 
    }
  } else {
    // reset outputs to default values on disconnect
    resetServos();
  }
  delay(10);
}

void resetServos() {
  for (int i = 0; i < SIZE_OF_SERVOS; i = i + 1) {
    servos[i].write(90);
  }
}





