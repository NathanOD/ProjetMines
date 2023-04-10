package fr.sos.projetmines;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world! 2");
        CSVInput csvInput = new CSVInput();
        csvInput.main(new String[]{});

        CSVOutput csv = new CSVOutput("C:/Users/hp/OneDrive/Desktop/projet IL/Fichiers/Model/output1.txt");
        csv.readCSV();
        String[][] data = csv.getData();
        for (String[] row : data) {
            for (String value : row) {
                System.out.print(value + "\t");
            }
            System.out.println();
        }
    }
}