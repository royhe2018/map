package com.sdkj.map.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.sdkj.map.dao.admWXUser.ClientUserMapper;
import com.sdkj.map.dao.driverTrace.DriverTraceMapper;
import com.sdkj.map.domain.po.ClientUser;
import com.sdkj.map.domain.po.DriverTrace;
import com.sdkj.map.domain.vo.MobileResultVO;
import com.sdkj.map.service.ShikraMapSerivce;
import com.sdlh.common.DateUtilLH;
import com.sdlh.common.HttpsUtil;
import com.sdlh.common.JsonUtil;
import com.sdlh.common.StringUtilLH;

@Service
public class ShikraMapSerivceImpl implements ShikraMapSerivce {
	
	Logger logger = LoggerFactory.getLogger(ShikraMapSerivceImpl.class);
	
	private static final String gaoDeMapWebApiKey = "9d28f5fda07db528d149fc98f40d2d75";
	
	private static final String addServiceUrl = "https://tsapi.amap.com/v1/track/service/add";
	
	private static final String queryServiceUrl = "https://tsapi.amap.com/v1/track/service/list";
	
	private static final String addTerminalUrl="https://tsapi.amap.com/v1/track/terminal/add";
	
	//private static final String queryTerminalUrl="https://tsapi.amap.com/v1/track/terminal/search?key=9d28f5fda07db528d149fc98f40d2d75&sid=8914&keywords=13058103932";
	
	private static final String addTraceUrl = "https://tsapi.amap.com/v1/track/trace/add";
	
	private static final String findTerminalCurrentLocationUrl = "https://tsapi.amap.com/v1/track/terminal/lastpoint";
	
	private static final String findTerminalTraceRouteUrl = "https://tsapi.amap.com/v1/track/terminal/trsearch";
	
	private static final String queryTerminalUrl = "https://tsapi.amap.com/v1/track/terminal/list";
	@Autowired
	private DriverTraceMapper driverTraceMapper;
	@Autowired
	private ClientUserMapper clientUserMapper;
	@Override
	public MobileResultVO addService() {
		MobileResultVO result = new MobileResultVO();
		try{
			Map<String,Object> param = new HashMap<String,Object>(); 
			param.put("key", gaoDeMapWebApiKey);
			param.put("name", "sldh_map_service");
			param.put("desc", "顺道拉货地图定位服务");
			JsonNode addServiceResult = HttpsUtil.doPost(addServiceUrl, null, param);
			logger.info(addServiceResult.asText());
			result.setData(result);
		}catch(Exception e){
			logger.error("添加服务异常", e);
			result.setCode(MobileResultVO.CODE_FAIL);
			result.setMessage(MobileResultVO.OPT_FAIL_MESSAGE);
		}
		return result;
	}

	public MobileResultVO queryService() {
		MobileResultVO result = new MobileResultVO();
		try{
			Map<String,Object> param = new HashMap<String,Object>(); 
			param.put("key", gaoDeMapWebApiKey);
			JsonNode queryServiceResult = HttpsUtil.doGet(queryServiceUrl, param);
			result.setData(queryServiceResult);
			//{"desc":"顺道拉货地图定位服务","name":"sldh_map_service","sid":8914}
			logger.info(queryServiceResult.toString());
		}catch(Exception e){
			logger.error("添加服务异常", e);
			result.setCode(MobileResultVO.CODE_FAIL);
			result.setMessage(MobileResultVO.OPT_FAIL_MESSAGE);
		}
		return result;
	}
	@Override
	public MobileResultVO addTerminal(Map<String, Object> param) {
		MobileResultVO result = new MobileResultVO();
		result.setCode(MobileResultVO.CODE_FAIL);
		result.setMessage(MobileResultVO.OPT_FAIL_MESSAGE);
		try{
			String phoneNumber = param.get("phoneNumber")+"";
			param.put("account", phoneNumber);
			param.put("userType", 2);
			ClientUser user = clientUserMapper.findSingleUser(param);
			if(user!=null){
				if(StringUtilLH.isNotEmpty(user.getMapTerminalId())){
					Map<String,Object> terminalMap = new HashMap<String,Object>();
					terminalMap.put("sid", 8914);
					terminalMap.put("name", phoneNumber);
					terminalMap.put("tid", user.getMapTerminalId());
					result.setData(terminalMap);
					result.setCode(MobileResultVO.CODE_SUCCESS);
					result.setMessage(MobileResultVO.OPT_SUCCESS_MESSAGE);
				}else{
					Map<String,Object> mapParam = new HashMap<String,Object>(); 
					mapParam.put("key", gaoDeMapWebApiKey);
					mapParam.put("sid", 8914);
					mapParam.put("name", phoneNumber);
					mapParam.put("props", JsonUtil.convertObjectToJsonStr(param));
					JsonNode addTerminalResult = HttpsUtil.doPost(addTerminalUrl, null, mapParam);
					logger.info(addTerminalResult.toString());
					if(addTerminalResult.has("data") && "10000".equals(addTerminalResult.get("errcode").asText())){
						user.setMapTerminalId(addTerminalResult.get("data").get("tid").asText());
						clientUserMapper.updateUser(user);
						result.setData(addTerminalResult.get("data"));
						result.setCode(MobileResultVO.CODE_SUCCESS);
						result.setMessage(MobileResultVO.OPT_SUCCESS_MESSAGE);
					}else{
						JsonNode terminalListInfo = this.queryTerminal(phoneNumber);
						if(terminalListInfo.has("data") && "10000".equals(terminalListInfo.get("errcode").asText())){
							JsonNode terminalInfo = terminalListInfo.get("data").get("results").get(0);
							String mapTerminalId = terminalInfo.get("tid").toString();
							if(StringUtils.isNotBlank(mapTerminalId)){
								user.setMapTerminalId(mapTerminalId);
								clientUserMapper.updateUser(user);
								result.setData(terminalInfo);
								result.setCode(MobileResultVO.CODE_SUCCESS);
								result.setMessage(MobileResultVO.OPT_SUCCESS_MESSAGE);
							}
						}
					}
				}
			}
		}catch(Exception e){
			logger.error("添加终端异常", e);
			result.setCode(MobileResultVO.CODE_FAIL);
			result.setMessage(MobileResultVO.OPT_FAIL_MESSAGE);
		}
		return result;
	}

	
	
	@Override
	public MobileResultVO findTerminalPosition(
			List<Map<String, Object>> terminalList) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MobileResultVO findTerminalTrackQuery(String terminalId) {
		// TODO Auto-generated method stub
		return null;
	}
	

	@Override
	public MobileResultVO findTerminalTrackId(String driverPhone) {
		MobileResultVO result = new MobileResultVO();
		result.setCode(MobileResultVO.CODE_FAIL);
		result.setMessage(MobileResultVO.OPT_FAIL_MESSAGE);
		try{
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("driverPhone", driverPhone);
			param.put("date", DateUtilLH.getCurrentDate());
			DriverTrace trace = this.driverTraceMapper.findSingleTrace(param);
			if(trace!=null){
				Map<String,Object> traceResult = new HashMap<String,Object>();
				traceResult.put("trid", trace.getTraceId());
				result.setCode(MobileResultVO.CODE_SUCCESS);
				result.setMessage(MobileResultVO.OPT_SUCCESS_MESSAGE);
				result.setData(traceResult);
			}else{
				param.clear();
				param.put("account", driverPhone);
				param.put("userType", 2);
				ClientUser user = clientUserMapper.findSingleUser(param);
				if(user!=null && StringUtilLH.isNotEmpty(user.getMapTerminalId())){
					param.clear();
					param.put("key", gaoDeMapWebApiKey);
					param.put("sid", 8914);
					param.put("tid", user.getMapTerminalId());
					JsonNode addTraceResult = HttpsUtil.doPost(addTraceUrl, null, param);
					if(addTraceResult.has("data") && "10000".equals(addTraceResult.get("errcode").asText())){
						trace = new DriverTrace();
						trace.setDriverPhone(driverPhone);
						trace.setDate(DateUtilLH.getCurrentDate());
						trace.setTraceId(addTraceResult.get("data").get("trid").asText());
						driverTraceMapper.insert(trace);
						result.setData(addTraceResult.get("data"));
						logger.info(addTraceResult.toString());
						result.setCode(MobileResultVO.CODE_SUCCESS);
						result.setMessage(MobileResultVO.OPT_SUCCESS_MESSAGE);
					}
				}
			}
		}catch(Exception e){
			logger.error("获取轨迹ID异常", e);
		}
		return result;
	}

	@Override
	public MobileResultVO findTerminalCurrentLocation(String terminalId,String traceId) {
		MobileResultVO result = new MobileResultVO();
		result.setCode(MobileResultVO.CODE_FAIL);
		result.setMessage(MobileResultVO.OPT_FAIL_MESSAGE);
		try{
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("key", gaoDeMapWebApiKey);
			param.put("sid", 8914);  
			param.put("tid", terminalId);
			param.put("trid", traceId);
			param.put("correction", "n");
			JsonNode addTraceResult = HttpsUtil.doGet(findTerminalCurrentLocationUrl,param);
			if(addTraceResult.has("data") && "10000".equals(addTraceResult.get("errcode").asText())){
				result.setData(addTraceResult.get("data"));
				logger.info(addTraceResult.toString());
				result.setCode(MobileResultVO.CODE_SUCCESS);
				result.setMessage(MobileResultVO.OPT_SUCCESS_MESSAGE);
			}
		}catch(Exception e){
			logger.error("获取轨迹信息异常", e);
		}
		return result;
	}

	@Override
	public MobileResultVO findTerminalTraceRoute(String terminalId,String traceId) {
		MobileResultVO result = new MobileResultVO();
		result.setCode(MobileResultVO.CODE_FAIL);
		result.setMessage(MobileResultVO.OPT_FAIL_MESSAGE);
		try{
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("key", gaoDeMapWebApiKey);
			param.put("sid", 8914);
			param.put("tid", terminalId);
			param.put("trid", traceId);
			param.put("recoup", 1);
			param.put("gap", 1000);
			param.put("pagesize", 900);
			Map<String,Object> correction = new HashMap<String,Object>();
			correction.put("denoise", "1");
			correction.put("mapmatch", "1");
			correction.put("attribute", "1");
			param.put("correction", "denoise=1,mapmatch=1,attribute=0,threshold=100,mode=driving");
			ArrayNode allPointList = null;
			for(int page=1;;page++) {
				param.put("page", page);
				JsonNode addTraceResult = HttpsUtil.doGet(findTerminalTraceRouteUrl,param);
				logger.info(addTraceResult.toString());
				if(addTraceResult.has("data") && "10000".equals(addTraceResult.get("errcode").asText())
						&& !addTraceResult.get("data").isNull()){
					JsonNode points = addTraceResult.get("data").get("tracks").get(0).get("points");
					if(points!=null && points.size()>0) {
						logger.info("points size:"+points.size()+"");
						ArrayNode  pageNode= (ArrayNode)points;
						if(allPointList==null) {
							allPointList = pageNode;
						}else {
							allPointList.addAll(pageNode);
						}
//						if(points!=null) {
//							int i=0;
//							for(JsonNode pointItem:points) {
//								i++;
//								logger.info("location:"+pointItem.get("location").asText());
//								String locatetime = pointItem.get("locatetime").asText();
//								logger.info("locatetime:"+locatetime);
//								Date time = new Date();
//								time.setTime(Long.valueOf(locatetime));
//								logger.info("convert locatetime:"+DateUtilLH.convertDate2Str(time, "yyyy-MM-dd HH:mm:ss"));
//							}
//							logger.info("point size :"+i);
//						}
						if(pageNode.size()<900) {
							break;
						}
					}else {
						break;
					}
					
				}else {
					break;
				}
			}
			if(allPointList!=null && allPointList.size()>0) {
				logger.info("allPointList size:"+allPointList.size()+"");
				result.setData(allPointList);
				result.setCode(MobileResultVO.CODE_SUCCESS);
				result.setMessage(MobileResultVO.OPT_SUCCESS_MESSAGE);
			}
		}catch(Exception e){
			logger.error("获取轨迹信息异常", e);
		}
		return result;
	}
	
	public  JsonNode queryTerminal(String name){
		try{
			Map<String,Object> param = new HashMap<String,Object>(); 
			param.put("key", gaoDeMapWebApiKey);
			param.put("sid", 8914);
			param.put("name", name);
			JsonNode queryTerminalResult = HttpsUtil.doGet(queryTerminalUrl, param);
			//{"desc":"顺道拉货地图定位服务","name":"sldh_map_service","sid":8914}
			logger.info(queryTerminalResult.toString());
			return queryTerminalResult;
		}catch(Exception e){
			logger.error("查询终端异常", e);
		}
		return null;
	}
	
	public static void main(String[] args){
		ShikraMapSerivceImpl test = new ShikraMapSerivceImpl();
		test.findTerminalCurrentLocation("1778513","1600");
	}
}
