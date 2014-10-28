//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.10.28 at 06:36:54 PM CET 
//


package com.enonic.wem.api.xml.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for contentFilter complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="contentFilter">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded">
 *         &lt;element name="deny" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="allow" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "contentFilter", propOrder = {
    "denyOrAllow"
})
public class XmlContentFilter {

    @XmlElementRefs({
        @XmlElementRef(name = "deny", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "allow", type = JAXBElement.class, required = false)
    })
    protected List<JAXBElement<String>> denyOrAllow;

    /**
     * Gets the value of the denyOrAllow property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the denyOrAllow property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDenyOrAllow().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link javax.xml.bind.JAXBElement }{@code <}{@link String }{@code >}
     * {@link javax.xml.bind.JAXBElement }{@code <}{@link String }{@code >}
     * 
     * 
     */
    public List<JAXBElement<String>> getDenyOrAllow() {
        if (denyOrAllow == null) {
            denyOrAllow = new ArrayList<JAXBElement<String>>();
        }
        return this.denyOrAllow;
    }

}
