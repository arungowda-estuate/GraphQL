package com.example.marketplacer.service.seller;

import com.example.marketplacer.config.MarketplacerConfig;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class SellerGraphQLService {

  private final WebClient webClient;


  public SellerGraphQLService(MarketplacerConfig config) {
    this.webClient =
        WebClient.builder()
            .baseUrl(config.getUrl())
            .defaultHeader("MARKETPLACER-API-KEY", config.getKey())
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
  }

  public String fetchAllSellers() {
    String query =
        "query getAllActiveSellers(\n"
            + "  $pageSize: Int\n"
            + "  $endCursor: String\n"
            + "  $includeDeleted: Boolean\n"
            + "  $updatedSince: ISO8601DateTime\n"
            + ") {\n"
            + "  allSellers(\n"
            + "    first: $pageSize\n"
            + "    after: $endCursor\n"
            + "    includeDeleted: $includeDeleted\n"
            + "    updatedSince: $updatedSince\n"
            + "  ) {\n"
            + "    totalCount\n"
            + "    pageInfo {\n"
            + "      hasNextPage\n"
            + "      endCursor\n"
            + "    }\n"
            + "    nodes {\n"
            + "      ... on Seller {\n"
            + "        __typename\n"
            + "        id\n"
            + "        legacyId\n"
            + "        businessName\n"
            + "        online\n"
            + "        isRetailer\n"
            + "        updatedAt\n"
            + "        phone\n"
            + "        primaryUser {\n"
            + "          firstName\n"
            + "          surname\n"
            + "          emailAddress\n"
            + "        }\n"
            + "        users {\n"
            + "          nodes {\n"
            + "            firstName\n"
            + "            surname\n"
            + "            emailAddress\n"
            + "          }\n"
            + "        }\n"
            + "      }\n"
            + "      ... on DeletedSeller {\n"
            + "        __typename\n"
            + "        id\n"
            + "        legacyId\n"
            + "        updatedAt\n"
            + "      }\n"
            + "    }\n"
            + "  }\n"
            + "}";

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

  public String createSeller(Map<String, Object> input) {
    String mutation =
        "mutation SellerCreate($input: SellerCreateMutationInput!) {\n"
            + "  sellerCreate(input: $input) {\n"
            + "    seller {\n"
            + "      id\n"
            + "      metadata {\n"
            + "        key\n"
            + "        value\n"
            + "      }\n"
            + "    }\n"
            + "    errors {\n"
            + "      field\n"
            + "      messages\n"
            + "    }\n"
            + "    status\n"
            + "  }\n"
            + "}";

    Map<String, Object> variables = new HashMap<>();
    variables.put("input", input);

    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("query", mutation);
    requestBody.put("operationName", "SellerCreate");
    requestBody.put("variables", variables);

    return webClient.post().bodyValue(requestBody).retrieve().bodyToMono(String.class).block();
  }


  public String updateSeller(Map<String, Object> input) {
    String mutation =
            "mutation SellerUpdate($input: SellerUpdateMutationInput!) {\n" +
                    "  sellerUpdate(input: $input) {\n" +
                    "    seller {\n" +
                    "      id\n" +
                    "      metadata {\n" +
                    "        key\n" +
                    "        value\n" +
                    "      }\n" +
                    "    }\n" +
                    "    errors {\n" +
                    "      field\n" +
                    "      messages\n" +
                    "    }\n" +
                    "    status\n" +
                    "  }\n" +
                    "}";

    Map<String, Object> variables = new HashMap<>();
    variables.put("input", input);

    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("query", mutation);
    requestBody.put("operationName", "SellerUpdate");
    requestBody.put("variables", variables);

    return webClient.post().bodyValue(requestBody).retrieve().bodyToMono(String.class).block();
  }

  public String getSellerById(Map<String, Object> input) {
    String query = "query getSellersById($pageSize: Int, $endCursor: String, $retailerIds: [ID!]) {\n"
            + "  sellersWhere(first: $pageSize, after: $endCursor, retailerIds: $retailerIds) {\n"
            + "    totalCount\n"
            + "    pageInfo {\n"
            + "      hasNextPage\n"
            + "      endCursor\n"
            + "    }\n"
            + "    nodes {\n"
            + "      __typename\n"
            + "      phone\n"
            + "      id\n"
            + "      legacyId\n"
            + "      businessName\n"
            + "      online\n"
            + "      isRetailer\n"
            + "      updatedAt\n"
            + "      primaryUser {\n"
            + "        firstName\n"
            + "        surname\n"
            + "      }\n"
            + "    }\n"
            + "  }\n"
            + "}";

    Integer pageSize = (Integer) input.get("pageSize");
    String endCursor = (String) input.get("endCursor");
    @SuppressWarnings("unchecked")
    java.util.List<String> retailerIds = (java.util.List<String>) input.get("retailerIds");

    Map<String, Object> variables = new HashMap<>();
    variables.put("pageSize", pageSize);
    variables.put("endCursor", endCursor);
    variables.put("retailerIds", retailerIds);

    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("query", query);
    requestBody.put("operationName", "getSellersById");
    requestBody.put("variables", variables);

    return webClient.post()
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(String.class)
            .block();
  }




  public String searchSellerByBusinessName(Map<String, Object> input) {
    String query = "query SellerSearchByBusinessName($pageSize: Int, $endCursor: String, $attributes: SellerSearchInput) {\n"
            + "  sellerSearch(attributes: $attributes) {\n"
            + "    sellers(first: $pageSize, after: $endCursor) {\n"
            + "      edges {\n"
            + "        node {\n"
            + "          id\n"
            + "          businessName\n"
            + "        }\n"
            + "      }\n"
            + "      pageInfo {\n"
            + "        hasNextPage\n"
            + "        endCursor\n"
            + "      }\n"
            + "    }\n"
            + "  }\n"
            + "}";

    // Extract fields from input map
    Integer pageSize = (Integer) input.get("pageSize");
    String endCursor = (String) input.get("endCursor");
    @SuppressWarnings("unchecked")
    Map<String, Object> attributes = (Map<String, Object>) input.get("attributes");

    Map<String, Object> variables = new HashMap<>();
    variables.put("pageSize", pageSize);
    variables.put("endCursor", endCursor);
    variables.put("attributes", attributes);

    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("query", query);
    requestBody.put("operationName", "SellerSearchByBusinessName");
    requestBody.put("variables", variables);

    return webClient.post()
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(String.class)
            .block();
  }

  public String getSellerById(String id) {
    String query =
            "query SellerWebhook($id: ID!) {\n" +
                    "  node(id: $id) {\n" +
                    "    ... on Seller {\n" +
                    "      __typename\n" +
                    "      id\n" +
                    "      businessName\n" +
                    "      phone\n" +
                    "      metadata { key value }\n" +
                    "      externalIds { key value }\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";

    Map<String, Object> variables = new HashMap<>();
    variables.put("id", id);

    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("query", query);
    requestBody.put("operationName", "SellerWebhook");
    requestBody.put("variables", variables);

    return webClient.post()
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(String.class)
            .block();
  }

}
