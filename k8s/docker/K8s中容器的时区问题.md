##  

在构建自己的容器时

进入到当前项目的目录

将服务器的时区文件拷贝到当前文件夹下

```shell
cp /usr/share/zoneinfo/Asia/Shanghai Shanghai
```

在修改dockerfile

新增如下

```
COPY Shanghai /usr/share/zoneinfo/Asia/Shanghai
RUN rm -f /etc/localtime && ln -sv /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo "Asia/Shanghai" > /etc/timezone

```



## 参考资料

