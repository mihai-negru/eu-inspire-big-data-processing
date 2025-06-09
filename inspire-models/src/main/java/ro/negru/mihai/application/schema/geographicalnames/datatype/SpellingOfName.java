package ro.negru.mihai.application.schema.geographicalnames.datatype;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.Setter;
import ro.negru.mihai.base.featuretype.Feature;
import ro.negru.mihai.base.stereotype.*;
import ro.negru.mihai.xml.namespace.InspireNamespaces;

@JacksonXmlRootElement(localName = "SpellingOfName", namespace = InspireNamespaces.GN)
@Getter
@Setter
public class SpellingOfName implements Feature {

    @Getter
    @Setter
    public static class Holder {
        @JacksonXmlProperty(namespace = InspireNamespaces.GN)
        private String text;

        @JacksonXmlProperty(namespace = InspireNamespaces.GN)
        private Voidable<String> script;

        @JacksonXmlProperty(namespace = InspireNamespaces.GN)
        private Voidable<String> transliterationScheme;
    }

    @JacksonXmlProperty(localName = "SpellingOfName", namespace = InspireNamespaces.GN)
    private Holder holder;
}