Spring中实现多线程，其实非常简单，只需要在配置类中添加`@EnableAsync`就可以使用多线程。在希望执行的**并发方法**中使用`@Async`就可以定义一个线程任务。通过spring给我们提供的`ThreadPoolTaskExecutor`就可以使用线程池。

# **1.定义线程池**

先在Spring Boot主类中定义一个线程池



1、利用@EnableAsync注解开启异步任务支持

2、配置类实现AsyncConfigurer接口并重写getAsyncExecutor方法，并返回一个ThreadPoolTaskExecutor，这样我们就获得了一个基于线程池TaskExecutor

```java
@Configuration
@EnableAsync  // 启用异步任务
public class AsyncConfiguration {

    // 声明一个线程池(并指定线程池的名字)
    @Bean("taskExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //核心线程数5：线程池创建时候初始化的线程数
        executor.setCorePoolSize(5);
        //最大线程数5：线程池最大的线程数，只有在缓冲队列满了之后才会申请超过核心线程数的线程
        executor.setMaxPoolSize(5);
        //缓冲队列500：用来缓冲执行任务的队列
        executor.setQueueCapacity(500);
        //允许线程的空闲时间60秒：当超过了核心线程出之外的线程在空闲时间到达之后会被销毁
        executor.setKeepAliveSeconds(60);
        //线程池名的前缀：设置好了之后可以方便我们定位处理任务所在的线程池
        executor.setThreadNamePrefix("DailyAsync-");
         // 缓冲队列满了之后的拒绝策略：由调用线程处理（一般是主线程）
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        executor.initialize();
        return executor;
    }
}
```



# 2.使用

在需要异步的方法上加`@Async`注解

1. 通过`@Async`注解表明该方法是异步方法，如果注解在类级别，则表明该类所有的方法都是异步方法，而这里的方法自动被注入使用`ThreadPoolTaskExecutor`作为T`askExecutor`。

2. 如果在异步方法所在类中调用异步方法，将会失效

## 2.1 调用不关注返回值

```java
@Slf4j
@Service
public class AsyncService {

    // 指定使用beanname为doSomethingExecutor的线程池
    @Async("doSomethingExecutor")
    public void doSomething(String message) {
        log.info("do something, message={}", message);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            log.error("do something error: ", e);
        }
        return ;
    }
}
```

## 2.2 调用关注返回值

需要异步调用的方法带有返回值`CompletableFuture`。

`CompletableFuture`是对Feature的增强，Feature只能处理简单的异步任务，而CompletableFuture可以将多个异步任务进行复杂的组合。



```java
@RestController
public class AsyncController {

    @Autowired
    private AsyncService asyncService;

    @SneakyThrows
    @ApiOperation("异步 有返回值")
    @GetMapping("/open/somethings")
    public String somethings() {
        CompletableFuture<String> createOrder = asyncService.doSomething1("create order");
        CompletableFuture<String> reduceAccount = asyncService.doSomething2("reduce account");
        CompletableFuture<String> saveLog = asyncService.doSomething3("save log");
        
        // 等待所有任务都执行完
        CompletableFuture.allOf(createOrder, reduceAccount, saveLog).join();
        // 获取每个任务的返回结果
        String result = createOrder.get() + reduceAccount.get() + saveLog.get();
        return result;
    }
}


@Slf4j
@Service
public class AsyncService {

    @Async("doSomethingExecutor")
    public CompletableFuture<String> doSomething1(String message) throws InterruptedException {
        log.info("do something1: {}", message);
        Thread.sleep(1000);
        return CompletableFuture.completedFuture("do something1: " + message);
    }

    @Async("doSomethingExecutor")
    public CompletableFuture<String> doSomething2(String message) throws InterruptedException {
        log.info("do something2: {}", message);
        Thread.sleep(1000);
        return CompletableFuture.completedFuture("; do something2: " + message);
    }

    @Async("doSomethingExecutor")
    public CompletableFuture<String> doSomething3(String message) throws InterruptedException {
        log.info("do something3: {}", message);
        Thread.sleep(1000);
        return CompletableFuture.completedFuture("; do something3: " + message);
    }
}

```



# **3. 注意事项**

在使用spring的异步多线程时经常回碰到多线程失效的问题，解决方式为：
异步方法和调用方法一定要**写在不同的类**中 ,如果写在一个类中,是没有效果的！
原因：

`spring`对`@Transactional`注解时也有类似问题，`spring`扫描时具有`@Transactional`注解方法的类时，是生成一个代理类，由代理类去开启关闭事务，而在同一个类中，方法调用是在类体内执行的，spring无法截获这个方法调用。



# Reference 

https://www.cnblogs.com/felixzh/p/12753365.html