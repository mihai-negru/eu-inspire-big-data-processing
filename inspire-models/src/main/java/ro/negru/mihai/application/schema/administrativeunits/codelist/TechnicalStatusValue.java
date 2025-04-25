package ro.negru.mihai.application.schema.administrativeunits.codelist;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import ro.negru.mihai.xml.namespace.InspireNamespaces;

@Getter
public class TechnicalStatusValue {
    @JsonIgnoreType
    public enum Enum {
        EDGE_MATCHER("edgeMatcher"),

        NOT_EDGE_MATCHER("notEdgeMatcher");

        private final String value;

        Enum(String v) {
            value = v;
        }

        public static Enum fromValue(String v) {
            for (Enum c : Enum.values())
                if (c.value.equals(v))
                    return c;
            throw new IllegalArgumentException(v);
        }
    }

    private final static String CODE_LIST = "http://inspire.ec.europa.eu/codelist/TechnicalStatusValue/";

    @JsonCreator
    public TechnicalStatusValue(@JacksonXmlProperty(isAttribute = true, localName = "href") final String href) {
        if (href == null || !href.startsWith(CODE_LIST))
            throw new IllegalArgumentException("The href attribute does not start with " + CODE_LIST);

        int valueIndex = href.lastIndexOf('/');
        if (valueIndex == -1 || valueIndex == href.length() - 1)
            throw new IllegalArgumentException("The href attribute does not contain the code list value");

        try {
            this.code = Enum.fromValue(href.substring(valueIndex + 1));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("The code list value '" + href + "' is not a " + TechnicalStatusValue.class.getName() + " value");
        }

        this.href = href;
    }

    @JacksonXmlProperty(isAttribute = true, namespace = InspireNamespaces.XLINK)
    private final String href;

    @JsonIgnore
    private final Enum code;
}
