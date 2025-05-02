package ro.negru.mihai.base.types.datatype;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.Setter;
import ro.negru.mihai.base.featuretype.Feature;
import ro.negru.mihai.base.stereotype.Voidable;
import ro.negru.mihai.xml.namespace.InspireNamespaces;

@Getter
@Setter
@JacksonXmlRootElement(localName = "Identifier", namespace = InspireNamespaces.BASE)
public class Identifier {
    @JacksonXmlProperty(localName = "localId", namespace = InspireNamespaces.BASE)
    private String localId;

    @JacksonXmlProperty(localName = "namespace", namespace = InspireNamespaces.BASE)
    private String namespace;

    @JacksonXmlProperty(localName = "versionId", namespace = InspireNamespaces.BASE)
    private Voidable<String> versionId;
}
