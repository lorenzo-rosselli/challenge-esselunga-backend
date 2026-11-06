package it.esselunga.gestionespese.entity;

/**
 * Stati del ciclo di vita di una spesa (salvato su MongoDB come stringa).
 * <p>
 * Flusso: CREATA → IN_PREPARAZIONE → PRONTA → IN_CONSEGNA → CONSEGNATA.
 * ANNULLATA è raggiungibile solo da CREATA o IN_PREPARAZIONE.
 */
public enum StatoSpesa {

    CREATA,
    IN_PREPARAZIONE,
    PRONTA,
    IN_CONSEGNA,
    CONSEGNATA,
    ANNULLATA
}
