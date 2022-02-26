## ThreadLocal取不到值的两种原因

### 1.两种原因

- 第一种，也是最常见的一种，就是多个线程使用ThreadLocal
- 第二种，**类加载器不同**造成取不到值，本质原因就是不同类加载器造成多个ThreadLocal对象





## 参考文献

https://blog.csdn.net/TheThirdMoon/article/details/109624711

https://cloud.tencent.com/developer/article/1563309