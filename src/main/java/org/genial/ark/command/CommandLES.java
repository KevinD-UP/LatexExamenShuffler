package org.genial.ark.command;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import picocli.CommandLine;


@CommandLine.Command(name = "les", description = "LaTeX main command",
        mixinStandardHelpOptions = true)
       // subcommands = {CommandSsgBuild.class, CommandSsgServe.class})
public class CommandLES implements Runnable{

    /**
     * Logger.
     */
    private static final Logger logger = LogManager.getLogger(CommandLES.class);

    /**
     * Runs the command.
     */
    @Override
    public void run() {
        logger.info("LES command called");
    }


}
