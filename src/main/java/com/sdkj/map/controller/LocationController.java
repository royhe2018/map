package com.sdkj.map.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sdkj.map.domain.vo.MobileResultVO;
import com.sdkj.map.service.LocationService;

@Controller
public class LocationController {
	Logger logger = LoggerFactory.getLogger(LocationController.class);
	@Autowired
	private LocationService locationService;
    //@RequestMapping(value="/location/upload",method=RequestMethod.GET)
	@ResponseBody
	public MobileResultVO sendPhoneRegisterSmsCheckCode(HttpServletRequest req) {
    	MobileResultVO result = new MobileResultVO();
		try {
			String targetId = req.getParameter("targetId");
			String targetType = req.getParameter("targetType");
			String locationType = req.getParameter("locationType");
			String lat = req.getParameter("lat");
			String log = req.getParameter("log");
			Map<String,String> param = new HashMap<String,String>();
			param.put("targetId", targetId);
			param.put("targetType", targetType);
			param.put("locationType", locationType);
			param.put("lat", lat);
			param.put("log", log);
			locationService.uploadTruckDriverLocation(param);
			result.setMessage(MobileResultVO.OPT_SUCCESS_MESSAGE);
		}catch(Exception e) {
			logger.error("位置上报异常", e);
			result.setCode(MobileResultVO.CODE_FAIL);
			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
		}
		return result;
	}
    
    //@RequestMapping(value="/location/query",method=RequestMethod.GET)
	@ResponseBody
	public MobileResultVO queryTruckLocation(HttpServletRequest req) {
    	MobileResultVO result = null;
		try {
			String targetId = req.getParameter("targetId");
			String targetType = req.getParameter("targetType");
			Map<String,String> param = new HashMap<String,String>();
			param.put("targetId", targetId);
			param.put("targetType", targetType);
			result = locationService.findTruckDriverLocation(param);
		}catch(Exception e) {
			logger.error("位置查询异常", e);
			result = new MobileResultVO();
			result.setCode(MobileResultVO.CODE_FAIL);
			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
		}
		return result;
	}
    
    //@RequestMapping(value="/query/location/history",method=RequestMethod.GET)
	@ResponseBody
	public MobileResultVO queryLocationHistory(HttpServletRequest req,HttpServletResponse response) {
    	MobileResultVO result = null;
		try {
			String account = req.getParameter("account");
			String targetType = req.getParameter("targetType");
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("account", account);
			param.put("targetType", targetType);
			result = locationService.findTruckLocationHistroy(param);
			String originHeader = req.getHeader("Origin");
			response.setHeader("Access-Control-Allow-Origin", originHeader);
			response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE"); 
			response.setHeader("Access-Control-Max-Age", "0"); 
			response.setHeader("Access-Control-Allow-Headers", "Authorization,Origin, No-Cache, X-Requested-With, If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, Content-Type, X-E4M-With,userId,token"); 
			response.setHeader("Access-Control-Allow-Credentials", "true"); 
			response.setHeader("XDomainRequestAllowed","1"); 
		}catch(Exception e) {
			logger.error("位置查询异常", e);
			result = new MobileResultVO();
			result.setCode(MobileResultVO.CODE_FAIL);
			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
		}
		return result;
	}
}
