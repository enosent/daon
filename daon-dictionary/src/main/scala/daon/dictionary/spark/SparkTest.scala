package daon.dictionary.spark

import java.sql.DriverManager
import java.util.Properties

import org.apache.phoenix.jdbc.PhoenixStatement
import org.apache.spark.sql._

object SparkTest {


//  {"seq":1,"word":"!","tag":"sf","irrRule":null,"prob":7.3839235,"subWords":null,"desc":""}
  case class Keyword(word: String, tag: String, tf: Long, prop: Double)


  def main(args: Array[String]) {

//    val conf = new SparkConf().setAppName("Simple Application").setMaster("local[*]")
//    val sc = new SparkContext(conf)
//    val spark = new SQLContext(sc)


    val spark = SparkSession
      .builder()
      .appName("daon dictionary")
      .master("local[*]")
//            .config("spark.some.config.option", "some-value")
      .getOrCreate()

//    import spark.sqlContext.implicits._
    import spark.implicits._

    val df = spark.read.json("/Users/mac/Downloads/sejong_mini.json")

//    df.registerTempTable("raw_sentence")

//    df.show()
    df.createOrReplaceTempView("raw_sentence")

    // 구조를 이대로 해도 될까??
    val allDF = spark.sql(
      """
        | SELECT
        |        sentence,
        |        seq as eojeol_seq,
        |        offset as eojeol_offset,
        |        surface,
        |        morpheme.seq as word_seq,
        |        morpheme.word,
        |        morpheme.tag,
        |        morpheme.prevOuter.word as p_outer_word,
        |        morpheme.prevOuter.tag as p_outer_tag,
        |        morpheme.nextOuter.word as n_outer_word,
        |        morpheme.nextOuter.tag as n_outer_tag,
        |        morpheme.prevInner.word as p_inner_word,
        |        morpheme.prevInner.tag as p_inner_tag,
        |        morpheme.nextInner.word as n_inner_word,
        |        morpheme.nextInner.tag as n_inner_tag
        | FROM (
        |   SELECT sentence, eojeol.surface as surface, eojeol.seq, eojeol.offset, eojeol.morphemes as morphemes
        |   FROM raw_sentence
        |   LATERAL VIEW explode(eojeols) exploded_eojeols as eojeol
        | )
        | LATERAL VIEW explode(morphemes) exploded_morphemes as morpheme
        |
      """.stripMargin)

    allDF.printSchema()

    val count = allDF.count()
    println("all cnt = " + count)

//    allDF.registerTempTable("sentence")
    allDF.createOrReplaceTempView("sentence")

    allDF.cache()

    allDF.show()


    Class.forName("org.apache.phoenix.jdbc.PhoenixDriver")
    val props = new Properties()
    props.setProperty("phoenix.connection.autoCommit", "true")
    val conn = DriverManager.getConnection("jdbc:phoenix:localhost", props).unwrap(classOf[org.apache.phoenix.jdbc.PhoenixConnection])

    //    conn.setAutoCommit(false)
    val phoenixStmt : PhoenixStatement = conn.createStatement().unwrap(classOf[org.apache.phoenix.jdbc.PhoenixStatement])

    phoenixStmt.executeUpdate(
      """
        |    DROP TABLE IF EXISTS WORDS
      """.stripMargin
    )

    phoenixStmt.executeUpdate(
      """
        |    CREATE TABLE WORDS (
        |      TAG VARCHAR NOT NULL,
        |      WORD VARCHAR NOT NULL,
        |      SURFACE VARCHAR NOT NULL,
        |      WORD_SEQ INTEGER NOT NULL,
        |      EOJEOL_SEQ INTEGER NOT NULL,
        |      EOJEOL_OFFSET INTEGER NOT NULL,
        |      SENTENCE VARCHAR NOT NULL,
        |      "PREV".P_OUTER_WORD VARCHAR,
        |      "NEXT".N_OUTER_WORD VARCHAR,
        |      "PREV".P_INNER_WORD VARCHAR,
        |      "NEXT".N_INNER_WORD VARCHAR,
        |      "PREV".P_OUTER_TAG VARCHAR,
        |      "NEXT".N_OUTER_TAG VARCHAR,
        |      "PREV".P_INNER_TAG VARCHAR,
        |      "NEXT".N_INNER_TAG VARCHAR
        |      CONSTRAINT PK PRIMARY KEY (TAG, WORD, SURFACE, WORD_SEQ, EOJEOL_SEQ, EOJEOL_OFFSET, SENTENCE)
        |    )
      """.stripMargin
    )


    phoenixStmt.executeUpdate(
      """
        |    CREATE INDEX WORDS_WORD_IDX ON WORDS (WORD)
      """.stripMargin
    )


    allDF.write.format("org.apache.phoenix.spark")
      .option("table", "WORDS")
      .option("zkUrl","localhost:2181")
      .mode("overwrite")
      .save()


    val notInTags = "tag not in ('nh', 'nb', 'un', 'ne')"

    //    22231026 ?
    //    22231621
    //총 노출 건수
    val totalFreq = spark.sql(
      s"""
        | select count(0) as cnt
        | from sentence
        | where ${notInTags}
      """.stripMargin)

    val totalCnt = totalFreq.collect().head.getLong(0)

    println("totalCnt = " + totalCnt)

    val wordFreqDF = spark.sql(
      s"""
        | select word, tag, count(surface) as tf
        | from sentence
        | where ${notInTags}
        | group by word, tag
        | order by word asc
      """.stripMargin)

    //    sqlDF.printSchema()
//    implicit val keywordKryoEncoder = Encoders.kryo[Keyword]

    val words = wordFreqDF.map(x => {

//      println(x.getString(0), x.getString(1), x.getLong(2), totalCnt, -math.log10(x.getLong(2).toFloat / totalCnt))
//      Row("word" -> x.getString(0), "tag" -> x.getString(2))

      val prob = -math.log(x.getLong(2).toFloat / totalCnt)

      Keyword(x.getString(0), x.getString(1), x.getLong(2), prob)
    })

//    words.toJSON.collect().foreach(println)

    words.sort().coalesce(1).write.mode(SaveMode.Overwrite).format("json").save("/Users/mac/Downloads/words/words")

//    df.toJSON.saveAsTextFile("/tmp/jsonRecords")
//    df.toJSON.take(2).foreach(println)

    /*

    val tagFreqDF = spark.sql(
      s"""
        | select tag, count(word) as tag_cnt
        | from sentence
        | where ${notInTags}
        | group by tag
        | order by count(word) desc
      """.stripMargin)


    tagFreqDF.collect().foreach(row => {

      val tag = row.get(0)
      val tagCnt = row.get(1)
      val where =
        s"""
           | tag = "${tag}"
         """.stripMargin

      val tagDF = allDF.where(where)

      val pInner = tagDF.where("p_inner_tag is not null").groupBy("p_inner_tag").count().sort(desc("count"))
      val nInner = tagDF.where("n_inner_tag is not null").groupBy("n_inner_tag").count().sort(desc("count"))
      val pOuter = tagDF.where("p_outer_tag is not null").groupBy("p_outer_tag").count().sort(desc("count"))
      val nOuter = tagDF.where("n_outer_tag is not null").groupBy("n_outer_tag").count().sort(desc("count"))

      println(tag, tagCnt)
      pInner.show()
      nInner.show()
      pOuter.show()
      nOuter.show()

    })


    //    val sqlDF3 = sqlDF2.join(sqlDF, sqlDF("word") === sqlDF2("word") && sqlDF("tag") === sqlDF2("tag"), joinType = "inner")


    //    sqlDF3.show()


    */
  }

  def wordProp() = {

  }
}
