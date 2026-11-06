package it.esselunga.gestionespese.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import org.bson.types.ObjectId;

/**
 * Conversione degli identificativi tra DTO (String) ed entity (ObjectId).
 * Il formato della stringa è già verificato dalla validazione dei DTO.
 */
@ApplicationScoped
public class IdMapper {

    public ObjectId toObjectId(String id) {
        return id == null ? null : new ObjectId(id);
    }

    public String toString(ObjectId id) {
        return id == null ? null : id.toHexString();
    }
}
