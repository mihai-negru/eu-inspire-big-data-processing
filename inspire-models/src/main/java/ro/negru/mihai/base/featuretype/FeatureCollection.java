package ro.negru.mihai.base.featuretype;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Envelope;
import ro.negru.mihai.xml.namespace.InspireNamespaces;

import java.util.List;

@JacksonXmlRootElement(localName = "FeatureCollection", namespace = InspireNamespaces.WFS)
@Getter
@Setter
public class FeatureCollection<T extends Feature> {
    @JacksonXmlProperty(namespace = InspireNamespaces.WFS)
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Envelope> boundedBy;

    @JacksonXmlProperty(namespace = InspireNamespaces.WFS)
    @JacksonXmlElementWrapper(useWrapping = false)
    @Size(min = 1)
    private List<T> member;

    @JacksonXmlProperty(namespace = InspireNamespaces.WFS)
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Object> additionalObjects;

    @JacksonXmlProperty(namespace = InspireNamespaces.WFS)
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Object> truncatedResponse;

    @JacksonXmlProperty(isAttribute = true)
    private String numberReturned;

    @JacksonXmlProperty(isAttribute = true)
    private String numberMatched;

    @JacksonXmlProperty(isAttribute = true)
    private String timeStamp;

    @JacksonXmlProperty(isAttribute = true)
    private String lockId;

    public String etsFamily() {
        return "";
    }
}
