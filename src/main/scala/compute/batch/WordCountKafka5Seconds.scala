//package compute.batch
//import java.text.SimpleDateFormat
//import java.util.Properties
//import java.util.Date
//
//import org.apache.flink.api.common.serialization.SimpleStringSchema
//import org.apache.flink.streaming.api.CheckpointingMode
//import org.apache.flink.streaming.api.scala._
//import org.apache.flink.streaming.api.windowing.time.Time
//import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer08
//import org.apache.flink.streaming.connectors.redis.RedisSink
//import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig
//import org.apache.flink.streaming.connectors.redis.common.mapper.{RedisCommand, RedisCommandDescription, RedisMapper}
//import org.apache.flink.util.Collector
//
//
///**
//  * 把数据每隔5秒写入Redis
//  */
//object WordCountKafka5Seconds {
//
//  private val sdf = new SimpleDateFormat("yyyyMMddHHmm")
//
//
//  class RedisExampleMapper extends RedisMapper[(String, String)] {
//
//    override def getCommandDescription: RedisCommandDescription = {
//      new RedisCommandDescription(RedisCommand.RPUSH)
//    }
//
//    override def getKeyFromData(data: (String, String)): String = data._1
//
//    override def getValueFromData(data: (String, String)): String = data._2
//  }
//
//  val ZOOKEEPER_HOST = "artemis-02:2181,artemis-03:2181,artemis-04:2181/microlens/artemis/kafka"
//  val KAFKA_BROKER = "artemis-02:9092,artemis-02:9092,artemis-02:9092"
//  val TRANSACTION_GROUP = "transaction"
//  val TOPIC = "test-flink"
//
//  def main(args: Array[String]) {
//    System.setProperty("HADOOP_USER_NAME", "hadoop")
//
//    val env = StreamExecutionEnvironment.getExecutionEnvironment
//    env.enableCheckpointing(5000)
//    env.getCheckpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE)
//
//    val kafkaProps = new Properties()
//
//    kafkaProps.setProperty("zookeeper.connect", ZOOKEEPER_HOST)
//    kafkaProps.setProperty("bootstrap.servers", KAFKA_BROKER)
//    kafkaProps.setProperty("group.id", TRANSACTION_GROUP)
//
//    val kafkaConsumer = new FlinkKafkaConsumer08[String](TOPIC, new SimpleStringSchema(), kafkaProps)
//    kafkaConsumer.setStartFromLatest()
//
//    val result = env
//      .addSource(kafkaConsumer)
//      .flatMap { x =>
//        x.split(" ")
//      }
//      .map((_, 1))
//      .keyBy(0)
//      .timeWindow(Time.minutes(1L))
//      .apply { (tuple, window, values, out: Collector[Map[String, Int]]) =>
//        val time = sdf.format(new Date()) + ":"
//        val re = values.groupBy(_._1)
//          .map { x =>
//            var sum = 0
//            val key = x._1
//            val value = x._2
//            for ((k, v) <- value if k == key) {
//              sum += v
//            }
//            (time + key, sum)
//          }
//        out.collect(re)
//      }
//      .flatMap(x => x)
//      .map { x =>
//        val kv = x._1.split(":")
//        (kv(0), kv(1) + "->" + x._2)
//      }
//
//    val conf = new FlinkJedisPoolConfig.Builder().setHost("artemis-02").setPort(6379).build()
//    result.addSink(new RedisSink[(String, String)](conf, new RedisExampleMapper()))
//
//    env.execute()
//  }
//}