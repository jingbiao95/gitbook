# 1. Flink 时间语义

Flink定义了三类时间

- 处理时间（Process Time）数据进入Flink被处理的系统时间（Operator处理数据的系统时间）

- 事件时间（Event Time）数据在数据源产生的时间，一般由事件中的时间戳描述，比如用户日志中的TimeStamp

- 摄取时间（Ingestion Time）数据进入Flink的时间，记录被Source节点观察到的系统时间

  ![img](https:////upload-images.jianshu.io/upload_images/15332094-0b2efa87307bb731.png?imageMogr2/auto-orient/strip|imageView2/2/w/765/format/webp)

  Flink时间概念

在Flink中默认使用的是Process Time，绝大部分的业务都会使用eventTime，一般只在eventTime无法使用时，才会被迫使用ProcessingTime或者IngestionTime。
 如果要使用EventTime，那么需要引入EventTime的时间属性，引入方式如下所



```cpp
//设置时间语义为Ingestion Time 
env.setStreamTimeCharacteristic(TimeCharacteristic.IngestionTime)
//设置时间语义为Event Time 我们还需要指定一下数据中哪个字段是事件时间（下文会讲） 
env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
```

------

# 2. WaterMark

## 2.1  Why WaterMark

我们知道，流处理从事件产生，到流经source，再到operator，中间是有一个过程和时间的，虽然大部分情况下，流到operator的数据都是按照事件产生的时间顺序来的，但是在遇到特殊情况下，比如遇到网络延迟或者使用Kafka（多分区） 很难保证数据都是按照事件时间的顺序进入Flink，很有可能是乱序进入。

![img](https:////upload-images.jianshu.io/upload_images/15332094-0728422ba5f06979.png?imageMogr2/auto-orient/strip|imageView2/2/w/294/format/webp)

乱序数据

那么此时出现一个问题，一旦出现乱序，如果只根据eventTime决定window的运行，我们不能明确数据是否全部到位，但又不能无限期的等下去，此时必须要有个机制来保证一个**特定的时间后**，必须**触发**window去进行**计算**了，这个特别的机制，就是Watermark。

------

## 2.2 WaterMark 概念 (what)

- Watermark是一种衡量Event Time进展的机制。
- Watermark是用于处理乱序事件的，而正确的处理乱序事件，通常用Watermark机制结合window来实现。
- 数据流中的Watermark用于表示timestamp小于Watermark的数据，都已经到达了，因此，window的执行也是由Watermark触发的。
- Watermark可以理解成一个延迟触发机制，我们可以设置Watermark的延时时长t，每次系统会校验已经到达的数据中最大的maxEventTime，然后认定eventTime小于maxEventTime - t的所有数据都已经到达，如果有窗口的停止时间等于maxEventTime – t，那么这个窗口被触发执行。

有序流的Watermarker如下图所示：（Watermark设置为0）

![img](https:////upload-images.jianshu.io/upload_images/15332094-f311ef427965fad4.png?imageMogr2/auto-orient/strip|imageView2/2/w/531/format/webp)

in order Watermark


 乱序流的Watermarker如下图所示：（Watermark设置为2）

![img](https:////upload-images.jianshu.io/upload_images/15332094-931db731d6be3acf.png?imageMogr2/auto-orient/strip|imageView2/2/w/526/format/webp)

Out of order waterMark


 当Flink接收到数据时，会按照一定的规则去生成Watermark，这条Watermark就等于当前所有到达数据中的maxEventTime - 延迟时长，也就是说，Watermark是基于数据携带的时间戳生成的，一旦Watermark比当前未触发的窗口的停止时间要晚，那么就会触发相应窗口的执行。由于event time是由数据携带的，因此，如果运行过程中无法获取新的数据，那么没有被触发的窗口将永远都不被触发。
 上图中，我们设置的允许最大延迟到达时间为2s，所以时间戳为7s的事件对应的Watermark是5s，时间戳为12s的事件的Watermark是10s，如果我们的窗口1是1s5s，窗口2是6s10s，那么时间戳为7s的事件到达时的Watermarker恰好触发窗口1，时间戳为12s的事件到达时的Watermark恰好触发窗口2。



Watermark 就是触发前一窗口的“关窗时间”，一旦触发关门那么以当前时刻为准在窗口范围内的所有所有数据都会收入窗中。
 只要没有达到水位那么不管现实中的时间推进了多久都不会触发关窗。

**注意：如果数据不会乱序进入Flink，没必要使用Watermark ProcessTime 是没有乱序的**

------

## 2.3 WaterMark 引入  （how） 怎样使用WaterMark

![img](https:////upload-images.jianshu.io/upload_images/15332094-aff58b157359cbf8.png?imageMogr2/auto-orient/strip|imageView2/2/w/656/format/webp)

DataStream关于WaterMark的方法

根据 DataStream类，WaterMark相关的两个方法：

### 2.3.1 assignAscendingTimestamps

一种简单的特殊情况是，如果我们事先得知数据流的时间戳是单调递增的，也就是说没有乱序，那我们可以使用**assignAscendingTimestamps**，这个方法会直接使用数据的时间戳生成watermark。



```jsx
val stream: DataStream[SensorReading] = ...
val withTimestampsAndWatermarks = stream
.assignAscendingTimestamps(e => e.timestamp)

>> result:  E(1), W(1), E(2), W(2), ...
```

### 2.3.2 assignTimestampsAndWatermarks

- assignTimestampsAndWatermarks(watermarkStrategy: WatermarkStrategy[T])
- assignTimestampsAndWatermarks(assigner: AssignerWithPeriodicWatermarks[T])
- assignTimestampsAndWatermarks(assigner: AssignerWithPunctuatedWatermarks[T])

AssignerWithPeriodicWatermarks
 AssignerWithPunctuatedWatermarks
 以上两个接口都继承自**TimestampAssigner**。 在1.11已经不建议使用。

------

#### 2.3.2.1. AssignerWithPeriodicWatermarks

周期性的生成watermark：系统会周期性的将watermark插入到流中(水位线也是一种特殊的事件!)。默认周期是200毫秒。可以使用ExecutionConfig.setAutoWatermarkInterval()方法进行设置。



```kotlin
val env = StreamExecutionEnvironment.getExecutionEnvironment
env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)

// 每隔5秒产生一个watermark
env.getConfig.setAutoWatermarkInterval(5000)
```

产生watermark的逻辑：每隔5秒钟，Flink会调用AssignerWithPeriodicWatermarks的getCurrentWatermark()方法。如果方法返回一个时间戳大于之前水位的时间戳，新的watermark会被插入到流中。这个检查保证了水位线是单调递增的。如果方法返回的时间戳小于等于之前水位的时间戳，则不会产生新的watermark。
 例子，自定义一个周期性的时间戳抽取：



```kotlin
class PeriodicAssigner extends AssignerWithPeriodicWatermarks[SensorReading] {
val bound: Long = 60 * 1000 // 延时为1分钟
var maxTs: Long = Long.MinValue // 观察到的最大时间戳

override def getCurrentWatermark: Watermark = {
new Watermark(maxTs - bound)
}

override def extractTimestamp(r: SensorReading, previousTS: Long) = {
maxTs = maxTs.max(r.timestamp)
r.timestamp
}
}
```

而对于乱序数据流，如果我们能大致估算出数据流中的事件的最大延迟时间，就可以使用如下代码：



```kotlin
val stream: DataStream[SensorReading] = ...
val withTimestampsAndWatermarks = stream.assignTimestampsAndWatermarks(
new SensorTimeAssigner
)

class SensorTimeAssigner extends BoundedOutOfOrdernessTimestampExtractor[SensorReading](Time.seconds(5)) {
// 抽取时间戳
override def extractTimestamp(r: SensorReading): Long = r.timestamp
}

>> relust:  E(10), W(0), E(8), E(7), E(11), W(1), ...
```

#### 2.3.2.2. AssignerWithPunctuatedWatermarks

间断式地生成watermark。和周期性生成的方式不同，这种方式不是固定时间的，而是可以根据需要对每条数据进行筛选和处理。直接上代码来举个例子，我们只给sensor_1的传感器的数据流插入watermark：



```kotlin
class PunctuatedAssigner extends AssignerWithPunctuatedWatermarks[SensorReading] {
  val bound: Long = 60 * 1000

  override def checkAndGetNextWatermark(r: SensorReading, extractedTS: Long): Watermark = {
    if (r.id == "sensor_1") {
    new Watermark(extractedTS - bound)
    } else {
    null
    }
}
  override def extractTimestamp(r: SensorReading, previousTS: Long): Long = {
    r.timestamp
  }
}
```

#### 2.3.2.3. WatermarkStrategy

新的 WatermarkAssigner 接口将之前的 AssignerWithPunctuatedWatermarks 和 AssignerWithPeriodicWatermarks 的两类 Watermark 的接口进行了整合，从而简化了后续开发支持插入 Watermark 的 Source 实现复杂度。

------

##### WatermarkStrategy 实现

- 1. new WatermarkStrategy 直接实现WatermarkStrategy 接口



```java
public interface WatermarkStrategy<T> extends TimestampAssignerSupplier<T>, WatermarkGeneratorSupplier<T>{

    /**
     * Instantiates a {@link TimestampAssigner} for assigning timestamps according to this
     * strategy.
     */
    @Override
    TimestampAssigner<T> createTimestampAssigner(TimestampAssignerSupplier.Context context);

    /**
     * Instantiates a WatermarkGenerator that generates watermarks according to this strategy.
     */
    @Override
    WatermarkGenerator<T> createWatermarkGenerator(WatermarkGeneratorSupplier.Context context);
}
```

需要实现createWatermarkGenerator  方法创建watermark
 以及实现createTimestampAssigner方法将数据指定时间戳

通常不会自己实现此接口，而是调用WatermarkStrategy中的静态方法

- 1. WatermarkStrategy 静态方法



```kotlin
WatermarkStrategy
  .forBoundedOutOfOrderness[(Long, String)](Duration.ofSeconds(20))
  .withTimestampAssigner(new SerializableTimestampAssigner[(Long, String)] {
    override def extractTimestamp(element: (Long, String), recordTimestamp: Long): Long = element._1
  })
```

------

##### TimestampAssigner 和WatermarkGenerator 作用和介绍

TimestampAssigner 负责从事件中提取时间戳字段
 WatermarkGenerator 负责生成watermark



```dart
/**
 * The {@code WatermarkGenerator} generates watermarks either based on events or
 * periodically (in a fixed interval).
 *
 * <p><b>Note:</b> This WatermarkGenerator subsumes the previous distinction between the
 * {@code AssignerWithPunctuatedWatermarks} and the {@code AssignerWithPeriodicWatermarks}.
 */
@Public
public interface WatermarkGenerator<T> {

    /**
     * Called for every event, allows the watermark generator to examine and remember the
     * event timestamps, or to emit a watermark based on the event itself.
     */
    void onEvent(T event, long eventTimestamp, WatermarkOutput output);

    /**
     * Called periodically, and might emit a new watermark, or not.
     *
     * <p>The interval in which this method is called and Watermarks are generated
     * depends on {@link ExecutionConfig#getAutoWatermarkInterval()}.
     */
    void onPeriodicEmit(WatermarkOutput output);
}
```

*onEvent()*: 每来一条数据，将这条数据与maxTimesStamp比较，看是否需要更新watermark
 *onPeriodicEmit*：周期性更新watermark 间隔时间看setAutoWatermarkInterval的参数

WatermarkStrategy  通过这两个方法来整合了periodic and punctuated两种watermark的生成。

**周期性水印（Periodic Watermark）**根据事件或者处理时间周期性的触发水印生成器(Assigner)，。 通过onEvent()来观察传入事件，然后通过调用onPeriodicEmit周期性的生成watermark
 **间歇性水印（Punctuated Watermark）**在观察到事件后，会依据用户指定的条件来决定是否发射
 水印。通过onEvent()查看事件并等待特殊的标记事件或根据条件标点，这些事件或标点在流中携带水印信息。会立即emit水印。通常，间歇性水印生成器不会调用onPeriodicEmit()发出水印。

##### WatermarkGenerator 周期性生成水印

- 周期性的生成器监测流事件并周期性生成水印（可能周期性事件可能根据流数据的元素，或基于处理时间）。
- 通过`ExecutionConfig.setAutoWatermarkInterval(...)`定义生成水印的间隔时间（每*n*毫秒）。每到这个间隔时间，生成器的`onPeriodicEmit()`方法每次都会被调用。如果返回的watermark非空且大于前一个watermark，则将发出新的watermark。

下面使用两个周期性水印生成的WatermarkGenerator 的简单示例。请注意，Flink附带了 `BoundedOutOfOrdernessWatermarks`，它的`WatermarkGenerator`工作原理与以下`BoundedOutOfOrdernessGenerator`所示类似。



```kotlin
/**
 * This generator generates watermarks assuming that elements arrive out of order,
 * but only to a certain degree. The latest elements for a certain timestamp t will arrive
 * at most n milliseconds after the earliest elements for timestamp t.
 */
class BoundedOutOfOrdernessGenerator extends AssignerWithPeriodicWatermarks[MyEvent] {

    val maxOutOfOrderness = 3500L // 3.5 seconds

    var currentMaxTimestamp: Long = _

    override def onEvent(element: MyEvent, eventTimestamp: Long): Unit = {
        currentMaxTimestamp = max(eventTimestamp, currentMaxTimestamp)
    }

    override def onPeriodicEmit(): Unit = {
        // emit the watermark as current highest timestamp minus the out-of-orderness bound
        output.emitWatermark(new Watermark(currentMaxTimestamp - maxOutOfOrderness - 1));
    }
}

/**
 * This generator generates watermarks that are lagging behind processing time by a fixed amount.
 * It assumes that elements arrive in Flink after a bounded delay.
 */
class TimeLagWatermarkGenerator extends AssignerWithPeriodicWatermarks[MyEvent] {

    val maxTimeLag = 5000L // 5 seconds

    override def onEvent(element: MyEvent, eventTimestamp: Long): Unit = {
        // don't need to do anything because we work on processing time
    }

    override def onPeriodicEmit(): Unit = {
        output.emitWatermark(new Watermark(System.currentTimeMillis() - maxTimeLag));
    }
}
```

##### WatermarkGenerator 间歇性生成水印

WatermarkGenerator 将监测事件流，看到符合条件的事件元素就会emit watermark
 这样，可以实现一个间歇性的WatermarkGenerator ，该Generator 在事件表明它带有特定标记时会发出水印：



```kotlin
class PunctuatedAssigner extends AssignerWithPunctuatedWatermarks[MyEvent] {

    override def onEvent(element: MyEvent, eventTimestamp: Long): Unit = {
        if (event.hasWatermarkMarker()) {
            output.emitWatermark(new Watermark(event.getWatermarkTimestamp()))
        }
    }

    override def onPeriodicEmit(): Unit = {
        // don't need to do anything because we emit in reaction to events above
    }
}
```

**注意**：可以在每个事件上生成水印。但是，由于每个水印都会在下游引起一些计算，因此过多的水印会降低性能。

## 参考文献

https://www.jianshu.com/p/96f7c36ee28f

> 参考 Flink官网 [Timely Stream Processing](https://links.jianshu.com/go?to=https%3A%2F%2Fci.apache.org%2Fprojects%2Fflink%2Fflink-docs-release-1.11%2Fconcepts%2Ftimely-stream-processing.html)

