package ca.mcgill.ecse211.lab3;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Navigation {
	
	private double curX = 0.0;
	private double curY = 0.0;
	private double angToTurn;
	
	private static final int FORWARD_SPEED = 200;
	private static final int ROTATE_SPEED = 100;
	
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
		if(destX > 0) {
			angToTurn = Math.atan((destY - curY) / (destX - curX));
			turnTo(angToTurn);
		}
		else if(destX < 0 && destY > 0) {
			angToTurn = Math.atan((destY - curY) / (destX - curX)) + Math.PI;
			turnTo(angToTurn);
		}
		
		else if(destX < 0 && destY < 0) {
			angToTurn = Math.atan((destY - curY) / (destX - curX)) - Math.PI;
			turnTo(angToTurn);
		}
		
	}
	
	private void turnTo(double theta) {

		leftMotor.setSpeed(100);
		rightMotor.setSpeed(100);
		leftMotor.rotate((int)theta, true);
	    rightMotor.rotate((int) theta, false);
	    
	}
	
	
	private boolean isNavigating() {
		
		return false;
	}
}
