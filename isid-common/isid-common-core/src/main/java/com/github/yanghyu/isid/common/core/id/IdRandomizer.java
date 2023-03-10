package com.github.yanghyu.isid.common.core.id;

import com.github.yanghyu.isid.common.core.util.Base64UrlEncodeAsciiOrderUtil;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

/**
 * 编号随机化器
 *
 * @author yanghongyu
 * @since 2023-02-13
 */
public class IdRandomizer {

    private static final Random random = new Random();

    private IdRandomizer() { }

    public static String randomize(long number) {
        // 为ID编号申请总长度为15个字节ByteBuffer。
        ByteBuffer idByteBuffer = ByteBuffer.allocate(15);

        // 获取当前时间的分钟数，强转为int类型后，四个字节共可表示时间范围是8378年。
        // 也就是可以表示1970～10348年之间的范围。
        int currentMinute  = (int) (System.currentTimeMillis() / 60000);
        // 第一段，4字节，分钟时间戳，ID编号的时间有序性来源于此。
        idByteBuffer.putInt(currentMinute);

        // 第二段，8字节，唯一编号，此处需要传入一个全局唯一的编号Number，ID编号的全局唯一性来源于此。高生
        // 成速率也依赖于此编号Number的生成速率。
        // 推荐使用美团LEAF算法进行生成，当然也可以采用其它算法，但必须保证全局唯一性。
        // 美团LEAF算法，参考https://tech.meituan.com/2017/04/21/mt-leaf.html 该算法具有降低数据
        // 库依赖，并提高生成速率的优点。
        // LEAF算法思想很好，但实现还可以更进一步优化，本人使用LinkedBlockingQueue对其进行了改写，主要
        // 思想是当队列中最前面的Segment中ID使用到一定程度时，就异步启动一个任务线程去加载一个新的Segment
        // 到队列末尾，这样改写后逻辑清晰易懂，也不易出现BUG。
        idByteBuffer.putLong(number);

        byte[] randomVar = new byte[3];
        random.nextBytes(randomVar);
        // 第三段，3字节，随机数，ID编号的随机性来源于此。3字节共约1678万种组合，可以有效防止恶意的爆破程序
        // 攻击。
        idByteBuffer.put(randomVar);

        // 编码为Base64UrlEncode格式，编码后的长度固定为20。
        String idBase64 = Base64.getUrlEncoder().encodeToString(idByteBuffer.array());
        // 但base64的编码字符的表示的值与字符在ascii码中顺序不一致，因此还要调整为正确的ascii码字符序。
        return Base64UrlEncodeAsciiOrderUtil.convert(idBase64);
    }

}
