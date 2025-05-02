package ro.negru.mihai.xml.xmladapter.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;

import javax.xml.namespace.QName;
import java.io.IOException;

public class ForceRootElementBeanSerializer extends StdSerializer<Object> /* Cannot use BeanSerializer because does not get called */ {
    private final BeanSerializerBase beanSerializer;

    public ForceRootElementBeanSerializer(BeanSerializerBase src) {
        super(src.handledType());

        this.beanSerializer = src;
    }

    @Override
    public void serialize(Object bean, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (!(gen instanceof ToXmlGenerator xmlGen))
            throw JsonMappingException.from(gen, "Expecting a ToXmlGenerator for serializing a root element");

        JacksonXmlRootElement root = handledType().getAnnotation(JacksonXmlRootElement.class);
        if (root == null)
            throw JsonMappingException.from(gen, "Expecting a JacksonXmlRootElement annotation");

        xmlGen.writeStartObject();
        xmlGen.setNextName(new QName(root.namespace(), root.localName()));
        xmlGen.writeFieldName(root.localName());
        beanSerializer.serialize(bean, gen, provider);
        xmlGen.writeEndObject();
    }

    @Override
    public void serializeWithType(Object bean, JsonGenerator gen, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
        serialize(bean, gen, provider);
    }
}
