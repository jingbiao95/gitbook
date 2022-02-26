# Java List去重的6种方法



### 1. List双重循环去重

```
String[] array = {"a","b","c","c","d","e","e","e","a"};  
List<String> result = new ArrayList<>();  
boolean flag;  
for(int i=0;i<array.length;i++){  
    flag = false;  
    for(int j=0;j<result.size();j++){  
        if(array[i].equals(result.get(j))){  
            flag = true;  
            break;  
        }  
    }  
    if(!flag){  
        result.add(array[i]);  
    }  
}  
String[] arrayResult = (String[]) result.toArray(new String[result.size()]);  
System.out.println(Arrays.toString(arrayResult));  
```

先遍历原数组，然后遍历结束集，通过每个数组的元素和结果集中的元素进行比对，若相同则break。若不相同，则存入结果集，两层循环进行遍历得出最终结果。

### 2. 使用indexOf方法进行判断结果集中是否存在了数组元素去重

```
String[] array = {"a","b","c","c","d","e","e","e","a"};  
List<String> list = new ArrayList<>();  
list.add(array[0]);  
for(int i=1;i<array.length;i++){  
    if(list.toString().indexOf(array[i]) == -1){  
            list.add(array[i]);  
    }  
}  
String[] arrayResult = (String[]) list.toArray(new String[list.size()]);  
System.out.println(Arrays.toString(arrayResult));  
```

### 3. 嵌套循环去重

```
String[] array = {"a","b","c","c","d","e","e","e","a"};  
List<String> list = new ArrayList<>();  
for(int i=0;i<array.length;i++){  
    for(int j=i+1;j<array.length;j++){  
        if(array[i] == array[j]){  
            j = ++i;  
        }  
    }  
    list.add(array[i]);  
}  
String[] arrayResult = (String[]) list.toArray(new String[list.size()]);  
System.out.println(Arrays.toString(arrayResult));
```

### 4. sort排序，相邻比较去重

```
String[] array = {"a","b","c","c","d","e","e","e","a"};  
Arrays.sort(array);  
List<String> list = new ArrayList<>();  
list.add(array[0]);  
for(int i=1;i<array.length;i++){  
    if(!array[i].equals(list.get(list.size()-1))){  
 list.add(array[i]);  
    }  
}
String[] arrayResult = (String[]) list.toArray(new String[list.size()]);  
```

### 5. set方法无序排列去重

```
String[] array = {"a","b","c","c","d","e","e","e","a"};  
Set<String> set = new HashSet<>();  
for(int i=0;i<array.length;i++){  
    set.add(array[i]);  
}  
String[] arrayResult = (String[]) set.toArray(new String[set.size()]);  
System.out.println(Arrays.toString(arrayResult));  
```

加入set方法进行添加，虽然是无序排列，但是也更方便的解决了去重的问题。

### 6. 利用Iterator遍历，remove方法移除去重

```
public void testList() {  
 List<Integer> list=new ArrayList<Integer>();  
 list.add(1);  
 list.add(2);  
 list.add(4);  
 list.add(1);  
 list.add(2);  
 list.add(5);  
 list.add(1);  
 List<Integer> listTemp= new ArrayList<Integer>();  
 Iterator<Integer> it=list.iterator();  
 while(it.hasNext()){  
  int a=it.next();  
  if(listTemp.contains(a)){  
   it.remove();  
  }  
  else{  
   listTemp.add(a);  
  }  
 }  
 for(Integer i:list){  
  System.out.println(i);  
 }  
}  
```

利用LinkedHashSet进行转换也可



# Java8的stream写法实现去重

### 1、distinct去重

```
//利用java8的stream去重
  List uniqueList = list.stream().distinct().collect(Collectors.toList());
  System.out.println(uniqueList.toString());
```

distinct()方法默认是按照父类Object的`equals`与`hashCode`工作的。所以：

　　上面的方法在List元素为基本数据类型及String类型时是可以的，但是如果List集合元素为对象，却不会奏效。不过如果你的实体类对象使用了目前广泛使用的lombok插件相关注解如：@Data,那么就会自动帮你重写了equals与hashcode方法，当然如果你的需求是根据某几个核心字段属性判断去重，那么你就要在该类中自定义**重写**equals与hashcode方法了。

### 2、也可以通过新特性简写方式实现

不过该方式不能保持原列表顺序而是使用了`TreeSet`按照字典顺序排序后的列表，如果需求不需要按原顺序则可直接使用。

```
//根据name属性去重
List<User> lt = list.stream().collect(
        collectingAndThen(
                toCollection(() -> new TreeSet<>(Comparator.comparing(User::getName))), ArrayList::new));
System.out.println("去重后的:" + lt);

//根据name与address属性去重
List<User> lt1 = list.stream().collect(
        collectingAndThen(
                toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getName() + ";" + o.getAddress()))), ArrayList::new));
System.out.println("去重后的:" + lt);
```

当需求中明确有排序要求也可以按上面简写方式再次加工处理使用stream流的sorted()相关API写法。

```
List<User> lt = list.stream().collect(
                collectingAndThen(
                        toCollection(() -> new TreeSet<>(Comparator.comparing(User::getName))),v -> v.stream().sorted().collect(Collectors.toList())));
```

### 3、通过 `filter()` 方法

我们首先创建一个方法作为 `Stream.filter()` 的参数，其返回类型为 `Predicate`，原理就是判断一个元素能否加入到 `Set` 中去，代码如下：

```
private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
    Set<Object> seen = ConcurrentHashMap.newKeySet();
    return t -> seen.add(keyExtractor.apply(t));
}
```

使用如下：

```
@Test
  public void distinctByProperty() throws JsonProcessingException {
    // 这里第二种方法我们通过过滤来实现根据对象某个属性去重
    ObjectMapper objectMapper = new ObjectMapper();
    List<Student> studentList = getStudentList();
 
    System.out.print("去重前        :");
    System.out.println(objectMapper.writeValueAsString(studentList));
    studentList = studentList.stream().distinct().collect(Collectors.toList());
    System.out.print("distinct去重后:");
    System.out.println(objectMapper.writeValueAsString(studentList));
    // 这里我们将 distinctByKey() 方法作为 filter() 的参数，过滤掉那些不能加入到 set 的元素
    studentList = studentList.stream().filter(distinctByKey(Student::getName)).collect(Collectors.toList());
    System.out.print("根据名字去重后 :");
    System.out.println(objectMapper.writeValueAsString(studentList));
  }
去重前        :[{"stuNo":"001","name":"Tom"},{"stuNo":"001","name":"Tom"},{"stuNo":"003","name":"Tom"}]
distinct去重后:[{"stuNo":"001","name":"Tom"},{"stuNo":"003","name":"Tom"}]
根据名字去重后 :[{"stuNo":"001","name":"Tom"}]
```



# Reference

http://www.51gjie.com/java/644.html

https://www.cnblogs.com/better-farther-world2099/articles/11905740.html