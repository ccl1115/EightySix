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

#### EightySix - parent

这是EightySix的根项目，它作为实际的EightySix项目和集成测试项目的容器。

#### EightySix-app

EightySix的应用层

#### EightySix - it

集成测试项目，基于Android的测试框架的测试项目

#### EightySix-base

基础服务

#### EightySix-fixture

提供测试数据，因为只在Debug版本中使用，所以Release中没有该依赖

#### EightySix-data

数据定义，用于将json解析为pojo对象

## Dependencies

我们依赖的开源项目

* Gson 用于序列化和反序列化JSON对象的工具库
* android-async-http 异步的Http网络库，针对Android平台和移动网络做了优化
* nineoldandroid 使得低版本的Android版本可以高版本Android动画库 Animator
* androlog 一个增强的Android日志工具
* DiskLruCache 文件LRU缓存
* Robotium 增强的Android平台测试框架
* oss-android 开源的阿里云OSS存储服务SDK

我们依赖的闭源项目

* PushService 百度云推送SDK
* locSDK 百度定位SDK
* mta-sdk 腾讯统计SDK

## Architecture

这里指的是为了使得**开发本应用更加方便**而设计的框架，它尽量不违背Android平台应用开发本身的特性。
              j

#### 自动化简单的工作

对于业务层，重复的工作会导致开发量的增加，而复杂的框架往往会使得开发量增加得更多，所以我们把那些认为是重复并且
足够简单得事情交给框架来做。

**我们如何鉴定什么工作是重复并且简单的**

1. 重复2次并不算是重复。
2. 如果不够简单，那么也不算重复。
3. 如果不是原子操作，同样不算重复。

**下面是框架做的一些自动化的工作**

1. 使用Gson的对象映射方式来解析JSON对象，避免大量编写用于解析JSON的代码
2. 使用注解的方式避免大量的通过findViewById()调用来寻找View对象的代码。本框架会大量使用注解来处理类似的问题，例如使用Layout注解标示
当前activity需要套用的布局，从而自动的调用setContentView()方法。
3. 统一的标题栏，避免每个界面构造自己的标题栏，不使用ActionBar的原因是我们目前还没有使用Android兼容包。
当然在设计上，我们应该对业务层隐藏实现，所以它们并不需要关心自己到底是使用的系统的ActionBar还是框架自己实现的标题栏。

#### 可测试化

保证各个功能模块是可测试的，尽量做到方便于编写TestCase的。

#### 外部配置

该应用有一个外部配置文件，位于 ```res/raw/configuration.properties```

它使用标准的Java Properties实现。这些属性可以被业务层使用，也可是是框架本身的配置。

#### 业务层应该和第三方库做到无耦合

业务层可以说是所有继承自BaseActivity的Activity，它们不允许直接调用任何第三方的库，
但是这取决于业务层代码自己，而不是架构本身能限制的。除非我们将框架层作为另外一个
项目，将依赖库对外部隐藏。但基于本框架本身就是只为这个应用服务的，所以暂时不使用
这种方式。

#### REST调用

通过BaseActivity#request()方法来发起对REST服务器的接口请求。传入一个POJO对象，
我们仍然通过注解来决定我们要调用那个API，以及需要的参数。

下面是一个请求对象的定义：

```
@Request("hello/world")
@Method(METHOD.GET)
public class SampleRequest {
    
    @Param("pwd")
    public String pwd;
    
    public SampleRequest(String pwd) {
        this.pwd = pwd;
    }
}
```

当调用request()方法并传入一个SampleRequest对象的时候：

```
public class SampleActivity extends BaseActivity {
    
    public void onCreate() {
        request(new SampleRequest("test"), response);
    }
}
```

以上我们就做了一个请求，host是预先配置好的，path＝/hello/wolrd，http method是GET，
而params是pwd=test。

**另外还有一些需要注意的地方：**

1. 重复的请求会被抛弃。我们根据path和params.toString()加起来的md5值来判断一个请求是不是重复的。
2. Activity在onDestroy的时候会自动取消所有的请求，也可以通过cancelAll()方法来暂停。

## Otto 消息和事件系统

Otto 是一个开源的消息系统，它采用Pub/Sub模型，支持多个Bus，支持线程限定。

#### 为什么引入消息系统

当我们有大量异步任务，或者一个事件被多个对象监听的时候，Pub／Sub消息系统是一个非常好的解决方案。它快速高效，并且降低了开发成本。

#### EightySix在哪里使用了Otto

在我们的应用启动的时候会创建一个全局的Bus，每个继承自BaseActivity的Activity都会自动的在onCreate()的时候注册，
在onDestroy()的时候反注册。这样每个Activity都能收到发送给全局Bus的消息，当然前提是我们订阅了某一个事件。

```
@Subscribe public void onSomeEvent(SomeEvent event) {
    // deal with this event
}
```

例如，如果你想关注用户的登录和注销，你可以订阅Account.LoginEvent事件，BaseActivity事实上已经监听了LogoutEvent，
因为大部分Activity都需要在用户登出的时候finish自身。


## Changelog

**2014/06/21**

更新项目结构变更说明

**2014/06/06**

增加Otto说明

**2014/05/28**

增加Dependencies和Architecture

**2014/05/14**

README初始版本