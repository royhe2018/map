package com.sdkj.map.service;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.sdkj.map.domain.vo.MobileResultVO;
import com.sdkj.map.domain.vo.Position;

public interface PoiSearcherService {
	public JsonNode findPoiInfo(Map<String,Object> param) throws Exception;
	
	public MobileResultVO  getDistance(Position start, Position end);
	
	public MobileResultVO queryNearlyDriver(Position position,String cityName,int distance);
}
