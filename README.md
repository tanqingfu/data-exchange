# archetypetpl

## 项目介绍
data
## 软件架构
// api 远程服务调用<br>
// provider 远程服务调用实现<br>
biz 业务代码<br>
web controller层<br>
task 跑批<br>
gen 代码生成器<br>

## 项目初始化
/home目录下新建日志目录，名称同工程<br>
集中配置
<pre>
jdbc.driver=com.mysql.cj.jdbc.Driver
jdbc.url=jdbc:mysql://xxxx:3306/xxxx?serverTimezone=Asia/Shanghai&useSSL=false&useUnicode=true&characterEncoding=UTF-8
jdbc.username=xxxx
jdbc.password=xxxxxx
</pre>

<pre>
redis.host=127.0.0.1
redis.port=6379
redis.password=xxxx
</pre>

<pre>
rocket-mq.nameserver.addr=localhost:9876
</pre>

## 单点登录
<pre>
# 集中配置
ucsso.server.domain=http://localhost:50003
ucsso.client.domain=http://localhost:50010
ucsso.client.authorization=true
</pre>

pom中引入
```
<dependency>
    <groupId>com.sdy</groupId>
    <artifactId>user-center-client</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```
* 单点登录异步登出：/sso/logout
* 异步ajax请求请加入header：req_type=async，方便后端重定向，需要重定向时候返回header：redirect=重定向地址，这里请在js中自行处理。代码参考：
```
$.ajaxSetup({
    beforeSend: function(xhr) {
        xhr.setRequestHeader('req_type', 'async');
    },
    complete:function(xhr, textStatus){
        var redirect = xhr.getResponseHeader("redirect");
        if (redirect) {
            window.location.href = redirect;
        }
    },
    cache: false
});
```

## 代码生成
gen模块下Generator类用来生成代码，需要配置连接信息、作者、表名等<br>
vue下需要自行配置route、store

## 一些规范
1.主键用id int(11) 自动递增<br>
2.如果有创建时间、修改时间，一般用create_time，update_time（自动更新）表示<br>
3.表名、字段名用小写+下划线命名<br>
4.字段请加上注释，如果是状态之类的字段请标明各个数字代表什么状态<br>
5.对其他表（例如table1）的id引用，字段命名`table1_id`，有必要时加上索引<br>
6.涉及到金额，请使用decimal(18,2)<br>
7.编码请使用utf8，排序规则请使用utf8_unicode_ci<br>
8.索引命名一般使用`idx_字段名`，如果是unique key，请使用`uk_字段名`<br>
9.禁止使用外键、联合主键
10.禁止存储过程

## 日志
日志目录在`/home/xxxx`(xxxx是项目名称)，按天区分，单文件最大1024MB，超过后分文件存储。

## 异常
web层做了统一异常处理，业务异常请往外抛BizException，系统异常往外抛Exception。

## 发布
<pre>
java -jar xxxx.jar --spring.profiles.active=prod --spring.cloud.nacos.config.namespace=xxxx
</pre>

## 参与贡献
zzq
