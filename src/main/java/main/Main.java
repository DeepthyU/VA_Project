package main;

import org.apache.commons.cli.*;
import utils.VisualizerPrefs;

import javax.swing.*;
import java.nio.file.Paths;

public class Main {

	public static void main(String[] args) {
		// This is what is run when the program is launched
		// All of the code for the GUI is in ControlUI
		VisualizerPrefs prefs = VisualizerPrefs.getInstance();

		// Define command line arguments
		Options options = new Options();
		options.addOption("p", "python", true, "Command to run the python executable.");
		options.addOption("r", "root", true, "Path to the project root.");
		options.addOption("d", "data", true, "Path to the data directory relative to the project root.");

		// Parse command line arguments
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.err.println("Parsing failed. Reason: " + e.getMessage());
			System.exit(-1);
		}

		if (cmd.hasOption("p")) prefs.setPythonExecutable(cmd.getOptionValues("python"));
		if (cmd.hasOption("r")) prefs.setRootPath(Paths.get(cmd.getOptionValue("r")));
		if (cmd.hasOption("d")) prefs.setDataDirPath(Paths.get(cmd.getOptionValue("d")));

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.out.println("Set look and feel failed with error " + e.getCause());
		}
		new ControlUI();
	}
}