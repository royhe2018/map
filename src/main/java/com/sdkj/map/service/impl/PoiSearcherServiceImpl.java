package com.sdkj.map.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Transactional
public class PoiSearcherServiceImpl implements PoiSearcherService {
	
	private Logger logger = LoggerFactory.getLogger(PoiSearcherServiceImpl.class);
	
	private static final String url = "https://restapi.amap.com/v3/assistant/inputtips";
	
	private static final String distanceUrl = "https://restapi.amap.com/v3/distance";
	
	private static final String roadDistanceUrl = "https://restapi.amap.com/v3/direction/driving";
	@Autowired
	private DriverTraceMapper driverTraceMapper;
	@Autowired
	private ShikraMapSerivce shikraMapSerivce;
	@Autowired
	private DriverInfoMapper driverInfoMapper;
	
	@Value("${gaoDe.map.web.api.key}")
	private String gaoDeMapWebApiKey;
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
			List<Integer> approDriverIndexArr=new ArrayList<Integer>();
			String destlocation="108.855413,34.197591";
			String origins="108.854726,34.200643|108.883393,34.214485|108.868201,34.194893|108.868116,34.210439";
			test.findDistanceDriver(destlocation, origins, approDriverIndexArr,0,5000);
			if(approDriverIndexArr.size()>0) {
				for(Integer idx:approDriverIndexArr) {
					System.out.println(idx);
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public MobileResultVO queryNearlyDriver(Position position, String cityName,
			int distance) {
		MobileResultVO result = new MobileResultVO();
		Map<String,Object> param = new HashMap<String,Object>();
		//查当前城市在线司机
		param.put("registerCity", cityName);
		param.put("onDutyStatus", 2);
		List<DriverInfo> driverList = driverInfoMapper.findDriverList(param);
		List<DriverInfo> destDriverList = new ArrayList<DriverInfo>();
		String origins = "";
		String destLocation = position.getLog()+","+position.getLat();
		if(driverList!=null && driverList.size()>0){
			List<Integer> approDriverIndexArr = new ArrayList<Integer>();
			int baseIndex =0;
			for(int i=0;i<driverList.size();i++){
				DriverInfo item = driverList.get(i);
				param.clear();
				param.put("driverPhone", item.getDriverPhone());
				param.put("date", DateUtilLH.getCurrentDate());
				DriverTrace trace = driverTraceMapper.findSingleTrace(param);
				if(trace!=null){
					MobileResultVO locationResult = shikraMapSerivce.findTerminalCurrentLocation(item.getMapIerminalId(), trace.getTraceId());
					JsonNode data = JsonUtil.convertObjToJson(locationResult.getData());
					if(data!=null && data.has("location")) {
						String location = data.get("location").asText();
						if(i>0 && i%90==0) {
							origins +=location;
							findDistanceDriver(destLocation,origins,approDriverIndexArr,baseIndex,distance);
							baseIndex +=90;
							origins = "";
						}else {
							origins +=location+"|";
						}
					}
				}
			}
			if(origins!=null && origins.length()>0) {
				origins = origins.substring(0, origins.length()-1);
				findDistanceDriver(destLocation,origins,approDriverIndexArr,baseIndex,distance);
			}
			if(approDriverIndexArr!=null && approDriverIndexArr.size()>0) {
				for(int j=0;j<approDriverIndexArr.size();j++) {
					destDriverList.add(driverList.get(approDriverIndexArr.get(j)));
				}
			}
			
		}
		
		result.setData(destDriverList);
		return result;
	}
	
	private void findDistanceDriver(String destlocation,String origins,List<Integer> approDriverIndexArr,int baseIndex,int limitDistance) {
		try {
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("key", gaoDeMapWebApiKey);
			param.put("origins", origins);
			param.put("destination", destlocation);
			param.put("output", "JSON");
			JsonNode result = HttpsUtil.doGet(distanceUrl, param);
			if(result!=null && result.has("results")) {
				JsonNode resultList = result.get("results");
				for(int i=0;i<resultList.size();i++) {
					JsonNode item = resultList.get(i);
					Integer originIdx = item.get("origin_id").asInt();
					Integer distance= item.get("distance").asInt();
					if(distance<limitDistance) {
						approDriverIndexArr.add(baseIndex+originIdx-1);
					}
				}
			}
			logger.info("result:"+result);
		}catch(Exception e) {
			logger.error("获取距离失败", e);
		}
	}
	@Override
	public MobileResultVO caculateRoutDistance(String origin, String destination, String waypoints) {
		MobileResultVO result = new MobileResultVO();
		try {
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("key", gaoDeMapWebApiKey);
			param.put("origin", origin);
			param.put("destination", destination);
			param.put("size", 1);
			param.put("showpolyline", 0);
			param.put("nosteps", 1);
			param.put("cartype", 1);
			param.put("strategy", 2);
			if(StringUtils.isNotEmpty(waypoints)) {
				param.put("waypoints", waypoints);
			}
			param.put("output", "JSON");
			JsonNode distanceResult = HttpsUtil.doGet(roadDistanceUrl, param);
			if(distanceResult!=null && distanceResult.has("route")) {
				JsonNode routeInfo = distanceResult.get("route");
				if(routeInfo!=null && routeInfo.has("paths")) {
					JsonNode paths = routeInfo.get("paths");
					int minDistance = 999999;
					if(paths!=null && paths.size()>0) {
						for(JsonNode path:paths) {
							int itemDistance = path.get("distance").asInt();
							if(itemDistance<minDistance) {
								minDistance = itemDistance;
							}
						}
					}
					float i=1000;
					int kilometer = (int)Math.ceil(minDistance/i);
					result.setCode(MobileResultVO.CODE_SUCCESS);
					result.setMessage(MobileResultVO.OPT_SUCCESS_MESSAGE);
					result.setData(kilometer);
				}
			}
		}catch(Exception e) {
			logger.error("获取路线距离失败", e);
		}
		return result;
	}
 
}
