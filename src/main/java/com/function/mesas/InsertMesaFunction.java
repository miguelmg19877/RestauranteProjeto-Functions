package com.function.mesas;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.OutputBinding;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.microsoft.azure.functions.annotation.CosmosDBOutput;

public class InsertMesaFunction {

    @FunctionName("InsertMesa")
    public HttpResponseMessage run(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.POST},
                authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Mesa> request,
            @CosmosDBOutput(
                name = "mesas",
                databaseName = "ProdigyDatabase",
                containerName = "Mesas",
                connection = "CosmosDBConnection")
            OutputBinding<Mesa> outputItem,
            final ExecutionContext context) {
            context.getLogger().info("Java HTTP trigger processed a request.");
            
            // Obter o objeto Carro do corpo da requisição
            Mesa mesa = request.getBody();

            // Validação básica
            if (mesa.getId() == null || mesa.getCapacidade() == null) {
                return request.createResponseBuilder(HttpStatus.I_AM_A_TEAPOT).body("Por favor, preencha todos os campos do carro.").build();
            } else {
                // Inserir o objeto Carro no CosmosDB
                
                outputItem.setValue(mesa);
            
                // Responder que a inserção foi bem-sucedida
                return request.createResponseBuilder(HttpStatus.OK).body("Mesa inserido com sucesso.").build();
            }
    }
    
}
