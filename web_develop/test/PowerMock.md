All usages require `@RunWith(PowerMockRunner.class)` and `@PrepareForTest` annotated at class level.



## 使用 PowerMock 测试私有方法

Mock static、final、private 方法等

`Whitebox.invokeMethod()`前两个参数分别为需要调用的类的实例、静态方法的名称，后面的不定长参数为调用目标方法的参数。



# Reference 

https://github.com/powermock/powermock/wiki/Mockito

