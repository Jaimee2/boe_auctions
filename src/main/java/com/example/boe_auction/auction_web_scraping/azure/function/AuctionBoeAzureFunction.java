package com.example.boe_auction.auction_web_scraping.azure.function;

import com.example.boe_auction.auction_web_scraping.service.AuctionService;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.boe_auction.auction_web_scraping.utils.AuctionUtils.extractMultipleValues;

@Slf4j
@Component
@AllArgsConstructor
public class AuctionBoeAzureFunction {

    private AuctionService auctionService;

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

    @FunctionName("getAllAuctions")
    public HttpResponseMessage getAllAuctions(
            @HttpTrigger(name = "req",
                    methods = {HttpMethod.GET},
                    authLevel = AuthorizationLevel.ANONYMOUS,
                    route = "auctions"
            ) HttpRequestMessage<Optional<String>> request,
            ExecutionContext context) {
        log.info(context.toString());
        log.info("Received request to getAllAuctions");

        String auctionType = request.getQueryParameters().getOrDefault("auctionType", null);

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
                .body(auctionService.getAllAuctions(
                        auctionType, city, startDate, endDate, minimumBid,
                        minAppraisalValue, maxAppraisalValue, province, assetTypes
                ))
                .build();
    }

    @PostConstruct
    void hello() {
        log.info(" HELLO ---> AuctionBoeAzureFunction");
    }

}
