package com.example.doctorai.service;

import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
@Slf4j
@Service
public class SSHUtils {
    /**
     * \文件上传
     * @param fileBytes 文件字节数组
     * @param fileName 文件名
     * @param filePath 文件路径
     */
    @Value("${ssh_ip}")
    private String ip;
    @Value("${ssh_username}")
    private String username;
    @Value("${ssh_password}")
    private String password;
    private int port = 22;
    public void sftp(byte[] fileBytes,String fileName,String filePath) throws Exception{
        int port = 22; // 端口号
        JSch jsch = new JSch();
        // 创建session连接
        Session session = jsch.getSession(username, ip, port);
        if (session == null) {
            throw new Exception("session create error");
        }
        session.setPassword(password);// 设置密码
        session.setConfig("StrictHostKeyChecking", "no"); // 设置登陆提示为"no"
        session.connect(10000); // 设置超时时间
        // 创建通信通道
        Channel channel = session.openChannel("sftp");
        if (channel == null) {
            log.info("channel create error");
        }
        channel.connect(1000); // 设置超时时间
        ChannelSftp sftp = (ChannelSftp) channel; // 创建sftp通道

        // 检查目录是否存在，不存在则创建
        try {
            sftp.cd(filePath);
        } catch (SftpException e) {
            if (ChannelSftp.SSH_FX_NO_SUCH_FILE == e.id) {
                // 目录不存在，创建目录
                sftp.mkdir(filePath);
                sftp.cd(filePath);
            } else {
                throw e;
            }
        }
        OutputStream outputStream = null;
        // 开始文件上传
        outputStream = sftp.put(fileName);
        outputStream.write(fileBytes);
        outputStream.flush();
        outputStream.close();
        sftp.disconnect();
        channel.disconnect();
        session.disconnect();
    }
}