package ro.negru.mihai.base.types2.codelist;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import ro.negru.mihai.xml.namespace.InspireNamespaces;

@JacksonXmlRootElement(localName = "Country", namespace = InspireNamespaces.GMD)
@Getter
public class CountryCode {
    @JsonIgnoreType
    public enum Enum {
        AT("AT"),
        BE("BE"),
        BG("BG"),
        CY("CY"),
        CZ("CZ"),
        DE("DE"),
        DK("DK"),
        EE("EE"),
        EL("EL"),
        ES("ES"),
        FI("FI"),
        FR("FR"),
        HR("HR"),
        HU("HU"),
        IE("IE"),
        IT("IT"),
        LT("LT"),
        LU("LU"),
        LV("LV"),
        MT("MT"),
        NL("NL"),
        PL("PL"),
        PT("PT"),
        RO("RO"),
        SE("SE"),
        SI("SI"),
        SK("SK"),
        TR("TR"),
        UK("UK");

        private final String value;

        Enum(String v) {
            this.value = v;
        }

        @JsonCreator
        public static Enum fromValue(String v) {
            for (Enum c : Enum.values())
                if (c.value.equals(v))
                    return c;
            throw new IllegalArgumentException(v);
        }
    }

    private final static String CODE_LIST = "http://inspire.ec.europa.eu/codelist/CountryCode/";

    @JsonCreator
    public CountryCode(@JacksonXmlProperty(isAttribute = true, localName = "href") final String href) {
        if (href == null || !href.startsWith(CODE_LIST))
            throw new IllegalArgumentException("The href attribute does not start with " + CODE_LIST);

        int valueIndex = href.lastIndexOf('/');
        if (valueIndex == -1 || valueIndex == href.length() - 1)
            throw new IllegalArgumentException("The href attribute does not contain the code list value");

        try {
            this.code = Enum.fromValue(href.substring(valueIndex + 1));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("The code list value '" + href + "' is not a " + CountryCode.class.getName() + " value");
        }

        this.href = href;
    }

    @JacksonXmlProperty(isAttribute = true, namespace = InspireNamespaces.XLINK)
    private final String href;

    @JsonIgnore
    private final Enum code;
}
