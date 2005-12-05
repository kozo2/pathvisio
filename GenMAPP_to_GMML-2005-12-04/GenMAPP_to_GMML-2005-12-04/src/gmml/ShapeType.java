//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v1.0.5-b16-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2005.10.19 at 03:49:42 PM CEST 
//


package gmml;


/**
 * FIXME: No comments for
 * now
 * Java content class for anonymous complex type.
 * <p>The following schema fragment specifies the expected content contained within this java content object. (defined at file:/L:/bigcat_just_cvs/reactome_genmapp/src/GMML.xsd line 654)
 * <p>
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{}BaseElement">
 *       &lt;sequence>
 *         &lt;element name="Notes" type="{}NoteType" minOccurs="0"/>
 *         &lt;element name="Graphics">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="CenterX" type="{}Coordinate" />
 *                 &lt;attribute name="CenterY" type="{}Coordinate" />
 *                 &lt;attribute name="Color" type="{}ColorType" />
 *                 &lt;attribute name="Height" type="{}Dimension" />
 *                 &lt;attribute name="Rotation" type="{http://www.w3.org/2001/XMLSchema}decimal" />
 *                 &lt;attribute name="Width" type="{}Dimension" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="Type" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Rectangle"/>
 *             &lt;enumeration value="Oval"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 */
public interface ShapeType
    extends gmml.BaseElement
{


    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String}
     */
    java.lang.String getType();

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String}
     */
    void setType(java.lang.String value);

    /**
     * Gets the value of the graphics property.
     * 
     * @return
     *     possible object is
     *     {@link gmml.ShapeType.GraphicsType}
     */
    gmml.ShapeType.GraphicsType getGraphics();

    /**
     * Sets the value of the graphics property.
     * 
     * @param value
     *     allowed object is
     *     {@link gmml.ShapeType.GraphicsType}
     */
    void setGraphics(gmml.ShapeType.GraphicsType value);

    /**
     * Text from Notes is not
     * displayed on GenMAPP map or backpage
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String}
     */
    java.lang.String getNotes();

    /**
     * Text from Notes is not
     * displayed on GenMAPP map or backpage
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String}
     */
    void setNotes(java.lang.String value);


    /**
     * Java content class for anonymous complex type.
     * <p>The following schema fragment specifies the expected content contained within this java content object. (defined at file:/L:/bigcat_just_cvs/reactome_genmapp/src/GMML.xsd line 673)
     * <p>
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="CenterX" type="{}Coordinate" />
     *       &lt;attribute name="CenterY" type="{}Coordinate" />
     *       &lt;attribute name="Color" type="{}ColorType" />
     *       &lt;attribute name="Height" type="{}Dimension" />
     *       &lt;attribute name="Rotation" type="{http://www.w3.org/2001/XMLSchema}decimal" />
     *       &lt;attribute name="Width" type="{}Dimension" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     */
    public interface GraphicsType {


        /**
         * Gets the value of the color property.
         * 
         * @return
         *     possible object is
         *     {@link java.math.BigInteger}
         *     byte[]{@link java.lang.String}
         */
        java.lang.Object getColor();

        /**
         * Sets the value of the color property.
         * 
         * @param value
         *     allowed object is
         *     {@link java.math.BigInteger}
         *     byte[]{@link java.lang.String}
         */
        void setColor(java.lang.Object value);

        /**
         * Gets the value of the width property.
         * 
         * @return
         *     possible object is
         *     {@link java.math.BigInteger}
         */
        java.math.BigInteger getWidth();

        /**
         * Sets the value of the width property.
         * 
         * @param value
         *     allowed object is
         *     {@link java.math.BigInteger}
         */
        void setWidth(java.math.BigInteger value);

        /**
         * Gets the value of the height property.
         * 
         * @return
         *     possible object is
         *     {@link java.math.BigInteger}
         */
        java.math.BigInteger getHeight();

        /**
         * Sets the value of the height property.
         * 
         * @param value
         *     allowed object is
         *     {@link java.math.BigInteger}
         */
        void setHeight(java.math.BigInteger value);

        /**
         * Gets the value of the rotation property.
         * 
         * @return
         *     possible object is
         *     {@link java.math.BigDecimal}
         */
        java.math.BigDecimal getRotation();

        /**
         * Sets the value of the rotation property.
         * 
         * @param value
         *     allowed object is
         *     {@link java.math.BigDecimal}
         */
        void setRotation(java.math.BigDecimal value);

        /**
         * Gets the value of the centerY property.
         * 
         * @return
         *     possible object is
         *     {@link java.math.BigInteger}
         */
        java.math.BigInteger getCenterY();

        /**
         * Sets the value of the centerY property.
         * 
         * @param value
         *     allowed object is
         *     {@link java.math.BigInteger}
         */
        void setCenterY(java.math.BigInteger value);

        /**
         * Gets the value of the centerX property.
         * 
         * @return
         *     possible object is
         *     {@link java.math.BigInteger}
         */
        java.math.BigInteger getCenterX();

        /**
         * Sets the value of the centerX property.
         * 
         * @param value
         *     allowed object is
         *     {@link java.math.BigInteger}
         */
        void setCenterX(java.math.BigInteger value);

    }

}
