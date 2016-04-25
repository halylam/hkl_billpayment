
package ru.bpc.orach.hkl.billpayment.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CustomerBills complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CustomerBills">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="BillNumbers" type="{http://119.82.253.240/}ArrayOfString" minOccurs="0"/>
 *         &lt;element name="StatusMessage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CustomerBills", propOrder = {
    "billNumbers",
    "statusMessage"
})
public class CustomerBills {

    @XmlElement(name = "BillNumbers")
    protected ArrayOfString billNumbers;
    @XmlElement(name = "StatusMessage")
    protected String statusMessage;

    /**
     * Gets the value of the billNumbers property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfString }
     *     
     */
    public ArrayOfString getBillNumbers() {
        return billNumbers;
    }

    /**
     * Sets the value of the billNumbers property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfString }
     *     
     */
    public void setBillNumbers(ArrayOfString value) {
        this.billNumbers = value;
    }

    /**
     * Gets the value of the statusMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatusMessage() {
        return statusMessage;
    }

    /**
     * Sets the value of the statusMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatusMessage(String value) {
        this.statusMessage = value;
    }

}
