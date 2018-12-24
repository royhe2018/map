package com.sdkj.map.service;

import java.util.Map;

import com.sdkj.map.domain.vo.MobileResultVO;

public interface LocationService {
	public Map<String,Object> uploadTruckDriverLocation(Map<String,String> param);
	
	public MobileResultVO findTruckDriverLocation(Map<String,String> param);
	
	public MobileResultVO findTruckLocationHistroy(Map<String,Object> param);
}
