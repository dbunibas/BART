package bart.persistence.xml;

import bart.exceptions.DAOException;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;

public class DAOXmlUtility {
    
    private static Logger logger = LoggerFactory.getLogger(DAOXmlUtility.class);
    
    public org.jdom.Document buildDOM(String fileName) throws DAOException {
        if (fileName == null || "".equals(fileName)) {
            throw new DAOException("Unable to load file. Null or empty path requested");
        }
        SAXBuilder builder = new SAXBuilder();
        builder.setValidation(false);
        org.jdom.Document document = null;
        try {
            fileName = checkFilePath(fileName);
            document = builder.build(fileName);
            return document;
        } catch (org.jdom.JDOMException jde) {
            logger.error(jde.toString());
            throw new DAOException(jde.getMessage());
        } catch (java.io.IOException ioe) {
            logger.error(ioe.toString());
            throw new DAOException(ioe.getMessage());
        }
    }
    
    public org.jdom.Document buildDOMFromString(String content) throws DAOException {
        if (content == null || "".equals(content)) {
            throw new DAOException("Unable to load xml from empty content.");
        }
        SAXBuilder builder = new SAXBuilder();
        builder.setValidation(false);
        org.jdom.Document document = null;
        try {
            document = builder.build(new ByteArrayInputStream(content.getBytes()));
            return document;
        } catch (org.jdom.JDOMException jde) {
            logger.error(jde.toString());
            throw new DAOException(jde.getMessage());
        } catch (java.io.IOException ioe) {
            logger.error(ioe.toString());
            throw new DAOException(ioe.getMessage());
        }
    }
    
    public static String checkFilePath(String path) {
        if (path != null && !path.startsWith("/")) {
            path = "/" + path;
        }
        return path;
    }
    
    public org.w3c.dom.Document buildNewDOM() throws DAOException {
        org.w3c.dom.Document document = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(true);
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.newDocument();
        } catch (ParserConfigurationException pce) {
            logger.error(pce.toString());
            throw new DAOException(pce.getLocalizedMessage());
        } catch (DOMException doe) {
            logger.error(doe.getLocalizedMessage());
            throw new DAOException(doe.getMessage());
        }
        return document;
    }
    
    public void saveDOM(org.w3c.dom.Document document, String filename) throws DAOException {
        try {
            File file = new java.io.File(filename);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", new Integer(2));
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "2");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);
            
        } catch (Exception ex) {
            logger.error("- Exception in saveDOM: \n" + ex);
            throw new DAOException(ex.getMessage());
        }
    }
    
    public void saveDOM(org.jdom.Document document, String filename) {
        File file = new java.io.File(filename);
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(file));
            XMLOutputter xout = new XMLOutputter(Format.getPrettyFormat());
            xout.output(document, out);
            
        } catch (IOException e) {
            logger.error("Error in producing file: " + e.getMessage());
        }
    }
    
    public static String cleanXmlString(String xmlString) {
        String stringCleaned = xmlString;
        stringCleaned.replaceAll("&gt;", ">");
        stringCleaned.replaceAll("&lt;", "<");
        stringCleaned.replaceAll("&quot;", "\"");
        stringCleaned.replaceAll("&apos;", "'");
        stringCleaned.replaceAll("&amp;", "&");
        
        return stringCleaned;
    }
}
