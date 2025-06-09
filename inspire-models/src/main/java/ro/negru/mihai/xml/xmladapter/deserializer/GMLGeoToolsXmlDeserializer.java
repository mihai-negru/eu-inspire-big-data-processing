package ro.negru.mihai.xml.xmladapter.deserializer;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser;
import org.geotools.gml3.GMLConfiguration;
import org.geotools.xsd.Parser;
import ro.negru.mihai.xml.namespace.InspireNamespaces;

import javax.xml.stream.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GMLGeoToolsXmlDeserializer<T> extends StdDeserializer<T> {

    private final Class<T> clazz;
    private final Parser parser;

    public GMLGeoToolsXmlDeserializer(Class<T> clazz) {
        super(clazz);
        this.clazz = clazz;

        parser = new Parser(new GMLConfiguration());
    }

    @Override
    public T deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        FromXmlParser xmlParser = (FromXmlParser) p;

        String rawXml;
        try {
            rawXml = validateRawGMLContent(extractRawElement(xmlParser));
        } catch (XMLStreamException e) {
            throw JsonMappingException.from(p, "Cannot extract the raw xml element", e);
        }

        try {
            Object obj = parser.parse(new StringReader(rawXml));
            if (clazz.isInstance(obj)) {
                return clazz.cast(obj);
            } else
                throw JsonMappingException.from(p, "Expected GeoTools JTS Type, but got: " + (obj == null ? "null" : obj.getClass().getName()));
        } catch (Exception e) {
            throw JsonMappingException.from(p, "Cannot parse the raw xml element", e);
        }
    }

    private String extractRawElement(FromXmlParser xmlParser) throws XMLStreamException, IOException {
        int startOffset = (int) xmlParser.currentTokenLocation().getCharOffset();

        int depth = 1;
        while (depth > 0) {
            JsonToken token = xmlParser.nextToken();

            if (token == JsonToken.START_OBJECT || token == JsonToken.START_ARRAY)
                depth++;
            if (token == JsonToken.END_OBJECT || token == JsonToken.END_ARRAY)
                depth--;
        }

        JsonLocation endLocation = xmlParser.currentTokenLocation();

        int endOffset = (int) endLocation.getCharOffset();
        BufferedInputStream bis = (BufferedInputStream) endLocation.contentReference().getRawContent();

        bis.reset();
        startOffset = (int) bis.skip(startOffset);

        byte[] buffer = new byte[endOffset - startOffset];
        bis.readNBytes(buffer, 0, buffer.length);

        return new String(buffer, StandardCharsets.UTF_8);
    }

    private String validateRawGMLContent(String rawXml) throws IllegalArgumentException {
        if (!rawXml.contains(InspireNamespaces.GML_PREFIX))
            return rawXml;

        Pattern rootPattern = Pattern.compile("<(\\w+:)?\\w+([^>]*)>");
        Matcher matcher = rootPattern.matcher(rawXml);

        if (matcher.find()) {
            String rootTag = matcher.group(0);
            String attributes = matcher.group(2);

            if (!attributes.contains("xmlns:" + InspireNamespaces.GML_PREFIX)) {
                String fixedRoot = rootTag.replaceFirst(">", " xmlns:" + InspireNamespaces.GML_PREFIX + "=\"" + InspireNamespaces.GML + "\">");
                return rawXml.replaceFirst(Pattern.quote(rootTag), Matcher.quoteReplacement(fixedRoot));
            }

            return rawXml;
        }

        throw new IllegalArgumentException("Could not set a GML namespace to the raw xml section");
    }
}
