# 使用基于OpenJDK 11的Docker映像作为基础镜像
FROM adoptopenjdk:11-jdk-hotspot

# 定义工作目录
WORKDIR /app

# 将项目的pom.xml文件复制到镜像中
COPY pom.xml .

# 安装maven
RUN apt-get update && apt-get install -y maven

# 下载并安装项目的所有依赖
RUN mvn dependency:resolve

# 复制应用程序的所有源代码到镜像中
COPY src ./src

# 构建项目，并将应用程序打包成可执行的JAR文件
RUN mvn package -DskipTests

# 将构建好的Jar文件复制到镜像的根目录中
RUN cp target/*.jar app.jar

# 暴露应用程序的端口（如果需要）
EXPOSE 8080

# 定义容器启动时运行的命令
CMD java -jar app.jar