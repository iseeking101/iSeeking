package tw.com.kemo.util;

import java.util.ArrayList;

public class MyOldManItem {
	
	
	private String beaconId;
	private String oldCharacteristic;
	private String oldhistory;
	private String oldClothes;
	private String oldAddr;
	private ArrayList<String> groupMember =new ArrayList<String>();
	private String oldName;
	private int oldPic;
	private String statusv;	
	
	public MyOldManItem(
			String beaconId ,String oldName,int oldPic,
			String oldCharacteristic,String oldhistory,
			String oldClothes, String oldAddr,ArrayList<String> groupMember,String statusv
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
	
}
