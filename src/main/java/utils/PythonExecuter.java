package utils;

import com.opencsv.CSVReader;
import utils.VisualizerPrefs;

import java.io.*;
import java.util.ArrayList;

public class PythonExecuter {

    public void runWordCloudScript(String filename, String[] params, String identifier) {
        try {
            Runtime RT = Runtime.getRuntime();
            System.out.println("INFO: run python script for WordCloudScript");

            // Create command array by concatenating pythonCommand + filename + params
            String[] command = concatCommandArray(filename, params);

            File file = new File(identifier + "pythonOut.txt");
            Writer output = new BufferedWriter(new FileWriter(file));
            Process runObj = RT.exec(command);
            BufferedReader br = new BufferedReader(new InputStreamReader(runObj.getInputStream()));
            String temp = br.readLine();
            while (temp != null) {
                output.write(temp);
                temp = br.readLine();
            }
            output.close();

            file = new File(identifier + "pythonError.txt");
            output = new BufferedWriter(new FileWriter(file));
            br = new BufferedReader(new InputStreamReader(runObj.getErrorStream()));
            temp = br.readLine();
            while (temp != null) {
                output.write(temp);
                temp = br.readLine();
            }
            output.close();
        } catch (Exception e) {
            System.out.println("Python code execution failed with error "+ e.getCause()+ ". Check pythonError.txt for more details.");
        }
    }

    public ArrayList<String[]> runArticleTsne(String filename, String[] params) {
        ArrayList<String[]> outList = new ArrayList<>();
        try {
            // Setup executor and commands
            Runtime RT = Runtime.getRuntime();
            System.out.println("INFO: run python script for ArticleTsne");
            String[] command = concatCommandArray(filename, params);

            // Actually execute
            Process runObj = RT.exec(command);

            // Capture output using csvReader
            BufferedReader br = new BufferedReader(new InputStreamReader(runObj.getInputStream()));

            CSVReader csvReader = new CSVReader(br);

            String[] line;
            while ((line = csvReader.readNext()) != null) {
                if (line.length > 1) {
                    outList.add(line);
                }
            }
        } catch (Exception e) {
            System.out.println("Python code execution failed with error "+ e.getCause()+ ". Check pythonError.txt for more details.");
        }
        // Remove header
        if (outList.size() > 0) {
            outList.remove(0);
        }
        return outList;
    }
    private String[] concatCommandArray(String pythonScriptName, String[] params) {
        String[] pythonCommand = VisualizerPrefs.getInstance().getPythonExecutable();
        String[] command = new String[pythonCommand.length + 1 + params.length];

        System.arraycopy(pythonCommand, 0, command, 0, pythonCommand.length);
        command[pythonCommand.length] = pythonScriptName;
        System.arraycopy(params, 0, command, pythonCommand.length + 1, params.length);

        return command;
    }
}
