/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.base.data;

import de.elbe5.base.log.Log;
import de.elbe5.base.util.StringUtil;
import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class XmlData {

    public static String STD_ENCODING = "UTF-8";
    public static String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    public static String XML_HEADER_START = "<?xml ";

    private static DateTimeFormatter datetimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static XmlData create() {
        XmlData data = new XmlData();
        if (!data.initialize())
            return null;
        data.createDocument();
        return data;
    }

    public static XmlData create(String xml) {
        return create(xml, STD_ENCODING);
    }

    public static XmlData create(String xml, String encoding) {
        if (xml == null || !xml.startsWith(XML_HEADER_START)) {
            return null;
        }
        XmlData data = new XmlData();
        if (!data.initialize() || !data.createDocument(xml, encoding))
            return null;
        return data;
    }

    private DocumentBuilder builder;
    private Document doc;

    private XmlData() {

    }

    private boolean initialize() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            return false;
        }
        return true;
    }

    private void createDocument() {
        doc = builder.newDocument();
    }

    private boolean createDocument(String xml, String encoding) {
        try {
            doc = builder.parse(new ByteArrayInputStream(xml.getBytes(encoding)));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public Element createRootNode(String rootName) {
        Element rootNode = doc.createElement(rootName);
        doc.appendChild(rootNode);
        return rootNode;
    }

    public Element setRootNode(Element rootNode) {
        doc.appendChild(rootNode);
        return rootNode;
    }

    public Element importElement(Element externalNode, boolean deep) {
        return (Element) doc.importNode(externalNode, deep);
    }

    public Element addNode(Element parentNode, String name) {
        Element childNode = doc.createElement(name);
        parentNode.appendChild(childNode);
        return childNode;
    }

    public Element replaceNode(Element oldNode, String name) {
        Element newNode = doc.createElement(name);
        oldNode.getParentNode().replaceChild(newNode, oldNode);
        return newNode;
    }

    public void addText(Element parentNode, String name, String text) {
        Element childNode = addNode(parentNode, name);
        childNode.appendChild(doc.createTextNode(StringUtil.toXml(text)));
    }

    public Text replaceWithText(Element oldNode, String text) {
        Text newNode = doc.createTextNode(StringUtil.toXml(text));
        oldNode.getParentNode().replaceChild(newNode, oldNode);
        return newNode;
    }

    public void addCDATA(Element parentNode, String content) {
        CDATASection cds = doc.createCDATASection(content == null ? "" : content.replaceAll("\r", ""));
        parentNode.appendChild(cds);
    }

    public void addCDATA(Element node, byte[] bytes) {
        CDATASection cds = doc.createCDATASection(bytes == null ? "" : Base64.encodeBase64String(bytes));
        node.appendChild(cds);
    }

    public void addAttribute(Element node, String key, String value) {
        Attr attr = doc.createAttribute(key);
        attr.setNodeValue(value == null ? "" : value);
        node.setAttributeNode(attr);
    }

    public void addIntAttribute(Element node, String key, int value) {

        addAttribute(node, key, Integer.toString(value));
    }

    public void addLongAttribute(Element node, String key, long value) {

        addAttribute(node, key, Long.toString(value));
    }

    public void addBooleanAttribute(Element node, String key, boolean value) {

        addAttribute(node, key, Boolean.toString(value));
    }

    public void addDateAttribute(Element node, String key, LocalDateTime date) {
        if (date != null) {
            addAttribute(node, key, date.format(datetimeFormatter));
        }
    }

    public void addLocaleAttribute(Element node, String key, Locale locale) {
        if (locale != null) {
            addAttribute(node, key, locale.getLanguage());
        }
    }

    public Element getRootNode() {
        return doc.getDocumentElement();
    }

    public List<Element> getChildElements(Element parent) {
        ArrayList<Element> list = new ArrayList<>();
        if (parent.hasChildNodes()) {
            NodeList childNodes = parent.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node node = childNodes.item(i);
                if (node instanceof Element)
                    list.add((Element) node);
            }
        }
        return list;
    }

    public List<Element> findChildElements(Element parent, String tagName, boolean recursive) {
        ArrayList<Element> list = new ArrayList<>();
        findChildElements(parent, tagName, recursive, list);
        return list;
    }

    private void findChildElements(Node parent, String tagName, boolean recursive, List<Element> list) {
        if (parent.hasChildNodes()) {
            NodeList childNodes = parent.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node node = childNodes.item(i);
                if ((node instanceof Element) && ((Element) node).getTagName().equals(tagName))
                    list.add((Element) node);
                if (recursive) {
                    findChildElements(node, tagName, true, list);
                }
            }
        }
    }

    public Map<String, String> getAttributes(Node node) {
        Map<String, String> map = new HashMap<>();
        if (node.hasAttributes()) {
            NamedNodeMap attrMap = node.getAttributes();
            for (int i = 0; i < attrMap.getLength(); i++) {
                Node attr = attrMap.item(i);
                map.put(attr.getNodeName(), attr.getNodeValue());
            }
        }
        return map;
    }

    public String getStringAttribute(Node node, String key) {
        if (node.hasAttributes()) {
            NamedNodeMap attrMap = node.getAttributes();
            Node attr = attrMap.getNamedItem(key);
            if (attr != null) {
                return attr.getNodeValue();
            }
        }
        return "";
    }

    public int getIntAttribute(Node node, String key) {
        int result = -1;
        if (node.hasAttributes()) {
            NamedNodeMap attrMap = node.getAttributes();
            Node attr = attrMap.getNamedItem(key);
            if (attr != null) {
                try {
                    result = Integer.parseInt(attr.getNodeValue());
                } catch (Exception ignored) {
                }
            }
        }
        return result;
    }

    public long getLongAttribute(Node node, String key) {
        long result = -1;
        if (node.hasAttributes()) {
            NamedNodeMap attrMap = node.getAttributes();
            Node attr = attrMap.getNamedItem(key);
            if (attr != null) {
                try {
                    result = Long.parseLong(attr.getNodeValue());
                } catch (Exception ignored) {
                }
            }
        }
        return result;
    }

    public boolean getBooleanAttribute(Node node, String key) {
        boolean result = false;
        if (node.hasAttributes()) {
            NamedNodeMap attrMap = node.getAttributes();
            Node attr = attrMap.getNamedItem(key);
            if (attr != null) {
                try {
                    result = Boolean.parseBoolean(attr.getNodeValue());
                } catch (Exception ignored) {
                }
            }
        }
        return result;
    }

    public LocalDateTime getDateAttribute(Node node, String key) {
        LocalDateTime result = null;
        if (node.hasAttributes()) {
            NamedNodeMap attrMap = node.getAttributes();
            Node attr = attrMap.getNamedItem(key);
            if (attr != null) {
                try {
                    result = LocalDateTime.parse(attr.getNodeValue(), datetimeFormatter);
                } catch (Exception ignored) {
                }
            }
        }
        return result;
    }

    public Locale getLocaleAttribute(Node node, String key) {
        Locale result = Locale.getDefault();
        if (node.hasAttributes()) {
            NamedNodeMap attrMap = node.getAttributes();
            Node attr = attrMap.getNamedItem(key);
            if (attr != null) {
                try {
                    result = new Locale(attr.getNodeValue());
                } catch (Exception ignored) {
                }
            }
        }
        return result;
    }

    public String getText(Element node) {
        if (node.hasChildNodes()) {
            Node child = node.getFirstChild();
            while (child != null) {
                if (child.getNodeType() == Node.TEXT_NODE) {
                    return child.getNodeValue();
                }
                child = child.getNextSibling();
            }
        }
        return "";
    }

    public String getCData(Node node) {
        if (node.hasChildNodes()) {
            Node child = node.getFirstChild();
            while (child != null) {
                if (child instanceof CDATASection) {
                    return ((CDATASection) child).getData();
                }
                child = child.getNextSibling();
            }
        }
        return "";
    }

    public Node findSubElement(Node parent, String localName) {
        if (parent == null) {
            return null;
        }
        Node child = parent.getFirstChild();
        while (child != null) {
            if ((child.getNodeType() == Node.ELEMENT_NODE) && (child.getLocalName().equals(localName))) {
                return child;
            }
            child = child.getNextSibling();
        }
        return null;
    }

    public List<String> getPropertiesFromXML(Node propNode) {
        List<String> properties;
        properties = new ArrayList<>();
        NodeList childList = propNode.getChildNodes();
        for (int i = 0; i < childList.getLength(); i++) {
            Node currentNode = childList.item(i);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                String nodeName = currentNode.getLocalName();
                String namespace = currentNode.getNamespaceURI();
                properties.add(namespace + ':' + nodeName);
            }
        }
        return properties;
    }

    public String getContent(Element element) {
        try {
            Source source = new DOMSource(element);
            StringWriter stringWriter = new StringWriter();
            Result result = new StreamResult(stringWriter);
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            transformer.transform(source, result);
            return stringWriter.getBuffer().toString();
        } catch (TransformerException e) {
            Log.error("xml error", e);
        }
        return "";
    }

    public String toString() {
        try {
            Source source = new DOMSource(doc);
            StringWriter stringWriter = new StringWriter();
            Result result = new StreamResult(stringWriter);
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            transformer.transform(source, result);
            return stringWriter.getBuffer().toString();
        } catch (TransformerException e) {
            Log.error("xml error", e);
        }
        return "";
    }

}
