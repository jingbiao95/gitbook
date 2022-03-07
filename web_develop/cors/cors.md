## 什么是cors

CORS是一个W3C标准，全称是"跨域资源共享"（Cross-origin resource sharing）。

它允许浏览器向跨源服务器，发出[`XMLHttpRequest`](http://www.ruanyifeng.com/blog/2012/09/xmlhttprequest_level_2.html)请求，从而克服了AJAX只能[同源](http://www.ruanyifeng.com/blog/2016/04/same-origin-policy.html)使用的限制。

CORS需要浏览器和服务器同时支持。目前，所有浏览器都支持该功能，IE浏览器不能低于IE10。

- 浏览器端：

  目前，所有浏览器都支持该功能（IE10以下不行）。整个CORS通信过程，都是浏览器自动完成，不需要用户参与。

- 服务端：

  CORS通信与AJAX没有任何差别，因此你不需要改变以前的业务逻辑。只不过，浏览器会在请求中携带一些头信息，我们需要以此判断是否允许其跨域，然后在响应头中加入一些信息即可。这一般通过过滤器完成即可。

## 原理

> 预检请求

跨域请求会在正式通信之前，增加一次HTTP查询请求，称为"预检"请求（preflight）。

浏览器先询问服务器，当前网页所在的域名是否在服务器的许可名单之中，以及可以使用哪些HTTP动词和头信息字段。只有得到肯定答复，浏览器才会发出正式的`XMLHttpRequest`请求，否则就报错。

一个“预检”请求的样板：

```http
OPTIONS /cors HTTP/1.1
Origin: http://localhost:1000
Access-Control-Request-Method: GET
Access-Control-Request-Headers: X-Custom-Header
User-Agent: Mozilla/5.0...
```

- Origin：会指出当前请求属于哪个域（协议+域名+端口）。服务会根据这个值决定是否允许其跨域。

- Access-Control-Request-Method：接下来会用到的请求方式，比如PUT
- Access-Control-Request-Headers：会额外用到的头信息

> 预检请求的响应

服务的收到预检请求，如果许可跨域，会发出响应：

```http
HTTP/1.1 200 OK
Date: Mon, 01 Dec 2008 01:15:39 GMT
Server: Apache/2.0.61 (Unix)
Access-Control-Allow-Origin: http://miaosha.jd.com
Access-Control-Allow-Credentials: true
Access-Control-Allow-Methods: GET, POST, PUT
Access-Control-Allow-Headers: X-Custom-Header
Access-Control-Max-Age: 1728000
Content-Type: text/html; charset=utf-8
Content-Encoding: gzip
Content-Length: 0
Keep-Alive: timeout=2, max=100
Connection: Keep-Alive
Content-Type: text/plain
```

如果服务器允许跨域，需要在返回的响应头中携带下面信息：

- Access-Control-Allow-Origin：可接受的域，是一个具体域名或者*（代表任意域名）
- Access-Control-Allow-Credentials：是否允许携带cookie，默认情况下，cors不会携带cookie，除非这个值是true
- Access-Control-Allow-Methods：允许访问的方式
- Access-Control-Allow-Headers：允许携带的头
- Access-Control-Max-Age：本次许可的有效时长，单位是秒，**过期之前的ajax请求就无需再次进行预检了**



> 有关cookie：

要想操作cookie，需要满足3个条件：

- 服务的响应头中需要携带Access-Control-Allow-Credentials并且为true。
- 浏览器发起ajax需要指定withCredentials 为true
- 响应头中的Access-Control-Allow-Origin一定不能为*，必须是指定的域名



## 实现方案

- 浏览器端都有浏览器自动完成，我们无需操心
- 服务端可以通过拦截器统一实现，不必每次都去进行跨域判定的编写。

事实上，Spring已经帮我们写好了CORS的跨域过滤器，内部已经实现了刚才所讲的判定逻辑。

```
spring-webmvc：CorsFilter
spring-webflux：CorsWebFilter
```

springcloud-gateway集成的是webflux，所以这里使用的是CorsWebFilter

在`gmall-gateway`中编写一个配置类，并且注册CorsWebFilter：



```java
@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {

        // 初始化CORS配置对象
        CorsConfiguration config = new CorsConfiguration();
        // 允许的域,不要写*，否则cookie就无法使用了
        config.addAllowedOrigin("http://manager.gmall.com");
        config.addAllowedOrigin("http://www.gmall.com");
        // 允许的头信息
        config.addAllowedHeader("*");
        // 允许的请求方式
        config.addAllowedMethod("*");
        // 是否允许携带Cookie信息
        config.setAllowCredentials(true);

        // 添加映射路径，我们拦截一切请求
        UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();
        corsConfigurationSource.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(corsConfigurationSource);
    }
}
```

