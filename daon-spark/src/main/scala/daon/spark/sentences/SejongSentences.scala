package daon.spark.sentences

import daon.spark.AbstractWriter
import org.apache.spark.sql._
import org.elasticsearch.spark.sql._

object SejongSentences extends AbstractWriter {


  def main(args: Array[String]) {

    val spark = getSparkSession()

    val prefix = CONFIG.getString("index.prefix")
    val jsonPath = CONFIG.getString("index.jsonPath")

    val sentencesVersion = CONFIG.getString("index.sentences.version")
    val sentencesScheme = CONFIG.getString("index.sentences.scheme")
    val sentencesType = CONFIG.getString("index.sentences.type")

    val trainSentences = s"${prefix}_train_sentences_$sentencesVersion"
    val testSentences = s"${prefix}_test_sentences_$sentencesVersion"

    createIndex(trainSentences, sentencesScheme)
    createIndex(testSentences, sentencesScheme)

    //초기 json 데이터 insert
    readSejongJsonWriteEs(spark, jsonPath, trainSentences, testSentences, sentencesType)

    createModelIndex()

    addAlias(trainSentences, "sentences")
    addAlias(testSentences, "sentences")
    addAlias(trainSentences, "train_sentences")
    addAlias(testSentences, "test_sentences")
  }

  override def getJobName() = "sejong_sentences_write"

  private def createModelIndex(): Unit = {
    //모델 스키마 생성
    val modelsVersion = CONFIG.getString("index.models.version")
    val modelsScheme = CONFIG.getString("index.models.scheme")

    val models = s"models_$modelsVersion"

    createIndex(models, modelsScheme)

    addAlias(models, "models")
  }

  private def readSejongJsonWriteEs(spark: SparkSession, jsonPath: String, trainIndexName: String, testIndexName: String, typeName: String) = {

    //spark.read.json => error Failed to find data source: json
//    val df = spark.read.json(jsonPath)
    val df = spark.read.format("org.apache.spark.sql.json").load(jsonPath)

    // 9:1
    val splitDF = df.randomSplit(Array(0.9, 0.1))
    val trainDF = splitDF(0)
    val testDF = splitDF(1)

    writeToEs(trainDF, trainIndexName, typeName)
    writeToEs(testDF, testIndexName, typeName)
  }
}
