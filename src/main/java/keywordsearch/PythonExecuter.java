package keywordsearch;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class PythonExecuter {

    Process mProcess;

    public void runWordCloudScript() {
        try {
            Runtime RT = Runtime.getRuntime();
            System.out.println("INFO: run python script");
            String command = "C:\\Users\\dipuu\\anaconda3\\Scripts\\conda.exe run python word_cloud.py search_text.txt 0.05"; //0.05
            File file = new File("pythonOut.txt");
            Writer output = new BufferedWriter(new FileWriter(file));
            Process runObj = RT.exec(command);
            BufferedReader br = new BufferedReader(new InputStreamReader(runObj.getInputStream()));
            String temp = br.readLine();
            while (temp != null) {
                output.write(temp);
                temp = br.readLine();
            }
            output.close();

            file = new File("pythonError.txt");
            output = new BufferedWriter(new FileWriter(file));
            br = new BufferedReader(new InputStreamReader(runObj.getErrorStream()));
            temp = br.readLine();
            while (temp != null) {
                output.write(temp);
                temp = br.readLine();
            }
            output.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}