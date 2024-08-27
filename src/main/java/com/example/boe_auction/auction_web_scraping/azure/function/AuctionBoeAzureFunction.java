package com.example.boe_auction.auction_web_scraping.azure.function;

import com.example.boe_auction.auction_web_scraping.service.AuctionService;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@AllArgsConstructor
public class AuctionBoeAzureFunction {

    private AuctionService auctionService;

    @FunctionName("hello")
    public HttpResponseMessage run(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.GET},
                    authLevel = AuthorizationLevel.ANONYMOUS,
                    route = "hello"
            )
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        log.info("Received a request in the hello function.");

        return request.createResponseBuilder(HttpStatus.OK)
                .body("Hello, World!")
                .build();
    }

}
