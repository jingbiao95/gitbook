`dbsize` 查看数据大小

`select` 2 选择2号数据库

`set A B`  设置A=B key-value对

`get A `获取A的值

`flushdb` 清除数据库

`keys * `获取所有key值

`FLUSHALL`  清除所有数据中的值

`EXISTs key` 判断key是否存在

`expire key 8 `设置key的过期时间

`ttl key` 查看key的剩余时间

`move key db` 移除db库中的当前key

`type key` 查看key的类型 

Redis是单线程，很快，基于内存操作，CPU不是redis的瓶颈，瓶颈是机器的内存和网络带宽。



官网查看redis命令 http://www.redis.cn/commands.html



**为什么Redis单线程很快**

redis是将所有数据放在内存中，所有单线程操作效率高，多线程（CPU上下文切换耗时最多），对于内存系统来说，没有上下文切换效率最高！多次读写都是一个CPU上的，内存就是最佳方案。





# 数据类型

## Redis-key



## String（字符串）

```
set key1 v1

append key1 v1 

strlen key1

incr  # 自增1

decr # 自减1

incrby view 10 #增加以10步长

decrby view 5 # 减少以10步长

# 字符串范围
getrange key1 0 3 #截取字符串[0,3]
getrange key1 0 -1 # 获取整个字符串
setrange key2 1 xx # 替换指定位置的字符串为xx

setex (set with expire) # 设置key的失效时间
setex key3 30 "hello"	#
setnx (set if not exist) # 不存在就设置（在分布式锁中会使用）
setnx

#####
mset # 批量设置
gset # 批量获取

MSETNX  #批量设置（不存在就设置） 一个原子操作。

# 对象
SET user:1 {name:zhangsan,age:3} #设置一个对象 值为json字符串来保存对象

  
GETSET k1 v1 # 先get后set  不存在key则返回null/存在则返回旧值；并设置新的值。
```



 

## List

基本的数据类型，列表

在redis 中，可以把list设置为栈，队列、阻塞队列

list的命令都是以`l`开头的

```
LPUSH list one #将一个或者多个值插入list开头（左）
LRANGE list	0 -1 #获取list中的整个值
LRANGE list 0 1 # 获取list区间中的值
RPUSH list rightr # 将一个或者多个值，插入到列表尾部（右）


##########
LPOP list# 移除list的第一个数据
RPOP list # 移除list的最后一个数据

LINDEX list 1# 通过下标获取list中的某一个值

LLEN list# 返列表长度

LREM list count val # 移除list中count个值为val的数据  


LTRIM list 1 2# 通过下标截取list[1,2] 并且list只剩下截取后的数据

RPOPLPUSH  list1  list2# 将list1的最后一个数据 移动到list2中


LSET list  0 item  # 指定下标的值替换成另外一个值，（下标必须存在）

LINSERT key BEFORE|BACK value1 value2  # 将列表中某个具体的value前或后插入value2

```







## Set

## Hash

## ZSet



## 三种特殊类型



