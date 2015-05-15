package com.home.automation.data;

public class AppllianceCntrl {
	String UID;
	public String getUID() {
		return UID;
	}
	public void setUID(String uID) {
		UID = uID;
	}
	public String getAddr() {
		return Addr;
	}
	public void setAddr(String addr) {
		Addr = addr;
	}
	public String getContricom() {
		return Contricom;
	}
	public void setContricom(String contricom) {
		Contricom = contricom;
	}
	public String getActcom() {
		return Actcom;
	}
	public void setActcom(String actcom) {
		Actcom = actcom;
	}
	public String getDadd() {
		return Dadd;
	}
	public void setDadd(String dadd) {
		Dadd = dadd;
	}
	public String getCmnddata() {
		return Cmnddata;
	}
	public void setCmnddata(String cmnddata) {
		Cmnddata = cmnddata;
	}
	String Addr;
	String Contricom;
	String Actcom;
	String Dadd;
	String Cmnddata;
	String cntrlName;
	public String getCntrlName() {
		return cntrlName;
	}
	public void setCntrlName(String cntrlName) {
		this.cntrlName = cntrlName;
	}
	
	
}
