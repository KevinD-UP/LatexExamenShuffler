package org.genial.ark.command;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.genial.ark.domain.Document;
import org.genial.ark.services.LatexToPdfConverter;
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

    @CommandLine.Parameters(description = "Number of desired variations to generate")
    int numberVariations;    // assigned index = "1"


    /**
     * Output Directory.
     */
    @CommandLine.Option(names = {"--output-dir"},
            description = "output directory, default is output/")
    private String outputDir = "output/";

    /**
     * Output filename.
     */
    @CommandLine.Option(names = {"--output-filename"},
            description = "output filename, default is generated")
    private String filename = "generated";

    /**
     * compiler.
     */
    @CommandLine.Option(names = {"--compiler"},
            description = "compiler, possible choices are: pdflatex, lualatex or xelatex. Default is pdflatex")
    private String compiler = "pdflatex";

    /**
     * Runs the command.
     */
    @Override
    public void run() {
        logger.info("LES command called");
        logger.info("input path is " + inputPath);
        if (!outputDir.endsWith("/")) {
            outputDir = outputDir + "/";
        }
        Document document = new Document(inputPath);
        document.generateVariations(outputDir, filename, numberVariations);
        LatexToPdfConverter converter = new LatexToPdfConverter(compiler);
        for(int i = 0; i < numberVariations; i++) {
            for(int j = 0; j < 2; j++) {
                converter.convert(outputDir + filename + (i+1) + ".tex", outputDir);
            }
        }
    }


}
