package ca.mcgill.ecse211.lab4;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class UltrasonicLocalizer {

  private Odometer odometer;
  private UltrasonicPoller usPoll;
  private EV3LargeRegulatedMotor leftMotor;
  private EV3LargeRegulatedMotor rightMotor;
  private double distance;
  private double x;
  private double y;
  private double theta;

  public UltrasonicLocalizer(Odometer odo, UltrasonicPoller poll, 
      EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor){
    this.odometer = odo;
    this.usPoll = poll;
    this.leftMotor = leftMotor;
    this.rightMotor = rightMotor;
  }


  public void fallingEdge() {

  }

  public void risingEdge() {


  }
}

