package fr.sos.projetmines.calculator.rpc;

import fr.sos.projetmines.*;
import fr.sos.projetmines.calculator.OrowanCalculator;
import fr.sos.projetmines.calculator.database.CalculatorDatabaseFacade;
import fr.sos.projetmines.calculator.util.DataFormatter;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class OrowanAuthenticatorService extends OrowanAuthenticatorGrpc.OrowanAuthenticatorImplBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrowanAuthenticatorService.class);

    @Override
    public void authenticateUser(OrowanUserCredentials request, StreamObserver<OrowanConnectionResult> responseObserver) {
        LOGGER.debug("Received an authentication request.");
        CalculatorDatabaseFacade database = OrowanCalculator.getInstance().getDatabase();
        byte[][] passAndSalt = database.getPasswordAndSalt(request.getUsername());

        if (passAndSalt.length != 0) {
            byte[] passwordHash = passAndSalt[0];
            byte[] salt = passAndSalt[1];
            byte[] testedHash = DataFormatter.hashPassword(request.getPassword(), salt);


            if (Arrays.equals(testedHash, passwordHash)) {
                OrowanConnectionResult result = OrowanConnectionResult.newBuilder()
                        .setResult(OperationResult.SUCCESSFUL)
                        .setUserJob(database.getUserJob(request.getUsername()).orElse(Job.WORKER))
                        .build();
                responseObserver.onNext(result);
                responseObserver.onCompleted();
                LOGGER.info("User {} successfully connected", request.getUsername());
                return;
            }
        }
        OrowanConnectionResult result = OrowanConnectionResult.newBuilder()
                .setResult(OperationResult.FAIL)
                .setUserJob(Job.WORKER)
                .build();
        responseObserver.onNext(result);
        responseObserver.onCompleted();
        LOGGER.debug("User {} was unable to authenticate.", request.getUsername());
    }


}

