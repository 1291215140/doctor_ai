package com.example.doctorai;
import com.alibaba.fastjson.JSONArray;
import com.example.doctorai.data.loginData;
import com.example.doctorai.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import javax.crypto.*;
import java.security.*;
@Slf4j
@SpringBootTest
class DoctorAiApplicationTests {
    @Autowired
    usermessagesmapper usermessagesmapper;
    @Test
    void contextLoads() throws Exception {
        String messages = usermessagesmapper.getusermessages("sss");
        log.info(messages);
    }

}