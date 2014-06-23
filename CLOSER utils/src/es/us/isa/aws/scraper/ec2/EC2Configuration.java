package es.us.isa.aws.scraper.ec2;

public class EC2Configuration {

	private String location;
	private String os;
	private String instanceType;
	private String purchaseMode;
	private String dedication;
	
	
	public EC2Configuration(String location, String oS, String instanceType,
			String purchaseMode, String dedication) {
		super();
		this.location = location;
		this.os = oS;
		this.instanceType = instanceType;
		this.purchaseMode = purchaseMode;
		this.dedication = dedication;
	}
	
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getOS() {
		return os;
	}
	public void setOS(String oS) {
		this.os = oS;
	}
	public String getInstanceType() {
		return instanceType;
	}
	public void setInstanceType(String instanceType) {
		this.instanceType = instanceType;
	}
	public String getPurchaseMode() {
		return purchaseMode;
	}
	public void setPurchaseMode(String purchaseMode) {
		this.purchaseMode = purchaseMode;
	}
	public String getDedication() {
		return dedication;
	}
	public void setDedication(String dedication) {
		this.dedication = dedication;
	}
	
	public boolean equals(Object o){
		boolean result = false;
		if (o instanceof EC2Configuration){
			EC2Configuration config = (EC2Configuration) o;
			if (config.getDedication().equals(dedication) &&
					config.getInstanceType().equals(instanceType) &&
					config.getLocation().equals(location) &&
					config.getOS().equals(os) &&
					config.getPurchaseMode().equals(purchaseMode)){
				result = true;
			}
		}
		return result;
	}
	
}
