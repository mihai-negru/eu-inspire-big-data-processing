package ro.negru.mihai.xml.xmladapter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.geotools.geometry.jts.MultiSurface;
import org.geotools.gml3.GML;
import org.geotools.xsd.Encoder;
import org.geotools.xsd.Parser;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import ro.negru.mihai.base.featuretype.Feature;
import ro.negru.mihai.base.featuretype.FeatureCollection;
import ro.negru.mihai.base.featuretype.features.administrativeunits.FCAdministrativeBoundary;
import ro.negru.mihai.base.featuretype.features.administrativeunits.FCAdministrativeUnit;
import ro.negru.mihai.base.featuretype.features.administrativeunits.FCCondominium;
import ro.negru.mihai.base.featuretype.features.administrativeunits.FCResidenceOfAuthority;
import ro.negru.mihai.base.featuretype.features.geographicalnames.FCGeographicalName;
import ro.negru.mihai.base.featuretype.features.geographicalnames.FCPronunciationOfName;
import ro.negru.mihai.base.featuretype.features.geographicalnames.FCSpellingOfName;
import ro.negru.mihai.base.stereotype.Voidable;
import ro.negru.mihai.xml.namespace.InspireNamespaces;
import ro.negru.mihai.xml.namespace.NamespaceXmlFactory;
import ro.negru.mihai.xml.xmladapter.deserializer.GMLGeoToolsXmlDeserializer;
import ro.negru.mihai.xml.xmladapter.deserializer.VoidableXmlDeserializer;
import ro.negru.mihai.xml.xmladapter.serializer.GMLGeoToolsXmlSerializer;
import ro.negru.mihai.xml.xmladapter.serializer.VoidableXmlSerializer;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

public final class XmlUtils {
    private XmlUtils() {}

    private final static List<Class<?>> FEATURES;
    static {
        FEATURES = List.of(
                FCAdministrativeBoundary.class,
                FCAdministrativeUnit.class,
                FCCondominium.class,
                FCResidenceOfAuthority.class,
                FCGeographicalName.class,
                FCPronunciationOfName.class,
                FCSpellingOfName.class
        );
    }

    public static class InspireXmlMapper extends XmlMapper {

        public InspireXmlMapper(XmlFactory xmlFactory) {
            super(xmlFactory);
        }

        @Override
        public <T> T readValue(InputStream src, Class<T> valueType) throws IOException {

            BufferedInputStream buffSrc = new BufferedInputStream(src);

            buffSrc.mark(Integer.MAX_VALUE);
            return super.readValue(buffSrc, valueType);
        }

        public <T extends Feature> FeatureCollection<T> readFeature(InputStream src, Class<FeatureCollection<T>> valueType) throws IOException {
            return readValue(src, valueType);
        }

        @SuppressWarnings("unchecked")
        public <T extends Feature> FeatureCollection<T> readFeature(InputStream src, String valueType) throws IOException {
            Class<FeatureCollection<T>> clazz = null;
            for (Class<?> fClazz : FEATURES) {
                if (fClazz.getSimpleName().equals(valueType)) {
                    clazz = (Class<FeatureCollection<T>>) fClazz;
                    break;
                }
            }

            if (clazz == null) return null;
            return readFeature(src, clazz);
        }
    }

    public static class InspireDefaultModule extends SimpleModule {
        private InspireDefaultModule() {
            super("InspireDefaultModule");

            setCustomSerializersAndDeserializers();

        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        private void setCustomSerializersAndDeserializers() {
            addDeserializer(Voidable.class, new VoidableXmlDeserializer<>());
            addDeserializer(Point.class, new GMLGeoToolsXmlDeserializer<>(Point.class, new Parser(new org.geotools.gml2.GMLConfiguration())));
            addDeserializer(LineString.class, new GMLGeoToolsXmlDeserializer<>(LineString.class, new Parser(new org.geotools.gml3.GMLConfiguration())));
            addDeserializer(MultiPolygon.class, new GMLGeoToolsXmlDeserializer<>(MultiPolygon.class, new Parser(new org.geotools.gml2.GMLConfiguration())));
            addDeserializer(MultiSurface.class, new GMLGeoToolsXmlDeserializer<>(MultiSurface.class, new Parser(new org.geotools.gml3.GMLConfiguration())));
            addDeserializer(Envelope.class, new GMLGeoToolsXmlDeserializer<>(Envelope.class, new Parser(new org.geotools.gml3.GMLConfiguration())));

            addSerializer(Voidable.class, (JsonSerializer) new VoidableXmlSerializer<>());
            addSerializer(Point.class, new GMLGeoToolsXmlSerializer<>(Point.class, GML._Geometry, new Encoder(new org.geotools.gml2.GMLConfiguration())));
            addSerializer(LineString.class, new GMLGeoToolsXmlSerializer<>(LineString.class, GML._Geometry, new Encoder(new org.geotools.gml3.GMLConfiguration())));
            addSerializer(MultiPolygon.class, new GMLGeoToolsXmlSerializer<>(MultiPolygon.class, GML._Geometry, new Encoder(new org.geotools.gml2.GMLConfiguration())));
            addSerializer(MultiSurface.class, new GMLGeoToolsXmlSerializer<>(MultiSurface.class, GML._Geometry, new Encoder(new org.geotools.gml3.GMLConfiguration())));
            addSerializer(Envelope.class, new GMLGeoToolsXmlSerializer<>(Envelope.class, GML.Envelope, new Encoder(new org.geotools.gml3.GMLConfiguration())));
        }

        private NamespaceXmlFactory generateNamespaceXmlFactory() {
            return new NamespaceXmlFactory(InspireNamespaces.getNamespacePrefixes());
        }

        public InspireXmlMapper getXmlMapper() {
            InspireXmlMapper xmlMapper = new InspireXmlMapper(generateNamespaceXmlFactory());
            xmlMapper.registerModules(
                    new JavaTimeModule(),
                    this
            );

            xmlMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            xmlMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            xmlMapper.enable(ToXmlGenerator.Feature.WRITE_XML_DECLARATION);
            xmlMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            return xmlMapper;
        }

        public InspireXmlMapper getPrettyXmlMapper() {
            InspireXmlMapper xmlMapper = new InspireXmlMapper(generateNamespaceXmlFactory());
            xmlMapper.registerModules(
                    new JavaTimeModule(),
                    this);

            xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
            xmlMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            xmlMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            xmlMapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true);

            return xmlMapper;
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class XmlPath {
        private String path;
        private long size;
    }

    public static InspireDefaultModule getModuleWithDefaults() {
        return new InspireDefaultModule();
    }

    public static List<String> getAvailableSchemas() {
        return FEATURES.stream().map(Class::getSimpleName).collect(Collectors.toList());
    }

    public static XmlPath computeFeaturePath(final String reducedPath) {
        if (reducedPath == null || reducedPath.isBlank()) return new XmlPath("", 0L);

        final String[] parts = reducedPath.split("\\.");
        final StringBuilder path = new StringBuilder();

        long pathSize = 0;
        path.append("holder");
        for (int i = 0; i < parts.length - 1; ++i) {
            if ("voidValue".equals(parts[i + 1]) || "voidReason".equals(parts[i + 1])) {
                path.append('.').append(parts[i]);
                --pathSize;
            } else {
                path.append('.').append(parts[i]).append(".holder");
            }
        }

        if ("voidValue".equals(parts[parts.length - 1]) || "voidReason".equals(parts[parts.length - 1]) ) {
            path.append('.').append(parts[parts.length - 1]);
        } else {
            path.append('.').append(parts[parts.length - 1]).append(".holder");
        }

        pathSize += parts.length;
        return new XmlPath(path.toString(), pathSize);
    }

    public static <T extends Feature> T getFeatureFromCollection(final FeatureCollection<T> featureCollection) {
        List<T> members = featureCollection.getMember();
        if (members == null || members.isEmpty()) {
            return null;
        }

        return members.get(0);
    }
}
