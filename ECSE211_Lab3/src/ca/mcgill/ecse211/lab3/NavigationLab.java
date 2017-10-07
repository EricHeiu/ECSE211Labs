package ca.mcgill.ecse211.lab3;


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


public class NavigationLab {
  public static final EV3LargeRegulatedMotor leftMotor =
      new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));

  public static final EV3LargeRegulatedMotor rightMotor =
      new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));

   public static double[] coordinates = {2, 1, 1, 1, 1, 2, 2, 0};
  //public static double[] coordinates = {0, 2, 1, 1, 2, 2, 2, 1, 1, 0};
  // public static double[] coordinates = {1, 1, 0, 2, 2, 2, 2, 1, 1, 0};
  // public static double[] coordinates = {0, 1, 1, 2, 1, 0, 2, 1, 2, 2};



  public static void main(String[] args) {
    int buttonChoice;

    // Instantiate all objects
    final TextLCD t = LocalEV3.get().getTextLCD();
    Odometer odometer = new Odometer(leftMotor, rightMotor);
    OdometryDisplay odometryDisplay = new OdometryDisplay(odometer, t);
    Navigation navigation = new Navigation(leftMotor, rightMotor, odometer);

    do {
      // clear the display
      t.clear();

      // ask the user whether to navigate course with or without avoidance
      t.drawString("< Left  | Right >", 0, 0);
      t.drawString("        |        ", 0, 1);
      t.drawString(" Nav    | Nav  ", 0, 2);
      t.drawString(" with   | with no  ", 0, 3);
      t.drawString("avoid   | avoid ", 0, 4);

      buttonChoice = Button.waitForAnyPress();
    } while (buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT);


    if (buttonChoice == Button.ID_LEFT) {
      t.clear();
      System.exit(0);

    } else {
      // clear the display
      t.clear();

      t.drawString("Press ", 0, 0);
      t.drawString(" to ", 0, 1);
      t.drawString("start", 0, 2);
      buttonChoice = Button.waitForAnyPress();

      // start all necessary threads
      odometer.start();
      odometryDisplay.start();
      navigation.start();
    }

    while (Button.waitForAnyPress() != Button.ID_ESCAPE);
    System.exit(0);
  }


}
