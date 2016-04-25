
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
 *         &lt;element name="UpdateBillResult" type="{http://119.82.253.240/}UpdateStatus" minOccurs="0"/>
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
    "updateBillResult"
})
@XmlRootElement(name = "UpdateBillResponse")
public class UpdateBillResponse {

    @XmlElement(name = "UpdateBillResult")
    protected UpdateStatus updateBillResult;

    /**
     * Gets the value of the updateBillResult property.
     * 
     * @return
     *     possible object is
     *     {@link UpdateStatus }
     *     
     */
    public UpdateStatus getUpdateBillResult() {
        return updateBillResult;
    }

    /**
     * Sets the value of the updateBillResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link UpdateStatus }
     *     
     */
    public void setUpdateBillResult(UpdateStatus value) {
        this.updateBillResult = value;
    }

}
