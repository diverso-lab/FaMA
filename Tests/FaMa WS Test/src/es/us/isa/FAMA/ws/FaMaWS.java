/**
 * FaMaWS.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package es.us.isa.FAMA.ws;

public interface FaMaWS extends javax.xml.rpc.Service {
    public java.lang.String getFaMaWSPortAddress();

    public es.us.isa.FAMA.ws.FaMaWSPortType getFaMaWSPort() throws javax.xml.rpc.ServiceException;

    public es.us.isa.FAMA.ws.FaMaWSPortType getFaMaWSPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
