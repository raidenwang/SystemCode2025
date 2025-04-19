package com.cqupt.utils;

import com.alibaba.fastjson.JSONObject;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

public class HttpUtil {
    public static String HttpRestClient(String url, HttpMethod method, JSONObject json) throws IOException {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(600*3000);
        requestFactory.setReadTimeout(600*3000);
        RestTemplate client = new RestTemplate(requestFactory);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<String>(json.toString(), headers);
        //  执行HTTP请求
        ResponseEntity<String> response = client.exchange(url, method, requestEntity, String.class);
        return response.getBody();
    }
}