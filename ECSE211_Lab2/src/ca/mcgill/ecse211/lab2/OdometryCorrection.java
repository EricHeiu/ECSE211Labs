/*
 * OdometryCorrection.java
 */
package ca.mcgill.ecse211.lab2;

import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;
import lejos.utility.Timer;


public class OdometryCorrection extends Thread {
	private static final long CORRECTION_PERIOD = 10;
	private Odometer odometer;

	//Initialize the lightSensor
	private static Port lightPort = LocalEV3.get().getPort("S1");
	@SuppressWarnings("resource") // Because we don't bother to close this resource
	EV3ColorSensor lightSensor = new EV3ColorSensor(lightPort); 
	SampleProvider lightSample = lightSensor.getRedMode();
	float[] lightData = new float[lightSample.sampleSize()]; 

	/*These counters count how many times the robot crosses the black line
	 * when traveling along each side of the arbitrary rectangle.
	 * counter1 is active when robot is traveling along the left side of the rectangle
	 * counter2 is active when robot is traveling along the top side of the rectangle
	 * counter3 is active ... the right side of the rectangle
	 * counter4 is active ... the bottom side of the rectangle
	 */
	private int counter1 = 0;
	private int counter2 = 0;
	private int counter3 = 0;
	private int counter4 = 0;

	private double correctionRange1 = 0.25; //lower bound for lightSensor reading when black line is detected
	private double correctionRange2 = 0.35; //higher bound for lightSensor reading when black line is detected
	private double tileWidth = 30.48; //width of each tile is 30.48cm


	// constructor
	public OdometryCorrection(Odometer odometer) {
		this.odometer = odometer;
	}

	// run method (required for Thread)
	public void run() {
		long correctionStart, correctionEnd;
		Sound.setVolume(80);
		while (true) {
			correctionStart = System.currentTimeMillis();

			//TODO Place correction implementation here
			lightSample.fetchSample(lightData, 0); //obtains sample data and store it in lightData array
			float curData = lightData[0]; //current light data


			//Case 1: robot moves along left side of rectangle, its orientation is between 0 to 3 degrees
			//Robot only crosses horizontal black lines, so only Y-pos is corrected (Y increases)
			if ((358 < this.odometer.getTheta() || this.odometer.getTheta() < 3) 
					&& curData > correctionRange1 && curData < correctionRange2) { 
				Sound.beep();
				/* if counter1 = 0, it means the robot is reaching the 1st black line on
				 * the left side of the rectangle, therefore correct y-pos by setting it to 0
				 */
				if (counter1 == 0) {
					this.odometer.setY(0);
					counter1++;
				}
				/* if counter1 != 0, it means the robot is crossing black lines other than the 1st one,
				 * therefore correct y-pos based on the value of counter1
				 */
				else {
					this.odometer.setY(counter1 * tileWidth);
					counter1++;
				}
			}

			//Case 2: robot moves along top side of rectangle, its orientation is between 87 to 93 degrees
			//robot only crosses vertical black lines, so only X-pos is corrected (X increases)
			if (87 < this.odometer.getTheta() && this.odometer.getTheta() < 93
					&& curData > correctionRange1 && curData < correctionRange2) {
				Sound.beep();
				/* if counter2 = 0, robot is crossing the 1st black line on
				 * the top side of the rectangle, therefore correct x-pos by setting it to 0
				 */
				if (counter2 == 0) {
					this.odometer.setX(0);
					counter2++;
				}
				/* if counter2 != 0, it means the robot is crossing black lines other than the 1st one,
				 * therefore correct x-pos based on the value of counter2
				 */
				else {
					this.odometer.setX(counter2 * tileWidth);
					counter2++;
				}

			}

			//Case 3: robot moves along right side of rectangle, its orientation is between 177 to 183 degrees
			//robot only crosses horizontal black lines, so only Y-pos is corrected (Y decreases)
			if (177 < this.odometer.getTheta() && this.odometer.getTheta() < 183 
					&& curData > correctionRange1 && curData < correctionRange2) {
				Sound.beep();
				/* if counter3 = 0, robot is crossing 1st black line on the right side of 
				 * the rectangle, therefore correct y-pos based on the value of counter2
				 */
				if (counter3 == 0) {
					this.odometer.setY((counter2 - 1) * tileWidth);
					counter3++;
				}
				/* if counter3 != 0, robot is crossing black lines other than the 1st one, 
				 * therefore correct y-pos by subtracting the distance robot has supposedly traveled 
				 * from its original displacement on the top point of the right side
				 */
				else {
					this.odometer.setY(((counter2 - 1) * tileWidth) - (counter3 * tileWidth));
					counter3++;
				}
			}

			//Case 4: robot moves along bottom side of rectangle, its orientation is between 267 and 273 degrees
			//robot only crosses vertical black lines, so only X-pos is corrected (X decreases)
			if (267 < this.odometer.getTheta() && this.odometer.getTheta() < 273 
					&& curData > correctionRange1 && curData < correctionRange2) {
				Sound.beep();
				/* if counter4 = 0, robot is crossing 1st black line on the bottom side of 
				 * the rectangle, therefore correct x-pos based on the value of counter3
				 */
				if (counter4 == 0) {
					this.odometer.setX((counter3 - 1) * tileWidth);
					counter4++;
				}
				/* if counter4 != 0, robot is crossing black lines other than the 1st one, 
				 * therefore correct x-pos by subtracting the distance robot has supposedly traveled 
				 * from its original displacement on the bottom point of the right side
				 */
				else {
					this.odometer.setX((counter3 - 1) * tileWidth - (counter4 * tileWidth));
					counter4++;
				}
			}


			// this ensure the odometry correction occurs only once every period
			correctionEnd = System.currentTimeMillis();
			if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
				try {
					Thread.sleep(CORRECTION_PERIOD - (correctionEnd - correctionStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometry correction will be
					// interrupted by another thread
				}
			}
		}
	}
}
