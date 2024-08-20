package com.example.boe_auction.auction_web_scraping;

import com.example.boe_auction.auction_web_scraping.service.service.AuctionWebScrapingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@AllArgsConstructor
@SpringBootApplication
public class BoeAuctionApplication {

    private AuctionWebScrapingService auctionWebScrapingService;

    public static void main(String[] args) {
        SpringApplication.run(BoeAuctionApplication.class, args);
    }

    @PostConstruct
    void postConstruct() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper().configure(SerializationFeature.INDENT_OUTPUT, true);
        System.out.println(objectMapper.writeValueAsString(auctionWebScrapingService.performQuery()));
    }

}
