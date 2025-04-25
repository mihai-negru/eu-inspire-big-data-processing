package ro.negru.mihai.base.featuretype;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Envelope;
import ro.negru.mihai.application.schema.administrativeunits.datatype.ResidenceOfAuthority;
import ro.negru.mihai.application.schema.administrativeunits.featuretype.AdministrativeBoundary;
import ro.negru.mihai.application.schema.administrativeunits.featuretype.AdministrativeUnit;
import ro.negru.mihai.application.schema.administrativeunits.featuretype.Condominium;
import ro.negru.mihai.application.schema.geographicalnames.datatype.GeographicalName;
import ro.negru.mihai.application.schema.geographicalnames.datatype.PronunciationOfName;
import ro.negru.mihai.application.schema.geographicalnames.datatype.SpellingOfName;
import ro.negru.mihai.xml.namespace.InspireNamespaces;

import java.util.List;

@JacksonXmlRootElement(localName = "FeatureCollection", namespace = InspireNamespaces.WFS)
@Getter
@Setter
public class FeatureCollection {
    @JacksonXmlProperty(namespace = InspireNamespaces.WFS)
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Envelope> boundedBy;

    @JacksonXmlProperty(namespace = InspireNamespaces.WFS)
    @JacksonXmlElementWrapper(useWrapping = false)
    @Size(min = 1)
    private List<SpellingOfName> member;

    @JacksonXmlProperty(namespace = InspireNamespaces.WFS)
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<FeatureCollection> additionalObjects;

    @JacksonXmlProperty(namespace = InspireNamespaces.WFS)
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Object> truncatedResponse;

    @JacksonXmlProperty(isAttribute = true, namespace = InspireNamespaces.WFS)
    private String numberReturned;

    @JacksonXmlProperty(isAttribute = true, namespace = InspireNamespaces.WFS)
    private String numberMatched;

    @JacksonXmlProperty(isAttribute = true, namespace = InspireNamespaces.WFS)
    private String timeStamp;

    @JacksonXmlProperty(isAttribute = true, namespace = InspireNamespaces.WFS)
    private String lockId;

    @JacksonXmlProperty(isAttribute = true, localName = "xmlns:" + InspireNamespaces.GN_PREFIX)
    private final String _GN = InspireNamespaces.GN;

    @JacksonXmlProperty(isAttribute = true, localName = "xmlns:" + InspireNamespaces.WFS_PREFIX)
    private final String _WFS = InspireNamespaces.WFS;

    @JacksonXmlProperty(isAttribute = true, localName = "xmlns:" + InspireNamespaces.GML_PREFIX)
    private final String _GML = InspireNamespaces.GML;

    @JacksonXmlProperty(isAttribute = true, localName = "xmlns:" + InspireNamespaces.XSI_PREFIX)
    private final String _XSI = InspireNamespaces.XSI;

    @JacksonXmlProperty(isAttribute = true, localName = "xmlns:" + InspireNamespaces.AU_PREFIX)
    private final String _AU = InspireNamespaces.AU;

    @JacksonXmlProperty(isAttribute = true, localName = "xmlns:" + InspireNamespaces.GMD_PREFIX)
    private final String _GMD = InspireNamespaces.GMD;

    @JacksonXmlProperty(isAttribute = true, localName = "xmlns:" + InspireNamespaces.BASE_PREFIX)
    private final String _BASE = InspireNamespaces.BASE;

    @JacksonXmlProperty(isAttribute = true, localName = "xmlns:" + InspireNamespaces.XLINK_PREFIX)
    private final String _XLINK = InspireNamespaces.XLINK;
}
