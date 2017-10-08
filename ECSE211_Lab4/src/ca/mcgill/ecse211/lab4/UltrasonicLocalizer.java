package ca.mcgill.ecse211.lab4;

import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class UltrasonicLocalizer {

  private Odometer odometer;
  private UltrasonicPoller usPoll;
  private EV3LargeRegulatedMotor leftMotor;
  private EV3LargeRegulatedMotor rightMotor;
  private Navigation navigation;
  private double distance;
  private double x;
  private double y;
  private double leftWallTheta;
  private double backWallTheta;
  // private boolean corrected = false;
  private double correctedTheta;
  private final double NOISE_MARGIN_RISING = 45; //D+K
  private final double NOISE_MARGIN_FALLING = 35; //D-K

  private boolean detectBack = false;
  private boolean detectLeft = false;
  private int counter = 0;

  //private final double D = 40;
  //private final double K = 20; //change name

  public UltrasonicLocalizer(Odometer odo, UltrasonicPoller poll, 
      EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, Navigation navigation){
    this.odometer = odo;
    this.usPoll = poll;
    this.leftMotor = leftMotor;
    this.rightMotor = rightMotor;
    this.navigation = navigation;
  }


  public void fallingEdge() {
    leftMotor.setSpeed(150);
    rightMotor.setSpeed(150);
    leftMotor.forward();
    rightMotor.backward();
    if(usPoll.readUSDistance() >= NOISE_MARGIN_RISING) {     //robot starts facing away from wall
      while(counter != 2) {
        if(usPoll.readUSDistance() <= NOISE_MARGIN_FALLING) {
          Sound.beep();
          leftMotor.stop();
          rightMotor.stop();
          counter++;
          if(counter == 1) {
            backWallTheta = odometer.getTheta();

            leftMotor.backward();
            rightMotor.forward();
          }
          else if(counter == 2) {
            leftWallTheta = odometer.getTheta();
          }
        }
      }
    }
    else if(usPoll.readUSDistance() <= NOISE_MARGIN_FALLING) {   //robot starts facing towards a wall
      while(detectBack == false || detectLeft == false) {
        if(usPoll.readUSDistance() >= NOISE_MARGIN_RISING && detectLeft == false) {
          Sound.beep();
          leftMotor.stop();
          rightMotor.stop();
          leftWallTheta = odometer.getTheta();
          detectLeft = true;
          leftMotor.forward();
          rightMotor.forward();
        }
        else if(usPoll.readUSDistance() <= NOISE_MARGIN_FALLING && detectLeft == true) {
          Sound.beep();
          leftMotor.stop();
          rightMotor.stop();
          backWallTheta = odometer.getTheta();
          detectBack = true;
        }

      }
    }

    if(backWallTheta < leftWallTheta) {     //a < b
      correctedTheta = 45 - (backWallTheta + leftWallTheta)/2;
    }
    else if(backWallTheta > leftWallTheta) {    //a > b
      correctedTheta = 225 - (backWallTheta + leftWallTheta)/2;
    }
    odometer.setTheta(correctedTheta);
    navigation.turnTo(correctedTheta);
  }

  public void risingEdge() {


  }
}

