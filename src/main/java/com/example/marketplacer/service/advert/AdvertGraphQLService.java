package com.example.marketplacer.service.advert;

import com.example.marketplacer.config.MarketplacerConfig;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class AdvertGraphQLService {

  private final WebClient webClient;

  public AdvertGraphQLService(MarketplacerConfig config) {
    this.webClient =
        WebClient.builder()
            .baseUrl(config.getUrl())
            .defaultHeader("MARKETPLACER-API-KEY", config.getKey())
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
  }

  public String createAdvert(Map<String, Object> input) {
    String query =
        "mutation AdvertCreate($input: AdvertUpsertMutationInput!) {\n"
            + "  advertUpsert(input: $input) {\n"
            + "    status\n"
            + "    advert {\n"
            + "      id\n"
            + "      legacyId\n"
            + "      published\n"
            + "      displayable\n"
            + "      vetted\n"
            + "      variants {\n"
            + "        edges {\n"
            + "          node {\n"
            + "            id\n"
            + "            buyable\n"
            + "            displayable\n"
            + "          }\n"
            + "        }\n"
            + "      }\n"
            + "    }\n"
            + "    errors {\n"
            + "      field\n"
            + "      messages\n"
            + "    }\n"
            + "  }\n"
            + "}";

    Map<String, Object> variables = new HashMap<>();
    variables.put("input", input);

    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("query", query);
    requestBody.put("operationName", "AdvertCreate");
    requestBody.put("variables", variables);

    return webClient.post().bodyValue(requestBody).retrieve().bodyToMono(String.class).block();
  }

  public String updateAdvertPublishStatus(Map<String, Object> input) {
    String query =
        "mutation AdvertPublish($input: AdvertPublishedUpdateMutationInput!) {\n"
            + "  advertPublishedUpdate(input: $input) {\n"
            + "    status\n"
            + "    advert {\n"
            + "      id\n"
            + "      legacyId\n"
            + "      published\n"
            + "      displayable\n"
            + "      vetted\n"
            + "      variants {\n"
            + "        edges {\n"
            + "          node {\n"
            + "            id\n"
            + "            buyable\n"
            + "            displayable\n"
            + "          }\n"
            + "        }\n"
            + "      }\n"
            + "    }\n"
            + "    errors {\n"
            + "      field\n"
            + "      messages\n"
            + "    }\n"
            + "  }\n"
            + "}";

    Map<String, Object> variables = new HashMap<>();
    variables.put("input", input);

    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("query", query);
    requestBody.put("operationName", "AdvertPublish");
    requestBody.put("variables", variables);

    return webClient.post().bodyValue(requestBody).retrieve().bodyToMono(String.class).block();
  }

  public String getAdvertById(String id) {
    String query =
        """
                query AdvertWebhook($id: ID!) {
                  node(id: $id) {
                    ... on Advert {
                      __typename
                      id
                      legacyId
                      taxonNullable {
                        treeName
                      }
                      title
                      images {
                        edges {
                          node {
                            id
                            alt
                            url
                          }
                        }
                      }
                      displayable
                      published
                      vetted
                      requiresVetting
                      vettingRejected
                      vettingRejectedAt
                      vettingRejectedReason
                      online
                      description
                      shippingParcelToUse {
                        weight
                      }
                      advertOptionValues {
                        edges {
                          node {
                            id
                            optionType {
                              name
                            }
                            optionValue {
                              id
                              name
                            }
                            textValue
                          }
                        }
                      }
                      seller {
                        businessName
                      }
                      variants(displayableOnly: false) {
                        edges {
                          node {
                            id
                            barcode
                            sku
                            label
                            countOnHand
                            published
                            displayable
                            buyable
                            shippingParcel {
                              weightInKg
                              lengthInCm
                              depthInCm
                              widthInCm
                            }
                            inventories {
                              edges {
                                node {
                                  countOnHand
                                  fixedPricing
                                  id
                                  infiniteQuantity
                                  price {
                                    amount
                                  }
                                  salePrice {
                                    amount
                                  }
                                  seller {
                                    businessName
                                  }
                                }
                              }
                            }
                            goldenProductVariant {
                              id
                            }
                            externalIds {
                              key
                              value
                            }
                            variantOptionValues {
                              edges {
                                node {
                                  id
                                  optionType {
                                    name
                                  }
                                  optionValue {
                                    id
                                    name
                                  }
                                  textValue
                                }
                              }
                            }
                            shippingParcelToUse {
                              weight
                            }
                          }
                        }
                      }
                    }
                  }
                }
                """;

    Map<String, Object> variables = new HashMap<>();
    variables.put("id", id);

    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("query", query);
    requestBody.put("operationName", "AdvertWebhook");
    requestBody.put("variables", variables);

    return webClient.post().bodyValue(requestBody).retrieve().bodyToMono(String.class).block();
  }
}
