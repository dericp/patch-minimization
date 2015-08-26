package edu.washington.bugisolation.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

/**
 * The Operations class performs all the utility functions of the bug
 * minimization process.
 *
 * @author dpang
 *
 */
public class Utils {

    public static final boolean DEBUG = true;

    /**
     * Runs a command on the command line.
     *
     * @param command
     *            the command that is to be executed on the command line, has a
     *            space between each argument
     * @param directory
     *            the directory in which the command will be executed
     * @param outputFilePath
     *            the path to which the output of the command line process will
     *            be saved
     * @return denoting the exit value of the command process
     */
    public static int commandLine(String command, String directory,
            String outputFilePath) {
        ArrayList<String> commandList = new ArrayList<String>();
        Scanner commandScan = new Scanner(command);
        while (commandScan.hasNext()) {
            commandList.add(commandScan.next());
        }
        commandScan.close();
        if (outputFilePath != null) {
            return startProcess(commandList, directory, outputFilePath);
        } else {
            return startProcess(commandList, directory);
        }
    }

    /**
     * Runs a command on the command line.
     *
     * @param command
     *            the command that is to be executed on the command line, has a
     *            space between each argument
     * @param directory
     *            the directory in which the command will be executed
     * @return
     */
    public static int commandLine(String command, String directory) {
        return commandLine(command, directory, null);
    }

    /**
     * Starts a process.
     *
     * @param commandList
     *            contains each argument as an element
     * @param directory
     *            the directory in which the process will be executed
     * @return the exit value of the process
     */
    private static int startProcess(List<String> commandList, String directory) {
        return startProcess(commandList, directory, null);
    }

    /**
     * Starts a process.
     *
     * @param commandList
     *            contains each argument as an element
     * @param directory
     *            the directory in which the process will be executed
     * @param outputFilePath
     *            the file to which the output of the process will be saved
     * @return
     */
    private static int startProcess(List<String> commandList, String directory,
            String outputFilePath) {
        try {
            ProcessBuilder pb = new ProcessBuilder(commandList);
            if ((outputFilePath) != null) {
                pb.redirectOutput(new File(outputFilePath));
            }
            pb.redirectErrorStream(true);
            pb.directory(new File(directory));
            Process pr = pb.start();
            BufferedReader input = new BufferedReader(new InputStreamReader(
                    pr.getInputStream()));

            String line;

            while ((line = input.readLine()) != null) {
                System.out.println(line);
            }

            int exitVal = pr.waitFor();
            System.out.println("Exited with error code " + exitVal);
            return exitVal;

        } catch (IOException | InterruptedException e) {
            System.out.println(e.toString());
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Converts a File to a List of Strings.
     *
     * @param filePath
     *            a String, the path of the file on the machine
     * @return a List of Strings, containing the information of the file at the
     *         designated file-path
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
     * @param fileLines
     *            a List of Strings, contains the information to be output in
     *            the file
     * @param filePath
     *            a String, the path of the file on the machine
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

    /**
     * Gets to the tests from a file that is in the ant test report format.
     *
     * @param filePath
     *            a String, the path of the file on the machine
     * @return a List of Strings, containing the information of the file at the
     *         designated file-path
     */
    public static List<String> getTests(String filePath) {
        List<String> lines = Utils.fileToLines(filePath);
        List<String> result = new ArrayList<String>();
        Iterator<String> iter = lines.listIterator();
        while (iter.hasNext()) {
            String line = iter.next();
            if (line.startsWith("---")) {
                result.add(line.substring(4));
            }
        }
        return result;
    }

    /**
     * Converts a boolean to its integer value.
     *
     * @param bool
     *            a bool, the one to be converted to an int
     * @return an int, 1 denoting true and -1 denoting false
     */
    public static int boolToInt(boolean bool) {
        if (bool) {
            return 1;
        }
        return -1;
    }

}
