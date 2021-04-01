package hu.unimiskolc;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * @author Csaba Tamás tamascsaba98@gmail.com
 * @version 1.0
 * @since 1.0
 */
public class Main {
    /**
     * String constant containing the footer text
     */
    private static final String PagePattern = "© Adobe Systems Incorporated 2008 – All rights reserved";

    /**
     * Regular expression finding the Figures.
     */
    private static final String figureRegex = "^Figure ((L.\\d+)|\\d+) –.*$";

    /**
     * Regular expression finding the page numbers.
     */
    private static final String PageRegex = "(" + PagePattern + " \\d+)|(\\d+ " + PagePattern + ")";

    /**
     * The File object of the PDF document.
     */
    private static File fileName;

    /**
     * @see org.apache.pdfbox.pdmodel.PDDocument
     */
    private static PDDocument doc = null;

    /**
     * @see org.apache.pdfbox.text.PDFTextStripper
     */
    private static PDFTextStripper stripper = null;

    /**
     * The lines of the PDF document
     */
    private static String PDFLines;

    /**
     * @see java.util.regex.Pattern
     */
    private static Pattern fig;

    /**
     * @see java.util.regex.Pattern
     */
    private static Pattern page;

    /**
     * If one page contains more than one figure, we should store them in an array until printing out.
     */
    private static List<String> figures = new ArrayList<>();

    /**
     * Export figures.
     * <p>
     * This program will take the https://www.adobe.com/content/dam/acom/en/devnet/pdf/pdfs/PDF32000_2008.pdf file and
     * export out the figures' reference with page numbers.
     * <p>
     *
     * @param args The arguments of the program. It takes only one arg, the file path.
     */
    public static void main(String[] args) {
        //Turning off warning messages.
        Logger.getLogger("org.apache.pdfbox").setLevel(Level.OFF);

        //Check, if we got exactly one argument.
        if (args.length != 1) {
            System.out.println("Looking for only one argument");
            System.exit(-1);
        } else
            fileName = new File(args[0]);

        //Try to load the PDF file and strip out all of the text
        try {
            doc = PDDocument.load(fileName);
            stripper = new PDFTextStripper();
            PDFLines = stripper.getText(doc);

            doc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Compile the regular expressions
        fig = Pattern.compile(figureRegex);
        page = Pattern.compile(PageRegex);

        //Iterate through the lines.
        for (String line : PDFLines.split("\\n")) {
            //If we find a Figure match, we add it to the list
            if (fig.matcher(line).find()) {
                figures.add(line);
            }

            //After the figure(s), we're looking for page regex match. On finding one, we print out all of the figures in the list.
            if (figures.size() > 0 && page.matcher(line).find()) {
                for (String figure : figures) {
                    System.out.println(figure.trim().replace("– ", "-") + " - Page " + (Integer.parseInt(line.replace(PagePattern, "").trim()) - 1));
                }
                figures.clear();
            }
        }
    }
}
