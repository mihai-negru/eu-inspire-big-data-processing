package ro.negru.mihai.base.types2.datatype;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.Setter;
import ro.negru.mihai.xml.namespace.InspireNamespaces;

@JacksonXmlRootElement(localName = "LocalisedCharacterString", namespace = InspireNamespaces.GMD)
@Getter
@Setter
public class LocalisedCharacterString {
    @JacksonXmlProperty(namespace = InspireNamespaces.GMD)
    private String value;

    @JacksonXmlProperty(namespace = InspireNamespaces.GMD)
    private String id;

    @JacksonXmlProperty(namespace = InspireNamespaces.GMD)
    private String locale;
}
