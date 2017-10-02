package ca.mcgill.ecse211.lab3;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

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
	private double [][] coordinates = {{0,2}, {1,1}, {2,2}, {2,1}, {1,0}};


	private static final int FORWARD_SPEED = 200;
	private static final int ROTATE_SPEED = 130;
	public static final double WHEEL_RADIUS = 2.1;
	public static final double TRACK = 14.33; 

	private EV3LargeRegulatedMotor leftMotor;
	private EV3LargeRegulatedMotor rightMotor;


	//constructor 
	public Navigation(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, 
					Odometer odometer1) {
		this.odometer = odometer1;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.curX = odometer.getX();
		this.curY = odometer.getY();
		this.curTheta = odometer.getTheta();
		
		// reset the motors
	    for (EV3LargeRegulatedMotor motor : new EV3LargeRegulatedMotor[] {leftMotor, rightMotor}) {
	      motor.stop();
	      motor.setAcceleration(3000);
	    }
	}
	
//	public void run() {
//		while (true) {
//			this.curX = this.odometer.getX();
//			this.curY = this.odometer.getY();
//			this.curTheta = this.odometer.getTheta();
//			travelTo(1,1);
//			//travelTo(coordinates[i][j] * 30.48, coordinates [i][j+1] * 30.48);
//			//i++;
//		}
//	}

	public void travelTo(double destX, double destY) {
		//x > 0, y > 0
		if((destX - curX) > 0 && (destY - curY) > 0) {
			destTheta = Math.atan2((destY - curY), (destX - curX));
			destTheta = (destTheta*180)/Math.PI; 	
		}
		//x > 0, y < 0
		else if((destX - curX) > 0 && (destY - curY) < 0) {
			destTheta = Math.atan2((destY - curY), (destX - curX)) + 2*Math.PI; //convert angle between 0 to 360
			destTheta = (destTheta*180)/Math.PI; 
		}
		//x < 0, y > 0
		else if((destX - curX)< 0 && (destY - curY) >= 0) {
			destTheta = Math.atan2((destY - curY), (destX - curX)); 
			destTheta = (destTheta*180)/Math.PI; 
		}
		
		//x < 0, y < 0
		else if((destX - curX) < 0 && (destY - curY) < 0) {
			destTheta = Math.atan2((destY - curY), (destX - curX)) + 2*Math.PI;//convert angle between 0 to 360
			destTheta = (destTheta*180)/Math.PI;
		}
		
		
		thetaDifference = destTheta - curTheta; //compute theta(d) - theta(r)
		
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
		
		
		//check if current theta is within range
		if (!(angleToTurn > -3 && angleToTurn < 3)) {
			turnTo(angleToTurn);
		}
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
		leftMotor.forward();
		rightMotor.forward();
	}

	
	
	private void turnTo(double theta) {

		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);
		
		if(thetaDifference > -180 && thetaDifference < 180 ) {
			leftMotor.rotate(-convertAngle(WHEEL_RADIUS, TRACK, theta * 2), false);
			rightMotor.rotate(convertAngle(WHEEL_RADIUS, TRACK, theta * 2), true);
		}
		//turning cw
		else if((thetaDifference) < -180) {
			leftMotor.rotate(convertAngle(WHEEL_RADIUS, TRACK, theta * 2), true);
			rightMotor.rotate(-convertAngle(WHEEL_RADIUS, TRACK, theta * 2), false);
		}
		//turning ccw
		else if((thetaDifference) > 180) {
			leftMotor.rotate(-convertAngle(WHEEL_RADIUS, TRACK, theta * 2), false);
			rightMotor.rotate(convertAngle(WHEEL_RADIUS, TRACK, theta * 2), true);
		}

		
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
