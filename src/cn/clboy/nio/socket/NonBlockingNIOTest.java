package cn.clboy.nio.socket;

import org.junit.Test;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

/**
 * @Author cloudlandboy
 * @Date 2019/12/29 下午9:14
 * @Since 1.0.0
 * 一、使用 NIO 完成网络通信的三个核心：
 * 1. 通道（Channel）：负责连接
 * java.nio.channels.Channel 接口：
 * - SelectableChannel
 * - - SocketChannel
 * - - ServerSocketChannel
 * - - DatagramChannel
 * <p>
 * - - Pipe.SinkChannel
 * - - Pipe.SourceChannel
 * 2. 缓冲区（Buffer）：负责数据的存取
 * 3. 选择器（Selector）：是 SelectableChannel 的多路复用器。用于监控 SelectableChannel 的 IO 状况
 */
public class NonBlockingNIOTest {

    /**
     * 选择器
     * 选择器（Selector） 是 SelectableChannle 对象的多路复用器
     * Selector 可以同时监控多SelectableChannel 的 IO 状况
     * 也就是说，利用 Selector 可使一个单独的线程管理多个 Channel。Selector 是非阻塞 IO 的核心。
     *
     * @throws Exception
     */
    @Test
    public void testSelector() throws Exception {
        //获取socket网络通道
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 80));

        //创建 Selector ：通过调用 Selector.open() 方法创建一个 Selector。
        Selector selector = Selector.open();

        //将SocketChannel切换到非阻塞模式
        socketChannel.configureBlocking(false);

        //向选择器注册通道：
        SelectionKey selectionKey = socketChannel.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ);
    }

    /**
     * 客户端
     *
     * @throws Exception
     */
    @Test
    public void testClient() throws Exception {
        //获取通道
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 7001));

        //切换到非阻塞模式
        socketChannel.configureBlocking(false);

        //分配指定大小的缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        //创建标准输入流
        Scanner scanner = new Scanner(System.in);
        System.out.println("键入您的昵称：");
        String name = scanner.nextLine();
        System.out.println("OK，开始聊天吧！");
        while (scanner.hasNext()) {
            String info = scanner.nextLine();
            if ("quit".equals(info)) {
                break;
            }
            String msg = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now()) + "：\n" + name + "：" + info;
            buffer.put(msg.getBytes());
            buffer.flip();
            socketChannel.write(buffer);
            buffer.clear();
        }

        //关闭通道
        socketChannel.close();
    }

    /**
     * 服务端
     *
     * @throws Exception
     */
    @Test
    public void testServer() throws Exception {
        //获取通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(7001));

        //切换到非阻塞模式
        serverSocketChannel.configureBlocking(false);

        //获取选择器
        Selector selector = Selector.open();

        //将通道注册到选择器，并且指定“监听接收事件”
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        //轮询式的获取选择器上已经“准备就绪”的事件
        while (selector.select() > 0) {
            //获取当前选择器中所有注册的“选择键(已就绪的监听事件)”
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> it = selectionKeys.iterator();
            while (it.hasNext()) {
                //获取准备“就绪”的事件
                SelectionKey sk = it.next();

                // 判断具体是什么事件准备就绪
                if (sk.isAcceptable()) {
                    //若“接收就绪”，获取客户端连接
                    SocketChannel socketChannel = serverSocketChannel.accept();

                    //切换到非阻模式
                    socketChannel.configureBlocking(false);

                    //将该通道注册到选择器上
                    socketChannel.register(selector, SelectionKey.OP_READ);
                } else if (sk.isReadable()) {
                    //获取当前选择器上“读就绪”状态的通道
                    SocketChannel socketChannel = (SocketChannel) sk.channel();

                    //分配缓冲区
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    //读取数据,客户端没有关闭，不能判断是否-1,返回的可能是0
                    while (socketChannel.read(buffer) > 0) {
                        buffer.flip();
                        System.out.println(new String(buffer.array(), 0, buffer.limit()));
                        buffer.clear();
                    }

                }

                //用完之后取消选择键 SelectionKey，不然下一次循环这个已经就绪的SelectionKey还存在
                it.remove();
            } //end while
        }

    }
}