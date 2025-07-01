import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import ro.negru.mihai.application.schema.administrativeunits.featuretype.AdministrativeBoundary;
import ro.negru.mihai.application.schema.administrativeunits.featuretype.AdministrativeUnit;
import ro.negru.mihai.application.schema.administrativeunits.featuretype.Condominium;
import ro.negru.mihai.application.schema.geographicalnames.datatype.GeographicalName;
import ro.negru.mihai.base.featuretype.Feature;
import ro.negru.mihai.base.featuretype.FeatureCollection;
import ro.negru.mihai.base.featuretype.features.administrativeunits.FCAdministrativeBoundary;
import ro.negru.mihai.base.featuretype.features.administrativeunits.FCAdministrativeUnit;
import ro.negru.mihai.base.featuretype.features.administrativeunits.FCCondominium;
import ro.negru.mihai.base.featuretype.features.geographicalnames.FCGeographicalName;
import ro.negru.mihai.base.stereotype.Voidable;
import ro.negru.mihai.xml.xmladapter.XmlUtils;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JacksonUnitTests {
    static XmlUtils.InspireXmlMapper xmlMapper;

    @BeforeAll
    static void setUp() {
        xmlMapper = XmlUtils.getModuleWithDefaults().getXmlMapper();
    }

    private <T extends Feature> FeatureCollection<T> generateValue(final String inputFile, String schema) {
        FeatureCollection<T> finalValue = assertDoesNotThrow(() -> {
            InputStream is = getClass().getClassLoader().getResourceAsStream(inputFile);
            if (is == null)
                throw new FileNotFoundException(inputFile);

            FeatureCollection<T> value = xmlMapper.readFeature(is, schema);
            is.close();

            return value;
        });

        assertNotNull(finalValue);

        return finalValue;
    }

    public void writeToOutputFile(final String outputFile, final Object inputCollection, boolean prettyPrint) {
        assertDoesNotThrow(() -> {
            final StringWriter stringWriter = new StringWriter();

            xmlMapper.writeValue(stringWriter, inputCollection);

            String processedXmlContent = stringWriter.toString();
            if (prettyPrint) {
                processedXmlContent = prettyFormatXml(processedXmlContent);
            }

            OutputStream out = Files.newOutputStream(new File(outputFile).toPath());

            out.write(processedXmlContent.getBytes(StandardCharsets.UTF_8));

            out.close();
        });
    }

    private static String prettyFormatXml(String inputXml) {
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
    public void condominium() {
        FeatureCollection<Condominium> v = generateValue("condominium-full.xml", FCCondominium.class.getSimpleName());
        writeToOutputFile("target/condominium-full.xml", v, true);
    }

    @Test
    public void combineFeatures() {
        FeatureCollection<GeographicalName> geoName = generateValue("combine-geographical-name.xml", FCGeographicalName.class.getSimpleName());
        FeatureCollection<Condominium> cond = generateValue("combine-condominium.xml", FCCondominium.class.getSimpleName());

        cond.getMember().get(0).getHolder().getName().add(
                Voidable.ofValue(geoName.getMember().get(0))
        );

        writeToOutputFile("target/combine-features.xml", cond, true);
    }

    @Test
    public void testExternFeature() {
        FeatureCollection<AdministrativeBoundary> boundary = generateValue("extern-feature.xml", FCAdministrativeBoundary.class.getSimpleName());
        writeToOutputFile("target/extern-feature.xml", boundary, false);
    }

    @Test
    public void delete() {
        FeatureCollection<AdministrativeBoundary> boundary = generateValue("administrative-boundary.xml", FCAdministrativeBoundary.class.getSimpleName());
        FeatureCollection<AdministrativeUnit> admUnit = generateValue("administrative-unit.xml", FCAdministrativeUnit.class.getSimpleName());
        FeatureCollection<Condominium> condominium = generateValue("condominium.xml", FCCondominium.class.getSimpleName());
        FeatureCollection<GeographicalName> geoName = generateValue("geographical-name.xml", FCGeographicalName.class.getSimpleName());

        assertNotNull(boundary);
        assertNotNull(admUnit);
        assertNotNull(condominium);
        assertNotNull(geoName);
    }
}
