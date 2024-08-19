package com.example.boe_auction.auction_web_scraping.service.service.impl;

import com.example.boe_auction.auction_web_scraping.model.Auction;
import com.example.boe_auction.auction_web_scraping.service.service.AuctionWebScrapingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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

                    String title = auctionElement.select("h3").text();
                    String entity = auctionElement.select("h4").text();
                    String statusAndDate = auctionElement.select("p").first().text();
                    String[] statusAndDateParts = statusAndDate.split(" - ");
                    String status = statusAndDateParts[0].replace("Estado: ", "");
                    String conclusion = statusAndDateParts.length > 1 ? statusAndDateParts[1]
                            .replace("[Conclusión prevista: ", "")
                            .replace("]", "") : "No disponible";
                    String place = auctionElement.select("p").get(1).text();

                    Auction auction = new Auction(title, entity, status, place, conclusion);
                    auctions.add(auction);
                }
        );

        return auctions;
    }

}
