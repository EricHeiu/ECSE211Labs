package ca.mcgill.ecse211.lab3;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Navigation {
	
	private double curX = 0.0;
	private double curY = 0.0;
	private double angToTurn;
	
	private static final int FORWARD_SPEED = 200;
	private static final int ROTATE_SPEED = 100;
	

	private static final EV3LargeRegulatedMotor leftMotor = 
			new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	
	private static final EV3LargeRegulatedMotor rightMotor = 
			new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	/* private int getX(int index) {
		
	}
	
	private int getY(int index) {
		
	}*/
	public Navigation(double x, double y) {
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
		leftMotor.rotate((int)theta, true);
	    rightMotor.rotate((int) theta, false);
	    
	}
	
	
	private boolean isNavigating() {
		
		return false;
	}
}
