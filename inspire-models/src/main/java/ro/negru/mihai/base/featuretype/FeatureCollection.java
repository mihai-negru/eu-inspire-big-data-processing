package ro.negru.mihai.base.featuretype;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Envelope;
import ro.negru.mihai.application.schema.administrativeunits.featuretype.Condominium;
import ro.negru.mihai.application.schema.geographicalnames.datatype.SpellingOfName;
import ro.negru.mihai.xml.namespace.InspireNamespaces;

import java.util.List;

@JacksonXmlRootElement(localName = "FeatureCollection", namespace = InspireNamespaces.WFS)
@Getter
@Setter
public class FeatureCollection {
//    @JacksonXmlProperty(namespace = InspireNamespaces.WFS)
//    @JacksonXmlElementWrapper(useWrapping = false)
//    private List<Envelope> boundedBy;

    @JacksonXmlProperty(namespace = InspireNamespaces.WFS)
    @JacksonXmlElementWrapper(useWrapping = false)
    @Size(min = 1)
    private List<Condominium> member;

//    @JacksonXmlProperty(namespace = InspireNamespaces.WFS)
//    @JacksonXmlElementWrapper(useWrapping = false)
//    private List<FeatureCollection> additionalObjects;
//
//    @JacksonXmlProperty(namespace = InspireNamespaces.WFS)
//    @JacksonXmlElementWrapper(useWrapping = false)
//    private List<Object> truncatedResponse;
//
//    @JacksonXmlProperty(isAttribute = true, namespace = InspireNamespaces.WFS)
//    private String numberReturned;
//
//    @JacksonXmlProperty(isAttribute = true, namespace = InspireNamespaces.WFS)
//    private String numberMatched;
//
//    @JacksonXmlProperty(isAttribute = true, namespace = InspireNamespaces.WFS)
//    private String timeStamp;
//
//    @JacksonXmlProperty(isAttribute = true, namespace = InspireNamespaces.WFS)
//    private String lockId;
}
