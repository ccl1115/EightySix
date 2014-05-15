# EightySix android project README

***项目代号： EightySix，纪念1886年5月1号。***

## Requirements
* [Maven 3.1.1 or later](http://maven.apache.org)
* Android Sdk r21 or later
* [maven-android-sdk-deployer](https://github.com/mosabua/maven-android-sdk-deployer)
* [git](http://git-scm.com)

## Getting started

该项目基于Maven。Maven是一个软件项目管理系统，它基于项目对象模型（Project object model）的概念而设计，以集中化的方式管理和负责构建，项目报告，以及文档生成等工作。

#### 安装Maven，下载[最新3.2.1版本](http://apache.fayea.com/apache-mirror/maven/maven-3/3.2.1/binaries/apache-maven-3.2.1-bin.tar.gz)
#### 安装Git
#### 安装Android SDK
#### Clone maven-android-sdk-deployer
```git clone https://github.com/mosabua/maven-android-sdk-deployer```

#### 使用maven-android-sdk-deployer搭建提供Maven使用的Android SDK环境

因为在Maven的官方中心库中中并没有最新的Android平台，例如4.4的Android不能在中心库中下载到。而maven-android-sdk-deployer解决了这个问题，它从Google官方库中将所需要的Android依赖下载到Maven的本地缓存中（~/.m2/repositories），从而使得我们可以使用到最近的Android SDK。

maven-android-sdk-deployer本身就是一个使用Maven管理的项目。当我们clone这个项目之后执行如下操作进行Android依赖安装：

```
$ cd maven-andorid-sdk-deployer
$ mvn install
```

等待下载完成，我们就得到了所有的Android SDK依赖，从1.5到4.4的。这是个漫长的过程，如果我们只想要安装某一个版本：

```
$ mvn install -P 4.4
```
上面的命令只会安装4.4的Android SDK。


#### 在项目中定义一个Android SDK依赖

```
<dependency>
  <groupId>android</groupId>
  <artifactId>android</artifactId>
  <version>3.0_r2</version>
  <scope>provided</scope>
</dependency>
```

#### 编译项目

在EightySix根目录下执行```mvn install```，如果没有任何错误，就可以完成编译并安装应用到连接的手机设备中。

***maven是一个依赖于网络的工具，需要在有网络链接的情况下才能正常使用***

## Project Structure

Maven本身是没有任何功能的，它基于插件来实现一切功能，包括编译java代码都是插件来负责的。为了支持Android环境的编译，我们使用了**maven-android-plugin**，它能方便的帮助我们编译Android项目，已经丰富的自定义能力。

#### EightySix - parent

这是EightySix的根项目，它作为实际的EightySix项目和集成测试项目的容器。

#### EightySix

实际的Android项目

#### EightySix - it

集成测试项目，基于Android的测试框架的测试项目

## Changelog

**2014/05/14**

README初始版本