//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v1.0.5-b16-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2005.10.19 at 03:49:42 PM CEST 
//


package gmml;


/**
 * Java content class for Arc element declaration.
 * <p>The following schema fragment specifies the expected content contained within this java content object. (defined at file:/L:/bigcat_just_cvs/reactome_genmapp/src/GMML.xsd line 518)
 * <p>
 * <pre>
 * &lt;element name="Arc">
 *   &lt;complexType>
 *     &lt;complexContent>
 *       &lt;extension base="{}BaseElement">
 *         &lt;sequence>
 *           &lt;element name="Notes" type="{}NoteType" minOccurs="0"/>
 *           &lt;element name="Comment" type="{}CommentType" minOccurs="0"/>
 *           &lt;element name="Graphics">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                   &lt;attribute name="Color" type="{}ColorType" />
 *                   &lt;attribute name="Height" use="required" type="{}Dimension" />
 *                   &lt;attribute name="Rotation" type="{http://www.w3.org/2001/XMLSchema}decimal" />
 *                   &lt;attribute name="StartX" use="required" type="{}Coordinate" />
 *                   &lt;attribute name="StartY" use="required" type="{}Coordinate" />
 *                   &lt;attribute name="Width" use="required" type="{}Dimension" />
 *                 &lt;/restriction>
 *               &lt;/complexContent>
 *             &lt;/complexType>
 *           &lt;/element>
 *         &lt;/sequence>
 *       &lt;/extension>
 *     &lt;/complexContent>
 *   &lt;/complexType>
 * &lt;/element>
 * </pre>
 * 
 */
public interface Arc
    extends javax.xml.bind.Element, gmml.ArcType
{


}
