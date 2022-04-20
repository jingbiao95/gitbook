[toc]



---



# 1.分类

在Maven的pom.xml文件中，存在如下两种<build>：

（1）全局配置（project build）

​     针对整个项目的所有情况都有效

（2）配置（profile build）

​      针对不同的profile配置

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"  
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"  
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">  
  ...  
  <!-- "Project Build" contains elements of the BaseBuild set and the Build set-->  
  <build>...</build>  
   
  <profiles>  
    <profile>  
      <!-- "Profile Build" contains elements of the BaseBuild set only -->  
      <build>...</build>  
    </profile>  
  </profiles>  
</project>
```



说明：

一种<build>被称为Project Build，即是<project>的**直接子元素**。

另一种<build>被称为Profile Build，即是<profile>的直接子元素。

Profile Build包含了基本的build元素，而Project Build还包含两个特殊的元素，即各种<...Directory>和<extensions>。

# 2. 配置说明

## 1.基本元素

示例如下

```xml
<build>  
  <defaultGoal>install</defaultGoal>  
  <directory>${basedir}/target</directory>  
  <finalName>${artifactId}-${version}</finalName>   
  <filters>
      <filter>filters/filter1.properties</filter>
  </filters>   
    ...
</build>
```



 1）defaultGoal

​          执行build任务时，如果没有指定目标，将使用的默认值。

​          如上配置：在命令行中执行mvn，则相当于执行mvn install



 2）directory
           build目标文件的存放目录，默认在${basedir}/target目录

 3）finalName

​           build目标文件的名称，默认情况为${artifactId}-${version}

  4）filter

​           定义*.properties文件，包含一个properties列表，该列表会应用到支持filter的resources中。

​           **也就是说，定义在filter的文件中的name=value键值对，会在build时代替${name}值应用到resources中。**

​           maven的默认filter文件夹为${basedir}/src/main/filters

## **2. Resources配置**

​         用于包含或者排除某些资源文件

```xml
<build>  
        ...  
       <resources>  
          <resource>  
             <targetPath>META-INF/plexus</targetPath>  
             <filtering>true</filtering>  
            <directory>${basedir}/src/main/plexus</directory>  
            <includes>  
                <include>configuration.xml</include>  
            </includes>  
            <excludes>  
                <exclude>**/*.properties</exclude>  
            </excludes>  
         </resource>  
    </resources>  
    <testResources>  
        ...  
    </testResources>  
    ...  
</build>
```



1）resources

​          一个resources元素的列表。每一个都描述与项目关联的文件是什么和在哪里

 2）targetPath

​          指定build后的resource存放的文件夹，默认是basedir。

​          通常被打包在jar中的resources的目标路径是META-INF

​       3）filtering

​          true/false，表示为这个resource，filter是否激活
​       4）directory

​          定义resource文件所在的文件夹，默认为${basedir}/src/main/resources

​       5）includes

​          指定哪些文件将被匹配，以*作为通配符

​       6）excludes

​          指定哪些文件将被忽略

​       7）testResources

​          定义和resource类似，只不过在test时使用

## **3 plugins配置**

​         用于指定使用的插件

```xml
<build>  
    ...  
    <plugins>  
        <plugin>  
            <groupId>org.apache.maven.plugins</groupId>  
            <artifactId>maven-jar-plugin</artifactId>  
            <version>2.0</version>  
            <extensions>false</extensions>  
            <inherited>true</inherited>  
            <configuration>  
                <classifier>test</classifier>  
            </configuration>  
            <dependencies>...</dependencies>  
            <executions>...</executions>  
        </plugin>  
    </plugins>  
</build>
```



##  **4 pluginManagement配置**

​     pluginManagement的配置和plugins的配置是一样的，只是用于继承，使得可以在孩子pom中使用。

​    父pom：

```xml
<build>  
    ...  
    <pluginManagement>  
        <plugins>  
            <plugin>  
              <groupId>org.apache.maven.plugins</groupId>  
              <artifactId>maven-jar-plugin</artifactId>  
              <version>2.2</version>  
                <executions>  
                    <execution>  
                        <id>pre-process-classes</id>  
                        <phase>compile</phase>  
                        <goals>  
                            <goal>jar</goal>  
                        </goals>  
                        <configuration>  
                            <classifier>pre-process</classifier>  
                        </configuration>  
                    </execution>  
                </executions>  
            </plugin>  
        </plugins>  
    </pluginManagement>  
    ...  
</build>
```



 则在子pom中，我们只需要配置：

```xml
<build>  
    ...  
    <plugins>  
        <plugin>  
            <groupId>org.apache.maven.plugins</groupId>  
            <artifactId>maven-jar-plugin</artifactId>  
        </plugin>  
    </plugins>  
    ...  
</build>
```

 这样大大简化了孩子pom的配置

# 参考

https://www.cnblogs.com/whx7762/p/7911890.html