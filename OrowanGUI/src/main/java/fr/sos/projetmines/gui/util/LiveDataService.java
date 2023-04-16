package fr.sos.projetmines.gui.util;

import fr.sos.projetmines.gui.rpc.OrowanLivedataClient;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LiveDataService extends Service {

    private static final Logger LOGGER = LoggerFactory.getLogger(LiveDataService.class);
    private final StringProperty host = new SimpleStringProperty("localhost");

    public String getHost() {
        return host.getValue();
    }

    public void setHost(String newHost) {
        this.host.set(newHost);
    }

    public StringProperty host() {
        return host;
    }
    private final IntegerProperty port = new SimpleIntegerProperty(32765);

    public int getPort() {
        return port.getValue();
    }

    public void setPort(int newPort) {
        this.port.set(newPort);
    }

    public IntegerProperty port() {
        return port;
    }

    @Override
    protected Task createTask() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                ManagedChannel channel = Grpc.newChannelBuilderForAddress(getHost(),
                        getPort(), InsecureChannelCredentials.create()).build();
                OrowanLivedataClient client = new OrowanLivedataClient(channel);
                client.startReceivingValues();
                return null;
            }
        };
    }
}
