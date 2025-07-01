package ro.negru.mihai.xml.xmladapter.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import org.geotools.xsd.Encoder;

import javax.xml.namespace.QName;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class GMLGeoToolsXmlSerializer<T> extends StdSerializer<T> {
    private final QName qname;
    private final Encoder encoder;

    public GMLGeoToolsXmlSerializer(Class<T> clazz, QName qname, Encoder encoder) {
        super(clazz);
        this.qname = qname;

        this.encoder = encoder;
        this.encoder.setOmitXMLDeclaration(true);
        this.encoder.setNamespaceAware(true);
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
