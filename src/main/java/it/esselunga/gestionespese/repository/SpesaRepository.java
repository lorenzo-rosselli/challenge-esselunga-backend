package it.esselunga.gestionespese.repository;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import it.esselunga.gestionespese.entity.Spesa;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Accesso alla collection "spese".
 */
@ApplicationScoped
public class SpesaRepository implements PanacheMongoRepository<Spesa> {
}
