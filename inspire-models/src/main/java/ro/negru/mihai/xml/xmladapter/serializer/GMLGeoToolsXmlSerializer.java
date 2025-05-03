package ro.negru.mihai.xml.xmladapter.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import org.codehaus.stax2.XMLStreamWriter2;
import org.geotools.gml3.GML;
import org.geotools.gml3.GMLConfiguration;
import org.geotools.xsd.Encoder;
import org.locationtech.jts.geom.Geometry;
import ro.negru.mihai.xml.namespace.InspireNamespaces;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class GMLGeoToolsXmlSerializer<T> extends StdSerializer<T> {
    private final QName qname;
    private final Encoder encoder;

    public GMLGeoToolsXmlSerializer(Class<T> clazz, QName qname) {
        super(clazz);
        this.qname = qname;

        encoder = new Encoder(new GMLConfiguration());
        encoder.setOmitXMLDeclaration(true);
        encoder.setNamespaceAware(true);
    }

    @Override
    public void serialize(T value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        ToXmlGenerator xmlGen = (ToXmlGenerator) gen;

        xmlGen.writeStartObject();

        ByteArrayOutputStream writer = new ByteArrayOutputStream();
        encoder.encode(value, qname, writer);

        xmlGen.writeRaw(removeNamespaces(writer.toString(StandardCharsets.UTF_8)));

        xmlGen.writeEndObject();

    }

    private static String removeNamespaces(String gmlXml) {
        return gmlXml.replaceAll("\\s+xmlns(:[A-Za-z0-9_-]+)?=\"[^\"]*\"", "");
    }
}
