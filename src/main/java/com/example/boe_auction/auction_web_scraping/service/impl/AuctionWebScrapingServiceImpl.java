package com.example.boe_auction.auction_web_scraping.service.impl;

import com.example.boe_auction.auction_web_scraping.dao.document.Auction;
import com.example.boe_auction.auction_web_scraping.dao.document.AuctionAsset;
import com.example.boe_auction.auction_web_scraping.dao.document.Coordinates;
import com.example.boe_auction.auction_web_scraping.dao.repository.AuctionAssetRepository;
import com.example.boe_auction.auction_web_scraping.dao.repository.AuctionRepository;
import com.example.boe_auction.auction_web_scraping.enums.Provinces;
import com.example.boe_auction.auction_web_scraping.ollama.service.OllamaService;
import com.example.boe_auction.auction_web_scraping.restcall.azure.geocoding.GeoCodingAzureApi;
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
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ch.qos.logback.core.util.StringUtil.isNullOrEmpty;
import static com.example.boe_auction.auction_web_scraping.utils.AuctionUtils.convertStringToDate;


@Slf4j
@Service
@AllArgsConstructor
public class AuctionWebScrapingServiceImpl implements AuctionWebScrapingService {

    private OllamaService ollamaService;
    private AuctionRepository auctionRepository;
    private AuctionAssetRepository auctionAssetRepository;
    private GeoCodingAzureApi geoCodingAzureApi;

    public List<Auction> performQuery() throws IOException {

        return Provinces.getAllCodes()
                .stream()
                .flatMap(provinceCode -> {
                    try {
                        Thread.sleep(100);
                        return getAuctionsByProvinceCode(provinceCode).stream(); // Convert List<Auction> to Stream<Auction>
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }).toList();
    }

    private List<Auction> getAuctionsByProvinceCode(String provinceCode) throws IOException, InterruptedException {
        log.info(" ------Scraping provinceCode: {} --------", provinceCode);
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
                .data("dato[8]", provinceCode) //28 -> Madrid //23 -> Jaén // 31 -> Navarra
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
                        .selectFirst("a.resultado-busqueda-link-defecto").attr("href")))
                .filter(auction -> !auctionRepository.existsById(getAuctionIdFromLink(auction)))
                .forEach(auctionLink -> {
                    try {
                        auctions.add(scrapeAuction(auctionLink));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });

        return auctions;
    }

    private void setGeoLocation(AuctionAsset auctionAsset) {
        if (auctionAsset.getAddress() == null || auctionAsset.getAddress().isEmpty()) return;

        auctionAsset.setFullAddress(String.format("%s, %s, %s %s",
                auctionAsset.getAddress(),
                auctionAsset.getPostalCode(),
                auctionAsset.getCity(),
                "España")
        );

        Coordinates coordinates =
                geoCodingAzureApi.getLatLonAzureGeocodingApi(auctionAsset.getFullAddress());
        if (coordinates != null) {
            auctionAsset.setCoordinates(coordinates);
        } else {
            log.error("Error to find the coordinates of the auctionAsset: {}", auctionAsset);
        }

    }

    private Auction scrapeAuction(String url) throws InterruptedException {
        Document detailDoc = getDocument(url);
        Auction auction = getGeneralInformation(detailDoc);

        auctionRepository.save(auction);
        Thread.sleep(100);

        detailDoc.select("td:contains(Identificador) + td strong").text();
        Elements linkElement = detailDoc.select("a[href*='ver=3']"); //This get either Lotes url and bines url
        String auctionUrl = getLink(linkElement.attr("href"));

        if (linkElement.text().contains("Bienes")) {
            AuctionAsset auctionAsset = getGoods(auctionUrl);

            auctionAsset.setAuctionId(auction.getIdentifier());
            auctionAsset.setStartDate(auction.getStartDate());
            auctionAsset.setEndDate(auction.getEndDate());
            auctionAsset.setAuctionValue(auction.getAuctionValue());
            auctionAsset.setAppraisalValue(auction.getAppraisalValue());
            auctionAsset.setMinimumBid(auction.getMinimumBid());
            auctionAsset.setDepositAmount(auction.getDepositAmount());
            auctionAsset.setBidIncrement(auction.getBidIncrement());

            setGeoLocation(auctionAsset);

            auctionAssetRepository.save(auctionAsset);

        }

        if (linkElement.text().contains("Lotes")) {
            List<AuctionAsset> auctionAssets = scrapeLots(auctionUrl);

            auctionAssets.forEach(auctionAsset -> {
                auctionAsset.setAuctionId(auction.getIdentifier());
                auctionAsset.setStartDate(auction.getStartDate());
                auctionAsset.setEndDate(auction.getEndDate());
                setGeoLocation(auctionAsset);
            });

            // Save in batches

            for (int i = 0; i < auctionAssets.size(); i += 50) {
                int end = Math.min(i + 50, auctionAssets.size());
                List<AuctionAsset> batch = auctionAssets.subList(i, end);
                auctionAssetRepository.saveAll(batch);
                Thread.sleep(1000);
            }

        }
        return auction;

    }

    private List<AuctionAsset> scrapeLots(String lotUrl) {
        Document lotDocument = getDocument(lotUrl);
        Elements lotLinks = lotDocument.select("ul.navlistver a[href*='idLote']");
        List<AuctionAsset> lotAssets = new ArrayList<>();

        lotLinks.forEach(lot -> {
            String urlLot = getLink(lot.attr("href"));
            log.info("Scraping lot URL: {}", urlLot);

            Document lotDetailDoc = getDocument(urlLot);

            // Dynamically select the correct lot block based on the pattern
            Element lotBlock = lotDetailDoc.selectFirst("div[id^=idBloqueLote]");
            if (lotBlock == null) {
                log.warn("No lot block found for URL: {}", urlLot);
                return;
            }
            AuctionAsset auctionAsset = extractLotDetails(lotBlock, urlLot);

            lotAssets.add(auctionAsset);


        });

        return lotAssets;
    }

    private AuctionAsset extractLotDetails(Element lotBlock, String urlLot) {
        urlLot = urlLot.replaceAll("&idBus=[^&]*", "");
        Element lotTable = lotBlock.selectFirst("table");

        Element assetTable = lotBlock.selectFirst("h4:contains(Bien) + table");
        if (assetTable == null || lotTable == null) {
            log.warn("Asset table not found for URL: {}", urlLot);
            return null;
        }
        return AuctionAsset.builder()
                .assetLink(urlLot)
                .assetType(getAssetType(lotBlock.selectFirst("h4:contains(Bien)")))
                .description(getTextFromTable(assetTable, "Descripción"))
                .address(getTextFromTable(assetTable, "Dirección"))
                .postalCode(getTextFromTable(assetTable, "Código Postal"))
                .city(getTextFromTable(assetTable, "Localidad"))
                .province(getTextFromTable(assetTable, "Provincia"))
                .isPrimaryResidence(getTextFromTable(assetTable, "Vivienda habitual").equalsIgnoreCase("Sí"))
                .possessionStatus(getTextFromTable(assetTable, "Situación posesoria"))
                .isVisitable(getTextFromTable(assetTable, "Visitable"))
                .encumbrances(getTextFromTable(assetTable, "Cargas"))
                .registryDetails(getTextFromTable(assetTable, "Inscripción registral"))
                .legalTitle(getTextFromTable(assetTable, "Título jurídico"))
                .auctionValue(stringCurrencyNumberToDouble(getTextFromTable(lotTable, "Valor Subasta")))
                .appraisalValue(stringCurrencyNumberToDouble(getTextFromTable(lotTable, "Valor de tasación")))
                .minimumBid(stringCurrencyNumberToDouble(getTextFromTable(lotTable, "Puja mínima")))
                .bidIncrement(stringCurrencyNumberToDouble(getTextFromTable(lotTable, "Tramos entre pujas")))
                .depositAmount(stringCurrencyNumberToDouble(getTextFromTable(lotTable, "Importe del depósito")))
                .build();
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

        stringCurrencyNumberToDouble(getTextFromTable(dataTable, "Valor subasta"));

        return Auction.builder()
                .identifier(getTextFromTable(dataTable, "Identificador"))
                .auctionType(getTextFromTable(dataTable, "Tipo de subasta"))
                .startDate(convertStringToDate((getTextFromTable(dataTable, "Fecha de inicio").split("\\(ISO")[0].trim())))
                .endDate(convertStringToDate((getTextFromTable(dataTable, "Fecha de conclusión").split("\\(ISO")[0].trim())))
                .lots((getTextFromTable(dataTable, "Lotes")))
                .announcementBOE((getTextFromTable(dataTable, "Anuncio BOE")))
                .auctionValue(stringCurrencyNumberToDouble(getTextFromTable(dataTable, "Valor subasta")))
                .appraisalValue(stringCurrencyNumberToDouble(getTextFromTable(dataTable, "Tasación")))
                .minimumBid(stringCurrencyNumberToDouble(getTextFromTable(dataTable, "Puja mínima")))
                .bidIncrement(stringCurrencyNumberToDouble(getTextFromTable(dataTable, "Tramos entre pujas")))
                .depositAmount(stringCurrencyNumberToDouble(getTextFromTable(dataTable, "Importe del depósito")))
                .build();

    }

    private float stringCurrencyNumberToDouble(String currencyNumber) {
        if (isNullOrEmpty(currencyNumber)) {
            log.error("Error currencyNumber is null or empty!");
            return 0f;
        }

        String value = currencyNumber.replaceAll("€", "");
        NumberFormat format = NumberFormat.getInstance(new Locale("es", "ES"));
        Number number = null;
        try {
            number = format.parse(value);
        } catch (ParseException e) {
            return 0f;
        }

        float doubleValue = number.floatValue();
        System.out.println("Parsed double value: " + doubleValue);
        return doubleValue;
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
                String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
                String value = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8);
                queryPairs.put(key, value);
            }

            return queryPairs.get("idSub");

        } catch (Exception e) {
            log.error("Error in getting auction Id from url");
            throw new RuntimeException();
        }
    }

    private AuctionAsset getGoods(String url) {
        url = url.replaceAll("&idBus=[^&]*", "");
        Document detailDoc = getDocument(url);
        Element assetTable = detailDoc.selectFirst("#idBloqueDatos3 .bloque table");
        assert assetTable != null;
        return AuctionAsset.builder()
                .assetLink(url)
                .assetType(getAssetType(detailDoc))
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

    private String getAssetType(Element assetHeading) {
        if (assetHeading == null) return "Unknown";
        String text = assetHeading.text(); // Example format: "Bien 1 - Inmueble (Local comercial)"
        Pattern pattern = Pattern.compile("\\(([^)]+)\\)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) return matcher.group(1);
        return "Unknown";
    }

    private static String getAssetType(Document detailDoc) {
        String type = String.valueOf(detailDoc.selectFirst("#idBloqueDatos3 .bloque h4"));
        Pattern pattern = Pattern.compile("\\(([^)]+)\\)");
        Matcher matcher = pattern.matcher(type);
        if (matcher.find()) {
            String valueInsideParentheses = matcher.group(1);
            log.info("Value inside parentheses: {}", valueInsideParentheses);
            return valueInsideParentheses;
        }
        return "????????";

    }

    public Coordinates getLatLonOpenStreetMap(String address) {

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
            log.info(response[0].toString());
            return response[0];
        } else {
            return null;
        }
    }

}
