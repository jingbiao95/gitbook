## 依赖仓库的配置方式

maven项目使用的仓库一共有如下几种方式：

1. 中央仓库，这是默认的仓库
2. 镜像仓库，通过 sttings.xml 中的 settings.mirrors.mirror 配置
3. 全局profile仓库，通过 settings.xml 中的  settings.repositories.repository 配置
4. 项目仓库，通过 pom.xml 中的 project.repositories.repository 配置
5. 项目profile仓库，通过 pom.xml 中的 project.profiles.profile.repositories.repository 配置
6. 本地仓库

如果所有配置都存在，依赖的搜索顺序就会变得异常复杂。



完整的搜索链：

local_repo > settings_profile_repo > pom_profile_repo > pom_repositories > settings_mirror > central





##  参考文献

[Maven 项目中依赖的搜索顺序](https://cloud.tencent.com/developer/article/1532388)