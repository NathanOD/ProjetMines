package fr.sos.projetmines.calculator.util;

import fr.sos.projetmines.calculator.OrowanCalculator;
import fr.sos.projetmines.calculator.model.OrowanDataOutput;
import fr.sos.projetmines.calculator.model.OrowanSensorData;
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
import java.util.Optional;

public class DataFormatter {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataFormatter.class);

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

    /**
     * Generates a random salt for password hashing
     * @return the generated salt
     */
    public static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    /**
     * Takes the sensor input with id entryId from the database and writes it to the input.txt file for orowan software
     * @param entryId id of the entry in the database
     * @return whether the execution was successful
     */
    public Optional<OrowanSensorData> sensorDataToFile(int entryId) {
        Optional<OrowanSensorData> dataOpt = OrowanCalculator.getInstance().getDatabase().retrieveSensorData(entryId);;
        if(dataOpt.isEmpty()){
            return dataOpt;
        }
        OrowanSensorData data = dataOpt.get();
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
        otherSymbols.setDecimalSeparator('.');
        DecimalFormat decimalFormat = new DecimalFormat("#0.000", otherSymbols);
        try {
            Path inputFilePath = Path.of(System.getProperty("user.dir"), "input.txt");
            File inputFile = inputFilePath.toFile();
            if (!inputFile.exists()) {
                inputFile.createNewFile();
            }
            PrintWriter pw = new PrintWriter(new FileWriter(inputFile));

            pw.print("Cas\tHe\tHs\tTe\tTs\tDiam_WR\tWRyoung\toffset ini\tmu_ini\tForce\tG\n");
            pw.print(data.getLp());
            pw.print('\t');
            writeDouble(pw, data.getEntryThickness(), true, decimalFormat);
            writeDouble(pw, data.getExitThickness(), true, decimalFormat);
            writeDouble(pw, data.getEntryTension(), true, decimalFormat);
            writeDouble(pw, data.getExitTension(), true, decimalFormat);
            writeDouble(pw, data.getWorkRollDiameter(), true, decimalFormat);
            writeDouble(pw, data.getYoungModulus(), true, decimalFormat);
            writeDouble(pw, data.getAverageSigma(), true, decimalFormat);
            writeDouble(pw, data.getMu(), true, decimalFormat);
            writeDouble(pw, data.getRollForce(), true, decimalFormat);
            writeDouble(pw, data.getForwardSlip(), false, decimalFormat);
            return dataOpt;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Retrieves the data from Orowan computation in the provided file and saves it to the database
     * @param orowanOutput Orowan output file path
     */
    public Optional<OrowanDataOutput> saveOrowanOutputToDatabase(Path orowanOutput) {
        if (orowanOutput == null) {
            LOGGER.error("Cannot access the file at the provided the path!");
            return Optional.empty();
        }
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(orowanOutput.toFile()));
            // On se connecte à la base de données

            // On ouvre le fichier CSV
            bufferedReader.readLine(); // Skip header line
            String line = bufferedReader.readLine();
            String[] columns = line.split("\t");
            int caseId = Integer.parseInt(columns[0]);
            float offsetYield = Float.parseFloat((columns[2]));
            float friction = Float.parseFloat((columns[3]));
            float rollingTorque = Float.parseFloat((columns[4]));
            float sigmaMoy = Float.parseFloat((columns[5]));
            float sigmaIni = Float.parseFloat((columns[6]));
            float sigmaOut = Float.parseFloat((columns[7]));
            float sigmaMax = Float.parseFloat((columns[8]));
            float forceError = Float.parseFloat((columns[9]));
            float slipError = Float.parseFloat((columns[10]));

            OrowanDataOutput output = new OrowanDataOutput(caseId, columns[1], offsetYield, friction, rollingTorque, sigmaMoy,
                    sigmaIni, sigmaOut, sigmaMax, forceError, slipError, columns[11]);

            bufferedReader.close();
            OrowanCalculator.getInstance().getDatabase().saveOrowanOutput(output);
            return Optional.of(output);
        } catch (IOException exception) {
            LOGGER.error(exception.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Starts the Orowan software with the correct parameters: i | c | input.txt | output.txt
     * @param orowanPath
     */
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

    private void writeDouble(PrintWriter printWriter, double value, boolean splitting, DecimalFormat format){
        printWriter.print(format.format(value));
        if(splitting)
            printWriter.print('\t');
    }
}
