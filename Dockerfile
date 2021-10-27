FROM h.cn/base/centos7pt:latest
ARG  env
ARG  idc
ENV  LANG en_US.UTF-8
ENV  env=${env} idc=${idc} appName=groot
RUN  mkdir -p /opt/settings/ && echo -e "env=${env}\nidc=${idc}" > /opt/settings/server.properties
COPY target/$appName.jar /opt/
EXPOSE 8080
ENTRYPOINT /usr/java/jdk1.8.0_121/bin/java ${JAVA_OPTS} -jar /opt/$appName.jar
