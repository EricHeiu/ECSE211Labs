package ca.mcgill.ecse211.lab3;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.Sound;

public class Navigation extends Thread {

  private Odometer odometer;
  private double curX = 0;
  private double curY = 0.0;
  private double curTheta = 0.0;
  private double destTheta = 0.0; // destination point's angle
  private double thetaDifference = 0; // destinationAngle - currentAngle
  private double angleToTurn = 0;
  private int i = 0; // index for coordinates array
  private double distanceToTravel;

  private static final int FORWARD_SPEED = 200;
  private static final int ROTATE_SPEED = 150;
  public static final double WHEEL_RADIUS = 2.1;
  public static final double TRACK = 14.33;

  private EV3LargeRegulatedMotor leftMotor;
  private EV3LargeRegulatedMotor rightMotor;
  private boolean navigating;


  // constructor
  public Navigation(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
      Odometer odometer) {
    this.odometer = odometer;
    this.leftMotor = leftMotor;
    this.rightMotor = rightMotor;
    this.curX = odometer.getX();
    this.curY = odometer.getY();
    this.curTheta = odometer.getTheta();
    navigating = false;

    // reset the motors
    for (EV3LargeRegulatedMotor motor : new EV3LargeRegulatedMotor[] {leftMotor, rightMotor}) {
      motor.stop();
      motor.setAcceleration(300);
    }
  }

  /**
   * Method called when navigation thread is started. The method will loop through a one-dimensional
   * array that alternates between x and y coordinate values at each index and assigns them
   * correspondingly to the parameters of the method travelTo()
   */
  public void run() {
    // wait 5 seconds
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      // there is nothing to be done here because it is not expected that
      // the odometer will be interrupted by another thread
    }

    int i = 0;
    while (i < NavigationLab.coordinates.length) {
      this.curX = this.odometer.getX();
      this.curY = this.odometer.getY();
      this.curTheta = this.odometer.getTheta();
      travelTo(NavigationLab.coordinates[i], NavigationLab.coordinates[i + 1]);
      i = i + 2;
    }
  }

  /**
   * Method will move the robot to the absolute field location specified by x and y coordinates
   * 
   * @param destX The desired x coordinate to end up at
   * @param destY The desired y coordinate to end up at
   */
  public void travelTo(double destX, double destY) {

    // calculate change in position in terms of dimensions of tiles
    double deltaX = (destX * 30.48) - curX;
    double deltaY = (destY * 30.48) - curY;

    double destAngle = Math.atan2(deltaX, deltaY) * 180 / Math.PI; // angle is between -180 to 180
                                                                   // degrees

    // convert destAngle to be between 0 to 360 degrees
    if (destAngle < 0) {
      destAngle = destAngle + 360;
    }

    thetaDifference = destAngle - curTheta;

    /*
     * if robot's current heading is not facing destination point (within +- 5 degrees range) turn
     * to destination angle
     */
    if (!(thetaDifference <= 2 && thetaDifference >= -2)) {
      turnTo(destAngle);
      Sound.beep();
    }

    // use Pythagorean Theorem to calculate how far robot needs to travel
    distanceToTravel = Math.sqrt(Math.pow(deltaY, 2) + Math.pow(deltaX, 2));
    leftMotor.setSpeed(FORWARD_SPEED);
    rightMotor.setSpeed(FORWARD_SPEED);
    navigating = true;
    leftMotor.rotate(convertDistance(WHEEL_RADIUS, distanceToTravel), true);
    rightMotor.rotate(convertDistance(WHEEL_RADIUS, distanceToTravel), false);
    Sound.buzz();
    navigating = false;
  }

  /**
   * Method will rotate the robot to the absolute heading theta using a minimal angle
   * 
   * @param theta Desired absolute heading
   */
  private void turnTo(double theta) {

    leftMotor.setSpeed(ROTATE_SPEED);
    rightMotor.setSpeed(ROTATE_SPEED);

    if ((thetaDifference) >= -180 && (thetaDifference) <= 180) {
      angleToTurn = thetaDifference;
    }
    // correct the angle so robot turns clockwise at minimal angle
    else if ((thetaDifference) < -180) {
      angleToTurn = (thetaDifference) + 360;
    }
    // correct the angle so robot turns counter-clockwise at minimal angle
    else if ((thetaDifference) > 180) {
      angleToTurn = (thetaDifference) - 360;
    }
    leftMotor.rotate(convertAngle(WHEEL_RADIUS, TRACK, angleToTurn), true);
    rightMotor.rotate(-convertAngle(WHEEL_RADIUS, TRACK, angleToTurn), false);

  }


  /**
   * Method returns the value of the boolean variable navigating
   * 
   * @return Navigating
   */
  private boolean isNavigating() {

    return navigating;
  }

  /**
   * Method adjusts the desired distance based on the radius of the robot's wheels
   * 
   * @param radius Wheel radius
   * @param distance Desired travel distance
   * @return Converted distance
   */
  private static int convertDistance(double radius, double distance) {
    return (int) ((180.0 * distance) / (Math.PI * radius));
  }

  /**
   * Method adjusts the desired angle based on the radius of the robot's wheels and the length
   * between its' wheels
   * 
   * @param radius Wheel radius
   * @param width Length between wheels
   * @param angle Desired angle
   * @return Converted angle
   */
  private static int convertAngle(double radius, double width, double angle) {
    return convertDistance(radius, Math.PI * width * angle / 360.0);
  }
}
