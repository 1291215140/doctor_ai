package com.example.doctorai.control;

import com.alibaba.fastjson.JSONObject;
import com.example.doctorai.service.RedisService;
import com.example.doctorai.service.SSHUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
@Slf4j
@Controller
public class upLoadFile {
    @Autowired
    private SSHUtils sshUtils;
    @Autowired
    private RedisService redisService;
    @ResponseBody
    @PostMapping("/upLoadImage")
    public JSONObject uploadImage(@RequestParam("file") MultipartFile file, @RequestParam("openid") String openid, @RequestParam("token") String token) {
        JSONObject params = new JSONObject();
        if (file.isEmpty()) {
            params.put("stauts", 100);
            params.put("erromsg", "上传文件为空");
        }
        else if (openid == null || openid.length() == 0 || token == null || token.length() == 0) {
            params.put("stauts", 100);
            params.put("erromsg", "openid或token为空");
        }
        else if (redisService.get(openid, 0) == null || !redisService.get(openid, 0).equals(token))
            {
                params.put("stauts", 100);
                params.put("erromsg", "token错误");
            }
        else {
            try {
                byte[] fileBytes = file.getBytes();
                String fileName = file.getOriginalFilename(); // 使用 openid 命名文件
                String filePath = "/www/server/nginx/html/" + openid; // 文件路径
                sshUtils.sftp(fileBytes, fileName, filePath); // 调用上传文件方法
                log.info("上传成功");
                params.put("stauts", 200);
                params.put("filename", fileName);
                params.put("url", "http://112.74.58.124/" + openid + "/" + fileName);
            } catch (Exception e) {
                params.put("stauts", 100);
                params.put("erromsg", "上传失败"+e.toString());
                log.info(e.toString());
            }
        }
        return params;
    }
}
