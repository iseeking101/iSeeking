package tw.com.kemo.util;



public class nouser_MemberListViewItem {
	
	private String rowTitle;
	private String rowInput;
	

	
	public nouser_MemberListViewItem(String rowTitle,String rowInput) {
		this.rowTitle=rowTitle;
		this.rowInput=rowInput;

	}
	
	public String getRowTitle(){
		return rowTitle;
	}
	
	public String getRowInput(){
		return rowInput;
	}

	
}
