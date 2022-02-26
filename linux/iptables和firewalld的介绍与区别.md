## 一、iptables

### **iptables简介**

netfilter/iptables（简称为iptables）组成Linux平台下的**包过滤防火墙**，与大多数的Linux软件一样，这个包过滤防火墙是免费的，它可以代替昂贵的商业防火墙解决方案，完成**封包过滤**、**封包重定向**和**网络地址转换（NAT）**等功能。

### **iptables基础**

规则（rules）其实就是网络管理员预定义的条件，规则一般的定义为“如果数据包头符合这样的条件，就这样处理这个数据包”。规则存储在内核空间的信息 包过滤表中，这些规则分别指定了源地址、目的地址、传输协议（如TCP、UDP、ICMP）和服务类型（如HTTP、FTP和SMTP）等。当数据包与规 则匹配时，iptables就根据规则所定义的方法来处理这些数据包，如放行（accept）、拒绝（reject）和丢弃（drop）等。配置防火墙的 主要工作就是添加、修改和删除这些规则。

### **iptables和netfilter的关系：**

这是第一个要说的地方，Iptables和netfilter的关系是一个很容易让人搞不清的问题。很多的知道iptables却不知道 netfilter。其实iptables只是Linux防火墙的**管理工具**而已，位于/sbin/iptables。真正实现防火墙功能的是 netfilter，它是Linux内核中实现包过滤的内部结构。

### **iptables传输数据包的过程**

① 当一个数据包进入网卡时，它首先进入PREROUTING链，内核根据数据包目的IP判断是否需要转送出去。
② 如果数据包就是进入本机的，它就会沿着图向下移动，到达INPUT链。数据包到了INPUT链后，任何进程都会收到它。本机上运行的程序可以发送数据包，这些数据包会经过OUTPUT链，然后到达POSTROUTING链输出。
③ 如果数据包是要转发出去的，且内核允许转发，数据包就会如图所示向右移动，经过FORWARD链，然后到达POSTROUTING链输出。

![在这里插入图片描述](https://raw.githubusercontent.com/jingbiao95/Images/main/20200407195834496.png)

#### **iptables的规则表和链：**

表（tables）提供特定的功能，iptables内置了4个表，即filter表、nat表、mangle表和raw表，分别用于实现包过滤，网络地址转换、包重构(修改)和数据跟踪处理。

链（chains）是数据包传播的路径，每一条链其实就是众多规则中的一个检查清单，每一条链中可以有一 条或数条规则。当一个数据包到达一个链时，iptables就会从链中第一条规则开始检查，看该数据包是否满足规则所定义的条件。如果满足，系统就会根据 该条规则所定义的方法处理该数据包；否则iptables将继续检查下一条规则，如果该数据包不符合链中任一条规则，iptables就会根据该链预先定 义的默认策略来处理数据包。

Iptables采用“表”和“链”的分层结构。在REHL4中是三张表五个链。现在REHL5成了四张表五个链了，不过多出来的那个表用的也不太多，所以基本还是和以前一样。下面罗列一下这四张表和五个链。注意一定要明白这些表和链的关系及作用。

![在这里插入图片描述](https://raw.githubusercontent.com/jingbiao95/Images/main/202004071958545.png)

##### **规则表：**

1.filter表——三个链：INPUT、FORWARD、OUTPUT
作用：过滤数据包 内核模块：iptables_filter.
2.Nat表——三个链：PREROUTING、POSTROUTING、OUTPUT
作用：用于网络地址转换（IP、端口） 内核模块：iptable_nat
3.Mangle表——五个链：PREROUTING、POSTROUTING、INPUT、OUTPUT、FORWARD
作用：修改数据包的服务类型、TTL、并且可以配置路由实现QOS内核模块：iptable_mangle(别看这个表这么麻烦，咱们设置策略时几乎都不会用到它)
4.Raw表——两个链：OUTPUT、PREROUTING
作用：决定数据包是否被状态跟踪机制处理 内核模块：iptable_raw
(这个是REHL4没有的，不过不用怕，用的不多)

##### **规则链：**

1.INPUT——进来的数据包应用此规则链中的策略
2.OUTPUT——外出的数据包应用此规则链中的策略
3.FORWARD——转发数据包时应用此规则链中的策略
4.PREROUTING——对数据包作路由选择前应用此链中的规则
（记住！所有的数据包进来的时侯都先由这个链处理）
5.POSTROUTING——对数据包作路由选择后应用此链中的规则
（所有的数据包出来的时侯都先由这个链处理）

##### **规则表之间的优先顺序：**

Raw——mangle——nat——filter
规则链之间的优先顺序（分三种情况）：

第一种情况：入站数据流向

从外界到达防火墙的数据包，先被PREROUTING规则链处理（是否修改数据包地址等），之后会进行路由选择（判断该数据包应该发往何处），如果数据包 的目标主机是防火墙本机（比如说Internet用户访问防火墙主机中的web服务器的数据包），那么内核将其传给INPUT链进行处理（决定是否允许通 过等），通过以后再交给系统上层的应用程序（比如Apache服务器）进行响应。

第二冲情况：转发数据流向

来自外界的数据包到达防火墙后，首先被PREROUTING规则链处理，之后会进行路由选择，如果数据包的目标地址是其它外部地址（比如局域网用户通过网 关访问QQ站点的数据包），则内核将其传递给FORWARD链进行处理（是否转发或拦截），然后再交给POSTROUTING规则链（是否修改数据包的地 址等）进行处理。

第三种情况：出站数据流向
防火墙本机向外部地址发送的数据包（比如在防火墙主机中测试公网DNS服务器时），首先被OUTPUT规则链处理，之后进行路由选择，然后传递给POSTROUTING规则链（是否修改数据包的地址等）进行处理。
![在这里插入图片描述](https://raw.githubusercontent.com/jingbiao95/Images/main/2020040719593433.png)
![在这里插入图片描述](https://raw.githubusercontent.com/jingbiao95/Images/main/20200407195949642.png)

### **管理和设置iptables规则**

iptables的基本语法格式

`iptables [-t 表名] 命令选项 ［链名］ ［条件匹配］ ［-j 目标动作或跳转］`
说明：表名、链名用于指定 iptables命令所操作的表和链，命令选项用于指定管理iptables规则的方式（比如：插入、增加、删除、查看等；条件匹配用于指定对符合什么样 条件的数据包进行处理；目标动作或跳转用于指定数据包的处理方式（比如允许通过、拒绝、丢弃、跳转（Jump）给其它链处理。

iptables命令的管理控制选项

```
-A 在指定链的末尾添加（append）一条新的规则 
-D 删除（delete）指定链中的某一条规则，可以按规则序号和内容删除 
-I 在指定链中插入（insert）一条新的规则，默认在第一行添加 
-R 修改、替换（replace）指定链中的某一条规则，可以按规则序号和内容替换 
-L 列出（list）指定链中所有的规则进行查看 
-E 重命名用户定义的链，不改变链本身 
-F 清空（flush） 
-N 新建（new-chain）一条用户自己定义的规则链 
-X 删除指定表中用户自定义的规则链（delete-chain） 
-P 设置指定链的默认策略（policy） 
-Z 将所有表的所有链的字节和数据包计数器清零 
-n 使用数字形式（numeric）显示输出结果 
-v 查看规则表详细信息（verbose）的信息 
-V 查看版本(version) 
-h 获取帮助（help）
```

#### 查看打开的端口

```
/etc/init.d/iptables status
iptables -nL
```

#### 打开某个端口

```shell
# 将tcp 8080 端口 插入到input链 末尾
iptables -A INPUT -p tcp --dport 8080 -j ACCEPT
# 打开49152~65534之间的端口
iptables -A INPUT -p tcp --dport 49152:65534 -j ACCEPT
```





#### **防火墙处理数据包的四种方式**

ACCEPT 允许数据包通过
DROP 直接丢弃数据包，不给任何回应信息
REJECT 拒绝数据包通过，必要时会给数据发送端一个响应的信息。
LOG在/var/log/messages文件中记录日志信息，然后将数据包传递给下一条规则

#### **iptables防火墙规则的保存与恢复**

iptables-save把规则保存到文件中，再由目录rc.d下的脚本（/etc/rc.d/init.d/iptables）自动装载

使用命令iptables-save来保存规则。一般用

```
iptables-save > /etc/sysconfig/iptables
```

生成保存规则的文件 /etc/sysconfig/iptables，

也可以用

```
service iptables save
```

它能把规则自动保存在/etc/sysconfig/iptables中。

当计算机启动时，rc.d下的脚本将用命令iptables-restore调用这个文件，从而就自动恢复了规则。





## 二、firewalld

**firewall 防火墙服务简述与安装**
1、Centos7 默认的防火墙是 firewall，替代了以前的 iptables
2、firewall 使用更加方便、功能也更加强大一些
3、firewalld 服务引入了一个信任级别的概念来管理与之相关联的连接与接口。它支持 ipv4 与 ipv6，并支持网桥，采用 firewall-cmd (command) 或 firewall-config (gui) 来动态的管理 kernel netfilter 的临时或永久的接口规则，并实时生效而无需重启服务。
4、查看 firewall 版本：`firewall-cmd --version`

```
[root@localhost ~]# firewall-cmd --version 0.4.4.4
```



firewall 防火墙安装

1）像使用 iptables 一样，firewall 同样需要安装
2）需要注意的是某些系统已经自带了 firewal l的，如果查看版本没有找到，则可以进行 yun 安装
3）安装指令：` yum install firewalld`

**firewalld 服务基本使用**
1、firewall 与 iptables 一样都是服务，所以可以使用 systemctl 服务管理工具来操作

| 查看防火墙状态                 | systemctl status firewalld          |
| ------------------------------ | ----------------------------------- |
| 关闭防火墙，停止 firewall 服务 | systemctl stop firewalld            |
| 开启防火墙，启动 firewall 服务 | systemctl start firewalld           |
| 重启防火墙，重启 firewall 服务 | systemctl restart firewalld         |
| 查看 firewall 服务是否开机启动 | systemctl is-enabled firewalld      |
| 开机时自动启动 firewall 服务   | systemctl enable firewalld.service  |
| 开机时自动禁用 firewall 服务   | systemctl disable firewalld.service |

**firewalld-cmd 防护墙命令使用**
1、上面所说的 firewall 可以看成整个防火墙服务，而 firewall-cmd 可以看成是其中的一个功能，可用来管理端口

（1）查看 firewall-cmd 状态，即查看 firewall 防火墙程序是否正在运行：` firewall-cmd --state`

```
[root@localhost ~]# firewall-cmd --state
running
[root@localhost ~]#
```

（2）查看已打开的所有端口，`firewall-cmd --zone=public --list-ports`

```
[root@localhost ~]# firewall-cmd --zone=public --list-ports
6379/tcp 22122/tcp 23000/tcp 8080/tcp 8888/tcp 9502/tcp 6662/tcp 9999/tcp 7002/tcp 6661/tcp 6688/tcp 6667/tcp 6689/tcp 8000/tcp 6663/tcp 9070/tcp 9089/tcp 9988/tcp 9222/tcp 4444/tcp
[root@localhost ~]#
```


（3）开启指定端口

1. 开启一个端口：`firewall-cmd --zone=public --add-port=80/tcp --permanent `（–permanent 永久生效，没有此参数重启后失效）
2. 重新加载 firewall，修改配置后，必须重新加载才能生效：`firewall-cmd --reload`

```
[root@localhost ~]# firewall-cmd --zone=public --list-port
9876/tcp 8090/tcp 80/tcp 8080/tcp
[root@localhost ~]# firewall-cmd --zone=public --add-port=3307/tcp --permanent
success
[root@localhost ~]# firewall-cmd --reload
success
[root@localhost ~]# firewall-cmd --zone=public --list-port
9876/tcp 8090/tcp 80/tcp 8080/tcp 3307/tcp
```



（4）关闭指定端口

1. 关闭 9876 端口：`firewall-cmd --zone=public --remove-port=9898/tcp --permanent`（–permanent 表示永久生效，没有此参数重启后失效）

2. 重新加载 firewall，修改配置后，必须重新加载才能生效：`firewall-cmd --reload`

   ```
   [root@localhost ~]# firewall-cmd --zone=public --list-ports 9876/tcp 8090/tcp 80/tcp 8080/tcp [root@localhost ~]# firewall-cmd --zone=public --remove-port=9876/tcp --permanent success [root@localhost ~]# firewall-cmd --reload success [root@localhost ~]# firewall-cmd --zone=public --list-ports 8090/tcp 80/tcp 8080/tcp
   ```

   **public.xml 文件修改防火墙端口**

- firewall-cmd对端口的操作，如开放端口等信息，都放在在"/etc/firewall/zones/public.xml"中记录所以直接修改此文件也是可以的

**注意事项**
1、如下所示，CentOS 7.2 Linux系统防火墙明明开启了指定的端口，tomcat服务器端口也指定正确，启动没有任何问题，最后从windows浏览器访问的时候，却只有80端口有效，其余的端口全部访问失败

2、最后原因居然是因为系统是阿里云服务器，而它的后台为了安全，封掉了其它端口的访问，所以即使防火墙修改了也没用，解决办法是登录阿里云服务器后台，修改它的安全组策略即可。这是生产上遇到的问题。

## 三、iptables和firewalld的区别

firewalld 与 iptables的比较：

1，firewalld可以动态修改单条规则，动态管理规则集，允许更新规则而不破坏现有会话和连接。而iptables，在修改了规则后必须得全部刷新才可以生效；

2，firewalld使用区域和服务而不是链式规则；

3，firewalld默认是拒绝的，需要设置以后才能放行。而iptables默认是允许的，需要拒绝的才去限制；

4，firewalld自身并不具备防火墙的功能，而是和iptables一样需要通过内核的netfilter来实现。也就是说，firewalld和iptables一样，它们的作用都用于维护规则，而真正使用规则干活的是内核的netfilter。只不过firewalld和iptables的结果以及使用方法不一样！

firewalld是iptables的一个封装，可以让你更容易地管理iptables规则。它并不是iptables的替代品，虽然iptables命令仍可用于firewalld，但建议firewalld时仅使用firewalld命令。



使用方式区别：

iptables要给http服务添加80端口允许规则，需要在/etc/sysconfig/iptables中添加如下内容：

-A INPUT -m state --state NEW -m tcp -p tcp --dport 80 -j ACCEPT

然后

systemctl stop iptables

systemctl restart iptables

systemctl enable iptables

firewalld实现同样的功能，可以用下面2种方式：

1.直接添加服务

firewall-cmd --permanent --zone=public --add-service=http

firewall-cmd --reload

2.添加端口

firewall-cmd --permanent --zone=public --add-port=80/tcp

firewall-cmd --reload

> iptables部分参考：https://www.cnblogs.com/metoy/p/4320813.html
> firewalld部分参考：https://blog.csdn.net/wangmx1993328/article/details/80738012
> iptables与firewalld的区别参考：https://blog.51cto.com/14390242/2408693
