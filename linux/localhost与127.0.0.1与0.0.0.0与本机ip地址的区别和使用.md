

## **localhost**

localhost其实是域名，一般windows系统默认将localhost指向127.0.0.1，但是localhost并不等于127.0.0.1，localhost指向的IP地址是可以配置的

在过去它指向 127.0.0.1 这个IP地址。在操作系统支持 ipv6 后，它同时还指向ipv6 的地址 [::1] 



在 Windows 中，这个域名是预定义的，从 hosts 文件中可以看出`C:\Windows\System32\drivers\etc\hosts`：

```
# localhost name resolution is handled within DNS itself.
# 127.0.0.1 localhost
# ::1 localhost
```

在 Linux 中，其定义位于 `/etc/hosts` 中：

```
127.0.0.1 localhost
```

## **127.0.0.1**

首先我们要先知道一个概念，凡是以127开头的IP地址，都是回环地址（Loop back address），其所在的回环接口一般被理解为虚拟网卡，并不是真正的路由器接口。

**loopback** 是一个特殊的网络接口(可理解成虚拟网卡)，用于**本机中各个应用之间的网络交互**。

**回环地址**：主机上发送给127开头的IP地址的数据包会被发送的主机自己接收，**根本传不出去**，外部设备也无法通过回环地址访问到本机。

 **说明**：正常的数据包会从IP层进入链路层，然后发送到网络上；而给回环地址发送数据包，数据包会直接被发送主机的IP层获取，不会进入链路层。

而127.0.0.1作为{127}集合中的一员，当然也是个回环地址。只不过127.0.0.1经常被默认配置为localhost的IP地址。

一般会通过ping 127.0.0.1来测试某台机器上的**网络设备是否工作正常**。

Windows 中看不到这个接口，Linux中这个接口叫 lo：

```
#ifconfig
eth0 Link encap:Ethernet hwaddr 00:00:00:00:00:00
　　inet addr :192.168.0.1 Bcase:192.168.0.255 Mask:255.255.255.0
　　......
lo     Link encap:Local Loopback
　　inetaddr: 127.0.0.1 Mask: 255.0.0.0
```



可以看出 lo 接口的地址是 127.0.0.1。事实上整个 127.* 网段都算能够使用，比如你 ping 127.0.0.2 也是通的。 

但是使用127.0.0.1作为loopback接口的默认地址只是一个惯例，比如下面这样：

```shell
# 设置lo接口地址
ifconfig lo 192.168.128.1

ping localhost	# 糟糕，ping不通了
ping 192.128.128.1 # 可以通
```

运行`ifconfig lo`则看到

```
lo  Link encap:Local Loopback
　 inetaddr: 192.168.128.1 Mask: 255.255.255.0
     ......
```

如果随便改这些配置，可能导致很多只认 127.0.0.1 的软件挂掉。 

 



![331C8D82212B48A9BDC277F79DB17FAC](https://raw.githubusercontent.com/jingbiao95/Images/main/331C8D82212B48A9BDC277F79DB17FAC.png)



## **0.0.0.0**

首先，0.0.0.0是不能被ping通的。在服务器中，0.0.0.0并不是一个真实的的IP地址，它表示本机中所有的IPV4地址。监听0.0.0.0的端口，就是监听本机中所有IP的端口。

 

## **本机IP**

本机IP通常仅指在同一个局域网内，能同时被外部设备访问和本机访问的那些IP地址（可能不止一个）。像127.0.0.1这种一般是不被当作本机IP的。

本机IP是与具体的**网络接口绑定**的，比如以太网卡、无线网卡或者PPP/PPPoE拨号网络的虚拟网卡，想要正常工作都要绑定一个地址，否则其他设备就不知道如何访问它。

