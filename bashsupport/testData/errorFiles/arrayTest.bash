#!/bin/sh

local index=0
while read LINE; do
        TOMCAT_HOST_LIST[$index]=$LINE
        index=$index+1
done < $TOMCAT_HOSTS_FILE;
