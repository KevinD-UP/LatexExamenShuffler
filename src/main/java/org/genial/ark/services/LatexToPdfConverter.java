package org.genial.ark.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;

public class LatexToPdfConverter {

    /**
     * Logger.
     */
    private static final Logger logger = LogManager.getLogger(org.genial.ark.services.LatexToPdfConverter.class);

    private final String compiler;

    public LatexToPdfConverter(String compiler){
        if(!compiler.equals("pdflatex") && !compiler.equals("lualatex") && !compiler.equals("xelatex")){
            logger.error("This compiler does not exist or you did not install it");
            System.exit(-1);
        }
        this.compiler = compiler;
    }

    public void convert(String latexFilePath, String outputdir) {
        // Run pdflatex command to compile LaTeX file
        logger.info("Trying to convert to pdf : " + latexFilePath + " with " + this.compiler);
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(this.compiler, "-output-directory=" + outputdir, latexFilePath);
            Process process = processBuilder.start();

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                logger.info("PDF generated successfully.");
            } else {
                logger.error("Error occurred while generating PDF.");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
