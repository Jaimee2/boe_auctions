package com.example.boe_auction.auction_web_scraping.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Auction {

    private String title;
    private String entity;
    private String status;
    private String place;
    private String conclusion;

    @Override
    public String toString() {
        return "\n Auction { \n" +
               "    title='" + title + '\n' +
               "    entity='" + entity + '\n' +
               "    status='" + status + '\n' +
               "    place='" + place + '\n' +
               "    conclusion='" + conclusion + '\n' +
               '}';
    }

}
