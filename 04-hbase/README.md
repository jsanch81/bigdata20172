# Unidad 3: Big Data - HBASE
## ST0263 - Tópicos Especiales en Telemática
## Ingeniería de Sistemas
## Universidad EAFIT
### Profesor: Edwin Montoya M. – emontoya@eafit.edu.co
## 2017-2

# HBASE DESDE EL cli

1. Conectarse al cluster Hadoop (192.168.10.75)

entrar al cluster:

```
$ ssh username@192.168.10.75
username@192.168.10.75's password: ******
[username@hdplabmaster ~]$
```

2. Comandos interactivos

```
      $ hbase shell

      hbase(main):001:0> help

      hbase(main):002:0> create ‘test’, ‘cf’

      hbase(main):003:0> list ‘test’

      // listar todas las TABLAS
      hbase(main):01x:0> list

      hbase(main):004:0>put 'test', 'row1', 'cf:a', 'val1'

      hbase(main):005:0> scan ‘test’

      hbase(main):006:0> get ‘test’, ‘row1’

      hbase(main):007:0>put 'test', 'row2', 'cf:b', 'val2'
      hbase(main):008:0>put 'test', 'row3', 'cf:a', 'val3'
      hbase(main):009:0>put 'test', 'row4', 'cf:b', 'val4'
      hbase(main):010:0>put 'test', 'row5', 'cf:a', 'val4'

      //listar todos los valores de una columna en una CF:

      hbase(main):011:0>scan ‘test', {COLUMNS => 'cf:a’}
```
3. Acceso a HBASE desde la API Java

ejemplo1: agregar datos a una tabla y leerlos.

[HBaseClient.java](src/hbase/HBaseClient.java)

compilar:

      $ cd 04-hbase/src
      $ javac -cp `hadoop classpath`:`hbase classpath` hbase/HBaseClient.java

Ejecutar:

      $ java -cp `hadoop classpath`:`hbase classpath` hbase.HBaseClient      

ejemplo2: crear una tabla

[HBaseCreateTable.java](src/hbase/HBaseCreateTable.java)

compilar:

      $ cd 04-hbase/src
      $ javac -cp `hadoop classpath`:`hbase classpath` hbase/HBaseCreateTable.java

Ejecutar:

      $ java -cp `hadoop classpath`:`hbase classpath` hbase.HBaseCreateTable <table_name>

4. Seguridad en HBASE

utiliza kerberos y un modelo de GRANTs:

por ejemplo, el usuario 'emontoya' quien tiene todos los privilegios. Hay un usuario 'edwinm67' que es un usuario normal.

'edwinm67' por defecto no tiene ningun acceso, y podria tener los acceso:

      'RWXCA'

      Read (R) - can read data at the given scope

      Write (W) - can write data at the given scope

      Execute (X) - can execute coprocessor endpoints at the given scope

      Create (C) - can create tables or drop tables (even those they did not create) at the given scope

      Admin (A) - can perform cluster operations such as balancing the cluster or assigning regions at the given scope

Scope:

      Superuser - superusers can perform any operation available in HBase, to any resource. The user who runs HBase on your cluster is a superuser, as are any principals assigned to the configuration property hbase.superuser in hbase-site.xml on the HMaster.

      Global - permissions granted at global scope allow the admin to operate on all tables of the cluster.

      Namespace - permissions granted at namespace scope apply to all tables within a given namespace.

      Table - permissions granted at table scope apply to data or metadata within a given table.

      ColumnFamily - permissions granted at ColumnFamily scope apply to cells within that ColumnFamily.

      Cell - permissions granted at cell scope apply to that exact cell coordinate (key, value, timestamp). This allows for policy evolution along with data.

(http://hbase.apache.org/book.html#_securing_access_to_your_data)

Asignar permisos:

      hbase(main):000:0> grant <user> <permissions> <table> [ <column family> [ <column qualifier> ] ]
      hbase(main):000:0> grant <@group> <permissions> <table> [ <column family> [ <column qualifier> ] ]

Remover permisos:

      hbase(main):000:0> revoke <user> <table> [ <column family> [ <column qualifier> ] ]

Alterar permisos:

      hbase(main):000:0> alter 'tablename', {OWNER => 'username'}

Permisos de usuarios:

      hbase(main):000:0> user_permission <table>

# Acceso a Hbase via Hive

[hive-hbase](hive-hbase.md)
