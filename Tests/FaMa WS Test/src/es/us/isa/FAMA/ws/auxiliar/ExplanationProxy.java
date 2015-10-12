/**
 * ExplanationProxy.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package es.us.isa.FAMA.ws.auxiliar;

public class ExplanationProxy  implements java.io.Serializable {
    private java.lang.String[] relationships;

    public ExplanationProxy() {
    }

    public ExplanationProxy(
           java.lang.String[] relationships) {
           this.relationships = relationships;
    }


    /**
     * Gets the relationships value for this ExplanationProxy.
     * 
     * @return relationships
     */
    public java.lang.String[] getRelationships() {
        return relationships;
    }


    /**
     * Sets the relationships value for this ExplanationProxy.
     * 
     * @param relationships
     */
    public void setRelationships(java.lang.String[] relationships) {
        this.relationships = relationships;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ExplanationProxy)) return false;
        ExplanationProxy other = (ExplanationProxy) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.relationships==null && other.getRelationships()==null) || 
             (this.relationships!=null &&
              java.util.Arrays.equals(this.relationships, other.getRelationships())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getRelationships() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getRelationships());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getRelationships(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ExplanationProxy.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://auxiliar.ws.FAMA.isa.us.es", "ExplanationProxy"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("relationships");
        elemField.setXmlName(new javax.xml.namespace.QName("http://auxiliar.ws.FAMA.isa.us.es", "relationships"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setItemQName(new javax.xml.namespace.QName("http://ws.FAMA.isa.us.es/", "string"));
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
