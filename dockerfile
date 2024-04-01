FROM java:20
# 设置工作目录
WORKDIR /app
# 修改编码
ENV LANG=zh_CN.UTF-8
# 复制Spring Boot应用的JAR文件到容器中
COPY target/doctorai.jar app.jar

# 暴露应用运行的端口
EXPOSE 8080

# 指定应用启动的命令
ENTRYPOINT ["java", "-jar", "app.jar"]
