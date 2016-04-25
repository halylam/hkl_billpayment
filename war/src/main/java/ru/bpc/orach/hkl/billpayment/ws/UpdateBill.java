
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
 *         &lt;element name="BillPaymentInfo" type="{http://119.82.253.240/}BillPaymentInfo" minOccurs="0"/>
 *         &lt;element name="LoginInfo" type="{http://119.82.253.240/}LoginInfo" minOccurs="0"/>
 *         &lt;element name="Checksum" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "billPaymentInfo",
    "loginInfo",
    "checksum"
})
@XmlRootElement(name = "UpdateBill")
public class UpdateBill {

    @XmlElement(name = "BillPaymentInfo")
    protected BillPaymentInfo billPaymentInfo;
    @XmlElement(name = "LoginInfo")
    protected LoginInfo loginInfo;
    @XmlElement(name = "Checksum")
    protected String checksum;

    /**
     * Gets the value of the billPaymentInfo property.
     * 
     * @return
     *     possible object is
     *     {@link BillPaymentInfo }
     *     
     */
    public BillPaymentInfo getBillPaymentInfo() {
        return billPaymentInfo;
    }

    /**
     * Sets the value of the billPaymentInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link BillPaymentInfo }
     *     
     */
    public void setBillPaymentInfo(BillPaymentInfo value) {
        this.billPaymentInfo = value;
    }

    /**
     * Gets the value of the loginInfo property.
     * 
     * @return
     *     possible object is
     *     {@link LoginInfo }
     *     
     */
    public LoginInfo getLoginInfo() {
        return loginInfo;
    }

    /**
     * Sets the value of the loginInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link LoginInfo }
     *     
     */
    public void setLoginInfo(LoginInfo value) {
        this.loginInfo = value;
    }

    /**
     * Gets the value of the checksum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChecksum() {
        return checksum;
    }

    /**
     * Sets the value of the checksum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChecksum(String value) {
        this.checksum = value;
    }

}
