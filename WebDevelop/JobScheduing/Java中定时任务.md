
## JDK自带的 Timer

## JDK自带的ScheduledExecutorService

## Quartz框架

## SpringBoot @Scheduled注解

### demo编写

1. 首先，在项目启动类上添加 `@EnableScheduling` 注解，开启对定时任务的支持。**`@EnableScheduling` 注解的作用是发现注解 `@Scheduled` 的任务并在后台执行该任务。**

   ```java
   @SpringBootApplication
   @EnableScheduling
   public class ScheduledApplication {
   
   	public static void main(String[] args) {
   		SpringApplication.run(ScheduledApplication.class, args);
   	}
   
   }
   ```

   

2. 编写定时任务类和方法，定时任务类通过 Spring IOC 加载，使用 `@Component` 注解

3. 定时方法使用 `@Scheduled`注解。下述代码中，`fixedRate` 是 `long` 类型，表示任务执行的间隔毫秒数，下面代码中的定时任务每 3 秒执行一次。

   ```java
   @Component
   public class ScheduledTask {
   
       @Scheduled(fixedRate = 3000)
       public void scheduledTask() {
           System.out.println("任务执行时间：" + LocalDateTime.now());
       }
   
   }
   ```
   
   4. 运行工程，项目启动和运行日志如下，可见每 3 秒打印一次日志执行记录。
   
      ```
      Server is running ...
      任务执行时间-ScheduledTask：2020-06-23T18:02:14.747
      任务执行时间-ScheduledTask：2020-06-23T18:02:17.748
      任务执行时间-ScheduledTask：2020-06-23T18:02:20.746
      任务执行时间-ScheduledTask：2020-06-23T18:02:23.747
      ```
   
      

### @Scheduled注解说明

   在上面 Demo 中，使用了 `@Scheduled(fixedRate = 3000)` 注解来定义每过 3 秒执行的任务。对于 `@Scheduled` 的使用可以总结如下几种方式

   - `@Scheduled(fixedRate = 3000)` ：上一次开始执行时间点之后 3 秒再执行（`fixedRate` 属性：定时任务开始后再次执行定时任务的延时（需等待上次定时任务完成），单位毫秒）
   - `@Scheduled(fixedDelay = 3000)` ：上一次执行完毕时间点之后 3 秒再执行（`fixedDelay` 属性：定时任务执行完成后再次执行定时任务的延时（需等待上次定时任务完成），单位毫秒）
   - `@Scheduled(initialDelay = 1000, fixedRate = 3000)` ：第一次延迟1秒后执行，之后按 `fixedRate` 的规则每 3 秒执行一次（ `initialDelay` 属性：第一次执行定时任务的延迟时间，需配合 `fixedDelay` 或者 `fixedRate` 来使用）
   - `@Scheduled(cron="0 0 2 1 * ? *")` ：通过 `cron` 表达式定义规则.

### 多线程执行定时任务

默认开启的是在一个线程中执行定时任务，若是有多个定时，应该开启多线程

1. 创建配置类：在传统的 Spring 项目中，我们可以在 xml 配置文件添加 task 的配置，而在 Spring Boot 项目中一般使用 config 配置类的方式添加配置，所以新建一个 `AsyncConfig` 类。**在配置类中，使用 `@EnableAsync` 注解开启异步事件的支持。**

   ```java
   import org.springframework.context.annotation.Bean;
   import org.springframework.context.annotation.Configuration;
   import org.springframework.scheduling.annotation.EnableAsync;
   import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
   
   import java.util.concurrent.Executor;
   
   @Configuration
   @EnableAsync
   public class AsyncConfig {
   
       private int corePoolSize = 10;
       private int maxPoolSize = 200;
       private int queueCapacity = 10;
       @Bean
       public Executor taskExecutor() {
           ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
           executor.setCorePoolSize(corePoolSize);
           executor.setMaxPoolSize(maxPoolSize);
           executor.setQueueCapacity(queueCapacity);
           executor.initialize();
           return executor;
       }
   }
   ```

   - `@Configuration`：表明该类是一个配置类
   - `@EnableAsync`：开启异步事件的支持

2. 在定时任务的类或者方法上添加 `@Async` 注解，表示是异步事件的定时任务。

   ```java
   @Component
   @Async
   public class ScheduledTask {
       private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ScheduledTask.class);
       
   
       @Scheduled(cron = "0/5 * * * * *")
       public void scheduled(){
           logger.info("使用cron  线程名称：{}",Thread.currentThread().getName());
       }
       
       @Scheduled(fixedRate = 5000)
       public void scheduled1() {
           logger.info("fixedRate--- 线程名称：{}",Thread.currentThread().getName());
       }
       @Scheduled(fixedDelay = 5000)
       public void scheduled2() {
           logger.info("fixedDelay  线程名称：{}",Thread.currentThread().getName());
       }
   }
   ```

   3. 运行程序，控制台输出如下，可以看到，定时任务是在多个线程中执行的。
   
      ```
      2020-06-23 23:45:08.514  INFO 34824 : fixedRate--- 线程名称：taskExecutor-1
      2020-06-23 23:45:08.514  INFO 34824 : fixedDelay  线程名称：taskExecutor-2
      2020-06-23 23:45:10.005  INFO 34824 : 使用cron  线程名称：taskExecutor-3
      
      2020-06-23 23:45:13.506  INFO 34824 : fixedRate--- 线程名称：taskExecutor-4
      2020-06-23 23:45:13.510  INFO 34824 : fixedDelay  线程名称：taskExecutor-5
      2020-06-23 23:45:15.005  INFO 34824 : 使用cron  线程名称：taskExecutor-6
      
      2020-06-23 23:45:18.509  INFO 34824 : fixedRate--- 线程名称：taskExecutor-7
      2020-06-23 23:45:18.511  INFO 34824 : fixedDelay  线程名称：taskExecutor-8
      2020-06-23 23:45:20.005  INFO 34824 : 使用cron  线程名称：taskExecutor-9
      
      2020-06-23 23:45:23.509  INFO 34824 : fixedRate--- 线程名称：taskExecutor-10
      2020-06-23 23:45:23.511  INFO 34824 : fixedDelay  线程名称：taskExecutor-1
      2020-06-23 23:45:25.005  INFO 34824 : 使用cron  线程名称：taskExecutor-2
      
      2020-06-23 23:45:28.509  INFO 34824 : fixedRate--- 线程名称：taskExecutor-3
      2020-06-23 23:45:28.512  INFO 34824 : fixedDelay  线程名称：taskExecutor-4
      2020-06-23 23:45:30.005  INFO 34824 : 使用cron  线程名称：taskExecutor-5
      
      2020-06-23 23:45:33.509  INFO 34824 : fixedRate--- 线程名称：taskExecutor-6
      2020-06-23 23:45:33.513  INFO 34824 : fixedDelay  线程名称：taskExecutor-7
      2020-06-23 23:45:35.005  INFO 34824 : 使用cron  线程名称：taskExecutor-8
      
      ...
      ```
   
      







## 分布式任务调度