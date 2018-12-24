package com.sdkj.map.dao.targetLocation;

import java.util.List;
import java.util.Map;

import com.sdkj.map.domain.po.TargetLocation;

public interface TargetLocationMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TargetLocation record);

    TargetLocation findSingleLocation(Map<String,String> param);

    int updateByPrimaryKeySelective(TargetLocation record);

    int updateByPrimaryKey(TargetLocation record);
    
    public List<Map<String,Object>> findTargetLocationHistory(Map<String,Object> param);
}