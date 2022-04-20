[toc]



----

# 一、bootstrap.yml（bootstrap.properties）与application.yml（application.properties）执行顺序

bootstrap.yml（bootstrap.properties）用来在程序引导时执行，应用于更加早期配置信息读取，如可以使用来配置application.yml中使用到参数等

application.yml（application.properties) 应用程序特有配置信息，可以用来配置后续各个模块中需使用的公共参数等。

bootstrap.yml 先于 application.yml 加载

# 二、典型的应用场景如下：



- 当使用 Spring Cloud Config Server 的时候，你应该在 bootstrap.yml 里面指定 spring.application.name 和 spring.cloud.config.server.git.uri
- 和一些加密/解密的信息

技术上，bootstrap.yml 是被一个父级的 Spring ApplicationContext 加载的。这个父级的 Spring ApplicationContext是先加载的，在加载application.yml 的 ApplicationContext之前。

为何需要把 config server 的信息放在 bootstrap.yml 里？

当使用 Spring Cloud 的时候，配置信息一般是从 config server 加载的，为了取得配置信息（比如密码等），你需要一些提早的引导配置。因此，把 config server 信息放在 bootstrap.yml，用来加载在这个时期真正需要的配置信息。



# 三、高级使用场景

### 启动上下文

**Spring Cloud会创建一个`Bootstrap Context`，作为Spring应用的`Application Context`的父上下文**。**初始化的时候，`Bootstrap Context`负责从外部源加载配置属性并解析配置。**这两个上下文共享一个从外部获取的`Environment`。`Bootstrap`属性有高优先级，默认情况下，它们不会被本地配置覆盖。 `Bootstrap context`和`Application Context`有着不同的约定，所以新增了一个`bootstrap.yml`文件，而不是使用`application.yml` (或者`application.properties`)。保证`Bootstrap Context`和`Application Context`配置的分离。下面是一个例子： **bootstrap.yml**

```yaml
spring:
  application:
    name: foo
  cloud:
    config:
      uri: ${SPRING_CONFIG_URI:http://localhost:8888}
```

推荐在`bootstrap.yml` or `application.yml`里面配置`spring.application.name`. 你可以通过设置`spring.cloud.bootstrap.enabled=false`来禁用`bootstrap`。



### 应用上下文层次结构

如果你通过`SpringApplication`或者`SpringApplicationBuilder`创建一个`Application Context`,那么会为spring应用的`Application Context`创建父上下文`Bootstrap Context`。在Spring里有个特性，子上下文会继承父类的`property sources` and `profiles` ，所以`main application context` 相对于没有使用Spring Cloud Config，会新增额外的`property sources`。额外的`property sources`有：

- “bootstrap” : 如果在`Bootstrap Context`扫描到`PropertySourceLocator`并且有属性，则会添加到CompositePropertySource`。**Spirng Cloud Config就是通过这种方式来添加的属性的**，详细看源码`**ConfigServicePropertySourceLocator**`。下面也也有一个例子自定义的例子。
- “applicationConfig: [classpath:bootstrap.yml]” ，（如果有`spring.profiles.active=production`则例如 applicationConfig: [classpath:/bootstrap.yml]#production）: 如果你使用`bootstrap.yml`来配置`Bootstrap Context`，他比`application.yml`优先级要低。它将添加到子上下文，作为Spring Boot应用程序的一部分。下文有介绍。

由于优先级规则，`Bootstrap Context`不包含从`bootstrap.yml`来的数据，但是可以用它作为默认设置。

你可以很容易的**扩展任何你建立的上下文层次**，可以使用它提供的接口，或者使用`SpringApplicationBuilder`包含的方法（`parent()`，`child()`，`sibling()`）。`Bootstrap Context`将是最高级别的父类。扩展的每一个`Context`都有有自己的`bootstrap property source`（有可能是空的）。扩展的每一个`Context`都有不同`spring.application.name`。同一层层次的父子上下文原则上也有一有不同的名称，因此，也会有不同的Config Server配置。子上下文的属性在相同名字的情况下将覆盖父上下文的属性。

**注意`SpringApplicationBuilder`允许共享`Environment`到所有层次，但是不是默认的**。因此，同级的兄弟上下文不在和父类共享一些东西的时候不一定有相同的`profiles`或者`property sources`。

### 修改Bootstrap属性配置

源码位置`BootstrapApplicationListener`。

```
String configName = environment.resolvePlaceholders("${spring.cloud.bootstrap.name:bootstrap}");

    String configLocation = environment.resolvePlaceholders("${spring.cloud.bootstrap.location:}");

    Map<String, Object> bootstrapMap = new HashMap<>();bootstrapMap.put("spring.config.name",configName);
    if(StringUtils.hasText(configLocation)){
        bootstrapMap.put("spring.config.location", configLocation);
    }
```



 `bootstrap.yml`是由`spring.cloud.bootstrap.name`（默认:”bootstrap”）或者`spring.cloud.bootstrap.location`（默认空）。这些属性行为与`spring.config.*`类似，通过它的`Environment`来配置引导`ApplicationContext`。如果有一个激活的`profile`（来源于`spring.profiles.active`或者`Environment`的Api构建），例如`bootstrap-development.properties` 就是配置了`profile`为`development`的配置文件.

### 覆盖远程属性

`property sources`被`bootstrap context` 添加到应用通常通过远程的方式，比如”Config Server”。默认情况下，本地的配置文件不能覆盖远程配置，但是可以通过启动命令行参数来覆盖远程配置。**如果需要本地文件覆盖远程文件，需要在远程配置文件里设置授权** 
`spring.cloud.config.allowOverride=true`（这个配置不能在本地被设置）。一旦设置了这个权限，你可以配置更加细粒度的配置来配置覆盖的方式，

比如： 
\- `spring.cloud.config.overrideNone=true` 覆盖任何本地属性 
\- `spring.cloud.config.overrideSystemProperties=false` 仅仅系统属性和环境变量 
源文件见**`PropertySourceBootstrapProperties`**

### 自定义启动配置

`bootstrap context`是依赖`/META-INF/spring.factories`文件里面的`org.springframework.cloud.bootstrap.BootstrapConfiguration`条目下面，通过逗号分隔的Spring  `@Configuration`类来建立的配置。任何`main application context`需要的自动注入的Bean可以在这里通过这种方式来获取。这也是`ApplicationContextInitializer`建立`@Bean`的方式。可以通过`@Order`来更改初始化序列，默认是”last”。

```java
# spring-cloud-context-1.1.1.RELEASE.jar
# spring.factories
# AutoConfiguration
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
org.springframework.cloud.autoconfigure.ConfigurationPropertiesRebinderAutoConfiguration,\
org.springframework.cloud.autoconfigure.RefreshAutoConfiguration,\
org.springframework.cloud.autoconfigure.RefreshEndpointAutoConfiguration,\
org.springframework.cloud.autoconfigure.LifecycleMvcEndpointAutoConfiguration

# Application Listeners
org.springframework.context.ApplicationListener=\
org.springframework.cloud.bootstrap.BootstrapApplicationListener,\
org.springframework.cloud.context.restart.RestartListener

# Bootstrap components
org.springframework.cloud.bootstrap.BootstrapConfiguration=\
org.springframework.cloud.bootstrap.config.PropertySourceBootstrapConfiguration,\
org.springframework.cloud.bootstrap.encrypt.EncryptionBootstrapConfiguration,\
org.springframework.cloud.autoconfigure.ConfigurationPropertiesRebinderAutoConfiguration,\
org.springframework.boot.autoconfigure.PropertyPlaceholderAutoConfiguration
```

警告 

> 小心，你添加的自定义`BootstrapConfiguration`类没有错误的`@ComponentScanned`到你的主应用上下文，他们可能是不需要的。使用一个另外的包不被`@ComponentScan`或者`@SpringBootApplication`注解覆盖到。



`bootstrap context`通过**`spring.factories`**配置的类初始化的所有的Bean都会在SpingApplicatin启动前加入到它的上下文里去。

### 自定义引导配置来源：Bootstrap Property Sources

默认的`property source`添加额外的配置是通过配置服务（Config Server），**你也可以自定义添加`property source`通过实现`PropertySourceLocator`接口来添加**。你可以使用它加配置属性从不同的服务、数据库、或者其他。

- 下面是一个自定义的例子:



```
@Configuration
public class CustomPropertySourceLocator implements PropertySourceLocator {

    @Override
    public PropertySource<?> locate(Environment environment) {
        return new MapPropertySource("customProperty",
                Collections.<String, Object>singletonMap("property.from.sample.custom.source", "worked as intended"));
    }
}
```



`Environment`被`ApplicationContext`建立，并传入`property sources`（可能不同个`profile`有不同的属性），所以，你可以从`Environment`寻找找一些特别的属性。比如`spring.application.name`，它是默认的`Config Server property source`。

如果你建立了一个jar包，里面添加了一个`META-INF/spring.factories`文件：

```java
org.springframework.cloud.bootstrap.BootstrapConfiguration=sample.custom.CustomPropertySourceLocator
```

那么，”customProperty“的`PropertySource`将会被包含到应用。



参考：

https://www.cnblogs.com/BlogNetSpace/p/8469033.html

