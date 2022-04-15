package keywordsearch;

import java.io.*;

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
            e.printStackTrace();
        }
    }
}