package com.example.marketplacer.service.h2database;


import com.example.marketplacer.config.MarketplacerConfig;
import com.example.marketplacer.model.Seller;
import com.example.marketplacer.repository.SellerRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.*;

@Service
public class SellerFetchService {
    @Autowired
    private SellerRepository sellerRepository;

    private final WebClient webClient;

    public SellerFetchService(MarketplacerConfig config) {
        this.webClient =
                WebClient.builder()
                        .baseUrl(config.getUrl())
                        .defaultHeader("MARKETPLACER-API-KEY", config.getKey())
                        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .build();
    }

    // Your existing fetchAllSellers() method here
    public String fetchAllSellers() {
        String query =
                """
                        query getAllActiveSellers($pageSize: Int, $endCursor: String, $includeDeleted: Boolean, $updatedSince: ISO8601DateTime) {
                          allSellers(
                            first: $pageSize
                            after: $endCursor
                            includeDeleted: $includeDeleted
                            updatedSince: $updatedSince
                          ) {
                            nodes {
                              ... on Seller {
                                id
                                emailAddress
                              }
                            }
                          }
                        }
                        """;

        Map<String, Object> variables = new HashMap<>();
        variables.put("pageSize", 100);
        variables.put("endCursor", null);
        variables.put("includeDeleted", false);
        variables.put("updatedSince", "2024-07-16T10:10:53+10:00");

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", query);
        requestBody.put("operationName", "getAllActiveSellers");
        requestBody.put("variables", variables);

        return webClient.post().bodyValue(requestBody).retrieve().bodyToMono(String.class).block();
    }

    // New method to parse JSON and save sellers to H2
    public void fetchParseAndSaveSellers() {
        String jsonResponse = fetchAllSellers();
        System.out.println("Received JSON response:\n" + jsonResponse);

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);

            // Check for GraphQL errors
            if (root.has("errors")) {
                System.err.println("GraphQL errors: " + root.get("errors").toPrettyString());
                return;
            }

            JsonNode sellerNodes = root.path("data").path("allSellers").path("nodes");

            if (!sellerNodes.isArray()) {
                System.out.println("No seller nodes found.");
                return;
            }

            for (JsonNode sellerNode : sellerNodes) {
                String id = sellerNode.path("id").asText(null);
                String email = sellerNode.path("emailAddress").asText(null);

                if (id != null && !id.isEmpty() && email != null && !email.isEmpty()) {
                    Seller seller =
                            new Seller(id, email); // Using id as name for now (since name not present in query)
                    sellerRepository.save(seller);
                    System.out.printf("Saved seller: id=%s, email=%s%n", id, email);
                } else {
                    System.out.printf("Skipping seller: missing id or email (id=%s, email=%s)%n", id, email);
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to parse JSON response:");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error during seller processing:");
            e.printStackTrace();
        }
    }
}