package ro.negru.mihai.xml.xmladapter.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import org.geotools.gml3.GML;
import org.geotools.gml3.GMLConfiguration;
import org.geotools.xsd.Encoder;
import org.locationtech.jts.geom.Geometry;

import javax.xml.namespace.QName;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class GMLGeoToolsXmlSerializer<T> extends StdSerializer<T> {
    private final QName qname;

    public GMLGeoToolsXmlSerializer(Class<T> clazz, QName qname) {
        super(clazz);
        this.qname = qname;
    }

    @Override
    public void serialize(T value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        ByteArrayOutputStream writer = new ByteArrayOutputStream();
        Encoder encoder = new Encoder(new GMLConfiguration());
        encoder.setNamespaceAware(true);
        encoder.setOmitXMLDeclaration(true);

        encoder.encode(value, qname, writer);

        String gmlXml = writer.toString(StandardCharsets.UTF_8);

        ToXmlGenerator xmlGen = (ToXmlGenerator) gen;
        xmlGen.writeStartObject();
        xmlGen.writeRaw(gmlXml);
        xmlGen.writeEndObject();
    }
}
