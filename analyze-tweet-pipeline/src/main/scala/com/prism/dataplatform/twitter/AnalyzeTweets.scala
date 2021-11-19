package com.prism.dataplatform.twitter

import com.prism.dataplatform.flink.FlinkJob
import com.prism.dataplatform.twitter.config.{Config, TConfig}
import com.prism.dataplatform.twitter.entities.responses.TweetResponse
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

    val factory = new AvroWriterFactory[TweetResponse](new AvroBuilder[TweetResponse]() {
      override def createWriter(out: OutputStream): DataFileWriter[TweetResponse] = {
        val schema = ReflectData.get.getSchema(classOf[TweetResponse])
        val datumWriter = new ReflectDatumWriter[TweetResponse](schema)

        val dataFileWriter = new DataFileWriter[TweetResponse](datumWriter)
        dataFileWriter.setCodec(CodecFactory.snappyCodec)
        dataFileWriter.create(schema, out)
        dataFileWriter
      }
    })

    val outputConfig = OutputFileConfig
      .builder()
      .withPartPrefix(
        "output")
      .withPartSuffix(".avro")
      .build()

    val sink = StreamingFileSink.forBulkFormat(
      new Path("D:\\sink"), factory)
      .withOutputFileConfig(outputConfig)
      .build()

    tweets.print()
  }
}

//TO DO: cache
// Source -> flatMap monads