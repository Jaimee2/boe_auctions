package com.example.boe_auction.auction_web_scraping;

import com.example.boe_auction.auction_web_scraping.service.service.AuctionWebScrapingService;
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
        System.out.println(auctionWebScrapingService.performQuery());
    }

}
