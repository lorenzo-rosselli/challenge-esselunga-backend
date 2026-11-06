package it.esselunga.gestionespese.entity;

/**
 * Stati del ciclo di vita di una spesa (salvato su MongoDB come stringa).
 * <p>
 * Flusso lineare: CREATA → IN_PREPARAZIONE → PRONTA → IN_CONSEGNA → CONSEGNATA.
 */
public enum StatoSpesa {

    CREATA,
    IN_PREPARAZIONE,
    PRONTA,
    IN_CONSEGNA,
    CONSEGNATA

}
