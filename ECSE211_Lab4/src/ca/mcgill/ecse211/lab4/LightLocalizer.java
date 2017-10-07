package ca.mcgill.ecse211.lab4;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;

public class LightLocalizer {

  //Initialize the lightSensor
  private static Port lightPort = LocalEV3.get().getPort("S4");
  @SuppressWarnings("resource") // Because we don't bother to close this resource
  EV3ColorSensor lightSensor = new EV3ColorSensor(lightPort); 
  SampleProvider lightSample = lightSensor.getRedMode(); //Red Mode measures the intensity of reflected light
  float[] lightData = new float[lightSample.sampleSize()]; //buffer to store sample data

  private Odometer odometer;
  private UltrasonicPoller usPoll;
  private EV3LargeRegulatedMotor leftMotor;
  private EV3LargeRegulatedMotor rightMotor;
  private double distance;
  private double x;
  private double y;
  private double theta;

  public LightLocalizer(Odometer odo, UltrasonicPoller poll, 
      EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor) {
    this.odometer = odo;
    this.usPoll = poll;
    this.leftMotor = leftMotor;
    this.rightMotor = rightMotor;
  }
  
  public static void lightLocalize() {
    
  }
}
