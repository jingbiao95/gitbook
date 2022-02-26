[toc]



-----

#  modelVersion

modelVersion：声明项目描述符遵循哪一个POM模型版本。
模型本身的版本很少改变，虽然如此，但它仍然是必不可少的.
这是为了当Maven引入了新的特性或者其他模型变更的时候，确保稳定性。

# parent

parent：引入父级pom文件。

# groupId

groupId：公司名称、组织名称、项目开发者，配置时生成路径也是由此生成（包名，如com.XXX）。

# artifactId

artifactId：项目通用名称。

# version

version：对应项目版本号。

# packaging

packaging：打包后的类型。如war、jar、maven-plugin、ejb、pom、ear、par、rar

# name

name:用户描述项目的名称，可选。

# description

description 描述项目  可选

# url

url:项目主页的URL, Maven产生文档时用。



-----------



# exclusions

exclusions：排除管理（写在dependency中）。

## exclusion

exclusion：具体要排除的依赖项。

# repositories

repositories:仓库管理。

## repository

repository：具体仓库（有id、name、url子元素）。

# properties

properties：自定义标签管理（可在其内自定义标签名、值，
用法同于el表达式：${标签名}得到其值），常用于集中定义依赖版本号。

# scope

scope：管理部署（可以使用5个值)

    * compile，缺省值，适用于所有阶段，会随着项目一起发布。
    * provided，类似compile，期望JDK、容器或使用者会提供这个依赖。如servlet.jar。
    * runtime，只在运行时使用，如JDBC驱动，适用运行和测试阶段。
    * test，只在测试时使用，用于编译和运行测试代码。不会随项目发布。
    * system，类似provided，需要显式提供包含依赖的jar，Maven不会在Repository中查找它。

# dependencies

dependencies:依赖,jar包管理。



项目会自动引入声明在dependencies里的所有依赖，并默认被所有的子项目继承。

如果项目中不写依赖项，则会从父项目继承（属性全部继承）声明在父项目dependencies里的依赖项。

## dependency 

dependency：具体的依赖项。

# dependencyManagement

dependencyManagement：依赖,jar包管理。

只是声明依赖，并不实现引入，因此子项目需要显示的声明需要的依赖。

如果不在子项目中声明依赖，是不会从父项目中继承的；

只有在子项目中写了该依赖项，并且没有指定具体版本，才会从父项目中继承该项，并且version和scope都读取自父pom;

如果子项目中指定了版本号，那么会使用子项目中指定的jar版本。

同时dependencyManagement让子项目引用依赖，而不用显示的列出版本号。

Maven会沿着父子层次向上走，直到找到一个拥有dependencyManagement元素的项目，然后它就会使用在这个dependencyManagement元素中指定的版本号,实现所有子项目使用的依赖项为同一版本。

#  build

build：全局配置（project build），针对当前项目的所有情况都有效。
自定义配置（profile build）针对不同的profile配置。

## defaultGoal

defaultGoal：执行build任务时，如果没有指定目标，将使用的默认值。

##  finalName

finalName：build目标文件的名称，默认情况为${artifactId}-${version}。

## include

include：指定哪些文件将被匹配，以*作为通配符。

## excludes

excludes：指定哪些文件将被忽略。

## resources

resources：用于包含或者排除某些资源文件。

## testResources

testResources：定义和resource类似，只不过在test时使用。

## directory

directory：build目标文件的存放目录，就是定义resource文件所在的文件夹，默认在${basedir}/target目录。

## plugins

plugins：指定使用的插件。

#### configuration

configuration：配置该plugin期望得到的properties。



```xml
<build>
    <>
    <plugins>
        <plugin>
            <artifactId>maven-pmd-plugin</artifactId>
            <configuration>
                <rulesets>
                    <ruleset>itzcpmd.xml</ruleset>
                </rulesets>
                <targetJdk>1.8</targetJdk>
                <excludes>
                    <exclude>**/src/test/*.java</exclude>
                </excludes>
                <includeTests>false</includeTests>
                <sourceEncoding>utf-8</sourceEncoding>
                <aggregate>true</aggregate>
            </configuration>
            <executions>
                <execution>
                    <phase>test</phase>
                    <goals>
                        <goal>pmd</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```



# modules

modules：一个项目有多个平级模块，也叫做多重模块，或者合成项目，modules实现平级模块管理。

## module

module：具体模块名称（标明该模块和artifactId标签中模块平级），是project标签的子元素。

# profiles

profiles：自定义配置信息管理。

## profile

profile：具体自定义配置（可以在不同环境下使用不同的配制文件）。

### activation

activation：profile 的子元素，指该配置的激活条件。Activation 是 profile 的开启钥匙，但不是激活profile的唯一方式。

### jdk

jdk：当匹配的jdk被检测到，profile被激活。

### os

os：用法同于jdk，当匹配的操作系统属性被检测到，profile 被激活。



