
package org.talend.mdm.webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getTransformerV2PKsResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getTransformerV2PKsResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="return" type="{http://www.talend.com/mdm}WSTransformerV2PKArray" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getTransformerV2PKsResponse", propOrder = {
    "_return"
})
public class GetTransformerV2PKsResponse {

    @XmlElement(name = "return")
    protected WSTransformerV2PKArray _return;

    /**
     * Default no-arg constructor
     * 
     */
    public GetTransformerV2PKsResponse() {
        super();
    }

    /**
     * Fully-initialising value constructor
     * 
     */
    public GetTransformerV2PKsResponse(final WSTransformerV2PKArray _return) {
        this._return = _return;
    }

    /**
     * Gets the value of the return property.
     * 
     * @return
     *     possible object is
     *     {@link WSTransformerV2PKArray }
     *     
     */
    public WSTransformerV2PKArray getReturn() {
        return _return;
    }

    /**
     * Sets the value of the return property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSTransformerV2PKArray }
     *     
     */
    public void setReturn(WSTransformerV2PKArray value) {
        this._return = value;
    }

}
