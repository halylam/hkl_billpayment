//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b52-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.12.17 at 05:06:29 PM MSK 
//


package ru.bpc.iso8583.xml;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ru.bpc.iso8583.xml package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private static final QName _Message_QNAME = new QName("http://www.bpc.ru/iso8583", "message");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ru.bpc.iso8583.xml
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link TagType }
     * 
     */
    public TagType createTagType() {
        return new TagType();
    }

    /**
     * Create an instance of {@link MessageType }
     * 
     */
    public MessageType createMessageType() {
        return new MessageType();
    }

    /**
     * Create an instance of {@link FieldType }
     * 
     */
    public FieldType createFieldType() {
        return new FieldType();
    }

    /**
     * Create an instance of {@link FieldsType }
     * 
     */
    public FieldsType createFieldsType() {
        return new FieldsType();
    }

    /**
     * Create an instance of {@link TagsType }
     * 
     */
    public TagsType createTagsType() {
        return new TagsType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.bpc.ru/iso8583", name = "message")
    public JAXBElement<MessageType> createMessage(MessageType value) {
        return new JAXBElement<MessageType>(_Message_QNAME, MessageType.class, null, value);
    }

}
