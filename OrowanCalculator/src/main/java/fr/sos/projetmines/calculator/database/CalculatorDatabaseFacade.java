package fr.sos.projetmines.calculator.database;


import fr.sos.projetmines.Job;
import fr.sos.projetmines.calculator.model.OrowanDataOutput;
import fr.sos.projetmines.calculator.model.OrowanSensorData;
import fr.sos.projetmines.commonutils.database.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class CalculatorDatabaseFacade extends DatabaseConnection {

    private final AuthenticatorRequests authenticatorRequests;
    private final OrowanDataRequests orowanDataRequests;
    private final UserManagingRequests userManagingRequests;

    public CalculatorDatabaseFacade(String address, String username, String password) {
        super(address, username, password);
        this.authenticatorRequests = new AuthenticatorRequests(this);
        this.orowanDataRequests = new OrowanDataRequests(this);
        this.userManagingRequests = new UserManagingRequests(this);
    }

    public byte[][] getPasswordAndSalt(String username){
        return authenticatorRequests.getPasswordAndSalt(username);
    }

    public boolean checkUserExistence(String username) {
        return authenticatorRequests.checkUserExistence(username);
    }

    public Optional<OrowanSensorData> retrieveSensorData(int entryId) {
        return orowanDataRequests.retrieveSensorData(entryId);
    }

    public void saveOrowanOutput(OrowanDataOutput data) {
        orowanDataRequests.saveOrowanOutput(data);
    }

    public Optional<Job> getUserJob(String username) {
        return userManagingRequests.getUserJob(username);
    }

    public void setUserJob(String username, Job job) {
        userManagingRequests.setUserJob(username, job);
    }

    public void addUser(String username, byte[] password, byte[] salt, Job job) {
        userManagingRequests.addUser(username, password, salt, job);
    }

    public void deleteUser(String username) {
        userManagingRequests.deleteUser(username);
    }
}
