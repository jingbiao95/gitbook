## 1.  Maven 依赖

spring-boot-starter-test 包含创建和执行单元测试的所有必须依赖。

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-test</artifactId>
</dependency>
```



如果你不使用 spring boot 你就需要加入下面的依赖：

```
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.12</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>2.15.0</version>
</dependency>
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-inline</artifactId>
    <version>2.15.0</version>
</dependency>
```

## 2. 创建项目

**MockitoJUnitRunner** 自动初始化所有被`@Mock` 和 `@InjectMocks `注解的对象

```java
@RunWith(MockitoJUnitRunner.class)
public class TestEmployeeManager {
    @InjectMocks
    EmployeeManager manager;

    @Mock
    EmployeeDao dao;
    //tests
}
```

如果我们没有使用MockitoJUnitRunner 这种方式，那么我们可以使用static 方法MockitoAnnotations.initMocks()。这个方法，也能在初始化junit tests是，实例化mock 对象。

```java
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Random;

import static org.mockito.Mockito.*;
public class TestEmployeeManager {

    @InjectMocks
    EmployeeManager manager;

    @Mock
    EmployeeDao dao;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }
    //tests

}
```



## 3.注解使用

### @Mock注解和mock方法

- `org.mockito.Mockito` 的` mock` 方法可以模拟类和接口。

- `@Mock` 注解可以理解为对 mock 方法的一个替代。

  使用该注解时，要使用`MockitoAnnotations.initMocks` 方法，让注解生效。

**mock方法**

```java
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Random;

import static org.mockito.Mockito.*;
public class MockitoDemo {

    @Test
    public void test() {
        Random mockRandom = mock(Random.class);
        when(mockRandom.nextInt()).thenReturn(100);  // 指定调用 nextInt 方法时，永远返回 100

        Assert.assertEquals(100, mockRandom.nextInt());
        Assert.assertEquals(100, mockRandom.nextInt());
    }
}
```

`mock 对象的方法的返回值默认都是返回类型的默认值`。例如，返回类型是 int，默认返回值是 0；返回类型是一个类，默认返回值是 `null`。

```java
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Random;

import static org.mockito.Mockito.*;
public class MockitoDemo {

    @Mock
    private Random random;

    @Test
    public void test() {
        // 让注解生效
        MockitoAnnotations.initMocks(this);

        when(random.nextInt()).thenReturn(100);

        Assert.assertEquals(100, random.nextInt());
    }

}
```

`MockitoAnnotations.initMocks` 放在 junit 的 `@Before` 注解修饰的函数中更合适。

```java
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Random;

import static org.mockito.Mockito.*;
public class MockitoDemo {

    @Mock
    private Random random;

    @Before
    public void before() {
        // 让注解生效
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void test() {
        when(random.nextInt()).thenReturn(100);

        Assert.assertEquals(100, random.nextInt());
    }

}
```



### @Spy和spy方法

spy 和 mock不同，不同点是：

- spy 的参数是对象示例，mock 的参数是 class。
- 被 spy 的对象，调用其方法时默认会走真实方法。mock 对象不会。

```java
import org.junit.Assert;
import org.junit.Test;
import static org.mockito.Mockito.*;


class ExampleService {

    int add(int a, int b) {
        return a+b;
    }

}

public class MockitoDemo {

    // 测试 spy
    @Test
    public void test_spy() {

        ExampleService spyExampleService = spy(new ExampleService());

        // 默认会走真实方法
        Assert.assertEquals(3, spyExampleService.add(1, 2));

        // 打桩后，不会走了
        when(spyExampleService.add(1, 2)).thenReturn(10);
        Assert.assertEquals(10, spyExampleService.add(1, 2));

        // 但是参数比匹配的调用，依然走真实方法
        Assert.assertEquals(3, spyExampleService.add(2, 1));

    }

    // 测试 mock
    @Test
    public void test_mock() {

        ExampleService mockExampleService = mock(ExampleService.class);

        // 默认返回结果是返回类型int的默认值
        Assert.assertEquals(0, mockExampleService.add(1, 2));

    }
}
```

spy 对应注解 @Spy，和 @Mock 是一样用的。需要调用` MockitoAnnotations.initMocks(this);`



对于`@Spy`，如果发现修饰的变量是 null，会自动调用类的无参构造函数来初始化。如果没有无参构造函数，必须使用写法2。

```
// 写法1
@Spy
private ExampleService spyExampleService;

// 写法2
@Spy
private ExampleService spyExampleService = new ExampleService();


```



### @InjectMocks

- **@InjectMocks** 也可以创建模拟对象实现类，另外还可以为标记@Mock对象注入模拟对象实现

mockito 会将 `@Mock`、`@Spy` 修饰的对象自动注入到 `@InjectMocks` 修饰的对象中。



注入方式有多种，mockito 会按照下面的顺序尝试注入：

1. 构造函数注入
2. 设值函数注入（set函数）
3. 属性注入

```java
package demo;

import java.util.Random;

public class HttpService {

    public int queryStatus() {
        // 发起网络请求，提取返回结果
        // 这里用随机数模拟结果
        return new Random().nextInt(2);
    }

}
```

```java
package demo;

public class ExampleService {

    private HttpService httpService;

    public String hello() {
        int status = httpService.queryStatus();
        if (status == 0) {
            return "你好";
        }
        else if (status == 1) {
            return "Hello";
        }
        else {
            return "未知状态";
        }
    }

}
```

```java
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.when;


public class ExampleServiceTest {

    @Mock
    private HttpService httpService;

    @InjectMocks
    private ExampleService exampleService = new ExampleService(); // 会将 httpService 注入进去

    @Test
    public void test01() {

        MockitoAnnotations.initMocks(this);

        when(httpService.queryStatus()).thenReturn(0);

        Assert.assertEquals("你好", exampleService.hello());

    }

}
```

## 4.方法的参数匹配

如果参数匹配即生命了精确匹配，也声明了模糊匹配；又或者同一个值的精确匹配出现了两次，**会匹配符合匹配条件的最新声明的匹配**。

#### 精确匹配

```java
 	@Test
    public void test() {
        List mockList = mock(List.class);

        Assert.assertEquals(0, mockList.size());
        Assert.assertEquals(null, mockList.get(0));

        mockList.add("a");  // 调用 mock 对象的写方法，是没有效果的

        Assert.assertEquals(0, mockList.size());      // 没有指定 size() 方法返回值，这里结果是默认值
        Assert.assertEquals(null, mockList.get(0));   // 没有指定 get(0) 返回值，这里结果是默认值

        when(mockList.get(0)).thenReturn("a");          // 指定 get(0)时返回 a

        Assert.assertEquals(0, mockList.size());        // 没有指定 size() 方法返回值，这里结果是默认值
        Assert.assertEquals("a", mockList.get(0));      // 因为上面指定了 get(0) 返回 a，所以这里会返回 a

        Assert.assertEquals(null, mockList.get(1));     // 没有指定 get(1) 返回值，这里结果是默认值
    }
}
```

对于精确匹配，还可以用` eq`方法，例如

```java
	@Test
    public void test() {

        mockStringList.add("a");

        when(mockStringList.get(eq(0))).thenReturn("a");  // 虽然可以用eq进行精确匹配，但是有点多余
        when(mockStringList.get(eq(1))).thenReturn("b");

        Assert.assertEquals("a", mockStringList.get(0));
        Assert.assertEquals("b", mockStringList.get(1));

    }
```

#### 模糊匹配

```java
	@Test
    public void test() {

        mockStringList.add("a");

        when(mockStringList.get(anyInt())).thenReturn("a");  // 使用 Mockito.anyInt() 匹配所有的 int

        Assert.assertEquals("a", mockStringList.get(0)); 
        Assert.assertEquals("a", mockStringList.get(1));

    }
```

目前 mockito 有多种匹配函数，部分如下：

| 函数名               | 匹配类型                                  |
| :------------------- | :---------------------------------------- |
| any()                | 所有对象类型                              |
| anyInt()             | 基本类型 int、非 null 的 Integer 类型     |
| anyChar()            | 基本类型 char、非 null 的 Character 类型  |
| anyShort()           | 基本类型 short、非 null 的 Short 类型     |
| anyBoolean()         | 基本类型 boolean、非 null 的 Boolean 类型 |
| anyDouble()          | 基本类型 double、非 null 的 Double 类型   |
| anyFloat()           | 基本类型 float、非 null 的 Float 类型     |
| anyLong()            | 基本类型 long、非 null 的 Long 类型       |
| anyByte()            | 基本类型 byte、非 null 的 Byte 类型       |
| anyString()          | String 类型(不能是 null)                  |
| anyList()            | `List<T>` 类型(不能是 null)               |
| anyMap()             | `Map<K, V>`类型(不能是 null)              |
| anyCollection()      | `Collection<T>`类型(不能是 null)          |
| anySet()             | `Set<T>`类型(不能是 null)                 |
| `any(Class<T> type)` | type类型的对象(不能是 null)               |
| isNull()             | null                                      |
| notNull()            | 非 null                                   |
| isNotNull()          | 非 null                                   |



## 5.方法的处理结果

- thenReturn 用来指定特定函数和参数调用的返回值。

- doReturn 的作用和 thenReturn 相同，但使用方式不同

- doNothing 用于让 void 函数什么都不做。

- then 和 thenAnswer 的效果是一样的。它们的参数是实现 Answer 接口的对象，在改对象中可以获取调用参数，自定义返回值。

- doAnswer 的作用和 [thenAnswer](https://www.letianbiji.com/java-mockito/mockito-then-thenanswer.html) 相同，但使用方式不同

- thenThrow 用来让函数调用抛出异常。

- doThrow 可以让返回void的函数抛出异常

- thenCallRealMethod 可以用来重置 spy 对象的特定方法特定参数调用

  

### thenReturn



thenReturn 中可以指定多个返回值。在调用时返回值依次出现。若调用次数超过返回值的数量，再次调用时返回最后一个返回值。

```java
 	@Test
    public void test() {
        Random mockRandom = mock(Random.class);

        when(mockRandom.nextInt()).thenReturn(1, 2, 3);

        Assert.assertEquals(1, mockRandom.nextInt());
        Assert.assertEquals(2, mockRandom.nextInt());
        Assert.assertEquals(3, mockRandom.nextInt());
        Assert.assertEquals(3, mockRandom.nextInt());
        Assert.assertEquals(3, mockRandom.nextInt());
    }
```

### doReturn

doReturn 的作用和 thenReturn 相同，但使用方式不同：

```java
	@Test
    public void test() {

        MockitoAnnotations.initMocks(this);

        Random random = mock(Random.class);
        doReturn(1).when(random).nextInt();

        Assert.assertEquals(1, random.nextInt());

    }
```

### doNothing 

使用 doNothing 让 void 函数什么都不做。因为 mock 对象中，void 函数就是什么都不做，所以该方法更适合 spy 对象。

```java
import org.junit.Test;
import static org.mockito.Mockito.*;

public class MockitoDemo {

    static class ExampleService {

        public void hello() {
            System.out.println("Hello");
        }

    }

    @Test
    public void test() {

        ExampleService exampleService = spy(new ExampleService());
        exampleService.hello();  // 会输出 Hello

        // 让 hello 什么都不做
        doNothing().when(exampleService).hello();
        exampleService.hello(); // 什么都不输出


    }

}
```



### then 和 thenAnswer 

then 和 thenAnswer 的效果是一样的。它们的参数是实现 Answer 接口的对象，在改对象中可以获取调用参数，自定义返回值。

```java
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

public class MockitoDemo {

    static class ExampleService {

        public int add(int a, int b) {
            return a+b;
        }

    }

    @Mock
    private ExampleService exampleService;

    @Test
    public void test() {

        MockitoAnnotations.initMocks(this);

        when(exampleService.add(anyInt(),anyInt())).thenAnswer(new Answer<Integer>() {
            @Override
            public Integer answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                // 获取参数
                Integer a = (Integer) args[0];
                Integer b = (Integer) args[1];

                // 根据第1个参数，返回不同的值
                if (a == 1) {
                    return 9;
                }
                if (a == 2) {
                    return 99;
                }
                if (a == 3) {
                    throw new RuntimeException("异常");
                }
                return 999;
            }
        });

        Assert.assertEquals(9, exampleService.add(1, 100));
        Assert.assertEquals(99, exampleService.add(2, 100));

        try {
            exampleService.add(3, 100);
            Assert.fail();
        } catch (RuntimeException ex) {
            Assert.assertEquals("异常", ex.getMessage());
        }
    }

}
```

### doAnswer

doAnswer 的作用和 thenAnswer 相同，但使用方式不同：

```java
import org.junit.Assert;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Random;

import static org.mockito.Mockito.*;

public class MockitoDemo {

    @Test
    public void test() {

        MockitoAnnotations.initMocks(this);

        Random random = mock(Random.class);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return 1;
            }
        }).when(random).nextInt();

        Assert.assertEquals(1, random.nextInt());

    }

}

```



### thenThrow 

thenThrow 用来让函数调用抛出异常。

thenThrow 中可以指定多个异常。在调用时异常依次出现。若调用次数超过异常的数量，再次调用时抛出最后一个异常。

```java
import org.junit.Assert;
import org.junit.Test;
import static org.mockito.Mockito.*;

import java.util.Random;

public class MockitoDemo {

    @Test
    public void test() {

        Random mockRandom = mock(Random.class);

        when(mockRandom.nextInt()).thenThrow(new RuntimeException("异常1"), new RuntimeException("异常2"));

        try {
            mockRandom.nextInt();
            Assert.fail();
        } catch (Exception ex) {
            Assert.assertTrue(ex instanceof RuntimeException);
            Assert.assertEquals("异常1", ex.getMessage());
        }

        try {
            mockRandom.nextInt();
            Assert.fail();
        } catch (Exception ex) {
            Assert.assertTrue(ex instanceof RuntimeException);
            Assert.assertEquals("异常2", ex.getMessage());
        }


    }

}


```



### doThrow 

如果一个对象的方法的**返回值是 void**，那么不能用 when .. thenThrow 让该方法抛出异常

```java
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.doThrow;

public class MockitoDemo {

    static class ExampleService {

        public void hello() {
            System.out.println("Hello");
        }

    }

    @Mock
    private ExampleService exampleService;

    @Test
    public void test() {

        MockitoAnnotations.initMocks(this);

        // 这种写法可以达到效果
        doThrow(new RuntimeException("异常")).when(exampleService).hello();

        try {
            exampleService.hello();
            Assert.fail();
        } catch (RuntimeException ex) {
            Assert.assertEquals("异常", ex.getMessage());
        }

    }

}

```





### thenCallRealMethod 

thenCallRealMethod 可以用来重置` spy `对象的特定方法特定参数调用。

```java
import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class MockitoDemo {

    static class ExampleService {

        public int add(int a, int b) {
            return a+b;
        }

    }

    @Test
    public void test() {

        ExampleService exampleService = spy(new ExampleService());

        // spy 对象方法调用会用真实方法，所以这里返回 3
        Assert.assertEquals(3, exampleService.add(1, 2));

        // 设置让 add(1,2) 返回 100
        when(exampleService.add(1, 2)).thenReturn(100);
        when(exampleService.add(2, 2)).thenReturn(100);
        Assert.assertEquals(100, exampleService.add(1, 2));
        Assert.assertEquals(100, exampleService.add(2, 2));

        // 重置 spy 对象，让 add(1,2) 调用真实方法，返回 3
        when(exampleService.add(1, 2)).thenCallRealMethod();
        Assert.assertEquals(3, exampleService.add(1, 2));

        // add(2, 2) 还是返回 100
        Assert.assertEquals(100, exampleService.add(2, 2));
    }

}

```



## 6.Mockito其他操作

### final class

https://github.com/mockito/mockito/wiki/What%27s-new-in-Mockito-2#mock-the-unmockable-opt-in-mocking-of-final-classesmethods

creating the file `src/test/resources/mockito-extensions/org.mockito.plugins.MockMaker`containing a single line:

```
mock-maker-inline
```

### Verify

使用 verify 可以校验 mock 对象是否**发生过**某些操作

### mockingDetails

Mockito 的 mockingDetails 方法会返回 MockingDetails 对象，它的 isMock 方法可以判断对象是否为 mock 对象，isSpy 方法可以判断对象是否为 spy 对象

### 使用 PowerMock 让 Mockito 支持静态方法

Mockito 默认是不支持静态方法

```java
public class ExampleService {

    public static int add(int a, int b) {
        return a+b;
    }

}
```

尝试给静态方法打桩，会报错：

```java
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MockitoDemo {

    @Test
    public void test() {

        // 会报错
        when(ExampleService.add(1, 2)).thenReturn(100);

    }

}
```

用 Powermock 弥补 Mockito 缺失的静态方法 mock 功能

```java
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)     // 这是必须的
@PrepareForTest(ExampleService.class)  // 声明要处理 ExampleService
public class MockitoDemo {
    @Test
    public void test() {

        PowerMockito.mockStatic(ExampleService.class);  // 这也是必须的

        when(ExampleService.add(1, 2)).thenReturn(100);

        Assert.assertEquals(100, ExampleService.add(1, 2));
        Assert.assertEquals(0, ExampleService.add(2, 2));

    }
}
```

PowerMockRunner 支持 Mockito 的 @Mock 等注解

##  SpringBoot MVC层使用

### Controller层

### Servicese层

### 其它层

### Redis Mock

当我们使用单元测试来验证应用程序代码时，如果代码中需要访问`Redis`，那么为了保证单元测试不依赖`Redis`，需要将整个`Redis` `mock`掉。在`Spring Boot`中结合`mockito`很容易做到这一点，如下代码：

```java
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.Mockito.when;

/**
 * mock掉整个RedisTemplate
 */
@ActiveProfiles("uttest")
@Configuration
public class RedisTemplateMocker {
    @Bean
    public RedisTemplate redisTemplate() {
        RedisTemplate redisTemplate = Mockito.mock(RedisTemplate.class);
        ValueOperations valueOperations = Mockito.mock(ValueOperations.class);
        SetOperations setOperations = Mockito.mock(SetOperations.class);
        HashOperations hashOperations = Mockito.mock(HashOperations.class);
        ListOperations listOperations = Mockito.mock(ListOperations.class);
        ZSetOperations zSetOperations = Mockito.mock(ZSetOperations.class);

        when(redisTemplate.opsForSet()).thenReturn(setOperations);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(redisTemplate.opsForList()).thenReturn(listOperations);
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);

        RedisOperations redisOperations = Mockito.mock(RedisOperations.class);
        RedisConnection redisConnection = Mockito.mock(RedisConnection.class);
        RedisConnectionFactory redisConnectionFactory = Mockito.mock(RedisConnectionFactory.class);
        when(redisTemplate.getConnectionFactory()).thenReturn(redisConnectionFactory);
        when(valueOperations.getOperations()).thenReturn(redisOperations);
        when(redisTemplate.getConnectionFactory().getConnection()).thenReturn(redisConnection);

        return redisTemplate;
    }
}
```







# Reference

https://www.letianbiji.com/java-mockito/mockito-hello-world.html

https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html