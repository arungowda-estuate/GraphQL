package com.example.marketplacer.controller;

import com.example.marketplacer.service.advert.AdvertGraphQLService;
import com.example.marketplacer.service.seller.SellerGraphQLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class Advertcontroller {


    @Autowired
    private final AdvertGraphQLService advertGraphQLService;

    public Advertcontroller(AdvertGraphQLService advertGraphQLService) {
        this.advertGraphQLService = advertGraphQLService;
    }


    @PostMapping("/createAdvert")
    public ResponseEntity<String> createAdvert(@RequestBody Map<String, Object> input) {
        String response = advertGraphQLService.createAdvert(input);
        return ResponseEntity.ok(response);
    }


    //optional
    @PostMapping("/adverts/publish")
    public ResponseEntity<String> updateAdvertPublishStatus(@RequestBody Map<String, Object> input) {
        String response = advertGraphQLService.updateAdvertPublishStatus(input);
        return ResponseEntity.ok(response);
    }


    //optional
    @PostMapping("/advert/webhook")
    public ResponseEntity<String> getAdvertWebhook(@RequestBody Map<String, Object> requestBody) {
        String id = (String) requestBody.get("id");
        String response = advertGraphQLService.getAdvertById(id);
        return ResponseEntity.ok(response);
    }
}
