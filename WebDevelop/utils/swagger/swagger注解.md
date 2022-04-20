# @Api



# @ApiImplicitParams



# @ApiImplicitParam

作用在方法上，表示单独的请求参数 
参数： 
\1. name ：参数名。 
\2. value ： 参数的具体意义，作用。 
\3. required ： 参数是否必填。 
\4. dataType ：参数的数据类型。 
\5. paramType ：查询参数类型，这里有几种形式：

类型 作用
path 以地址的形式提交数据
query 直接跟参数完成自动映射赋值
body 以流的形式提交 仅支持POST
header 参数在request headers 里边提交
form 以form表单的形式提交 仅支持POST