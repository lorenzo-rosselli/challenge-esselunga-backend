package it.esselunga.gestionespese.resource;

import io.quarkus.test.junit.QuarkusTest;
import it.esselunga.gestionespese.repository.SpesaRepository;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Test delle API dei magazzini: GET /magazzini e GET /magazzini/{id}/percorso (UC4).
 */
@QuarkusTest
class MagazzinoResourceTest {

    @Inject
    SpesaRepository spesaRepository;

    @BeforeAll
    static void configura() {
        DatiTest.configuraRestAssured();
    }

    @BeforeEach
    void pulisciSpese() {
        spesaRepository.deleteAll();
    }

    @Test
    void listaRestituisceIMagazziniInizialiOrdinatiPerNome() {
        given()
                .when().get("/magazzini")
                .then()
                .statusCode(200)
                .body("$", hasSize(9))
                .body("nome", contains("Campi Bisenzio", "Firenze Gignoro", "Firenze Novoli",
                        "Firenze Osmannoro", "Firenze Via Canova", "Pistoia Porta Nuova",
                        "Prato Leonardo", "Prato Viale Galilei", "Sesto Fiorentino"))
                .body("[0].posizione.latitudine", notNullValue())
                .body("[0].posizione.longitudine", notNullValue());
    }

    @Test
    void percorsoSenzaSpesePronteNonHaTappe() {
        String magazzinoId = DatiTest.idMagazzino(DatiTest.MAGAZZINO_NOVOLI);
        DatiTest.creaSpesa("Cliente non pronta", magazzinoId, 43.776, 11.255); // resta CREATA

        given()
                .when().get("/magazzini/" + magazzinoId + "/percorso")
                .then()
                .statusCode(200)
                .body("magazzino.id", equalTo(magazzinoId))
                .body("tappe", empty())
                .body("distanzaTotaleKm", comparesEqualTo(BigDecimal.ZERO));
    }

    @Test
    void percorsoVisitaLeSpesePronteDalPiuVicinoERientraAlMagazzino() {
        String novoli = DatiTest.idMagazzino(DatiTest.MAGAZZINO_NOVOLI); // 43.79122, 11.22319
        String prato = DatiTest.idMagazzino(DatiTest.MAGAZZINO_PRATO);

        // Create in ordine diverso da quello atteso nel percorso
        String lontana = DatiTest.creaSpesa("Lontana", novoli, 43.93, 10.91);
        String vicina = DatiTest.creaSpesa("Vicina", novoli, 43.795, 11.225);
        String media = DatiTest.creaSpesa("Media", novoli, 43.776, 11.255);
        DatiTest.portaAPronta(lontana);
        DatiTest.portaAPronta(vicina);
        DatiTest.portaAPronta(media);

        // Spesa pronta di un altro magazzino: non deve comparire nel percorso
        DatiTest.portaAPronta(DatiTest.creaSpesa("Altro magazzino", prato, 43.87, 11.09));

        given()
                .when().get("/magazzini/" + novoli + "/percorso")
                .then()
                .statusCode(200)
                .body("tappe", hasSize(3))
                .body("tappe.nomeCliente", contains("Vicina", "Media", "Lontana"))
                .body("tappe.ordine", contains(1, 2, 3))
                .body("tappe[0].spesaId", equalTo(vicina))
                .body("distanzaRientroKm", greaterThan(BigDecimal.ZERO))
                .body("distanzaTotaleKm", greaterThan(BigDecimal.ZERO));
    }

    @Test
    void percorsoDiMagazzinoInesistenteRestituisce404() {
        given()
                .when().get("/magazzini/" + DatiTest.ID_INESISTENTE + "/percorso")
                .then()
                .statusCode(404)
                .body("messaggio", notNullValue());
    }

    @Test
    void percorsoConIdNonValidoRestituisce404() {
        given()
                .when().get("/magazzini/non-valido/percorso")
                .then()
                .statusCode(404);
    }
}
