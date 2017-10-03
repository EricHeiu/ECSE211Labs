// Lab2.java

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
	private static final EV3LargeRegulatedMotor leftMotor =
			new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));

	private static final EV3LargeRegulatedMotor rightMotor =
			new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));

	public static final double WHEEL_RADIUS = 2.1;
	public static final double TRACK = 14.33; 
	
	public static double[][] coordinates = {{0,1}, {1,2}, {1,0}, {2,1}, {2,2}};
	public static double[] coordinates1 = {0,1,1,2,1,0,2,1,2,2};


	public static void main(String[] args) {
		int buttonChoice;

		final TextLCD t = LocalEV3.get().getTextLCD();
		Odometer odometer = new Odometer(leftMotor, rightMotor);
		OdometryDisplay odometryDisplay = new OdometryDisplay(odometer, t);
		//OdometryCorrection odometryCorrection = new OdometryCorrection(odometer);
		
		Navigation navigation = new Navigation(leftMotor,rightMotor, odometer);

		do {
			// clear the display
			t.clear();

			// ask the user whether the motors should drive in a square or float
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

			// ask the user whether the motors should drive in a square or float
			t.drawString("Press ", 0, 0);
			t.drawString(" to ", 0, 1);
			t.drawString("start", 0, 2);
			buttonChoice = Button.waitForAnyPress();

			odometer.start();
			odometryDisplay.start();
			navigation.start(); //start the navigation thread
			
//			for(int i=0; i<5; i++) {
//				int j=0;
//				navigation.travelTo(coordinates[i][j], coordinates[i][j+1]);
//			}

			
		}

		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}
	
	
}
