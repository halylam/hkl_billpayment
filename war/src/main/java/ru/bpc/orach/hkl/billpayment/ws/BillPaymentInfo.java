
package ru.bpc.orach.hkl.billpayment.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BillPaymentInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BillPaymentInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CustomerNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BillNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BillAmount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PaymentDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BillPaymentInfo", propOrder = {
    "customerNumber",
    "billNumber",
    "billAmount",
    "paymentDate"
})
public class BillPaymentInfo {

    @XmlElement(name = "CustomerNumber")
    protected String customerNumber;
    @XmlElement(name = "BillNumber")
    protected String billNumber;
    @XmlElement(name = "BillAmount")
    protected String billAmount;
    @XmlElement(name = "PaymentDate")
    protected String paymentDate;

    /**
     * Gets the value of the customerNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerNumber() {
        return customerNumber;
    }

    /**
     * Sets the value of the customerNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerNumber(String value) {
        this.customerNumber = value;
    }

    /**
     * Gets the value of the billNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBillNumber() {
        return billNumber;
    }

    /**
     * Sets the value of the billNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBillNumber(String value) {
        this.billNumber = value;
    }

    /**
     * Gets the value of the billAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBillAmount() {
        return billAmount;
    }

    /**
     * Sets the value of the billAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBillAmount(String value) {
        this.billAmount = value;
    }

    /**
     * Gets the value of the paymentDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaymentDate() {
        return paymentDate;
    }

    /**
     * Sets the value of the paymentDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaymentDate(String value) {
        this.paymentDate = value;
    }

}
