package ca.mcgill.ecse211.lab3;

public class NavigationLab {

	private static int[][] coordinates = { {2,1}, {1,1}, {1,2}, {2,0} };
	
	private static Navigation controller = new Navigation(0,0);
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		controller.travelTo(2,1);
		
	}

}
