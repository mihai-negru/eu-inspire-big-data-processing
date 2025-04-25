package ro.negru.mihai.xml.namespace;

import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.util.StaxUtil;
import jakarta.validation.constraints.NotNull;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Map;

public class NamespaceXmlFactory extends XmlFactory {
    private final Map<String, String> prefixToNs;

    public NamespaceXmlFactory(@NotNull Map<String, String> prefixToNs) {
        this.prefixToNs = prefixToNs;
    }

    @Override
    protected XMLStreamWriter _createXmlWriter(IOContext ctx, Writer w) throws IOException {
        return applyNamespacePrefixes(super._createXmlWriter(ctx, w));
    }

    @Override
    protected XMLStreamWriter _createXmlWriter(IOContext ctx, OutputStream out) throws IOException {
        return applyNamespacePrefixes(super._createXmlWriter(ctx, out));
    }

    private XMLStreamWriter applyNamespacePrefixes(XMLStreamWriter writer) throws IOException {
        try {
            for (Map.Entry<String, String> entry : prefixToNs.entrySet()) {
                writer.setPrefix(entry.getKey(), entry.getValue());
            }
        } catch (XMLStreamException e) {
            StaxUtil.throwAsGenerationException(e, null);
        }
        return writer;
    }


}
