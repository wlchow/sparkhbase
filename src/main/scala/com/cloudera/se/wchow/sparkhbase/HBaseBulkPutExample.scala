package com.cloudera.se.wchow.sparkhbase

import org.apache.hadoop.fs.Path
import org.apache.hadoop.hbase.spark.HBaseContext
import org.apache.spark.SparkContext
import org.apache.hadoop.hbase.{TableName, HBaseConfiguration}
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.client.Put
import org.apache.spark.SparkConf

object HBaseBulkPutExample {

  def main(args: Array[String]) {
    if (args.length < 2) {
      println("HBaseBulkPutExample {tableName} {columnFamily} are missing an arguments")
      return
    }

    val tableName = args(0)
    val columnFamily = args(1)

    val sparkConf = new SparkConf().setAppName("HBaseBulkPutExample " +
      tableName + " " + columnFamily)
    val sc = new SparkContext(sparkConf)
    try {
      //[(Array[Byte], Array[(Array[Byte], Array[Byte], Array[Byte])])]
      // This consists of:
      // RowKey, [ ColumnFamily, Column, Value ]

      // generate some dummy data with 5 rows
      val rdd = sc.parallelize(Array(
        (Bytes.toBytes("1"),
          Array((Bytes.toBytes(columnFamily), Bytes.toBytes("1"), Bytes.toBytes("1")))),
        (Bytes.toBytes("2"),
          Array((Bytes.toBytes(columnFamily), Bytes.toBytes("1"), Bytes.toBytes("2")))),
        (Bytes.toBytes("3"),
          Array((Bytes.toBytes(columnFamily), Bytes.toBytes("1"), Bytes.toBytes("3")))),
        (Bytes.toBytes("4"),
          Array((Bytes.toBytes(columnFamily), Bytes.toBytes("1"), Bytes.toBytes("4")))),
        (Bytes.toBytes("5"),
          Array((Bytes.toBytes(columnFamily), Bytes.toBytes("1"), Bytes.toBytes("5"))))
      ))



      val conf = HBaseConfiguration.create()

      // Pass the HBase configs and SparkContext to the HBaseContext
      // http://blog.cloudera.com/blog/2014/12/new-in-cloudera-labs-sparkonhbase/
       conf.addResource(new Path("/etc/hbase/conf/hbase-site.xml"))

      val hbaseContext = new HBaseContext(sc, conf)
      hbaseContext.bulkPut[(Array[Byte], Array[(Array[Byte], Array[Byte], Array[Byte])])](rdd,
        TableName.valueOf(tableName),
        (putRecord) => {
          val put = new Put(putRecord._1)
          putRecord._2.foreach((putValue) =>
            put.addColumn(putValue._1, putValue._2, putValue._3))
          put
        });
    } finally {
      sc.stop()
    }
  }

}
