package com.example.marketplacer.service.seller;

import com.example.marketplacer.config.MarketplacerConfig;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.marketplacer.model.Seller;
import com.example.marketplacer.repository.SellerRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class SellerGraphQLService {

  private final WebClient webClient;

  @Autowired
  private SellerRepository sellerRepository;

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
    String createQuery =
            """
        mutation SellerCreate($input: SellerCreateMutationInput!) {
          sellerCreate(input: $input) {
            seller {
              id
              primaryUser {
                emailAddress
              }
            }
            errors {
              field
              messages
            }
            status
          }
        }
        """;

    Map<String, Object> variables = new HashMap<>();
    variables.put("input", input);

    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("query", createQuery);
    requestBody.put("operationName", "SellerCreate");
    requestBody.put("variables", variables);

    String response =
            webClient.post().bodyValue(requestBody).retrieve().bodyToMono(String.class).block();

    try {
      ObjectMapper mapper = new ObjectMapper();
      JsonNode root = mapper.readTree(response);
      JsonNode sellerNode = root.path("data").path("sellerCreate").path("seller");

      if (!sellerNode.isMissingNode()) {
        String id = sellerNode.get("id").asText();
        String email = sellerNode.path("primaryUser").path("emailAddress").asText();

        Seller info = new Seller();
        info.setId(id);
        info.setEmail(email);

        sellerRepository.save(info);
      }
    } catch (Exception e) {
      e.printStackTrace(); // or use a logger
    }

    return response;
  }


  public String updateSeller(Map<String, Object> input) {
    String mutation =
            """
	mutation SellerUpdate($input: SellerUpdateMutationInput!) {
	sellerUpdate(input: $input) {
		seller {
			id	
			businessName
			metadata
			{
				key
				value
			}
		}
		errors {
			field
			messages
		}
		status
	}
}
       """
;
    Map<String, Object> variables = new HashMap<>();
    variables.put("input", input);

    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("query", mutation);
    requestBody.put("operationName", "SellerUpdate");
    requestBody.put("variables", variables);

    return webClient.post().bodyValue(requestBody).retrieve().bodyToMono(String.class).block();
  }

  public String getSellerById(String id) {
    String query =
            """
            query getSellersById($pageSize: Int, $endCursor: String, $retailerIds: [ID!]) {
              sellersWhere(first: $pageSize, after: $endCursor, retailerIds: $retailerIds) {
                totalCount
                pageInfo {
                  hasNextPage
                  endCursor
                }
                nodes {
                  __typename
                  phone
                  id
                  address {
                    address
                    country {
                      name
                    }
                    state {
                      name
                    }
                    city
                    postcode
                  }
                  users {
                    nodes {
                      firstName
                      surname
                      emailAddress
                    }
                  }
                  metadata {
                    key
                    value
                  }
                }
              }
            }
            """;

    Map<String, Object> variables = new HashMap<>();
    variables.put("pageSize", 10); // Since only one ID is passed
    variables.put("endCursor", null);
    variables.put("retailerIds", List.of(id));

    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("query", query);
    requestBody.put("operationName", "getSellersById");
    requestBody.put("variables", variables);

    return webClient.post().bodyValue(requestBody).retrieve().bodyToMono(String.class).block();
  }




  public String searchSellerByBusinessName(Map<String, Object> input) {
    String query =
            """
                    query getSellersById($pageSize: Int, $endCursor: String, $retailerIds: [ID!]) {
                      sellersWhere(first: $pageSize, after: $endCursor, retailerIds: $retailerIds) {
                        totalCount
                        pageInfo {
                          hasNextPage
                          endCursor
                        }
                        nodes {
                          __typename
                          phone
                          id
                          address {
                            address
                            country {
                              name
                            }
                            state {
                              name
                            }
                            city
                            postcode
                          }
                          users {
                            nodes {
                              firstName
                              surname
                              emailAddress
                            }
                          }
                          metadata {
                            key
                            value
                          }
                        }
                      }
                    }
                    
            """;

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

//  public String getSellerById(String id) {
//    String query =
//            "query SellerWebhook($id: ID!) {\n" +
//                    "  node(id: $id) {\n" +
//                    "    ... on Seller {\n" +
//                    "      __typename\n" +
//                    "      id\n" +
//                    "      businessName\n" +
//                    "      phone\n" +
//                    "      metadata { key value }\n" +
//                    "      externalIds { key value }\n" +
//                    "    }\n" +
//                    "  }\n" +
//                    "}";
//
//    Map<String, Object> variables = new HashMap<>();
//    variables.put("id", id);
//
//    Map<String, Object> requestBody = new HashMap<>();
//    requestBody.put("query", query);
//    requestBody.put("operationName", "SellerWebhook");
//    requestBody.put("variables", variables);
//
//    return webClient.post()
//            .bodyValue(requestBody)
//            .retrieve()
//            .bodyToMono(String.class)
//            .block();
//  }

  //validation of users
  public String getMarkerPlacerIdForEmailId(String emailId) {
    return sellerRepository.findByEmail(emailId).map(Seller::getId).orElse("User not found");
  }

  public String getApprovedSellers(Map<String, Object> input) {
    String query =
            "query SellerSearchByBusinessName($pageSize: Int, $endCursor: String, $attributes: SellerSearchInput) {\n" +
                    "  sellerSearch(attributes: $attributes) {\n" +
                    "    sellers(first: $pageSize, after: $endCursor) {\n" +
                    "      edges {\n" +
                    "        node {\n" +
                    "          id\n" +
                    "          phone\n" +
                    "          users {\n" +
                    "            nodes {\n" +
                    "              firstName\n" +
                    "              surname\n" +
                    "              emailAddress\n" +
                    "            }\n" +
                    "          }\n" +
                    "          metadata {\n" +
                    "            key\n" +
                    "            value\n" +
                    "          }\n" +
                    "          address {\n" +
                    "            address\n" +
                    "            country {\n" +
                    "              name\n" +
                    "            }\n" +
                    "            state {\n" +
                    "              name\n" +
                    "            }\n" +
                    "            city\n" +
                    "            postcode\n" +
                    "          }\n" +
                    "        }\n" +
                    "      }\n" +
                    "      pageInfo {\n" +
                    "        hasNextPage\n" +
                    "        endCursor\n" +
                    "      }\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";

    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("query", query);
    requestBody.put("operationName", "SellerSearchByBusinessName");
    requestBody.put("variables", input);

    return webClient.post()
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(String.class)
            .block();
  }

}
