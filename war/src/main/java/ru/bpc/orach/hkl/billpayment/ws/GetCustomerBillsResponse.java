
package ru.bpc.orach.hkl.billpayment.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="GetCustomerBillsResult" type="{http://119.82.253.240/}CustomerBills" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "getCustomerBillsResult"
})
@XmlRootElement(name = "GetCustomerBillsResponse")
public class GetCustomerBillsResponse {

    @XmlElement(name = "GetCustomerBillsResult")
    protected CustomerBills getCustomerBillsResult;

    /**
     * Gets the value of the getCustomerBillsResult property.
     * 
     * @return
     *     possible object is
     *     {@link CustomerBills }
     *     
     */
    public CustomerBills getGetCustomerBillsResult() {
        return getCustomerBillsResult;
    }

    /**
     * Sets the value of the getCustomerBillsResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomerBills }
     *     
     */
    public void setGetCustomerBillsResult(CustomerBills value) {
        this.getCustomerBillsResult = value;
    }

}
