package ro.negru.mihai.application.schema.geographicalnames.datatype;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;
import ro.negru.mihai.application.schema.geographicalnames.codelist.GrammaticalGenderValue;
import ro.negru.mihai.application.schema.geographicalnames.codelist.GrammaticalNumberValue;
import ro.negru.mihai.application.schema.geographicalnames.codelist.NameStatusValue;
import ro.negru.mihai.application.schema.geographicalnames.codelist.NativenessValue;
import ro.negru.mihai.base.featuretype.Feature;
import ro.negru.mihai.base.stereotype.Voidable;
import ro.negru.mihai.xml.namespace.InspireNamespaces;

import java.util.List;

@JacksonXmlRootElement(localName = "GeographicalName", namespace = InspireNamespaces.GN)
@Getter
@Setter
public class GeographicalName implements Feature {

    @Getter
    @Setter
    public static class Holder {
        @JacksonXmlProperty(namespace = InspireNamespaces.GN)
        @JacksonXmlElementWrapper(useWrapping = false)
        @Size(min = 1)
        private List<SpellingOfName> spelling;

        @JacksonXmlProperty(namespace = InspireNamespaces.GN)
        private Voidable<GrammaticalGenderValue> grammaticalGender;

        @JacksonXmlProperty(namespace = InspireNamespaces.GN)
        private Voidable<GrammaticalNumberValue> grammaticalNumber;

        @JacksonXmlProperty(namespace = InspireNamespaces.GN)
        private Voidable<String> language;

        @JacksonXmlProperty(namespace = InspireNamespaces.GN)
        private Voidable<NameStatusValue> nameStatus;

        @JacksonXmlProperty(namespace = InspireNamespaces.GN)
        private Voidable<NativenessValue> nativeness;

        @JacksonXmlProperty(namespace = InspireNamespaces.GN)
        private Voidable<PronunciationOfName> pronunciation;

        @JacksonXmlProperty(namespace = InspireNamespaces.GN)
        private Voidable<String> sourceOfName;
    }

    @JacksonXmlProperty(localName = "GeographicalName", namespace = InspireNamespaces.GN)
    private Holder holder;
}