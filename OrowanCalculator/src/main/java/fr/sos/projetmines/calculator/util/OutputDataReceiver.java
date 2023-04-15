package fr.sos.projetmines.calculator.util;

import fr.sos.projetmines.calculator.model.OrowanDataOutput;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class OutputDataReceiver {

    private static final OutputDataReceiver INSTANCE = new OutputDataReceiver();

    public static OutputDataReceiver getInstance(){
        return INSTANCE;
    }

    private final PropertyChangeSupport support;
    private OrowanDataOutput levelTwoData;

    private OutputDataReceiver(){
        this.support = new PropertyChangeSupport(this);
    }


    public void receiveLevelTwoData(OrowanDataOutput dataOutput, int standId){
        support.fireIndexedPropertyChange("newComputationResult", standId, levelTwoData, dataOutput);
        levelTwoData = dataOutput;
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }
}
