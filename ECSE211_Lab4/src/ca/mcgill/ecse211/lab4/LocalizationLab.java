package ca.mcgill.ecse211.lab4;


import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;


public class LocalizationLab {
  public static final EV3LargeRegulatedMotor leftMotor =
      new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));

  public static final EV3LargeRegulatedMotor rightMotor =
      new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));

  //Initialize the UltrasonicSensor
  public static final Port usPort = LocalEV3.get().getPort("S1");

  public static void main(String[] args) {
    int buttonChoice;

    // Instantiate all objects
    final TextLCD t = LocalEV3.get().getTextLCD();
    Odometer odometer = new Odometer(leftMotor, rightMotor);
    Navigation navigation = new Navigation(leftMotor, rightMotor, odometer);
    @SuppressWarnings("resource") // Because we don't bother to close this resource
    SensorModes usSensor = new EV3UltrasonicSensor(usPort); // usSensor is the instance
    SampleProvider usDistance = usSensor.getMode("Distance"); // usDistance provides samples from
                                                              // this instance
    float[] usData = new float[usDistance.sampleSize()]; // usData is the buffer in which data are
                                                          // returned
    UltrasonicPoller usPoll = new UltrasonicPoller(usDistance, usData);
    OdometryDisplay odometryDisplay = new OdometryDisplay(odometer, t, usPoll);
    UltrasonicLocalizer usLocal = new UltrasonicLocalizer(odometer, usPoll, leftMotor, rightMotor, navigation);

    do {
      // clear the display
      t.clear();

      // ask the user whether to navigate course with or without avoidance
      t.drawString("< Left   | Right >", 0, 0);
      t.drawString("         |        ", 0, 1);
      t.drawString(" Rising  | Falling  ", 0, 2);
      t.drawString(" edge    |  edge  ", 0, 3);
      t.drawString("local    | local", 0, 4);

      buttonChoice = Button.waitForAnyPress();
    } while (buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT);


    if (buttonChoice == Button.ID_LEFT) {
      t.clear();
      //System.exit(0);
      // start all necessary threads
      odometer.start();
      odometryDisplay.start();
      usPoll.start();
      //navigation.start();

      usLocal.risingEdge();


      /* t.drawString("Press ", 0, 0);
      t.drawString(" to ", 0, 1);
      t.drawString("start", 0, 2);
      t.drawString("light", 0, 3);
      t.drawString("localization", 0, 4);*/
      buttonChoice = Button.waitForAnyPress();



    } else {
      // clear the display
      t.clear();

      // start all necessary threads
      odometer.start();
      odometryDisplay.start();
      usPoll.start();
      //navigation.start();
      usLocal.fallingEdge();

      /* t.drawString("Press ", 0, 0);
      t.drawString(" to ", 0, 1);
      t.drawString("start", 0, 2);
      t.drawString("light", 0, 3);
      t.drawString("localization", 0, 4);*/
      buttonChoice = Button.waitForAnyPress();


    }

    while (Button.waitForAnyPress() != Button.ID_ESCAPE);
    System.exit(0);
  }
}
