package com.example.doctorai.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface usermessagesmapper {
    @Select("select message from usermessages where openid=#{openid}")
    String getusermessages(String openid);
    @Insert("insert into usermessages(openid,message) values(#{openid},#{message})")
    void insertusermessages(String openid,String message);
    @Update("update usermessages set message=#{message} where openid=#{openid}")
    void updatausermessages(String openid,String message);
}
