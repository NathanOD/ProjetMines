package fr.sos.projetmines.calculator.util;

import fr.sos.projetmines.calculator.model.OrowanDataOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DataFormatter {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataFormatter.class);

    public void formatToFile() {
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
        otherSymbols.setDecimalSeparator('.');
        DecimalFormat decimalFormat = new DecimalFormat("#0.000", otherSymbols);

        double[] doubleValues = CalculatorDatabaseConnection.getInstance().getData();

        try {
            Path inputFilePath = Path.of(System.getProperty("user.dir"), "input.txt");
            File inputFile = inputFilePath.toFile();
            if (!inputFile.exists()) {
                inputFile.createNewFile();
            }
            PrintWriter inputStream = new PrintWriter(new FileWriter(inputFile));

            inputStream.print("Cas\tHe\tHs\tTe\tTs\tDiam_WR\tWRyoung\toffset ini\tmu_ini\tForce\tG\n");
            inputStream.print((int)(doubleValues[0]));
            inputStream.print('\t');
            for (int i = 1; i < doubleValues.length; i++) {
                inputStream.print(decimalFormat.format(doubleValues[i]));
                if (i != doubleValues.length - 1) {
                    inputStream.print('\t');
                }
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void outputToDatabase(Path filePath) throws IOException{

        if (filePath == null) {
            LOGGER.error("Cannot access the file at the provided the path!");
            return;
        }
        BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath.toFile()));
        // On se connecte à la base de données

        // On ouvre le fichier CSV
        bufferedReader.readLine(); // Skip header line
        String line = bufferedReader.readLine();
        String[] columns = line.split("\t");
        int integer = Integer.parseInt(columns[0]);
        float double1 = Float.parseFloat((columns[2]));
        float double2 = Float.parseFloat((columns[3]));
        float double3 = Float.parseFloat((columns[4]));
        float double4 = Float.parseFloat((columns[5]));
        float double5 = Float.parseFloat((columns[6]));
        float double6 = Float.parseFloat((columns[7]));
        float double7 = Float.parseFloat((columns[8]));
        float double8 = Float.parseFloat((columns[9]));
        float double9 = Float.parseFloat((columns[10]));

        OrowanDataOutput output = new OrowanDataOutput(integer, columns[1], double1, double2, double3, double4, double5,
                double6, double7, double8, double9, columns[11]);

        bufferedReader.close();

        CalculatorDatabaseConnection.getInstance().addData(output);
    }

    public void runOrowan(Path orowanPath) {
        ProcessBuilder pb = new ProcessBuilder(orowanPath.toAbsolutePath().toString());
        pb.redirectErrorStream(true);

        try {
            Process p = pb.start();

            OutputStream out = p.getOutputStream();
            InputStream in = p.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(out));
            Path input = Path.of(System.getProperty("user.dir"), "input.txt");
            Path output = Path.of(System.getProperty("user.dir"), "output.txt");
            bf.write('i');
            bf.newLine();
            bf.flush();
            bf.write('c');
            bf.newLine();
            bf.flush();
            bf.write(input.toAbsolutePath().toString());
            bf.newLine();
            bf.flush();
            bf.write(output.toAbsolutePath().toString());
            bf.newLine();
            bf.flush();
            bf.close();
            br.close();
            p.waitFor();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
    /**
     * @param pass password to hash
     * @param salt salt used to hash this password
     * @return a String containing the hashed password
     */
    public static byte[] hashPassword(String pass, byte[] salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            return md.digest(pass.getBytes());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }
}
