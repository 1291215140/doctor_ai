package com.example.doctorai.mapper;

import com.example.doctorai.data.loginData;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.scheduling.annotation.Async;

@Mapper
public interface usermapper {
    //插入用户信息，用户信息在logindata中
    @Async
    @Insert("insert into user(openid,name,phonenumber,age,sex,other,avatarUrl) values(#{openid},#{name},#{phonenumber},#{age},#{sex},#{other},#{avatarUrl})")
    public void insertuser(loginData loginData);
    @Select("select * from user where openid=#{openid}")
    public loginData getuser(String openid);
    @Update("update user set name=#{name},phonenumber=#{phonenumber},age=#{age},sex=#{sex},other=#{other},avatarUrl=#{avatarUrl} where openid=#{openid}")
    public void updatauser(loginData loginData);
}
