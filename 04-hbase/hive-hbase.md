# Unidad 3: Big Data - HIVE-HBASE
## ST0263 - Tópicos Especiales en Telemática
## Ingeniería de Sistemas
## Universidad EAFIT
### Profesor: Edwin Montoya M. – emontoya@eafit.edu.co
## 2017-2

# HIVE-HBASE

para HDP 2.6 en

1. Se tiene una tabla en hbase llamada 'emontoya:test':

      $ hbase shell

      hbase(main):00x:0> create_namespace 'emontoya'
      hbase(main):00x:0> create 'emontoya:test', 'cf1', 'cf2'
      hbase(main):00x:0>put 'emontoya:test', 'r1', 'cf1:name', 'pepe'
      hbase(main):00x:0>put 'emontoya:test', 'r1', 'cf1:last', 'perez'
      hbase(main):00x:0>put 'emontoya:test', 'r2', 'cf1:last', 'montoya'
      hbase(main):00x:0>put 'emontoya:test', 'r2', 'cf2:salary', '1200'
      hbase(main):00x:0>put 'emontoya:test', 'r3', 'cf1:name', 'esteban'
      hbase(main):00x:0>put 'emontoya:test', 'r3', 'cf2:salary', '1100'         
      hbase(main):00x:0>scan 'emontoya:test'

      ROW                   COLUMN+CELL                                               
      r1                   column=cf1:last, timestamp=1506180597019, value=perez     
      r1                   column=cf1:name, timestamp=1506180591142, value=pepe      
      r2                   column=cf1:last, timestamp=1506180602675, value=montoya   
      r2                   column=cf2:salary, timestamp=1506180608682, value=1200    
      r3                   column=cf1:name, timestamp=1506180614063, value=esteban   
      r3                   column=cf2:salary, timestamp=1506180619223, value=1100    
      3 row(s) in 0.1050 seconds

2. Crear y consultar la tabla en HIVE


      $ kinit
      $ beeline
      beeline> !connect jdbc:hive2://hdplabmaster:2181/;serviceDiscoveryMode=zooKeeper;zooKeeperNamespace=hiveserver2
      username: emontoya
      password: *******

      0: jdbc:hive2://hdplabmaster:2181/> use emontoya;

      1: jdbc:hive2://hdplabmaster:2181/> create external table my_hbase_table (key string, name string, last string, salary string) stored by 'org.apache.hadoop.hive.hbase.HBaseStorageHandler' with serdeproperties ('hbase.columns.mapping'=':key,cf1:name,cf1:last,cf2:salary') tblproperties ('hbase.table.name'='emontoya:test');

      No rows affected (0.399 seconds)

      1: jdbc:hive2://hdplabmaster:2181/> select * from my_hbase_table;
      +---------------------+----------------------+----------------------+------------------------+--+
      | my_hbase_table.key  | my_hbase_table.name  | my_hbase_table.last  | my_hbase_table.salary  |
      +---------------------+----------------------+----------------------+------------------------+--+
      | r1                  | pepe                 | perez                | NULL                   |
      | r2                  | NULL                 | montoya              | 1200                   |
      | r3                  | esteban              | NULL                 | 1100                   |
      +---------------------+----------------------+----------------------+------------------------+--+
      3 rows selected (0.523 seconds)
      1: jdbc:hive2://hdplabmaster:2181/>
