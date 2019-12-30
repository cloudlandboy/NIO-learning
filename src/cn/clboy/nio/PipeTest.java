package cn.clboy.nio;

import org.junit.Test;
import sun.nio.ch.ThreadPool;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.*;

/**
 * @Author cloudlandboy
 * @Date 2019/12/30 下午7:58
 * @Since 1.0.0
 */

public class PipeTest {

    public static void main(String[] args) throws Exception {
        Pipe pipe = Pipe.open();

        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(2, 2, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), threadFactory);
        //线程A 将数据写到sink通道
        threadPool.execute(() -> {
            Pipe.SinkChannel sinkChannel = pipe.sink();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            try {
                int i = 0;
                while (i < 10) {
                    Thread.sleep(1000);
                    buffer.put(formatter.format(LocalDateTime.now()).getBytes());
                    buffer.flip();
                    sinkChannel.write(buffer);
                    buffer.clear();
                    i++;
                }
                sinkChannel.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        //线程B 从source通道读取数据
        threadPool.execute(() ->

        {
            Pipe.SourceChannel sourceChannel = pipe.source();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            try {
                while (sourceChannel.read(buffer) > 0) {
                    buffer.flip();
                    System.out.println(new String(buffer.array(), 0, buffer.limit()));
                    buffer.clear();
                }
                sourceChannel.close();
                threadPool.shutdown();
            } catch (Exception e) {
            }
        });
    }

}