package fr.sos.projetmines;

import java.text.DecimalFormat;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public class CSVReader {
    public  void main(String[] args) {

        String csvFile = "C:/Users/hp/OneDrive/Desktop/projet IL/Fichiers/Krakov/1939351_F2.txt";
        String txtFile = "C:/Users/hp/OneDrive/Desktop/projet IL/Fichiers/Krakov/1939351_F2_export.txt";
        String line = "";
        String csvSplitBy = ";";
        DecimalFormat df = new DecimalFormat("#0.000");

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile));
             PrintWriter pw = new PrintWriter(new FileWriter(txtFile))) {

            // Write the headers to the output file
            pw.print("Cas\t");
            pw.print("He\t");
            pw.print("Hs\t");
            pw.print("Te\t");
            pw.print("Ts\t");
            pw.print("Diam_WR\t");
            pw.print("WRyoung\t");
            pw.print("offset\t");
            pw.print("mu_ini\t");
            pw.print("Force\t");
            pw.print("G");
            pw.println();

            while ((line = br.readLine()) != null) {
                String[] values = line.split(csvSplitBy);

                // Extract the values from choosen columns
                String col1 = values[0];
                double col2 = Double.parseDouble(values[4].replace(',', '.'));
                String fCol2 = df.format(col2).replace(',', '.');
                double col3 = Double.parseDouble(values[5].replace(',', '.'));
                String fCol3 = df.format(col3).replace(',', '.');
                double col4 = Double.parseDouble(values[6].replace(',', '.'));
                String fCol4 = df.format(col4).replace(',', '.');
                double col5 = Double.parseDouble(values[7].replace(',', '.'));
                String fCol5 = df.format(col5).replace(',', '.');
                double col6 = Double.parseDouble(values[10].replace(',', '.'));
                String fCol6 = df.format(col6).replace(',', '.');
                double col7 = Double.parseDouble(values[12].replace(',', '.'));
                String fCol7 = df.format(col7).replace(',', '.');
                double col8 = Double.parseDouble(values[17].replace(',', '.'));
                String fCol8 = df.format(col8).replace(',', '.');
                double col9 = Double.parseDouble(values[15].replace(',', '.'));
                String fCol9 = df.format(col9).replace(',', '.');
                double col10 = Double.parseDouble(values[8].replace(',', '.'));
                String fCol10 = df.format(col10).replace(',', '.');
                double col11 = Double.parseDouble(values[9].replace(',', '.'));
                String fCol11 = df.format(col11).replace(',', '.');

                // Write the values to the output file, using a tab character as separator
    
                pw.print(col1 + "\t");
                pw.print(fCol2 + "\t");
                pw.print(fCol3 + "\t");
                pw.print(fCol4 + "\t");
                pw.print(fCol5 + "\t");
                pw.print(fCol6 + "\t");
                pw.print(fCol7 + "\t");
                pw.print(fCol8 + "\t");
                pw.print(fCol9 + "\t");
                pw.print(fCol10 + "\t");
                pw.print(fCol11);
                pw.println();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
