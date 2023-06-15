package com.function.clientes;

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

public class GetClienteFunction {

    @FunctionName("GetClienteFunction")
    public HttpResponseMessage run(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET},
                authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            @CosmosDBInput(
                name = "cliente",
                databaseName = "ProdigyDatabase",
                containerName = "Cliente",
                id = "{Query.id}",
                partitionKey = "{Query.id}",
                connection = "CosmosDBConnection")
            Cliente cliente,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        Gson gson = new Gson();
        String json = gson.toJson(cliente);

        if (cliente == null) {
            return request.createResponseBuilder(HttpStatus.NOT_FOUND).body("Cliente não encontrado.").build();
        } else {
            return request.createResponseBuilder(HttpStatus.OK).body(json).build();
        }
    }
    
}
