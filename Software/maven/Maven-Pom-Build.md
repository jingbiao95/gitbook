
## resources 标签
功能：主要用于打包资源文件，默认情况下maven只打包src/main/resource下的资源，通过：  
1、设置build_resources  
2、使用build-helper-maven-plugin插件  
3、使用maven-resources-plugin插件  
都可以自定义要打包的资源  

一般情况下，我们用到的资源文件（各种xml，properties，xsd文件）都放在src/main/resources下面，利用maven打包时，maven能把这些资源文件打包到相应的jar或者war里。

有时候，比如mybatis的mapper.xml文件，我们习惯把它和Mapper.java放在一起，都在src/main/java下面，这样利用maven打包时，就需要修改pom.xml文件，来吧mapper.xml文件一起打包进jar或者war里了，否则，这些文件不会被打包的。（maven认为src/main/java只是java的源代码路径）。

方法1，其中*/这样的写法，是为了保证各级子目录下的资源文件被打包。

```xml
<build>  
    <finalName>test</finalName>  
    <!--  
    这样也可以把所有的xml文件，打包到相应位置。  
    <resources>  
        <resource>  
            <directory>src/main/resources</directory>  
            <includes>  
                <include>**/*.properties</include>  
                <include>**/*.xml</include>  
                <include>**/*.tld</include>  
            </includes>  
            <filtering>false</filtering>  
        </resource>  
        <resource>  
            <directory>src/main/java</directory>  
            <includes>  
                <include>**/*.properties</include>  
                <include>**/*.xml</include>  
                <include>**/*.tld</include>  
            </includes>  
            <filtering>false</filtering>  
        </resource>  
    </resources>  
</build>
```

方法2，利用build-helper-maven-plugin插件

```xml
<build>  
  
    </plugins>  
        ...  
        <!--  
        此plugin可以用  
        利用此plugin，把源代码中的xml文件，  
        打包到相应位置，这里主要是为了打包Mybatis的mapper.xml文件   
        -->  
        <plugin>  
            <groupId>org.codehaus.mojo</groupId>  
            <artifactId>build-helper-maven-plugin</artifactId>  
            <version>1.8</version>  
            <executions>  
                <execution>  
                    <id>add-resource</id>  
                    <phase>generate-resources</phase>  
                    <goals>  
                        <goal>add-resource</goal>  
                    </goals>  
                    <configuration>  
                        <resources>  
                            <resource>  
                                <directory>src/main/java</directory>  
                                <includes>  
                                    <include>**/*.xml</include>  
                                </includes>  
                            </resource>  
                        </resources>  
                    </configuration>  
                </execution>  
            </executions>  
        </plugin>     
        ...  
    </plugins>       
    ...  
</build>
```

方法3，利用maven-resources-plugins插件

```xml
<build>  
    ...  
    </plugins>  
        ...  
        <!--  
        此plugin可以用  
        利用此plugin，把源代码中的xml文件，打包到相应位置，  
        这里主要是为了打包Mybatis的mapper.xml文件   
        -->  
        <plugin>  
            <artifactId>maven-resources-plugin</artifactId>  
            <version>2.5</version>  
            <executions>  
                <execution>  
                    <id>copy-xmls</id>  
                    <phase>process-sources</phase>  
                    <goals>  
                        <goal>copy-resources</goal>  
                    </goals>  
                    <configuration>  
                        <outputDirectory>${basedir}/target/classes</outputDirectory>  
                        <resources>  
                            <resource>  
                                <directory>${basedir}/src/main/java</directory>  
                                <includes>  
                                    <include>**/*.xml</include>  
                                </includes>  
                            </resource>  
                        </resources>  
                    </configuration>  
                </execution>  
            </executions>  
        </plugin>     
        ...  
    </plugins>       
    ...  
</build>

```

以下是对maven 插件的说明：

resources:描述工程中资源的位置

```xml
<resource> 
    <targetPath>META-INF/plexus</targetPath> 
    <filtering>false</filtering> 
    <directory>${basedir}/src/main/plexus</directory> 
    <includes> 
        <include>configuration.xml</include> 
    </includes> 
    <excludes> 
        <exclude>**/*.properties</exclude> 
    </excludes> 
</resource>
```

targetPath:指定build资源到哪个目录，默认是base directory

filtering:指定是否将filter文件(即build下的filters里定义的*.property文件)的变量值在这个resource文件有效,例如上面就指定那些变量值在configuration文件无效。

directory:指定属性文件的目录，build的过程需要找到它，并且将其放到targetPath下，默认的directory是${basedir}/src/main/resources

includes:指定包含文件的patterns,符合样式并且在directory目录下的文件将会包含进project的资源文件。

excludes:指定不包含在内的patterns,如果inclues与excludes有冲突，那么excludes胜利，那些符合冲突的样式的文件是不会包含进来的。

testResources:这个模块包含测试资源元素，其内容定义与resources类似，不同的一点是默认的测试资源路径是${basedir}/src/test/resources,测试资源是不部署的。

默认情况下，如果没有指定resources，目前认为自动会将classpath下的src/main/java下的.class文件和src/main/resources下的.xml文件放到target里头的classes文件夹下的package下的文件夹里。如果设定了resources，那么默认的就会失效，就会以指定的includes和excludes为准。例如，为了使打包的jar包里头包含.java源文件。