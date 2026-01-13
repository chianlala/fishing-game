package com.jeesite.modules.util;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.setting.Setting;
import cn.hutool.setting.SettingUtil;
import com.jeesite.common.config.Global;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * 配置文件工具类
 */
@Slf4j
public class MySettingUtil {

    // 类似：-DsysEngineMountPath=/SysEngineMountPath-XiaoMei/
    public static final String MOUNT_PATH = System.getProperty("sysEngineMountPath");

    // 类似：-DrsaPublicKey=
    public static final String PUBLIC_KEY = System.getProperty("rsaPublicKey");

    public static final Setting SETTING;

    static {

        String mountPath = MOUNT_PATH == null ? "" : MOUNT_PATH;

        SETTING = SettingUtil.get(mountPath + "gameSetting.setting");

        Field props = ReflectUtil.getField(Global.class, "props");

        Map<String, String> propsValue = (Map) ReflectUtil.getStaticFieldValue(props);

        // 需要：把 jeesite的配置信息给替换掉
        propsValue.put("productName", SETTING.getStr("gmPlatformName", "管理后台"));

        log.info("SETTING：{}", SETTING);

        SETTING.autoLoad(true, ok -> {

            if (BooleanUtil.isTrue(ok)) {

                // 需要：把 jeesite的配置信息给替换掉
                propsValue.put("productName", SETTING.getStr("gmPlatformName", "管理后台"));

                MyCsvUtil.MAP.clear(); // 清除：csv缓存

                log.info("SETTING：{}", SETTING);

            }

        });

    }

    /**
     * 获取：rsa解密之后的值 备注：私钥加密，公钥解密
     */
    public static String getRsaValue(String key) {

        RSA rsa = new RSA(null, PUBLIC_KEY);

        return rsa.decryptStr(SETTING.getStr(key), KeyType.PublicKey);

    }

    /**
     * 获取：rsa加密之后的值 备注：私钥加密，公钥解密
     */
    public static String getRsaValue(String privateKeyStr, String value) {

        RSA rsa = new RSA(privateKeyStr, null);

        return rsa.encryptBase64(value, KeyType.PrivateKey);

    }

}
