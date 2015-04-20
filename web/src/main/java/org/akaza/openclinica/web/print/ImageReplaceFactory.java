package org.akaza.openclinica.web.print;

import com.lowagie.text.Image;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.w3c.dom.Element;
import org.xhtmlrenderer.extend.FSImage;
import org.xhtmlrenderer.extend.ReplacedElement;
import org.xhtmlrenderer.extend.ReplacedElementFactory;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.pdf.ITextFSImage;
import org.xhtmlrenderer.pdf.ITextImageElement;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.extend.FormSubmissionListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * ImageReplaceFactory for correct image embed in the subject casebook pdf.
 */
public class ImageReplaceFactory implements ReplacedElementFactory {

    private final ReplacedElementFactory superFactory;

    /**
     * ImageReplacedFactory constructor.
     *
     * @param superFactory the super object.
     */
    public ImageReplaceFactory(ReplacedElementFactory superFactory) {
        this.superFactory = superFactory;
    }

    /**
     * @param layoutContext tracks state which changes over the course of a layout run.
     * @param blockBox the  block box as defined in the CSS spec.
     * @param userAgentCallback object that responsible for retrieving external resources.
     * @param cssWidth the css width parameter.
     * @param cssHeight the css height parameter.
     * @return the replaced img object.
     */
    public ReplacedElement createReplacedElement(LayoutContext layoutContext, BlockBox blockBox, UserAgentCallback userAgentCallback, int cssWidth, int cssHeight) {
        Element element = blockBox.getElement();
        if (element == null) {
            return null;
        }
        String nodeName = element.getNodeName();
        if ("img".equals(nodeName)) {
            InputStream input = null;
            try {
                Resource resource = new DefaultResourceLoader().getResource("..".concat(File.separator).concat("..")
                        .concat(File.separator).concat("images"));
                if (resource.exists()) {
                    for (String fileName : resource.getFile().list()) {
                        String src = element.getAttribute("src");
                        if (src.contains(fileName)) {
                            input = new FileInputStream(resource.getFile() + File.separator + src.substring(src.lastIndexOf("/"), src.length()));
                        }
                    }
                    final byte[] bytes = IOUtils.toByteArray(input);
                    final Image image = Image.getInstance(bytes);
                    final FSImage fsImage = new ITextFSImage(image);
                    if (fsImage != null) {
                        if ((cssWidth != -1) || (cssHeight != -1)) {
                            fsImage.scale(cssWidth, cssHeight);
                        }
                        return new ITextImageElement(fsImage);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("There was a problem trying to read a template embedded graphic.", e);
            } finally {
                IOUtils.closeQuietly(input);
            }
        }
        return this.superFactory.createReplacedElement(layoutContext, blockBox, userAgentCallback, cssWidth, cssHeight);
    }

    /**
     * Discard any cached data.
     */
    public void reset() {
        this.superFactory.reset();
    }

    /**
     * Removes any reference to e.
     * @param e the Element.
     */
    public void remove(Element e) {
        this.superFactory.remove(e);
    }

    /**
     * Set listener to the current session factory.
     *
     * @param listener listener object.
     */
    public void setFormSubmissionListener(FormSubmissionListener listener) {
        this.superFactory.setFormSubmissionListener(listener);
    }
}
