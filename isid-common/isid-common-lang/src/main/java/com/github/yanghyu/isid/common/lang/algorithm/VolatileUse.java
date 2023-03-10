package com.github.yanghyu.isid.common.lang.algorithm;

/**
 * volatile关键字使用
 *
 * @author yanghy
 * @since 2019-06-30
 */
public class VolatileUse {

    /*
     保持内存可见性
     内存可见性（Memory Visibility）：所有线程都能看到共享内存的最新状态。
     对volatile变量的写操作与普通变量的主要区别有两点：
　　（1）修改volatile变量时会强制将修改后的值刷新的主内存中。
　　（2）修改volatile变量后会导致其他线程工作内存中对应的变量值失效。因此，再读取该变量值的时候就需要重新从读取主内存中的值。
　　 通过这两个操作，就可以解决volatile变量的可见性问题。
     */

    /*
    volatile如何防止指令重排
    volatile关键字通过“内存屏障”来防止指令被重排序。
    为了实现volatile的内存语义，编译器在生成字节码时，会在指令序列中插入内存屏障来禁止特定类型的处理器重排序。
    然而，对于编译器来说，发现一个最优布置来最小化插入屏障的总数几乎不可能，为此，Java内存模型采取保守策略。
    下面是基于保守策略的JMM内存屏障插入策略：
    在每个volatile写操作的前面插入一个StoreStore屏障。
    在每个volatile写操作的后面插入一个StoreLoad屏障。
    在每个volatile读操作的后面插入一个LoadLoad屏障。
    在每个volatile读操作的后面插入一个LoadStore屏障。
     */

    /*
    对变量的写操作不依赖当前值；
    该变量没有包含在具有其他变量的不变式中。
    volatile经常用于两个两个场景：状态标记、double check
     */

}
