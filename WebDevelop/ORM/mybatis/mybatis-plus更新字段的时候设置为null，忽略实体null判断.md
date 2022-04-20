在实体类对应的字段上加注解**@TableField(strategy=FieldStrategy.IGNORED)**，忽略null值的判断



```java
@TableField(strategy = FieldStrategy.IGNORED)
private String name;
```



其中策略

```java
public enum FieldStrategy {
    /**
     * 忽略判断
     */
    IGNORED,
    /**
     * 非NULL判断
     */
    NOT_NULL,
    /**
     * 非空判断
     */
    NOT_EMPTY,
    /**
     * 默认的,一般只用于注解里
     * <p>1. 在全局里代表 NOT_NULL</p>
     * <p>2. 在注解里代表 跟随全局</p>
     */
    DEFAULT
}
```



# reference 

[mybatis-plus更新字段的时候设置为null，忽略实体null判断](https://blog.csdn.net/qq_39403545/article/details/85334250)