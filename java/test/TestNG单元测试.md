













## TestNG依赖关系

在`pom.xml`中添加TestNG库

```xml
	<dependencies>
        <dependency>
            <groupId>org.testng</groupId>
            <artifactId>testng</artifactId>
            <version>6.8.7</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>3.8.1</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
```



## TestNG的基本注解

| 注解              | 描述                                                         |
| ----------------- | ------------------------------------------------------------ |
| **@BeforeSuite**  | 注解的方法将只运行一次，运行所有**测试前**此**套件**中。     |
| **@AfterSuite**   | 注解的方法将只运行一次**此套件中**的所有测试都**运行之后**。 |
| **@BeforeClass**  | 注解的方法将只运行一次先行先试在**当前类**中的方法调用。     |
| **@AfterClass**   | 注解的方法将只运行一次后已经运行在当前类中的所有测试方法。   |
| **@BeforeTest**   | 注解的方法将被运行之前的任何测试方法属于内部类的标签的运行。 |
| **@AfterTest**    | 注解的方法将被运行后，所有的测试方法，属于内部类的标签的运行。 |
| **@BeforeGroups** | 组的列表，这种配置方法将之前运行。此方法是保证在运行属于任何这些组第一个测试方法，该方法被调用。 |
| **@AfterGroups**  | 组的名单，这种配置方法后，将运行。此方法是保证运行后不久，最后的测试方法，该方法属于任何这些组被调用。 |
| **@BeforeMethod** | 注解的方法将每个测试方法之前运行。                           |
| **@AfterMethod**  | 被注释的方法将被运行后，每个测试方法。                       |
| **@DataProvider** | 标志着一个方法，提供数据的一个测试方法。注解的方法必须返回一个Object[] []，其中每个对象[]的测试方法的参数列表中可以分配。该@Test 方法，希望从这个DataProvider的接收数据，需要使用一个dataProvider名称等于这个注解的名字。 |
| **@Factory**      | 作为一个工厂，返回TestNG的测试类的对象将被用于标记的方法。该方法必须返回Object[]。 |
| **@Listeners**    | 定义一个测试类的监听器。                                     |
| **@Parameters**   | 介绍如何将参数传递给@Test方法。                              |
| **@Test**         | 标记一个类或方法作为测试的一部分。                           |





## TestNG: Idea 引入 testng.xml 自动生成插件

### 1.安装插件: Create TestNG XML

1.1进入 插件管理	`file > settings > plugins`

1.2搜索插件 

1. 单击 `Browse repositories...`
2. 找到插件 `Create TestNG XML`



### 2.使用

创建 testng.xml 文件

1. 右键项目
2. 选择 `Create TestNG XML`





## 期望异常测试（Expected Test）

单元测试可以检查抛出预期异常( expected exceptions)的代码



## TestNG单元测试分层

### Controller层 ###

*   模拟HTTP request和response并初始化
   
    

    `BaseTest.class`

    ```java
    private MockHttpServletRequest request;
    
    private MockHttpServletResponse response;
    
    // 实例化HTTP request对象
    request = new MockHttpServletRequest();
    // 设置HTTP request编码格式
    response = new MockHttpServletResponse();
    // 实例化HTTP response对象
    request.setCharacterEncoding(CHARACTER_ENCODING);
    ```
    
*   模拟视图适配器

    ```java
    @InjectMocks
    private final AnnotationMethodHandlerAdapter handlerAdapter = new AnnotationMethodHandlerAdapter();
    ```
    
* 模拟Controller

  ```java
  @InjectMocks
  private final AbilityController abilityController = new AbilityController();
  ```

*   设置request URI、Method、body
    
    ```java
    // 设置HTTP URI
    getRequest().setRequestURI("/V1/admin/abilities/ssbServiceFlow/10001");
    // 设置HTTP Method
    getRequest().setMethod("GET");
    // 设置HTTP body
getRequest().setQueryString("{\"abilityId\":10001}");   
    ```
    
*   方法内部业务模拟

    ```java
    final String expectedResult = "{\"bipCode\":0000}";
    Mockito.when(abilityService.ssbServiceFlow(Mockito.anyInt())).thenReturn("{\"bipCode\":0000}");   
    ```
    
*   使用模型视图获取调用结果

    ```java
    final ModelAndView mav = handlerAdapter.handle(getRequest(), getResponse(), abilityController);
    final Map<String, Object> map = mav.getModel();
    final ResponseDto responseDto = (ResponseDto) map.get("responseDto");
    ```
    
*   断言

    ```java
    Assert.assertEquals(expectedResult, responseDto.getResult());
    ```
    
*   示例代码

    ```java
    import org.mockito.MockitoAnnotations;
    import org.springframework.mock.web.MockHttpServletRequest;
    import org.springframework.mock.web.MockHttpServletResponse;
    import org.testng.annotations.BeforeClass;
    
    /**
     * Controller层testNg单元测试父类
     */
    public class BaseTest {
    
        /**
         * HTTP request
         */
        private MockHttpServletRequest request;
    
        /**
         * HTTP response
         */
        private MockHttpServletResponse response;
    
        /**
         * HTTP request character encoding
         */
        private static final String CHARACTER_ENCODING = "UTF-8";
    
        public BaseTest() {
    
        }
    
        @BeforeClass(alwaysRun = true)
        public void init() {
    
            // 初始化当前测试类所有@Mock注解模拟对象
            MockitoAnnotations.initMocks(this);
            // 实例化HTTP request对象
            request = new MockHttpServletRequest();
            // 设置HTTP request编码格式
            response = new MockHttpServletResponse();
            // 实例化HTTP response对象
            request.setCharacterEncoding(CHARACTER_ENCODING);
    
        }
    
        public MockHttpServletRequest getRequest() {
    		return request;
        }
    
        public void setRequest(final MockHttpServletRequest request) {
    		this.request = request;
        }
    
        public MockHttpServletResponse getResponse() {
    		return response;
        }
    
        public void setResponse(final MockHttpServletResponse response) {
    		this.response = response;
        }
    
    }
    ```



```java
import java.util.Map;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.chinamobile.cmss.eshub.services.controller.BaseTest;
import com.chinamobile.cmss.eshub.services.dto.ability.AbilityInfoDto;
import com.chinamobile.cmss.eshub.services.dto.ability.AbilityLogOffInputDto;
import com.chinamobile.cmss.eshub.services.dto.ability.AddAbilityInputDto;
import com.chinamobile.cmss.eshub.services.dto.common.ResponseDto;
import com.chinamobile.cmss.eshub.services.dto.partner.PartnerInputListDto;
import com.chinamobile.cmss.eshub.services.service.ability.IAbilityService;

public class AbilityControllerTest extends BaseTest {
    
        @InjectMocks
        private final AnnotationMethodHandlerAdapter handlerAdapter = new AnnotationMethodHandlerAdapter();
    
        @InjectMocks
        private final AbilityController abilityController = new AbilityController();
    
        @Mock
        private IAbilityService abilityService;
    
        @Test(enabled = true)
        public void testSsbServiceFlow() throws Exception {
    
            // 设置HTTP URI
            getRequest().setRequestURI("/V1/admin/abilities/ssbServiceFlow/10001");
            // 设置HTTP Method
            getRequest().setMethod("GET");
            // 设置HTTP body
            getRequest().setQueryString("{\"abilityId\":10001}");

            // 方法内部业务模拟
            final String expectedResult = "{\"bipCode\":0000}";
            				Mockito.when(abilityService.ssbServiceFlow(Mockito.anyInt())).thenReturn("{\"bipCode\":0000}");

            // 使用模型视图获取调用结果
            final ModelAndView mav = handlerAdapter.handle(getRequest(), getResponse(), abilityController);
            final Map<String, Object> map = mav.getModel();
            final ResponseDto responseDto = (ResponseDto) map.get("responseDto");
            // 断言
            Assert.assertEquals(expectedResult, responseDto.getResult());
        }
    
        @Test(enabled = true)
        public void testUpdateAbilityStatus() throws Exception {
    
            // 设置HTTP URI
            getRequest().setRequestURI("/V1/admin/abilities/status");
            // 设置HTTP Method
            getRequest().setMethod("PUT");
            // 设置HTTP body
            getRequest().setQueryString("{\"partnerList\":{\"abilityId\":10001},\"changeReason\":\"ok\"}");

            // 方法内部业务模拟
            Mockito.doNothing().when(abilityService).updateAppAbilityStatus(Mockito.isA(PartnerInputListDto.class));

            // 使用模型视图获取调用结果
            final ModelAndView mav = handlerAdapter.handle(getRequest(), getResponse(), abilityController);
            final Map<String, Object> map = mav.getModel();
            final ResponseDto responseDto = (ResponseDto) map.get("responseDto");
            // 断言
            Assert.assertNotNull(responseDto);
        }
    
        @Test(enabled = true)
        public void testAddAbility() throws Exception {
    
            final String queryString = "{\"abilityName\":\"newAbility\",\"description\":\"123\",\"abilityType\":1,\"serviceId\":123,\"isAuth\":1,\"urlPath\":\"/nihao\",\"businessCode\":\"123\",\"transactionCode\":\"1233\",\"isSubscription\":0,\"isAsync\":0,\"isSandboxTest\":0}";

            // 设置HTTP URI
            getRequest().setRequestURI("/V1/admin/abilities");
            // 设置HTTP Method
            getRequest().setMethod("POST");
            // 设置HTTP body
            getRequest().setQueryString(queryString);

            // 方法内部业务模拟
            final AbilityInfoDto abilityInfoDto = Mockito.mock(AbilityInfoDto.class);
            Mockito.when(abilityService.addAbility(Mockito.isA(AddAbilityInputDto.class))).thenReturn(abilityInfoDto);

            // 使用模型视图获取调用结果
            final ModelAndView mav = handlerAdapter.handle(getRequest(), getResponse(), abilityController);
            final Map<String, Object> map = mav.getModel();
            final ResponseDto responseDto = (ResponseDto) map.get("responseDto");
            // 断言
            Assert.assertEquals(abilityInfoDto, responseDto.getResult());
        }
    
        @Test(enabled = true)
        public void testAbilityLogOff() throws Exception {
    
            // 设置HTTP URI
            getRequest().setRequestURI("/V1/admin/abilities");
            // 设置HTTP Method
            getRequest().setMethod("DELETE");
            // 设置HTTP body
            getRequest().setQueryString("{\"abilityId\":10001}");

            // 方法内部业务模拟
            Mockito.doNothing().when(abilityService).abilityLogOff(Mockito.isA(AbilityLogOffInputDto.class));

            // 使用模型视图获取调用结果
            final ModelAndView mav = handlerAdapter.handle(getRequest(), getResponse(), abilityController);
            final Map<String, Object> map = mav.getModel();
            final ResponseDto responseDto = (ResponseDto) map.get("responseDto");
            // 断言
            Assert.assertNotNull(responseDto);
        }
    }
```


### Service和Function层 ###


*   BaseTest父类

    ```java
    import org.mockito.MockitoAnnotations;
    import org.testng.annotations.BeforeMethod;
     
    /**
     * @author wangchao
     *
     */
    @SuppressWarnings("PMD.UnnecessaryConstructor")
    public class BaseTest {
     
        // private static final Logger LOGGER = LoggerFactory.getLogger(BaseTest.class);
     
        public BaseTest() {
     
        }
     
        @BeforeMethod(alwaysRun = true)
        public void init() {
     
            // 初始化当前测试类所有@Mock注解模拟对象
            MockitoAnnotations.initMocks(this);
        }
     
    }
    ```


*   测试类

    为每个类添加一个测试类，测试类继承BaseTest类


* 在测试类中首先Mock被测试类

  ```java
  /**
  * 待测试类，不使用@Resource注解，不依赖项目环境<br>
  * 使用new关键字，并且用@InjectMocks注入本测试中需要用到的变量
  */
  
  @InjectMocks
  ****Impl ****Impl = new ****Impl();
  ```

* 测试方法

  在测试类中使用一个总的测试方法，为该方法添加 `@Test(enabled = true)`注解，例如：

  ```java
  @Test(enabled = true)
  public void testMain(){
   
      test**();
      test****();
      test******();
      test********();
  }
  ```

* 编写测试方法

  ```java
  void test****() {
   
      //To do something
   
  }
  ```

*   有返回值的被测试方法，在测试方法中要如下进行3个步骤：

    (1).定义预期值`respect`

    (2).获得模拟调用被测试方法获得结果值`result`

    (3).断言`Assert.assertEquals(result, respect）`;

* 没有返回值的测试方法，在测试方法中要调用一次被测试方法

  针对被测试方法的入参

  - 如果入参为JAVA的基本类型，不需要Mock

  - 如果是其他的JAVA类则需要Mock，语句如下：

  ```java
     final *****Dto ******Dto = Mockito.mock(*****Dto.class);
  ```

  在被测试方法中，涉及到`******Dto.get****()`;需要使用如下语句为该方法设置返回值：

  ```java
     Mockito.when(******Dto.get****()).thenReturn(**);
  ```

  如果`thenRetrun`中的对象（非基本类型）即`******Dto.get****()`的返回值在后面没有用到，则使用如下语句：

  ```java
     Mockito.when(******Dto.get****()).thenReturn(new **());
  ```

  如果有用到，例如get获取属性，或者调用方法等，那么在测试方法中需要新建并实例化该对象，如下：

  ```java
  final *****Dto ******Dto = new *****Dto();
   
  Mockito.when(******Dto.get****()).thenReturn(******Dto);
  ```

  如果被测试类中使用了该对象，则在测试类中需要给该对象设置值；

  示例如下：

  ```java
     final List<AbilityInfoResponseDto> abilityInfoList = new ArrayList<AbilityInfoResponseDto>();
   
     final AbilityInfoResponseDto abilityInfoResponseDto = new AbilityInfoResponseDto();
   
     abilityInfoResponseDto.setAbilityType(0);
   
     abilityInfoList.add(abilityInfoResponseDto);
   
     Mockito.when(******Dao.get****()).thenReturn(abilityInfoList);
  ```

*   在被测试类中所有的依赖注入的一些Dao、Function等都需要在测试类中Mock，语句如下：

    ```java
   @Mock
       private I****Dao ****Dao;
   ```

*   对于一些无法获得Mock结果的情况，在when语句中参数使用

    `Mockito.isA(***.class)`、`Mockito.anyInt()`、`Mockito.anyString()`等方法，示例如下：

    ```java
   Mockito.when(******Dto.select****(Mockito.isA(***.class))).thenReturn(**);
   ```

* 对于没有返回值，但必须模拟的方法，使用`doNothing.when`语句

  示例如下：

  ```java
     Mockito.doNothing().when(***Dao).delete****(Mockito.anyInt());
  ```

*   当expect和result为对象时，一般情况下直接用

    ```java
   Assert.assertEquals(expected, result);
   ```

    肯定是错误的，可以使用：

    ```java
   Assert.assertEquals(expected.getId(), result.getId());
    ```
   
    某些情况下可以使用：
   
    ```java
        Assert.notNull(result);
    ```

### TestNG in IDE ###

#### 自动创建测试类 ####

*   在待测试类上右键，选择TestNG->create TestNG class
*   在弹框中选择Select all，点击next，做简单配置，点击Finish
*   包和测试类会自动生成，自动创建无法继承父类和实现接口，也不能代写测试逻辑

#### 通过测试 ####

*   自动创建后，每个方法里会有一行代码：
    
    ```java
throw new RuntimeException("Test not implemented");
    ```
    
*   使用全局替换成

    ```java
    LOGGER.info("success");
    ```
    
    可以通过测试
    
#### test case ####

*   全选项目，右键TestNG   ->   convert to TestNG会自动生成testng.xml，包含全部的测试类，文件内容如下：


```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "http://testng.org/testng-1.0.dtd">
<suite name="Suite" parallel="none">
  <test name="Test">
    <classes>
      <class name="com.chinamobile.cmss.eshub.services.annotations.AfterSystemControllerLogTest"/>
      <class name="com.chinamobile.cmss.eshub.services.dto.log.ClientTotalCountsListLogDtoTest"/>
      <class name="com.chinamobile.cmss.eshub.services.constants.FrameConstantsTest"/>
      <class name="com.chinamobile.cmss.eshub.services.dto.log.BusinessLogListTotalOutputDtoTest"/>
    </classes>
  </test> <!-- Test -->
</suite> <!-- Suite -->

```


*   在testng.xml 右键 run as -> TestNG suite，将运行全部的测试方法。





# Reference

https://howtodoinjava.com/java-testng-tutorials/

https://testng.org/doc/documentation-main.html#introduction

