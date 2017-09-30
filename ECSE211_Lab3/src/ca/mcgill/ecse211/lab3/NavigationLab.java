package ca.mcgill.ecse211.lab3;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;

public class NavigationLab {

	private static int[][] coordinates = { {2,1}, {1,1}, {1,2}, {2,0} };

	private static Navigation controller = new Navigation(0,0);
	private static final Port usPort = LocalEV3.get().getPort("S1");
	public static final EV3LargeRegulatedMotor leftMotor =
			new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	public static final EV3LargeRegulatedMotor rightMotor =
			new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		leftMotor.setSpeed(200);
		leftMotor.forward();
		//controller.travelTo(2,1);

	}

}
