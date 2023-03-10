package com.github.yanghyu.isid.common.lang.algorithm;

/**
 * 单例设计模式
 *
 * @author yanghongyu
 * @since 2019-06-30
 */

public class Singleton {

    /**
     * instance = new Singleton();
     * 它并不是一个原子操作。事实上，它可以”抽象“为下面几条JVM指令：
     * <p>
     * memory = allocate();    //1：分配对象的内存空间
     * initInstance(memory);   //2：初始化对象
     * instance = memory;      //3：设置instance指向刚分配的内存地址
     * 上面操作2依赖于操作1，但是操作3并不依赖于操作2，所以JVM可以以“优化”为目的对它们进行重排序，经过重排序后如下：
     * <p>
     * memory = allocate();    //1：分配对象的内存空间
     * instance = memory;      //3：设置instance指向刚分配的内存地址（此时对象还未初始化）
     * ctorInstance(memory);   //2：初始化对象
     * 可以看到指令重排之后，操作 3 排在了操作 2 之前，即引用instance指向内存memory时，这段崭新的内存还没有初始化——即，
     * 引用instance指向了一个"被部分初始化的对象"。此时，如果另一个线程调用getInstance方法，由于instance已经指向了一块内存空间，
     * 从而if条件判为false，方法返回instance引用，用户得到了没有完成初始化的“半个”单例。
     * 解决这个该问题，只需要将instance声明为volatile变量
     */
    private static volatile Singleton instance;

    private Object value;

    /**
     * 私有化构造方法
     */
    private Singleton() {
    }

    /**
     * 获取实例对象
     *
     * @return 单例对象
     */
    public static Singleton getInstance() {
        // 当instance不为null时，仍可能指向一个“被部分初始化的对象”
        if (instance == null) {
            // 使用synchronized关键字将getInstance方法改为同步方法；但这样串行化的单例是不能忍的。
            // 所以我猿族前辈设计了DCL（Double Check Lock，双重检查锁）机制，使得大部分请求都不会进入阻塞代码块
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}
