package cn.clboy.nio.socket;

import org.junit.Test;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @Author cloudlandboy
 * @Date 2019/12/29 下午9:47
 * @Since 1.0.0
 * 接收服务端的响应
 */

public class BlockingNIOTest02 {

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


        socketChannel.shutdownOutput();
        //接收服务端的反馈
        int len = 0;
        while ((len = socketChannel.read(byteBuffer)) != -1) {
            byteBuffer.flip();
            System.out.println(new String(byteBuffer.array(), 0, len));
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

        //发送反馈消息给客户端
        buffer.put("服务端成功接收".getBytes());
        buffer.flip();
        clientSocketChannel.write(buffer);

        //关闭通道
        clientSocketChannel.close();
        localFileChannel.close();
        serverSocketChannel.close();
    }
}