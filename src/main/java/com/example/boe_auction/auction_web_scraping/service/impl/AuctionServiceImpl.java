package com.example.boe_auction.auction_web_scraping.service.impl;

import com.example.boe_auction.auction_web_scraping.dao.document.Auction;
import com.example.boe_auction.auction_web_scraping.dao.repository.AuctionRepository;
import com.example.boe_auction.auction_web_scraping.service.AuctionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class AuctionServiceImpl implements AuctionService {

    private AuctionRepository auctionRepository;

    public List<Auction> getAllAuctions() {
        log.info("AuctionService::getAllAuctions");
        return auctionRepository.findAll();
    }

    public void deleteAllAuctions() {
        auctionRepository.deleteAll();
    }

    public void deleteOldAuctions() throws InterruptedException {
        Date now = new Date();
        int batchSize = 50; // Define a suitable batch size
        int totalDeleted = 0;

        List<Auction> auctionsToDelete;
        do {
            auctionsToDelete = auctionRepository.findTopByEndDateBefore(now, PageRequest.of(0, batchSize));
            Thread.sleep(1000);

            auctionRepository.deleteAll(auctionsToDelete);
            totalDeleted += auctionsToDelete.size();
            Thread.sleep(1000);

        } while (!auctionsToDelete.isEmpty());

        log.info("Number of auctions deleted: {}", totalDeleted);
    }

}
