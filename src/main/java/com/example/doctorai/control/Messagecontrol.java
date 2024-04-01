package com.example.doctorai.control;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.doctorai.mapper.usermessagesmapper;
import com.example.doctorai.service.*;
import com.example.doctorai.service.RedisService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.io.IOException;
@Slf4j
@Controller
public class Messagecontrol {
    @Autowired
    private HttpService gethttp;
    @Autowired
    private RedisService redisService;
    @Autowired
    usermessagesmapper  usermessagesmapper;
    @Value("${app_code}")
    String app_code;
    @Value("${api_key}")
    String api_key;
    @ResponseBody
    @PostMapping("/sendmessage")
    public JSONObject sendmessage(HttpServletRequest request,HttpServletResponse response)
    {
        JSONObject responsejsonObject  = new JSONObject();
        //获取参数
        String message = request.getParameter("message");
        String Token = request.getParameter("token");
        String openid = request.getParameter("openid");
        if(message==null||message.length()==0){
            responsejsonObject.put("status",100);
            responsejsonObject.put("erromsg","message为空");
        }
        else if (Token==null||Token.length()==0)
        {
            responsejsonObject.put("status",100);
            responsejsonObject.put("erromsg","Token为空");
        }
        else if(openid==null||openid.length()==0)
        {
            responsejsonObject.put("status",100);
            responsejsonObject.put("erromsg","openid为空");
        }
        else if(redisService.get(openid,0)==null||Token.compareTo(redisService.get(openid,0))!=0)
        {
            responsejsonObject.put("status",100);
            responsejsonObject.put("erromsg","Token错误");
        }
        else
        {
            //从reids取出历史消息并将其转换成对象
            String messages = redisService.get(openid,1);
//            //如果没有则从数据库取出
           if(messages==null||messages.length()==0) messages = usermessagesmapper.getusermessages(openid);
//            //如果还为空则从新生成
           if(messages==null||messages.length()==0) messages="[]";
            //序列化json字符串
            JSONArray aj = JSONArray.parseArray(messages);
            //创建一个json对象
            JSONObject jo = new JSONObject();
            jo.put("role","user");
            jo.put("content",message);
            //将json对象添加到json数组
            aj.add(jo);
            //完成请求体
            JSONObject body = new JSONObject();
            //这个aj起到上下文
            body.put("messages",aj);
            body.put("app_code",app_code);
            //发送请求
            String rsult = gethttp.sendPostRequest("https://api.link-ai.chat/v1/chat/completions", body.toJSONString(),api_key);
            JSONObject js = JSON.parseObject(rsult);
            JSONArray choices = js.getJSONArray("choices");
            JSONObject jsonObject = (JSONObject)choices.get(0);
            //继续！！！！！
            JSONObject mess = (JSONObject) jsonObject.get("message");
            aj.add(mess);
            String content = mess.getString("content");
            //返回数据
            responsejsonObject.put("status",200);
            responsejsonObject.put("message",content);
            log.info(aj.toString());
            //更新数据
            redisService.set(openid,aj.toString(),1);
        }
        return responsejsonObject;
    }
    //获取历史消息
    @ResponseBody
    @PostMapping("/gethistorymessage")
    public JSONObject getmessage(HttpServletRequest request)
    {
        JSONObject responsejsonObject  = new JSONObject();
        String openid = request.getParameter("openid");
        String Token = request.getParameter("token");
        log.info("getmessage: "+"openid:"+openid);
        log.info("getmessage: "+"Token:"+Token);
        if(openid==null||openid.length()==0)
        {
            responsejsonObject.put("status",100);
            responsejsonObject.put("erromsg","openid为空");
        }
        else if (Token==null||Token.length()==0) {
            responsejsonObject.put("status", 100);
            responsejsonObject.put("erromsg", "Token为空");
        }

        else if(redisService.get(openid,0)==null||redisService.get(openid,0).compareTo(Token)!=0)
        {
            responsejsonObject.put("status",100);
            responsejsonObject.put("erromsg","Token错误");
        }
        else
        {
            //从reids取出历史消息
            String messages = redisService.get(openid,1);
            //如果缓存没有则从数据库取出
            if(messages==null||messages.length()==0) messages = usermessagesmapper.getusermessages(openid);
            if(messages==null||messages.length()==0) messages="[]";
            redisService.set(openid,messages,1);
            responsejsonObject.put("status",200);
            log.info("getmessage: "+"messages:"+JSONArray.parseArray(messages));
            responsejsonObject.put("messages",JSONArray.parseArray(messages));
        }
        return responsejsonObject;
    }
    //用户退出,同步数据库
    @ResponseBody
    @RequestMapping("/userout")
    public void userout(HttpServletRequest request) {
        JSONObject params = new JSONObject();
        //用户退出,同步数据库
        String openid = request.getParameter("openid");
        String token = request.getParameter("token");
        if (openid == null || token == null || openid.length() == 0 || token.length() == 0) return;
        if(redisService.get(openid,0)==null&&redisService.get(openid,0).compareTo(token)!=0)return;
        redisService.del(openid,0);
        if(usermessagesmapper.getusermessages(openid)!=null) usermessagesmapper.updatausermessages(openid,redisService.get(openid,1));
        else  usermessagesmapper.insertusermessages(openid,redisService.get(openid,1));
        redisService.del(openid,1);
        log.info(openid+"用户"+"退出");
    }
}
