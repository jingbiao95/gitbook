1直接在controller层方法中传入request对象参数：通过该方法得到的request对象是不同的，是线程安全。

```java
@Controller
public class TestController{
	@RequestMapping("/test")
	public void test(HttpServletRequest request){
		//
	}
}
```



2.自动注入 通过该方法得到的request对象是不同的，是线程安全。

```
@Controller
public class TestController{
	@Autowired
	private HttpServletRequest request; //自动注入request
	
}
```

3.通过Controller基类进行自动注入

通过该方法得到的request对象是不同的，是线程安全的

```java
@Controller
public class BaseController{ //其他controller要继承该类
	@Autowired
	private HttpServletRequest request; //自动注入request
	
}
```

4.手动调用	

通过该方法得到的request对象是不同的，是线程安全的

```
@RequestMapping("/test")
public void test(){
	 HttpServletRequest request = ((ServletRequestAttributes)(RequestContextHolder.currentRequestAttributes())).getRequest();
        
	//
}
```

5.通过@ModelAttribute注解方法

通过该方法得到的request对象是相同的，是**线程不安全**。

```java
@Controller
public class TestController{
    private HttpServletRequest request;
    
    @ModelAttribute
    public 	void bindRequest(HttpServletRequest request){
        this.request = request;
    }
    
}
```



# reference



https://blog.csdn.net/qq_35387940/article/details/84023464