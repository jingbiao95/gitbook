



# 使用IDEA自带的代码覆盖率工具

1.查看配置（因为都是默认的，所以不用修改）

点击Edit Configurations



![image-20210513112610014](https://raw.githubusercontent.com/jingbiao95/Images/main/typora202105/13/112634-839353.png)



1.pom文件增加

```xml
<dependency>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.2</version>
</dependency>

<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
        <plugin>
            <groupId>org.jacoco</groupId>
            <artifactId>jacoco-maven-plugin</artifactId>
            <version>0.8.2</version>
            <configuration>
                <destFile>target/coverage-reports/jacoco-unit.exec</destFile>
                <dataFile>target/coverage-reports/jacoco-unit.exec</dataFile>
            </configuration>
            <executions>
                <execution>
                    <id>jacoco-initialize</id>
                    <goals>
                        <goal>prepare-agent</goal>
                    </goals>
                </execution>
                <!--这个report:对代码进行检测，然后生成index.html在 target/site/index.html中可以查看检测的详细结果-->
                <execution>
                    <id>jacoco-site</id>
                    <phase>package</phase>
                    <!--<phase>test</phase>写上test的时候会自动出现site文件夹，而不需执行下面的jacoco:report步骤，推荐-->
                    <goals>
                    	<goal>report</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

刷新maven，会发现多了一个插件

![image-20210513112910584](C:\Users\jingbiao9502\AppData\Roaming\Typora\typora-user-images\image-20210513112910584.png)

2.运行测试用例

在插件中选择test或者是命令行运行mvn test

成功后target文件夹中会出现文件夹



**点击右侧jacoco插件**`jacoco:report`



成功后在`target/site/jacoco/index.html`查看



#  JaCoCo概念

### 覆盖率计数器 - Coverage Counters

JaCoCo使用一组不同的计数器来计算覆盖率指标。所有这些计数器都从Java类文件中包含的信息派生而来，这些信息基本上是Java字节码指令以及调试信息（可选地嵌入在类文件中）。即使没有可用的源代码，这种方法也可以对应用程序进行高效的即时检测和分析(instrumentation and analysis)。在大多数情况下，可以将收集到的信息映射回源代码，并可视化到行级粒度。无论如何，这种方法存在局限性。必须使用调试信息编译类文件，以计算行级覆盖率并提供源高亮显示。并非所有Java语言构造都可以直接编译为相应的字节码。在这种情况下，Java编译器会创建所谓的合成代码，有时会导致意外的代码覆盖率结果。

### 指令 - Instructions（C0覆盖率）

JaCoCo计数的最小单位是单个Java字节代码指令。指令覆盖率提供有关已执行或遗漏(executed or missed)的代码量的信息。**该度量完全独立于源格式，并且即使在类文件中没有调试信息的情况下也始终可用。**

### 分支 - Branches（C1覆盖率）

JaCoCo还为所有**if和switch语句**计算分支覆盖率。此度量标准统计方法中此类分支的总数，并确定已执行或遗漏的分支的数量。**分支覆盖始终可用，即使类文件中没有调试信息也是如此**。请注意，在此计数器定义的上下文中，异常处理不视为分支。

如果尚未使用调试信息编译类文件，则可以将决策点映射到源代码行并高亮:

- 无覆盖范围：该行没有分支执行（红色菱形）
- 部分覆盖：仅执行了该行中的一部分分支（黄色菱形）
- 全面覆盖：该行中的所有分支均已执行（绿色菱形）

### 圈复杂度 - Cyclomatic Complexity

JaCoCo 还为每种非抽象方法计算圈复杂度，并汇总了类，包和组的复杂度。根据 McCabe1996 的定义，圈复杂度是可以（线性）组合生成一种方法的所有可能路径的最小路径数。因此，复杂度值可以作为完全覆盖某个软件的单元测试用例数量的指示。即使类文件中没有调试信息，也总是可以计算复杂度数字。

圈复杂度v（G）的形式定义基于方法的控制流图作为有向图的表示：

```javascript
v（G）= E- N 2
```

其中，E是边数，N是节点数。 JaCoCo根据分支数（B）和决策点数（D）使用以下等效方程式计算方法的圈复杂度：

```javascript
v（G）= B - D + 1
```

根据每个分支的覆盖状态，JaCoCo还可以计算每种方法的覆盖和遗漏复杂度。缺少的复杂性再次表明完全覆盖模块的测试用例的数量。请注意，由于JaCoCo不考虑异常处理，因为分支try / catch块也不会增加复杂性。

### 行

对于**已使用调试信息编译的所有类文件**，可以计算各个行的覆盖率信息。当已执行至少一个分配给该源代码行的指令时，该源代码行被视为已执行。

由于单行通常会编译为多字节代码指令，因此，源代码高亮显示每行包含源代码的三种不同状态：

- 无覆盖：该行中没有指令被执行（红色背景）
- 部分覆盖：仅执行了该行中的一部分指令（黄色背景）
- 全面覆盖：该行中的所有指令均已执行（绿色背景）

根据源格式，源代码的一行可能会引用多个方法或多个类。因此，不能简单地添加方法的行数以获得包含类的总数。单个源文件中的多个类的行也是如此。 JaCoCo根据覆盖的实际源代码行计算类和源文件的代码行覆盖率。

方法 每个非抽象方法都包含至少一条指令。当至少一个指令已被执行时，一种方法被视为已执行。由于JaCoCo在字节码级别上工作，因此构造函数和静态初始化程序也被视为方法。这些方法中的某些方法在Java源代码中可能没有直接的对应关系，例如隐式生成的常量的默认构造函数或初始化器。

### 类

当至少一个类的方法已执行时，该类被视为已执行。 请注意，JaCoCo将构造函数以及静态初始化程序视为方法。 由于Java接口类型可能包含静态初始化器，因此此类接口也被视为可执行类。

# Reference 

https://www.jacoco.org/jacoco/trunk/doc/

https://www.geek-share.com/detail/2790027471.html