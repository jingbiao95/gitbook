## SpringBoot默认的返回格式

### 第一种：返回 String

```java
@GetMapping("/hello")
public String getStr(){
  return "hello springboot";
}
```

此时调用接口获取到的返回值是这样：

```tex
hello springboot
```





### 第二种：返回自定义对象

```java
@GetMapping("/aniaml")
public Aniaml getAniaml(){
  Aniaml aniaml = new Aniaml(1,"pig");
  return aniaml;
}
```

返回值

```
{
  "id": 1,
  "name": "pig"
}
```



### 第三种：接口异常

```java
@GetMapping("/error")
public int error(){
    int i = 9/0;
    return i;
}
```

返回值

```
{
  "timestamp": "2021-07-08T08:05:15.423+00:00",
  "status": 500,
  "error": "Internal Server Error",
  "path": "/wrong"
}
```



## SpringBoot统一返回格式教程

### 引入依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

### 统一返回接口

#### 统一响应体定义

```java
@ApiModel(value = "响应")
@Data
public class ResponseDTO<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 编码：0000000表示成功，其他值表示失败
     */
    @ApiModelProperty(value = "编码：0000000 表示成功，其他值表示失败")
    private String code;

    /**
     * 消息内容
     */
    @ApiModelProperty(value = "消息内容")
    private String message;

    /**
     * 响应数据
     */
    @ApiModelProperty(value = "响应数据")
    private T body;

    /**
     * 响应时间
     */
    @ApiModelProperty(value = "响应时间")
    private Long timestamp = System.currentTimeMillis();

    public ResponseDTO() {

    }

    public ResponseDTO(String code, String message, T body) {

        this.code = code;
        this.message = message;
        this.body = body;
    }

    public ResponseDTO(final String code, final String message) {

        this.code = code;
        this.message = message;
    }

    public ResponseDTO(final IResponse response) {

        this.code = response.getCode();
        this.message = response.getMessage();
    }

    public ResponseDTO(final IResponse response, final T body) {

        this.code = response.getCode();
        this.message = response.getMessage();
        this.body = body;
    }
}
```

#### 枚举状态码定义

```java
public interface IResponse extends Serializable {

    /**
     * @return response code
     */
    String getCode();

    /**
     * @return response message
     */
    String getMessage();
}
```

在各个微服务中异常枚举类只要实现了IResponse即可

```java
public enum BaseResponseEnum implements IResponse {

    /**
     * "000000", "SUCCESS"
     */
    SUCCESS("000000000000", "SUCCESS"),

    /**
     * "100000", "业务错误"
     */
    BUSINESS_EXCEPTION("100000000000", "业务错误"),

    /**
     * "200000", "参数校验错误"
     */
    PARAMETER_EXCEPTION("200000000000", "参数校验错误"),

    /**
     * "300000", "权限错误"
     */
    AUTHORITY_EXCEPTION("300000000000", "权限不足"),

    /**
     * "400000", "系统错误"
     */
    SYSTEM_EXCEPTION("400000000000", "系统错误");

    private final String code;

    private final String message;

    BaseResponseEnum(final String code, final String message) {

        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {

        return this.code;
    }

    @Override
    public String getMessage() {

        return this.message;
    }

   
}
```

#### 统一ResponseBodyAdvice

```java
@RestControllerAdvice
public class ResponseBodyHandler implements ResponseBodyAdvice<Object> {

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 是否支持advice功能 <br/>
     * true 支持，false 不支持
     *
     * @param returnType
     * @param converterType
     * @return
     */
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 如果接口返回的类型本身就是ResponseDTO那就没有必要进行额外的操作，返回false

        return true;
    }

    @SneakyThrows
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        // String类型在 Springboot有特殊用法，若是直接返回值，则需要处理下
        // 如果Controller直接返回String的话，SpringBoot是直接返回，故我们需要手动转换成json
        if (body instanceof String) {
            // 将数据包装在ResponseDTO里后，再转换为json字符串响应给前端
            return objectMapper.writeValueAsString(ResponseUtil.wrapSuccess(body));
        }
        // 可在此处也可在上面
        if (body instanceof ResponseDTO) {
            return body;
        }
        return ResponseUtil.wrapSuccess(body);

    }
}
```

**ResponseBodyAdvice的作用**：拦截Controller方法的返回值，统一处理返回值/响应体，一般用来统一返回格式，加解密，签名等等。



##### `@RestControllerAdvice`注解

`@RestControllerAdvice`是 `@RestController`注解的增强，可以实现三个方面的功能：

1. 全局异常处理
2. 全局数据绑定
3. 全局数据预处理



#### 异常统一处理类

```java

/**
 * 全局异常处理器
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 默认异常处理
     *
     * @param e
     * @return ResponseDTO
     */
    @ExceptionHandler({Exception.class})
    public ResponseDTO defaultHandleException(final Exception e) {

        log.error(e.getMessage(), e);
        return ResponseUtil.wrapException(e);
    }

    /**
     * 处理业务异常
     *
     * @param e
     * @return ResponseDTO
     */
    @ExceptionHandler({ApplicationException.class})
    public ResponseDTO handleBusinessException(final ApplicationException e) {

        log.error(e.getMessage(), e);
        return ResponseUtil.wrapException(e);
    }

    /**
     * 处理自定义BusinessException业务异常
     *
     * @param e
     * @return ResponseDTO
     */
    @ExceptionHandler({BusinessException.class})
    public ResponseDTO handleBusinessException(final BusinessException e) {

        log.error(e.getMessage(), e);
        return ResponseUtil.wrapException(e.getCode(), e.getMessage());
    }

    /**
     * 处理数据校验异常
     *
     * @param e
     * @return ResponseDTO
     */
    @ExceptionHandler({BindException.class, MethodArgumentNotValidException.class})
    public ResponseDTO handleBusinessException(final Exception e) {

        log.error(e.getMessage(), e);

        List<FieldError> errors = null;
        if (e instanceof BindException) {
            errors = ((BindException) e).getFieldErrors();
        } else {
            errors = ((MethodArgumentNotValidException) e).getBindingResult().getFieldErrors();
        }

        return ResponseUtil.wrapException(new ParameterException(this.getFieldErrorString(errors)));
    }

    /**
     * 获取校验异常描述
     *
     * @param errors
     * @return
     */
    private String getFieldErrorString(final List<FieldError> errors) {

        final StringBuilder sb = new StringBuilder();
        for (final FieldError error : errors) {
            sb.append(error.getField() + ":" + error.getDefaultMessage());
            sb.append("\n");
        }
        return sb.toString();
    }
}
```

1. `@RestControllerAdvice`，RestController的增强类，可用于实现全局异常处理器
2. `@ExceptionHandler`,统一处理某一类异常，从而减少代码重复率和复杂度，比如要获取自定义异常可以`@ExceptionHandler(BusinessException.class)`
3. `@ResponseStatus`指定客户端收到的http状态码




#### ResponseUtil

```java
/**
 * Response util.
 */
public final class ResponseUtil {

    /**
     * private constructor.
     */
    private ResponseUtil() {

    }

    /**
     * Wrap null response body of success.
     *
     * @return ResponseDTO
     */
    public static ResponseDTO wrapSuccess() {

        return new ResponseDTO(BaseResponseEnum.SUCCESS);
    }

    /**
     * Wrap response body of success.
     *
     * @param body returned object
     * @return ResponseDTO
     */
    public static ResponseDTO wrapSuccess(final Object body) {

        return new ResponseDTO(BaseResponseEnum.SUCCESS, body);
    }

    /**
     * @param code    error code
     * @param message error message
     * @return ResponseDTO
     */
    public static ResponseDTO wrapException(final String code, final String message) {

        return new ResponseDTO(code, message);
    }

    /**
     * @param e ApplicationException
     * @return ResponseDTO
     */
    public static ResponseDTO wrapException(final ApplicationException e) {

        ResponseDTO response = new ResponseDTO();
        response.setCode(e.getCode());
        response.setMessage(e.getMessage());
        return response;
    }

    /**
     * @param e Exception
     * @return ResponseDTO
     */
    public static ResponseDTO wrapException(final Exception e) {

        return wrapException(new SystemException(e));
    }

}
```

