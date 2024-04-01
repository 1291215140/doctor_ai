package com.example.doctorai.service;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HttpService {

    private final RestTemplate restTemplate;

    @Autowired
    public HttpService() {
        this.restTemplate = new RestTemplate();
    }

    // 发送 GET 请求并返回服务器响应的字符串
    public String sendGetRequest(String url, JSONObject params) {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(buildUrlWithParams(url, params), HttpMethod.GET, entity, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            return null;
        }
    }

    // 发送 POST 请求并返回服务器响应的字符串
    public String sendPostRequest(String url, String postData, String authorization) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authorization);
        HttpEntity<String> requestEntity = new HttpEntity<>(postData, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            return null;
        }
    }
    // 构建带查询参数的 URL
    private String buildUrlWithParams(String url, JSONObject params) {
        if (params != null && !params.isEmpty()) {
            StringBuilder urlBuilder = new StringBuilder(url);
            urlBuilder.append("?");
            for (String key : params.keySet()) {
                urlBuilder.append(key).append("=").append(params.get(key)).append("&");
            }
            return urlBuilder.toString();
        }
        return url;
    }
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
