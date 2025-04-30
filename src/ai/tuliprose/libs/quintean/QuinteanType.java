package ai.tuliprose.libs.quintean;

import static ai.tuliprose.libs.quintean.QuinteanRepository.tabs;

public class QuinteanType {
    private final String name;
    private final String metaType;
    private final Locale locale;

    public QuinteanType(String name, String metaType, Locale locale) {
        this.name = name;
        this.metaType = metaType;
        this.locale = locale;
    }

    public String getName() {
        return name;
    }

    public String getMetaType() {
        return metaType;
    }

    public Locale getLocale() {
        return locale;
    }
	
	public String toJson(int indent) {
		return "{\n" + tabs(indent + 1) + "\"name\": \"" + name +
				"\",\n" + tabs(indent + 1) + "\"metaType\": \"" +
				metaType + "\"\n" +	tabs(indent) + "}";
	}
	
    @Override
    public String toString() {
        return "QuinteanType{" +
                "name='" + name + '\'' +
                ", metaType='" + metaType + '\'' +
                ", locale=" + locale +
                '}';
    }
}
