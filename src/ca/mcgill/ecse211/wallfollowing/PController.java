package ca.mcgill.ecse211.wallfollowing;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class PController implements UltrasonicController {


	/* Constants */

	private static final int MOTOR_SPEED = 170; //170
	private static final int FILTER_OUT = 20;
	private static final double PROPCONST = 4; //4
	private static final int MAXCORRECTION = 80; //80

	private final int bandCenter;
	private final int bandWidth;
	private int distance;
	private int filterControl;
	
	//variable for the proportional speed adjustment 
	private int diff;
	

	public PController(int bandCenter, int bandwidth) {
		this.bandCenter = bandCenter;
		this.bandWidth = bandwidth;
		this.filterControl = 0;

		WallFollowingLab.leftMotor.setSpeed(0); // Initialize motor rolling forward
		WallFollowingLab.rightMotor.setSpeed(0);
		WallFollowingLab.leftMotor.forward();
		WallFollowingLab.rightMotor.forward();
	}
	@Override
	public void processUSData(int distance) {

		// rudimentary filter - toss out invalid samples corresponding to null
		// signal.
		// (n.b. this was not included in the Bang-bang controller, but easily
		// could have).
		
		//enters on gaps, turns, and  
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

		// TODO: process a movement based on the us distance passed in (P style)
		int distError = bandCenter - distance;

		//Case 1: If Robot is within proper range, ignoring the gaps
		if (Math.abs(distError) <= bandWidth && filterControl < FILTER_OUT) {
			WallFollowingLab.leftMotor.setSpeed(MOTOR_SPEED);
			WallFollowingLab.rightMotor.setSpeed(MOTOR_SPEED);
			WallFollowingLab.leftMotor.forward();
			WallFollowingLab.rightMotor.forward();
			
		}

		//Case 2: If Robot is too close to the wall
		else if (distError > 0) {
			diff = calcProp(distError);
			WallFollowingLab.leftMotor.setSpeed(MOTOR_SPEED + (int) 2.3 * diff);
			WallFollowingLab.rightMotor.setSpeed(MOTOR_SPEED - 2 * diff);
			WallFollowingLab.leftMotor.forward();
			WallFollowingLab.rightMotor.forward();
		}

		//Case 3: If Robot is too far away from the wall

			int diff = calcProp(distError);

			WallFollowingLab.leftMotor.setSpeed(MOTOR_SPEED - diff);
			WallFollowingLab.rightMotor.setSpeed(MOTOR_SPEED + (int) 0.65 * diff);
			WallFollowingLab.leftMotor.forward();
			WallFollowingLab.rightMotor.forward();
		}

	//Method to calculate the proportional speed adjustment

	public int calcProp(int dist) {
		int correction = 0;

		//Speed adjustment is proportional to the magnitude of error

		//Case 1: too far away from the wall

		if (dist < 0) {
			dist = -dist;
			correction = (int)(PROPCONST * (double)dist);
		}
		

		//Case 2: too close to the wall

		if (dist > 0) {
			correction = (int)(PROPCONST * 2 * (double)dist);
		}
		
		if (correction >= MOTOR_SPEED) {
			correction = MAXCORRECTION;

		}
		return correction;
	}

	@Override
	public int readUSDistance() {
		return this.distance;
	}

}
