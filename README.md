# EightySix android project README

***项目代号： EightySix，纪念1886年5月1号。***

## Requirements
* [Maven 3.1.1 or later](http://maven.apache.org)
* Android Sdk r21 or later

## Getting started

该项目基于Maven。Maven是一个软件项目管理系统，它基于项目对象模型（Project object model）的概念而设计，以集中化的方式管理和负责构建，项目报告，以及文档生成等工作。

#### 安装Maven，下载[最新3.2.1版本](http://apache.fayea.com/apache-mirror/maven/maven-3/3.2.1/binaries/apache-maven-3.2.1-bin.tar.gz)
#### 安装Git
#### 安装Android SDK

## 非官方的Maven Repository

为了使用一些不存在于官方Maven Repository的库和最新的Android SDK，我们搭建了一个Maven服务器用于提供这些库。

[Dustr Repository](http://dustr.info:8081/nexus/)

## 编译项目

在EightySix根目录下执行```mvn install```，如果没有任何错误，就可以完成编译和集成并安装应用到连接的手机设备中，同时执行测试用例。

***maven是一个依赖于网络的工具，需要在有网络链接的情况下才能正常使用***

## Project Structure

Maven本身是没有任何功能的，它基于插件来实现一切功能，包括编译java代码都是插件来负责的。

#### EightySix - parent

这是EightySix的根项目，它作为实际的EightySix项目和集成测试项目的容器。

#### EightySix

实际的Android项目

#### EightySix - it

集成测试项目，基于Android的测试框架的测试项目

## Changelog

**2014/05/14**

README初始版本