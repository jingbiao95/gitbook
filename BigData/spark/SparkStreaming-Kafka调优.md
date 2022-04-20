# Spark Streaming消费Kafka

SS 是 Spark 上的一个流式处理框架，可以面向海量数据实现高吞吐量、高容错的实时计算。SS 支持多种类型数据源，包括 Kafka、Flume、twitter、zeroMQ、Kinesis 以及 TCP sockets 等。SS 实时接收数据流，并按照一定的时间间隔（下文称为“批处理时间间隔”）将连续的数据流拆分成一批批离散的数据集；然后应用诸如 map、reduce、join 和 window 等丰富的 API 进行复杂的数据处理；最后提交给 Spark 引擎进行运算，得到批量结果数据，因此其也被称为准实时处理系统。



## Spark Streaming 基础概念

### DStream

Discretized Stream 是 SS 的基础抽象，代表持续性的数据流和经过各种 Spark 原语操作后的结果数据流。DStream 本质上是一个以时间为键，RDD 为值的哈希表，保存了按时间顺序产生的 RDD，而每个 RDD 封装了批处理时间间隔内获取到的数据。SS 每次将新产生的 RDD 添加到哈希表中，而对于已经不再需要的 RDD 则会从这个哈希表中删除，所以 DStream 也可以简单地理解为以时间为键的 RDD 的动态序列。如下图：

![图片](https://mmbiz.qpic.cn/mmbiz_png/US10Gcd0tQGLrVC36tOG0fSSEZJmGBw9KSqUEicA0tdSoNzOmJ0Wjicic4LibboGYQSgd02KQxoBJLjnQiak9AJk1aw/640?wxfrom=5&wx_lazy=1&wx_co=1)

**窗口时间间隔**

窗口时间间隔又称为窗口长度，它是一个抽象的时间概念，决定了 SS 对 RDD 序列进行处理的范围与粒度，即用户可以通过设置窗口长度来对一定时间范围内的数据进行统计和分析。假如设置批处理时间间隔为 1s，窗口时间间隔为 3s。如下图，DStream 每 1s 会产生一个 RDD，红色边框的矩形框就表示窗口时间间隔，一个窗口时间间隔内最多有 3 个 RDD，Spark Streaming 在一个窗口时间间隔内最多会对 3 个 RDD 中的数据进行统计和分析。

![图片](https://mmbiz.qpic.cn/mmbiz_jpg/US10Gcd0tQGLrVC36tOG0fSSEZJmGBw9RoPBeCnojJbXMSzOeDh1pmpJsX1JOGNwzwRib1zy1PxcZOF5rDLTOFg/640?wxfrom=5&wx_lazy=1&wx_co=1)

**滑动时间间隔**

滑动时间间隔决定了 SS 程序对数据进行统计和分析的频率。它指的是经过多长时间窗口滑动一次形成新的窗口，滑动时间间隔默认情况下和批处理时间间隔相同，而窗口时间间隔一般设置的要比它们两个大。在这里必须注意的一点是滑动时间间隔和窗口时间间隔的大小一定得设置为批处理时间间隔的整数倍。

如下图，批处理时间间隔是 1 个时间单位，窗口时间间隔是 3 个时间单位，滑动时间间隔是 2 个时间单位。对于初始的窗口 time 1-time 3，只有窗口时间间隔满足了才触发数据的处理。这里需要注意的一点是，初始的窗口有可能覆盖的数据没有 3 个时间单位，但是随着时间的推进，窗口最终会覆盖到 3 个时间单位的数据。当每个 2 个时间单位，窗口滑动一次后，会有新的数据流入窗口，这时窗口会移去最早的两个时间单位的数据，而与最新的两个时间单位的数据进行汇总形成新的窗口（time3-time5）。

![图片](https://mmbiz.qpic.cn/mmbiz_jpg/US10Gcd0tQGLrVC36tOG0fSSEZJmGBw9icUS1J70ACLyxp1jG6pOHnOxC573iaEJxn43uOZYiakVa3p8HTGsqauQw/640?wxfrom=5&wx_lazy=1&wx_co=1)

Spark Streaming 读取 Kafka 数据

Spark Streaming 与 Kafka 集成接收数据的方式有两种：

- Receiver-based Approach
- Direct Approach (No Receivers)

**Receiver-based Approach**

这个方法使用了 Receivers 来接收数据。Receivers 的实现使用到 Kafka 高级消费者 API。对于所有的 Receivers，接收到的数据将会保存在 Spark executors 中，然后由 SS 启动的 Job 来处理这些数据。

然而，在默认的配置下，这种方法在失败的情况下会丢失数据，为了保证零数据丢失，你可以在 SS 中使用 WAL 日志，这是在 Spark 1.2.0 才引入的功能，这使得我们可以将接收到的数据保存到 WAL 中（WAL 日志可以存储在 HDFS 上），所以在失败的时候，我们可以从 WAL 中恢复，而不至于丢失数据。

# 调优

## 合理的批处理时间（batchDuration）

在StreamingContext初始化的时候，有一个参数便是批处理时间的设定。如果这个值设置的过短，即个batchDuration所产生的Job并不能在这期间完成处理，那么就会造成数据不断堆积，最终导致Spark
Streaming发生阻塞。而且，一般对于batchDuration的设置不会小于500ms，因为过小会导致SparkStreaming频繁的提交作业，对整个streaming造成额外的负担。在平时的应用中，根据不同的应用场景和硬件配置，我设在1~
10s之间，我们可以根据SparkStreaming的可视化监控界面，观察Total Delay来进行batchDuration的调整，如下图：

![img](https://raw.githubusercontent.com/jingbiao95/Images/main/524764-20170103171142066-1054088549.png)

## 合理的Kafka拉取量（maxRatePerPartition重要）

配置参数为：spark.streaming.kafka.maxRatePerPartition
这个参数默认是没有上线的，即kafka当中有多少数据它就会直接全部拉出。而根据生产者写入Kafka的速率以及消费者本身处理数据的速度，同时这个参数需要结合上面的batchDuration，使得每个partition拉取在每个batchDuration期间拉取的数据能够顺利的处理完毕，做到尽可能高的吞吐量

![](https://raw.githubusercontent.com/jingbiao95/Images/main/524764-20170103172311159-1621531817.png)

## 缓存反复使用的Dstream（RDD）

Spark中的RDD和SparkStreaming中的Dstream，如果被反复的使用，最好利用cache()，将该数据流缓存起来，防止过度的调度资源造成的网络开销。可以参考观察Scheduling Delay参数，如下图：

![img](https://raw.githubusercontent.com/jingbiao95/Images/main/524764-20170103185139331-147812384.png)

## 设置合理的GC

长期使用Java的小伙伴都知道，JVM中的垃圾回收机制，可以让我们不过多的关注与内存的分配回收，更加专注于业务逻辑，JVM都会为我们搞定。对JVM有些了解的小伙伴应该知道，在Java虚拟机中，将内存分为了初生代（eden generation）、年轻代（young generation）、老年代（old generation）以及永久代（permanent generation），其中每次GC都是需要耗费一定时间的，尤其是老年代的GC回收，需要对内存碎片进行整理，通常采用标记-清楚的做法。同样的在Spark程序中，JVM GC的频率和时间也是影响整个Spark效率的关键因素。在通常的使用中建议：

```bash
--conf "spark.executor.extraJavaOptions=-XX:+UseConcMarkSweepGC"
```

## 设置合理的CPU资源数

CPU的core数量，每个executor可以占用一个或多个core，可以通过观察CPU的使用率变化来了解计算资源的使用情况，例如，很常见的一种浪费是一个executor占用了多个core，但是总的CPU使用率却不高（因为一个executor并不总能充分利用多核的能力），这个时候可以考虑让么个executor占用更少的core，同时worker下面增加更多的executor，或者一台host上面增加更多的worker来增加并行执行的executor的数量，从而增加CPU利用率。但是增加executor的时候需要考虑好内存消耗，因为一台机器的内存分配给越多的executor，每个executor的内存就越小，以致出现过多的数据spill over甚至out of memory的情况。

## 设置合理的parallelism

partition和parallelism，partition指的就是数据分片的数量，每一次task只能处理一个partition的数据，这个值太小了会导致每片数据量太大，导致内存压力，或者诸多executor的计算能力无法利用充分；但是如果太大了则会导致分片太多，执行效率降低。在执行action类型操作的时候（比如各种reduce操作），partition的数量会选择parent RDD中最大的那一个。而parallelism则指的是在RDD进行reduce类操作的时候，默认返回数据的paritition数量（而在进行map类操作的时候，partition数量通常取自parent RDD中较大的一个，而且也不会涉及shuffle，因此这个parallelism的参数没有影响）。所以说，这两个概念密切相关，都是涉及到数据分片的，作用方式其实是统一的。通过spark.default.parallelism可以设置默认的分片数量，而很多RDD的操作都可以指定一个partition参数来显式控制具体的分片数量。
在SparkStreaming+kafka的使用中，我们采用了Direct连接方式，Spark中的partition和Kafka中的Partition是一一对应的，我们一般默认设置为Kafka中Partition的数量

## 使用高性能的算子

- 使用reduceByKey/aggregateByKey替代groupByKey
- 使用mapPartitions替代普通map
- 使用foreachPartitions替代foreach
- 使用filter之后进行coalesce操作
- 使用repartitionAndSortWithinPartitions替代repartition与sort类操作

## 使用Kryo优化序列化性能

在Spark中，主要有三个地方涉及到了序列化：

- 在算子函数中使用到外部变量时，该变量会被序列化后进行网络传输
- 将自定义的类型作为RDD的泛型类型时（比如JavaRDD，Student是自定义类型），所有自定义类型对象，都会进行序列化。因此这种情况下，也要求自定义的类必须实现Serializable接口。
- 使用可序列化的持久化策略时（比如MEMORY_ONLY_SER），Spark会将RDD中的每个partition都序列化成一个大的字节数组。

对于这三种出现序列化的地方，我们都可以通过使用Kryo序列化类库，来优化序列化和反序列化的性能。Spark默认使用的是Java的序列化机制，也就是ObjectOutputStream/ObjectInputStream API来进行序列化和反序列化。但是Spark同时支持使用Kryo序列化库，Kryo序列化类库的性能比Java序列化类库的性能要高很多。官方介绍，Kryo序列化机制比Java序列化机制，性能高10倍左右。Spark之所以默认没有使用Kryo作为序列化类库，是因为Kryo要求最好要注册所有需要进行序列化的自定义类型，因此对于开发者来说，这种方式比较麻烦。





使用Kryo的代码示例

我们只要设置序列化类，再注册要序列化的自定义类型即可（比如算子函数中使用到的外部变量类型、作为RDD泛型类型的自定义类型等）：

```scala
// 创建SparkConf对象。
val conf = new SparkConf().setMaster(...).setAppName(...)
// 设置序列化器为KryoSerializer。
conf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
// 注册要序列化的自定义类型。
conf.registerKryoClasses(Array(classOf[MyClass1], classOf[MyClass2]))

```

