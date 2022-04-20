做单元测试的时候经常会引用第三方服务，导致本地运行单元测试失败。这时候推荐使用mock的方法，把第三方服务mock出来，这样单元测试就可以验证其他逻辑了。



mockito：https://github.com/mockito/mockito

简介：Mockito 是一个针对 Java 的单元测试模拟框架，它与 EasyMock 和 jMock 很相似，都是为了简化单元测试过程中测试上下文 ( 或者称之为测试驱动函数以及桩函数 ) 的搭建而开发的工具



powermock[：https://github.com/powermock/powermock](https://github.com/powermock/powermock)

简介：EasyMock 以及 Mockito 都因为可以极大地简化单元测试的书写过程而被许多人应用在自己的工作中，但是这 2 种 Mock 工具都不可以实现对**静态函数、构造函数、私有函数、Final 函数以及系统函数**的模拟，但是这些方法往往是我们在大型系统中需要的功能。PowerMock 是在 EasyMock 以及 Mockito 基础上的扩展，通过定制类加载器等技术，PowerMock 实现了之前提到的所有模拟功能，使其成为大型系统上单元测试中的必备工具

### 框架原理区别

> 因为Mockito使用继承的方式实现mock的，用CGLIB生成mock对象代替真实的对象进行执行，为了mock实例的方法，你可以在subclass中覆盖它，而static方法是不能被子类覆盖的，所以Mockito不能mock静态方法。
> 但PowerMock可以mock静态方法，因为它直接在bytecode上工作。