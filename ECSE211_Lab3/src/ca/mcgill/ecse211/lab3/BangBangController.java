package ca.mcgill.ecse211.lab3;

import lejos.hardware.motor.*;

public class BangBangController implements UltrasonicController {

	private final int bandCenter;
	private final int bandwidth;
	private final int motorLow;
	private final int motorHigh;
	private int distance;
	private static final int FILTER_OUT = 20;
	private int filterControl;

	public BangBangController(int bandCenter, int bandwidth, int motorLow, int motorHigh) {
		// Default Constructor
		this.bandCenter = bandCenter;
		this.bandwidth = bandwidth;
		this.motorLow = motorLow;
		this.motorHigh = motorHigh;
		//Initialize motors, but robot will not move until sensor is turned on
		WallFollowingLab.leftMotor.setSpeed(0); 
		WallFollowingLab.rightMotor.setSpeed(0);
		WallFollowingLab.leftMotor.forward();
		WallFollowingLab.rightMotor.forward();
	}

	@Override
	public void processUSData(int distance) {
		this.distance = distance;
		
		//The following 3 if/elseif statements act as signal filters
		
		if (distance >= 180 && filterControl < FILTER_OUT) {
			// bad value, do not set the distance var, however do increment the
			// filter value
			filterControl++;
		} 
		
		else if (distance >= 180) { 
			// We have repeated large values, so there must actually be nothing
			// there: leave the distance alone
			this.distance = distance;
		
		} 
		
		else if (distance < 180){
			// distance went below 180: reset filter and leave
			// distance alone.
			filterControl = 0;
			this.distance = distance;
		}
		
		
		// TODO: process a movement based on the us distance passed in (BANG-BANG style)

		int distError = bandCenter - distance;

		//Case 1: robot is moving within desired range, will continue to move with stable
		//speed while ignoring gaps (if there are any)
		if(Math.abs(distError) <= bandwidth && filterControl < FILTER_OUT) {
			WallFollowingLab.leftMotor.setSpeed(motorHigh);
			WallFollowingLab.rightMotor.setSpeed(motorHigh);
			WallFollowingLab.leftMotor.forward();
			WallFollowingLab.rightMotor.forward();
		}

		//Case 2: robot is too close to the wall, its left wheel will turn at a 
		//speed of 330 deg/sec and the right wheel will reverse at a speed of 
		//100 deg/sec
		else if(distError > 0) {
			WallFollowingLab.leftMotor.setSpeed(330);
			WallFollowingLab.rightMotor.setSpeed(100);
			WallFollowingLab.leftMotor.forward();
			WallFollowingLab.rightMotor.backward();
		}

		//Case 3: robot is too far away from the wall, its right wheel will turn 
		//faster and the left wheel will turn slower
		
		else {
			WallFollowingLab.leftMotor.setSpeed(motorLow); 
			WallFollowingLab.rightMotor.setSpeed(motorHigh); 
			WallFollowingLab.leftMotor.forward();
			WallFollowingLab.rightMotor.forward();
		}
	}

	@Override
	public int readUSDistance() {
		return this.distance;
	}
}
