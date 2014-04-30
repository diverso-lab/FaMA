package es.us.isa.FAMA.ws;

public class FaMaWSPortTypeProxy implements es.us.isa.FAMA.ws.FaMaWSPortType {
  private String _endpoint = null;
  private es.us.isa.FAMA.ws.FaMaWSPortType faMaWSPortType = null;
  
  public FaMaWSPortTypeProxy() {
    _initFaMaWSPortTypeProxy();
  }
  
  public FaMaWSPortTypeProxy(String endpoint) {
    _endpoint = endpoint;
    _initFaMaWSPortTypeProxy();
  }
  
  private void _initFaMaWSPortTypeProxy() {
    try {
      faMaWSPortType = (new es.us.isa.FAMA.ws.FaMaWSLocator()).getFaMaWSPort();
      if (faMaWSPortType != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)faMaWSPortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)faMaWSPortType)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (faMaWSPortType != null)
      ((javax.xml.rpc.Stub)faMaWSPortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public es.us.isa.FAMA.ws.FaMaWSPortType getFaMaWSPortType() {
    if (faMaWSPortType == null)
      _initFaMaWSPortTypeProxy();
    return faMaWSPortType;
  }
  
  public boolean isValidProduct(byte[] arg0, es.us.isa.FAMA.ws.auxiliar.ProductProxy arg1) throws java.rmi.RemoteException{
    if (faMaWSPortType == null)
      _initFaMaWSPortTypeProxy();
    return faMaWSPortType.isValidProduct(arg0, arg1);
  }
  
  public long getNumberOfProducts(byte[] arg0) throws java.rmi.RemoteException{
    if (faMaWSPortType == null)
      _initFaMaWSPortTypeProxy();
    return faMaWSPortType.getNumberOfProducts(arg0);
  }
  
  public java.lang.String[] getVariantFeatures(byte[] arg0) throws java.rmi.RemoteException{
    if (faMaWSPortType == null)
      _initFaMaWSPortTypeProxy();
    return faMaWSPortType.getVariantFeatures(arg0);
  }
  
  public boolean isValidConfiguration(byte[] arg0, es.us.isa.FAMA.stagedConfigManager.Configuration arg1) throws java.rmi.RemoteException{
    if (faMaWSPortType == null)
      _initFaMaWSPortTypeProxy();
    return faMaWSPortType.isValidConfiguration(arg0, arg1);
  }
  
  public es.us.isa.FAMA.ws.auxiliar.ErrorProxy[] getErrors(byte[] arg0) throws java.rmi.RemoteException{
    if (faMaWSPortType == null)
      _initFaMaWSPortTypeProxy();
    return faMaWSPortType.getErrors(arg0);
  }
  
  public java.lang.String[] getCoreFeatures(byte[] arg0) throws java.rmi.RemoteException{
    if (faMaWSPortType == null)
      _initFaMaWSPortTypeProxy();
    return faMaWSPortType.getCoreFeatures(arg0);
  }
  
  public es.us.isa.FAMA.ws.auxiliar.ProductProxy productRepair(byte[] arg0, es.us.isa.FAMA.ws.auxiliar.ProductProxy arg1) throws java.rmi.RemoteException{
    if (faMaWSPortType == null)
      _initFaMaWSPortTypeProxy();
    return faMaWSPortType.productRepair(arg0, arg1);
  }
  
  public es.us.isa.FAMA.ws.auxiliar.ProductProxy[] getProducts(byte[] arg0) throws java.rmi.RemoteException{
    if (faMaWSPortType == null)
      _initFaMaWSPortTypeProxy();
    return faMaWSPortType.getProducts(arg0);
  }
  
  public es.us.isa.FAMA.ws.auxiliar.ErrorProxy[] detectAndExplainErrors(byte[] arg0) throws java.rmi.RemoteException{
    if (faMaWSPortType == null)
      _initFaMaWSPortTypeProxy();
    return faMaWSPortType.detectAndExplainErrors(arg0);
  }
  
  public float getVariability(byte[] arg0) throws java.rmi.RemoteException{
    if (faMaWSPortType == null)
      _initFaMaWSPortTypeProxy();
    return faMaWSPortType.getVariability(arg0);
  }
  
  public long getCommonality(byte[] arg0, java.lang.String arg1) throws java.rmi.RemoteException{
    if (faMaWSPortType == null)
      _initFaMaWSPortTypeProxy();
    return faMaWSPortType.getCommonality(arg0, arg1);
  }
  
  public boolean isValid(byte[] arg0) throws java.rmi.RemoteException{
    if (faMaWSPortType == null)
      _initFaMaWSPortTypeProxy();
    return faMaWSPortType.isValid(arg0);
  }
  
  
}