package com.maple.game.osee.service;

import com.maple.engine.data.ServerUser;
import com.maple.game.osee.entity.gm.CommonResponse;
import com.maple.game.osee.proto.ActiveList;

import java.util.Map;

public interface ActiveService {

    void getActiveList(ActiveList.GetActiveListRequest request, ServerUser user);

    void activeConfigPut(Map<String, Object> paramMap, CommonResponse response);

    void activeConfigPage(Map<String, Object> paramMap, CommonResponse response);

    void getHeadImageList(ActiveList.GetHeadImageListRequest request, ServerUser user);

}
