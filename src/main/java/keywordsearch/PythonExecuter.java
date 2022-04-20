package keywordsearch;

import com.opencsv.CSVReader;

import java.io.*;
import java.util.ArrayList;

public class PythonExecuter {

    Process mProcess;

    public void runWordCloudScript(String filename, String params[], String identifier) {
        try {
            Runtime RT = Runtime.getRuntime();
            System.out.println("INFO: run python script");
            String command = "conda run python " + filename + " " + params[0] + " " + params[1]; //0.05
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
            System.out.println("INFO: run python script");

            String[] command = new String[params.length + 4];
            command[0] = "conda";
            command[1] = "run";
            command[2] = "python";
            command[3] = filename;

            System.arraycopy(params, 0, command, 4, params.length);
            // Only for Yvan's computer since he put his sklearn install inside a conda env
//            String[] command = new String[params.length + 6];
//            command[0] = "conda";
//            command[1] = "run";
//            command[2] = "-n";
//            command[3] = "visual_analytics";
//            command[4] = "python";
//            command[5] = filename;
//
//            System.arraycopy(params, 0, command, 6, params.length);

            // Actually execute
            Process runObj = RT.exec(command);

            // Capture output using csvReader
            BufferedReader br = new BufferedReader(new InputStreamReader(runObj.getInputStream()));
//            String lineDebug;
//            while ((lineDebug = br.readLine()) != null) {
//                System.out.println(lineDebug);
//            }
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
}