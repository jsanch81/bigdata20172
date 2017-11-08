# Unidad 3: Big Data - HIVE
## ST0263 - Tópicos Especiales en Telemática
## Ingeniería de Sistemas
## Universidad EAFIT
### Profesor: Edwin Montoya M. – emontoya@eafit.edu.co
## 2017-2

# TABLAS SENCILLAS EN HIVE

1. Se usarán los datos de Human Development Report (HDR) (http://hdr.undp.org/en/statistics/data)

o usar los datos en [datasets](datasets) de este repositorio.

2. Conectarse al cluster Hadoop (192.168.10.75)

entrar al cluster:

```
$ ssh username@192.168.10.75
username@192.168.10.75's password: ******
[username@hdplabmaster ~]$

// autentiquese con kerberos:

[username@hdplabmaster ~]$ kinit

ej:

[emontoya@hdplabmaster ~]$ kinit
Password for emontoya@DIS.EAFIT.EDU.CO: *****
[emontoya@hdplabmaster ~]$


```

3. copiar el archivo hdi-data.csv al HOME LOCAL del usuario:

```
/tmp/<username>/datasets
```

debe haber una archivo /tmp/<username>/datasets/hdi-data.csv para el trabajo a continuación.  

es importante que el directorio y archivos de datos, tengan acceso público a todos los usuarios, en especial el usuario 'hive'

4. ejecutar Hive:

```
$ beeline
beeline>
!connect jdbc:hive2://hdplabmaster:2181/;serviceDiscoveryMode=zooKeeper;zooKeeperNamespace=hiveserver2     
```

si va a trabajar en el cluster, se recomienda que cree una base de datos por usuario, y todos los comandos los use basados en esa base de datos.

```
beeline> CREATE DATABASE <username>;
beeline> USE <username>;

```


5. Crear la tabla HDI en Hive:
```
beeline> CREATE TABLE HDI (id INT, country STRING, hdi FLOAT, lifeex INT, mysch INT, eysch INT, gni INT) ROW FORMAT DELIMITED FIELDS TERMINATED BY ',' STORED AS TEXTFILE;
```

Nota: Esta tabla la crea en una BASE DE DATOS ‘default’
```
beeline> use default;
beeline> show tables;
beeline> describe hdi;
```

6. cargar los datos LOCALES a la tabla HDI:

// CUANDO SE CARGAN DATOS CON 'LOAD DATA' SE BORRAN LOS ARCHIVOS ORIGEN, POR LO CUAL DEBE CAMBIAR LOS PERMISOS LOCALES O REMOTOS con chmod 777 de los archivos.

// si es local:
```
$ chmod -R 777 /tmp/<username>/datasets/<dir>/*
```
// si estan en hdfs:
```
$ hdfs dfs -chmod -r 777 /user/<username>/datasets/<dir>/*
```

```
beeline> use <username>
beeline> load data local inpath '/tmp/<username>/datasets/hdi-data.csv' into table HDI;
```

7. hacer consultas y cálculos sobre la tabla HDI:
```
beeline> select * from hdi;
beeline>
beeline> select country, gni from hdi where gni > 2000;    
beeline>
```

## EJECUTAR UN JOIN CON HIVE:

1. Obtener los datos base: export-data.csv

usar los datos en 'datasets' de este repositorio.

2. Iniciar hive y crear la tabla EXPO:

```
beeline> CREATE TABLE EXPO (country STRING, expct FLOAT) ROW FORMAT DELIMITED FIELDS TERMINATED BY ‘,’ STORED AS TEXTFILE;
```

3. Carga los datos: export-data.csv
```
beeline>LOAD DATA LOCAL INPATH '/tmp/<username>/datasets/export-data.csv' INTO TABLE EXPO;
```

4. EJECUTAR EL JOIN DE 2 TABLAS:

// este comando esta fallando en el cluster 192.168.10.75. Funciona en su sandbox o en Zeppelin con el interprete '%spark2.sql', mire el lab de spark.

```
beeline> SELECT h.country, gni, expct FROM HDI h JOIN EXPO e ON (h.country = e.country) WHERE gni > 2000;
```
## WORDCOUNT EN HIVE:
```
beeline> use <username>
beeline>CREATE TABLE docs (line STRING);
```
// carga incremental desde HDFS
```
beeline>LOAD DATA INPATH '/user/<username>/datasets/gutenberg-txt-es/*.txt' INTO TABLE docs;
```
// carga desde cero  desde HDFS
```
beeline>LOAD DATA INPATH '/user/<username>/datasets/gutenberg-txt-es/*.txt' OVERWRITE INTO TABLE docs;
```
// carga incremental desde LOCAL
```
beeline>LOAD DATA LOCAL INPATH '/tmp/<username>/datasets/gutenberg-txt-es/*.txt' INTO TABLE docs;
```
// carga desde cero  desde LOCAL
```
beeline>LOAD DATA LOCAL INPATH '/tmp/<username>/datasets/gutenberg-txt-es/*.txt' OVERWRITE INTO TABLE docs;
```

// ordenado por palabra

// este comando esta fallando en el cluster 192.168.10.75. Funciona en su sandbox o en Zeppelin con el interprete '%spark2.sql', mire el lab de spark.

```
beeline>SELECT word, count(1) AS count FROM
    (SELECT explode(split(line,' ')) AS word FROM docs) w
    GROUP BY word
    ORDER BY word;
```
// ordenado por frecuencia de menor a mayor
```
beeline>SELECT word, count(1) AS count FROM
    (SELECT explode(split(line,' ')) AS word FROM docs) w
    GROUP BY word
    ORDER BY count;
```

RETO:

¿cómo llenar una tabla con los resultados de un Query? por ejemplo, como almacenar en una tabla el diccionario de frecuencia de palabras en el wordcount?

// TENER EN CUENTA LA CARGA LOCAL o DESDE EL HDFS de los DATOS a HIVE:

Carga desde archivos locales:
```
beeline>LOAD DATA LOCAL INPATH …
```
Carga desde archivos del HDFS:
```
beeline>LOAD DATA INPATH …
```

# Apache Sqoop

## Datos en MySQL

```
En 192.168.10.75, se tiene Mysql con:
Base de datos: “st0263”
Tabla: “username_employee”
User: st0263/3620ts
$ mysql –u st0263 –p
Enter password: ******
mysql> use st0263;
```


//creación de la tabla: username_employee (cambie username por su propio)
```
$ mysql -u st0263 -p
Enter password: 3620ts
mysql> use st0263;
mysql> create table <username>_employee (emp_id int, name varchar(50), salary int, primary key (emp_id));
```

//ingreso de datos
```
$ mysql -u st0263 -p
Enter password: 3620ts
mysql> use st0263;
mysql> insert into <username>_employee values (101, 'name1', 1800);
mysql> insert into <username>_employee values (102, 'name2', 1500);
mysql> insert into <username>_employee values (103, 'name3', 1000);
mysql> insert into <username>_employee values (104, 'name4', 2000);
mysql> insert into <username>_employee values (105, 'name5', 1600);
Query OK, 1 row affected (0.00 sec)
mysql> 
```
## Comandos Sqoop

//Transferir datos de una base de datos (tipo mysql) hacia HDFS:
```
$ sqoop import --connect jdbc:mysql://192.168.10.75:3306/st0263 --username st0263 -P --table username_employee --target-dir /user/<username>/mysqlOut -m 1
```

// listar archivos:
```
$ hadoop fs -ls /user/<username>/mysqlOut
```

// Crear tabla HIVE a partir de definición tabla Mysql:
```
$ sqoop create-hive-table --connect jdbc:mysql://192.168.10.75:3306/st0263 --username st0263 -P --table username_employee --hive-table username_emps --mysql-delimiters
```

// Transferir datos de una base de datos (tipo mysql) hacia HIVE vía HDFS:

```
$ sqoop import --connect jdbc:mysql://192.168.10.75:3306/st0263 --username st0263 -P --table username_employee --hive-import --hive-table username_emps -m 1
```

// Crear la tabla Hive Manualmente:
```
$ beeline
beeline> CREATE TABLE username_emps (empid INT, name  STRING, salary INT) ROW FORMAT DELIMITED FIELDS TERMINATED BY ','  LINES TERMINATED BY ‘\n’ STORED AS TEXTFILE;
beeline>
```
// Cargar datos a Hive Manualmente:
```
beeline> load data inpath '/user/<username>/mysqlOut/part-m-00000' into table username_emps;
OK                          
beeline> select * from username_emps;
OK
101 name1 1800
102 name2 1500
103 name3 1000
104 name4 2000
105 name5 1600
taken: 0.269 seconds, Fetched: 5 row(s) Time
beeline> 
```

//Sqoop export hacia mysql:

// Crear una Tabla ‘username_employee2’ en Mysql con los mismos atributos de ‘username_employee’
```
mysql> CREATE TABLE `st0263`.`username_employee2` (  `emp_id` INT NOT NULL,  `name` VARCHAR(45),  `salary` INT,  PRIMARY KEY (`emp_id`));
```

// Asumiendo datos separados por ”,” en HDFS en:

/user/username/mysql_in/*

```
$ sqoop export --connect jdbc:mysql://192.168.10.75:3306/st0263 --username st0263 -P --table username_employee2 --export-dir /user/<username>/mysql_in
```

# RETO:

## Compare el rendimiento del wordcount en hive vs mapreduce (en java o python) manteniendo el mismo dataset (gutenberg-txt-es)
