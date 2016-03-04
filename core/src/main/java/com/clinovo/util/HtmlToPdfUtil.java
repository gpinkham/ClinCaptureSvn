package com.clinovo.util;

import org.akaza.openclinica.print.ImageReplaceFactory;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.PrettyXmlSerializer;
import org.htmlcleaner.TagNode;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.resource.FSEntityResolver;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Util for HTML to PDF converting.
 */
public final class HtmlToPdfUtil {

    /**
     * Gets HTML and returns OutputStream with PDF.
     *
     * @param htmlCode the source html.
     * @param outputStream the source with pdf.
     * @return the OutputStream.
     * @throws Exception for all exceptions.
     */
    public static OutputStream buildPdf(String htmlCode, OutputStream outputStream) throws Exception {

		Document document = getDocument(prepareHtmlForPrint(htmlCode));
        ITextRenderer renderer = new ITextRenderer();
        SharedContext sharedContext = renderer.getSharedContext();
        sharedContext.setReplacedElementFactory(new ImageReplaceFactory(renderer.getSharedContext().getReplacedElementFactory()));
        renderer.setDocument(document, null);
        renderer.layout();
        renderer.createPDF(outputStream);
        outputStream.flush();
        outputStream.close();

        return outputStream;
    }

    private static String prepareHtmlForPrint(String htmlSource) throws IOException {
        String csUrl = "";
        String normalize = "";
        Resource resource = new DefaultResourceLoader().getResource("..".concat(File.separator).concat("..")
                .concat(File.separator).concat("includes").concat(File.separator).concat("css"));
        if (resource.exists()) {
            for (String fileName : resource.getFile().list()) {
                if (fileName.equals("app.css")) {
                    csUrl = readFile(resource.getFile().getAbsolutePath() + File.separator + "app.css", StandardCharsets.UTF_8);
                } else if (fileName.equals("normalize.css")) {
                    normalize = readFile(resource.getFile().getAbsolutePath() + File.separator + "normalize.css", StandardCharsets.UTF_8);
                }
            }
        }
        String mainCss = "<style>" + csUrl + "</style>";
        String normalizeCss = "<style>" + normalize + "</style>";
        String html = "<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />" + mainCss + "\n" + normalizeCss + "</head>";
        html = html.concat(htmlSource);
        HtmlCleaner cleaner = new HtmlCleaner();
        TagNode node = cleaner.clean(html);
        return new PrettyXmlSerializer(cleaner.getProperties()).getAsString(node);
    }

    private static Document getDocument(String htmlContent) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);
        factory.setValidating(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        builder.setEntityResolver(FSEntityResolver.instance());
        return builder.parse(new ByteArrayInputStream(htmlContent.getBytes("UTF-8")));
    }

    static String readFile(String path, Charset encoding)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
}
