package it.esselunga.gestionespese.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import it.esselunga.gestionespese.repository.SpesaRepository;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Test delle API delle spese: creazione, lista, modifica e avanzamento di stato.
 */
@QuarkusTest
class SpesaResourceTest {

    @Inject
    SpesaRepository spesaRepository;

    private String magazzinoId;
    private String latteId;
    private String spaghettiId;

    @BeforeAll
    static void configura() {
        DatiTest.configuraRestAssured();
    }

    @BeforeEach
    void prepara() {
        spesaRepository.deleteAll();
        magazzinoId = DatiTest.idMagazzino(DatiTest.MAGAZZINO_NOVOLI);
        latteId = DatiTest.idArticolo(DatiTest.LATTE);
        spaghettiId = DatiTest.idArticolo(DatiTest.SPAGHETTI);
    }

    // ------------------------------------------------------------------
    // POST /spese
    // ------------------------------------------------------------------

    @Nested
    class Creazione {

        @Test
        void creaLaSpesaCalcolandoTotaliEImpostandoIDatiDelServer() {
            String richiesta = DatiTest.richiestaSpesa("Mario Rossi", magazzinoId, 45.4642, 9.19,
                    DatiTest.elemento(latteId, 3), DatiTest.elemento(spaghettiId, 2));

            given().contentType(ContentType.JSON).body(richiesta)
                    .when().post("/spese")
                    .then()
                    .statusCode(201)
                    .body("id", notNullValue())
                    .body("stato", equalTo("CREATA"))
                    .body("versione", equalTo(0))
                    .body("dataCreazione", notNullValue())
                    .body("magazzinoId", equalTo(magazzinoId))
                    .body("posizioneConsegna.latitudine", comparesEqualTo(new BigDecimal("45.4642")))
                    .body("posizioneConsegna.longitudine", comparesEqualTo(new BigDecimal("9.19")))
                    .body("articoli", hasSize(2))
                    .body("articoli[0].nome", equalTo(DatiTest.LATTE))
                    .body("articoli[0].prezzo", comparesEqualTo(new BigDecimal("1.29")))
                    .body("articoli[0].prezzoTotale", comparesEqualTo(new BigDecimal("3.87")))
                    .body("articoli[1].prezzoTotale", comparesEqualTo(new BigDecimal("1.78")))
                    .body("totale", comparesEqualTo(new BigDecimal("5.65")));
        }

        @Test
        void magazzinoInesistenteRestituisce400() {
            String richiesta = DatiTest.richiestaSpesa("Mario Rossi", DatiTest.ID_INESISTENTE, 45.46, 9.19,
                    DatiTest.elemento(latteId, 1));

            given().contentType(ContentType.JSON).body(richiesta)
                    .when().post("/spese")
                    .then()
                    .statusCode(400)
                    .body("messaggio", containsString("Magazzino"));
        }

        @Test
        void articoloInesistenteRestituisce400() {
            String richiesta = DatiTest.richiestaSpesa("Mario Rossi", magazzinoId, 45.46, 9.19,
                    DatiTest.elemento(DatiTest.ID_INESISTENTE, 1));

            given().contentType(ContentType.JSON).body(richiesta)
                    .when().post("/spese")
                    .then()
                    .statusCode(400)
                    .body("messaggio", containsString("Articoli non trovati"));
        }

        @Test
        void articoloRipetutoRestituisce400() {
            String richiesta = DatiTest.richiestaSpesa("Mario Rossi", magazzinoId, 45.46, 9.19,
                    DatiTest.elemento(latteId, 1), DatiTest.elemento(latteId, 2));

            given().contentType(ContentType.JSON).body(richiesta)
                    .when().post("/spese")
                    .then()
                    .statusCode(400)
                    .body("messaggio", containsString("più volte"));
        }

        @Test
        void datiNonValidiVengonoRifiutatiDallaValidazione() {
            // nome cliente vuoto, latitudine fuori intervallo, quantità zero
            String richiesta = DatiTest.richiestaSpesa(" ", magazzinoId, 95.0, 9.19,
                    DatiTest.elemento(latteId, 0));

            given().contentType(ContentType.JSON).body(richiesta)
                    .when().post("/spese")
                    .then()
                    .statusCode(400)
                    .body("violations", hasSize(3));
        }

        @Test
        void spesaSenzaArticoliRestituisce400() {
            String richiesta = DatiTest.richiestaSpesa("Mario Rossi", magazzinoId, 45.46, 9.19);

            given().contentType(ContentType.JSON).body(richiesta)
                    .when().post("/spese")
                    .then()
                    .statusCode(400);
        }
    }

    // ------------------------------------------------------------------
    // GET /spese
    // ------------------------------------------------------------------

    @Nested
    class Lista {

        @Test
        void listaRestituisceTutteLeSpeseDallaPiuRecente() throws InterruptedException {
            DatiTest.creaSpesa("Prima", magazzinoId, 45.46, 9.19);
            Thread.sleep(10); // date di creazione distinte (precisione al millisecondo)
            String seconda = DatiTest.creaSpesa("Seconda", magazzinoId, 45.47, 9.20);

            given()
                    .when().get("/spese")
                    .then()
                    .statusCode(200)
                    .body("$", hasSize(2))
                    .body("[0].id", equalTo(seconda))
                    .body("stato", hasItem("CREATA"));
        }
    }

    // ------------------------------------------------------------------
    // PATCH /spese/{id}
    // ------------------------------------------------------------------

    @Nested
    class Modifica {

        @Test
        void modificaIlNomeClienteEIncrementaLaVersione() {
            String spesaId = DatiTest.creaSpesa("Mario Rossi", magazzinoId, 45.46, 9.19);

            given().contentType(ContentType.JSON)
                    .body("{ \"nomeCliente\": \"Mario Bianchi\", \"versione\": 0 }")
                    .when().patch("/spese/" + spesaId)
                    .then()
                    .statusCode(200)
                    .body("nomeCliente", equalTo("Mario Bianchi"))
                    .body("indirizzoConsegna", equalTo("Via Roma 1, Milano"))
                    .body("versione", equalTo(1));
        }

        @Test
        void modificaIndirizzoEPosizioneInsieme() {
            String spesaId = DatiTest.creaSpesa("Mario Rossi", magazzinoId, 45.46, 9.19);

            given().contentType(ContentType.JSON)
                    .body("""
                            {
                              "indirizzoConsegna": "Corso Buenos Aires 10, Milano",
                              "posizioneConsegna": { "latitudine": 45.478, "longitudine": 9.21 },
                              "versione": 0
                            }""")
                    .when().patch("/spese/" + spesaId)
                    .then()
                    .statusCode(200)
                    .body("indirizzoConsegna", equalTo("Corso Buenos Aires 10, Milano"))
                    .body("posizioneConsegna.latitudine", comparesEqualTo(new BigDecimal("45.478")))
                    .body("nomeCliente", equalTo("Mario Rossi"));
        }

        @Test
        void indirizzoSenzaPosizioneRestituisce400() {
            String spesaId = DatiTest.creaSpesa("Mario Rossi", magazzinoId, 45.46, 9.19);

            given().contentType(ContentType.JSON)
                    .body("{ \"indirizzoConsegna\": \"Via Verdi 3, Milano\", \"versione\": 0 }")
                    .when().patch("/spese/" + spesaId)
                    .then()
                    .statusCode(400);
        }

        @Test
        void richiestaSenzaCampiDaModificareRestituisce400() {
            String spesaId = DatiTest.creaSpesa("Mario Rossi", magazzinoId, 45.46, 9.19);

            given().contentType(ContentType.JSON)
                    .body("{ \"versione\": 0 }")
                    .when().patch("/spese/" + spesaId)
                    .then()
                    .statusCode(400);
        }

        @Test
        void versioneNonAggiornataRestituisce409() {
            String spesaId = DatiTest.creaSpesa("Mario Rossi", magazzinoId, 45.46, 9.19);
            String richiesta = "{ \"nomeCliente\": \"Mario Bianchi\", \"versione\": 0 }";

            given().contentType(ContentType.JSON).body(richiesta)
                    .when().patch("/spese/" + spesaId)
                    .then().statusCode(200);

            // Stessa richiesta con la versione ormai superata
            given().contentType(ContentType.JSON).body(richiesta)
                    .when().patch("/spese/" + spesaId)
                    .then()
                    .statusCode(409)
                    .body("messaggio", containsString("modificata nel frattempo"));
        }

        @Test
        void spesaGiaProntaNonEModificabile() {
            String spesaId = DatiTest.creaSpesa("Mario Rossi", magazzinoId, 45.46, 9.19);
            DatiTest.portaAPronta(spesaId); // versione 2

            given().contentType(ContentType.JSON)
                    .body("{ \"nomeCliente\": \"Mario Bianchi\", \"versione\": 2 }")
                    .when().patch("/spese/" + spesaId)
                    .then()
                    .statusCode(409)
                    .body("messaggio", containsString("non è modificabile"));
        }

        @Test
        void spesaInesistenteRestituisce404() {
            given().contentType(ContentType.JSON)
                    .body("{ \"nomeCliente\": \"Mario Bianchi\", \"versione\": 0 }")
                    .when().patch("/spese/" + DatiTest.ID_INESISTENTE)
                    .then()
                    .statusCode(404);
        }
    }

    // ------------------------------------------------------------------
    // PATCH /spese/{id}/avanza-stato
    // ------------------------------------------------------------------

    @Nested
    class AvanzamentoStato {

        @Test
        void percorreTuttoIlFlussoFinoAConsegnataEPoiSiFerma() {
            String spesaId = DatiTest.creaSpesa("Mario Rossi", magazzinoId, 45.46, 9.19);
            String[] statiAttesi = {"IN_PREPARAZIONE", "PRONTA", "IN_CONSEGNA", "CONSEGNATA"};

            for (int versione = 0; versione < statiAttesi.length; versione++) {
                given().contentType(ContentType.JSON)
                        .body("{ \"versione\": " + versione + " }")
                        .when().patch("/spese/" + spesaId + "/avanza-stato")
                        .then()
                        .statusCode(200)
                        .body("stato", equalTo(statiAttesi[versione]))
                        .body("versione", equalTo(versione + 1));
            }

            given().contentType(ContentType.JSON)
                    .body("{ \"versione\": 4 }")
                    .when().patch("/spese/" + spesaId + "/avanza-stato")
                    .then()
                    .statusCode(409)
                    .body("messaggio", containsString("stato finale"));
        }

        @Test
        void doppioInvioConLaStessaVersioneAvanzaUnaSolaVolta() {
            String spesaId = DatiTest.creaSpesa("Mario Rossi", magazzinoId, 45.46, 9.19);
            String richiesta = "{ \"versione\": 0 }";

            given().contentType(ContentType.JSON).body(richiesta)
                    .when().patch("/spese/" + spesaId + "/avanza-stato")
                    .then().statusCode(200).body("stato", equalTo("IN_PREPARAZIONE"));

            given().contentType(ContentType.JSON).body(richiesta)
                    .when().patch("/spese/" + spesaId + "/avanza-stato")
                    .then().statusCode(409);
        }

        @Test
        void spesaInesistenteRestituisce404() {
            given().contentType(ContentType.JSON)
                    .body("{ \"versione\": 0 }")
                    .when().patch("/spese/" + DatiTest.ID_INESISTENTE + "/avanza-stato")
                    .then()
                    .statusCode(404);
        }
    }
}
