package com.example.boe_auction.auction_web_scraping.utils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AuctionUtils {

    public static List<String> extractMultipleValues(String queryString) {
        if (queryString == null) return Collections.emptyList();

        List<String> values = new ArrayList<>();
        String[] params = queryString.split("&");

        for (String param : params) {
            String[] keyValue = param.split("=");
            if (keyValue.length == 2 && keyValue[0].equals("assetType"))
                values.add(URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8));
        }
        return values;
    }
}