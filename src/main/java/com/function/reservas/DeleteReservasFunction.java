package com.function.reservas;

import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import com.microsoft.azure.cosmosdb.Document;
import com.microsoft.azure.cosmosdb.PartitionKey;
import com.microsoft.azure.cosmosdb.RequestOptions;
import com.microsoft.azure.cosmosdb.ResourceResponse;
import com.microsoft.azure.cosmosdb.rx.AsyncDocumentClient;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;

public class DeleteReservasFunction {

    @FunctionName("DeleteReservas")
    public HttpResponseMessage run(
        @HttpTrigger(name = "req", methods = {HttpMethod.DELETE}, route = "reservas/delete/{id}", authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Reservas> request,
            @BindingName("id") String id,
        final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");
        
        try {
            AsyncDocumentClient asyncClient = new AsyncDocumentClient.Builder().withServiceEndpoint("https://prodigiodojavadeprog3account.documents.azure.com:443").withMasterKeyOrResourceToken("1yKn3H5GMTWxF48G7oygfHDetbzXrOhcL8lmpHewdxa4JQcacGoJBTq1XVtMgwsJn6ix4M31RgeRACDbUj0Dcw==").build();
            RequestOptions options = new RequestOptions();
            options.setPartitionKey(new PartitionKey(id));
            ResourceResponse<Document> response = asyncClient.deleteDocument("/dbs/ProdigyDatabase/colls/Reservas/docs/" + id, options).toBlocking().single();
            context.getLogger().info("null: " + response);
            asyncClient.close();
            return request.createResponseBuilder(HttpStatus.OK).body("Reserva eliminado com sucesso").build();
        } catch (Exception e) {
            return request.createResponseBuilder(HttpStatus.NOT_FOUND).body("Carro não encontrado.").build();
        }

        
    }
    
}
