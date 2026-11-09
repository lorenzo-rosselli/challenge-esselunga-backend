package it.esselunga.gestionespese.resource;

import io.restassured.RestAssured;
import io.restassured.config.JsonConfig;
import io.restassured.http.ContentType;
import io.restassured.path.json.config.JsonPathConfig;

import java.util.Arrays;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

/**
 * Supporto ai test delle API REST: lettura dei dati iniziali e preparazione delle spese.
 * <p>
 * Magazzini e articoli sono quelli caricati all'avvio dall'applicazione (dati iniziali).
 */
final class DatiTest {

    static final String MAGAZZINO_PIOLTELLO = "Magazzino Pioltello";
    static final String MAGAZZINO_BIANDRATE = "Magazzino Biandrate";
    static final String LATTE = "Latte 1L";       // 1.29
    static final String SPAGHETTI = "Spaghetti 500g"; // 0.89

    static final String ID_INESISTENTE = "000000000000000000000000";

    private DatiTest() {
    }

    /** Gli importi nel JSON vengono letti come BigDecimal, per confronti esatti. */
    static void configuraRestAssured() {
        RestAssured.config = RestAssured.config().jsonConfig(
                JsonConfig.jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL));
    }

    static String idMagazzino(String nome) {
        return given().when().get("/magazzini")
                .then().statusCode(200)
                .extract().path("find { it.nome == '" + nome + "' }.id");
    }

    static String idArticolo(String nome) {
        return given().when().get("/articoli")
                .then().statusCode(200)
                .extract().path("find { it.nome == '" + nome + "' }.id");
    }

    /** Elemento della richiesta di creazione nel formato JSON. */
    static String elemento(String articoloId, int quantita) {
        return """
                { "articoloId": "%s", "quantita": %d }""".formatted(articoloId, quantita);
    }

    /** Corpo JSON di una richiesta di creazione spesa. */
    static String richiestaSpesa(String nomeCliente, String magazzinoId, double latitudine, double longitudine,
                                 String... elementi) {
        String articoli = Arrays.stream(elementi).collect(Collectors.joining(", "));
        return """
                {
                  "nomeCliente": "%s",
                  "indirizzoConsegna": "Via Roma 1, Milano",
                  "posizioneConsegna": { "latitudine": %s, "longitudine": %s },
                  "magazzinoId": "%s",
                  "articoli": [ %s ]
                }""".formatted(nomeCliente, latitudine, longitudine, magazzinoId, articoli);
    }

    /** Crea una spesa e ne restituisce l'id. */
    static String creaSpesa(String nomeCliente, String magazzinoId, double latitudine, double longitudine) {
        return given().contentType(ContentType.JSON)
                .body(richiestaSpesa(nomeCliente, magazzinoId, latitudine, longitudine,
                        elemento(idArticolo(LATTE), 1)))
                .when().post("/spese")
                .then().statusCode(201)
                .extract().path("id");
    }

    /** Fa avanzare la spesa di uno stato, partendo dalla versione indicata. */
    static void avanzaStato(String spesaId, long versione) {
        given().contentType(ContentType.JSON)
                .body("{ \"versione\": " + versione + " }")
                .when().post("/spese/" + spesaId + "/avanza-stato")
                .then().statusCode(200);
    }

    /** Porta una spesa appena creata (CREATA, versione 0) allo stato PRONTA. */
    static void portaAPronta(String spesaId) {
        avanzaStato(spesaId, 0); // CREATA -> IN_PREPARAZIONE
        avanzaStato(spesaId, 1); // IN_PREPARAZIONE -> PRONTA
    }
}
