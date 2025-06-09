package ro.negru.mihai.application.schema.geographicalnames.datatype;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.Setter;
import ro.negru.mihai.base.featuretype.Feature;
import ro.negru.mihai.base.stereotype.Voidable;
import ro.negru.mihai.xml.namespace.InspireNamespaces;

import java.net.URI;

@JacksonXmlRootElement(localName = "PronunciationOfName", namespace = InspireNamespaces.GN)
@Getter
@Setter
public class PronunciationOfName implements Feature {

    @Getter
    @Setter
    public static class Holder {
        @JacksonXmlProperty(namespace = InspireNamespaces.GN)
        private Voidable<String> pronunciationIPA;

        @JacksonXmlProperty(namespace = InspireNamespaces.GN)
        private Voidable<URI> pronunciationSoundLink;
    }

    @JacksonXmlProperty(localName = "PronunciationOfName", namespace = InspireNamespaces.GN)
    private Holder holder;
}
