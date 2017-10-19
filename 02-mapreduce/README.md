# Unidad 3: Big Data - MAP/REDUCE
## ST0263 - Tópicos Especiales en Telemática
## Ingeniería de Sistemas
## Universidad EAFIT
### Profesor: Edwin Montoya M. – emontoya@eafit.edu.co
## 2017-2

# Reto para hacer:

[Reto MapReduce](retomapreduce.md) - REALIZAR AL MENOS UN RETO.

# Laboratorio de MapReduce

## (1) WordCount en Java

Tomado de: https://hadoop.apache.org/docs/r2.7.3/hadoop-mapreduce-client/hadoop-mapreduce-client-core/MapReduceTutorial.html

* Contador de palabras en archivos texto en Java

* Se tiene el programa ejemplo: WordCount.java, el cual despues de compilarse en la version hadoop 2.7.3, genera un jar wc.jar, el cual será el que finalmente se ejecute.

* Descargarlo, compilarlo y generar el jar (wc.jar)

[script-compilacion-jar](wc-gen-jar.sh)

>		user@master$ cd 02-mapreduce
>		user@master$ sh wc-gen-jar.sh

### Para ejecutar:

>		user@master$ hadoop jar wc.jar WordCount hdfs:///datasets/gutenberg-txt-es/*.txt hdfs:///user/<username>/data_out1

(puede tomar varios minutos)

* el comando hadoop se este abandonando por yarn:

>		user@master$ yarn jar wc.jar WordCount hdfs:///datasets/gutenberg-txt-es/19*.txt hdfs:///user/<username>/data_out2

``` Java
    //
    // WordCount.java
    //
    import java.io.IOException;
    import java.util.StringTokenizer;

    import org.apache.hadoop.conf.Configuration;
    import org.apache.hadoop.fs.Path;
    import org.apache.hadoop.io.IntWritable;
    import org.apache.hadoop.io.Text;
    import org.apache.hadoop.mapreduce.Job;
    import org.apache.hadoop.mapreduce.Mapper;
    import org.apache.hadoop.mapreduce.Reducer;
    import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
    import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

    public class WordCount {

      public static class TokenizerMapper
           extends Mapper<Object, Text, Text, IntWritable>{

        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();

        public void map(Object key, Text value, Context context
                        ) throws IOException, InterruptedException {
          StringTokenizer itr = new StringTokenizer(value.toString());
          while (itr.hasMoreTokens()) {
            word.set(itr.nextToken());
            context.write(word, one);
          }
        }
      }

      public static class IntSumReducer
           extends Reducer<Text,IntWritable,Text,IntWritable> {
        private IntWritable result = new IntWritable();

        public void reduce(Text key, Iterable<IntWritable> values,
                           Context context
                           ) throws IOException, InterruptedException {
          int sum = 0;
          for (IntWritable val : values) {
            sum += val.get();
          }
          result.set(sum);
          context.write(key, result);
        }
      }

      public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "word count");
        job.setJarByClass(WordCount.class);
        job.setMapperClass(TokenizerMapper.class);
        job.setCombinerClass(IntSumReducer.class);
        job.setReducerClass(IntSumReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
      }
    }
```
## (2) WordCount en python

* Hay varias librerias de python para acceder a servicios MapReduce en Hadoop

* Se usará MRJOB (https://pythonhosted.org/mrjob/)

* Se puede emplear una version de python 2.x o 3.x, del sistema (como root) o con un manejador de versiones de node (pyenv o virtualenv).

* Como parte del sistema, se instalará mrjob así:

>		user@master$ sudo yum install python-pip
>		user@master$ sudo pip install --upgrade pip
>		user@master$ sudo pip install mrjob

* Si utilizará un manejador de versiones de python, puede ser así:

primero instalar pyenv (https://github.com/pyenv/pyenv-installer)

### EL CLUSTER DE LA MATERIA, YA TIENE INSTALADO MRJOB, LAS SIGUIENTES INSTRUCCIONES ES POR SI UD DESEA INSTALAR SU PROPIO CLUSTER O CONFIGURAR SU SANDBOX:

>		user@master$ curl -L https://raw.githubusercontent.com/pyenv/pyenv-installer/master/bin/pyenv-installer | bash
>		user@master$ pyenv update
>		user@master$ pyenv install 2.7.13
>		user@master$ pyenv local 2.7.13
>		user@master$ pip install mrjob

* Probar mrjob python local:

>		user@master$ cd 02-mapreduce
>		user@master$ python wordcount-mr.py /datasets/gutenberg-txt-es/1*.txt

* Ejecutar mrjob python en Hadoop con datos en hdfs:

>		user@master$ python wordcount-mr.py hdfs:///datasets/gutenberg-txt-es/*.txt -r hadoop --output-dir hdfs:///user/<username>/data_out1

* HORTONWORKS 2.5 SANDBOX (local o en azure), algunas veces puede sacar error de falta de la librearia hadoop-streaming.jar:

* ver: (http://wiktorski.github.io/blog/using-mrjob-with-hortonworks-sandbox/)

      user@master$ export HADOOP_HOME=/usr/hdp/current/hadoop-client
      user@master$ cp /usr/hdp/current/hadoop-mapreduce-client/hadoop-streaming.jar $HADOOP_HOME
      user@master$ python wordcount-mr.py hdfs:///user/<username>/gutenberg/470*.txt -r hadoop --output-dir hdfs:///user/<username>/data_out1 --hadoop-streaming-jar $HADOOP_HOME/hadoop-streaming.jar

* el directorio 'data_out1' no puede existir)
