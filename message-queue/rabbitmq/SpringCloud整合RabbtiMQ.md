

## 1. RabbitTemplate和AmqpTemplate有什么关系

- 源码中会发现rabbitTemplate实现自amqpTemplate接口，使用起来并无区别，需引入spring-boot-starter-amqp依赖。



## 2. 环境配置

pom.xml配置

```xml
 <dependency>     <groupId>org.springframework.boot</groupId>     <artifactId>spring-boot-starter-amqp</artifactId> </dependency>
```

配置application-boot.yml

```
spring:
  # 配置rabbitMQspring:
  rabbitmq:
    host: 10.240.80.134
    username: spring-boot
    password: spring-boot
    virtual-host: spring-boot-vhost
```








## 3. 三种发布模式

### Direct发布模式

#### Config

```java
@Configuration
public class RabbitConfigureDirect{

    // 队列名称
    public final static String SPRING_BOOT_QUEUE = "spring-boot-queue-2";
    // 交换机名称
    public final static String SPRING_BOOT_EXCHANGE = "spring-boot-exchange-2";
    // 绑定的值
    public static final String SPRING_BOOT_BIND_KEY = "spring-boot-bind-key-2";
}
```



#### 发送消息

在spring boot默认会生成AmqpAdmin和AmqpTemplate 供我们和RabbitMQ交互。 

AmqpTemplate 的默认实例是RabbitTemplate，AmqpAdmin 默认实例是RabbitAdmin，通过源码发现其内部实现实际是RabbitTemplate。所以AmqpAdmin和AmqpTemplate两者本质是相同的

```java

@Component
public class SendMsgDirect{

    // 此接口默认实现只有一个，且是RabbitAdmin，通过源码发现其内部实现实际是RabbitTemplate。所以AmqpAdmin和AmqpTemplate当前两者本质是相同的
    @Autowired
    private AmqpAdmin amqpAdmin;

    // 此接口的默认实现是RabbitTemplate，目前只有一个实现，
    @Autowired
    private AmqpTemplate amqpTemplate;

    /**
     * 发送消息
     *
     * @param msgContent
     */
    public void sendMsg(String msgContent) {
        amqpTemplate.convertAndSend(RabbitConfigure2.SPRING_BOOT_EXCHANGE, RabbitConfigure2.SPRING_BOOT_BIND_KEY, msgContent);
    }
}

```



#### 接收消息

通过@RabbitListener注解要接收消息的方法，在此注解中可以定义

- @QueueBinding：定义要本次要监听的消息绑定
- @Queue：定义队列，如果RabbitMQ没有这个队列，则创建，如果有且配置参数相同，则忽略，否则抛出异常
- @Exchange：定义交换机，如果RabbitMQ没有这个交换机，则创建，如果有且配置参数相同，则忽略，否则抛出异常

```java
@Component
public class ReceiveMsgDirect {

    /**
     * === 在RabbitMQ上创建queue,exchange,binding 方法二：直接在@RabbitListener声明 begin ===
     * 接收
     * @param content
     */
    @RabbitListener(containerFactory = "rabbitListenerContainerFactory",
            bindings = @QueueBinding(
            value = @Queue(value = RabbitConfigure2.SPRING_BOOT_QUEUE+"3", durable = "true", autoDelete="true"),
            exchange = @Exchange(value = RabbitConfigure2.SPRING_BOOT_EXCHANGE, type = ExchangeTypes.DIRECT),
            key = RabbitConfigure2.SPRING_BOOT_BIND_KEY)
    )
    public void receive_2(String content) {
        // ...
        System.out.println("[ReceiveMsg-2] receive msg: " + content);
    }

}
```



### Topic发布模式



### Fanout发布模式





# Reference 

https://blog.csdn.net/weixin_44004647/article/details/88420281

https://juejin.cn/post/6844903580881813511#heading-5

https://www.liaoxuefeng.com/wiki/1252599548343744/1282385960239138?luicode=10000011&lfid=1076031658384301&featurecode=newtitl&u=https%3A%2F%2Fwww.liaoxuefeng.com%2Fwiki%2F1252599548343744%2F1282385960239138