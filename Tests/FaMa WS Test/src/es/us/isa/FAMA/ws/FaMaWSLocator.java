/**
 * FaMaWSLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package es.us.isa.FAMA.ws;

public class FaMaWSLocator extends org.apache.axis.client.Service implements es.us.isa.FAMA.ws.FaMaWS {

    public FaMaWSLocator() {
    }


    public FaMaWSLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public FaMaWSLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for FaMaWSPort
    private java.lang.String FaMaWSPort_address = "http://localhost:8082/FaMaWS";

    public java.lang.String getFaMaWSPortAddress() {
        return FaMaWSPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String FaMaWSPortWSDDServiceName = "FaMaWSPort";

    public java.lang.String getFaMaWSPortWSDDServiceName() {
        return FaMaWSPortWSDDServiceName;
    }

    public void setFaMaWSPortWSDDServiceName(java.lang.String name) {
        FaMaWSPortWSDDServiceName = name;
    }

    public es.us.isa.FAMA.ws.FaMaWSPortType getFaMaWSPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(FaMaWSPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getFaMaWSPort(endpoint);
    }

    public es.us.isa.FAMA.ws.FaMaWSPortType getFaMaWSPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            es.us.isa.FAMA.ws.FaMaWSSoapBindingStub _stub = new es.us.isa.FAMA.ws.FaMaWSSoapBindingStub(portAddress, this);
            _stub.setPortName(getFaMaWSPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setFaMaWSPortEndpointAddress(java.lang.String address) {
        FaMaWSPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (es.us.isa.FAMA.ws.FaMaWSPortType.class.isAssignableFrom(serviceEndpointInterface)) {
                es.us.isa.FAMA.ws.FaMaWSSoapBindingStub _stub = new es.us.isa.FAMA.ws.FaMaWSSoapBindingStub(new java.net.URL(FaMaWSPort_address), this);
                _stub.setPortName(getFaMaWSPortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("FaMaWSPort".equals(inputPortName)) {
            return getFaMaWSPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://ws.FAMA.isa.us.es/", "FaMaWS");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://ws.FAMA.isa.us.es/", "FaMaWSPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("FaMaWSPort".equals(portName)) {
            setFaMaWSPortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
