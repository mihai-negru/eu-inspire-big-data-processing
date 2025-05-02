package ro.negru.mihai.xml.xmladapter.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.BeanDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBase;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.io.IOException;

public class ForceRootElementBeanDeserializer extends BeanDeserializer {
    public ForceRootElementBeanDeserializer(BeanDeserializerBase src) {
        super(src);
    }

    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        if (p.isExpectedStartObjectToken()) {
            JacksonXmlRootElement root = handledType().getAnnotation(JacksonXmlRootElement.class);
            if (root == null)
                throw JsonMappingException.from(p, "The current element is not a root element");

            final String expectedRootName = root.localName();
            final String parentContextName = getContextParentName(p);
            if (expectedRootName.equals(parentContextName))
                return super.deserialize(p, ctx);

            if (p.nextToken() != JsonToken.FIELD_NAME)
                throw JsonMappingException.from(p, "Hierarchical structure is not correct, expecting a tag named: " + expectedRootName);

            final String currentRootName = p.currentName();
            if (!expectedRootName.equals(currentRootName))
                throw JsonMappingException.from(p, "Expected root name to be " + expectedRootName + ", but was " + currentRootName);

            p.nextToken();
            Object value = super.deserialize(p, ctx);
            p.nextToken();

            return value;
       }

       return super.deserialize(p, ctx);
    }

    private String getContextParentName(JsonParser p) {
        JsonStreamContext ctx = p.getParsingContext();

        while (ctx != null && ctx.getCurrentName() == null)
            ctx = ctx.getParent();

        return ctx != null ? ctx.getCurrentName() : null;
    }
}
