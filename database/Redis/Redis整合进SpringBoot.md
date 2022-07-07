Springboot以Repository方式整合Redis



# 1. 引入相关依赖

引入`Springboot Web`的依赖，以启动REST服务。还需要引入`Spring Data Redis`相关的依赖。最后，还需要`commons-pool2`，不然会因为缺少类而无法启动。

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
<dependency>
  <groupId>org.apache.commons</groupId>
  <artifactId>commons-pool2</artifactId>
</dependency>
```



# 2.配置连接信息



配置`Redis`的连接信息，这个信息跟你安装时的配置有关，同时配置了连接池，各项的配置及相关解释如下

```
# Redis数据库索引，默认为0
spring.redis.database=0
# Redis端口
spring.redis.port=6379
# Redis服务器主机
spring.redis.host=localhost
# 连接池最大连接数
spring.redis.lettuce.pool.max-active=8
# 连接池最大空闲
spring.redis.lettuce.pool.max-idle=8
# 连接池最小空闲
spring.redis.lettuce.pool.min-idle=2
# 连接池最大阻塞等待时间
spring.redis.lettuce.pool.max-wait=1ms
# 超时时间
spring.redis.lettuce.shutdown-timeout=100ms
```



# 3.创建实体类

存入`Redis`中的数据类型，可以是自定义的一个类，注意需要加上注解`@RedisHash`和`@Id`。存入`Redis`的数据为`Set`类型.

```xml
package com.pkslow.redis.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import java.util.Date;
import lombok.Data;

@RedisHash("User")
@Data 
public class User {
    @Id
    private String userId;
    
    private String name;
    
    private Integer age;
    
    private Date createTime;

    
}
```



# 4.数据库访问层UserRepository接口

直接继承`CrudRepository`接口就行了，不用自己来实现，需要注意`CrudRepository<User, String>`的泛型类型：

```java
package com.pkslow.redis.dal;
import com.pkslow.redis.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, String> {
}

```

# 5. 实现Controller

`Controller`实现了`RESTful`风格的增删改查功能，只要把`UserRepository`注入便可以使用它来操作：

```java
package com.pkslow.redis.controller;

import com.pkslow.redis.dal.UserRepository;
import com.pkslow.redis.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("")
    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{userId}")
    public User getByUserId(@PathVariable String userId) {
        return userRepository.findById(userId).orElse(new User());
    }

    @PostMapping("")
    public User addNewUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    @DeleteMapping("/{userId}")
    public String delete(@PathVariable String userId) {
        User user = new User();
        user.setUserId(userId);
        userRepository.deleteById(userId);
        return "deleted: " + userId;
    }

    @PutMapping("")
    public User update(@RequestBody User user) {
        return userRepository.save(user);
    }
}
```

