package ai.tuliprose.libs.quintean;

import java.util.Arrays;
import java.util.List;

public class Locale {
    public static final Locale DE =
        new Locale("de", "Deutsch", Arrays.asList(Place.DEFAULT_PLACE));

    private final String code; // e.g. "de", "en", "fr", etc.
    private final String name; // e.g. "Deutsch", "English", "Français", etc.
    private final List<Place> places; // e.g. "Rose-Pichler-Weg 7", "80937", "München", "Deutschland", "Europa", "Erde", "Sonnensystem", "Milchstraße", "Universum"
    private final List<Locale> connectedLocales = List.of(); // e.g. "en", "fr", "es", etc.
    
    public Locale(String code, String name, List<Place> places) {
        this.code = code;
        this.name = name;
        this.places = places;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public List<Place> getPlaces() {
        return places;
    }

    public boolean addConnectedLocale(Locale locale) {
        return connectedLocales.add(locale);
    }

    public List<Locale> getConnectedLocales() {
        return connectedLocales;
    }
}
