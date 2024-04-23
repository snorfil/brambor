import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class TotalSpentByUser {
    public static class TokenizerMapper extends Mapper<Object, Text, Text, FloatWritable>{
        private Text customer = new Text();

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String[] parts = value.toString().split(",");
            float amount = Float.parseFloat(parts[3]); // total_amount en la posición 3
            customer.set(parts[7]); // customer_id en la posición 7
            context.write(customer, new FloatWritable(amount));
        }
    }

    public static class FloatSumReducer extends Reducer<Text,FloatWritable,Text,FloatWritable> {
        private FloatWritable result = new FloatWritable();

        public void reduce(Text key, Iterable<FloatWritable> values, Context context) throws IOException, InterruptedException {
            float sum = 0;
            for (FloatWritable val : values) {
                sum += val.get();
            }
            result.set(sum);
            context.write(key, result);
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Total spent by user");
        job.setJarByClass(TotalSpentByUser.class);
        job.setMapperClass(TokenizerMapper.class);
        job.setCombinerClass(FloatSumReducer.class);
        job.setReducerClass(FloatSumReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(FloatWritable.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}



hadoop jar TotalSpentByUser.jar [vuestro_nombre_apellidos]/input [vuestro_nombre_apellidos]/output


