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

- **La Spesa è un aggregato**: nella quasi totalità dei casi viene letta e scritta come un'unica unità, quindi si presta a essere modellata come un singolo documento con le righe annidate, senza join.
- **Gli Use Case lavorano su una sola collection**: creazione (UC1), modifica (UC2), lista (UC3) e selezione delle spese pronte per il calcolo del percorso (UC4) operano tutti sulla collection `spese`.
- **Atomicità a livello di documento**: ogni operazione richiesta tocca un solo documento, che in MongoDB è aggiornato in modo atomico. Non servono quindi transazioni multi-documento.

#### Modello dei dati

| Collection | Contenuto | Relazioni |
|---|---|---|
| `magazzini` | id, nome, posizione | - |
| `articoli` | id, nome, descrizione, prezzo | - |
| `spese` | id, nomeCliente, indirizzoConsegna, posizioneConsegna, magazzinoId, stato, dataCreazione, dataUltimaModifica, versione, totale, articoli | `magazzinoId` (riferimento al magazzino), `articoloId` in ogni elemento della lista |

Ogni elemento della lista `articoli` della spesa contiene: `articoloId`, `nome`, `prezzo`, `quantita` e `prezzoTotale` (= `prezzo` × `quantita`). Nome e prezzo sono **copiati dall'articolo** in fase di creazione, così la spesa contiene già tutte le informazioni utili alla visualizzazione (frontend) e al calcolo dei totali (backend), senza ulteriori letture del catalogo.

Le posizioni geografiche sono salvate in formato GeoJSON (`[longitudine, latitudine]`); le API invece le espongono come coppia esplicita `latitudine` / `longitudine`, per evitare inversioni delle coordinate.

Stati della spesa, con flusso lineare: `CREATA` → `IN_PREPARAZIONE` → `PRONTA` → `IN_CONSEGNA` → `CONSEGNATA`.

### Servizi REST

| Metodo | Percorso | Descrizione | Riferimento |
|---|---|---|---|
| `POST` | `/spese` | Creazione di una spesa | UC1 |
| `PATCH` | `/spese/{id}` | Modifica parziale di una spesa | UC2 |
| `GET` | `/spese` | Lista di tutte le spese con il loro stato | UC3 |
| `POST` | `/spese/{id}/avanza-stato` | Avanzamento della spesa allo stato successivo | estensione |
| `GET` | `/magazzini` | Elenco dei magazzini | supporto al frontend |
| `GET` | `/articoli` | Catalogo degli articoli | supporto al frontend |

Gli errori sono restituiti con i codici HTTP standard (`400` richiesta non valida, `404` risorsa non trovata, `409` conflitto) e con un messaggio descrittivo in formato JSON.

### Ipotesi e assunzioni

#### Dati di partenza

- **Magazzini e articoli sono dati di partenza**: il cliente non richiede servizi per gestirli, ma sono necessari per creare una spesa. Vengono quindi caricati automaticamente all'avvio dell'applicazione e sono esposti in sola lettura.
- Il caricamento iniziale **non crea duplicati**: un magazzino o un articolo viene inserito solo se non ne esiste già uno con lo stesso nome, quindi l'applicazione può essere riavviata senza effetti sui dati presenti.
- **Il prezzo degli articoli è fisso** e non varia nel tempo: la copia del prezzo nell'elemento della spesa resta quindi sempre coerente con il catalogo.

#### Creazione della spesa (UC1)

- Il frontend invia solo il riferimento all'articolo e la quantità per ogni elemento: **nome, prezzo, `prezzoTotale` e `totale` sono determinati dal server** a partire dal catalogo. Il frontend può comunque mostrare un'anteprima del saldo usando i prezzi del catalogo, ma il valore salvato è sempre quello calcolato dal server.
- Lo stesso articolo può comparire una sola volta nella spesa (la quantità indica il numero di pezzi); magazzino e articoli indicati devono esistere.
- Id, stato iniziale (`CREATA`), date e versione sono impostati dal server.

#### Modifica della spesa (UC2)

- La spesa è **modificabile solo negli stati `CREATA` e `IN_PREPARAZIONE`**, cioè prima che parta dal magazzino.
- Sono modificabili **il nome del cliente e i dati di consegna**. Indirizzo e posizione di consegna vanno modificati **insieme**, perché la posizione è quella usata per il calcolo del percorso: modificare solo l'indirizzo lascerebbe le coordinate non più coerenti.
- La modifica è **parziale**: il client invia solo i campi da cambiare, gli altri restano invariati.
- Ogni spesa ha un campo `versione`, incrementato a ogni modifica. Il client invia la versione che ha letto: se nel frattempo la spesa è stata modificata o è cambiato il suo stato, la richiesta viene rifiutata (`409`) invece di sovrascrivere i dati. Verifica e aggiornamento avvengono in un'unica operazione atomica sul database.

#### Lista delle spese (UC3)

- Il servizio restituisce **l'intera lista delle spese**, ordinata dalla più recente; **filtri e ordinamenti sono gestiti dal frontend**. La scelta è motivata dal volume di dati contenuto previsto per la challenge: con volumi elevati sarebbe necessario spostare filtri, ordinamento e paginazione sul servizio.

#### Ciclo di vita della spesa (estensione)

- Il cliente non richiede esplicitamente un servizio di cambio stato, ma è necessario perché una spesa possa diventare `PRONTA` ed entrare nel calcolo del percorso (UC4).
- La spesa avanza **solo allo stato successivo** del flusso: non è possibile saltare stati né tornare indietro, e il nuovo stato è deciso dal server. `CONSEGNATA` è lo stato finale.
- Anche l'avanzamento usa il controllo sulla `versione`, così un doppio invio della stessa richiesta (ad esempio un doppio clic) non fa avanzare la spesa di due stati.
- Lo stato di annullamento non è stato previsto, in quanto non richiesto.

#### Integrità dei dati e ambiente

- Il dominio contiene comunque delle relazioni (Spesa → Magazzino, elemento → Articolo). In assenza di foreign key, **l'integrità referenziale e la validazione sono garantite a livello applicativo**: validazione formale dei dati in ingresso e controlli di coerenza prima del salvataggio.
- L'istanza MongoDB del `docker-compose.yml` è **senza autenticazione**, in quanto pensata per l'esecuzione in locale ai fini della challenge.
