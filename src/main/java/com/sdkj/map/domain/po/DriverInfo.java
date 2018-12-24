package com.sdkj.map.domain.po;

public class DriverInfo {
    private Long id;

    private Long userId;

    private String registerCity;
    
    private String idCardNo;

    private Integer idCardType;

    private String drivingLicenseNo;

    private String drivingLicenseFileNo;

    private String drivingLicenseType;

    private String drivingLicenseImage;

    private String idCardImage;

    private String idCardBackImage;

    private String carNo;

    private String carDrivingImage;

    private String createTime;

    private Integer status;
    
    private Integer onDutyStatus;
    
    private String registrionId;
    
    private String mapIerminalId;
    
    private String driverPhone;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRegisterCity() {
		return registerCity;
	}

	public void setRegisterCity(String registerCity) {
		this.registerCity = registerCity;
	}

	public String getIdCardNo() {
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo == null ? null : idCardNo.trim();
    }

    public Integer getIdCardType() {
        return idCardType;
    }

    public void setIdCardType(Integer idCardType) {
        this.idCardType = idCardType;
    }

    public String getDrivingLicenseNo() {
        return drivingLicenseNo;
    }

    public void setDrivingLicenseNo(String drivingLicenseNo) {
        this.drivingLicenseNo = drivingLicenseNo == null ? null : drivingLicenseNo.trim();
    }

    public String getDrivingLicenseFileNo() {
        return drivingLicenseFileNo;
    }

    public void setDrivingLicenseFileNo(String drivingLicenseFileNo) {
        this.drivingLicenseFileNo = drivingLicenseFileNo == null ? null : drivingLicenseFileNo.trim();
    }

    public String getDrivingLicenseType() {
        return drivingLicenseType;
    }

    public void setDrivingLicenseType(String drivingLicenseType) {
        this.drivingLicenseType = drivingLicenseType == null ? null : drivingLicenseType.trim();
    }

    public String getDrivingLicenseImage() {
        return drivingLicenseImage;
    }

    public void setDrivingLicenseImage(String drivingLicenseImage) {
        this.drivingLicenseImage = drivingLicenseImage == null ? null : drivingLicenseImage.trim();
    }

    public String getIdCardImage() {
        return idCardImage;
    }

    public void setIdCardImage(String idCardImage) {
        this.idCardImage = idCardImage == null ? null : idCardImage.trim();
    }

    public String getIdCardBackImage() {
        return idCardBackImage;
    }

    public void setIdCardBackImage(String idCardBackImage) {
        this.idCardBackImage = idCardBackImage == null ? null : idCardBackImage.trim();
    }

    public String getCarNo() {
        return carNo;
    }

    public void setCarNo(String carNo) {
        this.carNo = carNo == null ? null : carNo.trim();
    }

    public String getCarDrivingImage() {
        return carDrivingImage;
    }

    public void setCarDrivingImage(String carDrivingImage) {
        this.carDrivingImage = carDrivingImage == null ? null : carDrivingImage.trim();
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime == null ? null : createTime.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

	public Integer getOnDutyStatus() {
		return onDutyStatus;
	}

	public void setOnDutyStatus(Integer onDutyStatus) {
		this.onDutyStatus = onDutyStatus;
	}

	public String getRegistrionId() {
		return registrionId;
	}

	public void setRegistrionId(String registrionId) {
		this.registrionId = registrionId;
	}

	public String getMapIerminalId() {
		return mapIerminalId;
	}

	public void setMapIerminalId(String mapIerminalId) {
		this.mapIerminalId = mapIerminalId;
	}

	public String getDriverPhone() {
		return driverPhone;
	}

	public void setDriverPhone(String driverPhone) {
		this.driverPhone = driverPhone;
	}
    
}