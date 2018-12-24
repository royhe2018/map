package com.sdkj.map.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sdkj.map.dao.admWXUser.ClientUserMapper;
import com.sdkj.map.dao.targetLocation.TargetLocationMapper;
import com.sdkj.map.domain.po.ClientUser;
import com.sdkj.map.domain.po.TargetLocation;
import com.sdkj.map.domain.vo.MobileResultVO;
import com.sdkj.map.service.LocationService;
import com.sdlh.common.DateUtilLH;
import com.sdlh.common.RedisTemplateUtil;
import com.sdlh.common.StringUtilLH;

@Service
public class LocationServiceImpl implements LocationService {
	Logger logger = LoggerFactory.getLogger(LocationServiceImpl.class);
	@Autowired
	private RedisTemplateUtil redisUtil;
	
	@Autowired
	private TargetLocationMapper targetLocationMapper;
	@Autowired
	private ClientUserMapper admWXUserMapper;
	@Value("${truck.driver.location.prefix}")
	private String truckDriverLocationPrefix;
	public Map<String,Object> uploadTruckDriverLocation(Map<String,String> param){
		String targetId = param.get("targetId");
		String targetType = param.get("targetType");
		String log = param.get("log");
		String lat = param.get("lat");
		Map<String,Object> result = new HashMap<String,Object>();
		redisUtil.set(truckDriverLocationPrefix+targetType+"_"+targetId, lat+","+log);
		TargetLocation targetLocation = new TargetLocation();
		targetLocation.setCreateTime(DateUtilLH.getCurrentTime());
		targetLocation.setTargetId(Long.valueOf(targetId));
		targetLocation.setTargetType(Integer.valueOf(targetType));
		targetLocation.setLat(lat);
		targetLocation.setLog(log);
		targetLocationMapper.insert(targetLocation);
		return result;
	}
	@Override
	public MobileResultVO findTruckDriverLocation(Map<String, String> param) {
		MobileResultVO result = new MobileResultVO();
		String targetId = param.get("targetId");
		String targetType = param.get("targetType");
		Map<String,Object> resultLocation = new HashMap<String,Object>();
		String location = redisUtil.get(truckDriverLocationPrefix+targetType+"_"+targetId)+"";
		logger.info("location:"+location);
		if(StringUtilLH.isNotEmpty(location)) {
			String[] locationArr = location.split(",");
			resultLocation.put("lat", locationArr[0]);
			resultLocation.put("log", locationArr[1]);
			result.setMessage(MobileResultVO.OPT_SUCCESS_MESSAGE);
		}else{
			TargetLocation lastLocation = targetLocationMapper.findSingleLocation(param);
			if(lastLocation!=null){
				resultLocation.put("lat", lastLocation.getLat());
				resultLocation.put("log", lastLocation.getLog());
				result.setMessage(MobileResultVO.OPT_SUCCESS_MESSAGE);
			}else{
				result.setCode(MobileResultVO.CODE_FAIL);
				result.setMessage(MobileResultVO.OPT_FAIL_MESSAGE);
			}
		}
		result.setData(resultLocation);
		return result;
	}
	@Override
	public MobileResultVO findTruckLocationHistroy(Map<String, Object> param) {
		MobileResultVO result = new MobileResultVO();
		Map<String,Object> userParam = new HashMap<String,Object>();
		userParam.put("account", param.get("account"));
		userParam.put("userType", 2);
		ClientUser user = admWXUserMapper.findSingleUser(userParam);
		if(user!=null){
			param.put("targetId", user.getId());
			param.put("targetType", 2);
			param.put("queryDate", "2018-10-15");
			List<Map<String,Object>> locationList = this.targetLocationMapper.findTargetLocationHistory(param);
			result.setData(locationList);
		}
		return result;
	}
}
