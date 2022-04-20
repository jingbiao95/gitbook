Pulsar是pub-sub（发布-订阅）模式的分布式消息平台，拥有灵活的消息模型和直观的客户端API。



## 概念

### Topic

Topic是Pulsar的核心概念，表示一个“channel”，Producer可以写入数据，Consumer从中消费数据（Kafka、RocketMQ都是这样）。

Topic名称的URL类似如下的结构：

```none
{persistent|non-persistent}://tenant/namespace/topic
```



- persistent|non-persistent表示数据是否持久化（Pulsar支持消息持久化和非持久化两种模式）
- Tenant为租户
- Namespace一般聚合一系列相关的Topic，一个租户下可以有多个Namespace



# reference

https://ifeve.com/apache-pulsar%E4%BB%8B%E7%BB%8D/