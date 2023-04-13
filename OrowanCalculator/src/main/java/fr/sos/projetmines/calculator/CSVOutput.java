package fr.sos.projetmines.calculator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CSVOutput {

    private String file;
    private String[][] data;

    public CSVOutput(String fileName) {
        this.file = fileName;
    }

    public void readCSV() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(this.file));
            String line;
            int row = 0;
            while ((line = br.readLine()) != null) {
                String[] values = line.split("\t");
                if (row == 0) {
                    data = new String[1][values.length];
                } else {
                    String[][] newData = new String[row+1][values.length];
                    System.arraycopy(data, 0, newData, 0, row);
                    data = newData;
                }
                for (int i = 0; i < values.length; i++) {
                    data[row][i] = values[i];
                }
                row++;
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[][] getData() {
        return data;
    }
}