package com.function.clientes;


import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import com.microsoft.azure.cosmosdb.Document;
import com.microsoft.azure.cosmosdb.FeedOptions;
import com.microsoft.azure.cosmosdb.FeedResponse;
import com.microsoft.azure.cosmosdb.PartitionKey;
import com.microsoft.azure.cosmosdb.RequestOptions;
import com.microsoft.azure.cosmosdb.ResourceResponse;
import com.microsoft.azure.cosmosdb.rx.AsyncDocumentClient;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;

public class DeleteClienteFunction {

    @FunctionName("DeleteCliente")
    public HttpResponseMessage run(
        @HttpTrigger(name = "req", methods = {HttpMethod.DELETE}, route = "cliente/delete/{id}", authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Cliente> request,
            @BindingName("id") String id,
        final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        try {
            AsyncDocumentClient asyncClient = new AsyncDocumentClient.Builder().withServiceEndpoint("https://prodigiodojavadeprog3account.documents.azure.com:443").withMasterKeyOrResourceToken("1yKn3H5GMTWxF48G7oygfHDetbzXrOhcL8lmpHewdxa4JQcacGoJBTq1XVtMgwsJn6ix4M31RgeRACDbUj0Dcw==").build();
            //query for a reserva that has the same mesaid and data
            String query = "SELECT * FROM c WHERE c.clienteid = '" + id + "'";
            //query all container of reservas so if the mesaid and data already exists it will return an error
            FeedOptions queryOptions = new FeedOptions();
            queryOptions.setEnableCrossPartitionQuery(true);
            FeedResponse<Document> queryResults = asyncClient.queryDocuments("/dbs/ProdigyDatabase/colls/Reservas", query, queryOptions).toBlocking().single();
            if (queryResults.getResults().size() > 0) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Cliente tem reservas no sistema.").build();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            AsyncDocumentClient asyncClient = new AsyncDocumentClient.Builder().withServiceEndpoint("https://prodigiodojavadeprog3account.documents.azure.com:443").withMasterKeyOrResourceToken("1yKn3H5GMTWxF48G7oygfHDetbzXrOhcL8lmpHewdxa4JQcacGoJBTq1XVtMgwsJn6ix4M31RgeRACDbUj0Dcw==").build();
            RequestOptions options = new RequestOptions();
            options.setPartitionKey(new PartitionKey(id));
            ResourceResponse<Document> response = asyncClient.deleteDocument("/dbs/ProdigyDatabase/colls/Cliente/docs/" + id, options).toBlocking().single();
            context.getLogger().info("null: " + response);
            asyncClient.close();
            return request.createResponseBuilder(HttpStatus.OK).body("Cliente eliminado com sucesso").build();
        } catch (Exception e) {
            return request.createResponseBuilder(HttpStatus.NOT_FOUND).body("Cliente não encontrado.").build();
        }

        
    }

}
