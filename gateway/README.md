API 网关是客户端访问服务的统一入口，API 网关封装了后端服务，还提供了一些更高级的功能，例如：身份验证、监控、负载均衡、缓存、多协议支持、限流、熔断等等



## **Kong**

Kong是一个在 Nginx 中运行的Lua应用程序，并且可以通过lua-nginx模块实现，Kong不是用这个模块编译Nginx，而是与 OpenResty 一起发布，OpenResty已经包含了 lua-nginx-module， OpenResty 不是 Nginx 的分支，而是一组扩展其功能的模块。

它的核心是实现数据库抽象，路由和插件管理，插件可以存在于单独的代码库中，并且可以在几行代码中注入到请求生命周期的任何位置。

## **Traefik**

Traefik 是一个现代 HTTP 反向代理和负载均衡器，可以轻松部署微服务，Traeffik 可以与您现有的组件（Docker、Swarm，Kubernetes，Marathon，Consul，Etcd，…）集成，并自动动态配置。

## **Ambassador**

Ambassador 是一个开源的微服务 API 网关，建立在 Envoy 代理之上，为用户的多个团队快速发布，监控和更新提供支持，支持处理 Kubernetes ingress controller 和负载均衡等功能，可以与 Istio 无缝集成。

## **Tyk**

Tyk是一个开源的、轻量级的、快速可伸缩的 API 网关，支持配额和速度限制，支持认证和数据分析，支持多用户多组织，提供全 RESTful API。基于 go 编写。

## **Zuul**

Zuul 是一种提供动态路由、监视、弹性、安全性等功能的边缘服务。Zuul 是 Netflix 出品的一个基于 JVM 路由和服务端的负载均衡器。





# 参考文献

https://zhuanlan.zhihu.com/p/61014955