# 注册中心

注册中心有
- [Eureka](eureka/README.md)
- [Nacos](../alibaba/nacos/README.md)
- [Zookeeper](../zookeeper/README.md)
- [Consul](consul/README.md)
- [Etcd](../k8s/etcd/README.md)







| 序号 | 比较项                       | Eureka               | zookeeper                     | Nacos                       | Consul                          |
| ---- | ---------------------------- | -------------------- | ----------------------------- | --------------------------- | ------------------------------- |
| 1    | 集群结构                     | 平级                 | 主从                          | 支持平级和主从              | 主从                            |
| 2    | 集群角色                     | 主人                 | Leader、follower observer     | leader、follower、candidate | server-leader、server以及client |
| 3    | 是否可以及时知道服务状态变化 | 不能及时知道         | 会及时知道                    | 不能及时知道                | 不能及时知道                    |
| 4    | 一致性协议（**CAP****）**    | 注重可用性（AP）     | 注重一致性(CP)                | 支持CP和AP-如何实现         | 注重一致性(CP)                  |
| 5    | 雪崩保护                     | 有                   | 没有                          | 有                          | 没有                            |
| 6    | 社区是否活跃                 | Eureka2.0不再维护了  | 持续维护                      | 持续维护                    | 持续维护                        |
| 7    | 管理端                       | 有现成的eureka管理端 | 没有现成的管理端              | 有现成的管理端              | 有现成的管理端                  |
| 8    | 负载均衡策略                 | 使用ribbon实现       | 一般可以直接采用RPC的负载均衡 | 权重/metadata/Selector      | Fabio                           |
| 9    | 权限控制                     | 无                   | 使用ACL实现节点权限控制       | RBAC-用户、角色、权限       | ACL                             |
| 10   | Spring Cloud集成             | 支持                 | 支持                          | 支持                        | 支持                            |
| 11   | 健康检查                     | Client Beat          | Keep Alive                    | TCP/HTTP/MYSQL/Client Beat  | TCP/HTTP/gRPC/Cmd               |
| 12   | 自动注销实例                 | 支持                 | 支持                          | 支持                        | 不支持                          |
| 13   | 访问协议                     | HTTP                 | TCP                           | HTTP/DNS                    | HTTP/DNS                        |
| 14   | 是否可用作配置中心           | 否                   | 是                            | 是                          | 是                              |
| 15   | 多数据中心                   | 不支持               | 不支持                        | 不支持                      | 支持                            |
| 16   | 跨注册中心同步               | 不支持               | 不支持                        | 支持                        | 支持                            |
| 17   | Dubbo集成                    | 不支持               | 支持                          | 支持                        | 不支持                          |
| 18   | K8S集成                      | 支持                 | 支持                          | 支持                        | 支持                            |





# 参考文献

https://juejin.cn/post/7012084821224603656

https://jishuin.proginn.com/p/763bfbd29957