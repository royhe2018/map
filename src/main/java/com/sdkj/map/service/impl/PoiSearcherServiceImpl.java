package com.sdkj.map.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.sdkj.map.dao.driverInfo.DriverInfoMapper;
import com.sdkj.map.dao.driverTrace.DriverTraceMapper;
import com.sdkj.map.domain.po.DriverInfo;
import com.sdkj.map.domain.po.DriverTrace;
import com.sdkj.map.domain.vo.MobileResultVO;
import com.sdkj.map.domain.vo.Position;
import com.sdkj.map.service.PoiSearcherService;
import com.sdkj.map.service.ShikraMapSerivce;
import com.sdlh.common.DateUtilLH;
import com.sdlh.common.HttpsUtil;
import com.sdlh.common.JsonUtil;

@Service
public class PoiSearcherServiceImpl implements PoiSearcherService {
	
	private static final String url = "https://restapi.amap.com/v3/assistant/inputtips";
	
	@Autowired
	private DriverTraceMapper driverTraceMapper;
	@Autowired
	private ShikraMapSerivce shikraMapSerivce;
	@Autowired
	private DriverInfoMapper driverInfoMapper;
	@Override
	public JsonNode findPoiInfo(Map<String, Object> param) throws Exception {
		param.put("key", "9d28f5fda07db528d149fc98f40d2d75");
		param.put("keywords", "高新");
		param.put("city", "西安");
		param.put("type", "120302|130104|130601|090101");
		param.put("citylimit", "true");
		JsonNode result = HttpsUtil.doGet(url, param);
		return result;
	}
	public MobileResultVO  getDistance(Position start, Position end) {
		MobileResultVO result = new MobileResultVO();
		result.setCode(MobileResultVO.CODE_SUCCESS);
		result.setMessage(MobileResultVO.OPT_SUCCESS_MESSAGE);
        double lon1 = (Math.PI / 180) * start.getLog();
        double lon2 = (Math.PI / 180) * end.getLog();
        double lat1 = (Math.PI / 180) * start.getLat();
        double lat2 = (Math.PI / 180) * end.getLat();
        // 地球半径
        double R = 6371;
        // 两点间距离 km，如果想要米的话，结果*1000就可以了
        double d = Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1))
                * R;
        //distance = d * 1000;
        Map<String,Object> distanceMap = new HashMap<String,Object>();
        BigDecimal distance = new BigDecimal(d);
        BigDecimal setScale = distance.setScale(1,BigDecimal.ROUND_HALF_UP);
        distanceMap.put("distance", setScale);
        result.setData(distanceMap);
        return result;
    }
 
	public static void main(String[] args) {
		try {
			PoiSearcherServiceImpl test = new PoiSearcherServiceImpl();
			JsonNode result = test.findPoiInfo(new HashMap<String,Object>());
			System.out.println(result);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public MobileResultVO queryNearlyDriver(Position position, String cityName,
			Double distance) {
		MobileResultVO result = new MobileResultVO();
		Map<String,Object> param = new HashMap<String,Object>();
		//查当前城市在线司机
		param.put("registerCity", cityName);
		param.put("onDutyStatus", 2);
		List<DriverInfo> driverList = driverInfoMapper.findDriverList(param);
		List<DriverInfo> destDriverList = new ArrayList<DriverInfo>();
		if(driverList!=null && driverList.size()>0){
			for(DriverInfo item:driverList){
				param.clear();
				param.put("driverPhone", item.getDriverPhone());
				param.put("date", DateUtilLH.getCurrentDate());
				DriverTrace trace = driverTraceMapper.findSingleTrace(param);
				if(trace!=null){
					MobileResultVO locationResult = shikraMapSerivce.findTerminalCurrentLocation(item.getMapIerminalId(), trace.getTraceId());
					JsonNode data = JsonUtil.convertObjToJson(locationResult.getData());
					String location = data.get("location").asText();
					String[] positonArr = location.split(",");
					Position destPosition = new Position();
					destPosition.setLat(Double.valueOf(positonArr[0]));
					destPosition.setLog(Double.valueOf(positonArr[0]));
					MobileResultVO distanceMap = getDistance(position,destPosition);
					JsonNode distanceNum = JsonUtil.convertObjToJson(distanceMap.getData());
					double realDistance = distanceNum.get("distance").asDouble();
					if(distance>=realDistance){
						destDriverList.add(item);
					}
				}
			}
		}
		result.setData(destDriverList);
		return result;
	}
}
