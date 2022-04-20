

### 1. OAuth2.0 的四种授权模式

- 授权码模式（authorization code）
- 简化模式（implicit）
- 密码模式（resource owner password credentials）
- 客户端模式（client credentials）

其中密码模式常用于外部服务的鉴权，客户端模式常用于内部服务鉴权和开放平台应用的授权，授权码模式常用于社会化登录和 SSO，因此 OAuth2.0 可作为完整的统一身份认证和授权方案。



### 2. OAuth2.0 的几种重要角色

>  必须注意的是，这些角色是相对的概念。

- 客户端 Client：一般指第三方应用程序，例如用 QQ 登录豆瓣网站，这里的豆瓣网就是 Client；但在微服务体系里，Client 通常是服务本身，如 APP 端的注册登录服务；
- 资源所有者 Resource Owner：一般指用户，例如用 QQ 登录豆瓣网站，这里的所有者便是用户；但在微服务体系里，资源所有者是服务提供者本身；
- 资源服务器 Resource Server：一般指资源所有者授权存放用户资源的服务器，例如用 QQ 登录豆瓣网站，这里的 QQ 就是资源服务器；但在微服务体系里，服务提供者本身便是资源服务器；
- 授权服务器 Authorization Server：一般是指服务提供商的授权服务，例如用 QQ 登录豆瓣网站，这里的 QQ 便是授权服务器；类似地，在微服务体系里，鉴权服务便是授权服务器。



# reference

[理解OAuth 2.0](http://www.ruanyifeng.com/blog/2014/05/oauth_2_0.html)

