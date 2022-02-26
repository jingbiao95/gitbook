任务能够定时地执行



# 1.Timer和TimerTask

写一个类继承TimerTask，覆盖run()方法，并在方法里面加入定时的任务，然后调用Timer对象的`schedule(TimerTask task, long delay, long period)`方法即可。

```java
import java.util.Timer;
import java.util.TimerTask;

public class Demo extends TimerTask{
    @Override
    public void run() {
        //这里添加你需要定时执行的任务
        System.out.println("timertask run!");
    }

    public static void main(String[] args) {
        Timer timer = new Timer();
        long delay = 5000;//指定从当前时间延迟多久后开始执行定时的任务，单位是毫秒
        long period = 1000;//指定每次执行任务的间隔时间，单位是毫秒
        timer.schedule(new Demo(), delay, period);
    }
}
```





# 2.ScheduledExecutorService

## 2.1 scheduleWithFixedDelay()或scheduleAtFixedRate()方法

首先要写一个类实现runnable或callable接口，覆盖run()或call()方法，在方法里添加你要定时执行的任务。然后创建一个ScheduledExecutorService，调用定时方法。

```java
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledExecutorDemo implements Runnable{
    @Override
    public void run() {
        //这里添加你需要定时执行的任务
        System.out.println("scheduled executor run!");
    }

    public static void main(String[] args) {
        //使用Excutors的静态方法创建一个线程池ScheduledExecutorService，数字代表线程池的大小。
        //如果任务数量超过了这个数字，那么任务会在一个queue里等待执行
        ScheduledExecutorService service = Executors.newScheduledThreadPool(5);
        long delay = 5;//指定从当前时间延迟多久后开始执行定时的任务，时间单位可以在调用方法时指定
        long period = 1;//指定每次执行任务的时间间隔
        service.scheduleAtFixedRate(new ScheduledExecutorDemo(),delay,period, TimeUnit.SECONDS);
        service.scheduleWithFixedDelay(new ScheduledExecutorDemo(),delay,period, TimeUnit.SECONDS);
    }
}
```

scheduleAtFixedRate和scheduleWithFixedDelay区别在于：前者是每次执行任务都是在上一个任务开始之后固定的period间隔后开始，而后者则是每次执行任务时都是在上一个任务结束后的period时间后才开始，即后者的任务开始执行的间隔不是固定的，会受到实际任务的执行时间波动



## 2.2 schedule()方法

上面两种创建定时任务的方法都比较简洁，但是也有一定的缺点，那就是无法在外部控制定时任务的开启和关闭，而且定时的时间也是写死的，不利于后期的维护。所以这里介绍一下第三种方式。我们知道，ScheduledExecutorService类的schedule(Callable call,long delay, TimeUnit unit)方法是让指定的Callable任务call在指定的时间delay延迟后执行，那么我们只要在任务类call的run()方法中使用schedule()方法并传递自身的引用，就可以形成循环调用，让这个任务过一段时间之后重新执行自身。为了解耦，这个任务类还需要有开启和关闭任务、指定延迟时间的接口。详细代码如下：

```java
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
public class MyCallable implements Callable {
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);
    private long timeDelay = 5;
    public void excute(){
        scheduledExecutorService.submit(this);
    }
    public void stop(){
        scheduledExecutorService.shutdown();
    }
    public void setTimeDelay(long timeDelay){
        this.timeDelay = timeDelay;
    }
    @Override
    public Object call() throws Exception {
        //一些业务逻辑，也就是你要循环执行的代码
        System.out.println("my call Executed!");
        scheduledExecutorService.schedule(this, timeDelay, TimeUnit.SECONDS);
        return "Called!";
    }
}
```

执行这个循环任务的简单示例如下：



```java
public static void main(String[] args) throws Exception{

    MyCallable call = new MyCallable();
    call.setTimeDelay(1);//指定定时执行的时间间隔
    call.excute();//执行任务
    Thread.sleep(10000);//让主线程等待一定时间
    call.stop();//关闭任务
}
```

# Reference 

https://my.oschina.net/caibinice/blog/783836