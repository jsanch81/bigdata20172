package hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;

public class HBaseListTables
{
  public static void main(String[] args) throws Exception
  {

    //content to HBase
    System.out.println( "Connecting..." );
    Configuration conf = HBaseConfiguration.create();
    conf.set("hbase.master","localhost:16000");

    HBaseConfiguration hconfig = new HBaseConfiguration(conf);
    HBaseAdmin hbase_admin = new HBaseAdmin( hconfig );

    System.out.println( "List Tables..." );

    // Getting all the list of tables using HBaseAdmin object
    HTableDescriptor[] tableDescriptor = hbase_admin.listTables();

    // printing all the table names.
    for (int i=0; i<tableDescriptor.length;i++ ){
       System.out.println(tableDescriptor[i].getNameAsString());
    }

    System.out.println("Done!");
  }
}
