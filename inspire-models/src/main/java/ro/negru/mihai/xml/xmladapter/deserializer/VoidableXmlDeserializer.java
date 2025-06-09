package ro.negru.mihai.xml.xmladapter.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser;
import ro.negru.mihai.base.stereotype.Voidable;

import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class VoidableXmlDeserializer<T> extends JsonDeserializer<Voidable<T>> implements ContextualDeserializer {

    private final JsonDeserializer<T> innerDeserializer;
    private final TypeDeserializer innerTypeDeserializer;

    public VoidableXmlDeserializer() {
        this(null, null);
    }

    public VoidableXmlDeserializer(JsonDeserializer<T> innerDeserializer, TypeDeserializer innerTypeDeserializer) {
        this.innerDeserializer = innerDeserializer;
        this.innerTypeDeserializer = innerTypeDeserializer;
    }

    private Map<String, String> getAttributeValueIgnoreNS(XMLStreamReader reader, Set<String> attrs) {
        int totalAttrs = reader.getAttributeCount();

        Map<String, String> attrsMap = new HashMap<>(totalAttrs);
        for (int attrCount = 0; attrCount < totalAttrs; attrCount++) {
            final String attrLocalName = reader.getAttributeLocalName(attrCount);

            if (attrs.contains(attrLocalName)) {
                attrsMap.put(attrLocalName, reader.getAttributeValue(attrCount));
            }
        }

        return attrsMap;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Voidable<T> deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        FromXmlParser xmlParser = (FromXmlParser) p;

        xmlParser.disable(FromXmlParser.Feature.PROCESS_XSI_NIL);

        XMLStreamReader reader = xmlParser.getStaxReader();

        if (reader.getEventType() == XMLStreamReader.START_ELEMENT) {
            Map<String, String> attrsMap = getAttributeValueIgnoreNS(reader, Set.of("nil", "nilReason"));

            String nilValue = attrsMap.get("nil");
            if ("true".equalsIgnoreCase(nilValue)) {
                String reason = attrsMap.get("nilReason");
                if (reason == null) reason = "unknown";
                p.skipChildren();
                return Voidable.ofVoid(reason);
            }
        }

        final T value;
        if (innerTypeDeserializer != null)
            value = (T) innerTypeDeserializer.deserializeTypedFromAny(p, ctx);
        else if (innerDeserializer != null)
            value = innerDeserializer.deserialize(p, ctx);
        else
            value = null;
        return Voidable.ofValue(value);
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctx, BeanProperty property) throws JsonMappingException {
        if (property == null)
            return this;

        JavaType voidableWrapper = property.getType();

        while (voidableWrapper.isCollectionLikeType())
            voidableWrapper = voidableWrapper.containedType(0);

        JavaType genericType = voidableWrapper.containedType(0);

        if (genericType == null || Voidable.class.isAssignableFrom(genericType.getRawClass()))
            throw JsonMappingException.from(ctx, "Inner type of " + genericType + " is not a Voidable");

        return new VoidableXmlDeserializer<>(ctx.findContextualValueDeserializer(genericType, property), ctx.getConfig().findTypeDeserializer(genericType));
    }
}
