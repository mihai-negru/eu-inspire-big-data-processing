package ro.negru.mihai.application.schema.administrativeunits.featuretype;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;
import org.geotools.geometry.jts.MultiSurface;
import ro.negru.mihai.application.schema.geographicalnames.datatype.GeographicalName;
import ro.negru.mihai.base.featuretype.Feature;
import ro.negru.mihai.base.stereotype.Voidable;
import ro.negru.mihai.base.types.datatype.Identifier;
import ro.negru.mihai.xml.namespace.InspireNamespaces;

import java.time.LocalDateTime;
import java.util.List;

@JacksonXmlRootElement(localName = "Condominium", namespace = InspireNamespaces.AU)
@Getter
@Setter
public class Condominium implements Feature {
    @JacksonXmlProperty(namespace = InspireNamespaces.AU)
    private MultiSurface geometry;

    @JacksonXmlProperty(namespace = InspireNamespaces.AU)
    private Identifier inspireId;

    @JacksonXmlProperty(namespace = InspireNamespaces.AU)
    private Voidable<LocalDateTime> beginLifespanVersion;

    @JacksonXmlProperty(namespace = InspireNamespaces.AU)
    private Voidable<LocalDateTime> endLifespanVersion;

    @JacksonXmlProperty(namespace = InspireNamespaces.AU)
    @JacksonXmlElementWrapper(useWrapping = false)
    @Size
    private List<Voidable<GeographicalName>> name;

    @JacksonXmlProperty(namespace = InspireNamespaces.AU)
    @JacksonXmlElementWrapper(useWrapping = false)
    @Size(min = 1)
    private List<Voidable<AdministrativeUnit>> admUnit;
}
