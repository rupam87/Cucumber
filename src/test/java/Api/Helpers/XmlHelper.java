package Api.Helpers;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

public class XmlHelper {

	Document xDom = null;
	DocumentBuilderFactory dFactory = null;
	DocumentBuilder dBuilder = null;
	XPathFactory xFactory = null;
	XPath xpath = null;
	TransformerFactory tf = null;
	Transformer transformer = null;

	/*
	 * Gets org.w3c.dom.Document object for the XML file. Creates a new one if
	 * there is none existing already
	 */
	public Document CreateXDocument(File xmlFile) throws Exception {
		if (xDom == null) {
			dFactory = DocumentBuilderFactory.newInstance();
			dFactory.setNamespaceAware(true);
			DocumentBuilder dBuilder = dFactory.newDocumentBuilder();
			xDom = dBuilder.parse(xmlFile);
			xDom.getDocumentElement().normalize();
		}
		return xDom;
	}

	/*
	 * Gets org.w3c.dom.Document object for the XML file contents. Creates a new
	 * one if there is none existing already
	 */
	public Document CreateXDocument(String xmlFileContents) throws Exception {
		if (xDom == null) {
			dFactory = DocumentBuilderFactory.newInstance();
			dFactory.setNamespaceAware(true);
			DocumentBuilder dBuilder = dFactory.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(xmlFileContents));
			xDom = dBuilder.parse(is);
			xDom.getDocumentElement().normalize();
		}
		return xDom;
	}

	public Document GetXDocument() {
		return xDom;
	}

	/*
	 * Retrieve text values from the specified Document for the Xpath provided
	 */
	public List<String> GetNodeValues(Document xDom, String xpathForNodeToFind) throws Exception {
		List<String> values = new ArrayList<String>();
		System.out.println("xpath for node to find: " + xpathForNodeToFind);
		XPathExpression expr = CreateXExpr(xpathForNodeToFind);

		NodeList nodes = (NodeList) expr.evaluate(xDom, XPathConstants.NODESET);
		
		for (int i = 0; i < nodes.getLength(); i++) {
			System.out.println("Printing node txt content for finding " + nodes.item(i).getTextContent());
			values.add(nodes.item(i).getTextContent());
		}

		return values;
	}

	/*
	 * Sets the value of a particular XML Node specified by Xpath in the
	 * Document
	 */
	public void SetNodeValue(Document xDom, String xpathForNode, String valueToSet) throws Exception {
		XPathExpression expr = CreateXExpr(xpathForNode);
		NodeList nodes = (NodeList) expr.evaluate(xDom, XPathConstants.NODESET);
		nodes.item(0).setTextContent(valueToSet);
	}

	/*
	 * Gets the string equivalent of the XML Document object
	 */
	public String ConvertToString(Document xDom) throws Exception {
		if (tf == null)
			tf = TransformerFactory.newInstance();
		transformer = tf.newTransformer();
		StringWriter writer = new StringWriter();
		// transform document to string
		transformer.transform(new DOMSource(xDom), new StreamResult(writer));
		return writer.getBuffer().toString();
	}

	/*
	 * Dispose off all instances of Xdocument related objects
	 */
	public void dispose() {
		xDom = null;
		dFactory = null;
		dBuilder = null;
		xFactory = null;
		xpath = null;
		tf = null;
		transformer = null;
	}

	/*
	 * Private method to create Xpath Expression from the string Xpath
	 */
	private XPathExpression CreateXExpr(String xpathExpr) throws XPathExpressionException {
		if (xFactory == null)
			xFactory = XPathFactory.newInstance();
		xpath = xFactory.newXPath();
		xpath.setNamespaceContext(new MyNamespaceContext());
		return xpath.compile(xpathExpr);
	}

	/*
	 * To handle XML Namespace which are present by default we need to implement
	 * the NameSpaceContext interface and match the ns from input xpath to
	 * replace in actual query
	 */
	private static class MyNamespaceContext implements NamespaceContext {

		@Override
		public String getNamespaceURI(String prefix) {
			if ("ns:soapenv".equals(prefix)) {
				return "http://schemas.xmlsoap.org/soap/envelope/";
			}
			if ("ns:tem".equals(prefix)) {
				return "http://tempuri.org/";
			}
			if ("ns:soap".equals(prefix)) {
				return "http://schemas.xmlsoap.org/soap/envelope/";
			}
			return null;
		}

		@Override
		public String getPrefix(String namespaceURI) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Iterator getPrefixes(String namespaceURI) {
			// TODO Auto-generated method stub
			return null;
		}

	}
}
