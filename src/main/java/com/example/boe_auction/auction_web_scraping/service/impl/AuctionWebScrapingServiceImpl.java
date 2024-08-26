package com.example.boe_auction.auction_web_scraping.service.impl;

import com.example.boe_auction.auction_web_scraping.dao.document.Auction;
import com.example.boe_auction.auction_web_scraping.dao.document.AuctionAsset;
import com.example.boe_auction.auction_web_scraping.dao.document.Coordinates;
import com.example.boe_auction.auction_web_scraping.dao.repository.AuctionRepository;
import com.example.boe_auction.auction_web_scraping.ollama.service.OllamaService;
import com.example.boe_auction.auction_web_scraping.service.AuctionWebScrapingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class AuctionWebScrapingServiceImpl implements AuctionWebScrapingService {

    private OllamaService ollamaService;
    private AuctionRepository auctionRepository;

    public List<Auction> performQuery() throws IOException {

        String url = "https://subastas.boe.es/subastas_ava.php";

        Document document = Jsoup.connect(url)
                .method(Connection.Method.POST)
                .data("campo[2]", "SUBASTA.ESTADO.CODIGO")
                .data("dato[2]", "EJ")
                .data("campo[3]", "BIEN.TIPO")
                .data("dato[3]", "I")
                .data("dato[4]", "")
                .data("campo[7]", "BIEN.LOCALIDAD")
                .data("dato[7]", "")
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

        document.select(".resultado-busqueda")
                .stream()
                .map(auctionElement -> getLink(auctionElement
                        .selectFirst("a.resultado-busqueda-link-defecto").attr("href"))
                )
                .filter(auction -> !auctionRepository.existsById(getAuctionIdFromLink(auction)))
                .forEach(auctionLink -> auctions.add(scrapeAuction(auctionLink)));

        log.info(" ************************** Ended web scraping ****************************************************");
        log.info("New auction: {}", auctions);
        log.info(" **************************************************************************************************");

        auctions.forEach(auction ->
                auction.getAssets().forEach(auctionAsset -> {
                    if (auctionAsset.getAddress() == null || auctionAsset.getAddress().isEmpty()) return;
                    auctionAsset.setAddressIA(
                            ollamaService.improveAddressForGeoDataApi(auctionAsset.getAddress())
                    );
                    auctionAsset.setFullAddressWithIA(
                            STR."\{auctionAsset.getAddressIA()}, \{auctionAsset.getPostalCode()}, \{auctionAsset.getCity()}"
                    );
                    auctionAsset.setFullAddress(
                            STR."\{auctionAsset.getAddress()}, \{auctionAsset.getPostalCode()}, \{auctionAsset.getCity()}"
                    );
                    auctionAsset.setCoordinates(getLatLon(auctionAsset.getFullAddressWithIA()));
                })
        );

        auctionRepository.saveAll(auctions);
        return auctions;
    }


    private Auction scrapeAuction(String url) {
        Document detailDoc = getDocument(url);
        Auction auction = getGeneralInformation(detailDoc);

        detailDoc.select("td:contains(Identificador) + td strong").text();
        Elements linkElement = detailDoc.select("a[href*='ver=3']");
        String auctionUrl = getLink(linkElement.attr("href"));

        auction.setAssets(List.of(getGoods(auctionUrl)));

        return auction;
    }

    private Document getDocument(String url) {
        Document detailDoc;
        try {
            detailDoc = Jsoup.connect(url).get();
        } catch (IOException e) {
            log.error("Error trying to get the url {}", url);
            throw new RuntimeException(e);
        }
        return detailDoc;
    }

    private Auction getGeneralInformation(Document detailDoc) {
        Element dataTable = detailDoc.getElementById("idBloqueDatos1").selectFirst("table tbody");
        assert dataTable != null;
        return Auction.builder()
                .identifier(getTextFromTable(dataTable, "Identificador"))
                .auctionType(getTextFromTable(dataTable, "Tipo de subasta"))
                .startDate((getTextFromTable(dataTable, "Fecha de inicio").split("\\(ISO")[0].trim()))
                .endDate((getTextFromTable(dataTable, "Fecha de conclusión").split("\\(ISO")[0].trim()))
                .lots((getTextFromTable(dataTable, "Lotes")))
                .announcementBOE((getTextFromTable(dataTable, "Lotes")))
                .auctionValue(getTextFromTable(dataTable, "Anuncio BOE"))
                .appraisalValue(getTextFromTable(dataTable, "Valor subasta"))
                .minimumBid(getTextFromTable(dataTable, "Puja mínima"))
                .bidIncrement(getTextFromTable(dataTable, "Tramos entre pujas"))
                .depositAmount(getTextFromTable(dataTable, "Importe del depósito"))
                .build();

    }

    private String getTextFromTable(Element table, String headerText) {
        Elements rows = table.select("tr");
        for (Element row : rows) {
            Element header = row.selectFirst("th");
            if (header != null && header.text().equalsIgnoreCase(headerText)) {
                return row.selectFirst("td").text();
            }
        }
        return "";
    }

    private String getLink(String hrefLink) {
        return "https://subastas.boe.es" + hrefLink.substring(1);
    }

    public static String getAuctionIdFromLink(String urlString) {
        try {
            URL url = new URL(urlString);
            String query = url.getQuery();

            Map<String, String> queryPairs = new HashMap<>();
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                String key = URLDecoder.decode(pair.substring(0, idx), "UTF-8");
                String value = URLDecoder.decode(pair.substring(idx + 1), "UTF-8");
                queryPairs.put(key, value);
            }

            return queryPairs.get("idSub");

        } catch (Exception e) {
            log.error("Error in getting auction Id from url");
            throw new RuntimeException();
        }
    }

    private AuctionAsset getGoods(String url) {
        Document detailDoc = getDocument(url);
        Element assetTable = detailDoc.selectFirst("#idBloqueDatos3 .bloque table");
        assert assetTable != null;
        return AuctionAsset.builder()
                .assetLink(url)
                .description(getTextFromTable(assetTable, "Descripción"))
                .iDufir(getTextFromTable(assetTable, "IDUFIR"))
                .cadastralReference(getTextFromTable(assetTable, "Referencia catastral"))
                .address(getTextFromTable(assetTable, "Dirección"))
                .postalCode(getTextFromTable(assetTable, "Código Postal"))
                .city(getTextFromTable(assetTable, "Localidad"))
                .province(getTextFromTable(assetTable, "Provincia"))
                .isPrimaryResidence(getTextFromTable(assetTable, "Vivienda habitual")
                        .equalsIgnoreCase("Sí")
                )
                .possessionStatus(getTextFromTable(assetTable, "Situación posesoria"))
                .isVisitable(getTextFromTable(assetTable, "Visitable"))
                .encumbrances(getTextFromTable(assetTable, "Cargas"))
                .registryDetails(getTextFromTable(assetTable, "Inscripción registral"))
                .legalTitle(getTextFromTable(assetTable, "Título jurídico"))
                .build();
    }

    public Coordinates getLatLon(String address) {

        String url = "https://nominatim.openstreetmap.org/search";

        RestClient restClient = RestClient.create();

        URI uri = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("q", address)
                .queryParam("format", "json")
                .build()
                .toUri();
        log.info(uri.toString());

        Coordinates[] response = restClient.get()
                .uri(uri)
                .retrieve()
                .body(Coordinates[].class);

        if (response != null && response.length >= 1) {
            System.out.println(response[0]);
            return response[0];
        } else {
            return null;
        }

    }

}
