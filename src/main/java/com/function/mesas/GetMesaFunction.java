package com.function.mesas;

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

public class GetMesaFunction {

    @FunctionName("GetMesa")
    public HttpResponseMessage run(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET},
                authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            @CosmosDBInput(
                name = "mesa",
                databaseName = "ProdigyDatabase",
                containerName = "Mesas",
                id = "{Query.id}",
                partitionKey = "{Query.id}",
                connection = "CosmosDBConnection")
            Mesa mesa,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        Gson gson = new Gson();
        String json = gson.toJson(mesa);

        if (mesa == null) {
            return request.createResponseBuilder(HttpStatus.NOT_FOUND).body("Mesa não encontrada.").build();
        } else {
            return request.createResponseBuilder(HttpStatus.OK).body(json).build();
        }
    }
    
}
