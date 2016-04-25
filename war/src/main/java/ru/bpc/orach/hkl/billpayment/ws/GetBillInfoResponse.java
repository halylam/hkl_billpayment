
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
 *         &lt;element name="GetBillInfoResult" type="{http://119.82.253.240/}BillInfo" minOccurs="0"/>
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
    "getBillInfoResult"
})
@XmlRootElement(name = "GetBillInfoResponse")
public class GetBillInfoResponse {

    @XmlElement(name = "GetBillInfoResult")
    protected BillInfo getBillInfoResult;

    /**
     * Gets the value of the getBillInfoResult property.
     * 
     * @return
     *     possible object is
     *     {@link BillInfo }
     *     
     */
    public BillInfo getGetBillInfoResult() {
        return getBillInfoResult;
    }

    /**
     * Sets the value of the getBillInfoResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link BillInfo }
     *     
     */
    public void setGetBillInfoResult(BillInfo value) {
        this.getBillInfoResult = value;
    }

}
