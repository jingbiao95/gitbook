Dockerfile中RUN，CMD和ENTRYPOINT都能够用于执行命令，下面是三者的主要用途：

- RUN命令执行命令并创建新的镜像层，通常用于**安装软件包**
- CMD命令设置容器启动后默认执行的命令及其参数，但CMD设置的命令能够被`docker run`命令后面的命令行参数替换
- ENTRYPOINT配置容器启动时的执行命令（不会被忽略，一定会被执行，即使运行 `docker run`时指定了其他命令）



## Shell格式和Exec格式运行命令

我们可用两种方式指定 RUN、CMD 和 ENTRYPOINT 要运行的命令：Shell 格式和 Exec 格式：

- Shell格式：<instruction> <command>。例如：apt-get install python3
- Exec格式：<instruction> ["executable", "param1", "param2", ...]。例如： ["apt-get", "install", "python3"]

**CMD 和 ENTRYPOINT 推荐使用 Exec 格式，因为指令可读性更强，更容易理解。RUN 则两种格式都可以。**



## Run命令

RUN 指令通常用于安装应用和软件包。RUN 在当前镜像的顶部执行命令，并通过创建新的镜像层。

```
RUN apt-get update && apt-get install -y \  
 bzr \
 cvs \
 git \
 mercurial \
 subversion
```

apt-get update 和 apt-get install 被放在一个 RUN 指令中执行，这样能够保证每次安装的是最新的包。如果 apt-get install 在单独的 RUN 中执行，则会使用 apt-get update 创建的镜像层，而这一层可能是很久以前缓存的。

## CMD命令

CMD 指令允许用户指定容器的默认执行的命令。**此命令会在容器启动且 docker run 没有指定其他命令时运行。**

```bash
CMD echo "Hello world"
```

运行容器 `docker run -it [image] `将输出：

```undefined
Hello world
```

但当后面加上一个命令，比如 `docker run -it [image] /bin/bash`，CMD 会被忽略掉，命令 bash 将被执行

```ruby
root@10a32dc7d3d3:/#
```



## ENTRYPOINT命令

ENTRYPOINT 的 Exec 格式用于设置容器启动时要执行的命令及其参数，同时可通过CMD命令或者命令行参数提供额外的参数。ENTRYPOINT 中的参数始终会被使用，这是与CMD命令不同的一点。下面是一个例子

```bash
ENTRYPOINT ["/bin/echo", "Hello"]  
```

当容器通过 `docker run -it [image] `启动时，输出为：

```undefined
Hello
```

而如果通过 docker run -it [image] CloudMan 启动，则输出为：

```undefined
Hello CloudMan
```

将Dockerfile修改为：

```dockerfile
ENTRYPOINT ["/bin/echo", "Hello"]  
CMD ["world"]
```

当容器通过 docker run -it [image] 启动时，输出为：

```undefined
Hello world
```

而如果通过 docker run -it [image] CloudMan 启动，输出依旧为：

```undefined
Hello CloudMan
```

ENTRYPOINT 中的参数始终会被使用，而 CMD 的额外参数可以在容器启动时动态替换掉。

## 总结

- 使用 RUN 指令安装应用和软件包，构建镜像。
- 如果 Docker 镜像的用途是运行应用程序或服务，比如运行一个 MySQL，应该优先使用 Exec 格式的 ENTRYPOINT 指令。CMD 可为 ENTRYPOINT 提供额外的默认参数，同时可利用 docker run 命令行替换默认参数。
- 如果想为容器设置默认的启动命令，可使用 CMD 指令。用户可在 docker run 命令行中替换此默认命令。

