package ro.negru.mihai.xml.xmladapter.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import ro.negru.mihai.base.stereotype.Voidable;
import ro.negru.mihai.xml.namespace.InspireNamespaces;

import javax.xml.namespace.QName;
import java.io.IOException;

public class VoidableXmlSerializer<T> extends JsonSerializer<Voidable<T>> implements ContextualSerializer {

    private final JsonSerializer<T> innerSerializer;

    public VoidableXmlSerializer(JsonSerializer<T> innerSerializer) {
        this.innerSerializer = innerSerializer;
    }

    public VoidableXmlSerializer() {
        this(null);
    }

    @Override
    public void serialize(Voidable<T> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        ToXmlGenerator xmlGen = (ToXmlGenerator) gen;

        if (value == null || value.isVoid()) {
            xmlGen.writeStartObject();

            xmlGen.setNextName(new QName(InspireNamespaces.XSI, "nil", InspireNamespaces.XSI_PREFIX));
            xmlGen.setNextIsAttribute(true);
            xmlGen.writeBooleanField("nil", true);
            xmlGen.setNextName(new QName(InspireNamespaces.XSI, "nilReason", InspireNamespaces.XSI_PREFIX));
            xmlGen.setNextIsAttribute(true);
            xmlGen.writeStringField("nilReason", value != null ? value.getReason().value() : "unknown");
            xmlGen.writeEndObject();
        } else {
            innerSerializer.serialize(value.getValue(), gen, serializers);
        }
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        if (property == null)
            return this;

        JavaType voidableWrapper = property.getType();

        while (voidableWrapper.isCollectionLikeType())
            voidableWrapper = voidableWrapper.containedType(0);

        JavaType genericType = voidableWrapper.containedType(0);

        if (genericType == null || Voidable.class.isAssignableFrom(genericType.getRawClass()))
            throw JsonMappingException.from(prov, "Inner type of " + genericType + " is not a Voidable");

        return new VoidableXmlSerializer<>(prov.findValueSerializer(genericType, property));
    }
}
