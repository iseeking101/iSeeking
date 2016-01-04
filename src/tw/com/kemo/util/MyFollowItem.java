package tw.com.kemo.util;

import java.util.ArrayList;

public class MyFollowItem {
	
	
	private String beaconId;
	private String oldCharacteristic;
	private String oldhistory;
	private String oldClothes;
	private String oldAddr;
	private ArrayList<String> groupMember =new ArrayList<String>();
	private String oldName;
	private int oldPic;
	private String statusv;
	private Double longitude;
	private Double latitude;
	private String missingMillis;
	private ArrayList<OldTraceItem> location = new ArrayList<OldTraceItem>();
	
	
	
	
	public MyFollowItem(String oldName,int oldPic) {
		this.oldName=oldName;
		this.oldPic=oldPic;
	}
	public MyFollowItem(
			String beaconId ,String oldName,int oldPic,
			String oldCharacteristic,String oldhistory,
			String oldClothes, String oldAddr,ArrayList<String> groupMember,String statusv
			,Double longitude,Double latitude
			,String missingMillis,ArrayList<OldTraceItem> location
			) {
		this.beaconId = beaconId;
		this.oldName=oldName;
		this.oldPic=oldPic;
		this.oldCharacteristic=oldCharacteristic;
		this.oldhistory = oldhistory;
		this.oldClothes = oldClothes;
		this.oldAddr = oldAddr;
		this.groupMember= groupMember;
		this.statusv = statusv;
		this.longitude = longitude;
		this.latitude = latitude;
		this.missingMillis = missingMillis;
		this.location = location;
	}

	public Double getLongitude(){
		return longitude;
	}
	public Double getLatitude(){
		return latitude;
	}
	public String getStatusv(){
		return statusv;
	}
	public String getBeaconId(){
		return beaconId;
	}
	public String getOldCharacteristic(){
		return oldCharacteristic;
	}
	public String getOldhistory(){
		return oldhistory;
	}
	public String getOldClothes(){
		return oldClothes;
	}
	public String getOldAddr(){
		return oldAddr;
	}
	public String getOldName(){
		return oldName;
	}
	public ArrayList<String> getGroupMember(){
		return groupMember;
	}
	
	public int getOldPic(){
		return oldPic;
	}
	public ArrayList<OldTraceItem> getLocation(){
		return location;
	}
	public String getMissingMillis(){
		return missingMillis;
	}

}
