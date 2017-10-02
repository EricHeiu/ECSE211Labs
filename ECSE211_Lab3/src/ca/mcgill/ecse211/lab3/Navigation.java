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
	private boolean isAtNextPoint = false;
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

	public void run() {
		while (true) {
			this.curX = this.odometer.getX();
			this.curY = this.odometer.getY();
			this.curTheta = this.odometer.getTheta();
			//			travelTo(coordinates[i][j] * 30.48, coordinates [i][j+1] * 30.48);
			//			i++;
			//travelTo(0,2);
		}
	}

	public void travelTo(double destX, double destY) {
		destX = destX * 30.48;
		destY = destY * 30.48;
		//x > 0, y > 0
		if((destX - curX) >= 0 && (destY - curY) >= 0) {
			destTheta = Math.atan2((destX - curX), (destY - curY));
			destTheta = (destTheta*180)/Math.PI; 	
		}
		//x > 0, y < 0
		else if((destX - curX) >= 0 && (destY - curY) <= 0) {
			destTheta = Math.atan2((destX - curX), (destY - curY)); //convert angle between 0 to 360
			destTheta = (destTheta*180)/Math.PI; 
		}
		//x < 0, y > 0
		else if((destX - curX) <= 0 && (destY - curY) >= 0) {
			destTheta = Math.atan2((destX - curX), (destY - curY)) + 2*Math.PI; 
			destTheta = (destTheta*180)/Math.PI; 
		}

		//x < 0, y < 0
		else if((destX - curX) <= 0 && (destY - curY) <= 0) {
			destTheta = Math.atan2((destX - curX), (destY - curY)) + 2*Math.PI;//convert angle between 0 to 360
			destTheta = (destTheta*180)/Math.PI;
		}
		
		thetaDifference = destTheta - curTheta; //compute theta(d) - theta(r)

		if((thetaDifference) > -180 && (thetaDifference) < 180) {
			angleToTurn = thetaDifference;

		}

		else if((thetaDifference) < -180) {
			angleToTurn = (thetaDifference) + 360;
		}

		else if((thetaDifference) > 180) {
			angleToTurn = (thetaDifference) - 360;
		}


		//check if current theta is within range
		if (!(angleToTurn > -3 && angleToTurn < 3)) {
			turnTo(angleToTurn);
		}

		//		while (isAtNextPoint == false) {
		//			if ((odometer.getX()/30.48 <= 0.2+destX && odometer.getX()/30.48 >= destX - 0.2 ) 
		//					&& (odometer.getY()/30.48 <= 0.2+destY && odometer.getY()/30.48 >= destY-0.2)) {
		//				leftMotor.setSpeed(FORWARD_SPEED);
		//				rightMotor.setSpeed(FORWARD_SPEED);
		//				leftMotor.forward();
		//				rightMotor.forward();
		//			}
		//			else {
		//				leftMotor.stop();
		//				rightMotor.stop();
		//				isAtNextPoint = true;
		//			}
		//		}
		
		while (!((curX <= 2+destX && curX >= destX - 2 ) 
				&& (curY <= 2+destY && curY >= destY-2))) {
			leftMotor.setSpeed(FORWARD_SPEED);
			rightMotor.setSpeed(FORWARD_SPEED);
			leftMotor.forward();
			rightMotor.forward();
		}
		leftMotor.stop();
		rightMotor.stop();

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
