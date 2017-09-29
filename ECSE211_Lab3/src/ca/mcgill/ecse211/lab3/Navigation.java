package ca.mcgill.ecse211.lab3;

public class Navigation {

	private static int[][] coordinates = { {2,1}, {1,1}, {1,2}, {2,0} };
	
	private static double curX = 0.0;
	private static double curY = 0;
	
	private static double angToTurn;
	
	
	
	
	public static void main(String[] args) { 
		travelTo(2,1);
	}
	/* private int getX(int index) {
		
	}
	
	private int getY(int index) {
		
	}*/
	
	
	private static void travelTo(double destX, double destY) {
		if(destX > 0) {
			angToTurn = Math.atan((destY - curY) / (destX - curX));
			turnTo(angToTurn);
		}
		else if(destX < 0 && destY > 0) {
			angToTurn = Math.atan((destY - curY) / (destX - curX)) + Math.PI;
			turnTo(angToTurn);
		}
		
		else if(destX < 0 && destY < 0) {
			angToTurn = Math.atan((destY - curY) / (destX - curX)) - Math.PI;
			turnTo(angToTurn);
		}
		
	}
	
	private static void turnTo(double theta) {
		
	}
	
	private boolean isNavigating() {
		
		return false;
	}
}
