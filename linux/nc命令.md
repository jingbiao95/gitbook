**端口**是与 Linux 操作系统上的应用或进程的通讯端点的逻辑实体。

`netcat`（或简称 `nc`）是一个功能强大且易于使用的程序，可用于 Linux 中与 TCP、UDP 或 UNIX 域套接字相关的任何事情。

## 安装

```shell
yum install nc                  # [在 CentOS/RHEL 中]
dnf install nc                  # [在 Fedora 22+ 中]
sudo apt-get install netcat     # [在 Debian/Ubuntu 中]
```

## nc的作用

（1）实现任意TCP/UDP端口的侦听，nc可以作为server以TCP或UDP方式侦听指定端口

（2）端口的扫描，nc可以作为client发起TCP或UDP连接

（3）机器之间传输文件

（4）机器之间网络测速 



### nc控制参数

| 控制参数 | 解释                                                         |
| :------------------: | :----------------------------------------------------------- |
| -l       | 用于指定nc将处于侦听模式。指定该参数，则意味着nc被当作server，侦听并接受连接，而非向其它地址发起连接。 |
| -p <port> | 暂未用到（老版本的nc可能需要在端口号前加-p参数，下面测试环境是centos6.6，nc版本是nc-1.84，未用到-p参数） |
| -s | 指定发送数据的源IP地址，适用于多网卡机 |
| -u | 指定nc使用UDP协议，默认为TCP |
| -v | 输出交互或出错信息，新手调试时尤为有用 |
| -w | 超时秒数，后面跟数字 |
| -z | 表示zero，表示扫描时不发送任何数据 |



## 场景

### **nc可以作为server端启动一个tcp的监听**

```shell
nc -l 9999
```

### nc命令作为客户端工具进行端口探测

```
nc -vz -w 2 10.0.1.161 9999  # tcp端口（-v可视化，-z扫描时不发送数据，-w超时几秒，后面跟数字）
nc -vzw 2 10.0.1.161 9999 # 同上
nc -vuz 10.0.1.161 9998 # udp端口
```

### **nc作为server端启动一个udp的监听**

```shell
nc  -ul  9998
```





##  参考目录

https://www.cnblogs.com/nmap/p/6148306.html
