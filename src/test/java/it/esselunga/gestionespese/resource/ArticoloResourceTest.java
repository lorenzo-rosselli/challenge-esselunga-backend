package it.esselunga.gestionespese.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;

/**
 * Test dell'API del catalogo articoli: GET /articoli.
 */
@QuarkusTest
class ArticoloResourceTest {

    @BeforeAll
    static void configura() {
        DatiTest.configuraRestAssured();
    }

    @Test
    void listaRestituisceGliArticoliInizialiOrdinatiPerNome() {
        given()
                .when().get("/articoli")
                .then()
                .statusCode(200)
                .body("$", hasSize(14))
                .body("nome", contains("Acqua 6x1,5L", "Biscotti 700g", "Caffè 250g", "Latte 1L",
                        "Mele 1kg", "Olio 1L", "Pane 500g", "Passata 500g", "Petto di pollo 500g",
                        "Prosciutto 100g", "Sale 1kg", "Spaghetti 500g", "Tonno 3x80g", "Uova x6"))
                .body("find { it.nome == 'Latte 1L' }.prezzo", comparesEqualTo(new BigDecimal("1.29")));
    }
}
