package com.sdkj.map.dao.driverInfo;

import java.util.List;
import java.util.Map;

import com.sdkj.map.domain.po.DriverInfo;

public interface DriverInfoMapper {

    DriverInfo findSingleDriver(Map<String,Object> param);
    
    DriverInfo findDriverInfoExist(Map<String,Object> param);

    List<DriverInfo> findDriverList(Map<String,Object> param);
}