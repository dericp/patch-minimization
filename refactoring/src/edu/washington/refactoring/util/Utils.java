package edu.washington.refactoring.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides all the utility functions for the automatic refactoring
 * program.
 * @author dpang
 *
 */
public class Utils {
    
    /**
     * Reads in a specified file and returns its lines as a List of Strings.
     *
     * @param filePath specifies the absolute path of the file to be read
     * @return a List of Strings where each element is a line in the specified
     *         file
     */
    public static List<String> fileToLines(String filePath) {
        List<String> lines = new ArrayList<String>();
        String line = "";
        try {
            BufferedReader in = new BufferedReader(new FileReader(filePath));
            while ((line = in.readLine()) != null) {
                lines.add(line);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }
    
    /**
     * Converts a List of Strings to a File.
     *
     * @param fileLines is a List of Strings where each element is a line in
     *        the file to be created
     * @param filePath specifies the absolute path of the file to be created
     * @return
     */
    public static File linesToFile(List<String> fileLines, String filePath) {
        File file = new File(filePath);
        try {
            PrintStream output = new PrintStream(file);
            for (String line : fileLines) {
                if (line != null) {
                    output.println(line);
                }
            }
            output.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return file;
    }
}
