# **一、生命周期流程图：**

　　Spring Bean的完整生命周期从创建Spring容器开始，直到最终Spring容器销毁Bean，这其中包含了一系列关键点。

![181454040628981](https://raw.githubusercontent.com/jingbiao95/Images/main/typora202106/07/201315-346435.png)

![181454040628981 (1)](https://raw.githubusercontent.com/jingbiao95/Images/main/typora202106/07/201309-107617.png)

若容器注册了以上各种接口，程序那么将会按照以上的流程进行。下面将仔细讲解各接口作用。



# **二、各种接口方法分类**

Bean的完整生命周期经历了各种方法调用，这些方法可以划分为以下几类：

1、Bean自身的方法　　：　　这个包括了Bean本身调用的方法和通过配置文件中<bean>的init-method和destroy-method指定的方法

2、Bean级生命周期接口方法　　：　　这个包括了BeanNameAware、BeanFactoryAware、InitializingBean和DiposableBean这些接口的方法

3、容器级生命周期接口方法　　：　　这个包括了InstantiationAwareBeanPostProcessor 和 BeanPostProcessor 这两个接口实现，一般称它们的实现类为“后处理器”。

4、工厂后处理器接口方法　　：　　这个包括了AspectJWeavingEnabler, ConfigurationClassPostProcessor, CustomAutowireConfigurer等等非常有用的工厂后处理器　　接口的方法。工厂后处理器也是容器级的。在应用上下文装配配置文件之后立即调用。

# Reference

https://www.cnblogs.com/zrtqsk/p/3735273.htmls