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
import com.sdkj.map.service.ShikraMapSerivce;
import com.sdlh.common.StringUtilLH;

@Controller
public class ShikraMapController {
	
	Logger logger = LoggerFactory.getLogger(ShikraMapController.class);
	
	@Autowired				 
	private ShikraMapSerivce shikraMapService; 
	
    @RequestMapping(value="/add/terminal",method=RequestMethod.POST)
	@ResponseBody
	public MobileResultVO addTerminal(HttpServletRequest req){
    	MobileResultVO result = null;
    	try{
    		String phoneNumber = req.getParameter("phoneNumber");
    		String driverId = req.getParameter("driverId");
    		String carNo = req.getParameter("carNo");
    		Map<String,Object> param = new HashMap<String,Object>();
    		if(StringUtilLH.isNotEmpty(carNo)){
    			param.put("carNo", carNo);
    		}
    		if(StringUtilLH.isNotEmpty(phoneNumber)){
    			param.put("phoneNumber", phoneNumber);
    		}
    		if(StringUtilLH.isNotEmpty(driverId)){
    			param.put("driverId", driverId);
    		}
    		result = shikraMapService.addTerminal(param);
    	}catch(Exception e){
    		logger.error("添加终端异常",e);
    		result = new MobileResultVO();
    		result.setCode(MobileResultVO.CODE_FAIL);
    		result.setMessage("添加终端异常");
    	}
    	return result;
	}
    
    @RequestMapping(value="/find/terminal/trace",method=RequestMethod.POST)
	@ResponseBody
	public MobileResultVO findTerminalTrace(HttpServletRequest req){
    	MobileResultVO result = null;
    	try{
    		String phoneNumber = req.getParameter("phoneNumber");
    		result = shikraMapService.findTerminalTrackId(phoneNumber);
    	}catch(Exception e){
    		logger.error("添加终端异常",e);
    		result = new MobileResultVO();
    		result.setCode(MobileResultVO.CODE_FAIL);
    		result.setMessage("添加终端异常");
    	}
    	return result;
	}
    
    
    @RequestMapping(value="/query/terminal/locationt",method=RequestMethod.GET)
	@ResponseBody
	public MobileResultVO queryTruckLocation(HttpServletRequest req,HttpServletResponse response) {
    	MobileResultVO result = null;
		try {
			String terminalId = req.getParameter("terminalId");
			String traceId = req.getParameter("traceId");
			result = shikraMapService.findTerminalCurrentLocation(terminalId, traceId);
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
    
    @RequestMapping(value="/query/terminal/trace",method=RequestMethod.GET)
	@ResponseBody
	public MobileResultVO queryTruckLocationTrace(HttpServletRequest req,HttpServletResponse response) {
    	MobileResultVO result = null;
		try {
			String terminalId = req.getParameter("terminalId");
			String traceId = req.getParameter("traceId");
			String startTime = req.getParameter("startTime");
			String endTime = req.getParameter("endTime");
			result = shikraMapService.findTerminalTraceRoute(terminalId, traceId,startTime,endTime);
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
    
    @RequestMapping(value="/find/nearly/terminal")
	@ResponseBody
	public MobileResultVO findNearlyTerminal(HttpServletRequest req){
    	MobileResultVO result = null;
    	try{
    		String lat = req.getParameter("lat");
    		String lon = req.getParameter("lon");
    		String radius = req.getParameter("radius");
    		result = shikraMapService.findNearlyTerminal(lon,lat,radius);
    	}catch(Exception e){
    		logger.error("查询附近终端异常",e);
    		result = new MobileResultVO();
    		result.setCode(MobileResultVO.CODE_FAIL);
    		result.setMessage("查询附近终端异常");
    	}
    	return result;
	}
}
