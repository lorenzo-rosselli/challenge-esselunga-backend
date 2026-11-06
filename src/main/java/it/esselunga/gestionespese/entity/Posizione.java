package it.esselunga.gestionespese.entity;

import java.util.List;

/**
 * Posizione geografica in formato GeoJSON Point (documento annidato).
 * <pre>{ "type": "Point", "coordinates": [longitudine, latitudine] }</pre>
 * Attenzione: in GeoJSON l'ordine delle coordinate è [longitudine, latitudine].
 */
public class Posizione {

    private String type = "Point";

    /** [longitudine, latitudine] */
    private List<Double> coordinates;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Double> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Double> coordinates) {
        this.coordinates = coordinates;
    }
}
