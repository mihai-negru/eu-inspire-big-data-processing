package ro.negru.mihai.application.schema.administrativeunits.featuretype;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.LineString;
import ro.negru.mihai.application.schema.administrativeunits.codelist.AdministrativeHierarchyLevel;
import ro.negru.mihai.application.schema.administrativeunits.codelist.LegalStatusValue;
import ro.negru.mihai.application.schema.administrativeunits.codelist.TechnicalStatusValue;
import ro.negru.mihai.base.featuretype.Feature;
import ro.negru.mihai.base.stereotype.Voidable;
import ro.negru.mihai.base.types.datatype.Identifier;
import ro.negru.mihai.base.types2.codelist.CountryCode;
import ro.negru.mihai.xml.namespace.InspireNamespaces;

import java.time.LocalDateTime;
import java.util.List;

@JacksonXmlRootElement(localName = "AdministrativeBoundary", namespace = InspireNamespaces.AU)
@Getter
@Setter
public class AdministrativeBoundary implements Feature {

    @Getter
    @Setter
    public static class Holder {
        @JacksonXmlProperty(namespace = InspireNamespaces.AU)
        private CountryCode country;

        @JacksonXmlProperty(namespace = InspireNamespaces.AU)
        private LineString geometry;

        @JacksonXmlProperty(namespace = InspireNamespaces.AU)
        private Identifier inspireId;

        @JacksonXmlProperty(namespace = InspireNamespaces.AU)
        @Size(min = 1, max = 6)
        private List<AdministrativeHierarchyLevel> nationalLevel;

        @JacksonXmlProperty(namespace = InspireNamespaces.AU)
        private Voidable<LocalDateTime> beginLifespanVersion;

        @JacksonXmlProperty(namespace = InspireNamespaces.AU)
        private Voidable<LocalDateTime> endLifespanVersion;

        @JacksonXmlProperty(namespace = InspireNamespaces.AU)
        private Voidable<LegalStatusValue> legalStatus;

        @JacksonXmlProperty(namespace = InspireNamespaces.AU)
        private Voidable<TechnicalStatusValue> technicalStatus;

        @JacksonXmlProperty(namespace = InspireNamespaces.AU)
        @JacksonXmlElementWrapper(useWrapping = false)
        @Size(min = 1)
        private List<Voidable<AdministrativeUnit>> admUnit;
    }

    @JacksonXmlProperty(localName = "AdministrativeBoundary", namespace = InspireNamespaces.AU)
    private Holder holder;
}
