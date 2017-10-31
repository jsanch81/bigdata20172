from pyspark.sql import SparkSession

spark = SparkSession.builder.appName("WordCount").getOrCreate()

sc = spark.sparkContext

text_file = sc.textFile("hdfs:///datasets/gutenberg-txt-es/*.txt")
counts = text_file.flatMap(lambda line: line.split(" ")) \
             .map(lambda word: (word, 1)) \
             .reduceByKey(lambda a, b: a + b)
counts.saveAsTextFile("hdfs:///user/emontoya/spout1")
