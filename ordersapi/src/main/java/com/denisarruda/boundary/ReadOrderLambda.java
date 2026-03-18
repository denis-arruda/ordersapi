package com.denisarruda.boundary;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.denisarruda.entity.Order;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.Item;
import java.util.ArrayList;
import java.util.List;

public class ReadOrderLambda {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
    private static final DynamoDB dynamoDB = new DynamoDB(client);

    public APIGatewayProxyResponseEvent readOrder(APIGatewayProxyRequestEvent request) throws JsonMappingException, JsonProcessingException {
        Table table = dynamoDB.getTable(System.getenv("ORDERS_TABLE"));
        ScanSpec scanSpec = new ScanSpec();
        ItemCollection<ScanOutcome> items = table.scan(scanSpec);
        List<Order> orders = new ArrayList<>();
        for (Item item : items) {
            orders.add(new Order(
                item.getInt("id"),
                item.getString("itemName"),
                item.getInt("quantity")
            ));
        }
        String ordersJson = objectMapper.writeValueAsString(orders);
        return new APIGatewayProxyResponseEvent()
            .withStatusCode(200)
            .withBody(ordersJson);
    }
}
