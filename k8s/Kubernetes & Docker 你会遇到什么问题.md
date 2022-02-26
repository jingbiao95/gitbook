

**Kubernetes & Docker 你会遇到什么问题**

[netkiller：Kubernetes & Docker 你会遇到什么问题zhuanlan.zhihu.com!](https://zhuanlan.zhihu.com/p/266306984)

在项目中实施容器技术，你可以遇到下列问题

## 镜像遇到的问题



目前docker 镜像，没有统一标准，体现在一下几个方面。

使用**OS**发行版不统一

在使用过程中会遇到过各种本班的 OS。包括 alpine, debian, ubuntu, centos, oraclelinux, redhat 等等

经过裁剪的 **OS** 面目全非，不完整

即使是镜像采用 CentOS 母版，很多镜像制作者会给操作系统减肥。经过优化后，已经不是官方版本，在使用过程中你会遇到各种麻烦。例如调试的时候需要 curl,wget,telnet,nslookup 等工具在镜像中没有。甚至 ps, top, free, find, netstat, ifconfig 命令都没有。

很多容器都不带 iptables 所以，即使带有iptables 在容器中修改规则也很麻烦。

## 安装位置不统一

传统OS 以 CentOS为例，有严格的安装规范，例如:

通常安装位置是：

```text
/etc/example  配置文件
/bin/sbin 二进制文件
/var/lib/example 数据文件
/var/log/example 日志文件
/var/run/example PID 文件

/etc/sysconfig/example 启动参数文件
/etc/system.d/example  启动脚本
```

或者被安装在：

```text
/usr/local/etc 配置文件
/usr/local/bin 可执行文件
/usr/local/share 文档
```

最后一种是独立安装在：

/usr/local/example 下

容器镜像那可是五花八门，没有统一标准，如果不看 Dockerfile 根本不知道作者将文件安装到了哪里。

常常存储目录被放置在根目录。例如 /data

## **Linux** 系统也存在**BUG**

在我的20年执业生涯中是遇到过 Linux 系统有BUG的，还向 Redhat 提交过 BUG。如果你采用的镜像有BUG，你想过怎么去debug 吗？

## 容器遇到的问题

**程序启动的区别**

在Linux是一般是采用守护进程方式启动。启动后进入后台，启动采用 systemd 。

容器中启动通常是直接运行，这样的运行方式，相当于你在linux的Shell 终端直接运行一样，是在前台运行，随时 CTRL + C 或者关闭终端窗口，程序就会退出。容器采用这种方式启动，就是为了让 docker 管理容器，docker 能够感知到容器的当前状态，如果程序退出，docker 将会重新启动这个容器。

守护进程方式需要记录 pid 即父进程ID，用于后面管理该进程，例如可以实现 HUP 信号处理。也就是 reload 操作，不用退出当前程序实现配置文件刷新。处理 HUP 信号，无需关闭 Socker 端口，也不会关闭线程或进程，用户体验更好。

容器是直接运行（前台运行），所以没有 PID 也不能实现 reload 操作。 配置文件更新需要重新启动容器，容器启动瞬间TCP Socker 端口关闭，此时用户会 timeout。甚至该服务可能会引起集群系统的雪崩效应。

很多镜像制作者更趋向使用环境变量传递启动参数。

当然你也可以在容器中使用 systemd ，这样做容器不能直接感知到容器的运行状态，systemctl stop example 后，容器仍然正常。需要做存活和健康检查。通过健康状态判断容器的工作情况。如果处于非健康状态，将该节点从负载均衡节点池中将它踢出去。

Linux 启动一个应用远远比docker 启动一个容器速度要快。因为物理机或者虚拟机的Linux操作系统已经启动，虚拟机也分配了资源，运行可执行文件基本上是瞬间启动。而 docker 启动容器，要分配资源（分配内存和CPU资源，新建文件系统），相当于创建一个虚拟机的过程，最后载入约200MB左右的镜像，并将镜像运行起来，所以启动所需时间较长，有时不可控，尤其是Java应用更为突出。

**存储面临的问题**

传统 Linux 直接操作本地硬盘，IO性能最大化。

私有云还好办公有云处处受限。

自建的 Docker 或 Kubrnetes 可以使用宿主主机资源，公有云只能使用网络文件系统和分布式系统。

这也是我的架构中 KVM，Docker，Kubernetes，物理机混合使用的原因，根据业务场景的需要来选择哪种方案。

物理机上部署 docker 可以分配宿主主机的所有资源，适合做有状态的服务的存储持久化的需求。

私有云 Kubernetes 适合做 CPU密集型运算服务，虽然通过local 卷和 hostPath 可以绑定，但是管理起来不如 Docker 更方便。

NFS 基本是做实验用的，不能用在生产环境。我20年的职业生涯遇到过很多奇葩，例如 NFS 卡顿，NFS 用一段时间后访问不了，或者可以访问，文件内容是旧的等等。

无论是NFS是更先进的分布式文件系统，如果不是 10G以太网，基本都不能用在生产环境。10年前我用4电口1G网卡做端口聚合勉强可以用于生产环境，不过10年前的互联网生态跟当今不同，那时还是以图文为主，确切的说是文字为主，配图还很少。

所以涉及到存储使用分布式文件系统的前提是必须是 10G以上以太网或者8G以上的FC 存储。这样才不会有IO瓶颈。任何分布式文件系统都不可能比本地文件系统稳定，除了速度还有延迟等等。

10GB 电口，光口以太网已经出来十几年了，相对比较便宜，可以使用 4光口 10G网卡，然后做端口聚合，变成 40G 网口。

现在 40G光口交换机都在10-20万之间。一个40G的交换口可以分出四个10GB口。

如果使用40GB以上的以太网，那么总成本可能会超过物理机+虚拟机的解决方案。

**内部域名DNS**

由于在集群环境中容器名称是随机，IP地址是不固定的，甚至端口也是动态的。为了定位到容器的节点，通常集群中带有DNS功能，为每个节点分配一个域名，在其他容器中使用域名即可访问到需要的容器。

看似没有问题，我的职业生涯中就遇到过DNS的问题，bind,dnsmseq 我都用过，都出现过事故。解析卡顿，ping [www.domain.com](https://link.zhihu.com/?target=http%3A//www.domain.com/) 后迟迟解析不出IP。最长一次用了几分钟才解析到IP地址。

所以后面就非常谨慎，配置文件中我们仍然使用域名，因为修改配置文件可能需要 reload 应用，或者重新部署等等。域名写入配置，方便IP地址变更。例如 db.host=[db.netkiller.cn](https://link.zhihu.com/?target=http%3A//db.netkiller.cn/) 同时我们会在 /etc/hosts 中增加 xxx.xxx.xxx.xxx [db.netkiller.cn](https://link.zhihu.com/?target=http%3A//db.netkiller.cn/) 。这样主要使用 /etc/hosts 做解析，一旦漏掉 /etc/hosts 配置 DNS 还能工作。

故障分析，DNS 使用 UDP 协议 53 端口，UDP 在网络中传输不会返回状态，有无数种可能导致 DNS 解析失败。例如内部的交换机繁忙，背板带宽不够（用户存储转发数据包，你可以理解就是交换机的内存），路由的问题等等……

**容器与网络**

相比传统网络，容器中的网络环境是十分复杂的。传统网络中一个数据包仅仅经过路由器，交换机，达到服务器，最多在服务前在增加一些防火墙，负载均衡等设备。

容器网络部分实现方式SDN（软件定义网络）相比物理机（路由器、交换机、无服务）实现相对复杂。容器里面使用了IP转发，端口转发，软路由，lvs，7层负载均衡等等技术…… 调试起来非常复杂。docker 的 iptables 规则很头痛。

例如一个TCP/IP 请求，需要经过多层虚拟网络设备（docker0,bridge0,tun0……）层层转发，再经过4层和7层的各种应用拆包，封包，最终到达容器内部。

有兴趣你可以测试一下对比硬件设备，容器的网络延迟和吞吐量。

**容器的管理**

传统服务可以通过键盘和显示器本地管理，OpenSSH 远程管理，通过配置还能使用串口。

容器的管理让你抓狂 docker exec 和 kubectl exec 进入后与传统Linux差异非常大，这是镜像制作者造成了。

有些镜像没有初始化 shell 只有一个 $ 符号

没有彩色显示

可能不支持 UTF-8，中文乱码

可能不是标准 ANSI/XTerm 终端

键盘定义五花八门，可能不是美式104键盘

国家和时区并不是东八区，上海

HOME 目录也是不是 /root

想查看端口情况，发现 netstat 和 ss 命令没有。

想查看IP地址，发现 ifconfig, ip 命令没有。

想测试IP地址是否畅通，发现 ping, traceroute 没有。

想测试URL，发现 curl , wget 没有。

有些镜像 dnf,yum,apk,apt 可以使用，有些镜像把包管理也给阉割了，你想安装上述工具都安装不了。

卧槽！！！ 一万匹草泥马

然后就自己用 Dockerfile 编译，整出200MB的镜像，卧槽这么大。

**容器与安全**

很多容器的镜像中是不包含 iptables 的，所以无法做颗粒度很细的容器内部网络安全设置。即使你制作的镜像带有iptables ，多数容器的侧咯，IP地址和端口是随机变化的。

绑定IP地址又带了容器的复杂性。

一旦攻入一个容器，进入容器后，容器与容器间基本是畅通无阻。

在容器中藏一个后门比物理机更容易，如上文所说很多容器中没有调试相关命令，限制了你排查后门的难度。所以Dockerfile 制作镜像，最好使用官方镜像衍生出你的镜像

**容器与监控**

谈到监控，跳不开 prometheus（普罗米修斯），它并不能覆盖到所有监控。

我曾经写过一篇文章《监控的艺术》网上可以搜到。

**容器与CI/CD**

在DevOps场景中，使用 docker 或 kubernetes 做 CI/CD 是很扯淡的。

当 git 产生提交后，gitlab/jenkins 启动容器，下载代码，编译，打包，测试，产生构建物，编译 Dockerfile ，上传 docker 镜像到 registry，最后部署到容器执行。

卧槽！！！速度能急死你。

于是乎，我们做了 Cache。 不用每次都 pull 镜像，缓存 Maven 的 .m2 库，不再清理代码（mvn clean）提速不少，测试环境凑合用吧。 注意不mvn clean 有时会编译出错

至于生产环境，我就不说了，有多少人真用CD部署生产环境。

**人员的问题**

现实中真正精通容器应用的人很少，容器实在太复杂。Google 将 Kubernetes 设计成大而全系统，想用 Kubernetes 解决所有问题。它涵盖了几大块。

操作系统，虚拟化，软件定义网络，存储，容器管理，用户体系，权限体系……

我们的大学教育是本科教育专科化，本科教育本应该重视通识教育，我们的教育却按照专科标准教育。本科是面向学术的起点，专科是面向工作，解决实际问题。

你问一个中国大学生他会什么，他会说：我会Java，我会Linux……

反应到工作上，就是程序猿不懂运维知识，运维攻城狮不会写程序。员工更趋向深耕一个领域，很类似现在的医生教育和医院体系，专科化，割裂化，导致很多跨科的疾病难以诊断。

于是我提出了「多维度架构」。

## 最后总结

使用物理机，虚拟机，学习成本，试错成本，部署成本远远低于容器技术。

Google 官方也曾经说过，未来 kubernetes 重点可能会转向虚拟机。

我个人认为容器更适合CPU密集型的业务。

我的架构中 KVM，Docker，Kubernetes，物理机混合使用，根据业务场景的需要来选择最佳方案。

前期制作符合你需求的镜像，可能需要花费很长时间。

[netkiller：Kubernetes(minikube) 私有 registry 使用详解](https://zhuanlan.zhihu.com/p/261722859)
[netkiller：Kubernetes Registry](https://zhuanlan.zhihu.com/p/259901925)
[netkiller：怎样实施 DevOps?面临什么问题？如何解决？](https://zhuanlan.zhihu.com/p/144557586)
[netkiller：多维度架构之分库分表](https://zhuanlan.zhihu.com/p/285048622)
[netkiller：多维度架构之微服务的服务拆分](https://zhuanlan.zhihu.com/p/281924388)
[netkiller：多维度架构之消息队列](https://zhuanlan.zhihu.com/p/275670325)
[netkiller：多维度架构之超时时间](https://zhuanlan.zhihu.com/p/268685746)
[netkiller：多维度架构之网络损耗](https://zhuanlan.zhihu.com/p/267156746)
[netkiller：多维度架构之会话数](https://zhuanlan.zhihu.com/p/269035392)


## 参考资料

# Kubernetes & Docker 你会遇到什么问题