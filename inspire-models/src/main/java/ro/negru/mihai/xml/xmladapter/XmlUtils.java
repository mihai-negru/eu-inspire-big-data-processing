package ro.negru.mihai.xml.xmladapter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.BeanDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanSerializer;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.geotools.gml3.GML;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import ro.negru.mihai.application.schema.administrativeunits.datatype.ResidenceOfAuthority;
import ro.negru.mihai.application.schema.administrativeunits.featuretype.AdministrativeBoundary;
import ro.negru.mihai.application.schema.administrativeunits.featuretype.AdministrativeUnit;
import ro.negru.mihai.application.schema.administrativeunits.featuretype.Condominium;
import ro.negru.mihai.application.schema.geographicalnames.datatype.GeographicalName;
import ro.negru.mihai.application.schema.geographicalnames.datatype.PronunciationOfName;
import ro.negru.mihai.application.schema.geographicalnames.datatype.SpellingOfName;
import ro.negru.mihai.base.stereotype.Voidable;
import ro.negru.mihai.base.types.datatype.Identifier;
import ro.negru.mihai.base.types2.datatype.LocalisedCharacterString;
import ro.negru.mihai.xml.namespace.InspireNamespaces;
import ro.negru.mihai.xml.namespace.NamespaceXmlFactory;
import ro.negru.mihai.xml.xmladapter.deserializer.ForceRootElementBeanDeserializer;
import ro.negru.mihai.xml.xmladapter.deserializer.GMLGeoToolsXmlDeserializer;
import ro.negru.mihai.xml.xmladapter.deserializer.VoidableXmlDeserializer;
import ro.negru.mihai.xml.xmladapter.serializer.ForceRootElementBeanSerializer;
import ro.negru.mihai.xml.xmladapter.serializer.GMLGeoToolsXmlSerializer;
import ro.negru.mihai.xml.xmladapter.serializer.VoidableXmlSerializer;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

public final class XmlUtils {
    private XmlUtils() {}

    public static class BufferedXmlMapper extends XmlMapper {

        public BufferedXmlMapper(XmlFactory xmlFactory) {
            super(xmlFactory);
        }

        @Override
        public <T> T readValue(InputStream src, Class<T> valueType) throws IOException {

            BufferedInputStream buffSrc = new BufferedInputStream(src);

            buffSrc.mark(Integer.MAX_VALUE);
            return super.readValue(buffSrc, valueType);
        }
    }

    public static class InspireDefaultModule extends SimpleModule {
        private static final Set<Class<?>> forceRootElements = new HashSet<>();

        static {
            forceRootElements.add(LocalisedCharacterString.class);
            forceRootElements.add(Identifier.class);

            forceRootElements.add(SpellingOfName.class);
            forceRootElements.add(PronunciationOfName.class);
            forceRootElements.add(GeographicalName.class);

            forceRootElements.add(Condominium.class);
            forceRootElements.add(AdministrativeUnit.class);
            forceRootElements.add(AdministrativeBoundary.class);
            forceRootElements.add(ResidenceOfAuthority.class);
        }

        private InspireDefaultModule() {
            super("InspireDefaultModule");

            setCustomSerializersAndDeserializers();
            forceRootElementsSerializerAndDeserializer();

        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        private void setCustomSerializersAndDeserializers() {
            addDeserializer(Voidable.class, new VoidableXmlDeserializer<>());
            addDeserializer(Point.class, new GMLGeoToolsXmlDeserializer<>(Point.class));
            addDeserializer(LineString.class, new GMLGeoToolsXmlDeserializer<>(LineString.class));
            addDeserializer(MultiPolygon.class, new GMLGeoToolsXmlDeserializer<>(MultiPolygon.class));
            addDeserializer(Envelope.class, new GMLGeoToolsXmlDeserializer<>(Envelope.class));

            addSerializer(Voidable.class, (JsonSerializer) new VoidableXmlSerializer<>());
            addSerializer(Point.class, new GMLGeoToolsXmlSerializer<>(Point.class, GML._Geometry));
            addSerializer(LineString.class, new GMLGeoToolsXmlSerializer<>(LineString.class, GML._Geometry));
            addSerializer(MultiPolygon.class, new GMLGeoToolsXmlSerializer<>(MultiPolygon.class, GML._Geometry));
            addSerializer(Envelope.class, new GMLGeoToolsXmlSerializer<>(Envelope.class, GML.Envelope));
        }

        private void forceRootElementsSerializerAndDeserializer() {
            setDeserializerModifier(new BeanDeserializerModifier() {



                @Override
                public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
                    final Class<?> clazz = beanDesc.getBeanClass();

                    if (deserializer instanceof BeanDeserializer beanDeserializer && forceRootElements.contains(clazz))
                        return new ForceRootElementBeanDeserializer(beanDeserializer);

                    return deserializer;
                }
            });

            setSerializerModifier(new BeanSerializerModifier() {
                @Override
                public JsonSerializer<?> modifySerializer(SerializationConfig config, BeanDescription beanDesc, JsonSerializer<?> serializer) {
                    final Class<?> clazz = beanDesc.getBeanClass();

                    if (serializer instanceof BeanSerializer beanSerializer && forceRootElements.contains(clazz))
                        return new ForceRootElementBeanSerializer(beanSerializer);

                    return serializer;
                }
            });
        }

        private NamespaceXmlFactory generateNamespaceXmlFactory() {
            return new NamespaceXmlFactory(InspireNamespaces.getNamespacePrefixes());
        }

        public XmlMapper getXmlMapper() {
            XmlMapper xmlMapper = new BufferedXmlMapper(generateNamespaceXmlFactory());
            xmlMapper.registerModules(
                    new JavaTimeModule(),
                    this
            );

            xmlMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

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

            return xmlMapper;
        }
    }

    public static InspireDefaultModule getModuleWithDefaults() {
        return new InspireDefaultModule();
    }
}
