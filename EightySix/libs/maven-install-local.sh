#!/bin/bash


#安装项目依赖到本地的maven库中

mvn install:install-file -Dfile=cos_sdk.android.jar -DgroupId=cos_sdk -DartifactId=cos_sdk -Dversion=1 -Dpackaging=jar
mvn install:install-file -Dfile=dns.jar -DgroupId=dns -DartifactId=dns -Dversion=1 -Dpackaging=jar
mvn install:install-file -Dfile=mta-sdk-1.7.2.jar -DgroupId=mta -DartifactId=mta -Dversion=1 -Dpackaging=jar
