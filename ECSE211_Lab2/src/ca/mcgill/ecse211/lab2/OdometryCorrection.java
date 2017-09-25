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
	private static final double SQUARE_LENGTH = 30.48;
	private Odometer odometer;
	
	//Initialize the lightSensor
	private static Port lightPort = LocalEV3.get().getPort("S1");
	@SuppressWarnings("resource") // Because we don't bother to close this resource
	SensorModes lightSensor = new EV3ColorSensor(lightPort);
	SampleProvider lightSample = ((EV3ColorSensor) lightSensor).getRedMode(); 
	float[] lightData = new float[lightSample.sampleSize()]; 
	
	//added code
	
	private static boolean rolling = true;
	private static int status;
	private static int numSamples=0;
	
	private static int theta;
	private static boolean isReset;
	private static int counter1;
	private static int counter2;
	private static int counter3;
	private static int counter4;
	private static double currentY;
	private static double correctY;
	private static double sensorY;
	private static double sensorX;
	
	private static boolean detectLine;
	

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
			//System.out.println(lightData[0]);
		
			theta = (int)this.odometer.getTheta();
			//&& lightData[0] == 0.15
			if(theta <= 3 && theta >= 0 && lightData[0] > 0.11 && lightData[0] < 0.17) {
				Sound.beep();
				if(counter1 == 0) {
					this.odometer.setX(0);
					this.odometer.setY(-15.24);
					counter1= counter1 + 2;
				}
				else {
					counter1++; 
					sensorY = (counter1-1) * SQUARE_LENGTH;
					this.odometer.setY(sensorY);
				}
			}
			else if(theta <= 93 && theta >= 87 && lightData[0] > 0.11 && lightData[0] < 0.17) {
				Sound.beep();
				if(counter2 == 0) {
					this.odometer.setX(0);
					this.odometer.setY(76.2);
					counter2 = counter2 + 2;
				}
				else {
					counter2++;
					sensorX = (counter2 - 1) * SQUARE_LENGTH;
					this.odometer.setX(sensorX);
				}
			}
			else if(theta >= 177 && theta <= 183 && lightData[0] > 0.11 && lightData[0] < 0.17) {
				Sound.beep();
				if(counter3 == 0) {
					this.odometer.setX(76.2);
					this.odometer.setY(60.96);
					counter3 = counter3 + 2;
				}
				else {
					
					sensorY = (counter3) * SQUARE_LENGTH;
					this.odometer.setY(sensorY);
					counter3--;
				}
			}
			else if(theta >= 267 && theta <= 273 && lightData[0] > 0.11 && lightData[0] < 0.17) {
			  Sound.beep();
				if(counter4 == 0) {
					this.odometer.setX(60.96);
					this.odometer.setY(-15.24);
					counter4 = counter4 + 2;
				}
				else {
					sensorX = (counter4) * SQUARE_LENGTH;
					this.odometer.setX(sensorX);
					counter4--;
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
