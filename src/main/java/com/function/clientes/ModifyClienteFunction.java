package com.function.clientes;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.OutputBinding;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.CosmosDBInput;
import com.microsoft.azure.functions.annotation.CosmosDBOutput;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

public class ModifyClienteFunction {

    @FunctionName("ModifyCliente")
    public HttpResponseMessage run(
        @HttpTrigger(
                name = "req",
                methods = {HttpMethod.POST},
                authLevel = AuthorizationLevel.ANONYMOUS)
                HttpRequestMessage<Cliente> request,
            @CosmosDBInput(name = "carroInput", 
                databaseName = "ProdigyDatabase",
                containerName = "Cliente",
                partitionKey = "{id}", id = "{id}", 
                connection = "CosmosDBConnection") Cliente existingCarro,
            @CosmosDBOutput(name = "carroOutput", 
                databaseName = "ProdigyDatabase",
                containerName = "Cliente",
                connection = "CosmosDBConnection") OutputBinding<Cliente> modifiedCarro,
        final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        if (request.getBody().getId() == null || request.getBody().getNome() == null || request.getBody().getTelefone() == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Invalid request body.").build();
        }

        if (existingCarro == null) {
            return request.createResponseBuilder(HttpStatus.NOT_FOUND).body("Carro not found.").build();
        }
            
        Cliente modifiedCarroData = request.getBody();

        // Update the properties of the existing Carro document
        existingCarro.setTelefone(modifiedCarroData.getTelefone());
        existingCarro.setNome(modifiedCarroData.getNome());

        modifiedCarro.setValue(existingCarro);

        return request.createResponseBuilder(HttpStatus.OK).body("Cliente modified successfully.").build();
        }
    
}
