package com.example.doctorai.control;

import com.alibaba.fastjson.JSONObject;
import com.example.doctorai.Compoent.MyScheduledTask;
import com.example.doctorai.bool.str_boo;
import com.example.doctorai.data.loginData;
import com.example.doctorai.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.example.doctorai.mapper.*;
@Slf4j
@Controller
public class LoginControl {
    @Autowired
    private HttpService getHttp;
    @Autowired
    private MyScheduledTask task;//获取access_token用
    @Autowired
    RedisService redisService;//获取redis用
    @Autowired
    usermapper usermapper;
    @Value("${appid}")
    String appid;
    @Value("${secret}")
    String secret;
    @ResponseBody
    @RequestMapping("/islogin")
    public JSONObject islogin(HttpServletRequest request, HttpServletResponse response)
    {
        JSONObject params=new JSONObject();
        String code = request.getParameter("code");
        if(code == null||code.length()==0){
            params.put("stauts",100);
            params.put("erromsg","code为空");
        }
        else {
            //构造请求参数
            JSONObject req = new JSONObject();
            req.put("js_code", code);
            req.put("appid",appid);
            req.put("secret",secret);
            req.put("grant_type","authorization_code");
            JSONObject res = (JSONObject) JSONObject.parse(getHttp.sendGetRequest("https://api.weixin.qq.com/sns/jscode2session", req));
            log.info("islogin:"+res.toString());
            if (res.get("openid")!=null)
            {
                loginData loginData = usermapper.getuser(res.get("openid").toString());
                if(loginData==null)
                {
                    params.put("stauts",200);
                    params.put("islogin",0);
                    params.put("erromsg","未注册,先注册");
                }
                else
                {
                    params.put("stauts",200);
                    params.put("islogin",1);
                    //将logindata数据填入
                    params.put("userinformation",loginData);
                    log.info("获取数据成功");
                }
                //无论是否登录过，都返回openid和token
                String openid = (String) res.get("openid");
                params.put("openid",openid);
                String Token =str_boo.generateRandomString(7);
                params.put("token", Token);
                redisService.set(openid,Token,0);
            }
            else
            {
                params.put("stauts",100);
                params.put("erromsg","获取oepnid错误,错误信息"+res.get("errmsg")+"错误码"+"openid");
            }

        }
        return params;
    }
    @ResponseBody
    @GetMapping("/register")
    public JSONObject userlogin(HttpServletRequest request, HttpServletResponse response)
    {
        JSONObject params=new JSONObject();
        String access_token=task.access_token;
        if(access_token.length()==0){
            params.put("stauts",100);
            params.put("erromsg","服务器卡住了，请等一下");
            log.info("服务器卡住了，请等一下");
        }
        loginData   loginData = new loginData();
        String code = request.getParameter("code");
        String openid = request.getParameter("openid");
        String token = request.getParameter("token");
        String phoneNumber = null;
        int age = request.getParameter("age")==null?18:Integer.parseInt(request.getParameter("age"));
        String sex = request.getParameter("sex")==null?"男":request.getParameter("sex");
        String other = request.getParameter("other")==null?"":request.getParameter("other");
        String name  = request.getParameter("name")==null?"白给":request.getParameter("name");
        String avatarUrl = request.getParameter("avatarUrl")==null?"http://112.74.58.124/meinv.jpg":request.getParameter("avatarUrl");
        if (code == null){
            params.put("stauts",100);
            params.put("erromsg","请调用https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/getPhoneNumber.html接口");
        }
        else if (token==null||openid==null||token.length()==0||openid.length()==0)
        {
            params.put("stauts",100);
            params.put("erromsg","请先调用islogin接口获取token和openid");
        }
        else if(redisService.get(openid,0)==null||!redisService.get(openid,0).equals(token))
        {
            params.put("stauts",100);
            params.put("erromsg","token错误,请先调用islogin接口获取token");
        }
        else
        {
            JSONObject req = new JSONObject();
            req.put("code", code);
            String getPhoneNumber_str = getHttp.sendPostRequest("https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token="+access_token, req.toString(),null
            );
            JSONObject getPhoneNumber = JSONObject.parseObject(getPhoneNumber_str);
            log.info(getPhoneNumber_str);
            if (getPhoneNumber.get("errcode").equals(0)) {
                JSONObject phone_info = getPhoneNumber.getJSONObject("phone_info");
                phoneNumber = phone_info.getString("phoneNumber");
                loginData.setPhonenumber(phoneNumber);
                loginData.setName(name);
                loginData.setSex(sex);
                loginData.setOther(other);
                loginData.setAge(age);
                loginData.setAvatarUrl(avatarUrl);
                loginData.setOpenid(openid);
                usermapper.updatauser(loginData);
                params.put("stauts",200);
                params.put("userinformation",loginData);
                if(usermapper.getuser(openid)!=null)usermapper.updatauser(loginData);
                else usermapper.insertuser(loginData);
                log.info("注册完成");
            }
            else {
                params.put("stauts",300);
                params.put("erromsg",getPhoneNumber.get("errmsg"));
                log.info(getPhoneNumber.get("errmsg").toString());
            }
        }
        return params;
    }

}
