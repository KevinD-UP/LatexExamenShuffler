package org.genial.ark.command;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.genial.ark.domain.Document;
import picocli.CommandLine;

import java.util.List;


@CommandLine.Command(name = "les", description = "LaTeX main command",
        mixinStandardHelpOptions = true)
public class CommandLES implements Runnable{

    /**
     * Logger.
     */
    private static final Logger logger = LogManager.getLogger(CommandLES.class);

    @CommandLine.Parameters(hidden = true)   // "hidden": don't show this parameter in usage help message
    List<String> allParameters;  // no "index" attribute: captures _all_ arguments

    @CommandLine.Parameters(description = "Path of the input .tex exam file")
    String inputPath;    // assigned index = "0"


    /**
     * Runs the command.
     */
    @Override
    public void run() {
        logger.info("LES command called");
        logger.info("input path is " + inputPath);
        Document document = new Document(inputPath);
    }


}
