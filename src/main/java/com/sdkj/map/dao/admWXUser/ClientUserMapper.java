package com.sdkj.map.dao.admWXUser;

import java.util.Map;

import com.sdkj.map.domain.po.ClientUser;

public interface ClientUserMapper {
    ClientUser findSingleUser(Map<String,Object> param);

    int updateUser(ClientUser record);
}