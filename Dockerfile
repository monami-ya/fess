FROM monami0ya/docker-baseimage:14.04
MAINTAINER <support@monami-ya.com>

ENV DEBIAN_FRONTEND=noninteractive

RUN add-apt-repository ppa:openjdk-r/ppa
RUN apt-get update
RUN apt-get install -y openjdk-8-jre-headless
RUN sudo update-ca-certificates -f

ADD fess-10.0.2 /fess-10.0.2

EXPOSE 8080

RUN mkdir /etc/service/fess
ADD fess.sh /etc/service/insight/run


