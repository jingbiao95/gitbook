# 通过maven，给没有pom文件的jar包生成pom文件，maven项目引入本地jar包

命令：

```
mvn install:install-file -DgroupId=common_util -DartifactId=common_util -Dversion=3.13 -Dfile=D:/jar/common_util-3.13.jar -Dpackaging=jar -DgeneratePom=true
```



DgroupId：是项目组织唯一的标识符，自己随便起名
DartifactId：项目的唯一的标识符，自己可以随便起
Dversion：项目版本
Dfile：jar包路径（绝对路径）
DgeneratePom：是否生成pom文件，ture:生成，false：不生成

执行成功，会在本地的maven jar包目录下以下结果




```shell
mvn install:install-file -DgroupId=io.github.xhystc -DartifactId=cozy -Dversion=1.1.0 -Dfile=io.github.xhystc.cozy-1.1.0.jar -Dpackaging=jar -DgeneratePom=true

mvn install:install-file -DgroupId=com.nsfocus.bsa -DartifactId=parameter_lib -Dversion=1.0 -Dfile=com.nsfocus.bsa.parameter_lib-1.0.jar -Dpackaging=jar -DgeneratePom=true

mvn install:install-file -DgroupId=com.nsfocus.bsa.serializer -DartifactId=bsa-serializer -Dversion=1.0.1 -Dfile=com.nsfocus.bsa.serializer.bsa-serializer-1.0.1.jar -Dpackaging=jar -DgeneratePom=true

mvn install:install-file -DgroupId=taskmanagerapi -DartifactId=taskmanagerapi -Dversion=1.0 -Dfile=taskmanagerapi.taskmanagerapi-1.0.jar -Dpackaging=jar -DgeneratePom=true

mvn install:install-file -DgroupId=taskmanagerapi -DartifactId=taskmanagerapi -Dversion=2.0 -Dfile=taskmanagerapi-2.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.nsfocus.bsa -DartifactId=geocbb -Dversion=2.0 -Dfile=com.nsfocus.bsa.geocbb-2.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=common_util -DartifactId=common_util -Dversion=3.13 -Dfile=common_util-3.13.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=ng-format-engine -DartifactId=ng-format-engine -Dversion=1.0 -Dfile=ng-format-engine.ng-format-engine-1.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=bsaata_util -DartifactId=bsaata_util -Dversion=3.0 -Dfile=bsaata_util.bsaata_util-3.0.jar -Dpackaging=jar -DgeneratePom=true

mvn install:install-file -DgroupId=com.nsfocus.isop -DartifactId=setl-las -Dversion=1.0 -Dfile=setl-las-1.0.jar -Dpackaging=jar -DgeneratePom=true

```

