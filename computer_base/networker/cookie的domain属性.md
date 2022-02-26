为了保证安全性，cookie无法设置除当前域名或者其父域名之外的其他domain。
在此，分为两种情况：

1. 一种是前端范围内的是指cookie，如果网站的域名为，i.xiaohan.com,那么前端cookie的domain只能设置，i.xiaohan.com和其父域名xiaohan.com，如果设置成同级域名如api.xiaohan.com或者子域名api.i.xiaohan.com 那么cookie设置将无效。

2. 同样在服务端上，如果制定你的server服务的域名为server.xiaohan.com那么在服务端生成的cookie的domain只能指定为server.xiaohan.com或者xiaohan.com 其他domain都无法成功设置cookie。

所以，如果你网页地址为i.xiaohan.com 而你请求地址为server.xiaohan.com， 那么在前端范围内，domain设置规则如上面第一种情况，而在服务器端设置cookie则将符合上述第二种情况。

1. 在setcookie中省略domain参数，那么domain默认为当前域名。
2. domain参数可以设置父域名以及自身，但不能设置其它域名，包括子域名，否则cookie不起作用。

cookie的作用域是domain本身以及domain下的所有子域名。例如设置xiaohan.com为domain的cookie时，只有该域名或者其子域名才能获取到这个cookie

例如server.xiaohan.com发送请求的时候，服务器会自动带上server.xiaohan.com以及xiaohan.com域下的cookie

前端设置可以直接通过chrome控制台输入 document.cookie = "example=2; expires=Mon, 11 Nov 2026 07:34:46 GMT; domain=test.com;path=/" 进行测试
参考文章：[http://blog.csdn.net/czhphp/a...](http://blog.csdn.net/czhphp/article/details/65628977)