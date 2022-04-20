MyBatis-plus是一款MyBatis的增强工具，在MyBatis 的基础上只做增强不做改变。其是国内团队苞米豆在MyBatis基础上开发的增强框架，扩展了一些功能，以提高效率。引入 Mybatis-Plus 不会对现有的 Mybatis 构架产生任何影响，而且 MyBatis-plus 支持所有 Mybatis 原生的特性



1）依赖少：仅仅依赖 Mybatis 以及 Mybatis-Spring 。

2）损耗小：启动即会自动注入基本 CURD，性能基本无损耗，直接面向对象操作 。

3）预防Sql注入：内置 Sql 注入剥离器，有效预防Sql注入攻击 。

4）通用CRUD操作：内置通用 Mapper、通用 Service，仅仅通过少量配置即可实现单表大部分 CRUD 操作，更有强大的条件构造器，满足各类使用需求 。

5）多种主键策略：支持多达4种主键策略（内含分布式唯一ID生成器），可自由配置，完美解决主键问题 。

6）支持热加载：Mapper 对应的 XML 支持热加载，对于简单的 CRUD 操作，甚至可以无 XML 启动

7）支持ActiveRecord：支持 ActiveRecord 形式调用，实体类只需继承 Model 类即可实现基本 CRUD 操作

8）支持代码生成：采用代码或者 Maven 插件可快速生成 Mapper 、 Model 、 Service 、 Controller 层代码（生成自定义文件，避免开发重复代码），支持模板引擎、有超多自定义配置等。

9）支持自定义全局通用操作：支持全局通用方法注入（ Write once, use anywhere ）。

10）支持关键词自动转义：支持数据库关键词（order、key…）自动转义，还可自定义关键词 。

11）内置分页插件：基于 Mybatis 物理分页，开发者无需关心具体操作，配置好插件之后，写分页等同于普通List查询。

12）内置性能分析插件：可输出 Sql 语句以及其执行时间，建议开发测试时启用该功能，能有效解决慢查询 。

13）内置全局拦截插件：提供全表 delete 、 update 操作智能分析阻断，预防误操作。

14）默认将实体类的类名查找数据库中的表，使用@TableName(value="table1")注解指定表名，@TableId指定表主键，若字段与表中字段名保持一致可不加注解。



作者：小院看客
链接：https://www.jianshu.com/p/8556c8468241
来源：简书
著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。