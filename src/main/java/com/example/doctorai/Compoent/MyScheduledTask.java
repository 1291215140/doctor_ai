package com.example.doctorai.Compoent;

import com.alibaba.fastjson.JSONObject;
import com.example.doctorai.service.HttpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MyScheduledTask {
    @Value("${access_token}")
    public String access_token;
    @Value("${appid}")
    private String appid;
    @Value("${secret}")
    private String secret;
    @Autowired
    private HttpService getHttp;
    //设置定时任务
    // 每隔60分钟执行一次
    @Scheduled(fixedRate = 300000)
    public void myTask() {
        log.info("定时任务执行");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("grant_type", "client_credential");
        jsonObject.put("appid",appid);
        jsonObject.put("secret", secret);
        JSONObject access_token_obejct = JSONObject.parseObject(getHttp.sendGetRequest("https://api.weixin.qq.com/cgi-bin/token",jsonObject));
        this.access_token = access_token_obejct.getString("access_token");
        // 输出更新后的值
        log.info("my.property updated to: " + access_token);
        int access_token_expires_in = (int) access_token_obejct.get("expires_in");

    }
}


