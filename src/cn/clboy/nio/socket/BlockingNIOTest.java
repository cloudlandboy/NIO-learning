package cn.clboy.nio.socket;

import org.junit.Test;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

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

public class BlockingNIOTest {

    /**
     * 客户端
     *
     * @throws Exception
     */
    @Test
    public void testClient() throws Exception {
        //获取网络通道
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 7001));

        //获取读取本地文件的通道
        FileChannel localFileChannel = FileChannel.open(Paths.get("resources", "1.jpg"), StandardOpenOption.READ);

        //分配指定大小的缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        //读取本地文件，并发送到服务端
        while (localFileChannel.read(byteBuffer) != -1) {
            byteBuffer.flip();
            socketChannel.write(byteBuffer);
            byteBuffer.clear();
        }

        //关闭通道
        localFileChannel.close();
        socketChannel.close();
    }


    /**
     * 服务端
     *
     * @throws Exception
     */
    @Test
    public void testServer() throws Exception {
        //获取服务端网络通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //绑定端口号
        serverSocketChannel.bind(new InetSocketAddress(7001));

        //获取本地文件存储通道
        FileChannel localFileChannel = FileChannel.open(
                Paths.get("resources/server/1.jpg"),
                StandardOpenOption.WRITE, StandardOpenOption.CREATE);

        //获取客户端连接的通道
        SocketChannel clientSocketChannel = serverSocketChannel.accept();

        //分配指定大小的缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        //接收客户端的数据，并保存到本地
        while (clientSocketChannel.read(buffer) != -1) {
            buffer.flip();
            localFileChannel.write(buffer);
            buffer.clear();
        }

        //关闭通道
        clientSocketChannel.close();
        localFileChannel.close();
        serverSocketChannel.close();
    }
}