package fr.sos.projetmines.databasenotifier;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class DataUpdateHolder {
    private static final DataUpdateHolder INSTANCE = new DataUpdateHolder();
    private final PropertyChangeSupport support;

    public static DataUpdateHolder getInstance(){
        return INSTANCE;
    }

    private DataUpdateHolder(){
        this.support = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl){
        this.support.addPropertyChangeListener(pcl);
    }

    public void onNewUpdate(){
        this.support.firePropertyChange("row_inserted", 0, 0);
    }
}
