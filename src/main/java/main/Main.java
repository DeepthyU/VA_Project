package main;

public class Main {

	public static void main(String[] args) {
		//This is what is run when the program
		//All of the code for the GUI is in ControlUI
		ControlUI mainUI = new ControlUI(); 
		if (args.length > 0) {
			mainUI.loadGraph();
		}
	}
}