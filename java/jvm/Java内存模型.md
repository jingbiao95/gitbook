Java内存模型是一种**规范**,Java虚拟机会实现这个规范



内存模型主要的内容：

1. Java内存模型的抽象结构
2. happen-before规则
3. 对volatile内存语义的探讨



# 1. Java内存模型的抽象结构

Java线程对内存数据进行交互的规范



线程之间的**共享变量**存储在**主内存**中,每个线程都有自己的私有**本地内存**,本地内存存储在该线程以读/写共享变量的副本。



本地内存是Java内存模型的抽象概念，并不是真实存在



![image-20210707093426795](C:\Users\jingbiao9502\AppData\Roaming\Typora\typora-user-images\image-20210707093426795.png)

Java内存模型规定：线程对变量的所有操作都必须在**本地内存**进行，**不能直接读写主内存**的变量



Java内存模型定义了8种操作来完成**变量如何从主内存到本地内存，以及如何从本地内存到主内存**

- read
- load
- use
- assgin
- store
- write
- lock
- unlock

![image-20210707093657126](C:\Users\jingbiao9502\AppData\Roaming\Typora\typora-user-images\image-20210707093657126.png)



# 2.happen-before

happen-before 实际也是一套**规则**。改规则是为了阐述**操作之间**的内存**可见性**



happen-before规则总共有8条

1. 传递性
2. volatile 变量规则
3. 程序顺序规则
4. 监视器锁的规则





改规则的目的是：在重要的场景下，这一组操作都不能进行重排序（指令重排），**前一个操作的结果对后续操作必须是可见的**。



# 3. volatile

volatile: 可见性和有序性（禁止重排序）

Java内存模型为了实现volatile有序性和可见性，定义了4种内存屏蔽的规范：

- LoadLoad
- LoadStore
- StoreLoad
- StoreStore



在volatile**前后**加上**内存屏障**，使得编译器和CPU无法进行重排序，致使有序，并且写volatile变量对其他线程可见。



Hotspot虚拟机的实现：在**汇编层面**上实际是通过Lock前缀指令来实现的，而不是各种fence指令(主要是简便，各大平台都支持lock,而fence指令是x86平台的)



lock指令能保证：禁止CPU和编译器的重排序（有序性）、保证CPU写核心的指令可以立即生效且其他核心的缓存数据失效（有序性）



---



**volatile和MESI协议的关系？**

没有直接关系

Java内存模型关注的是编程语言层面上，它是高纬度的抽象。

MESI是CPU缓存一致性协议，不同CPU架构都不一样，可能有的CPU没有MESI协议。



MESI相关介绍:

https://zhuanlan.zhihu.com/p/162099300

-----



Java内存模型的happen-before规则中对volatile变量规则的定义：

- 对一个volatile变量的写操作相对于后续对这个volatile变量的读操作可见
- 只要变量声明了volatile关键字，写后再读，读必须可见写的值（有序性、可见性）

