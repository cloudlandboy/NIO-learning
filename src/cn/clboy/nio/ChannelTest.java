package cn.clboy.nio;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @Author cloudlandboy
 * @Date 2019/12/28 下午8:59
 * @Since 1.0.0
 * <p>
 * 通道（Channel）：由 java.nio.channels 包定义的。Channel 表示 IO 源与目标打开的连接。
 * Channel 类似于传统的“流”。只不过 Channel 本身不能直接访问数据，Channel 只能与Buffer 进行交互。
 */

public class ChannelTest {

    /**
     * 利用通道完成文件的复制（非直接缓冲区）
     *
     * @throws Exception
     */
    @Test
    public void test01() throws Exception {
        long start = System.currentTimeMillis();

        FileInputStream in = new FileInputStream("resources/1.jpg");
        FileOutputStream out = new FileOutputStream("resources/1_copy.jpg");
        //创建缓存区
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        FileChannel inChannel = in.getChannel();
        FileChannel outChannel = out.getChannel();

        //从 Channel 读取数据到 Buffer
        while (inChannel.read(byteBuffer) != -1) {
            //切换读取数据的模式
            byteBuffer.flip();

            //将 Buffer 中数据写入 Channel
            outChannel.write(byteBuffer);

            //清空缓冲区
            byteBuffer.clear();
        }
        //关闭资源
        if (outChannel != null) outChannel.close();
        if (inChannel != null) inChannel.close();
        if (out != null) out.close();
        if (in != null) in.close();

        long end = System.currentTimeMillis();
        System.out.println("非直接缓冲区耗费时间：" + (end - start));
    }

    /**
     * 使用直接缓冲区完成文件的复制(内存映射文件)
     *
     * @throws Exception
     */
    @Test
    public void test02() throws Exception {
        long start = System.currentTimeMillis();

        //创建一个用于读的通道
        FileChannel inChannel = FileChannel.open(Paths.get("resources", "1.jpg"), StandardOpenOption.READ);

        //创建一个用于写的通道,因为下面获取的内存映射是读写模式所以这里也要开启读的模式，如果文件不存在还需要能够创建
        FileChannel outChannel = FileChannel.open(Paths.get("resources", "1_copy_2.jpg"), StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.CREATE);

        //内存映射文件
        MappedByteBuffer inMapBuffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
        MappedByteBuffer outMapBuffer = outChannel.map(FileChannel.MapMode.READ_WRITE, 0, inChannel.size());

        //直接对缓存区进行读写操作,不需要通过通道操作
        byte[] bytes = new byte[inMapBuffer.limit()];
        inMapBuffer.get(bytes);
        outMapBuffer.put(bytes);

        outChannel.close();
        inChannel.close();

        long end = System.currentTimeMillis();
        System.out.println("直接缓冲区耗费时间：" + (end - start));
    }

    /**
     * transferTo()和transferFrom()
     * 将数据从源通道传输到其他 Channel 中
     *
     * @throws Exception
     */
    @Test
    public void test03() throws Exception {
        FileChannel inChannel = FileChannel.open(Paths.get("resources", "1.jpg"), StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(Paths.get("resources", "1_copy_3.jpg"), StandardOpenOption.WRITE, StandardOpenOption.CREATE);

        inChannel.transferTo(0, inChannel.size(), outChannel);
        //outChannel.transferFrom(inChannel, 0, inChannel.size());

        outChannel.close();
        inChannel.close();
    }

    /**
     * 分散(Scatter)和聚集(Gather)
     * 分散读取（Scattering Reads）是指从 Channel 中读取的数据“分散”到多个 Buffer 中。
     * 聚集写入（Gathering Writes）是指将多个 Buffer 中的数据“聚集”到 Channel。
     *
     * @throws Exception
     */
    @Test
    public void test04() throws Exception {
        RandomAccessFile file = new RandomAccessFile("resources/a1.txt", "rw");
        //获取通道
        FileChannel inChannel = file.getChannel();

        //分配指定大小的缓存区
        ByteBuffer buffer1 = ByteBuffer.allocate(26);
        ByteBuffer buffer2 = ByteBuffer.allocate(102);

        //分散读取
        ByteBuffer[] buffers = {buffer1, buffer2};
        inChannel.read(buffers);

        //切换读取数据模式
        for (ByteBuffer buffer : buffers) {
            buffer.flip();
            //查看每个buffer读取的数据
            System.out.println(new String(buffer.array(), 0, buffer.limit()));
        }

        //聚集写入
        RandomAccessFile out = new RandomAccessFile("resources/a1_copy.txt", "rw");
        FileChannel outChannel = out.getChannel();
        outChannel.write(buffers);

        outChannel.close();
        inChannel.close();
        out.close();
        file.close();
    }

    /**
     * 字符集，编码与解码
     *
     * @throws Exception
     */
    @Test
    public void test05() throws Exception {
        //001.txt的编码为GBK
        FileChannel channel = FileChannel.open(Paths.get("resources", "001.txt"), StandardOpenOption.READ);
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        channel.read(byteBuffer);
        byteBuffer.flip();
        //输出乱码,默认是使用UTF-8解码
        System.out.println(new String(byteBuffer.array(), 0, byteBuffer.limit()));

        Charset gbk = Charset.forName("GBK");
        //使用GBK编码格式进行解码
        CharBuffer decode = gbk.decode(byteBuffer);
        System.out.println(String.valueOf(decode.array()));

        //使用GBK编码
        ByteBuffer byteBuffer02 = gbk.encode("我爱你，亲爱的姑娘。。。");
        //乱码
        System.out.println(new String(byteBuffer02.array(), 0, byteBuffer02.limit()));
        //使用GBK解码
        System.out.println(new String(byteBuffer02.array(), 0, byteBuffer02.limit(), gbk));
    }

}