package fr.sos.projetmines.model;

import java.util.UUID;

public class OrowanUser {

    private final UUID uuid;
    private String username;
    private String displayName;
    private OrowanJob job;


    public OrowanUser(String username, String displayName, UUID uuid, OrowanJob orowanJob) {
        this.username = username;
        this.displayName = displayName;
        this.uuid = uuid;
        this.job = orowanJob;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public UUID getUuid() {
        return uuid;
    }

    public OrowanJob getJob() {
        return job;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setJob(OrowanJob job) {
        this.job = job;
    }
}
