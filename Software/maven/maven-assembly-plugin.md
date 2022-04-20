

## 1. 简介

maven-assembly-plugin 就是用来帮助打包用的，比如说打出一个什么类型的包，包里包括哪些内容等等。

## 2. 常见的maven插件

常见的maven打包插件

| plugin                  | function                                       |
| ----------------------- | ---------------------------------------------- |
| `maven-jar-plugin`      | maven 默认打包插件，用来创建 project jar       |
| `maven-shade-plugin`    | 用来打可执行包，executable(fat) jar            |
| `maven-assembly-plugin` | 支持定制化打包方式，例如 apache 项目的打包方式 |



### maven-compiler-plugin

编译Java源码，一般只需设置编译的jdk版本

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.6.0</version>
    <configuration>
        <source>1.8</source>
        <target>1.8</target>
    </configuration>
</plugin>
```

### maven-jar-plugin

打成jar时，设定manifest的参数，比如指定运行的Main class，还有依赖的jar包，加入classpath中

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-jar-plugin</artifactId>
    <version>2.4</version>
    <configuration>
        <archive>
            <manifest>
                <addClasspath>true</addClasspath>
                <classpathPrefix>/data/lib</classpathPrefix>
                <mainClass>com.zhang.spring.App</mainClass>
            </manifest>
        </archive>
    </configuration>
</plugin>
```

### tomcat7-maven-plugin

用于远程部署Java Web项目

```xml
<plugin>
    <groupId>org.apache.tomcat.maven</groupId>
    <artifactId>tomcat7-maven-plugin</artifactId>
    <version>2.2</version>
    <configuration>
        <url>http://59.110.162.178:8080/manager/text</url>
        <username>linjinbin</username>
        <password>linjinbin</password>
    </configuration>
</plugin>
```



### maven-shade-plugin

用于把多个jar包，打成1个jar包
一般Java项目都会依赖其他第三方jar包，最终打包时，希望把其他jar包包含在一个jar包里。
与assembly类似，使用assembly即可。以下详解assembly。



## 3. maven-assembly-plugin

### 1.在pom中引入插件

1.首先我们需要在pom.xml中配置maven的assembly插件

```xml
 <build>
    <plugins>
        <plugin>       
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-assembly-plugin</artifactId>
            <executions>
              
              <execution><!-- 配置执行器 -->
              <id>make-assembly</id>
                <phase>package</phase><!-- 绑定到package生命周期阶段上 -->
                (1)
                <goals>
                  <goal>single</goal><!-- 只运行一次 -->   
                </goals>
                
                (2)
                <configuration>
                  <finalName>${project.name}</finalName>
                  <!--主类入口等-->
                  ...
                  
                  <descriptor>src/main/assembly/assembly.xml</descriptor><!--配置描述文件路径--> 
                </configuration>
              
              </execution>
            </executions>
          </plugin>
        </plugins>
     </build>
```

2.两个主要的参数设置（上述中的（1）（2））



#### I） Assembly插件的goals

- single
- help

可以执行如下命令完成动作：

```vbnet
mvn assembly:single
```

或者是，绑定到package生命周期阶段上（见上配置）触发。后续可以直接执行：

```actionscript
mvn package
```

这也是最常见的Assembly插件配置方式。

#### II）Assembly descriptor

Assembly Descriptor可以使用内置的，或者定制的。

##### (1) 使用内置的Assembly Descriptor

要使用maven-assembly-plugin，需要指定至少一个要使用的assembly descriptor 文件。默认情况下，maven-assembly-plugin内置了几个可以用的assembly descriptor：

- bin ： 类似于默认打包，会将bin目录下的文件打到包中；
- jar-with-dependencies ： 会将所有依赖都解压打包到生成物中；
- src ：只将源码目录下的文件打包；
- project ： 将整个project资源打包。

使用 descriptorRefs来引用(官方提供的定制化打包方式)【不建议使用】

```xml
<plugin>  
    <artifactId>maven-assembly-plugin</artifactId>  
    <configuration>  
    
        <descriptorRefs>  
            <descriptorRef>jar-with-dependencies</descriptorRef>  
        </descriptorRefs>  
    </configuration>  
</plugin>
```

上述直接配置`jar-with-dependencies`打包方式。不需要引入额外文件。实际上，上述4中预定义的assembly descriptor有对应的xml。要查看它们的详细定义，可以到maven-assembly-plugin.jar里去看，例如对应 bin 的assembly descriptor 原始文件如下：

```xml
<assembly xmlns="http://maven.apache.org/plugins/maven-assembly-plugin/assembly/1.1.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/plugins/maven-assembly-plugin/assembly/1.1.0 http://maven.apache.org/xsd/assembly-1.1.0.xsd">
    <id>bin</id>
    <formats>
        <format>tar.gz</format>
        <format>tar.bz2</format>
        <format>zip</format>
    </formats>
    <fileSets>
        <fileSet>
            <directory>${project.basedir}</directory>
            <outputDirectory>/</outputDirectory>
            <includes>
                <include>README*</include>
                <include>LICENSE*</include>
                <include>NOTICE*</include>
            </includes>
        </fileSet>
        <fileSet>
            <directory>${project.build.directory}</directory>
            <outputDirectory>/</outputDirectory>
            <includes>
                <include>*.jar</include>
            </includes>
        </fileSet>
        <fileSet>
            <directory>${project.build.directory}/site</directory>
            <outputDirectory>docs</outputDirectory>
        </fileSet>
    </fileSets>
</assembly>
```

##### (2) 自定义Assembly Descriptor

一般来说，内置的assembly descriptor都不满足需求，这个时候就需要写自己的assembly descriptor的实现了。
使用 `descriptors`，指定打包文件 `src/assembly/assembly.xml`，即在配置文件内指定打包操作要使用这个自定义assembly descriptor(自定义的xml中配置)，需要如下配置，即要引入描述文件：

```xml
<configuration>  
    <finalName>demo</finalName>  
    <descriptors>
        <!--描述文件路径-->
        <descriptor>src/assembly/assembly.xml</descriptor>  
    </descriptors>  
    <outputDirectory>output</outputDirectory>
</configuration> 
```

示例：
`src/assembly/assembly.xml`:

```xml
<?xml version='1.0' encoding='UTF-8'?>
<assembly xmlns="http://maven.apache.org/plugins/maven-assembly-plugin/assembly/1.1.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/plugins/maven-assembly-plugin/assembly/1.1.0  
                    http://maven.apache.org/xsd/assembly-1.1.0.xsd">
    <id>demo</id>
    
    <formats>
        <format>jar</format>
    </formats>
    
    <includeBaseDirectory>false</includeBaseDirectory>
    
    <fileSets>
        <fileSet>
        
            <directory>${project.build.directory}/classes</directory>
            
            <outputDirectory>/</outputDirectory>
            
        </fileSet>
    </fileSets>
</assembly>
```

这个定义很简单：

- format：指定打包类型；
- includeBaseDirectory：指定是否包含打包层目录（比如finalName是output，当值为true，所有文件被放在output目录下，否则直接放在包的根目录下）；
- fileSets：指定要包含的文件集，可以定义多个fileSet；
- directory：指定要包含的目录；
- outputDirectory：指定当前要包含的目录的目的地。

回到pom的配置中，自定义的configuration配置后，将会生成一个`demo-demo.jar` 文件在目录 `output` 下，其中前一个`demo`来自`finalName`，后一个`demo`来自assembly descriptor中的`id`，其中的内容和默认的打包出来的jar类似。

如果只想有finalName，则增加配置：

```xml
<appendAssemblyId>false</appendAssemblyId>  
```

对于描述文件的元素，即assembly.xml中的配置节点的详细配置，在此稍作总结，见下。

## assembly.xml节点配置

在配置assembly.xml之前，我们先看一下pom中引入插件的结构。

```xml
<project>
  [...]
  <build>
    [...]
    <plugins>
      <plugin>
 ------------（1）坐标-----------
        <artifactId>maven-assembly-plugin</artifactId>
        <version>3.0.0</version>
        <configuration>
 ------------（2）入口-----------
          <archive>
            <manifest>
                <addClasspath>true</addClasspath>
                <mainClass>com.***.startup.BootStrap</mainClass> <!-- 你的主类名 -->
            </manifest>
          </archive>
 ------------（3）描述-----------  
          <descriptors>
            <descriptor>src/assembly/assembly.xml</descriptor>
          </descriptors>
        </configuration>
        [...]
</project>
```

**archive说明**
上面的`mainClass`标签中的内容替换成自己的`main`函数所在的类，前面要包含`package`名字，也就是`package_name.MainClassName`。

assembly.xml文件的主要结构如下。

#### id

```applescript
<id>distribution</id>
```

id 标识符，添加到生成文件名称的后缀符。如果指定 id 的话，目标文件则是 `${artifactId}-${id}.tar.gz`

#### formats

maven-assembly-plugin 支持的打包格式有zip、tar、tar.gz (or tgz)、tar.bz2 (or tbz2)、jar、dir、war，可以同时指定多个打包格式

```xml
<formats>
    <format>dir</format>
  </formats>
```

#### dependencySets

用来定制工程依赖 jar 包的打包方式，核心元素如下表所示。

| 元素            | 类型         | 作用                                 |
| --------------- | ------------ | ------------------------------------ |
| outputDirectory | String       | 指定包依赖目录，该目录是相对于根目录 |
| includes        | List<String> | 包含依赖                             |
| excludes        | List<String> | 排除依赖                             |

```xml
    <dependencySets>
        <dependencySet>
            <outputDirectory>/lib</outputDirectory>
            <excludes>
                <exclude>${project.groupId}:${project.artifactId}</exclude>
            </excludes>
        </dependencySet>
        <dependencySet>
            <outputDirectory>/</outputDirectory>
            <includes>
                <include>${project.groupId}:${project.artifactId}</include>
            </includes>
        </dependencySet>
    </dependencySets>
```

#### fileSets

管理一组文件的存放位置，核心元素如下表所示。

| 元素            | 类型         | 作用                                                         |
| --------------- | ------------ | ------------------------------------------------------------ |
| outputDirectory | String       | 指定文件集合的输出目录，该目录是相对于根目录                 |
| includes        | List<String> | 包含文件                                                     |
| excludes        | List<String> | 排除文件                                                     |
| fileMode        | String       | 指定文件属性，使用八进制表达，分别为(User)(Group)(Other)所属属性，默认为 0644 |

```xml
    <fileSets>
        <fileSet>
            <directory>shell</directory>
            <outputDirectory>/shell</outputDirectory>
        </fileSet>
        <fileSet>
            <directory>cluster_config</directory>
            <outputDirectory>/cluster_config</outputDirectory>
        </fileSet>
    </fileSets>
```

#### 其他

其他的，如files节点基本类似fileSets。不常用的暂不介绍。

至此，即可按打包成功。下边给出具体示例。

## 参考文献

[maven--插件篇（assembly插件）](https://segmentfault.com/a/1190000016237395)

