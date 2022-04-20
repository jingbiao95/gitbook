## SQL 约束种类

- NOT NULL:
  用于控制字段的内容一定不能为空（NULL）。
- UNIQUE:
  控件字段内容不能重复，一个表允许有多个 Unique 约束。
- PRIMARY KEY:
  也是用于控件字段内容不能重复，但它在一个表只允许出现一个。
- FOREIGN KEY:
  用于预防破坏表之间连接的动作，也能防止非法数据插入外键列，因为它必须是它指向的那个表中的值之一。
- CHECK:
  用于控制字段的值范围。



## UNION与UNION ALL的区别？

- 如果使用UNION ALL，不会合并重复的记录行
- 效率 UNION ALL 高于 UNION