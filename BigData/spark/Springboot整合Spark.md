
--common-dm 依赖版本管理
--common-de 依赖输出
--demo springboot整个spark demo

## 1.common-dm依赖管理
在pom.xml中进行版本控制

```xml
    <groupId>xxx</groupId>
    <artifactId>common-dm</artifactId>
    <version>1.0.0</version>
    <packing>pom</packing>
```
## 2.common-de 依赖
在pom.xml中配置相关,则对应的依赖包会输出到target/lib下
```xml
   <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>xxx</groupId>
                <artifactId>common-dm</artifactId>
                <version>1.0.0</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-dependency-plugin</artifactId>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>copy-dependencies</goal>
                        </goals>
                        <configuration>
                            <prependGroupId>true</prependGroupId>
                            <outputDirectory>${project.build.directory}/lib</outputDirectory>
                            <includeScope>runtime</includeScope>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <configuration>
                    <archive>
                        <manifest>
                            <addClasspath>true</addClasspath>
                            <classpathLayoutType>custom</classpathLayoutType>
                            <customClasspathLayout>$${artifact.groupId}@$${artifact.artifactId}@$${artifact.version}
                            </customClasspathLayout>
                        </manifest>
                    </archive>
                </configuration>
            </plugin>
        </plugins>
    </build>
```

## springboot整合spark

### yarn集群模式
on yarn集群上，所以只能用spark-submit去启动,由Spark-Yarn-Client托管应用的jar，否则，应用的jar中缺少向Yarn资源管理器申请资源的模块，无法正常启动

```shell
./spark-submit 
--conf spark.yarn.jars="/xxxx/spark/jars/*,hdfs://ns1:8020/xxxxx/lib/*" 
--driver-java-options "-Dorg.springframework.boot.logging.LoggingSystem=none -Dspring.profiles.active=test -Dspark.yarn.dist.files=/yarn-site.xml"
--master yarn
--deploy-mode client  
--executor-cores 3 
--num-executors 80 
--executor-memory 12g 
--driver-memory 12g 
--name xxxxx_analysis 
--queue xxxxxxx 
--class org.springframework.boot.loader.JarLauncher 
./com.xxx-1.0-SNAPSHOT.jar 
--principal xxxx 
--keytab xxx.keytab >> xxx.log 2>&1 &
```

#### 存在问题
1.日志冲突
spark-submit内部使用log4j作为日志模块，springboot采用的是logbak作为日志模块


2.Gson版本冲突
Spark自带的GSON版本可能与SpringBoot依赖的版本冲突


3.guava和validation-api包 冲突

在springboot中直接把所有涉及到的东西都exclusion掉，其中涉及到的有spark和javaee-api


4.序列化和反序列问题
java使用JavaSerializer序列化方式，spark使用kyro方式
解决方案：把springboot打包后的target文件夹下面的xxx.jar.original这个文件，重命名为xxx.jar，这个就是我们自己开发程序的jar包，将这个jar上传到我们提前指定的hdfs的项目依赖的路径下即可
