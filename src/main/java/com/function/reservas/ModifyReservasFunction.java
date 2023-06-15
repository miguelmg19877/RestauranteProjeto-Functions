package com.function.reservas;

import com.microsoft.azure.cosmosdb.Document;
import com.microsoft.azure.cosmosdb.FeedOptions;
import com.microsoft.azure.cosmosdb.FeedResponse;
import com.microsoft.azure.cosmosdb.rx.AsyncDocumentClient;
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

public class ModifyReservasFunction {

    @FunctionName("ModifyReservas")
    public HttpResponseMessage run(
        @HttpTrigger(
                name = "req",
                methods = {HttpMethod.POST},
                authLevel = AuthorizationLevel.ANONYMOUS)
                HttpRequestMessage<Reservas> request,
            @CosmosDBInput(name = "reservaInput", 
                databaseName = "ProdigyDatabase",
                containerName = "Reservas",
                partitionKey = "{id}", id = "{id}", 
                connection = "CosmosDBConnection") Reservas existingReservas,
            @CosmosDBOutput(name = "reservaOutput", 
                databaseName = "ProdigyDatabase",
                containerName = "Reservas",
                connection = "CosmosDBConnection") OutputBinding<Reservas> modifiedReservas,
        final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        if (request.getBody().getId() == null || request.getBody().getCapacidade() == null || request.getBody().getClienteid() == null || request.getBody().getMesaid() == null || request.getBody().getData() == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Invalid request body.").build();
        }

        if (existingReservas == null) {
            return request.createResponseBuilder(HttpStatus.NOT_FOUND).body("Carro not found.").build();
        }

        try {
            AsyncDocumentClient asyncClient = new AsyncDocumentClient.Builder().withServiceEndpoint("https://prodigiodojavadeprog3account.documents.azure.com:443").withMasterKeyOrResourceToken("1yKn3H5GMTWxF48G7oygfHDetbzXrOhcL8lmpHewdxa4JQcacGoJBTq1XVtMgwsJn6ix4M31RgeRACDbUj0Dcw==").build();
            //query for a reserva that has the same mesaid and data
            String query = "SELECT * FROM c WHERE c.mesaid = '" + request.getBody().getMesaid() + "' AND c.data = '" + request.getBody().getData() + "' AND c.id != '" + request.getBody().getId() + "' AND c.clienteid != '" + request.getBody().getClienteid() + "'" ;
            //query all container of reservas so if the mesaid and data already exists it will return an error
            FeedOptions queryOptions = new FeedOptions();
            queryOptions.setEnableCrossPartitionQuery(true);
            FeedResponse<Document> queryResults = asyncClient.queryDocuments("/dbs/ProdigyDatabase/colls/Reservas", query, queryOptions).toBlocking().single();
            if (queryResults.getResults().size() > 0) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Reserva já existe.").build();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
            
        Reservas modifiedReservaData = request.getBody();

        existingReservas.setCapacidade(modifiedReservaData.getCapacidade());
        existingReservas.setClienteid(modifiedReservaData.getClienteid());
        existingReservas.setData(modifiedReservaData.getData());
        existingReservas.setMesaid(modifiedReservaData.getMesaid());

        modifiedReservas.setValue(existingReservas);
        

        return request.createResponseBuilder(HttpStatus.OK).body("Carro modified successfully.").build();
        }
    
}
