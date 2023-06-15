package com.function.reservas;

import java.time.LocalDate;

import com.function.clientes.Cliente;
import com.function.mesas.Mesa;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.OutputBinding;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.CosmosDBInput;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.microsoft.azure.functions.annotation.CosmosDBOutput;

public class InsertReservaFunction {

    @FunctionName("InsertReserva")
    public HttpResponseMessage run(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.POST},
                authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Reservas> request,
            @CosmosDBInput(
                name = "mesa",
                databaseName = "ProdigyDatabase",
                containerName = "Mesas",
                id = "{mesaid}",
                partitionKey = "{mesaid}",
                connection = "CosmosDBConnection") Mesa mesa,
            @CosmosDBInput(
                name = "cliente",
                databaseName = "ProdigyDatabase",
                containerName = "Cliente",
                id = "{clienteid}",
                partitionKey = "{clienteid}",
                connection = "CosmosDBConnection") Cliente cliente,
            @CosmosDBOutput(
                name = "carroOutput",
                databaseName = "ProdigyDatabase",
                containerName = "Reservas",
                connection = "CosmosDBConnection")
            
            OutputBinding<Reservas> outputItem,
            final ExecutionContext context) {
                if (mesa == null) {
                    return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Mesa não encontrada.").build();
                }
            context.getLogger().info("Java HTTP trigger processed a request.");
            // Converter o body da data, com o formato YYYY-MM-DD para LocalDate
            try {
                String[] data = request.getBody().getData().split("-");
                LocalDate date = LocalDate.of(Integer.parseInt(data[0]), Integer.parseInt(data[1]), Integer.parseInt(data[2]));
                if (date == null) {
                    return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Por favor, insira uma data válida.").build();
                }
            } catch (Exception e) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Por favor, insira uma data válida.").build();
            }
            
            // Obter o objeto Carro do corpo da requisição
            Reservas reserva = request.getBody();

            // Validação básica
            if (reserva.getId() == null || reserva.getCapacidade() == null || reserva.getClienteid() == null || reserva.getData() == null || reserva.getMesaid() == null) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Por favor, preencha todos os campos do carro.").build();
            } else {
                if (mesa.getCapacidade() < reserva.getCapacidade()) {
                    return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("A capacidade da reserva é maior que a capacidade da mesa.").build();
                }else {
                    // Inserir o objeto Carro no CosmosDB
                
                    outputItem.setValue(reserva);
                
                    // Responder que a inserção foi bem-sucedida
                    return request.createResponseBuilder(HttpStatus.OK).body("Reserva inserido com sucesso.").build();
                }

            }
    }
    
}
