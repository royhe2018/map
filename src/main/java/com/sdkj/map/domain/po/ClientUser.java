package com.sdkj.map.domain.po;

public class ClientUser {
    private Integer id;


    private String account;


    private String userType;
    
    private String mapTerminalId;
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
 
	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getMapTerminalId() {
		return mapTerminalId;
	}

	public void setMapTerminalId(String mapTerminalId) {
		this.mapTerminalId = mapTerminalId;
	}
    
}