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
	EV3ColorSensor lightSensor = new EV3ColorSensor(lightPort); //SensorModes
	SampleProvider lightSample = lightSensor.getRedMode();
	float[] lightData = new float[lightSample.sampleSize()]; 
	
	//added code
	
//	private double startingX = this.odometer.getX();
//	private double startingY = this.odometer.getY();
	private int counter1 = 0;
	private int counter2 = 0;
	private int counter3 = 0;
	private int counter4 = 0;
	


	// constructor
	public OdometryCorrection(Odometer odometer) {
		this.odometer = odometer;
	}

	// run method (required for Thread)
	public void run() {
		long correctionStart, correctionEnd;

		while (true) {
			correctionStart = System.currentTimeMillis();
			
			//TODO Place correction implementation here
			lightSample.fetchSample(lightData, 0);
			float curData = lightData[0]; //current light data
			
			//Case 1: robot moves along left side of rectangle
			//Y-pos increases only
			if (0 < this.odometer.getTheta() && this.odometer.getTheta() < 3 
					&& curData > 0.11 && curData < 0.17) {
				Sound.beep();
				if (counter1 == 0) {
					//this.odometer.setX(-15.24);
					this.odometer.setY(0);
					counter1++;
				}
				else {
					//this.odometer.setX(-15.24);
					this.odometer.setY(counter1 * 30.48);
					counter1++;
				}
			}
			
			//Case 2: robot moves along top side of rectangle
			//X-pos increases only
			if (87 < this.odometer.getTheta() && this.odometer.getTheta() < 93
					&& curData > 0.11 && curData < 0.17) {
				Sound.beep();
				if (counter2 == 0) {
					this.odometer.setX(0);
					counter2++;
				}
				else {
					this.odometer.setX(counter2 * 30.48);
					counter2++;
				}
				
			}
			
			//Case 3: robot moves along right side of rectangle
			//Y-pos decreases only
			if (177 < this.odometer.getTheta() && this.odometer.getTheta() < 183 
					&& curData > 0.11 && curData < 0.17) {
				Sound.beep();
				if (counter3 == 0) {
					this.odometer.setY((counter2 - 1) * 30.48);
					counter3++;
				}
				else {
					this.odometer.setY(((counter2 - 1) * 30.48) - (counter3 * 30.48));
					counter3++;
				}
			}
			
			//Case 4: robot moves along bottom side of rectangle 
			//X-pos decreases only 
			if (267 < this.odometer.getTheta() && this.odometer.getTheta() < 273 
					&& curData > 0.11 && curData < 0.17) {
				Sound.beep();
				if (counter4 == 0) {
					this.odometer.setX((counter3 - 1) * 30.48);
					counter4++;
				}
				else {
					this.odometer.setX((counter3 - 1) * 30.48 - (counter4 * 30.48));
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
