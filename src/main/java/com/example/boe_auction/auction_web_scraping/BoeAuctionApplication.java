package com.example.boe_auction.auction_web_scraping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;


@EnableMongoRepositories
@SpringBootApplication()
public class BoeAuctionApplication {

    public static void main(String[] args) {
        SpringApplication.run(BoeAuctionApplication.class, args);
    }

}
