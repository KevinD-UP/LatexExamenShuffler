package org.genial.ark.command;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import picocli.CommandLine;

import java.util.List;


@CommandLine.Command(name = "les", description = "LaTeX main command",
        mixinStandardHelpOptions = true)
       // subcommands = {CommandSsgBuild.class, CommandSsgServe.class})
public class CommandLES implements Runnable{

    /**
     * Logger.
     */
    private static final Logger logger = LogManager.getLogger(CommandLES.class);

    @CommandLine.Parameters(hidden = true)   // "hidden": don't show this parameter in usage help message
    List<String> allParameters;  // no "index" attribute: captures _all_ arguments

    @CommandLine.Parameters
    String group;    // assigned index = "0"
    @CommandLine.Parameters
    String artifact; // assigned index = "1"


    /**
     * Runs the command.
     */
    @Override
    public void run() {
        logger.info("LES command called");
        System.out.println(group);
        System.out.println(artifact);
    }


}
