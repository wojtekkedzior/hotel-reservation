#!/bin/bash


docker run --name mysql -v /mnt/llms/mysql:/var/lib/mysql -p 13306:3306  -e MYSQL_ROOT_PASSWORD=root -d mysql:9.0.1
#TODO find the container ID here
docker exec  c029867185a1 mysql -uroot -proot -e "SET GLOBAL SQL_MODE=ANSI_QUOTES;"

# Run command
#-DhotelDbHost=localhost:13306 -DhotelDbUsername=root -DhotelDbPassword=root -Dspring.profiles.active="dev"