/**
 * IsValidConfiguration.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package es.us.isa.FAMA.ws;

public class IsValidConfiguration  implements java.io.Serializable {
    private byte[] arg0;

    private es.us.isa.FAMA.stagedConfigManager.Configuration arg1;

    public IsValidConfiguration() {
    }

    public IsValidConfiguration(
           byte[] arg0,
           es.us.isa.FAMA.stagedConfigManager.Configuration arg1) {
           this.arg0 = arg0;
           this.arg1 = arg1;
    }


    /**
     * Gets the arg0 value for this IsValidConfiguration.
     * 
     * @return arg0
     */
    public byte[] getArg0() {
        return arg0;
    }


    /**
     * Sets the arg0 value for this IsValidConfiguration.
     * 
     * @param arg0
     */
    public void setArg0(byte[] arg0) {
        this.arg0 = arg0;
    }


    /**
     * Gets the arg1 value for this IsValidConfiguration.
     * 
     * @return arg1
     */
    public es.us.isa.FAMA.stagedConfigManager.Configuration getArg1() {
        return arg1;
    }


    /**
     * Sets the arg1 value for this IsValidConfiguration.
     * 
     * @param arg1
     */
    public void setArg1(es.us.isa.FAMA.stagedConfigManager.Configuration arg1) {
        this.arg1 = arg1;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof IsValidConfiguration)) return false;
        IsValidConfiguration other = (IsValidConfiguration) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.arg0==null && other.getArg0()==null) || 
             (this.arg0!=null &&
              java.util.Arrays.equals(this.arg0, other.getArg0()))) &&
            ((this.arg1==null && other.getArg1()==null) || 
             (this.arg1!=null &&
              this.arg1.equals(other.getArg1())));
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
        if (getArg0() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getArg0());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getArg0(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getArg1() != null) {
            _hashCode += getArg1().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(IsValidConfiguration.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ws.FAMA.isa.us.es/", "isValidConfiguration"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("arg0");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ws.FAMA.isa.us.es/", "arg0"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "base64Binary"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("arg1");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ws.FAMA.isa.us.es/", "arg1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://stagedConfigManager.FAMA.isa.us.es", "Configuration"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
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
