**目的：**

Java如何根据字符串模板在运行时动态生成对象。

Java是一门静态语言，通常，我们需要的class在编译的时候就已经生成了，为什么有时候我们还想在运行时动态生成class呢？



## 一、利用JDK自带工具类实现

1. 如果我们要自己直接输出二进制格式的字节码，在完成这个任务前，必须先认真阅读[JVM规范第4章](http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html)，详细了解class文件结构。
2. 使用已有的一些能操作字节码的库，帮助我们创建class。

目前，能够操作字节码的开源库主要有[CGLib](https://github.com/cglib/cglib)和[Javassist](http://jboss-javassist.github.io/javassist/)两种，它们都提供了比较高级的API来操作字节码，最后输出为class文件。



CGLib，典型的用法如下：

```java
Enhancer e = new Enhancer();
e.setSuperclass(...);
e.setStrategy(new DefaultGeneratorStrategy() {
    protected ClassGenerator transform(ClassGenerator cg) {
        return new TransformingGenerator(cg,
            new AddPropertyTransformer(new String[]{ "foo" },
                    new Class[] { Integer.TYPE }));
    }});
Object obj = e.create();
```

学会它的API还是得花大量的时间，不建议





Java的编译器是`javac`，但是，在很早很早的时候，Java的编译器就已经用纯Java重写了，自己能编译自己，行业黑话叫“自举”。从Java 1.6开始，编译器接口正式放到JDK的公开API中，于是，我们不需要创建新的进程来调用`javac`，而是直接使用编译器API来编译源码。

**使用文件**

```java
JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
int compilationResult = compiler.run(null, null, null, '/path/Test.java');
```

使用方式：内存中的Java代码 --> 文件 -- > 编译  --> 读取class文件内容(ClassLoader加载)



**使用字符串**

Java编译器根本不关心源码的内容是从哪来：`String`当作源码，它就可以输出`byte[]`作为class的内容

```java
Map<String, byte[]> results;
JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
StandardJavaFileManager stdManager = compiler.getStandardFileManager(null, null, null);
try (MemoryJavaFileManager manager = new MemoryJavaFileManager(stdManager)) {
    JavaFileObject javaFileObject = manager.makeStringSource(fileName, source);
    CompilationTask task = compiler.getTask(null, manager, null, null, null, Arrays.asList(javaFileObject));
    if (task.call()) {
        results = manager.getClassBytes();
    }
}
```

上述代码的几个关键在于：

1. 用`MemoryJavaFileManager`替换JDK默认的`StandardJavaFileManager`，以便在编译器请求源码内容时，不是从文件读取，而是直接返回`String`；
2. 用`MemoryOutputJavaFileObject`替换JDK默认的`SimpleJavaFileObject`，以便在接收到编译器生成的`byte[]`内容时，不写入class文件，而是直接保存在内存中。

最后，编译的结果放在`Map<String, byte[]>`中，Key是类名，对应的`byte[]`是class的二进制内容。

- `byte []`是因为一个`.java`的源文件编译后可能有多个`.class`文件！只要包含了静态类、匿名类等，编译出的class肯定多于一个。

### 编译Java类文件



### 加载编译后的class

只需要创建一个`ClassLoader`，覆写`findClass()`方法

```java
class MemoryClassLoader extends URLClassLoader {

    Map<String, byte[]> classBytes = new HashMap<String, byte[]>();

    public MemoryClassLoader(Map<String, byte[]> classBytes) {
        super(new URL[0], MemoryClassLoader.class.getClassLoader());
        this.classBytes.putAll(classBytes);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] buf = classBytes.get(name);
        if (buf == null) {
            return super.findClass(name);
        }
        classBytes.remove(name);
        return defineClass(name, buf, 0, buf.length);
    }
}
```



## 二、利用三方Jar包实现

利用三方包com.itranswarp.compiler来实现：

1. 引入Maven依赖包：

```xml
<dependency>
    <groupId>com.itranswarp</groupId>
    <artifactId>compiler</artifactId>
    <version>1.0</version>
</dependency>
```

2. 编写工具类

```java
public class StringCompiler {
    public static Object run(String source, String...args) throws Exception {
        // 声明类名
        String className = "Main";
        String packageName = "top.fomeiherz";
        // 声明包名：package top.fomeiherz;
        String prefix = String.format("package %s;", packageName);
        // 全类名：top.fomeiherz.Main
        String fullName = String.format("%s.%s", packageName, className);
        
        // 编译器
        JavaStringCompiler compiler = new JavaStringCompiler();
        // 编译：compiler.compile("Main.java", source)
        Map<String, byte[]> results = compiler.compile(className + ".java", prefix + source);
        // 加载内存中byte到Class<?>对象
        Class<?> clazz = compiler.loadClass(fullName, results);
        // 创建实例
        Object instance = clazz.newInstance();
        Method mainMethod = clazz.getMethod("main", String[].class);
        // String[]数组时必须使用Object[]封装
        // 否则会报错：java.lang.IllegalArgumentException: wrong number of arguments
        return mainMethod.invoke(instance, new Object[]{args});
    }
}
```

3. 测试执行

```java
public class StringCompilerTest {
    public static void main(String[] args) throws Exception {
        // 传入String类型的代码
        String source = "import java.util.Arrays;public class Main" +
                "{" +
                "public static void main(String[] args) {" +
                "System.out.println(Arrays.toString(args));" +
                "}" +
                "}";
        StringCompiler.run(source, "1", "2");
    }
}
```

## 三、利用Groovy脚本实现

Groovy原生就支持脚本动态生成对象

\1. 引入Groovy maven依赖

```xml
<dependency>
    <groupId>org.codehaus.groovy</groupId>
    <artifactId>groovy-all</artifactId>
    <version>2.4.13</version>
  </dependency>
```

\2. 直接上测试代码

```java
	@Test
    public void testGroovyClasses() throws Exception {
        //groovy提供了一种将字符串文本代码直接转换成Java Class对象的功能
        GroovyClassLoader groovyClassLoader = new GroovyClassLoader();
        //里面的文本是Java代码,但是我们可以看到这是一个字符串我们可以直接生成对应的Class<?>对象,而不需要我们写一个.java文件
        Class<?> clazz = groovyClassLoader.parseClass("package com.xxl.job.core.glue;\n" +
                "\n" +
                "public class Main {\n" +
                "\n" +
                "    public int age = 22;\n" +
                "    \n" +
                "    public void sayHello() {\n" +
                "        System.out.println(\"年龄是:\" + age);\n" +
                "    }\n" +
                "}\n");
        Object obj = clazz.newInstance();
        Method method = clazz.getDeclaredMethod("sayHello");
        method.invoke(obj);

        Object val = method.getDefaultValue();
        System.out.println(val);
    }
```



## 四、利用Scala脚本实现

\1. 定义工具类

```scala
package com.damll.rta.flink.utils
 
import java.lang.reflect.Method
import java.util
import java.util.UUID
import scala.reflect.runtime.universe
import scala.tools.reflect.ToolBox
 
case class ClassInfo(clazz: Class[_], instance: Any, defaultMethod: Method, methods: Map[String, Method]) {
  def invoke[T](args: Object*): T = {
    defaultMethod.invoke(instance, args: _*).asInstanceOf[T]
  }
}
 
object ClassCreateUtils {
  private val clazzs = new util.HashMap[String, ClassInfo]()
  private val classLoader = scala.reflect.runtime.universe.getClass.getClassLoader
  private val toolBox = universe.runtimeMirror(classLoader).mkToolBox()
 
  def apply(classNameStr: String, func: String): ClassInfo = this.synchronized {
    var clazz = clazzs.get(func)
    if (clazz == null) {
      val (className, classBody) = wrapClass(classNameStr, func)
      val zz = compile(prepareScala(className, classBody))
      val defaultMethod = zz.getDeclaredMethods.head
      val methods = zz.getDeclaredMethods
      clazz = ClassInfo(
        zz,
        zz.newInstance(),
        defaultMethod,
        methods = methods.map { m => (m.getName, m) }.toMap
      )
      clazzs.put(func, clazz)
    }
    clazz
  }
 
  def compile(src: String): Class[_] = {
    val tree = toolBox.parse(src)
    toolBox.compile(tree).apply().asInstanceOf[Class[_]]
  }
 
  def prepareScala(className: String, classBody: String): String = {
    classBody + "\n" + s"scala.reflect.classTag[$className].runtimeClass"
  }
 
  def wrapClass(className:String, function: String): (String, String) = {
    //val className = s"dynamic_class_${UUID.randomUUID().toString.replaceAll("-", "")}"
    val classBody =
      s"""
         |class $className extends Serializable{
         |  $function
         |}
            """.stripMargin
    (className, classBody)
  }
}
```

\2. 调用动态加载类

```scala
object CreateTest {
  def main(args: Array[String]): Unit = {
    val cim = ClassCreateUtils("Calculator", "def toUps(str:String):String = str.toUpperCase")
    val value = cim.methods("toUps").invoke(cim.instance, "hello")
    println(value) // method1
    println(cim.invoke("World")) // method2
  }
}
```

\3. 运行结果

```
HELLO
WORLD
```

## 五、利用Aviator脚本实现

\1. 引入jar

```xml
 <dependency>
     <groupId>com.googlecode.aviator</groupId>
     <artifactId>aviator</artifactId>
     <version>4.2.10</version>
</dependency>
```

\2. 编写代码

```java
@Test
    public void testAviatorClasses() throws Exception {
        final ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");

        // AviatorScript code in a String. This code defines a script object 'obj'
        // with one method called 'hello'.
        String script =
                "var obj = new Object(); obj.hello = function(name) { print('Hello, ' + name); }";
        // evaluate script
        engine.eval(script);

        // javax.script.Invocable is an optional interface.
        // Check whether your script engine implements or not!
        // Note that the AviatorScript engine implements Invocable interface.
        Invocable inv = (Invocable) engine;

        // get script object on which we want to call the method
        Object obj = engine.get("obj");

        // invoke the method named "hello" on the script object "obj"
        inv.invokeMethod(obj, "hello", "Script Method !!");
    }
```

https://docs.oracle.com/javase/7/docs/technotes/guides/scripting/programmer_guide/#helloworld

## 参考文献

[运行时动态生成类](https://www.heshengbang.tech/2018/07/%E7%AC%AC24%E8%AE%B2-%E8%BF%90%E8%A1%8C%E6%97%B6%E5%8A%A8%E6%80%81%E7%94%9F%E6%88%90%E7%B1%BB/)

[Java运行时动态生成class的方法](https://www.liaoxuefeng.com/article/1080190250181920)

[[Java运行时动态生成类几种方式](https://www.cnblogs.com/barrywxx/p/13233373.html)]()

