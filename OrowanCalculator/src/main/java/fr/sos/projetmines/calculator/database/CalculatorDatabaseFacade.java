package fr.sos.projetmines.calculator.database;


import fr.sos.projetmines.CurvePoint;
import fr.sos.projetmines.Job;
import fr.sos.projetmines.Stand;
import fr.sos.projetmines.calculator.model.OrowanDataOutput;
import fr.sos.projetmines.calculator.model.OrowanInputDataRange;
import fr.sos.projetmines.calculator.model.OrowanSensorData;
import fr.sos.projetmines.commonutils.database.DatabaseConnection;

import java.util.Optional;
import java.util.Set;

public class CalculatorDatabaseFacade extends DatabaseConnection {

    private final AuthenticatorRequests authenticatorRequests;
    private final OrowanDataRequests orowanDataRequests;
    private final UserManagingRequests userManagingRequests;
    private final InputRangeRequests inputRangeRequests;

    private final StandRequests standRequests;

    /**
     * Creates a new instance of the CalculatorDatabaseFacade.
     * @param address the address of the database server
     * @param username the username to use for the database connection
     * @param password the password to use for the database connection
     */
    public CalculatorDatabaseFacade(String address, String username, String password) {
        super(address, username, password);
        this.authenticatorRequests = new AuthenticatorRequests(this);
        this.orowanDataRequests = new OrowanDataRequests(this);
        this.userManagingRequests = new UserManagingRequests(this);
        this.inputRangeRequests = new InputRangeRequests(this);
        this.standRequests = new StandRequests(this);
    }

    /**
     * Retrieves the password and salt for the specified username.
     * @param username the username to retrieve the password and salt for
     * @return a byte array containing the password and salt for the specified username
     */
    public byte[][] getPasswordAndSalt(String username) {
        return authenticatorRequests.getPasswordAndSalt(username);
    }

    /**
     * Checks if a user with the specified username exists in the database.
     * @param username the username to check for existence
     * @return true if a user with the specified username exists, false otherwise
     */
    public boolean checkUserExistence(String username) {
        return authenticatorRequests.checkUserExistence(username);
    }

    /**
     * Retrieves the Orowan sensor data for the specified entry ID.
     * @param entryId the ID of the Orowan sensor data to retrieve
     * @return an {@link Optional} containing the Orowan sensor data for the specified entry ID, or an empty
     * Optional if no data exists for the specified ID
     */
    public Optional<OrowanSensorData> retrieveSensorData(int entryId) {
        return orowanDataRequests.retrieveSensorData(entryId);
    }

    /**
     * Saves the specified Orowan data output to the database.
     * @param data the Orowan data output to save
     */
    public void saveOrowanOutput(OrowanDataOutput data) {
        orowanDataRequests.saveOrowanOutput(data);
    }

    /**
     * Saves the specified Orowan average output to the database.
     * @param meanXTime the mean value of the product of the load and time
     * @param averageFriction the average friction value
     */
    public void saveOrowanAverageOutput(float meanXTime, float averageFriction) {
        orowanDataRequests.saveOrowanAverageOutput(meanXTime, averageFriction);
    }

    /**
     * Retrieves the job for the specified username.
     * @param username the username to retrieve the job for
     * @return an {@link Optional} containing the job for the specified username, or an empty Optional if no job
     * exists for the specified username
     */
    public Optional<Job> getUserJob(String username) {
        return userManagingRequests.getUserJob(username);
    }

    /**
     * Sets the job of a given user.
     * @param username The username of the user whose job is being set.
     * @param job The new job of the user.
     * @return true if the job was successfully set, false otherwise.
     */
    public boolean setUserJob(String username, Job job) {
        return userManagingRequests.setUserJob(username, job);
    }

    /**
     * Adds a new user with the given username, password, salt, and job.
     * @param username The username of the new user.
     * @param password The password of the new user.
     * @param salt The salt used to hash the password.
     * @param job The job of the new user.
     */
    public void addUser(String username, byte[] password, byte[] salt, Job job) {
        userManagingRequests.addUser(username, password, salt, job);
    }

    /**
     * Deletes a user with the given username.
     * @param username The username of the user to delete.
     * @return true if the user was successfully deleted, false otherwise.
     */
    public boolean deleteUser(String username) {
        return userManagingRequests.deleteUser(username);
    }

    /**
     * Gets the input data ranges.
     * @return A set of OrowanInputDataRange objects representing the input data ranges.
     */
    public Set<OrowanInputDataRange> getInputRanges() {
        return inputRangeRequests.getInputRanges();
    }

    /**
     * Updates a constraint input range.
     * @param inputDataRange The new input data range.
     */
    public void updateConstraintInputRange(OrowanInputDataRange inputDataRange) {
        inputRangeRequests.updateConstraintInputRange(inputDataRange);
    }

    /**
     * Gets a set of all usernames.
     * @return An Optional containing a set of all usernames, or an empty Optional if there are no users.
     */
    public Optional<Set<String>> getUsers() {
        return userManagingRequests.getUsers();
    }

    /**
     * Gets a set of all stands.
     * @return An Optional containing a set of all stands, or an empty Optional if there are no stands.
     */
    public Optional<Set<Stand>> getStands() {
        return standRequests.getStands();
    }

    /**
     * Sets the state of a stand with the given ID.
     * @param id The ID of the stand to update.
     * @param state The new state of the stand.
     * @return true if the stand state was successfully updated, false otherwise.
     */
    public boolean setStandState(int id, boolean state){ return standRequests.setStandState(id,state); }
}
