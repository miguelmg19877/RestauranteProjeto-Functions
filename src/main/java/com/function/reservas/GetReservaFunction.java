package com.function.reservas;

import java.util.Optional;

import com.google.gson.Gson;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.CosmosDBInput;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

public class GetReservaFunction {

    @FunctionName("GetReserva")
    public HttpResponseMessage run(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET},
                authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            @CosmosDBInput(
                name = "reserva",
                databaseName = "ProdigyDatabase",
                containerName = "Reservas",
                id = "{Query.id}",
                partitionKey = "{Query.id}",
                connection = "CosmosDBConnection")
            Reservas reserva,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        Gson gson = new Gson();
        String json = gson.toJson(reserva);

        if (reserva == null) {
            return request.createResponseBuilder(HttpStatus.NOT_FOUND).body("Reserva não encontrada.").build();
        } else {
            return request.createResponseBuilder(HttpStatus.OK).body(json).build();
        }
    }
    
}
