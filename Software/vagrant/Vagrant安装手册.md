## 安装软件

### **安装 VirtualBox**

进入 VirtualBox 的[主页](https://link.zhihu.com/?target=https%3A//www.virtualbox.org/)，点击大大的下载按钮，即可进入下载页面。

VirtualBox 是一个跨平台的虚拟化工具，支持多个操作系统，根据自己的情况选择对应的版本下载即可。





### **安装 Vagrant**

在 [Vagant 网站](https://link.zhihu.com/?target=https%3A//www.vagrantup.com/)下载最新的版本，根据自己的操作系统选择对应的版本下载即可。

注意，Vagrant 是没有图形界面的，所以安装完成后也没有桌面快捷方式。具体使用方法，接下来会详细说明。

Vagrant 的安装程序会自动把安装路径加入到 PATH 环境变量，所以，这时候可以通过命令行执行 `vagrant version` 检查是否安装成功：

```text
> vagrant version
Installed Version: 2.2.7
Latest Version: 2.2.8
```

## 配置虚机存放位置

创建虚拟机会占用较多的磁盘空间，在 Windows 系统下默认的虚机创建位置是在 C 盘，所以最好配置到其它地方。

### **配置 VirtualBox**

启动 VirtualBox 后，通过菜单 `管理` -> `全局设定`，或者按下快捷键 `Ctrl + g`，在全局设定对话框中，修改 `默认虚拟电脑位置`，指定一个容量较大的磁盘。



### **配置 Vagrant**

通过 Vagrant 创建虚机需要先导入镜像文件，也就是 `box`，它们默认存储的位置在用户目录下的 `.vagrant.d` 目录下，对于 Windows 系统来说，就是 `C:\Users\用户名\.vagrant.d`。

如果后续可能会用到较多镜像，或者你的 C 盘空间比较紧缺，可以通过设置环境变量 `VAGRANT_HOME` 来设置该目录。

在 Windows 系统中，可以这样操作：新建系统环境变量，环境变量名为 `VAGRANT_HOME`，变量值为 `E:\VirtualBox\.vagrant.d`

> **注意**，最后这个 `.vagrant.d` 目录名称不是必须的，但是建议保持一致，这样一眼看上去就能知道这个目录是做什么用处的了。



## 下载虚机镜像

Vagrant 有一个[镜像网站](https://link.zhihu.com/?target=https%3A//app.vagrantup.com/boxes/search)，里面列出了都有哪些镜像可以用，并且提供了操作文档。默认下载往往会比较慢

### Ubuntu

清华大学镜像站，如: `https://mirrors.tuna.tsinghua.edu.cn/ubuntu-cloud-images/bionic/current/bionic-server-cloudimg-amd64-vagrant.box`

### CentOS

中科大镜像站，如: `https://mirrors.ustc.edu.cn/centos-cloud/centos/7/vagrant/x86_64/images/CentOS-7.box`



## 添加 box

接下来我们需要将下载后的 `.box` 文件添加到 vagrant 中。

**查看所有box**

```shell 
> vagrant box list
There are no installed boxes! Use `vagrant box add` to add some.
```

如果这是第一次运行，此时 `VAGRANT_HOME` 目录下会自动生成若干的文件和文件夹，其中有一个 `boxes` 文件夹，这就是要存放 box 文件的地方。

执行 `vagrant box add` 命令添加 box:

```cmd
> vagrant box add e:\Downloads\CentOS-7.box --name centos-7
==> box: Box file was not detected as metadata. Adding it directly...
==> box: Adding box 'centos-7' (v0) for provider:
    box: Unpacking necessary files from: file:///e:/Downloads/CentOS-7.box
    box:
==> box: Successfully added box 'centos-7' (v0) for 'virtualbox'!
```

注意：这里box的路径应该是**绝对路径**

