package Crawler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Writes the JSON web pages to a file.
 *
 * @author Seth Dovgan
 * @version 22FEB18
 */
public class FileProcessor {

    private PrintWriter writer;
    private String fileName;

    /**
     * Constructor. Sets the name of the file to write the Json strings to
     * @param fileName to write the Json string to
     */
    public FileProcessor(String fileName){

        this.fileName = fileName;
    }

    /**
     * Sets up the output file. This method must be called before
     * any other functions are called in the class.
     */
    public void setup(){

        File outputFile;

        try {

            // Create a new file and writer
            outputFile = new File(fileName);
            writer = new PrintWriter(outputFile);

        // Print error message if file is not accessible
        } catch(FileNotFoundException e){

            System.err.println("File " + fileName + " could not be found.");
        }
    }

    /**
     * Close the open file writer.
     */
    public void teardown(){

        // Check if the writer has been initialized first
        if(writer != null){
            writer.close();
        }
    }

    /**
     * Appends the given json to the file.
     * @param json to write to the file.
     */
    public void writeJSONToFile(String json){

        writer.println(json);
    }
}
