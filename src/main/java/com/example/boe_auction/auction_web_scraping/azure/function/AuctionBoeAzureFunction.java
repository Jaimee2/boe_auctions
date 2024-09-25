package com.example.boe_auction.auction_web_scraping.azure.function;

import com.example.boe_auction.auction_web_scraping.service.AssetService;
import com.example.boe_auction.auction_web_scraping.service.AuctionService;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@AllArgsConstructor
public class AuctionBoeAzureFunction {

    private AuctionService auctionService;
    private AssetService assetService;

    @FunctionName("hello")
    public HttpResponseMessage hello(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.GET},
                    authLevel = AuthorizationLevel.ANONYMOUS,
                    route = "hello"
            )
            HttpRequestMessage<Optional<String>> request,
            ExecutionContext context) {
        log.info(context.toString());
        log.info("Received a request in the hello function.");

        return request.createResponseBuilder(HttpStatus.OK)
                .body("Hello, World!")
                .build();
    }

    @FunctionName("getAllAssetMainMap")
    public HttpResponseMessage getAllAssetMainMap(
            @HttpTrigger(name = "req",
                    methods = {HttpMethod.GET},
                    authLevel = AuthorizationLevel.ANONYMOUS,
                    route = "asset/map-filter"
            ) HttpRequestMessage<Optional<String>> request,
            ExecutionContext context) {
        log.info(context.toString());
        log.info("Received request to getAllAssetMainMap");

//        String auctionType = request.getQueryParameters().getOrDefault("auctionType", null);

        String city = request.getQueryParameters().getOrDefault("city", null);
        String province = request.getQueryParameters().getOrDefault("province", null);
        String startDate = request.getQueryParameters().getOrDefault("startDate", null);
        String endDate = request.getQueryParameters().getOrDefault("endDate", null);
        String minimumBid = request.getQueryParameters().getOrDefault("minimumBid", null);
        String minAppraisalValue = request.getQueryParameters().getOrDefault("minAppraisalValue", null);
        String maxAppraisalValue = request.getQueryParameters().getOrDefault("maxAppraisalValue", null);

        String queryString = request.getUri().getQuery();
        List<String> assetTypes = extractMultipleValues(queryString);

        return request.createResponseBuilder(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(assetService.getAllAssetForMap(city, startDate, endDate, minimumBid,
                        minAppraisalValue, maxAppraisalValue, province, assetTypes))
                .build();
    }

    private List<String> extractMultipleValues(String queryString) {
        if (queryString == null) return Collections.emptyList();
        List<String> values = new ArrayList<>();
        String[] params = queryString.split("&");
        for (String param : params) {
            String[] keyValue = param.split("=");
            if (keyValue.length == 2 && keyValue[0].equals("assetTypes")) values.add(keyValue[1]);
        }
        return values;
    }

    @PostConstruct
    void hello() {
        log.info(" HELLO ---> AuctionBoeAzureFunction");
    }
}
