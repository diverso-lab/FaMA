/**
 * ErrorProxy.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package es.us.isa.FAMA.ws.auxiliar;

public class ErrorProxy  implements java.io.Serializable {
    private es.us.isa.FAMA.ws.auxiliar.ExplanationProxy[] explanations;

    private es.us.isa.FAMA.ws.String2AnyTypeMapEntry[] observations;

    public ErrorProxy() {
    }

    public ErrorProxy(
           es.us.isa.FAMA.ws.auxiliar.ExplanationProxy[] explanations,
           es.us.isa.FAMA.ws.String2AnyTypeMapEntry[] observations) {
           this.explanations = explanations;
           this.observations = observations;
    }


    /**
     * Gets the explanations value for this ErrorProxy.
     * 
     * @return explanations
     */
    public es.us.isa.FAMA.ws.auxiliar.ExplanationProxy[] getExplanations() {
        return explanations;
    }


    /**
     * Sets the explanations value for this ErrorProxy.
     * 
     * @param explanations
     */
    public void setExplanations(es.us.isa.FAMA.ws.auxiliar.ExplanationProxy[] explanations) {
        this.explanations = explanations;
    }


    /**
     * Gets the observations value for this ErrorProxy.
     * 
     * @return observations
     */
    public es.us.isa.FAMA.ws.String2AnyTypeMapEntry[] getObservations() {
        return observations;
    }


    /**
     * Sets the observations value for this ErrorProxy.
     * 
     * @param observations
     */
    public void setObservations(es.us.isa.FAMA.ws.String2AnyTypeMapEntry[] observations) {
        this.observations = observations;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ErrorProxy)) return false;
        ErrorProxy other = (ErrorProxy) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.explanations==null && other.getExplanations()==null) || 
             (this.explanations!=null &&
              java.util.Arrays.equals(this.explanations, other.getExplanations()))) &&
            ((this.observations==null && other.getObservations()==null) || 
             (this.observations!=null &&
              java.util.Arrays.equals(this.observations, other.getObservations())));
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
        if (getExplanations() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getExplanations());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getExplanations(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getObservations() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getObservations());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getObservations(), i);
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
        new org.apache.axis.description.TypeDesc(ErrorProxy.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://auxiliar.ws.FAMA.isa.us.es", "ErrorProxy"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("explanations");
        elemField.setXmlName(new javax.xml.namespace.QName("http://auxiliar.ws.FAMA.isa.us.es", "explanations"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://auxiliar.ws.FAMA.isa.us.es", "ExplanationProxy"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setItemQName(new javax.xml.namespace.QName("http://auxiliar.ws.FAMA.isa.us.es", "ExplanationProxy"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("observations");
        elemField.setXmlName(new javax.xml.namespace.QName("http://auxiliar.ws.FAMA.isa.us.es", "observations"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://ws.FAMA.isa.us.es/", ">string2anyTypeMap>entry"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setItemQName(new javax.xml.namespace.QName("http://ws.FAMA.isa.us.es/", "entry"));
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
