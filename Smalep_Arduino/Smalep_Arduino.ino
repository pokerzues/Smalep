//Smalep V2.0 for Arduino
//现在存在在windows操作系统下编译下载至Arduino板后出现液晶显示不正常的问题，暂无解决办法。建议至Ubuntu环境下进行编译下载

#define PT_USE_TIMER
#include "pt.h"
#include <dht11.h>
#include <Wire.h> 
#include <LiquidCrystal_I2C.h>
#include "DS3231.h"

dht11 DHT11;
DS3231 RTC;
LiquidCrystal_I2C lcd(0x27, 16, 2);

#define CmdNum 4
#define SensorNum 3

#define LightSensor_pin  0  //a0
#define DHT11_pin  2 //d2


#define Light_pin  7  //d7
#define Humidifier_pin  8 //d8
#define Fan_pin  9  //d9
#define Heat_pin  10  //d10

int cmd[CmdNum]={0,0,0,0};
int sensor[SensorNum]={0,0,0};
char weekDay[][4] = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat" , "Sun"};
DateTime now;

int DHT11Check;
char Rcv[1];
int Reverse;
int Counti;
int count_dht11=0;

//-------------------------------------------------------------------------------

static struct pt Sensor;
static struct pt Cmd;
static struct pt Display;

//-------------------------------------------------------------------------------

void setup() {
  Serial.begin(9600);
  delay(500);

  
  pinMode(Light_pin, OUTPUT);
  pinMode(Humidifier_pin, OUTPUT);
  pinMode(Fan_pin, OUTPUT);
  pinMode(Heat_pin, OUTPUT);

  Wire.begin();
  RTC.begin();

  lcd.init();
  lcd.init();
  lcd.backlight();

  lcd.init();
  lcd.init();
  lcd.backlight();

  PT_INIT(&Sensor);
  PT_INIT(&Cmd);
  PT_INIT(&Display);
}

//-------------------------------------------------------------------------------
void loop() {
  Sensor_entry(&Sensor);
  Cmd_entry(&Cmd);
  Display_entry(&Display);
}

//-------------------------------------------------------------------------------
static int Sensor_entry(struct pt *pt) {
  PT_BEGIN(pt);

  sensor[2] = analogRead(LightSensor_pin);
  
  if(count_dht11==50){
    DHT11Check = DHT11.read(DHT11_pin);
    sensor[0] = DHT11.temperature;
    sensor[1] = DHT11.humidity;
  }
  count_dht11++;
  

  Serial.print("{");
  Serial.print(sensor[0]);
  Serial.print("+");
  Serial.print(sensor[1]);
  Serial.print("+");
  Serial.print(sensor[2]);
  Serial.println("}");

  PT_TIMER_DELAY(pt, 100);
  
  PT_END(pt);
}

//-------------------------------------------------------------------------------
static int Cmd_entry(struct pt *pt) {
  PT_BEGIN(pt);

  while (Serial.available()) {
    Rcv[0] = Serial.read();
    if (Rcv[0] == '{') {
      Reverse = 0;
      Counti = 0;
    } else if (Rcv[0] != '{' && Rcv[0] != '}' && Rcv[0] != '+') {
      Reverse = Reverse * 10 + atoi(Rcv);
    } else if (Rcv[0] == '+' || Rcv[0] == '}') {
//      if (cmd[Counti] != Reverse) {
//        ResetCmd = true;
//      }
      cmd[Counti] = Reverse;
      Reverse = 0;
      Counti++;
    }
  }
  
  analogWrite(Light_pin, cmd[0]);
  
  if(cmd[1]==1){
    digitalWrite(Humidifier_pin, HIGH);
  }else{
    digitalWrite(Humidifier_pin, LOW);
  }

  analogWrite(Fan_pin, cmd[2]);

  if(cmd[3]==1){
    digitalWrite(Heat_pin, HIGH);
  }else{
    digitalWrite(Heat_pin, LOW);
  } 

  PT_END(pt);
}
//-------------------------------------------------------------------------------
static int Display_entry(struct pt *pt) {
  PT_BEGIN(pt);

//  if (Person == true) {
//    lcd.setBacklight(true);
//  } else {
//    lcd.setBacklight(false);
//  }
  now = RTC.now();

  //first line
  lcd.setCursor(1, 0);
  lcd.print(now.year());
  lcd.setCursor(5, 0);
  lcd.print("/");
  lcd.setCursor(6, 0);
  if (now.month() < 10) {
    lcd.print("0");
    lcd.setCursor(7, 0);
    lcd.print(now.month());
  } else {
    lcd.print(now.month());
  }
  lcd.setCursor(8, 0);
  lcd.print("/");
  lcd.setCursor(9, 0);
  if (now.date() < 10) {
    lcd.print("0");
    lcd.setCursor(10, 0);
    lcd.print(now.date());
  } else {
    lcd.print(now.date());
  }
  lcd.setCursor(12, 0);
  lcd.print(weekDay[now.dayOfWeek() - 1]);

  //second line
  lcd.setCursor(1, 1);
  if (now.hour() < 10) {
    lcd.print("0");
    lcd.setCursor(2, 1);
    lcd.print(now.hour());
  } else {
    lcd.print(now.hour());
  }
  lcd.setCursor(3, 1);
  lcd.print(":");
  lcd.setCursor(4, 1);
  if (now.minute() < 10) {
    lcd.print("0");
    lcd.setCursor(5, 1);
    lcd.print(now.minute());
  } else {
    lcd.print(now.minute());
  }

  lcd.setCursor(7, 1);
  if (sensor[0] < 10) {
    lcd.print(" ");
    lcd.setCursor(8, 1);
    lcd.print(sensor[0]);
  } else {
    lcd.print(sensor[0]);
  }
  lcd.setCursor(9, 1);
  lcd.print("^C");

  lcd.setCursor(12, 1);
  if (sensor[1] < 10) {
    lcd.print(" ");
    lcd.setCursor(13, 1);
    lcd.print(sensor[1]);
  } else {
    lcd.print(sensor[1]);
  }
  lcd.setCursor(14, 1);
  lcd.print("%");

  PT_TIMER_DELAY(pt, 1000);

  PT_END(pt);
}
