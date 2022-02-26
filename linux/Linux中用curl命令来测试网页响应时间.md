

```
curl -o /dev/null -s -w '%{time_connect}:%{time_starttransfer}:%{time_total}\n' 'http://kisspeach.com'



.081:0.272:0.779
```



下面给出对kisspeach.com站点执行 curl 命令的情况.输出通常是 HTML 代码,通过 -o 参数发送到 /dev/null.-s 参数去掉所有状态信息.-w 参数让 curl 写出列出的计时器的状态信息： curl 使用的计时器：
计时器描述：
time_connect 建立到服务器的 TCP 连接所用的时间
time_starttransfer 在发出请求之后,Web 服务器返回数据的第一个字节所用的时间
time_total 完成请求所用的时间
time_namelookup DNS解析时间,从请求开始到DNS解析完毕所用时间(记得关掉 Linux 的 nscd 的服务测试)
speed_download 下载速度，单位-字节每秒。

这些计时器都相对于事务的起始时间,甚至要先于 Domain Name Service（DNS）查询.因此,在发出请求之后,Web 服务器处理请求并开始发回数据所用的时间是 0.272 – 0.081 = 0.191 秒.客户机从服务器下载数据所用的时间是 0.779 – 0.272 = 0.507 秒. 通过观察curl数据及其随时间变化的趋势,可以很好地了解站点对用户的响应性.以上变量会按CURL认为合适的格式输出，输出变量需要按照%{variable_name}的格式，如果需要输出%，double一下即可，即%%，同时，\n是换行，\r是回车，\t是TAB。 当然,Web 站点不仅仅由页面组成.它还有图像、JavaScript 代码、CSS 和 cookie 要处理，curl很适合了解单一元素的响应时间,但是有时候需要了解整个页面的装载速度.




## 参考文献

[Linux中用curl命令来测试网页响应时间](https://blog.csdn.net/qq_39479575/article/details/78534214)