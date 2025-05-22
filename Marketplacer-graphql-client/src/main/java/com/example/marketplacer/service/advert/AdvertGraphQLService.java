package com.example.marketplacer.service.advert;

import com.example.marketplacer.config.MarketplacerConfig;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

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
        String query = "mutation AdvertCreate($input: AdvertUpsertMutationInput!) {\n"
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

        return webClient.post()
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }


}
