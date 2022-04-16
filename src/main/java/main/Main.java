package main;

import javax.swing.*;

public class Main {

	public static void main(String[] args) {
		//This is what is run when the program
		//All of the code for the GUI is in ControlUI
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.out.println("Set look and feel failed with error " + e.getCause());
		}
		new ControlUI();
	}
}