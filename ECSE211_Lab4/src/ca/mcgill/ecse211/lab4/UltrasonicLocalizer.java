package ca.mcgill.ecse211.lab4;

import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class UltrasonicLocalizer {

  private Odometer odometer;
  private UltrasonicPoller usPoll;
  private EV3LargeRegulatedMotor leftMotor;
  private EV3LargeRegulatedMotor rightMotor;
  private double distance;
  private double x;
  private double y;
  private double leftWallTheta;
  private double backWallTheta;
  private boolean corrected = false;
  //private final double NOISE_MARGIN = 20;
  private final double D = 20;
  private final double K = 20; //change name

  public UltrasonicLocalizer(Odometer odo, UltrasonicPoller poll, 
      EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor){
    this.odometer = odo;
    this.usPoll = poll;
    this.leftMotor = leftMotor;
    this.rightMotor = rightMotor;
  }


  public void fallingEdge() {
    leftMotor.setSpeed(150);
    rightMotor.setSpeed(150);
    while(usPoll.readUSDistance()[0] > D-K) {
      leftMotor.forward();
      rightMotor.backward();
      if(usPoll.readUSDistance()[0] < D-K) {
        Sound.beep();
        leftMotor.stop();
        rightMotor.stop();
        backWallTheta = odometer.getTheta();
        break;
      }
    }
    while(usPoll.readUSDistance()[0] > D-K) {
      leftMotor.backward();
      rightMotor.forward();
      if(usPoll.readUSDistance()[0] < D-K) {
        Sound.beep();
        leftMotor.stop();
        rightMotor.stop();
        backWallTheta = odometer.getTheta();
        break;
      }
    }

  }

  public void risingEdge() {


  }
}

