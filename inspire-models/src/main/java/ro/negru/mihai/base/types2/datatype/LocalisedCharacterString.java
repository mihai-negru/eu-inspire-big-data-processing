package ro.negru.mihai.base.types2.datatype;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import lombok.Getter;
import lombok.Setter;
import ro.negru.mihai.xml.namespace.InspireNamespaces;

@JacksonXmlRootElement(localName = "LocalisedCharacterString", namespace = InspireNamespaces.GMD)
@Getter
@Setter
public class LocalisedCharacterString {

    @Getter
    @Setter
    public static class Holder {
        @JacksonXmlProperty(namespace = InspireNamespaces.GMD)
        @JacksonXmlText
        private String value;

        @JacksonXmlProperty(isAttribute = true, namespace = InspireNamespaces.GMD)
        private String id;

        @JacksonXmlProperty(isAttribute = true, namespace = InspireNamespaces.GMD)
        private String locale;
    }

    @JacksonXmlProperty(localName = "LocalisedCharacterString", namespace = InspireNamespaces.GMD)
    private Holder holder;
}
