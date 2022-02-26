## 1.设置登录系统的账号和密码（不推荐）

### 1.1在 application.properties

```properties
spring.security.user.name=lijingbiao
spring.security.user.password=950211
```

### 1.2编写类实现接口

 SecurityConfig.class

该类用于注册所需要的类

```java
@Configuration
public class SecurityConfig {
	// 注入 PasswordEncoder 类到 spring 容器中
	@Bean
	public PasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();
	} 
}
```

实现UserDetailsService 用于加载用户名和密码

```java
@Service
public class LoginService implements UserDetailsService {
    @Override
	public UserDetails loadUserByUsername(String username) throws  UsernameNotFoundException {
        // 判断用户名是否存在
        if (!"admin".equals(username)){
			throw new UsernameNotFoundException("用户名不存在！");
    	}
    	// 从数据库中获取的密码 atguigu 的密文
        String pwd =  "$2a$10$2R/M6iU3mCZt3ByG7kwYTeeW0w7/UqdeXrb27zkBIizBvAven0/na";
		// 第三个参数表示权限
		return new User(username,pwd, AuthorityUtils.commaSeparatedStringToAuthorityList("admin,"));
	} 
}
```

## 2.通过访问数据库中数据来认证用户登录（推荐）

### **添加依赖**

```xml
<dependencies>
     <dependency>
         <groupId>org.springframework.boot</groupId>
         <artifactId>spring-boot-starter-web</artifactId>
     </dependency>
     <dependency>
         <groupId>org.springframework.boot</groupId>
         <artifactId>spring-boot-starter-security</artifactId>
     </dependency>
     <dependency>
         <groupId>org.springframework.boot</groupId>
         <artifactId>spring-boot-starter-test</artifactId>
     	 <scope>test</scope>
     </dependency>
     <!--mybatis-plus-->
     <dependency>
         <groupId>com.baomidou</groupId>
         <artifactId>mybatis-plus-boot-starter</artifactId>
     	 <version>3.0.5</version>
     </dependency>
     <!--mysql-->
     <dependency>
         <groupId>mysql</groupId>
         <artifactId>mysql-connector-java</artifactId>
     </dependency>
     <!--lombok 用来简化实体类-->
     <dependency>
         <groupId>org.projectlombok</groupId>
         <artifactId>lombok</artifactId>
     </dependency>
</dependencies>
```

### **制作实体类**

```java
@Data
public class Users {
    private Integer id;
 	private String username;
 	private String password; 
}
```

### **整合** **MybatisPlus** **制作** **mapper**

```java
@Repository
public interface UsersMapper extends BaseMapper<Users> {
}

```

```properties
# 配置文件添加数据库配置 
# mysql 数据库连接
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver 
spring.datasource.url=jdbc:mysql://localhost:3306/demo?serverTimezone=GMT%2B8 
spring.datasource.username=root 
spring.datasource.password=root
```

### **实现UserDetailService类**

```java
public class MyUserDetailsService implements UserDetailsService {
    @Resource
    private UsersMapper usersMapper;
    
    @Override
 	public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
 		QueryWrapper<Users> wrapper = new QueryWrapper();
 		wrapper.eq("username",s);
 		Users users = usersMapper.selectOne(wrapper);
 		if(users == null) {
 			throw new UsernameNotFoundException("用户名不存在！");
		}
		 System.out.println(users);
 		List<GrantedAuthority> auths =
 		AuthorityUtils.commaSeparatedStringToAuthorityList("role");
 		return new User(users.getUsername(), new BCryptPasswordEncoder().encode(users.getPassword()),auths);
	} 
}
```



### **安全配置类**

```java
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Resource
    private UserFeignDetailsService userDetailService;
    
    @Resource
    private PasswordEncoder passwordEncoder;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        // 如果需要特殊处理加解密操作，例如加上超级密码，默认通过验证，可以继承BCryptPasswordEncoder，在match方法里实现
        return new BCryptPasswordEncoder();
    }
    
     /**
     * AuthenticationManagerBuilder用于创建AuthenticationManager。
     * 允许轻松构建内存身份验证，LDAP身份验证，基于JDBC的身份验证，添加UserDetailsService以及添加AuthenticationProvider
     */
    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
		// 根据输入的用户名查找用户
        auth.userDetailsService(this.userDetailService)
                // 和用户提交的密码通过passwordEncoder进行比对校验
                .passwordEncoder(this.passwordEncoder());
    }
    
    //安全拦截机制（最重要）
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/r/**").authenticated()//所有/r/**的请求必须认证通过
                .anyRequest().permitAll()//除了/r/**，其它的请求可以访问
                .and()
                .formLogin()//允许表单登录
                .successForwardUrl("/login-success");//自定义登录成功的页面地址

    }
}
```

