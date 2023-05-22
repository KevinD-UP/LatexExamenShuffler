package org.genial.ark;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.genial.ark.command.CommandLES;
import picocli.CommandLine;

/**
 * Hello world!
 *
 */
public class App 
{

    /**
     * Logger.
     */
    private static final Logger logger = LogManager.getLogger(App.class);

    public static void main( String[] args )
    {
        logger.info("Launch app");
        int exitCode = new CommandLine(new CommandLES()).execute(args);
        System.exit(exitCode);
    }
}
