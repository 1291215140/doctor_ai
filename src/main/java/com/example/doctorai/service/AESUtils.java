package com.example.doctorai.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
/*
数据加密与解密工具类
* */
@Service
public class AESUtils {
    @Value("${aes_key}")
    String KEY;
    @Value("${aes_iv}")
    String IV;
    //加密
    public  String encrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec secretKeySpec = new SecretKeySpec(KEY.getBytes(), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(IV.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }
    //解密
    public String decrypt(String data) throws Exception {
        byte[] decoded = Base64.getDecoder().decode(data);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec secretKeySpec = new SecretKeySpec(KEY.getBytes(), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(IV.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] decrypted = cipher.doFinal(decoded);
        return new String(decrypted, StandardCharsets.UTF_8);
    }
}
