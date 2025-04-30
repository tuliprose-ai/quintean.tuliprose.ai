package ai.tuliprose.libs.quintean;

import java.util.Arrays;
import java.util.List;

public class Place {
    public static final String HOME = "zu Hause";
    public static final Locale DEFAULT_LOCALE = Locale.DE;
    public static final List<String> DEFAULT_PLACE_NAME =
        Arrays.asList("Rose-Pichler-Weg", "7", "80937", "München", "Deutschland",
        "Europa", "Erde", "Sonnensystem", "Milchstraße", "Universum");
    public static final Place DEFAULT_PLACE = new Place(HOME, DEFAULT_LOCALE, DEFAULT_PLACE_NAME);
    
    private final String type; // e.g. "home", "work", "school", "university", "gym", "park", etc.
    private final Locale locale; // e.g. "de", "en", "fr", etc.
    private final List<String> placeName; // e.g. "Rose-Pichler-Weg 7", "80937", "München", "Deutschland", "Europa", "Erde", "Sonnensystem", "Milchstraße", "Universum"
    private final List<Place> connectedPlaces = List.of(); // e.g. "Rose-Pichler-Weg 7", "80937", "München", "Deutschland", "Europa", "Erde", "Sonnensystem", "Milchstraße", "Universum"

    public Place(String type, Locale locale, List<String> placeName) {
        this.type = type;
        this.locale = locale;
        this.placeName = placeName;
    }

    public String getType() {
        return type;
    }

    public Locale getLocale() {
        return locale;
    }

    public List<String> getPlaceName() {
        return placeName;
    }

    public String getPlaceNameAsString(int depth) {
        if (depth < 0 || depth >= placeName.size()) {
            return String.join(", ", placeName);
        }

        return String.join(", ", placeName.subList(0, depth - 1));
    }

    public String getPlaceNameSection(int depth) {
        if (depth < 0 || depth >= placeName.size()) {
            return String.join(", ", placeName);
        }

        return placeName.get(depth);
    }

    public boolean isHome() {
        return type.equals(HOME);
    }

    public boolean isTypeOfPlace(String type) {
        return this.type.equals(type);
    }

    public boolean addConnectedPlace(Place place) {
        return connectedPlaces.add(place);
    }
    
    public List<Place> getConnectedPlaces() {
        return connectedPlaces;
    }
}
