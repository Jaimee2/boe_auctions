package com.example.boe_auction.auction_web_scraping.service.service.impl;

import com.example.boe_auction.auction_web_scraping.model.Auction;
import com.example.boe_auction.auction_web_scraping.service.service.AuctionWebScrapingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class AuctionWebScrapingServiceImpl implements AuctionWebScrapingService {

    public List<Auction> performQuery() throws IOException {
        String url = "https://subastas.boe.es/subastas_ava.php";

        Document document = Jsoup.connect(url)
                .method(Connection.Method.POST)
                .data("campo[2]", "SUBASTA.ESTADO.CODIGO")
                .data("dato[2]", "EJ")
                .data("campo[3]", "BIEN.TIPO")
                .data("dato[3]", "I")
                .data("dato[4]", "")
                .data("campo[8]", "BIEN.COD_PROVINCIA")
                .data("dato[8]", "28") //28 -> Madrid
                .data("page_hits", "500")
                .data("sort_field[0]", "SUBASTA.FECHA_FIN")
                .data("sort_order[0]", "desc")
                .data("sort_field[1]", "SUBASTA.FECHA_FIN")
                .data("sort_order[1]", "asc")
                .data("accion", "Buscar")
                .execute().parse();

        List<Auction> auctions = new ArrayList<>();

        document.select(".resultado-busqueda").forEach(
                auctionElement -> {
                    Element linkElement = auctionElement.selectFirst("a.resultado-busqueda-link-defecto");
                    String auctionUrl = "https://subastas.boe.es" + linkElement.attr("href").substring(1);
                    try {
                        scrapeDetailPage(auctionUrl);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        return auctions;
    }

    private Auction scrapeDetailPage(String url) throws IOException {
        Document detailDoc = Jsoup.connect(url).get();
        Elements bienesLinks = detailDoc.select("a[href*='ver=3']");
        System.out.println(bienesLinks);
        String auctionUrl = "https://subastas.boe.es" + bienesLinks.attr("href").substring(1);
        scrapeDetailPageGoods(auctionUrl);
        return null;
    }

    private Auction scrapeDetailPageGoods(String url) throws IOException {
        Document detailDoc = Jsoup.connect(url).get();
        System.out.println(detailDoc);
        return null;
    }

}
