package hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;

public class HBaseCreateTable
{
  public static void main(String[] args) throws Exception
  {

    //content to HBase
    System.out.println( "Connecting..." );
    Configuration conf = HBaseConfiguration.create();
    conf.set("hbase.master","localhost:16000");

    HBaseConfiguration hconfig = new HBaseConfiguration(conf);
    HBaseAdmin hbase_admin = new HBaseAdmin( hconfig );

    HTableDescriptor htable = new HTableDescriptor(args[0]);
    htable.addFamily( new HColumnDescriptor("id"));
    htable.addFamily( new HColumnDescriptor("Name"));

    System.out.println( "Creating Table..." );
    hbase_admin.createTable( htable );
    System.out.println("Done!");
  }
}
