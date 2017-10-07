package ca.mcgill.ecse211.lab4;

import lejos.robotics.SampleProvider;

/**
 * Control of the wall follower is applied periodically by the UltrasonicPoller thread. The while
 * loop at the bottom executes in a loop. Assuming that the us.fetchSample, and cont.processUSData
 * methods operate in about 20mS, and that the thread sleeps for 50 mS at the end of each loop, then
 * one cycle through the loop is approximately 70 mS. This corresponds to a sampling rate of 1/70mS
 * or about 14 Hz.
 */
public class UltrasonicPoller extends Thread {
  private SampleProvider us;
  // private UltrasonicController cont;
  private float[] usData;
  int distance;
  private static final int FILTER_OUT = 20;
  private int filterControl;

  public UltrasonicPoller(SampleProvider us, float[] usData) {
    this.us = us;
    //this.cont = cont;
    this.usData = usData;
  }

  /*
   * Sensors now return floats using a uniform protocol. Need to convert US result to an integer
   * [0,255] (non-Javadoc)
   * 
   * @see java.lang.Thread#run()
   */
  public void run() {
    while (true) {
      us.fetchSample(usData, 0); // acquire data
      distance = (int) (usData[0] * 100.0); // extract from buffer, cast to int

      //cont.processUSData(distance); // now take action depending on value

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
      try {
        Thread.sleep(50); 
      } catch (Exception e) {
      } // Poor man's timed sampling
    }
  }
  public int readUSDistance() {
    return this.distance;
  }

}
