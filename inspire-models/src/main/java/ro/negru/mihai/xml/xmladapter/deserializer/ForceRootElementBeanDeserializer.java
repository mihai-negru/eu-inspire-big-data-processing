package ro.negru.mihai.xml.xmladapter.deserializer;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.BeanDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBase;
import com.fasterxml.jackson.databind.deser.ResolvableDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;

public class ForceRootElementBeanDeserializer<T> extends StdDeserializer<T> implements ResolvableDeserializer {
    private final Class<T> clazz;
    private final BeanDeserializer beanDeserializer;
    private final QName wrapperName;

    public ForceRootElementBeanDeserializer(Class<T> clazz, BeanDeserializer beanDeserializer) {
        super(clazz);

        this.clazz = clazz;
        this.beanDeserializer = beanDeserializer;

        JacksonXmlRootElement root = clazz.getAnnotation(JacksonXmlRootElement.class);
        if (root == null)
            throw new IllegalArgumentException("No @JacksonXmlRootElement annotation found on class " + clazz.getSimpleName());

        wrapperName = new QName(root.namespace(), root.localName());
    }

    @Override
    public void resolve(DeserializationContext ctxt) throws JsonMappingException {
        if (beanDeserializer != null)
            beanDeserializer.resolve(ctxt);
    }

    @Override
    public T deserialize(JsonParser p, DeserializationContext ctx) throws IOException, JacksonException {
//        FromXmlParser xmlParser = (FromXmlParser) p;
//        XMLStreamReader reader = xmlParser.getStaxReader();
//
//        if (xmlParser.currentToken() == JsonToken.START_OBJECT)
//            xmlParser.nextToken();
//
//        if (xmlParser.currentToken() == JsonToken.FIELD_NAME)
//            xmlParser.nextToken();
//
//        if (xmlParser.currentToken() == JsonToken.START_OBJECT)
//            xmlParser.nextToken();
//
//        if (xmlParser.currentToken() == JsonToken.FIELD_NAME)
//            xmlParser.nextToken();
//
//        Object value = clazz.cast(beanDeserializer.deserialize(xmlParser, ctx));
//
//        if (xmlParser.currentToken() == JsonToken.END_OBJECT)
//            xmlParser.nextToken();
//
//        return clazz.cast(value);
        return clazz.cast(beanDeserializer.deserialize(p, ctx));
    }

    //    @Override
//    public Object deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
////        if (p.isExpectedStartObjectToken()) {
////            JacksonXmlRootElement root = handledType().getAnnotation(JacksonXmlRootElement.class);
////            if (root == null)
////                throw JsonMappingException.from(p, "The current element is not a root element");
////
////            final String expectedRootName = root.localName();
////            final String parentContextName = getContextParentName(p);
////            if (expectedRootName.equals(parentContextName)) {
////                Object value =  super.deserialize(p, ctx);
////                return value;
////            }
////
////            if (p.nextToken() != JsonToken.FIELD_NAME)
////                throw JsonMappingException.from(p, "Hierarchical structure is not correct, expecting a tag named: " + expectedRootName);
////
////            final String currentRootName = p.currentName();
////            if (!expectedRootName.equals(currentRootName))
////                throw JsonMappingException.from(p, "Expected root name to be " + expectedRootName + ", but was " + currentRootName);
////
////            while (p.currentToken() != null && p.currentToken() != JsonToken.START_OBJECT && p.currentToken() != JsonToken.START_ARRAY)
////                p.nextToken();
////            Object value = super.deserialize(p, ctx);
////            while (p.currentToken() != null && p.currentToken() != JsonToken.END_OBJECT && p.currentToken() != JsonToken.END_ARRAY)
////                p.nextToken();
////
////            return value;
////       }
////
////       return super.deserialize(p, ctx);
//        // Only do anything special if this is actually an XML parser
//        return null;
//    }

//    @Override
//    public Object deserializeWithType(JsonParser p, DeserializationContext ctx, TypeDeserializer typeDeserializer) throws IOException {
//        return deserialize(p, ctx);
//    }

//    private String getContextParentName(JsonParser p) {
//        JsonStreamContext ctx = p.getParsingContext();
//
//        while (ctx != null && ctx.getCurrentName() == null)
//            ctx = ctx.getParent();
//
//        return ctx != null ? ctx.getCurrentName() : null;
//    }
}
