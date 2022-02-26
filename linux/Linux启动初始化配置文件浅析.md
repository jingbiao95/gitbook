[toc]



---
## 配置文件介绍

### /etc/profile 

`/etc/profile`   登录时，会执行。 
全局（公有）配置，不管是哪个用户，登录时都会读取该文件。 

### /ect/bashrc

/ect/bashrc   Ubuntu没有此文件，与之对应的是 `/ect/bash.bashrc`
bash.bashrc 是交互式shell的初始化文件

### ~/.profile
`~/.profile`  某个用户读取的配置。 
若bash是以**login**方式执行时，读取`~/.bash_profile`，若它不存在，则读取`~ /.bash_login`，若前两者不存在，读取`~ /.profile`。 
另外，图形模式登录时，此文件将被读取，即使存在`~/.bash_profile`和`~/.bash_login`。 

### ~/.bash_login
若 bash是以login方式执行时，读取`~/.bash_profile`，若它不存在，则读取`~/.bash_login`，若前两者不存在，读取`~ /.profile`。 

### ~/.bash_profile 
Unbutu默认没有此文件，可新建。 只有 bash是以login形式执行时，才会读取此文件。通常该配置文件还会配置成去读取`~/.bashrc`。 

### ~/.bashrc
该文件包含专用于某个用户的bash shell的bash信息,当该用户登录时以及每次打开新的shell时,该文件被读取. 

当 bash是以non-login形式执行时，读取此文件。若是以login形式执行，则不会读取此文件。 
### ~/.bash_logout 
注销时，且是longin形式，此文件才会读取。也就是说，在文本模式注销时，此文件会被读取，图形模式注销时，此文件不会被读取。 

补充一点，`/etc/rc.local`是系统shell会执行的文件，linux启动后会退出的；`/etc/profile` 或 `/etc/bash.bashrc`是用户shell会的配置，我们一般的Shell是用户Shell的子进程，而非系统shell的子进程，所以如果在 `/etc/rc.local`中指定`"alias ll='ls -a'"`这样的别名，对登录系统后的shell是没用的。

某网友总结如下： 

- `/etc/profile`，`/etc /bashrc` 是系统**全局**环境变量设定 

## /etc/profile与/etc/bashrc的区别？ 

前一个主要用来设置一些系统变量,比如JAVA_HOME等等,后面一个主要用来保存一些bash的设置. 

`~/.profile`，`~ /.bashrc`用户家目录下的**私有环境变量**设定 

当登入系统时候获得一个shell进程时，其读取环境设定档有三步 
1. 首先读入的是全局环境变量设定档`/etc/profile`，然后根据其内容读取额外的设定的文档，如 `/etc/profile.d`和`/etc/inputrc`
2. 然后根据不同使用者帐号，去其家目录读取`~/.bash_profile`，如果这读取不了就读取`~/.bash_login`，这个也读取不了才会读取 `~/.profile`，这三个文档设定基本上是一样的，读取有优先关系 
3. 然后在根据用户帐号读取`~/.bashrc`至于`~/.profile`与`~/.bashrc`的区别，都具有个性化定制功能 
	- `~/.profile`可以设定本用户专有的路径，环境变量，等，它只能登入的时候执行一次 
	- `~/.bashrc`也是某用户专有设定文档，可以设定路径，命令别名，每次shell script的执行都会使用它一次 


## 样例 

1. 图形模式登录时，顺序读取：`/etc/profile`和`~/.profile` 
2. 图形模式登录后，打开终端时，顺序读取：`/etc/bash.bashrc`和`~/.bashrc` 
3. 文本模式登录时，顺序读取：`/etc/bash.bashrc`，`/etc/profile`和`~/.bash_profile` 
4. 从其它用户su到该用户，则分两种情况： 
    （1）如果带-l参数（或-参数，--login参数），如：su -l username，则bash是login的，它将顺序读取以下配置文件：`/etc/bash.bashrc`，`/etc/profile`和`~ /.bash_profile`。 
    （2）如果没有带-l参数，则bash是non-login的，它将顺序读取：`/etc/bash.bashrc`和`~/.bashrc` 
5. 注销时，或退出su登录的用户，如果是login方式，那么bash会读取：`~/.bash_logout` 
6. 执行自定义的shell文件时，若使用`bash -l a.sh`的方式，则bash会读取行：`/etc/profile`和`~/.bash_profile`，若使用其它方式，如：`bash a.sh， ./a.sh，sh a.sh`（这个不属于bash shell），则不会读取上面的任何文件。 
7. 上面的例子凡是读取到`~/.bash_profile`的，若该文件不存在，则读取`~/.bash_login`，若前两者不存在，读取`~ /.profile`。

## Reference
https://www.cnblogs.com/caidingyu/p/10015549.html