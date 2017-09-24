/*
 * OdometryCorrection.java
 */
package ca.mcgill.ecse211.lab2;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;
import lejos.utility.Timer;


public class OdometryCorrection extends Thread {
	private static final long CORRECTION_PERIOD = 10;
	private Odometer odometer;
	
	//added code
	private static Port lightPort = LocalEV3.get().getPort("S1");
	private static boolean rolling = true;
	private static int status;
	private static int numSamples=0;
	

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
			@SuppressWarnings("resource") // Because we don't bother to close this resource
			SensorModes lightSensor = new EV3ColorSensor(lightPort);
			SampleProvider lightSample = lightSensor.getMode(1); 
			float[] lightData = new float[lightSample.sampleSize()]; 
			
			//Timer myTimer = new Timer(SINTERVAL, new DataAcquisition());
			//lightSensor.fetchSample(lightData, 0);
			//System.out.println(lightData[0]);
			
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
