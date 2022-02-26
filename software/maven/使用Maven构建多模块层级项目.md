Maven多模块项目,适用于一些比较大的项目，通过合理的模块拆分，实现代码的复用，便于维护和管理。尤其是一些开源框架，也是采用多模块的方式，提供插件集成，用户可以根据需要配置指定的模块。

　　项目结构如下：

 　　　　test-hd-parent 　(父级)
    　　　　 ---pom.xml
    　　　　 ---test-hd-api  　　  (第三方接口层)
       　　　　　 ----pom.xml   
  　　　　　 ---test-hd-foundation   (基础工具层)
       　　　　　 ----pom.xml
    　　　　 ---test-hd-resource　  (资源层) 
        　　　　　　----pom.xml
    　　　　 ---test-hd-service 　　  (逻辑业务层)
       　　　　　 ----pom.xml
  　　　　　  ---test-hd-modules 　　 (web层)
       　　　　　　----pom.xml
   　　　 　　 　　---test-hd-www  　 　　(web模块1)
         　　　　　 　　 ----pom.xml
   　　　 　　 　　---test-hd-admin 　  　　(web模块2)
         　　　　　 　　 ----pom.xm



在多模块的工程中，如果模块之间存在依赖关系，那模块的编译必须按照顺序，