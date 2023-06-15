package com.function.reservas;

import java.util.List;
import java.util.Optional;

import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;

public class GetAllReservasFunction {

    @FunctionName("GetAllReservas")
    public HttpResponseMessage run(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET},
                authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            @CosmosDBInput(
                name = "reservas",
                databaseName = "ProdigyDatabase",
                containerName = "Reservas",
                connection = "CosmosDBConnection",
                sqlQuery = "SELECT * FROM c")
            List<Reservas> reserva,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        if (reserva == null || reserva.size() == 0) {
            return request.createResponseBuilder(HttpStatus.NOT_FOUND).build();
        }
        return request.createResponseBuilder(HttpStatus.OK).body(reserva).build();
    }
    
}

