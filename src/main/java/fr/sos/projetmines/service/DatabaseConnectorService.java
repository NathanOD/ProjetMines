package fr.sos.projetmines.service;

import fr.sos.projetmines.MinesProject;
import fr.sos.projetmines.util.DatabaseConnection;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class DatabaseConnectorService extends Service<Boolean> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConnectorService.class);

    private final StringProperty identifier = new SimpleStringProperty(this, "identifier");
    private final StringProperty password = new SimpleStringProperty(this, "password");

    public String getIdentifier() {
        return identifier.getValue();
    }

    public void setIdentifier(String newIdentifier) {
        identifier.set(newIdentifier);
    }

    public final StringProperty identifierProperty() {
        return identifier;
    }

    public String getPassword() {
        return password.getValue();
    }

    public void setPassword(String newPassword) {
        password.set(newPassword);
    }

    public final StringProperty passwordProperty() {
        return password;
    }


    //Path.of(System.getProperty("user.dir"), "database-configuration.properties");

    @Override
    protected Task<Boolean> createTask() {
        return new Task<>() {
            @Override
            protected Boolean call() {
                return DatabaseConnection.getInstance().connect(getIdentifier(), getPassword());
            }
        };
    }
}
