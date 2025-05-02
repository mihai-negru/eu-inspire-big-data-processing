import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import ro.negru.mihai.application.schema.administrativeunits.datatype.ResidenceOfAuthority;
import ro.negru.mihai.application.schema.geographicalnames.datatype.GeographicalName;
import ro.negru.mihai.base.featuretype.FeatureCollection;
import ro.negru.mihai.xml.xmladapter.XmlUtils;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.charset.StandardCharsets;
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

    public void writeToOutputFile(final String outputFile, final Object input) throws Exception {
        StringWriter stringWriter = new StringWriter();
        xmlMapper.writeValue(stringWriter, input);
        String rawXml = stringWriter.toString();

        rawXml = prettyFormatXml(rawXml);

        try (OutputStream out = Files.newOutputStream(new File(outputFile).toPath())) {
            out.write(rawXml.getBytes(StandardCharsets.UTF_8));
        }
    }

    private static String prettyFormatXml(String inputXml) throws Exception {
        try {
            InputSource src = new InputSource(new StringReader(inputXml));
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(src);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", 4);
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            Writer out = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(out));
            return out.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error occurs when pretty-printing xml:\n", e);
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
