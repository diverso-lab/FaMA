/**
 * FaMaWSPortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package es.us.isa.FAMA.ws;

public interface FaMaWSPortType extends java.rmi.Remote {
    public boolean isValidProduct(byte[] arg0, es.us.isa.FAMA.ws.auxiliar.ProductProxy arg1) throws java.rmi.RemoteException;
    public long getNumberOfProducts(byte[] arg0) throws java.rmi.RemoteException;
    public java.lang.String[] getVariantFeatures(byte[] arg0) throws java.rmi.RemoteException;
    public boolean isValidConfiguration(byte[] arg0, es.us.isa.FAMA.stagedConfigManager.Configuration arg1) throws java.rmi.RemoteException;
    public es.us.isa.FAMA.ws.auxiliar.ErrorProxy[] getErrors(byte[] arg0) throws java.rmi.RemoteException;
    public java.lang.String[] getCoreFeatures(byte[] arg0) throws java.rmi.RemoteException;
    public es.us.isa.FAMA.ws.auxiliar.ProductProxy productRepair(byte[] arg0, es.us.isa.FAMA.ws.auxiliar.ProductProxy arg1) throws java.rmi.RemoteException;
    public es.us.isa.FAMA.ws.auxiliar.ProductProxy[] getProducts(byte[] arg0) throws java.rmi.RemoteException;
    public es.us.isa.FAMA.ws.auxiliar.ErrorProxy[] detectAndExplainErrors(byte[] arg0) throws java.rmi.RemoteException;
    public float getVariability(byte[] arg0) throws java.rmi.RemoteException;
    public long getCommonality(byte[] arg0, java.lang.String arg1) throws java.rmi.RemoteException;
    public boolean isValid(byte[] arg0) throws java.rmi.RemoteException;
}
