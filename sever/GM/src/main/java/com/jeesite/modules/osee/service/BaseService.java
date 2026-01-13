package com.jeesite.modules.osee.service;

import com.jeesite.modules.osee.config.GameApiConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

/**
 * 基本服务类，服务类共有的参数或属性
 *
 * @author zjl
 */
public class BaseService {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected RestTemplate restTemplate;

    @Autowired
    protected GameApiConfig apiConfig;
}
