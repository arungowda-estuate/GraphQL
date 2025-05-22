package com.example.marketplacer.controller;

import com.example.marketplacer.service.advert.AdvertGraphQLService;
import com.example.marketplacer.service.seller.SellerGraphQLService;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

  private final SellerGraphQLService sellerGraphQLService;
  private final AdvertGraphQLService advertGraphQLService;

  public UserController(
      SellerGraphQLService sellerGraphQLService, AdvertGraphQLService advertGraphQLService) {
    this.sellerGraphQLService = sellerGraphQLService;
    this.advertGraphQLService = advertGraphQLService;
  }

  @GetMapping("/sellers")
  public ResponseEntity<String> getAllSellers() {
    String response = sellerGraphQLService.fetchAllSellers();
    return ResponseEntity.ok(response);
  }

  @PostMapping("/sellers")
  public ResponseEntity<String> createSeller(@RequestBody Map<String, Object> input) {
    String response = sellerGraphQLService.createSeller(input);
    return ResponseEntity.ok(response);
  }

  @PutMapping("/sellers")
  public ResponseEntity<String> updateSeller(@RequestBody Map<String, Object> input) {
    String response = sellerGraphQLService.updateSeller(input);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/sellers/search")
  public ResponseEntity<String> searchSellerByBusinessName(@RequestBody Map<String, Object> input) {
    String response = sellerGraphQLService.searchSellerByBusinessName(input);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/sellers/by-id")
  public ResponseEntity<String> getSellerById(@RequestBody Map<String, Object> input) {
    String response = sellerGraphQLService.getSellerById(input);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/adverts/create")
  public ResponseEntity<String> createAdvert(@RequestBody Map<String, Object> input) {
    String response = advertGraphQLService.createAdvert(input);
    return ResponseEntity.ok(response);
  }
}
