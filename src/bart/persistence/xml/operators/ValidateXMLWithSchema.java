package bart.persistence.xml.operators;

import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.xerces.parsers.SAXParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

class ValidateXMLWithSchema {

    void validateXerces(String SchemaUrl, String XmlDocumentUrl) {
        XercesValidator validator = new XercesValidator();
        validator.validateSchema(SchemaUrl, XmlDocumentUrl);
    }

    void validateJAXP(String SchemaUrl, String XmlDocumentUrl) {
        JAXPValidator validator = new JAXPValidator();
        validator.validateSchema(SchemaUrl, XmlDocumentUrl);
    }
}

class XercesValidator {

    private static Logger logger = LoggerFactory.getLogger(XercesValidator.class);

    void validateSchema(String SchemaUrl, String XmlDocumentUrl) {
        SAXParser parser = new SAXParser();
        try {
            parser.setFeature("http://xml.org/sax/features/validation", true);
            parser.setFeature("http://apache.org/xml/features/validation/schema", true);
            parser.setFeature("http://apache.org/xml/features/validation/schema-full-checking",
                    true);
            parser.setProperty("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation", SchemaUrl);
            Validator handler = new Validator();
            parser.setErrorHandler(handler);
            parser.parse(XmlDocumentUrl);
            if (handler.validationError == true) {
                logger.error("XML Document has Error:" + handler.validationError + "" + handler.saxParseException.getMessage());
            }
        } catch (java.io.IOException ioe) {
            logger.error("IOException" + ioe.getMessage());
        } catch (SAXException e) {
            logger.error("SAXException" + e.getMessage());
        }
    }

    private class Validator extends DefaultHandler {

        public boolean validationError = false;
        public SAXParseException saxParseException = null;

        public void error(SAXParseException exception) throws SAXException {
            validationError = true;
            saxParseException = exception;
        }

        public void fatalError(SAXParseException exception) throws SAXException {
            validationError = true;
            saxParseException = exception;
        }

        public void warning(SAXParseException exception) throws SAXException {
        }
    }
}

class JAXPValidator {
    
    private static Logger logger = LoggerFactory.getLogger(JAXPValidator.class);

    void validateSchema(String SchemaUrl, String XmlDocumentUrl) {
        try {
            System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
                    "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setValidating(true);
            factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage",
                    "http://www.w3.org/2001/XMLSchema");
            factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource", SchemaUrl);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Validator handler = new Validator();
            builder.setErrorHandler(handler);
            builder.parse(XmlDocumentUrl);
            if (handler.validationError == true) {
                logger.error("XML Document has Error:" + handler.validationError + " " + handler.saxParseException.getMessage());
            }
        } catch (IOException ioe) {
            logger.error("IOException " + ioe.getMessage());
        } catch (SAXException e) {
            logger.error("SAXException" + e.getMessage());
        } catch (ParserConfigurationException e) {
            logger.error("ParserConfigurationException                    " + e.getMessage());
        }
    }

    private class Validator extends DefaultHandler {

        public boolean validationError = false;
        public SAXParseException saxParseException = null;

        public void error(SAXParseException exception) throws SAXException {
            validationError = true;
            saxParseException = exception;
        }

        public void fatalError(SAXParseException exception) throws SAXException {
            validationError = true;
            saxParseException = exception;
        }

        public void warning(SAXParseException exception) throws SAXException {
        }
    }
}