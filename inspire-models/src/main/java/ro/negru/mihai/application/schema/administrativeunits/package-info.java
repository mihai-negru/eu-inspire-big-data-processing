@XmlSchema(
        namespace = "http://www.opengis.net/wfs/2.0",
        xmlns = {
                @XmlNs(prefix = "wfs", namespaceURI = "http://www.opengis.net/wfs/2.0"),
                @XmlNs(prefix = "au", namespaceURI = "http://inspire.ec.europa.eu/schemas/2025.1/au/5.0"),
                @XmlNs(prefix = "base", namespaceURI = "http://inspire.ec.europa.eu/schemas/2025.1/base/4.0"),
                @XmlNs(prefix = "xlink", namespaceURI = "http://www.w3.org/1999/xlink"),
                @XmlNs(prefix = "xsi", namespaceURI = "http://www.w3.org/2001/XMLSchema-instance")
        },
        elementFormDefault = XmlNsForm.QUALIFIED
)
package ro.negru.mihai.application.schema.administrativeunits;

import jakarta.xml.bind.annotation.XmlNs;
import jakarta.xml.bind.annotation.XmlNsForm;
import jakarta.xml.bind.annotation.XmlSchema;