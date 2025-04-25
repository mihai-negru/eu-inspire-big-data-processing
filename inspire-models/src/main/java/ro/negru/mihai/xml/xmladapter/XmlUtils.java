package ro.negru.mihai.xml.xmladapter;

import com.ctc.wstx.shaded.msv_core.datatype.SerializationContext;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import org.apache.commons.lang3.SerializationUtils;
import org.geotools.gml3.GML;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import ro.negru.mihai.base.stereotype.Voidable;
import ro.negru.mihai.xml.namespace.InspireNamespaces;
import ro.negru.mihai.xml.namespace.NamespaceXmlFactory;
import ro.negru.mihai.xml.xmladapter.deserializer.GMLGeoToolsXmlDeserializer;
import ro.negru.mihai.xml.xmladapter.deserializer.VoidableXmlDeserializer;
import ro.negru.mihai.xml.xmladapter.serializer.GMLGeoToolsXmlSerializer;
import ro.negru.mihai.xml.xmladapter.serializer.VoidableXmlSerializer;

import javax.xml.stream.XMLStreamReader;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class XmlUtils {
    private XmlUtils() {}

    public static class BufferedXmlMapper extends XmlMapper {

        public BufferedXmlMapper(XmlFactory xmlFactory) {
            super(/*xmlFactory*/);
        }

        @Override
        public <T> T readValue(InputStream src, Class<T> valueType) throws IOException {

            BufferedInputStream buffSrc = new BufferedInputStream(src);

            buffSrc.mark(Integer.MAX_VALUE);
            return super.readValue(buffSrc, valueType);
        }
    }

    public static class InspireDefaultModule extends SimpleModule {
        @SuppressWarnings({"unchecked", "rawtypes"})
        private InspireDefaultModule() {
            super("InspireDeserializationModule");

            addDeserializer(Voidable.class, new VoidableXmlDeserializer<>());
            addDeserializer(Point.class, new GMLGeoToolsXmlDeserializer<>(Point.class));
            addDeserializer(LineString.class, new GMLGeoToolsXmlDeserializer<>(LineString.class));
            addDeserializer(MultiPolygon.class, new GMLGeoToolsXmlDeserializer<>(MultiPolygon.class));
            addDeserializer(Envelope.class, new GMLGeoToolsXmlDeserializer<>(Envelope.class));

            addSerializer(Voidable.class, (JsonSerializer) new VoidableXmlSerializer<>());
            addSerializer(Point.class, new GMLGeoToolsXmlSerializer<>(GML._Geometry));
            addSerializer(LineString.class, new GMLGeoToolsXmlSerializer<>(GML._Geometry));
            addSerializer(MultiPolygon.class, new GMLGeoToolsXmlSerializer<>(GML._Geometry));
            addSerializer(Envelope.class, new GMLGeoToolsXmlSerializer<>(GML.Envelope));
        }

        private NamespaceXmlFactory generateNamespaceXmlFactory() {
            return new NamespaceXmlFactory(InspireNamespaces.getNamespacePrefixes());
        }

        public XmlMapper getXmlMapper() {
            XmlMapper xmlMapper = new BufferedXmlMapper(generateNamespaceXmlFactory());
            xmlMapper.registerModule(this);
            xmlMapper.registerModule(new JavaTimeModule());

            return xmlMapper;
        }

        public XmlMapper getPrettyXmlMapper() {
            XmlMapper xmlMapper = new BufferedXmlMapper(generateNamespaceXmlFactory());
            xmlMapper.registerModule(this);
            xmlMapper.registerModule(new JavaTimeModule());

            xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
            xmlMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            xmlMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            xmlMapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true);

            xmlMapper.setAnnotationIntrospector(
                    AnnotationIntrospector.pair(
                            new JaxbAnnotationIntrospector(TypeFactory.defaultInstance()),
                            new JacksonAnnotationIntrospector())
                    );

            return xmlMapper;
        }
    }

    public static InspireDefaultModule getModuleWithDefaults() {
        return new InspireDefaultModule();
    }
}
