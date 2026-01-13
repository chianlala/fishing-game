/**
 * Alipay.com Inc. Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.jeesite.modules.api.pay.util;

import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * 进行签名验签的测试
 *
 * @author jin.xie
 * @version $Id: XmlSignTest.java, v 0.1 2016年2月1日 上午10:51:39 jin.xie Exp $
 */
public class XmlSignUtil {

    private static final String CHARSET = "UTF-8";
    private static final String SIGN_ALGORITHM = "SHA256withRSA";
    private static final String XML_ALGORITHM = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
    private static final String REQUEST_TAG_NAME = "request";
    private static final String RESPONSE_TAG_NAME = "response";
    private static final String SIGNATURE_TAG_NAME = "Signature";

    /**
     * 签名- XML
     *
     * @throws Exception
     */
    public static String sign(String xmlContent, String RsaPrivateKey) throws Exception {
        Document doc = parseDocumentByString(xmlContent);
        PrivateKey privateKey = SignatureUtils.getPrivateKey(RsaPrivateKey);
        String result = SignatureUtils.signXmlElement(privateKey, doc, "request",
            XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA256, 2);
        return result;
    }

    public static String signRes(String xmlContent, String RsaPrivateKey) throws Exception {
        Document doc = parseDocumentByString(xmlContent);
        PrivateKey privateKey = SignatureUtils.getPrivateKey(RsaPrivateKey);
        String result = SignatureUtils.signXmlElement(privateKey, doc, "response",
            XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA256, 2);
        return result;
    }


    /**
     * 私钥签名应答报文
     *
     * @param responseMessage
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static String signResponseMessage(String responseMessage, PrivateKey privateKey)
        throws Exception {
        return signXmlMessage(responseMessage, RESPONSE_TAG_NAME, privateKey);
    }


    /**
     * 私钥签名
     *
     * @param xmlMessageSource 待签名的XML
     * @param elementTagName
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static String signXmlMessage(String xmlMessageSource, String elementTagName,
        PrivateKey privateKey) throws Exception {

        org.apache.xml.security.Init.init();

        Document xmlDocument = getDocument(xmlMessageSource);
        XMLSignature xmlSignature = new XMLSignature(xmlDocument, xmlDocument.getDocumentURI(),
            XML_ALGORITHM);

        NodeList nodeList = xmlDocument.getElementsByTagName(elementTagName);
        if (nodeList == null || nodeList.getLength() != 1) {
            throw new Exception("Document element with tag name " + elementTagName + " not fount");
        }
        Node elementNode = nodeList.item(0);
        elementNode.getParentNode().appendChild(xmlSignature.getElement());

        Transforms transforms = new Transforms(xmlDocument);
        transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
        xmlSignature.addDocument("", transforms, Constants.ALGO_ID_DIGEST_SHA1);

        xmlSignature.sign(privateKey);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        XMLUtils.outputDOM(xmlDocument, os);
        return os.toString(CHARSET);
    }

    /**
     * 解析XML字符串
     *
     * @param xmlMessageSource
     * @return
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws UnsupportedEncodingException
     */
    private static Document getDocument(String xmlMessageSource)
        throws ParserConfigurationException,
        SAXException, IOException,
        UnsupportedEncodingException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document xmlDocument = builder.parse(new InputSource(new ByteArrayInputStream(
            xmlMessageSource.getBytes(CHARSET))));
        return xmlDocument;
    }

    /**
     * 验签 - XML
     *
     * @throws Exception
     */
    public static boolean verify(String xmlContent, String RsaPublicKey) throws Exception {
        Document doc = parseDocumentByString(xmlContent);
        PublicKey publicKey = SignatureUtils.getPublicKey(RsaPublicKey);

        return SignatureUtils.verifyXmlElement(publicKey, doc);
    }

    /**
     * 解析XML
     *
     * @param xmlContent
     * @return
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    private static Document parseDocumentByString(String xmlContent) throws Exception {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);// 否则无法识别namespace
        return factory.newDocumentBuilder().parse(new InputSource(new StringReader(xmlContent)));
    }


}