package fr.sos.projetmines.calculator.rpc;

import fr.sos.projetmines.*;
import fr.sos.projetmines.calculator.OrowanCalculator;
import fr.sos.projetmines.calculator.database.CalculatorDatabaseFacade;
import fr.sos.projetmines.calculator.util.DataFormatter;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InputRangeEditorService extends InputRangeEditorGrpc.InputRangeEditorImplBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrowanLiveDataService.class);

    private final DataFormatter dataFormatter = OrowanCalculator.getInstance().getDataFormatter();

    /**
     * Updates the input range of the specified constraint with the given minimum and maximum values.
     * Responds with an {@link OrowanOperationResult} indicating whether the operation was successful or not.
     *
     * @param request         The request containing the constraint name, minimum value, and maximum value.
     * @param responseObserver The observer to send the response to.
     */
    @Override
    public void editInputRange(InputRangeEditRequest request, StreamObserver<OrowanOperationResult> responseObserver) {
        dataFormatter.updateDataRange(request.getConstraintName(), request.getMinValue(), request.getMaxValue());
        OrowanOperationResult result = OrowanOperationResult.newBuilder().setResult(OperationResult.SUCCESSFUL).build();
        responseObserver.onNext(result);
    }
}
