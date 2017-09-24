package ca.mcgill.ecse211.lab1;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class PController implements UltrasonicController {


	/* Constants */

	private static final int MOTOR_SPEED = 170; 
	private static final int FILTER_OUT = 18;
	private static final double PROPCONST = 5; 
	private static final int MAXCORRECTION = 80; 

	private static final double NEAR_RIGHT_MOTOR_SPEED_SCALE = 4; //Scales the proportional speed adjustment (PSA) of right motor when close to wall by 4
	private static final double NEAR_LEFT_MOTOR_SPEED_SCALE = 4.6; //Scales the PSA of left motor when close to wall by 4.6
	private static final double FAR_RIGHT_MOTOR_SPEED_SCALE = 0.65; //Scales the PSA of right motor when far from wall by 0.65
	private final int bandCenter;
	private final int bandWidth;
	private int distance;
	private int filterControl;
	private int diff; //proportional speed adjustment


	public PController(int bandCenter, int bandwidth) {
		this.bandCenter = bandCenter;
		this.bandWidth = bandwidth;
		this.filterControl = 0;

		// Initialize motors, but robot will not move until sensor is turned on
		WallFollowingLab.leftMotor.setSpeed(0); 
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

		if (distance >= 180 && filterControl < FILTER_OUT) {
			// bad value, do not set the distance var, however do increment the
			// filter value
			filterControl++;
		} else if (distance >= 180) {
			// We have repeated large values, so there must actually be nothing
			// there: leave the distance alone
			this.distance = distance;
		}
		else {
			// distance went below 180: reset filter and leave
			// distance alone.
			filterControl = 0;
			this.distance = distance;
		}

		// TODO: process a movement based on the us distance passed in (P style)
		int distError = bandCenter - distance;

		//Case 1: If Robot is within proper range, continue to move forward
		//with stable speed while ignoring the gaps (if there are any)
		if (Math.abs(distError) <= bandWidth && filterControl < FILTER_OUT) {
			WallFollowingLab.leftMotor.setSpeed(MOTOR_SPEED);
			WallFollowingLab.rightMotor.setSpeed(MOTOR_SPEED);
			WallFollowingLab.leftMotor.forward();
			WallFollowingLab.rightMotor.forward();

		}

		//Case 2: If Robot is too close to the wall, move away from the wall with 
		//proportional speeds

		else if (distError > 0) {
			diff = calcProp(distError);
			WallFollowingLab.leftMotor.setSpeed(MOTOR_SPEED + (int) NEAR_LEFT_MOTOR_SPEED_SCALE * diff);
			WallFollowingLab.rightMotor.setSpeed(MOTOR_SPEED - (int) NEAR_RIGHT_MOTOR_SPEED_SCALE * diff);
			WallFollowingLab.leftMotor.forward();
			WallFollowingLab.rightMotor.backward();
		}

		//Case 3: If Robot is too far away from the wall, move closer to the wall with
		//proportional speeds
		else if(distError < 0) {
			diff = calcProp(distError);
			WallFollowingLab.leftMotor.setSpeed(MOTOR_SPEED - diff); 
			WallFollowingLab.rightMotor.setSpeed(MOTOR_SPEED + (int) FAR_RIGHT_MOTOR_SPEED_SCALE * diff);
			WallFollowingLab.leftMotor.forward();
			WallFollowingLab.rightMotor.forward();
		}
	}

	//Method to calculate the proportional speed adjustment

	public int calcProp(int dist) {
		int correction = 0;

		//Speed adjustment is proportional to the magnitude of distance between 
		//robot and wall

		//Case 1: too far away from the wall

		if (dist < 0) {
			dist = -dist;
			correction = (int)(PROPCONST * (double)dist);
		}


		//Case 2: too close to the wall

		if (dist > 0) {
			correction = (int)(PROPCONST * (double)dist); 
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
