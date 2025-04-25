package ro.negru.mihai.application.schema.geographicalnames.codelist;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import ro.negru.mihai.xml.namespace.InspireNamespaces;

@Getter
public class NativenessValue {
    @JsonIgnoreType
    public enum Enum {
        ENDONYM("endonym"),
        EXONYM("exonym");

        private final String value;

        Enum(String value) {
            this.value = value;
        }

        public static Enum fromValue(String v) {
            for (Enum c : Enum.values())
                if (c.value.equals(v))
                    return c;
            throw new IllegalArgumentException(v);
        }
    }

    private final static String CODE_LIST = "http://inspire.ec.europa.eu/codelist/NativenessValue/";

    @JsonCreator
    public NativenessValue(@JacksonXmlProperty(isAttribute = true, localName = "href") final String href) {
        if (href == null || !href.startsWith(CODE_LIST))
            throw new IllegalArgumentException("The href attribute does not start with " + CODE_LIST);

        int valueIndex = href.lastIndexOf('/');
        if (valueIndex == -1 || valueIndex == href.length() - 1)
            throw new IllegalArgumentException("The href attribute does not contain the code list value");

        try {
            this.code = Enum.fromValue(href.substring(valueIndex + 1));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("The code list value '" + href + "' is not a " + NativenessValue.class.getName() + " value");
        }

        this.href = href;
    }

    @JacksonXmlProperty(isAttribute = true, namespace = InspireNamespaces.XLINK)
    private final String href;

    @JsonIgnore
    private final Enum code;
}

