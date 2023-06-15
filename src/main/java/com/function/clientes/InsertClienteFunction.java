package com.function.clientes;

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

public class InsertClienteFunction {

    @FunctionName("InsertClienteFunction")
    public HttpResponseMessage run(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.POST},
                authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Cliente> request,
            @CosmosDBOutput(
                name = "carroOutput",
                databaseName = "ProdigyDatabase",
                containerName = "Cliente",
                connection = "CosmosDBConnection")
            OutputBinding<Cliente> outputItem,
            final ExecutionContext context) {
            context.getLogger().info("Java HTTP trigger processed a request.");
            
            // Obter o objeto Carro do corpo da requisição
            Cliente cliente = request.getBody();

            // Validação básica
            if (cliente.getId() == null || cliente.getNome() == null || cliente.getTelefone() == null) {
                return request.createResponseBuilder(HttpStatus.I_AM_A_TEAPOT).body("Por favor, preencha todos os campos do carro.").build();
            } else {
                // Inserir o objeto Carro no CosmosDB
                
                outputItem.setValue(cliente);
            
                // Responder que a inserção foi bem-sucedida
                return request.createResponseBuilder(HttpStatus.OK).body("Cliente inserido com sucesso.").build();
            }
    }
    
}
