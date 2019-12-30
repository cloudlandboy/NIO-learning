package cn.clboy.nio;

import org.junit.Test;

import java.nio.Buffer;
import java.nio.ByteBuffer;

/**
 * @Author cloudlandboy
 * @Date 2019/12/28 下午6:44
 * @Since 1.0.0
 * 缓冲区（Buffer）：在 Java NIO 中负责数据的存取。缓冲区就是数组。用于存储不同数据类型的数据
 * 根据数据类型不同（boolean 除外），提供了相应类型的缓冲区：(ByteBuffer,CharBuffer,ShortBuffer,IntBuffer,LongBuffer,FloatBuffer,DoubleBuffer)
 */
public class BufferTest {

    /**
     * 获取缓存区
     *
     * @throws Exception
     */
    @Test
    public void test01() throws Exception {
        //通过 allocate() 获取缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        this.print(byteBuffer);
    }

    /**
     * 利用 put() 存入数据到缓冲区中
     *
     * @throws Exception
     */
    @Test
    public void test02() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        byteBuffer.put("abc".getBytes());
        this.print(byteBuffer);
        byteBuffer.put("中".getBytes());
        this.print(byteBuffer);
    }

    /**
     * 切换读取数据模式
     *
     * @throws Exception
     */
    @Test
    public void test03() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        byteBuffer.put("abcde".getBytes());
        this.print(byteBuffer);
        //切换读取数据模式,将缓冲区的界限(limit)设置为当前位置(position)，并将当前位置重置为 0
        byteBuffer.flip();
        this.print(byteBuffer);
    }

    /**
     * 利用 get() 读取缓冲区中的数据
     *
     * @throws Exception
     */
    @Test
    public void test04() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        byteBuffer.put("abcde".getBytes());
        this.print(byteBuffer);
        //切换读取数据模式,将缓冲区的界限(limit)设置为当前位置(position)，并将当前位置重置为 0
        byteBuffer.flip();
        this.print(byteBuffer);
        //利用 get() 读取缓冲区中的数据
        byte[] b = new byte[byteBuffer.limit()];
        byteBuffer.get(b);
        System.out.println(new String(b));
        this.print(byteBuffer);
    }

    /**
     * rewind() : 可重复读
     *
     * @throws Exception
     */
    @Test
    public void test05() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        byteBuffer.put("abcde".getBytes());
        //切换读取数据模式,将缓冲区的界限(limit)设置为当前位置(position)，并将当前位置重置为 0
        byteBuffer.flip();
        //利用 get() 读取缓冲区中的数据
        byte[] b = new byte[byteBuffer.limit()];
        byteBuffer.get(b);
        System.out.println(new String(b));
        this.print(byteBuffer);
        byteBuffer.rewind();
        this.print(byteBuffer);
    }

    /**
     * clear() : 清空缓冲区. 但是缓冲区中的数据依然存在，但是处于“被遗忘”状态
     *
     * @throws Exception
     */
    @Test
    public void test06() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        byteBuffer.put("abcde".getBytes());
        //切换读取数据模式,将缓冲区的界限(limit)设置为当前位置(position)，并将当前位置重置为 0
        byteBuffer.flip();
        //利用 get() 读取缓冲区中的数据
        byte[] b = new byte[byteBuffer.limit()];
        byteBuffer.get(b);
        System.out.println(new String(b));
        this.print(byteBuffer);
        byteBuffer.rewind();
        this.print(byteBuffer);
        byteBuffer.clear();
        this.print(byteBuffer);
        System.out.println((char) byteBuffer.get());
        System.out.println((char) byteBuffer.get());
        System.out.println((char) byteBuffer.get());
        System.out.println((char) byteBuffer.get());
        System.out.println((char) byteBuffer.get());
        System.out.println(byteBuffer.get());
    }

    /**
     * mark() : 标记
     * reset() : 恢复到 mark 的位置
     *
     * @throws Exception
     */
    @Test
    public void test07() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        byteBuffer.put("abcde".getBytes());
        byteBuffer.flip();

        byte[] bytes = new byte[byteBuffer.limit()];
        byteBuffer.get(bytes, 0, 2);
        System.out.println(byteBuffer.position());
        //标记当前position位置
        byteBuffer.mark();
        System.out.println(new String(bytes));

        //再读取两个
        byteBuffer.get(bytes, byteBuffer.position(), 2);
        System.out.println(byteBuffer.position());
        System.out.println(new String(bytes));

        //reset() : 恢复到 mark 的位置
        byteBuffer.reset();
        System.out.println(byteBuffer.position());
        System.out.println((char) byteBuffer.get());
    }

    /**
     * hasRemaining()：判断缓冲区中是否还有剩余数据
     * remaining()：获取缓冲区中可以操作的数量
     *
     * @throws Exception
     */
    @Test
    public void test08() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        byteBuffer.put("abcde".getBytes());

        System.out.println(byteBuffer.hasRemaining());
        System.out.println(byteBuffer.remaining());


        byteBuffer.put("12345".getBytes());
        System.out.println(byteBuffer.hasRemaining());
        System.out.println(byteBuffer.remaining());
    }

    public void print(Buffer buffer) {
        //表示 Buffer 最大数据容量
        System.out.println("capacity：" + buffer.capacity());

        //界限，表示缓冲区中可以操作数据的大小
        System.out.println("limit：" + buffer.limit());

        //位置，表示缓冲区中正在操作数据的位置
        System.out.println("position：" + buffer.position());
    }

}