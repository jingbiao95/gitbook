## 查看硬盘容量

展示集群整体的容量情况，NameNode和DataNode的容量信息

1. 网页查看 http://localhost:50070/dfshealth.html#tab-overview
2. 命令行查看`hdfs dfsadmin -report`



## 查看硬盘读写速度，发现异常磁盘

通过hadoop监控软件来做

	- Ganglia或者Zobbix或者收费   或者linux监控



