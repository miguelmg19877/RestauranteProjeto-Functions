package com.function.mesas;


import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.time.LocalDate;

import com.function.reservas.Reservas;
import com.microsoft.azure.cosmosdb.Document;
import com.microsoft.azure.cosmosdb.FeedOptions;
import com.microsoft.azure.cosmosdb.FeedResponse;
import com.microsoft.azure.cosmosdb.PartitionKey;
import com.microsoft.azure.cosmosdb.RequestOptions;
import com.microsoft.azure.cosmosdb.ResourceResponse;
import com.microsoft.azure.cosmosdb.rx.AsyncDocumentClient;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;

public class DeleteMesaFunction {

    @FunctionName("DeleteMesa")
    public HttpResponseMessage run(
        @HttpTrigger(name = "req", methods = {HttpMethod.DELETE}, route = "mesa/delete/{id}", authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Mesa> request,
            @BindingName("id") String id,
        final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        try {
            AsyncDocumentClient asyncClient = new AsyncDocumentClient.Builder().withServiceEndpoint("https://prodigiodojavadeprog3account.documents.azure.com:443").withMasterKeyOrResourceToken("1yKn3H5GMTWxF48G7oygfHDetbzXrOhcL8lmpHewdxa4JQcacGoJBTq1XVtMgwsJn6ix4M31RgeRACDbUj0Dcw==").build();
            //query for a reserva that has the same mesaid and data
            String query = "SELECT * FROM c WHERE c.mesa = '" + id + "'";
            //query all container of reservas so if the mesaid and data already exists it will return an error
            FeedOptions queryOptions = new FeedOptions();
            queryOptions.setEnableCrossPartitionQuery(true);
            FeedResponse<Document> queryResults = asyncClient.queryDocuments("/dbs/ProdigyDatabase/colls/Reservas", query, queryOptions).toBlocking().single();
            if (queryResults.getResults().size() > 0) {
                //check query results and check if the data is after the current date, create a new instance of LocalDate based on YYYY-MM-DD which is on the data
                //if the date is after the current date, return a bad request
                for (Document doc : queryResults.getResults()) {
                    Reservas reserva = new Reservas(doc.getString("id"), doc.getString("mesaid"), doc.getString("clienteid"), doc.getInt("capacidade"), doc.getString("data"));
                    LocalDate date = LocalDate.parse(reserva.getData());
                    if (date.isAfter(LocalDate.now())) {
                        return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Mesa tem reservas futuras.").build();
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            AsyncDocumentClient asyncClient = new AsyncDocumentClient.Builder().withServiceEndpoint("https://prodigiodojavadeprog3account.documents.azure.com:443").withMasterKeyOrResourceToken("1yKn3H5GMTWxF48G7oygfHDetbzXrOhcL8lmpHewdxa4JQcacGoJBTq1XVtMgwsJn6ix4M31RgeRACDbUj0Dcw==").build();
            RequestOptions options = new RequestOptions();
            options.setPartitionKey(new PartitionKey(id));
            ResourceResponse<Document> response = asyncClient.deleteDocument("/dbs/ProdigyDatabase/colls/Mesas/docs/" + id, options).toBlocking().single();
            context.getLogger().info("null: " + response);
            asyncClient.close();
            return request.createResponseBuilder(HttpStatus.OK).body("Mesa eliminado com sucesso").build();
        } catch (Exception e) {
            return request.createResponseBuilder(HttpStatus.NOT_FOUND).body("Mesa não encontrado.").build();
        }

        
    }

}
