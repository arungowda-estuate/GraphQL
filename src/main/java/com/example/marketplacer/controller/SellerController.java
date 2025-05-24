package com.example.marketplacer.controller;

import com.example.marketplacer.service.advert.AdvertGraphQLService;
import com.example.marketplacer.service.seller.SellerGraphQLService;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Slf4j
public class SellerController {

  private final SellerGraphQLService sellerGraphQLService;


  public SellerController(
      SellerGraphQLService sellerGraphQLService) {
    this.sellerGraphQLService = sellerGraphQLService;

  }

  @GetMapping("/getAllSellers")
  public ResponseEntity<String> getAllSellers() {
    String response = sellerGraphQLService.fetchAllSellers();
    return ResponseEntity.ok(response);
  }

  @PostMapping("/createSeller")
  public ResponseEntity<String> createSeller(@RequestBody Map<String, Object> input) {
    String response = sellerGraphQLService.createSeller(input);
    return ResponseEntity.ok(response);
  }

  @PutMapping("/updateSeller")
  public ResponseEntity<String> updateSeller(@RequestBody Map<String, Object> input) {
    String response = sellerGraphQLService.updateSeller(input);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/searchSellerByBusinessName")
  public ResponseEntity<String> searchSellerByBusinessName(@RequestBody Map<String, Object> input) {
    String response = sellerGraphQLService.searchSellerByBusinessName(input);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/getSellerById")
  public ResponseEntity<String> getSellerByRetailerId(@RequestParam String id) {
    String response = sellerGraphQLService.getSellerById(id);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/getSellerWebhook")
  public ResponseEntity<String> getSellerWebhook(@RequestBody Map<String, Object> requestBody) {
    String id = (String) requestBody.get("id");
    String response = sellerGraphQLService.getSellerById(id);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/login")
  public ResponseEntity<String> getSellerIdByEmail(@RequestParam String email) {
    String MarketPlacerID = sellerGraphQLService.getMarkerPlacerIdForEmailId(email);
    log.info(MarketPlacerID);
    return ResponseEntity.ok(MarketPlacerID);
  }

  @PostMapping("/sellersApproved")
  public ResponseEntity<String> getApprovedSellers(@RequestBody Map<String, Object> request) {
    String response = sellerGraphQLService.getApprovedSellers(request);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/sellersNotApproved")
  public ResponseEntity<String> getNonApprovedSellers(@RequestBody Map<String, Object> request) {
    String response = sellerGraphQLService.getApprovedSellers(request);
    return ResponseEntity.ok(response);
  }

}
