package ro.negru.mihai.application.schema.administrativeunits.datatype;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Point;
import ro.negru.mihai.application.schema.geographicalnames.datatype.GeographicalName;
import ro.negru.mihai.base.featuretype.Feature;
import ro.negru.mihai.base.stereotype.Voidable;
import ro.negru.mihai.xml.namespace.InspireNamespaces;

@JacksonXmlRootElement(localName = "ResidenceOfAuthority", namespace = InspireNamespaces.AU)
@Getter
@Setter
public class ResidenceOfAuthority implements Feature {

    @Getter
    @Setter
    public static class Holder {
        @JacksonXmlProperty(namespace = InspireNamespaces.AU)
        private GeographicalName name;

        @JacksonXmlProperty(namespace = InspireNamespaces.AU)
        private Voidable<Point> geometry;
    }

    @JacksonXmlProperty(localName = "ResidenceOfAuthority", namespace = InspireNamespaces.AU)
    private Holder holder;
}
