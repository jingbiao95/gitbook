`@PostConstruct`和`CommandLineRunner接口`，它们都能用来实现初始化



## 共性与区别

- 两者都能够实现初始化的操作，
- `CommandLineRunner`会在服务启动之后被立即执行，CommandLineRunner可以有多个，且多个直接可以用@order注解进行排序。
- @PostConstruct则是能够在类加载的时候，为**当前类初始化**一些数据。类中只能有一个。



@PostConstruct是会在容器**没有完全启动**的情况下就能够进行一个加载初始化，类加载的时候进行一个初始化的操作.

CommandLineRunner的初始化一定是在容器**完全启动**之后执行的。需要在容器启动后进行一些初始化操作。



# @PostConstruct注解

**它用来修饰一个非静态的void方法。它会在服务器加载Servlet的时候运行，并且只运行一次**

```
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


@Component
public class TestInitC {

    @PostConstruct
    public void testA(){
        System.out.println("TestInitC PostConstruct executed...");
    }
}
```



# CommandLineRunner接口

```java
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Component
@Order(2)
public class TestInitA implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        System.out.println("TestInitA CommandLineRunner executed...");
    }
}
```



```java
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Component
@Order(1)
public class TestInitB implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        System.out.println("TestInitB CommandLineRunner executed...");
    }
}
```

# 查看结果

```
TestInitC PostConstruct executed...
。。。。。

TestInitB CommandLineRunner executed...
TestInitA CommandLineRunner executed...
```

@PostConstruct是不要求容器启动完成就行一个初始化的，而CommandLineRunner则是在容器启动完成之后才完成初始化的

CommandLineRunner其实也是容器启动的一部分。资料待查找

# reference

https://zhuanlan.zhihu.com/p/374230565