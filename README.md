# sparkhbase

Spark2 On HBase is not Supported
https://www.cloudera.com/documentation/spark2/latest/topics/spark2_known_issues.html#ki_spark_on_hbase

The hbase-spark module is currently not supported with Spark 2.2. You'll get compile errors.
Need to use Spark 1.6

Tried this with CDH 5.11.1 and 5.13.0

### Prerequisites

Create the HBase table
Note: If you want to use Lily HBase NRT Indexer Service then for every existing table, set the REPLICATION_SCOPE on every column family that you want to index:
https://www.cloudera.com/documentation/enterprise/latest/topics/search_use_hbase_indexer_service.html

```
hbase(main):002:0> whoami
william@HADOOP.WCHOW.PVE (auth:KERBEROS)
    groups: domain, users, spark_users, cdh_admins, sentry_admins

hbase(main):003:0> create 'tbl1', {NAME => 'cf1' , REPLICATION_SCOPE => 1}, {NAME => 'cf2', REPLICATION_SCOPE => 1}
0 row(s) in 3.2330 seconds

=> Hbase::Table - tbl1
```

### Run this with
```
spark-submit --class com.cloudera.se.wchow.sparkhbase.HBaseBulkPutExample --master yarn --deploy-mode client target/sparkhbase-1.0-SNAPSHOT.jar tbl1 cf1
```

### hbase shell output
```
hbase(main):004:0> scan 'tbl1'
ROW                             COLUMN+CELL
 1                              column=cf1:1, timestamp=1505415095903, value=1
 2                              column=cf1:1, timestamp=1505415095903, value=2
 3                              column=cf1:1, timestamp=1505415096240, value=3
 4                              column=cf1:1, timestamp=1505415096240, value=4
 5                              column=cf1:1, timestamp=1505415096240, value=5
5 row(s) in 0.3910 seconds
```
