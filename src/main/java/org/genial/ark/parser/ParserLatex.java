package org.genial.ark.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.genial.ark.App;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

public class ParserLatex {


    /**
     * Logger.
     */
    private static final Logger logger = LogManager.getLogger(ParserLatex.class);

    public void parse(){
        try
        {
            //the file to be opened for reading
            FileInputStream fis=new FileInputStream("Demo.txt");
            Scanner sc=new Scanner(fis);    //file to be scanned
            //returns true if there is another line to read
            while(sc.hasNextLine())
            {
                System.out.println(sc.nextLine());      //returns the line that was skipped
            }
            sc.close();     //closes the scanner
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }


}
