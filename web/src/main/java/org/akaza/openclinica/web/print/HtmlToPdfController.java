package org.akaza.openclinica.web.print;

import org.akaza.openclinica.web.SQLInitServlet;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.PrettyXmlSerializer;
import org.htmlcleaner.TagNode;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w3c.dom.Document;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.resource.FSEntityResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Help class for HTML to PDF converting.
 */
@Controller
public class HtmlToPdfController {

    /**
     * Current method gets html source and subject oid and builds pdf file.
     * @param request The request containing the item to code.
     * @param response The response to redirect to.
     * @throws Exception For all exceptions.
     */
    @RequestMapping(value = "/getPdf", method = RequestMethod.POST)
    @ResponseBody
    public void buildPdf(HttpServletResponse response, HttpServletRequest request) throws Exception {

        String pdfName = searchParameter("fileName", request);
        response.setHeader("Content-Disposition", "inline; filename=" + pdfName + "_casebook.pdf");
        response.setContentType("application/pdf;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        Document document = getDocument(prepareHtmlForPrint(request));
        ITextRenderer renderer = new ITextRenderer();
        SharedContext sharedContext = renderer.getSharedContext();
        sharedContext.setReplacedElementFactory(new ImageReplaceFactory(renderer.getSharedContext().getReplacedElementFactory()));
        renderer.setDocument(document, null);
        renderer.layout();

        String isJob = (String) request.getAttribute("isJob");
        if (isJob != null && Boolean.valueOf(isJob)) {
            OutputStream os = new FileOutputStream(getFile(pdfName, "Casebooks"));
            renderer.createPDF(os);
            os.flush();
            os.close();
        } else {
            renderer.createPDF((response.getOutputStream()));
            response.getOutputStream().flush();
            response.getOutputStream().close();
        }

    }

    private String prepareHtmlForPrint(HttpServletRequest request) throws IOException {
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
        String htmlCode = request.getParameter("htmlCode") == null ? (String) request.getAttribute("htmlCode") : request.getParameter("htmlCode");
        html = html.concat(htmlCode);
        HtmlCleaner cleaner = new HtmlCleaner();
        TagNode node = cleaner.clean(html);
        return new PrettyXmlSerializer(cleaner.getProperties()).getAsString(node);
    }

    private String searchParameter(String parameter, HttpServletRequest request) {
        return request.getParameter(parameter) == null ? (String) request.getAttribute(parameter) : request.getParameter(parameter);
    }

    private File getFile(String pdfName, String folderName) throws IOException {
        String datasetFilePath = SQLInitServlet.getField("filePath") + folderName + File.separator + pdfName + ".pdf";
        File file = new File(datasetFilePath);
        if (!file.getParentFile().isDirectory()) {
            file.getParentFile().mkdir();
        }
        file.createNewFile();

        return file;
    }

    private Document getDocument(String htmlContent) throws Exception {
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
