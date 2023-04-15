package fr.sos.projetmines.calculator.rpc;

import fr.sos.projetmines.*;
import fr.sos.projetmines.calculator.util.CalculatorDatabaseConnection;
import fr.sos.projetmines.calculator.util.DataFormatter;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Optional;

public class OrowanAuthenticatorService extends OrowanAuthenticatorGrpc.OrowanAuthenticatorImplBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrowanAuthenticatorService.class);

    @Override
    public void authenticateUser(OrowanUserCredentials request, StreamObserver<OrowanConnectionResult> responseObserver) {
        String providedUsername = request.getUsername();
        String providedPassword = request.getPassword();

        /*byte[] salt = generateSalt();
        byte[] password = hashPassword(providedPassword, salt);
        CalculatorDatabaseConnection.getInstance().addUser(providedUsername, password, salt, Job.PROCESS_ENGINEER);*/

        byte[][] passAndSalt = CalculatorDatabaseConnection.getInstance().getPasswordAndSalt(providedUsername);
        if (passAndSalt.length != 0) {
            byte[] passwordHash = passAndSalt[0];
            byte[] salt = passAndSalt[1];
            byte[] testedHash = DataFormatter.hashPassword(providedPassword, salt);
            if (Arrays.equals(testedHash, passwordHash)) {
                OrowanConnectionResult result = OrowanConnectionResult.newBuilder()
                        .setResult(ConnectionResult.LOGIN_SUCCESSFUL)
                        .setUserJob(CalculatorDatabaseConnection.getInstance().getJob(providedUsername).orElse(Job.WORKER))
                        .build();
                responseObserver.onNext(result);
                responseObserver.onCompleted();
                return;
            }
        }
        OrowanConnectionResult result = OrowanConnectionResult.newBuilder()
                .setResult(ConnectionResult.WRONG_CREDENTIALS)
                .setUserJob(Job.WORKER)
                .build();
        responseObserver.onNext(result);
        responseObserver.onCompleted();
    }


}

