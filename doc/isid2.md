# 互联网系统基础架构设计·二
[TOC]

## 二、基础设计
### 1 全局唯一编号
首先分析一下一个优秀的编号生成机制需要有哪些特点呢？
- 全局唯一性，这算是个基本要求，也是系统实现分布式的基础
- 空间占用小，可减少索引占用空间大小
- 高生成速率，能够适应高并发业务量场景
- 时序递增性，当使用编号建立索引时，有此性质的编号可以提高数据插入性能
- 难以遍历性，编号生成带有一定随机性，让攻击者很难进行遍历获取数据

针对上述分析，我们设计了一个三段结构共15字节的ID编号，基本可以满足上面提出的一些特点，具体实现参见下面代码中IdRandomizer.randomize()方法。
- 第一段，4字节，分钟时间戳，ID编号的时间有序性来源于此。
- 第二段，8字节，唯一编号，此处需要传入一个全局唯一的编号Number，ID编号的全局唯一性来源于此。高生成速率也依赖于此编号Number的生成速率。优秀的编号Number生成策略很多，我们这里推荐采用Leaf算法。
- 第三段，3字节，随机数，ID编号的随机性来源于此。3字节共约1678万种组合，可以有效防止恶意的爆破程序攻击。

这个15字节的编码经过base64编码后为长度20的字符串。但base64的编码字符的表示的值与字符在ascii码中顺序不一致，因此还要调整为正确的ascii码字符序。最后得到的20长度的串就是我们期望使用的编号。


```java
package com.github.yanghyu.gifu.foundation.core.id;

import com.github.yanghyu.gifu.foundation.core.util.Base64UrlEncodeAsciiOrderUtil;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

public class IdRandomizer {

    private static final Random random = new SecureRandom();

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
```
```java
package com.github.yanghyu.gifu.foundation.core.util;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;

/**
 * 将Base64UrlEncode的字符串转换为Ascii默认排序
 */
public class Base64UrlEncodeAsciiOrderUtil {

    private static final char[] toBase64URL = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_'
    };

    private static final char[] toBase64URLAsciiOrder = {
            '-',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            '_',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
    };

    private static final HashMap<Character, Character> map = new HashMap<>();

    static {
        for (int i = 0; i < 64; i++) {
            map.put(toBase64URL[i], toBase64URLAsciiOrder[i]);
        }
    }

    public static String convert(String base64UrlEncodeString) {
        if (StringUtils.isEmpty(base64UrlEncodeString)) {
            return base64UrlEncodeString;
        }
        StringBuilder ret = new StringBuilder();
        for (Character c : base64UrlEncodeString.toCharArray()) {
            Character v = map.get(c);
            if (v == null) {
                throw new IllegalArgumentException("存在非法字符：" + c);
            }
            ret.append(v);
        }
        return ret.toString();
    }

}
```
