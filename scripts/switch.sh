#!/usr/bin/env bash

PROJECT_ROOT="/home/ubuntu/app"

DEPLOY_LOG="$PROJECT_ROOT/deploy.log"

CURRENT_PORT=$(cat /home/ubuntu/service_url.inc | grep -Po '[0-9]+' | tail -1)
TARGET_PORT=0

if [ $CURRENT_PORT -eq 8081 ]; then
  TARGET_PORT=8082
  DEPLOY_LOG="$PROJECT_ROOT/deploy2.log"
elif [ $CURRENT_PORT -eq 8082 ]; then
  TARGET_PORT=8081
else
  echo "> No WAS is connected to nginx"
  exit 1
fi

# 프록시 포트번호 변경
echo "set \$service_url http://127.0.0.1:${TARGET_PORT};" | sudo tee /home/ubuntu/service_url.inc
echo "> Now Nginx proxies to ${TARGET_PORT}." >> $DEPLOY_LOG

# PREV_PID : 이전 PORT 번호로 구동 중인 애플리케이션 pid
PREV_PID=$(lsof -Fp -i TCP:${CURRENT_PORT} | grep -Po 'p[0-9]+' | grep -Po '[0-9]+')

# PREV_PID 구동중이면 kill
if [ ! -z $PREV_PID ]; then
  echo "$TIME_NOW > 실행중인 $PREV_PID 애플리케이션 종료 " >> $DEPLOY_LOG
  kill -15 $PREV_PID
fi

# nginx reload
sudo service nginx reload