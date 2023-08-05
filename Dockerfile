# 使用适合您的Java版本的基础镜像
FROM adoptopenjdk:11-jdk-hotspot

# 设置工作目录
WORKDIR /app

# 复制应用程序的编译结果文件到容器中
COPY target/chat-0.0.1-SNAPSHOT.jar /app/chat-0.0.1-SNAPSHOT.jar

# 暴露应用程序的端口号
EXPOSE 8080

# 运行应用程序
CMD ["java", "-jar", "chat-0.0.1-SNAPSHOT.jar"]