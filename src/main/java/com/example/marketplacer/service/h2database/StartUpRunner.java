package com.example.marketplacer.service.h2database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class StartUpRunner implements ApplicationRunner {

    @Autowired
    private SellerFetchService sellerFetchService;

    @Override
    public void run(ApplicationArguments args) {
        sellerFetchService.fetchParseAndSaveSellers();
    }
}

