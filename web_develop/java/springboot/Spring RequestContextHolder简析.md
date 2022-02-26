在Web开发中，service层或者某个工具类中需要获取到HttpServletRequest对象还是比较常见的。一种方式是将HttpServletRequest作为方法的参数从controller层一直放下传递，不过这种有点费劲，且做起来不是优雅；还有另一种则是RequestContextHolder，直接在需要用的地方使用如下方式取HttpServletRequest即可，使用代码如下：

```java
HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
```

要理解上面的为何可以这么使用，需要理解两个问题：

1. RequestContextHolder为什么能获取到当前的HttpServletRequest
2. HttpServletRequest是在什么时候设置到RequestContextHolder

对于第1个问题，熟悉ThreadLocal的人应该很容易看出来这个是ThreadLocal的应用，这个类的原理在博文[(ThreadLocal原理)](https://www.jianshu.com/p/6bf1adb775e0)有讲到，其实很类似上篇博文文末提到的UserContextHolder。

第2个问题应该属于spring-mvc的问题，这个是在spring-mvc执行时设置进去的



