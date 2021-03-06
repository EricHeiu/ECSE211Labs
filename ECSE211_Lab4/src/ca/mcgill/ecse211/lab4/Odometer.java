package ca.mcgill.ecse211.lab4;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Odometer extends Thread {
  // robot position
  private double x; // current x posisition
  private double y; // current y position
  private double theta; // current orientation
  private int leftMotorTachoCount;
  private int rightMotorTachoCount;
  private int lastTachoL; // previous tacho count of left motor
  private int lastTachoR; // previous tacho count of right motor
  private int nowTachoL; // current tacho count of left motor
  private int nowTachoR; // current tacho count of right motor
  private EV3LargeRegulatedMotor leftMotor;
  private EV3LargeRegulatedMotor rightMotor;
  private double distLWheel, distRWheel, deltaD, deltaT, dX, dY;

  private static final long ODOMETER_PERIOD = 25; /* odometer update period, in ms */
  public static final double WHEEL_RADIUS = 2.1;
  public static final double TRACK = 15.3;

  private Object lock; /* lock object for mutual exclusion */

  // default constructor
  public Odometer(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor) {
    this.leftMotor = leftMotor;
    this.rightMotor = rightMotor;
    this.x = 0.0;
    this.y = 0.0;
    this.theta = 0.0;
    this.lastTachoL = 0;
    this.lastTachoR = 0;
    this.nowTachoL = 0;
    this.nowTachoR = 0;
    lock = new Object();
  }

  // run method (required for Thread)
  public void run() {
    long updateStart, updateEnd;

    while (true) {
      updateStart = System.currentTimeMillis();
      // TODO put (some of) your odometer code here

      nowTachoL = leftMotor.getTachoCount(); // get current tacho counts for both motors
      nowTachoR = rightMotor.getTachoCount();
      distLWheel = Math.PI * WHEEL_RADIUS * (nowTachoL - lastTachoL) / 180; // left wheel
                                                                            // displacement
      distRWheel = Math.PI * WHEEL_RADIUS * (nowTachoR - lastTachoR) / 180; // right wheel
                                                                            // displacement
      lastTachoL = nowTachoL; // save current tacho counts for next iteration
      lastTachoR = nowTachoR;
      deltaD = 0.5 * (distLWheel + distRWheel); // calculate vehicle displacement

      deltaT = (distLWheel - distRWheel) / TRACK; // compute change in robot's orientation



      synchronized (lock) {
        /**
         * Don't use the variables x, y, or theta anywhere but here! Only update the values of x, y,
         * and theta in this block. Do not perform complex math
         * 
         */
        // TODO replace example value
        // if current theta exceeds 359.9, reset it back to 0 degrees
        if (((deltaT * 180 / Math.PI) + theta) >= 360) {
          theta = 0;
        }
        // if current theta is below 0, set it to 359.9 degrees
        else if (((deltaT * 180 / Math.PI) + theta) < 0) {
          theta = 359.9;
        } else {
          theta += deltaT * 180 / Math.PI; // update robot's orientation
        }
        dX = deltaD * Math.sin(theta * Math.PI / 180); // compute x-displacement
        dY = deltaD * Math.cos(theta * Math.PI / 180); // compute y-displacement

        x = x + dX; // update current x-displacement
        y = y + dY; // update current y-displacement
      }

      // this ensures that the odometer only runs once every period
      updateEnd = System.currentTimeMillis();
      if (updateEnd - updateStart < ODOMETER_PERIOD) {
        try {
          Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
        } catch (InterruptedException e) {
          // there is nothing to be done here because it is not
          // expected that the odometer will be interrupted by
          // another thread
        }
      }
    }
  }

  public void getPosition(double[] position, boolean[] update) {
    // ensure that the values don't change while the odometer is running
    synchronized (lock) {
      if (update[0])
        position[0] = x;
      if (update[1])
        position[1] = y;
      if (update[2])
        position[2] = theta;
    }
  }

  public double getX() {
    double result;

    synchronized (lock) {
      result = x;
    }

    return result;
  }

  public double getY() {
    double result;

    synchronized (lock) {
      result = y;
    }

    return result;
  }

  public double getTheta() {
    double result;

    synchronized (lock) {
      result = theta;
    }

    return result;
  }

  // mutators
  public void setPosition(double[] position, boolean[] update) {
    // ensure that the values don't change while the odometer is running
    synchronized (lock) {
      if (update[0])
        x = position[0];
      if (update[1])
        y = position[1];
      if (update[2])
        theta = position[2];
    }
  }

  public void setX(double x) {
    synchronized (lock) {
      this.x = x;
    }
  }

  public void setY(double y) {
    synchronized (lock) {
      this.y = y;
    }
  }

  public void setTheta(double theta) {
    synchronized (lock) {
      this.theta = theta;
    }
  }

  /**
   * @return the leftMotorTachoCount
   */
  public int getLeftMotorTachoCount() {
    return leftMotorTachoCount;
  }

  /**
   * @param leftMotorTachoCount the leftMotorTachoCount to set
   */
  public void setLeftMotorTachoCount(int leftMotorTachoCount) {
    synchronized (lock) {
      this.leftMotorTachoCount = leftMotorTachoCount;
    }
  }

  /**
   * @return the rightMotorTachoCount
   */
  public int getRightMotorTachoCount() {
    return rightMotorTachoCount;
  }

  /**
   * @param rightMotorTachoCount the rightMotorTachoCount to set
   */
  public void setRightMotorTachoCount(int rightMotorTachoCount) {
    synchronized (lock) {
      this.rightMotorTachoCount = rightMotorTachoCount;
    }
  }
}
