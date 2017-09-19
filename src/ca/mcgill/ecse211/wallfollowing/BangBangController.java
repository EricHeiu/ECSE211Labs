package ca.mcgill.ecse211.wallfollowing;

import lejos.hardware.motor.*;

public class BangBangController implements UltrasonicController {

	private final int bandCenter;
	private final int bandwidth;
	private final int motorLow;
	private final int motorHigh;
	private int distance;
	private int initialSpeed = 0;
	private static final int FILTER_OUT = 15;
	private int filterControl;

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
		
		//enters on gaps, turns, and random error
		if (distance >= 255 && filterControl < FILTER_OUT) {
			// bad value, do not set the distance var, however do increment the
			// filter value
			filterControl++;
		} else if (distance >= 255) {
			// We have repeated large values, so there must actually be nothing
			// there: leave the distance alone
			this.distance = distance;
		
		} else {
			// distance went below 255: reset filter and leave
			// distance alone.
			filterControl = 0;
			this.distance = distance;
		}
		// TODO: process a movement based on the us distance passed in (BANG-BANG style)

		int distError = bandCenter - distance;

		//if moving within desired range, while ignoring all the gaps
		if(Math.abs(distError) <= bandwidth && filterControl < FILTER_OUT) {
			WallFollowingLab.leftMotor.setSpeed(motorHigh);
			WallFollowingLab.rightMotor.setSpeed(motorHigh);
			WallFollowingLab.leftMotor.forward();
			WallFollowingLab.rightMotor.forward();
		}

		//Case 2: when the robot is too close to the wall, the left wheel will turn faster
		//and the right wheel will reverse slowly to provide more free space 
		else if(distError > 0) {
			WallFollowingLab.leftMotor.setSpeed(330);
			WallFollowingLab.rightMotor.setSpeed(65);
			WallFollowingLab.leftMotor.forward();
			WallFollowingLab.rightMotor.backward();
		}

		//if too far away from the wall
		else {
			WallFollowingLab.leftMotor.setSpeed(160);
			WallFollowingLab.rightMotor.setSpeed(225);
			WallFollowingLab.leftMotor.forward();
			WallFollowingLab.rightMotor.forward();
		}
	}

	@Override
	public int readUSDistance() {
		return this.distance;
	}
}
