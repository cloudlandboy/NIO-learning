package cn.clboy.nio;

import org.junit.Test;

import java.nio.ByteBuffer;

/**
 * @Author cloudlandboy
 * @Date 2019/12/28 下午8:30
 * @Since 1.0.0
 * <p>
 * 非直接缓冲区：通过 allocate() 方法分配缓冲区，将缓冲区建立在 JVM 的内存中
 * 直接缓冲区：通过 isDirect() 方法分配直接缓冲区，将缓冲区建立在物理内存中。可以提高效率
 */

public class BufferTypesTest {

    @Test
    public void test01() throws Exception {
        //非直接缓存区
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        System.out.println(byteBuffer.isDirect());
        //直接缓存区
        ByteBuffer directByteBuffer = ByteBuffer.allocateDirect(1024);
        System.out.println(directByteBuffer.isDirect());
    }
}