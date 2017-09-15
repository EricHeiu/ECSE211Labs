package ca.mcgill.ecse211.wallfollowing;

import lejos.hardware.motor.*;

public class BangBangController implements UltrasonicController {

	private final int bandCenter;
	private final int bandwidth;
	private final int motorLow;
	private final int motorHigh;
	private int distance;
	private int initialSpeed = 0;
	
	public BangBangController(int bandCenter, int bandwidth, int motorLow, int motorHigh) {
		// Default Constructor
		this.bandCenter = bandCenter;
		this.bandwidth = bandwidth;
		this.motorLow = motorLow;
		this.motorHigh = motorHigh;
		WallFollowingLab.leftMotor.setSpeed(initialSpeed); // Start robot moving forward
		WallFollowingLab.rightMotor.setSpeed(initialSpeed);
		WallFollowingLab.leftMotor.forward();
		WallFollowingLab.rightMotor.forward();
	}

	@Override
	public void processUSData(int distance) {
		this.distance = distance;
		// TODO: process a movement based on the us distance passed in (BANG-BANG style)
		
		int distError = bandCenter - distance;
			if(Math.abs(distError) <= bandwidth) {
				WallFollowingLab.leftMotor.setSpeed(motorHigh);
				WallFollowingLab.rightMotor.setSpeed(motorHigh);
				WallFollowingLab.leftMotor.forward();
				WallFollowingLab.rightMotor.forward();
			}
			// if too close from the wall
			else if(distError > 0) {
				WallFollowingLab.leftMotor.setSpeed(motorHigh);
				WallFollowingLab.rightMotor.setSpeed(motorLow);
				WallFollowingLab.leftMotor.forward();
				WallFollowingLab.rightMotor.forward();
			}
			/*else if(distError <= 15) {
				WallFollowingLab.leftMotor.setSpeed(-motorHigh);
				WallFollowingLab.rightMotor.setSpeed(-motorHigh);
				WallFollowingLab.leftMotor.forward();
				WallFollowingLab.rightMotor.forward();
			}*/
			else{
				WallFollowingLab.leftMotor.setSpeed(motorLow);
				WallFollowingLab.rightMotor.setSpeed(motorHigh);
				WallFollowingLab.leftMotor.forward();
				WallFollowingLab.rightMotor.forward();
			}
			/*if(distance ==21474) {
				WallFollowingLab.leftMotor.setSpeed(0);
				WallFollowingLab.rightMotor.setSpeed(0);
				WallFollowingLab.leftMotor.forward();
				WallFollowingLab.rightMotor.forward();
			}*/
			
		

	}

	@Override
	public int readUSDistance() {
		return this.distance;
	}
}
