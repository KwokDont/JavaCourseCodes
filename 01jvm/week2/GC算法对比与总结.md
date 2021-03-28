##Week2 Homework
#（必做）根据上述自己对于 1 和 2 的演示，写一段对于不同 GC 和堆内存的总结，提交到 GitHub。

1. 串行GC（Serial GC）
开启方式：启动参数使用 -XX:+UseSerialGC
GC算法：年轻代使用mark-copy(标记-复制)算法，老年代使用mark-sweep-compact(标记-清除-整理)算法
特点：会导致STW，不能充分利用多核cpu的计算资源，不管有多少个cpu，JVM在垃圾回收时只能使用单个核心。
适用场景：只适合几百MB堆内存的JVM，且是单核CPU的情况。

2. 并行GC
开启方式： Java8默认的GC算法，或添加启动参数 -XX:+UseParallelGC
GC算法：年轻代使用mark-copy(标记-复制)算法，老年代使用mark-sweep-compact(标记-清除-整理)算法
特点：会导致STW，但可以同时使用多个cpu内核进行垃圾回收