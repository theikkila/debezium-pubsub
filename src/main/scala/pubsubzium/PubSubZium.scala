package pubsubzium

import io.debezium.config.Configuration;
import io.debezium.embedded.EmbeddedEngine;
import io.debezium.util.Clock;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.json.JsonConverter;
import java.util.concurrent.Executors
import java.nio.charset.StandardCharsets
//
// object Handler {
//
// }

object PubSubZium extends App {
//  def main(args: Array[String]): Unit = {

  println("starting")

  // Define the configuration for the embedded and MySQL connector ...
  val config = Configuration.create()
          /* begin engine properties */
          .`with`("connector.class",
                "io.debezium.connector.sqlserver.SqlServerConnector")
          .`with`("offset.storage",
                "org.apache.kafka.connect.storage.FileOffsetBackingStore")
          .`with`("offset.storage.file.filename",
                "offset.dat")
          .`with`("offset.flush.interval.ms", 1000)
          /* begin connector properties */
          .`with`("name", "my-sql-connector")
          .`with`("database.hostname", "localhost")
          .`with`("database.dbname", "vuoronumerodb")
          .`with`("table.whitelist", "default.turn_number")
          .`with`("database.port", 1433)
          .`with`("database.user", "sa")
          .`with`("database.password", "kissa@2Kossu")
          .`with`("schemas.enable", false)
          // .`with`("database.server.id", 85744)
          .`with`("database.server.name", "vuoronumerodb")
          .`with`("database.history",
                "io.debezium.relational.history.FileDatabaseHistory")
          .`with`("database.history.file.filename",
                "dbhistory.dat")
          .build()

    val valueConverter = new JsonConverter();
    valueConverter.configure(config.asMap(), false);

    def handleEvent(record: SourceRecord) {
      println("event!")
      println(record)

      val payload = valueConverter.fromConnectData("dummy", record.valueSchema(), record.value());

      println("data")
      val str = new String(payload, StandardCharsets.UTF_8)
      println(str)
    }

  // Create the engine with this configuration ...
  val engine = EmbeddedEngine.create()
          .using(config)
          .using(Clock.SYSTEM)
          .notifying(handleEvent _)
          .build();

  // Run the engine asynchronously ...
  val executor = Executors.newSingleThreadExecutor();
  executor.execute(engine);

  scala.sys.addShutdownHook {
    println("Requesting embedded engine to shut down");
    engine.stop();
  }

  // the submitted task keeps running, only no more new ones can be added
  //executor.shutdown();


  while (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
      println("Waiting another 10 seconds for the embedded engine to complete");
  }
//  }

}
