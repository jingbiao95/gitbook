

# Elasticsearch

[TOC]



## 1.Elsaticsearch是什么？

一个分布式，RESTful风格的搜索引擎，能够解决不断涌现的各种用例。

```
The Elastic Stack, 包括 Elasticsearch、Kibana、Beats 和 Logstash（也称为 ELK Stack）。
能够安全可靠地获取任何来源、任何格式的数据，然后实时地对数据进行搜索、分析和可视
化。Elaticsearch，简称为 ES，ES 是一个开源的高扩展的分布式全文搜索引擎，是整个 Elastic
Stack 技术栈的核心。它可以近乎实时的存储、检索数据；本身扩展性很好，可以扩展到上
百台服务器，处理 PB 级别的数据。
```

## 2.Elsaticsearch使用

### 2.1Elsaticsearch安装

1.Elasticsearch 的官方地址：https://www.elastic.co/cn/

2.目录结构

![image-20211101170255481](https://raw.githubusercontent.com/zhaoyaoting/md_img/main/image-20211101170255481.png)



<img src="https://raw.githubusercontent.com/zhaoyaoting/md_img/main/image-20211101170337665.png" alt="image-20211101170337665" style="zoom: 80%;" />



3.启动：进入 bin 文件目录，点击 elasticsearch.bat 文件启动 ES 服务

```
注意：9300 端口为 Elasticsearch 集群间组件的通信端口，9200 端口为浏览器访问的 http 协议 RESTful 端口。
```

4.验证是否启动成功

打开浏览器，输入地址：http://localhost:9200

<img src="https://raw.githubusercontent.com/zhaoyaoting/md_img/main/image-20211101170552438.png" alt="image-20211101170552438" style="zoom:80%;" />

5.如果空间不足，可以修改config/jvm.options 配置文件

```properties
# 设置 JVM 初始内存为 1G。此值可以设置与-Xmx 相同，以避免每次垃圾回收完成后 JVM 重新分配内存
# Xms represents the initial size of total heap space
# 设置 JVM 最大可用内存为 1G
# Xmx represents the maximum size of total heap space
-Xms1g
-Xmx1g
```

### 2.2RESTful简介

```
REST 指的是一组架构约束条件和原则。满足这些约束条件和原则的应用程序或设计就是 RESTful。
```

### 2.3数据格式

Elasticsearch 是面向文档型数据库，一条数据在这里就是一个文档。

Elasticsearch 里存储文档数据和关系型数据库 MySQL 存储数据的概念进行一个类比

<img src="https://raw.githubusercontent.com/zhaoyaoting/md_img/main/image-20211101171610151.png" alt="image-20211101171610151" style="zoom:67%;" />

ES 里的 Index 可以看做一个库，而 Types 相当于表，Documents 则相当于表的行。

```
这里 Types 的概念已经被逐渐弱化，Elasticsearch 6.X 中，一个 index 下已经只能包含一个type，Elasticsearch 7.X 中, Type 的概念已经被删除了。
```

### 2.4HTTP操作

#### 1.创建索引

```
对比关系型数据库，创建索引就等同于创建数据库
```

在 Postman 中，向 ES 服务器发 PUT 请求 ：http://127.0.0.1:9200/shopping

![image-20211101172108556](https://raw.githubusercontent.com/zhaoyaoting/md_img/main/image-20211101172108556.png)

```
{
 "acknowledged"【响应结果】: true, # true 操作成功
 "shards_acknowledged"【分片结果】: true, # 分片操作成功
 "index"【索引名称】: "shopping"
}
# 注意：创建索引库的分片数默认 1 片，在 7.0.0 之前的 Elasticsearch 版本中，默认 5 片
```

#### 2.查询所有索引

在 Postman 中，向 ES 服务器发 GET 请求 ：http://127.0.0.1:9200/_cat/indices?v

这里请求路径中的_cat 表示查看的意思，indices 表示索引，所以整体含义就是查看当前 ES 服务器中的所有索引，就好像 MySQL 中的 show tables 的感觉，服务器响应结果如下

![image-20211101172546879](https://raw.githubusercontent.com/zhaoyaoting/md_img/main/image-20211101172546879.png)

| 表头           | 含义                                                         |
| :------------- | ------------------------------------------------------------ |
| health         | 当前服务器健康状态： green(集群完整) yellow(单点正常、集群不完整) red(单点不正常) |
| status         | 索引打开、关闭状态                                           |
| index          | 索引名                                                       |
| uuid           | 索引统一编号                                                 |
| pri            | 主分片数量                                                   |
| rep            | 副本数量                                                     |
| docs.count     | 可用文档数量                                                 |
| docs.deleted   | 文档删除状态（逻辑删除）                                     |
| store.size     | 主分片和副分片整体占空间大小                                 |
| pri.store.size | 主分片占空间大小                                             |

#### 3.查看单个索引

在 Postman 中，向 ES 服务器发 GET 请求 ：http://127.0.0.1:9200/shopping

![image-20211101173240986](https://raw.githubusercontent.com/zhaoyaoting/md_img/main/image-20211101173240986.png)

```
"number_of_shards"【设置 - 索引 - 主分片数量】: "1",
"number_of_replicas"【设置 - 索引 - 副分片数量】: "1",
"uuid"【设置 - 索引 - 唯一标识】: "AVjOHeceSMmhfh1fMGhiSw",
```

#### 4.删除索引

在 Postman 中，向 ES 服务器发 DELETE 请求 ：http://127.0.0.1:9200/shopping

![image-20211101173835065](https://raw.githubusercontent.com/zhaoyaoting/md_img/main/image-20211101173835065.png)

#### 5.创建文档

在 Postman 中，向 ES 服务器发 POST 请求 ：http://127.0.0.1:9200/shopping/_doc

(此处发送请求的方式必须为 POST，不能是 PUT，否则会发生错误)

![image-20211101174141739](https://raw.githubusercontent.com/zhaoyaoting/md_img/main/image-20211101174141739.png)

```json
{
 "_index"【索引】: "shopping",
 "_type"【类型-文档】: "_doc",
 "_id"【唯一标识】: "Xhsa2ncBlvF_7lxyCE9G", #可以类比为 MySQL 中的主键，随机生成
 "_version"【版本】: 1,
 "result"【结果】: "created", #这里的 create 表示创建成功
 "_shards"【分片】: {
 "total"【分片 - 总数】: 2,
 "successful"【分片 - 成功】: 1,
 "failed"【分片 - 失败】: 0
 },
 "_seq_no": 0,
 "_primary_term": 1
}
```

上面的数据创建后，由于没有指定数据唯一性标识（ID），默认情况下，ES 服务器会随机 生成一个。

如果想要自定义唯一性标识，需要在创建时指定：http://127.0.0.1:9200/shopping/_doc/1

![image-20211101174607710](https://raw.githubusercontent.com/zhaoyaoting/md_img/main/image-20211101174607710.png)

```
此处需要注意：如果增加数据时明确数据主键，那么请求方式也可以为 PUT
```

#### 6.查看文档

在 Postman 中，向 ES 服务器发 GET 请求 ：http://127.0.0.1:9200/shopping/_doc/1

(查看文档时，需要指明文档的唯一性标识，类似于 MySQL 中数据的主键查询)

![image-20211101174847468](https://raw.githubusercontent.com/zhaoyaoting/md_img/main/image-20211101174847468.png)

```json
{
 "_index"【索引】: "shopping",
 "_type"【文档类型】: "_doc",
 "_id": "1",
 "_version": 1,
 "_seq_no": 1,
 "_primary_term": 1,
 "found"【查询结果】: true, # true 表示查找到，false 表示未查找到
 "_source"【文档源信息】: {
 "title": "华为手机",
 "category": "华为",
 "images": "http://www.gulixueyuan.com/hw.jpg",
 "price": 4999.00
 }
}
```

#### 7.修改文档

在 Postman 中，向 ES 服务器发 POST 请求 ：http://127.0.0.1:9200/shopping/_doc/1

（和新增文档一样，输入相同的 URL 地址请求，如果请求体变化，会将原有的数据内容覆盖）

![image-20211101175246088](https://raw.githubusercontent.com/zhaoyaoting/md_img/main/image-20211101175246088.png)

```json
{
 "_index": "shopping",
 "_type": "_doc",
 "_id": "1",
 "_version"【版本】: 2,
 "result"【结果】: "updated", # updated 表示数据被更新
 "_shards": {
 "total": 2,
 "successful": 1,
 "failed": 0
 },
 "_seq_no": 2,
 "_primary_term": 1
}
```

#### 8.修改字段

在 Postman 中，向 ES 服务器发 POST 请求 ：http://127.0.0.1:9200/shopping/_update/1

![image-20211101175614176](https://raw.githubusercontent.com/zhaoyaoting/md_img/main/image-20211101175614176.png)

```
根据唯一性标识，查询文档数据，文档数据已经更新
```

#### 9.删除文档

（删除一个文档不会立即从磁盘上移除，它只是被标记成已删除（逻辑删除））

在 Postman 中，向 ES 服务器发 DELETE 请求 ：http://127.0.0.1:9200/shopping/_doc/1

![image-20211101182236219](https://raw.githubusercontent.com/zhaoyaoting/md_img/main/image-20211101182236219.png)

```json
{
 "_index": "shopping",
 "_type": "_doc",
 "_id": "1",
 "_version"【版本】: 4, #对数据的操作，都会更新版本
 "result"【结果】: "deleted", # deleted 表示数据被标记为删除
 "_shards": {
 "total": 2,
 "successful": 1,
 "failed": 0
 },
 "_seq_no": 4,
 "_primary_term": 1
}
```

#### 10.条件删除文档

一般删除数据都是根据文档的唯一性标识进行删除，实际操作时，也可以根据条件对多条数 据进行删除

向 ES 服务器发 POST 请求 ：http://127.0.0.1:9200/shopping/_delete_by_query

![image-20211102142646244](https://raw.githubusercontent.com/zhaoyaoting/md_img/main/image-20211102142646244.png)

```json
{
 "took"【耗时】: 981,
 "timed_out"【是否超时】: false,
 "total"【总数】: 1,
 "deleted"【删除数量】: 1,
 "batches": 1,
 "version_conflicts": 0,
 "noops": 0,
 "retries": {
 "bulk": 0,
 "search": 0
 },
 "throttled_millis": 0,
 "requests_per_second": -1.0,
 "throttled_until_millis": 0,
 "failures": []
}
```

#### 11.创建映射

有了索引库，等于有了数据库中的 database。接下来就需要建索引库(index)中的映射了，类似于数据库(database)中的表结构(table)。 创建数据库表需要设置字段名称，类型，长度，约束等；索引库也一样，需要知道这个类型 下有哪些字段，每个字段有哪些约束信息，这就叫做映射(mapping)。

在 Postman 中，向 ES 服务器发 PUT 请求 ：http://127.0.0.1:9200/student/_mapping

```
映射数据说明：
  字段名：任意填写，下面指定许多属性，例如：title、subtitle、images、price
  type：类型，Elasticsearch 中支持的数据类型非常丰富，说几个关键的：
  		String 类型，又分两种：
  			text：可分词
  			keyword：不可分词，数据会作为完整字段进行匹配
  		Numerical：数值类型，分两类	
        	基本数据类型：long、integer、short、byte、double、float、half_float
        	浮点数的高精度类型：scaled_float
        Date：日期类型
        Array：数组类型
        Object：对象
  index：是否索引，默认为 true，也就是说你不进行任何配置，所有字段都会被索引。
  	true：字段会被索引，则可以用来进行搜索
  	false：字段不会被索引，不能用来搜索
  store：是否将数据进行独立存储，默认为 false
  	原始的文本会存储在_source 里面，默认情况下其他提取出来的字段都不是独立存储
	的，是从_source 里面提取出来的。当然你也可以独立的存储某个字段，只要设置
	"store": true 即可，获取独立存储的字段要比从_source 中解析快得多，但是也会占用
	更多的空间，所以要根据实际业务需求来设置。
  analyzer：分词器，这里的 ik_max_word 即使用 ik 分词器
```



#### 12.查看映射

在 Postman 中，向 ES 服务器发 GET 请求 ：http://127.0.0.1:9200/student/_mapping

![image-20211102150311166](https://raw.githubusercontent.com/zhaoyaoting/md_img/main/image-20211102150311166.png)

#### 13.索引映射关联

在 Postman 中，向 ES 服务器发 PUT 请求 ：http://127.0.0.1:9200/student1

![image-20211102150559856](https://raw.githubusercontent.com/zhaoyaoting/md_img/main/image-20211102150559856.png)

#### 14.高级查询

##### 	1.查询所有文档

在 Postman 中，向 ES 服务器发 GET 请求 ：http://127.0.0.1:9200/student/_search

```json
{
 "query": {
 "match_all": {}
 }
}
# "query"：这里的 query 代表一个查询对象，里面可以有不同的查询属性
# "match_all"：查询类型，例如：match_all(代表查询所有)， match，term ， range 等等
# {查询条件}：查询条件会根据类型的不同，写法也有差异
```

![image-20211102151239614](https://raw.githubusercontent.com/zhaoyaoting/md_img/main/image-20211102151239614.png)

```json
{
 "took【查询花费时间，单位毫秒】" : 1116,
 "timed_out【是否超时】" : false,
 "_shards【分片信息】" : {
 "total【总数】" : 1,
 "successful【成功】" : 1,
 "skipped【忽略】" : 0,
 "failed【失败】" : 0
 },
 "hits【搜索命中结果】" : {
 "total"【搜索条件匹配的文档总数】: {
 "value"【总命中计数的值】: 3,
 "relation"【计数规则】: "eq" # eq 表示计数准确， gte 表示计数不准确
 },
 "max_score【匹配度分值】" : 1.0,
 "hits【命中结果集合】" : [
 。。。
 }
 ]
 }
}

```



##### 2.匹配查询

match 匹配类型查询，会把查询条件进行分词，然后进行查询，多个词条之间是 or 的关系

​	在 Postman 中，向 ES 服务器发 GET 请求 ：http://127.0.0.1:9200/student/_search

![image-20211102152226256](https://raw.githubusercontent.com/zhaoyaoting/md_img/main/image-20211102152226256.png)

##### 3.字段匹配查询

multi_match 与 match 类似，不同的是它可以在多个字段中查询。

在 Postman 中，向 ES 服务器发 GET 请求 ：http://127.0.0.1:9200/student/_search

![image-20211102152918151](https://raw.githubusercontent.com/zhaoyaoting/md_img/main/image-20211102152918151.png)

##### 4.关键字精确查询

term 查询，精确的关键词匹配查询，不对查询条件进行分词。

在 Postman 中，向 ES 服务器发 GET 请求 ：http://127.0.0.1:9200/student/_search

![image-20211102153114852](https://raw.githubusercontent.com/zhaoyaoting/md_img/main/image-20211102153114852.png)

##### 5.多关键字精确查询

terms 查询和 term 查询一样，但它允许你指定多值进行匹配。 如果这个字段包含了指定值中的任何一个值，那么这个文档满足条件，类似于 mysql 的 in 

在 Postman 中，向 ES 服务器发 GET 请求 ：http://127.0.0.1:9200/student/_search

![image-20211102153302898](https://raw.githubusercontent.com/zhaoyaoting/md_img/main/image-20211102153302898.png)

##### 6.指定查询字段

默认情况下，Elasticsearch 在搜索的结果中，会把文档中保存在_source 的所有字段都返回。 如果我们只想获取其中的部分字段，我们可以添加_source 的过滤

在 Postman 中，向 ES 服务器发 GET 请求 ：http://127.0.0.1:9200/student/_search

![image-20211102153454857](https://raw.githubusercontent.com/zhaoyaoting/md_img/main/image-20211102153454857.png)

##### 7.过滤字段

includes：来指定想要显示的字段

excludes：来指定不想要显示的字段

在 Postman 中，向 ES 服务器发 GET 请求 ：http://127.0.0.1:9200/student/_search

includes

```json
{
    "_source": {
        "includes": [
            "name",
            "nickname"
        ]
    },
    "query": {
        "terms": {
            "nickname": [
                "zhangsan"
            ]
        }
    }
}
```

```json
{
    "_source": {
        "excludes": [
            "name",
            "nickname"
        ]
    },
    "query": {
        "terms": {
            "nickname": [
                "zhangsan"
            ]
        }
    }
}
```

##### 8.组合查询

`bool`把各种其它查询通过`must`（必须 ）、`must_not`（必须不）、`should`（应该）的方 式进行组合

在 Postman 中，向 ES 服务器发 GET 请求 ：http://127.0.0.1:9200/student/_search

```json
{
    "query": {
        "bool": {
            "must": [
                {
                    "match": {
                        "name": "zhangsan"
                    }
                }
            ],
            "must_not": [
                {
                    "match": {
                        "age": "40"
                    }
                }
            ],
            "should": [
                {
                    "match": {
                        "sex": "男"
                    }
                }
            ]
        }
    }
}
```

##### 9.范围查询

range 查询找出那些落在指定区间内的数字或者时间。range 查询允许以下字符

| 操作符 | 说明       |
| :----- | :--------- |
| gt     | 大于>      |
| gte    | 大于等于>= |
| lt     | 小于<      |
| lte    | 小于等于<= |

在 Postman 中，向 ES 服务器发 GET 请求 ：http://127.0.0.1:9200/student/_search

```json
{
    "query": {
        "range": {
            "age": {
                "gte": 30,
                "lte": 35
            }
        }
    }
}
```

##### 10.模糊查询

返回包含与搜索字词相似的字词的文档。 编辑距离是将一个术语转换为另一个术语所需的一个字符更改的次数。这些更改可以包括：

​	更改字符（box → fox）

​	删除字符（black → lack）

​	插入字符（sic → sick）

​	转置两个相邻字符（act → cat）

为了找到相似的术语，fuzzy 查询会在指定的编辑距离内创建一组搜索词的所有可能的变体 或扩展。然后查询返回每个扩展的完全匹配。

在 Postman 中，向 ES 服务器发 GET 请求 ：http://127.0.0.1:9200/student/_search

```json
{
    "query": {
        "fuzzy": {
            "title": {
                "value": "zhangsan",
                "fuzziness": 2
            }
        }
    }
}
```

##### 11.单字段排序

sort 可以让我们按照不同的字段进行排序，并且通过 order 指定排序的方式。desc 降序，asc 升序。

在 Postman 中，向 ES 服务器发 GET 请求 ：http://127.0.0.1:9200/student/_search

```json
{
    "query": {
        "match": {
            "name": "zhangsan"
        }
    },
    "sort": [
        {
            "age": {
                "order": "desc"
            }
        }
    ]
}
```

##### 12.多字段排序

假定我们想要结合使用 age 和 _score 进行查询，并且匹配的结果首先按照年龄排序，然后 按照相关性得分排序 

在 Postman 中，向 ES 服务器发 GET 请求 ：http://127.0.0.1:9200/student/_search

```json
{
    "query": {
        "match_all": {}
    },
    "sort": [
        {
            "age": {
                "order": "desc"
            }
        },
        {
            "_score": {
                "order": "desc"
            }
        }
    ]
}
```

##### 13.高亮查询

在进行关键字搜索时，搜索出的内容中的关键字会显示不同的颜色，称之为高亮。

Elasticsearch 可以对查询内容中的关键字部分，进行标签和样式(高亮)的设置。 在使用 match 查询的同时，加上一个 highlight 属性：

​	pre_tags：前置标签

​	post_tags：后置标签 

​	fields：需要高亮的字段

​	title：这里声明 title 字段需要高亮，后面可以为这个字段设置特有配置，也可以空

在 Postman 中，向 ES 服务器发 GET 请求 ：http://127.0.0.1:9200/student/_search

![image-20211102163003240](https://raw.githubusercontent.com/zhaoyaoting/md_img/main/image-20211102163003240.png)

##### 14.分页查询

from：当前页的起始索引，默认从 0 开始。 from = (pageNum - 1) * size size：每页显示多少条 

在 Postman 中，向 ES 服务器发 GET 请求 ：http://127.0.0.1:9200/student/_search

```json
{
    "query": {
        "match_all": {}
    },
    "sort": [
        {
            "age": {
                "order": "desc"
            }
        }
    ],
    "from": 0,
    "size": 2
}
```

##### 15.聚合查询

聚合允许使用者对 es 文档进行统计分析，类似与关系型数据库中的 group by，当然还有很 多其他的聚合，例如取最大值、平均值等等。

对某个字段取最大值 max

在 Postman 中，向 ES 服务器发 GET 请求 ：http://127.0.0.1:9200/student/_search

```json
{
    "aggs": {
        "max_age": {
            "max": {
                "field": "age"
            }
        }
    },
    "size": 0
}
```

对某个字段取最小值 min

```json
{
    "aggs": {
        "min_age": {
            "min": {
                "field": "age"
            }
        }
    },
    "size": 0
}
```

对某个字段求和 sum

```json
{
    "aggs": {
        "sum_age": {
            "sum": {
                "field": "age"
            }
        }
    },
    "size": 0
}
```

对某个字段取平均值 avg

```json
{
    "aggs": {
        "avg_age": {
            "avg": {
                "field": "age"
            }
        }
    },
    "size": 0
}
```

对某个字段的值进行去重之后再取总数

```json
{
    "aggs": {
        "distinct_age": {
            "cardinality": {
                "field": "age"
            }
        }
    },
    "size": 0
}
```

State 聚合

```json
{
    "aggs": {
        "stats_age": {
            "stats": {
                "field": "age"
            }
        }
    },
    "size": 0
}
```

##### 16.桶聚合查询

桶聚和相当于 sql 中的 group by 语句

terms 聚合，分组统计

在 Postman 中，向 ES 服务器发 GET 请求 ：http://127.0.0.1:9200/student/_search

```json
{
    "aggs": {
        "age_groupby": {
            "terms": {
                "field": "age"
            }
        }
    },
    "size": 0
}
```

在 terms 分组下再进行聚合

```json
{
    "aggs": {
        "age_groupby": {
            "terms": {
                "field": "age"
            },
            "aggs": {
                "sum_ages": {
                    "sum": {
                        "field": "age"
                    }
                }
            }
        }
    },
    "size": 0
}
```

postman 测试json[Readme1](https://raw.githubusercontent.com/zhaoyaoting/md_img/main/studyES.postman_collection.json)



### 2.5javaAPI操作

#### 1.导入maven依赖

```xml
<dependencies>
 <dependency>
 <groupId>org.elasticsearch</groupId>
 <artifactId>elasticsearch</artifactId>
 <version>7.8.0</version>
 </dependency>
 <!-- elasticsearch 的客户端 -->
 <dependency>
 <groupId>org.elasticsearch.client</groupId>
 <artifactId>elasticsearch-rest-high-level-client</artifactId>
 <version>7.8.0</version>
 </dependency>
 <!-- elasticsearch 依赖 2.x 的 log4j -->
 <dependency>
 <groupId>org.apache.logging.log4j</groupId>
 <artifactId>log4j-api</artifactId>
 <version>2.8.2</version>
 </dependency>
 <dependency>
 <groupId>org.apache.logging.log4j</groupId>
 <artifactId>log4j-core</artifactId>
 <version>2.8.2</version>
</dependency>
<dependency>
<groupId>com.fasterxml.jackson.core</groupId>
<artifactId>jackson-databind</artifactId>
<version>2.9.9</version>
</dependency>
 <!-- junit 单元测试 -->
 <dependency>
 <groupId>junit</groupId>
 <artifactId>junit</artifactId>
 <version>4.12</version>
 </dependency>
</dependencies>

```

#### 2.创建客户端

```java
// 创建客户端对象
RestHighLevelClient client = new RestHighLevelClient(
RestClient.builder(new HttpHost("localhost", 9200, "http"))
);
// 关闭客户端连接
client.close();
```

##### 2.1创建索引

```java
// 创建索引 - 请求对象
CreateIndexRequest request = new CreateIndexRequest("user");
// 发送请求，获取响应
CreateIndexResponse response = client.indices().create(request,
RequestOptions.DEFAULT);
boolean acknowledged = response.isAcknowledged();
// 响应状态
System.out.println("操作状态 = " + acknowledged);
```

##### 2.2查看索引

```java
// 查询索引 - 请求对象
GetIndexRequest request = new GetIndexRequest("user");
// 发送请求，获取响应
GetIndexResponse response = client.indices().get(request,
RequestOptions.DEFAULT);
System.out.println("aliases:"+response.getAliases());
System.out.println("mappings:"+response.getMappings());
System.out.println("settings:"+response.getSettings());

```

##### 2.3删除索引

```java
// 删除索引 - 请求对象
DeleteIndexRequest request = new DeleteIndexRequest("user");
// 发送请求，获取响应
AcknowledgedResponse response = client.indices().delete(request,
RequestOptions.DEFAULT);
// 操作结果
System.out.println("操作结果 ： " + response.isAcknowledged());
```

##### 2.4新增文档

```java
// 新增文档 - 请求对象
IndexRequest request = new IndexRequest();
// 设置索引及唯一性标识
request.index("user").id("1001");
// 创建数据对象
User user = new User();
user.setName("zhangsan");
user.setAge(30);
user.setSex("男");
ObjectMapper objectMapper = new ObjectMapper();
String productJson = objectMapper.writeValueAsString(user);
// 添加文档数据，数据格式为 JSON 格式
request.source(productJson,XContentType.JSON);
// 客户端发送请求，获取响应对象
IndexResponse response = client.index(request, RequestOptions.DEFAULT);
////3.打印结果信息
System.out.println("_index:" + response.getIndex());
System.out.println("_id:" + response.getId());
System.out.println("_result:" + response.getResult()); 
```

##### 2.5修改文档

```java
// 修改文档 - 请求对象
UpdateRequest request = new UpdateRequest();
// 配置修改参数
request.index("user").id("1001");
// 设置请求体，对数据进行修改
request.doc(XContentType.JSON, "sex", "女");
// 客户端发送请求，获取响应对象
UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
System.out.println("_index:" + response.getIndex());
System.out.println("_id:" + response.getId());
System.out.println("_result:" + response.getResult());

```

##### 2.6查询文档

```java
//1.创建请求对象
GetRequest request = new GetRequest().index("user").id("1001");
//2.客户端发送请求，获取响应对象
GetResponse response = client.get(request, RequestOptions.DEFAULT);
////3.打印结果信息
System.out.println("_index:" + response.getIndex());
System.out.println("_type:" + response.getType());
System.out.println("_id:" + response.getId());
System.out.println("source:" + response.getSourceAsString());

```

##### 2.7删除文档

```java
//创建请求对象
DeleteRequest request = new DeleteRequest().index("user").id("1");
//客户端发送请求，获取响应对象
DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
//打印信息
System.out.println(response.toString());
```

##### 2.8批量新增

```java
//创建批量新增请求对象
BulkRequest request = new BulkRequest();
request.add(new
IndexRequest().index("user").id("1001").source(XContentType.JSON, "name",
"zhangsan"));
request.add(new IndexRequest().index("user").id("1002").source(XContentType.JSON, "name",
"lisi"));
request.add(new
IndexRequest().index("user").id("1003").source(XContentType.JSON, "name",
"wangwu"));
//客户端发送请求，获取响应对象
BulkResponse responses = client.bulk(request, RequestOptions.DEFAULT);
//打印结果信息
System.out.println("took:" + responses.getTook());
System.out.println("items:" + responses.getItems());
```

##### 2.9批量删除

```java
//创建批量删除请求对象
BulkRequest request = new BulkRequest();
request.add(new DeleteRequest().index("user").id("1001"));
request.add(new DeleteRequest().index("user").id("1002"));
request.add(new DeleteRequest().index("user").id("1003"));
//客户端发送请求，获取响应对象
BulkResponse responses = client.bulk(request, RequestOptions.DEFAULT);
//打印结果信息
System.out.println("took:" + responses.getTook());
System.out.println("items:" + responses.getItems());

```

##### 2.10高级查询

###### 1.请求体查询

###### 1.1查询所有索引数据

```java
// 创建搜索请求对象
SearchRequest request = new SearchRequest();
request.indices("student");
// 构建查询的请求体
SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
// 查询所有数据
sourceBuilder.query(QueryBuilders.matchAllQuery());
request.source(sourceBuilder);
SearchResponse response = client.search(request, RequestOptions.DEFAULT);
// 查询匹配
SearchHits hits = response.getHits();
System.out.println("took:" + response.getTook());
System.out.println("timeout:" + response.isTimedOut());
System.out.println("total:" + hits.getTotalHits());
System.out.println("MaxScore:" + hits.getMaxScore());
System.out.println("hits========>>");
for (SearchHit hit : hits) {
//输出每条查询的结果信息
System.out.println(hit.getSourceAsString());
}
System.out.println("<<========");
```

###### 1.2term 查询，查询条件为关键字

```java
// 创建搜索请求对象
SearchRequest request = new SearchRequest();
request.indices("student");
// 构建查询的请求体
SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
sourceBuilder.query(QueryBuilders.termQuery("age", "30"));
request.source(sourceBuilder);
SearchResponse response = client.search(request, RequestOptions.DEFAULT);
// 查询匹配
SearchHits hits = response.getHits();
System.out.println("took:" + response.getTook());
System.out.println("timeout:" + response.isTimedOut());
System.out.println("total:" + hits.getTotalHits());
System.out.println("MaxScore:" + hits.getMaxScore());
System.out.println("hits========>>");
for (SearchHit hit : hits) {
//输出每条查询的结果信息
System.out.println(hit.getSourceAsString());
}
System.out.println("<<========");

```

###### 1.3分页查询

```java
// 创建搜索请求对象
SearchRequest request = new SearchRequest();
request.indices("student");
// 构建查询的请求体
SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
sourceBuilder.query(QueryBuilders.matchAllQuery());
// 分页查询
// 当前页其实索引(第一条数据的顺序号)，from
sourceBuilder.from(0);
// 每页显示多少条 size
sourceBuilder.size(2);
request.source(sourceBuilder);
SearchResponse response = client.search(request, RequestOptions.DEFAULT);
// 查询匹配
SearchHits hits = response.getHits();
System.out.println("took:" + response.getTook());
System.out.println("timeout:" + response.isTimedOut());
System.out.println("total:" + hits.getTotalHits());
System.out.println("MaxScore:" + hits.getMaxScore());
System.out.println("hits========>>");
for (SearchHit hit : hits) {
//输出每条查询的结果信息
System.out.println(hit.getSourceAsString());
}
System.out.println("<<========");
```

###### 1.4数据排序

```java
// 构建查询的请求体
SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
sourceBuilder.query(QueryBuilders.matchAllQuery());
// 排序
sourceBuilder.sort("age", SortOrder.ASC);
request.source(sourceBuilder);
SearchResponse response = client.search(request, RequestOptions.DEFAULT);
// 查询匹配
SearchHits hits = response.getHits();
System.out.println("took:" + response.getTook());
System.out.println("timeout:" + response.isTimedOut());
System.out.println("total:" + hits.getTotalHits());
System.out.println("MaxScore:" + hits.getMaxScore());
System.out.println("hits========>>");
for (SearchHit hit : hits) {
//输出每条查询的结果信息
System.out.println(hit.getSourceAsString());
}
System.out.println("<<========");
```

###### 1.5过滤字段

```java
// 创建搜索请求对象
SearchRequest request = new SearchRequest();
request.indices("student");
// 构建查询的请求体
SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
sourceBuilder.query(QueryBuilders.matchAllQuery());
//查询字段过滤
String[] excludes = {};
String[] includes = {"name", "age"};
sourceBuilder.fetchSource(includes, excludes);
request.source(sourceBuilder);
SearchResponse response = client.search(request, RequestOptions.DEFAULT);
// 查询匹配
SearchHits hits = response.getHits();
System.out.println("took:" + response.getTook());
System.out.println("timeout:" + response.isTimedOut());
System.out.println("total:" + hits.getTotalHits());
System.out.println("MaxScore:" + hits.getMaxScore());
System.out.println("hits========>>");
for (SearchHit hit : hits) {
//输出每条查询的结果信息
System.out.println(hit.getSourceAsString());
}
System.out.println("<<========");
```

###### 1.6Bool 查询

```java
// 创建搜索请求对象
SearchRequest request = new SearchRequest();
request.indices("student");
// 构建查询的请求体
SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
// 必须包含
boolQueryBuilder.must(QueryBuilders.matchQuery("age", "30"));
// 一定不含
boolQueryBuilder.mustNot(QueryBuilders.matchQuery("name", "zhangsan"));
// 可能包含
boolQueryBuilder.should(QueryBuilders.matchQuery("sex", "男"));
sourceBuilder.query(boolQueryBuilder);
request.source(sourceBuilder);
SearchResponse response = client.search(request, RequestOptions.DEFAULT);
// 查询匹配
SearchHits hits = response.getHits();
System.out.println("took:" + response.getTook());
System.out.println("timeout:" + response.isTimedOut());
System.out.println("total:" + hits.getTotalHits());
System.out.println("MaxScore:" + hits.getMaxScore());
System.out.println("hits========>>");
for (SearchHit hit : hits) {
//输出每条查询的结果信息
System.out.println(hit.getSourceAsString());
}
System.out.println("<<========");
```

###### 1.7范围查询

```java
// 创建搜索请求对象
SearchRequest request = new SearchRequest();
request.indices("student");
// 构建查询的请求体
SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("age");
// 大于等于
rangeQuery.gte("30");
// 小于等于
rangeQuery.lte("40");
sourceBuilder.query(rangeQuery);
request.source(sourceBuilder);
SearchResponse response = client.search(request, RequestOptions.DEFAULT);
// 查询匹配
SearchHits hits = response.getHits();
System.out.println("took:" + response.getTook());
System.out.println("timeout:" + response.isTimedOut());
System.out.println("total:" + hits.getTotalHits());
System.out.println("MaxScore:" + hits.getMaxScore());
System.out.println("hits========>>");
for (SearchHit hit : hits) {
//输出每条查询的结果信息
System.out.println(hit.getSourceAsString());
}
System.out.println("<<========");

```

###### 1.8模糊查询

```java
// 创建搜索请求对象
SearchRequest request = new SearchRequest();
request.indices("student");
// 构建查询的请求体
SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
sourceBuilder.query(QueryBuilders.fuzzyQuery("name","zhangsan").fuzziness(Fu
zziness.ONE));
request.source(sourceBuilder);
SearchResponse response = client.search(request, RequestOptions.DEFAULT);
// 查询匹配
SearchHits hits = response.getHits();
System.out.println("took:" + response.getTook());
System.out.println("timeout:" + response.isTimedOut());
System.out.println("total:" + hits.getTotalHits());
System.out.println("MaxScore:" + hits.getMaxScore());
System.out.println("hits========>>");
for (SearchHit hit : hits) {
//输出每条查询的结果信息
System.out.println(hit.getSourceAsString());
}
System.out.println("<<========");
```

###### 1.9高亮查询

```java
// 高亮查询
SearchRequest request = new SearchRequest().indices("student");
//2.创建查询请求体构建器
SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
//构建查询方式：高亮查询
TermsQueryBuilder termsQueryBuilder =
QueryBuilders.termsQuery("name","zhangsan");
//设置查询方式
sourceBuilder.query(termsQueryBuilder);
//构建高亮字段
HighlightBuilder highlightBuilder = new HighlightBuilder();
highlightBuilder.preTags("<font color='red'>");//设置标签前缀
highlightBuilder.postTags("</font>");//设置标签后缀
highlightBuilder.field("name");//设置高亮字段
//设置高亮构建对象
sourceBuilder.highlighter(highlightBuilder);
//设置请求体
request.source(sourceBuilder);
//3.客户端发送请求，获取响应对象
SearchResponse response = client.search(request, RequestOptions.DEFAULT);
//4.打印响应结果
SearchHits hits = response.getHits();
System.out.println("took::"+response.getTook());
System.out.println("time_out::"+response.isTimedOut());
System.out.println("total::"+hits.getTotalHits());
System.out.println("max_score::"+hits.getMaxScore());
System.out.println("hits::::>>");
for (SearchHit hit : hits) {
String sourceAsString = hit.getSourceAsString();
System.out.println(sourceAsString);
//打印高亮结果
Map<String, HighlightField> highlightFields = hit.getHighlightFields();
System.out.println(highlightFields);
}
System.out.println("<<::::");
```

###### 1.10聚合查询

​	最大年龄

```java
// 最大年龄
SearchRequest request = new SearchRequest().indices("student");
SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
sourceBuilder.aggregation(AggregationBuilders.max("maxAge").field("age"));
//设置请求体
request.source(sourceBuilder);
//3.客户端发送请求，获取响应对象
SearchResponse response = client.search(request, RequestOptions.DEFAULT);
//4.打印响应结果
SearchHits hits = response.getHits();
System.out.println(response);

```

​	分组统计

```java
// 分组统计
SearchRequest request = new SearchRequest().indices("student");
SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
sourceBuilder.aggregation(AggregationBuilders.terms("age_groupby").field("ag
e"));
//设置请求体
request.source(sourceBuilder);
//3.客户端发送请求，获取响应对象
SearchResponse response = client.search(request, RequestOptions.DEFAULT);
//4.打印响应结果
SearchHits hits = response.getHits();
System.out.println(response);
```

### 2.6Spring Data Elasticsearch

1.pom文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
http://maven.apache.org/xsd/maven-4.0.0.xsd">
 <modelVersion>4.0.0</modelVersion>
 <parent>
 <groupId>org.springframework.boot</groupId>
 <artifactId>spring-boot-starter-parent</artifactId>
 <version>2.3.6.RELEASE</version>
 <relativePath/>
 </parent>
 <groupId>com.atguigu.es</groupId>
 <artifactId>springdata-elasticsearch</artifactId>
 <version>1.0</version>
 <properties>
 <maven.compiler.source>8</maven.compiler.source>
 <maven.compiler.target>8</maven.compiler.target>
 </properties>
 <dependencies>
 <dependency>
 <groupId>org.projectlombok</groupId>
 <artifactId>lombok</artifactId>
 </dependency>
 <dependency>
 <groupId>org.springframework.boot</groupId>
 <artifactId>spring-boot-starter-data-elasticsearch</artifactId>
 </dependency>
 <dependency>
 <groupId>org.springframework.boot</groupId>
 <artifactId>spring-boot-devtools</artifactId>
     <scope>runtime</scope>
 <optional>true</optional>
 </dependency>
 <dependency>
 <groupId>org.springframework.boot</groupId>
 <artifactId>spring-boot-starter-test</artifactId>
 <scope>test</scope>
 </dependency>
 <dependency>
 <groupId>org.springframework.boot</groupId>
 <artifactId>spring-boot-test</artifactId>
 </dependency>
 <dependency>
 <groupId>junit</groupId>
 <artifactId>junit</artifactId>
 </dependency>
 <dependency>
 <groupId>org.springframework</groupId>
 <artifactId>spring-test</artifactId>
 </dependency>
 </dependencies>
</project>
```



