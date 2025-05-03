package ro.negru.mihai.xml.namespace;

import javax.xml.namespace.QName;
import java.util.Map;
import java.util.function.Function;

public final class InspireNamespaces {
    private InspireNamespaces() {}

    public final static String GN = "http://inspire.ec.europa.eu/schemas/gn/4.0/GeographicalNames.xsd";
    public final static String GN_PREFIX = "gn";

    public static final String WFS = "http://www.opengis.net/wfs/2.0";
    public static final String WFS_PREFIX = "wfs";

    public static final String GML = "http://www.opengis.net/gml/3.2";
    public static final String GML_PREFIX = "gml";

    public final static String XSI = "http://www.w3.org/2001/XMLSchema-instance";
    public final static String XSI_PREFIX = "xsi";

    public final static String AU = "http://inspire.ec.europa.eu/schemas/2024.2/au/4.0/AdministrativeUnits.xsd";
    public final static String AU_PREFIX = "au";

    public final static String GMD = "https://www.isotc211.org/2005/gmd/gmd.xsd";
    public final static String GMD_PREFIX = "gmd";

    public final static String BASE = "http://inspire.ec.europa.eu/schemas/base/3.3/BaseTypes.xsd";
    public final static String BASE_PREFIX = "base";

    public final static String XLINK = "http://www.w3.org/1999/xlink.xsd";
    public final static String XLINK_PREFIX = "xlink";

    public static Map<String, String> getNamespacePrefixes() {
        return Map.of(
                GN_PREFIX, GN,
                XSI_PREFIX, XSI,
                AU_PREFIX, AU,
                GMD_PREFIX, GMD,
                GML_PREFIX, GML,
                BASE_PREFIX, BASE,
                XLINK_PREFIX, XLINK
                );
    }
}
