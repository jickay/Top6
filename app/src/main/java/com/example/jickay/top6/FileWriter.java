package com.example.jickay.top6;

import android.content.Context;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import static android.content.Context.MODE_APPEND;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by ViJack on 7/10/2017.
 */

public class FileWriter {

    private ArrayList<String> outputText;
    private String textFile = "tasks.txt";

    // Constructors
    public FileWriter() {}

    // Getter / setter methods
    public void setTextFile(String fileName) { textFile = fileName; }
    public String getTextFile() { return textFile; }

    // Main methods
    public void onSave(Context context) {
        setOutputText(context);
        try {
            FileOutputStream fileOut = context.openFileOutput(textFile,MODE_PRIVATE);
            OutputStreamWriter outputWriter = new OutputStreamWriter(fileOut);

            try {
                String separator = System.getProperty("line.separator");

                for (String aLine : outputText) {
                    outputWriter.write(aLine);
                    outputWriter.append(separator);
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

            outputWriter.flush();
            outputWriter.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void onLoad(Context context) {
        try {
            FileInputStream fileIn = context.openFileInput(textFile);
            BufferedReader r = new BufferedReader(new InputStreamReader(fileIn));

            MainActivity.getIncompleteTasks().clear();

            String aLine = "";
            while ((aLine = r.readLine()) != null) {
                String[] lineParts = aLine.split(",");
                Task task = new Task(lineParts[0],lineParts[1],lineParts[2],
                        Integer.parseInt(lineParts[3]));
                MainActivity.getIncompleteTasks().add(task);
            }

            r.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void setOutputText(Context context) {
        outputText = new ArrayList<>();
        for (Task task : MainActivity.getIncompleteTasks()){
            String taskLine = task.getTitle() + ","
                    + task.getDate() + ","
                    + task.getDescription() + ","
                    + Integer.toString(task.getImportance()) + ","
                    + Boolean.toString(task.getCompletion());
            outputText.add(taskLine);
        }
    }
}
