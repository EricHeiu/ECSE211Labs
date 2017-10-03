package ca.mcgill.ecse211.lab3;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.Sound;

public class Navigation extends Thread {

	private Odometer odometer;
	private double curX = 0;
	private double curY = 0.0;
	private double curTheta = 0.0;
	private double destTheta = 0.0;
	private double thetaDifference = 0; //thetaDifference
	private double angleToTurn = 0;
	private int i = 0; //index for coordinates array
	private int j = 0;
	//private double [][] coordinates = {{1,1}, {0,2}, {2,2}, {2,1}, {1,0}};

	private double distanceToTravel;


	private static final int FORWARD_SPEED = 200;
	private static final int ROTATE_SPEED = 130;
	public static final double WHEEL_RADIUS = 2.1;
	public static final double TRACK = 14.33; 

	private EV3LargeRegulatedMotor leftMotor;
	private EV3LargeRegulatedMotor rightMotor;
	private boolean navigating;


	//constructor 
	public Navigation(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, 
			Odometer odometer1) {
		this.odometer = odometer1;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.curX = odometer.getX();
		this.curY = odometer.getY();
		this.curTheta = odometer.getTheta();
		navigating = false;

		//reset the motors
		for (EV3LargeRegulatedMotor motor : new EV3LargeRegulatedMotor[] {leftMotor, rightMotor}) {
			motor.stop();
			motor.setAcceleration(300);
		}
	}

	public void run() {
		//wait 5 seconds
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// there is nothing to be done here because it is not expected that
			// the odometer will be interrupted by another thread
		}
		int i = 0;
		while (i < NavigationLab.coordinates1.length) {
			this.curX = this.odometer.getX();
			this.curY = this.odometer.getY();
			this.curTheta = this.odometer.getTheta();
			travelTo(NavigationLab.coordinates1[i], NavigationLab.coordinates1[i+1]);
			i = i + 2;
		}
		//while (true) {}
	}

	public void travelTo(double destX, double destY) {

		//		this.curX = odometer.getX();
		//		this.curY = odometer.getY();
		//		this.curTheta = odometer.getTheta();

		double deltaX = (destX*30.48) - curX;
		double deltaY = (destY*30.48) - curY;

		double destAngle = Math.atan2(deltaX, deltaY) * 180 /Math.PI; //angle is between -180 to 180 degrees

		//convert destAngle to be between 0 to 360 degrees 
		if (destAngle < 0) {
			destAngle = destAngle + 360;
		}

		thetaDifference = destAngle - curTheta; //compute theta(d) - theta(r)

		if (!(thetaDifference <= 5 && thetaDifference >= -5)) {
			turnTo(destAngle);
			Sound.beep();
		}

		distanceToTravel = Math.sqrt(Math.pow(deltaY, 2) + Math.pow(deltaX,2));
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
		navigating = true;
		leftMotor.rotate(convertDistance(WHEEL_RADIUS, distanceToTravel), true);
		rightMotor.rotate(convertDistance(WHEEL_RADIUS, distanceToTravel), false);
		Sound.buzz();
		navigating = false;
		//this.curTheta = odometer.getTheta();

		//if ((curTheta <= destAngle + 2) && (curTheta >= destAngle - 2)) {


		//}





	}



	private void turnTo(double theta) {

		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);

		if((thetaDifference) > -180 && (thetaDifference) < 180) {
			angleToTurn = thetaDifference;
		}
		//turning cw
		else if((thetaDifference) < -180) {
			angleToTurn = (thetaDifference) + 360;
		}
		//turning ccw
		else if((thetaDifference) > 180) {
			angleToTurn = (thetaDifference) - 360;
		}
		leftMotor.rotate(convertAngle(WHEEL_RADIUS, TRACK, angleToTurn), true);
		rightMotor.rotate(-convertAngle(WHEEL_RADIUS, TRACK, angleToTurn), false);

	}


	private boolean isNavigating() {

		return navigating;
	}

	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
}
