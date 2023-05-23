package org.genial.ark.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class LatexToPdfConverter {

    /**
     * Logger.
     */
    private static final Logger logger = LogManager.getLogger(org.genial.ark.services.LatexToPdfConverter.class);

    private final String compiler;

    public LatexToPdfConverter(String compiler){
        this.compiler = compiler;
    }

    public void convert(String latexFilePath, String outputdir) {
        // Run pdflatex command to compile LaTeX file
        logger.info("Trying to convert to pdf : " + latexFilePath);
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(this.compiler, "-output-directory=" + outputdir, latexFilePath);
            Process process = processBuilder.start();

            // Read the output of the command
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                // Do something with the output if needed
                logger.info(line);
            }

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
