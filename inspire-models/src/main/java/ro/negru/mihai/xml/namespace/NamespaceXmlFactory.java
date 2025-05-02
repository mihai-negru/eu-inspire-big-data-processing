package ro.negru.mihai.xml.namespace;

import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import jakarta.validation.constraints.NotNull;
import org.codehaus.stax2.XMLStreamWriter2;

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

    private XMLStreamWriter applyNamespacePrefixes(XMLStreamWriter writer) {
        if (writer instanceof XMLStreamWriter2 writer2)
            return new NamespaceStreamWriter(writer2, prefixToNs);

        return writer;
    }
}
