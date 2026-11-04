# challenge-esselunga-backend
Progetto realizzato per un colloquio con Esselunga.

## Container Gestione Spese Online

Al candidato è richiesto lo sviluppo di un container in grado di implementare gli Use Case di seguito descritti:

1. **Servizio REST di creazione di una spesa:** Riceve l'entità spesa e la persiste.
2. **Servizio REST di modifica spesa:** È permessa la modifica solo di alcuni campi della spesa (es. correzione indirizzo di consegna prima che la spesa parta dal magazzino).
3. **Servizio REST per lista di spese:** Restituisce la lista delle spese con il loro stato, eventualmente dando la possibilità di filtrare e ordinare.
4. **Servizio REST:** A partire dalla lista delle spese pronte per la consegna, calcola il percorso "migliore" dal magazzino Esselunga agli indirizzi di consegna considerando il rientro in magazzino, dove "migliore" corrisponde a un qualsiasi algoritmo, anche fake o banalmente lineare con interpolazione tra i punti.


### Entità
#### Spesa
- Coordinate Lat/Long di consegna.
- Elenco di articoli.
- Altri attributi ritenuti opportuni dal candidato.

#### Entità Magazzino
- Nome parlante.
- Coordinate Lat/Long.

#### Eventuali altre entità

Lo sviluppo deve essere effettuato con un framework moderno cloud native (in ordine di preferenza: Quarkus, Spring Boot, altro a scelta del candidato ritenuto opportuno allo scopo) e deve produrre un container Docker.

Il tool di build automation preferito è Maven, con l'utilizzo di JUnit per i test.



## Scelte tecniche

### Database: MongoDB

Per la persistenza è stato scelto **MongoDB**, eseguito in un container dedicato tramite `docker-compose.yml`.

#### Motivazione

- **La Spesa è un aggregato**: Nella quasi totalità dei casi viene letta e scritta come un'unica unità, quindi si presta a essere modellata come un singolo documento con le righe annidate, senza join.
- **Gli Use Case lavorano su una sola collection**: creazione (UC1), modifica (UC2), lista con filtri e ordinamento (UC3) e selezione delle spese pronte per il calcolo del percorso (UC4) operano tutti sulla collection `spese`.
- **Atomicità a livello di documento**: ogni operazione richiesta tocca un solo documento, che in MongoDB è aggiornato in modo atomico. Non servono quindi transazioni multi-documento.

#### Modello dei dati

| Collection | Contenuto | Relazioni |
|---|---|---|
| `magazzini` | id, nome, posizione | - |
| `articoli` | id, nome, descrizione, prezzo | - |
| `spese` | id, nomeCliente, indirizzoConsegna, posizioneConsegna, magazzinoId, stato, dataCreazione, dataUltimaModifica, versione, totale, articoli | `magazzinoId` (riferimento al magazzino), `articoloId` in ogni elemento della lista |

Ogni elemento della lista `articoli` della spesa contiene: `articoloId`, `nome`, `prezzo`, `quantita` e `prezzoTotale` (= `prezzo` × `quantita`). Nome e prezzo sono **copiati dall'articolo** in fase di creazione, così la spesa contiene già tutte le informazioni utili alla visualizzazione (frontend) e al calcolo dei totali (backend), senza ulteriori letture del catalogo.

Stati della spesa: `CREATA` → `IN_PREPARAZIONE` → `PRONTA` → `IN_CONSEGNA` → `CONSEGNATA`; `ANNULLATA` è raggiungibile solo da `CREATA` o `IN_PREPARAZIONE`.

#### Trade-off e assunzioni

- Il dominio contiene comunque delle relazioni (Spesa → Magazzino, riga → Articolo). In assenza di foreign key, **l'integrità referenziale e la validazione sono garantite a livello applicativo** (Bean Validation sui DTO e controlli nel service).
- **Il prezzo degli articoli è fisso** e non varia nel tempo: la copia del prezzo nell'elemento della spesa resta quindi sempre coerente con il catalogo.
- `totale` e `prezzoTotale` sono **calcolati dal server**: in creazione il client invia solo `articoloId` e `quantita` per ogni elemento; id, stato iniziale, date e versione sono anch'essi impostati dal server.
- La spesa è **modificabile (UC2) solo negli stati `CREATA` e `IN_PREPARAZIONE`**, cioè prima che parta dal magazzino; i campi modificabili sono `indirizzoConsegna` e `posizioneConsegna`.
- La modifica di una spesa (UC2) è eseguita con un singolo aggiornamento atomico condizionato allo stato e al campo `versione` (contatore incrementato a ogni modifica), per evitare modifiche concorrenti o su spese già partite dal magazzino.
- L'istanza MongoDB del `docker-compose.yml` è **senza autenticazione**, in quanto pensata per l'esecuzione in locale ai fini della challenge.
