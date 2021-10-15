package com.prism.dataplatform.twitter

import com.prism.dataplatform.flink.FlinkJob
import com.prism.dataplatform.twitter.config.{Config, TConfig}
import com.prism.dataplatform.twitter.entities.responses.TweetsResponse
import com.prism.dataplatform.twitterconnector.Twitter
import org.apache.avro.file.{CodecFactory, DataFileWriter}
import org.apache.avro.reflect.{ReflectData, ReflectDatumWriter}
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.core.fs.Path
import org.apache.flink.formats.avro.{AvroBuilder, AvroWriterFactory}
import org.apache.flink.streaming.api.functions.sink.filesystem.{OutputFileConfig, StreamingFileSink}

import java.io.OutputStream

final class AnalyzeTweets extends FlinkJob[Config] {
  override def script(): Unit = {
    logger.info("Application started")

    val tconfig = TConfig(
      config.twitter.consumerKey,
      config.twitter.consumerSecret,
      config.twitter.bearerToken,
      config.twitter.token,
      config.twitter.tokenSecret
    )
    val tweets =
      env.addSource(Twitter(tconfig))
        .name("Tweets")

    val factory = new AvroWriterFactory[TweetsResponse](new AvroBuilder[TweetsResponse]() {
      override def createWriter(out: OutputStream): DataFileWriter[TweetsResponse] = {
        val schema = ReflectData.get.getSchema(classOf[TweetsResponse])
        val datumWriter = new ReflectDatumWriter[TweetsResponse](schema)

        val dataFileWriter = new DataFileWriter[TweetsResponse](datumWriter)
        dataFileWriter.setCodec(CodecFactory.snappyCodec)
        dataFileWriter.create(schema, out)
        dataFileWriter
      }
    })

    val outputConfig = OutputFileConfig
      .builder()
      .withPartPrefix("output")
      .withPartSuffix(".avro")
      .build()

    val sink = StreamingFileSink.forBulkFormat(
      new Path("D:\\sink"), factory)
      .withOutputFileConfig(outputConfig)
      .build()

    tweets.addSink(sink)
  }
}

//TO DO: cache
// Source -> flatMap monads