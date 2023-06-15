package com.function.mesas;

import java.util.List;
import java.util.Optional;

import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;

public class GetAllMesasFunction {

    @FunctionName("GetAllMesas")
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
                connection = "CosmosDBConnection",
                sqlQuery = "SELECT * FROM c")
            List<Mesa> mesa,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        if (mesa == null || mesa.size() == 0) {
            return request.createResponseBuilder(HttpStatus.NOT_FOUND).build();
        }
        return request.createResponseBuilder(HttpStatus.OK).body(mesa).build();
    }
    
}
