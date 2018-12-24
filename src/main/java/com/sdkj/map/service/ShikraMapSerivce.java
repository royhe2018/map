package com.sdkj.map.service;

import java.util.List;
import java.util.Map;

import com.sdkj.map.domain.vo.MobileResultVO;

public interface ShikraMapSerivce {
	public MobileResultVO addService();
	
	public MobileResultVO addTerminal(Map<String,Object> param);
	/**
	 * 查询终端当前位置
	 * @param terminalList
	 * @return
	 */
	public MobileResultVO findTerminalPosition(List<Map<String,Object>> terminalList);
	/**
	 * 查询某一终端轨迹
	 * @param terminalId
	 * @return
	 */
	public MobileResultVO findTerminalTrackQuery(String terminalId);
	
	/**
	 * 查询某一终端当天轨迹ID
	 * @param phoneNumber
	 * @return
	 */
	public MobileResultVO findTerminalTrackId(String phoneNumber);
	
	/**
	 * 查询终端当前位置
	 * @param terminalId
	 * @param traceId
	 * @return
	 */
	public MobileResultVO findTerminalCurrentLocation(String terminalId,String traceId);
	/**
	 * 查询终端行驶轨迹
	 * @param terminalId
	 * @param traceId
	 * @return
	 */
	public MobileResultVO findTerminalTraceRoute(String terminalId,String traceId);
}
