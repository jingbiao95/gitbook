## 交叉连接（CROSS JOIN）

```sql
SELECT * FROM A,B(,C)或者SELECT * FROM A CROSS JOIN B (CROSS JOIN C)
```

没有任何关联条件，结果是**笛卡尔积**，结果集会很大，没有意义



## 内连接（INNER JOIN）

- 等值连接：ON A.id=B.id
- 不等值连接：ON A.id > B.id
- 自连接：SELECT * FROM A T1 INNER JOIN A T2 ON T1.id=T2.pid



## 外连接（LEFT JOIN/RIGHT JOIN）

### 左外连接：

LEFT OUTER JOIN, **以左表为主**，先查询出左表，按照ON后的关联条件匹配右表，没有匹配到的用NULL填充，可以简写成LEFT JOIN

### 右外连接：

RIGHT OUTER JOIN, **以右表为主**，先查询出右表，按照ON后的关联条件匹配左表，没有匹配到的用NULL填充，可以简写成RIGHT JOIN






## 联合查询（UNION与UNION ALL）

```sql
SELECT * FROM A UNION SELECT * FROM B UNION ...
```

- 就是把多个结果集集中在一起，**UNION前**的结果为**基准**，需要注意的是联合查询的列数要相等，相同的记录行会合并
- 如果使用UNION ALL，不会合并重复的记录行
- 效率 UNION ALL 高于 UNION



## 全连接（FULL JOIN）

>  MySQL不支持

可以使用LEFT JOIN 和UNION和RIGHT JOIN联合使用

`SELECT * FROM A LEFT JOIN B ON A.id=B.id UNION SELECT * FROM A RIGHT JOIN B ON A.id=B.id`



## 示例：

### 测试表

- R表

  | A    | B    | C    |
  | ---- | ---- | ---- |
  | a1   | b1   | c1   |
  | a2   | b2   | c2   |
  | a3   | b3   | c3   |

- S表

  | C    | D    |
  | ---- | ---- |
  | c1   | d1   |
  | c2   | d2   |
  | c4   | d3   |



### 关联测试

交叉连接

`select R.*,S.* from R,S`

| A    | B    | C    | C    | D    |
| ---- | ---- | ---- | ---- | ---- |
| a1   | b1   | c1   | c1   | d1   |
| a2   | b2   | c2   | c1   | d1   |
| a3   | b3   | c3   | c1   | d1   |
| a1   | b1   | c1   | c2   | d2   |
| a2   | b2   | c2   | c2   | d2   |
| a3   | b3   | c3   | c2   | d2   |
| a1   | b1   | c1   | c4   | d3   |
| a2   | b2   | c2   | c4   | d3   |
| a3   | b3   | c3   | c4   | d3   |



### 内连接

`select R.*,S.* from R inner join S on R.C=S.C`

| A    | B    | C    | C    | D    |
| ---- | ---- | ---- | ---- | ---- |
| a1   | b1   | c1   | c1   | d1   |
| a2   | b2   | c2   | c2   | d2   |

### 左连接

`select R.*,S.* from R left join S on R.C=S.C`



| A    | B    | C    | C    | D    |
| ---- | ---- | ---- | ---- | ---- |
| a1   | b1   | c1   | c1   | d1   |
| a2   | b2   | c2   | c2   | d2   |
| a3   | b3   | c3   |      |      |

### 右连接

`select R.*,S.* from R right join S on R.C=S.C`

| A    | B    | C    | C    | D    |
| ---- | ---- | ---- | ---- | ---- |
| a1   | b1   | c1   | c1   | d1   |
| a2   | b2   | c2   | c2   | d2   |
|      |      |      | c4   | d3   |



### 全连接

>  MySql不支持，Oracle支持

`select R.*,S.* from R full join S on R.C=S.C`

| A    | B    | C    | C    | D    |
| ---- | ---- | ---- | ---- | ---- |
| a1   | b1   | c1   | c1   | d1   |
| a2   | b2   | c2   | c2   | d2   |
| a3   | b3   | c3   |      |      |
|      |      |      | c4   | d3   |