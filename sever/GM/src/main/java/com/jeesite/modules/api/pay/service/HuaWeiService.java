package com.jeesite.modules.api.pay.service;

import com.jeesite.modules.osee.vo.CommonResponse;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface HuaWeiService {

    CommonResponse notify(HttpServletRequest request) throws IOException;
}
