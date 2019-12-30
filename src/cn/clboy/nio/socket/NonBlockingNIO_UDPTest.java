package cn.clboy.nio.socket;

import org.junit.Test;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Scanner;

/**
 * @Author cloudlandboy
 * @Date 2019/12/29 下午10.05
 * @Since 1.0.0
 * 接收服务端的响应
 */

public class NonBlockingNIO_UDPTest {

    /**
     * 发送端
     *
     * @throws Exception
     */
    @Test
    public void testSend() throws Exception {
        //获取网络通道
        DatagramChannel datagramChannel = DatagramChannel.open();

        //设置为非阻塞模式
        datagramChannel.configureBlocking(false);

        ByteBuffer buffer = ByteBuffer.allocate(1024);

        Scanner scanner = new Scanner(System.in);
        System.out.println("键入你的昵称：");
        String name = scanner.nextLine();
        System.out.println("OK,开始聊天吧!");
        while (scanner.hasNext()) {
            String info = scanner.next();
            if ("quit".equalsIgnoreCase(info)) {
                break;
            }
            String msg = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now()) + "：\n" + name + "：" + info;
            buffer.put(msg.getBytes());
            buffer.flip();
            datagramChannel.send(buffer, new InetSocketAddress("127.0.0.1", 7001));
            buffer.clear();
        }

        datagramChannel.close();
    }

    /**
     * 接收端
     *
     * @throws Exception
     */
    @Test
    public void testReceive() throws Exception {
        DatagramChannel datagramChannel = DatagramChannel.open();
        datagramChannel.bind(new InetSocketAddress(7001));

        //设置为非阻塞模式
        datagramChannel.configureBlocking(false);

        //创建选择器
        Selector selector = Selector.open();


        //注册到选择器
        datagramChannel.register(selector, SelectionKey.OP_READ);
        while (selector.select() > 0) {
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while (it.hasNext()) {
                SelectionKey sk = it.next();
                if (sk.isReadable()) {
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    datagramChannel.receive(buffer);
                    buffer.flip();
                    System.out.println(new String(buffer.array(), 0, buffer.limit()));
                    buffer.clear();
                }
            }
            it.remove();
        }
    }
}