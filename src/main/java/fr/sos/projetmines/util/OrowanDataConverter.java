package fr.sos.projetmines.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;


public class OrowanDataConverter {

    private static final String CSV_SPLITTER = ";";
    private static final Logger LOGGER = LoggerFactory.getLogger(OrowanDataConverter.class);

    public OrowanDataConverter(String localInputFilePath, String outputFilePath) {
        try {
            InputStream inputCsv = getClass().getClassLoader().getResourceAsStream(localInputFilePath);
            if (inputCsv == null) {
                LOGGER.error("Cannot access the file at the provided the path \"{}\"!", localInputFilePath);
                return;
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputCsv));
            PrintWriter outputStream = new PrintWriter(new FileWriter(outputFilePath));

            NumberFormat commaFormat = NumberFormat.getInstance(Locale.FRANCE);
            DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
            otherSymbols.setDecimalSeparator('.');
            DecimalFormat decimalFormat = new DecimalFormat("#0.000", otherSymbols);

            // Write the headers to the output file
            outputStream.print("Cas\tHe\tHs\tTe\tTs\tDiam_WR\tWRyoung\toffset ini\tmu_ini\tForce\tG\n");

            String line ;
            while ((line = bufferedReader.readLine()) != null) {
                String[] values = line.split(CSV_SPLITTER);

                outputStream.print(values[0]);
                outputStream.print('\t');
                int[] columns = new int[]{ 4, 5, 6, 7, 10, 12, 17, 15, 8, 9 };
                for (int i = 0; i < columns.length; i++) {
                    double number = commaFormat.parse(values[columns[i]].trim()).doubleValue();
                    outputStream.print(decimalFormat.format(number));
                    if (i != columns.length - 1) {
                        outputStream.print('\t');
                    } else {
                        outputStream.println();
                    }
                }
            }
            outputStream.close();
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
