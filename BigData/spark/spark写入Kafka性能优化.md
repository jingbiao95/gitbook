# Java版本



# Scala版本

## 一般写法

```
kafkaStreams.foreachRDD(rdd => {
      if (!rdd.isEmpty()) {
        rdd.foreachPartition(pr => {
          val properties = new Properties()
          properties.put("group.id", "jaosn_")
          properties.put("acks", "all")
          properties.put("retries ", "1")
          properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, PropertiesScalaUtils.loadProperties("broker"))
          properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName) //key的序列化;
          properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)
          val producer = new KafkaProducer[String, String](properties)
          pr.foreach(pair => {
            producer.send(new ProducerRecord(PropertiesScalaUtils.loadProperties("topic") + "_error", pair.value()))
          })
        })
      }
    })
```

缺点:
- 对于每个rdd的每一个partition的数据，每一次都需要创建一个KafkaProducer
- 带来性能问题，导致写的速度特别慢

## 优化写法

1.首先，我们需要将KafkaProducer利用lazy val的方式进行包装如下：

broadcastKafkaProducer.scala
```scala
package kafka

import java.util.concurrent.Future
import org.apache.kafka.clients.producer.{ KafkaProducer, ProducerRecord, RecordMetadata }


class broadcastKafkaProducer[K,V](createproducer:() => KafkaProducer[K,V]) extends Serializable{
  lazy val producer = createproducer()
  def send(topic:String,key:K,value:V):Future[RecordMetadata] = producer.send(new ProducerRecord[K,V](topic,key,value))
  def send(topic: String, value: V): Future[RecordMetadata] = producer.send(new ProducerRecord[K, V](topic, value))
}



object broadcastKafkaProducer {
  import scala.collection.JavaConversions._
  def apply[K,V](config:Map[String,Object]):broadcastKafkaProducer[K,V] = {
    val createProducerFunc  = () => {
      val producer = new KafkaProducer[K,V](config)
      sys.addShutdownHook({
        producer.close()
      })
      producer
    }
    new broadcastKafkaProducer(createProducerFunc)
  }
  def apply[K, V](config: java.util.Properties): broadcastKafkaProducer[K, V] = apply(config.toMap)
}
```

2、之后我们利用广播变量的形式，将KafkaProducer广播到每一个executor，如下：
```scala
// 广播 broadcastKafkaProducer 到每一个excutors上面;
    val kafkaProducer: Broadcast[broadcastKafkaProducer[String, String]] = {
      val kafkaProducerConfig = {
        val p = new Properties()
        p.put("group.id", "jaosn_")
        p.put("acks", "all")
        p.put("retries ", "1")
        p.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, PropertiesScalaUtils.loadProperties("broker"))
        p.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName) //key的序列化;
        p.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)
        p
      }
      scc.sparkContext.broadcast(broadcastKafkaProducer[String, String](kafkaProducerConfig))
    }
```

3、然后我们就可以在每一个executor上面将数据写入到kafka中了
```scala
kafkaStreams.foreachRDD(rdd => {
      if (!rdd.isEmpty()) {
        rdd.foreachPartition(pr => {
          pr.foreach(pair => {
            kafkaProducer.value.send("topic_name",pair.value())
          })
        })
      }
    })
```
这样的话，就不需要每次都去创建了。先写到这儿吧。经过测试优化过的写法性能是之前的几十倍