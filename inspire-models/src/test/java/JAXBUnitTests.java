import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.Setter;
import net.opengis.wfs.FeatureCollectionType;
import net.opengis.wfs.WfsFactory;
import net.opengis.wfs.impl.WfsFactoryImpl;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.feature.type.FeatureTypeFactory;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.wfs.v2_0.WFSConfiguration;
import org.geotools.xsd.Encoder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ro.negru.mihai.application.schema.administrativeunits.codelist.LegalStatusValue;
import ro.negru.mihai.application.schema.administrativeunits.datatype.ResidenceOfAuthority;
import ro.negru.mihai.application.schema.administrativeunits.featuretype.AdministrativeBoundary;
import ro.negru.mihai.application.schema.administrativeunits.featuretype.Condominium;
import ro.negru.mihai.application.schema.geographicalnames.datatype.GeographicalName;
import ro.negru.mihai.base.featuretype.FeatureCollection;
import ro.negru.mihai.base.stereotype.Voidable;
import ro.negru.mihai.xml.xmladapter.XmlUtils;

import javax.xml.namespace.QName;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

public class JAXBUnitTests {
    static XmlMapper xmlMapper;

    @BeforeAll
    static void setUp() {
        xmlMapper = XmlUtils.getModuleWithDefaults().getPrettyXmlMapper();
    }

    public <T> T generateValue(final String inputFile, Class<T> clazz) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(inputFile)) {
            is.mark(Integer.MAX_VALUE);
            return xmlMapper.readValue(is, clazz);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return null;
    }

    public void writeToOutputFile(final String outputFile, final Object input) throws Exception{
        try (OutputStream out = Files.newOutputStream(new File(outputFile).toPath())) {
            xmlMapper.writeValue(out, input);
        }
    }

    @Test
    public void testCondominium() throws Exception {
        FeatureCollection v = generateValue("condominium.xml", FeatureCollection.class);
        writeToOutputFile("target/condominium.xml", v);
    }

    @Test
    public void testGeoName() throws Exception {
        GeographicalName v = generateValue("geoname.xml", GeographicalName.class);
        writeToOutputFile("target/geoname.xml", v);
    }

    @Test
    public void testResidenceOfAuthority() throws Exception {
        ResidenceOfAuthority v = generateValue("residence.xml", ResidenceOfAuthority.class);

        System.err.println(v.toString());

        writeToOutputFile("target/residence.xml", v);
    }

    @Test
    public void testFeatureType() throws Exception {
        FeatureCollection v = generateValue("feature.xml", FeatureCollection.class);

        System.err.println(v.toString());

        writeToOutputFile("target/feature.xml", v);
    }

    @Test
    public void testFeatureSpellingType() throws Exception {
        FeatureCollection v = generateValue("feature-spelling.xml", FeatureCollection.class);

        System.err.println(v.toString());

        writeToOutputFile("target/feature-spelling.xml", v);
    }

    @Test
    public void testBoundary() throws Exception {
        FeatureCollection v = generateValue("boundary.xml", FeatureCollection.class);

        System.err.println(v.toString());

        writeToOutputFile("target/boundary.xml", v);
    }
}
