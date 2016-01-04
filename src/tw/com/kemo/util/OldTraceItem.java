package tw.com.kemo.util;

public class OldTraceItem {
	private Double longitude;
	private Double latitude;
	private String missingMillis;
	
	public OldTraceItem(Double longitude,Double latitude,String millis){
		this.longitude=longitude;
		this.latitude=latitude;
		this.missingMillis=millis;
	}
	public Double getLongitude(){
		return longitude;
	}
	public Double getLatitude(){
		return latitude;
	}
	public String getMillis(){
		return missingMillis;
	}
}
