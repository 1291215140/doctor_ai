package com.example.doctorai.bool;

import java.util.Random;

public class str_boo {
    // 生成指定长度的随机字符串
    public static String generateRandomString(int length) {
        // 可选的字符集合，这里只包含了英文大写字母和小写字母
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            // 从字符集合中随机选择一个字符，并添加到字符串生成器中
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }
}
