/*
 * OdometryCorrection.java
 */
package ca.mcgill.ecse211.lab2;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;

public class OdometryCorrection extends Thread {
  private static final long CORRECTION_PERIOD = 10;
  private Odometer odometer;
  private static final Port usPort = LocalEV3.get().getPort("S1");

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
      SensorModes usSensor = new EV3LightSensor(usPort); // usSensor is the instance
      SampleProvider usDistance = usSensor.getMode("Distance"); // usDistance provides samples from
                                                                // this instance
      float[] usData = new float[usDistance.sampleSize()]; // usData is the buffer in which data are
                                                           // returned
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
