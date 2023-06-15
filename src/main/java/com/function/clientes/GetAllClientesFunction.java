package com.function.clientes;

import java.util.List;
import java.util.Optional;

import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;

public class GetAllClientesFunction {

    @FunctionName("GetAllClientesFunction")
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
                connection = "CosmosDBConnection",
                sqlQuery = "SELECT * FROM c")
            List<Cliente> cliente,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        if (cliente == null || cliente.size() == 0) {
            return request.createResponseBuilder(HttpStatus.NOT_FOUND).build();
        }
        return request.createResponseBuilder(HttpStatus.OK).body(cliente).build();
    }
    
}
