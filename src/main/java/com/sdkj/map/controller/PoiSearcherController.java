package com.sdkj.map.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.sdkj.map.domain.vo.MobileResultVO;
import com.sdkj.map.domain.vo.Position;
import com.sdkj.map.service.PoiSearcherService;

@Controller
public class PoiSearcherController {
	Logger logger = LoggerFactory.getLogger(LocationController.class);
	
	@Autowired
	private PoiSearcherService poiSearcherService;
	
    @RequestMapping(value="/poi/searcher",method=RequestMethod.GET)
	@ResponseBody
	public MobileResultVO sendPhoneRegisterSmsCheckCode(HttpServletRequest req) {
    	MobileResultVO result = new MobileResultVO();
		try {
			String keywords = req.getParameter("keywords");
			String lat = req.getParameter("lat");
			String lot = req.getParameter("log");
			String city = req.getParameter("city");
			String log = req.getParameter("log");
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("key", "9d28f5fda07db528d149fc98f40d2d75");
			param.put("keywords", "高新");
			JsonNode poiResult = poiSearcherService.findPoiInfo(param);
			result.setData(poiResult);
			result.setMessage(MobileResultVO.OPT_SUCCESS_MESSAGE);
		}catch(Exception e) {
			logger.error("注册验证码发送异常", e);
			result.setCode(MobileResultVO.CODE_FAIL);
			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
		}
		return result;
	}
    
    @RequestMapping(value="/query/distance",method=RequestMethod.GET)
	@ResponseBody
    public MobileResultVO caculateDistance(HttpServletRequest req){
    	MobileResultVO result = null;
		try {
			String latStart = req.getParameter("latStart");
			String logStart = req.getParameter("logStart");
			String latEnd = req.getParameter("latEnd");
			String logEnd = req.getParameter("logEnd");
			Position start = new Position();
			start.setLat(Double.valueOf(latStart));
			start.setLog(Double.valueOf(logStart));
			Position end = new Position();
			end.setLat(Double.valueOf(latEnd));
			end.setLog(Double.valueOf(logEnd));
			result = poiSearcherService.getDistance(start, end);
		}catch(Exception e) {
			logger.error("计算两点距离异常", e);
			result = new MobileResultVO();
			result.setCode(MobileResultVO.CODE_FAIL);
			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
		}
		return result;
    }
    
    @RequestMapping(value="/query/nearly/driver",method=RequestMethod.GET)
	@ResponseBody
    public MobileResultVO queryNearlyDriver(HttpServletRequest req){
    	MobileResultVO result = null;
		try {
			String lat = req.getParameter("lat");
			String log = req.getParameter("log");
			String cityName = req.getParameter("cityName");
			String distance = req.getParameter("distance");
			Position position = new Position();
			position.setLat(Double.valueOf(lat));
			position.setLog(Double.valueOf(log));
			result = poiSearcherService.queryNearlyDriver(position, cityName,Double.valueOf(distance));
		}catch(Exception e) {
			logger.error("计算两点距离异常", e);
			result = new MobileResultVO();
			result.setCode(MobileResultVO.CODE_FAIL);
			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
		}
		return result;
    }
}
