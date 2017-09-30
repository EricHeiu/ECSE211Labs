package ca.mcgill.ecse211.lab3;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Navigation {
	
	private double curX = 0.0;
	private double curY = 0.0;
	private double destTheta = 0;
	private double curTheta = 0;
	
	private static final int FORWARD_SPEED = 200;
	private static final int ROTATE_SPEED = 100;
	public static final double WHEEL_RADIUS = 2.1;
	public static final double TRACK = 14.33; 
	
	private EV3LargeRegulatedMotor leftMotor;
	private EV3LargeRegulatedMotor rightMotor;
	/* private int getX(int index) {
		
	}
	
	private int getY(int index) {
		
	}*/
	public Navigation(double x, double y, EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor) {
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.curX = x;
		this.curY = y;
		
	}
	
	public void travelTo(double destX, double destY) {
		System.out.println(destX);
		System.out.println(destY);
		//x > 0
		if((destX - curX) > 0) {
			destTheta = Math.atan((destY - curY) / (destX - curX));
			destTheta = (destTheta*180)/Math.PI; //convert to rad from deg
			System.out.println(destTheta);
			turnTo(destTheta);
		}
		//x < 0, y > 0
		else if((destX - curX)< 0 && (destY - curY) > 0) {
			destTheta = Math.atan((destY - curY) / (destX - curX)) + Math.PI;
			turnTo(destTheta);
		}
		//x < 0, y < 0
		else if((destX - curX) < 0 && (destY - curY) < 0) {
			destTheta = Math.atan((destY - curY) / (destX - curX)) - Math.PI;
			turnTo(destTheta);
		}
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
		//leftMotor.forward();
		//rightMotor.forward();
	}
	
	private void turnTo(double theta) {
		
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);
		leftMotor.rotate(convertAngle(WHEEL_RADIUS, TRACK, theta), true);
	    rightMotor.rotate(-convertAngle(WHEEL_RADIUS, TRACK, theta), false);
	}
	
	
	private boolean isNavigating() {
		
		return false;
	}
	
	private static int convertDistance(double radius, double distance) {
	    return (int) ((180.0 * distance) / (Math.PI * radius));
	  }

	  private static int convertAngle(double radius, double width, double angle) {
	    return convertDistance(radius, Math.PI * width * angle / 360.0);
	  }
}
