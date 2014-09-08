CommandRouter
==============

一个帮助你将文本命令转化为对特定方法调用的工具，适用于Java或Android。


## 快速上手

如果你希望通过像这样子的文本形式的命令：

```
cmd://trial/show?msg=helloworld&repeat=3
```

来执行下面那样子的代码：

```java
for (int i = 0; i < 3; i++) {
   System.out.println("helloworld")
}
```

传统的做法是进行字符串的解析，然后通过冗长的if-else代码，根据内容找到需要调用的方法。
更加麻烦的是你还要进行参数类型的转换，比如把字符串解析成int。当你要加入新类型命令时，也要修改不少代码。

当你使用 **CommandRouter（命令路由器）**，上述的步骤都可以省去，只需关注核心业务代码。
利用其注解方式可轻松定义一个命令处理器：

```java
@HandlerAlias("trial")
public class TestHandler extends CommandHandler {

    @CommandAlias("show")
    public void showText(Object context,
                         @ParamAlias("msg") String text,
                         @ParamAlias("repeat") int repeatTimes) {
        for (int i = 0; i < repeatTimes; i++) {
            System.out.println(text);
        }
    }
}
```

然后用 ```CommandRouter``` “执行”你的命令就搞掂了:

```java
router.executeCommand(this, "cmd://trial/show?msg=helloworld&repeat=3");
```

Enjoy the clean code!


## 详细用法

### STEP 1: 创建 CommandHandler 和命令方法

一个命令处理器 *Command Handler* 是一组命令方法的集合（或称为模块），每一种命令对应其中的一个命令方法。
你需要创建```CommandHandler```的子类及命令方法，并使用下列三个重要的注解来进行修饰：

 - **```HandlerAlias```**：定义命令处理器（模块）的名称。
 - **```CommandAlias```**: 定义命令方法的名字，一个命令可以有多个别名，假如名字为空的话，将使用被注解方法的名字。
 - **```ParamAlias```**: 定义命令参数的名字(key)，另外还有2个可选的选项:
  - ```defaultRaw```: 参数默认值（使用原始文本进行表示）；
  - ```converter```: 用于解析该参数的自定义转换期的类名（必须先注册）。

> 注意：要处理的命令参数必须是以Key-Value的形式存储的。

栗子：

```java
@HandlerAlias("trial")
public class TestHandler extends CommandHandler {

    @CommandAlias("show")
    public void showText(Object context,
                         @ParamAlias("msg") String text,
                         @ParamAlias(value="repeat", defaultRaw="5") int repeatTimes) {
        // some code ...
    }
}
```

> 注意: 命令方法的第一个参数总是上下文对象 (Context Object)。

### STEP 2: 创建命令路由器 CommandRouter 的实例

再创建```CommandRouter```之前，你需要根据要解析的文本命令的格式，选择正确的```CommandDriver```作为解析器。
目前最新版本已经内建下面几种命令格式的支持:

解析器名称           | 格式              | 命令示例
-------------------|------------------|---------------
UriDriver          | Hierarchical URI | ```cmd://trial/show?msg=helloworld``` （host部分充当模块名，path部分充当命令名）
JsonDriver*        | Json             | ```{handler:"trial", command:"show",param:{msg:"hello"}}```
CommandLineDriver* | shell-style      | ```trial show --msg helloworld```

推荐使用 ```CommandRouterBuilder``` 来创建命令路由器的实例:

```java
CommandRouter router = new CommandRouterBuilder()
                .setDriver(new UriDriver())
                .addCommandHandler(TestHandler.class)
                .addGeneralValueConverters()
                .build();
```

### STEP 3: 执行命令

现在执行你的文本命令已经再简单不过了，在需要调用命令的地方，使用命令路由器对象中 ```executeCommand``` 方法即可。
你的上下文对象（可选的）将会传递到相应的命令方法中。一颗栗子如下：

```java
router.executeCommand(context, "cmd://trial/show?msg=helloworld&repeat=3");
```


### 在Android环境下使用

你需要确保下面这条配置已经加入到你的 *proguard* 配置文件中:

```
-keepattributes *Annotation*
```


## 高级用法

### 值转换器和自定义值转换器

值转换器用于把命令中文本形式的参数转换成命令方法所需的类型的参数。
目前已经内置了Java中常见的值类型的转换器，这些值类型包括：

- int
- long
- float
- double
- string
- boolean
- java.lang.Date

上述类型的值将会被自动转换，但某些场景下我们希望使用自定义的类型作为参数，这时候就需要创建自定义的值转换器了。
例如下面命令中的 *addr* 参数，其对应Java类是Address：

```
cmd://trial/locate?addr=N%20Western%20Avenue,Chicago,90027
```

```java
public class Address {
    public String street;
    public String city;
    public String postcode;
}
```

我们就需要继承 ```ValueConverter``` 创建如下的自定义值转换器：

```java
public class AddressConverter implements ValueConverter {
    @Override
    public String marshal(Object source) throws ValueConverterException {
        Address address = (Address) source;
        return address.street + "," + address.city + "," + address.postcode;
    }

    @Override
    public Object unmarshal(String source, Class<?> type) throws ValueConverterException {
        String[] addressDataArray = source.split(",");
        Address address = new Address();
        address.street = addressDataArray[0];
        address.city = addressDataArray[1];
        address.postcode = addressDataArray[2];
        return address;
    }
}
```

有了值转换器后，我们还需要让 *CommandRouter* 知道你要用它来转换addr参数，我们有两种方法来达到这个目的：

方法一：在 ```CommandRouter``` 中注册这个值转换器，注册后将和int、float等转换器一样是一等公民。
这种方法的好处是命令参数的定义方法与基本类型一样简单，但缺点是你需要获取 ```CommandRouter``` 的实例来进行值转换器的注册。

```java
router.addValueConverter(Address.class, new AddressConverter());
```

```java
@CommandAlias("locate")
public void showAddress(Object context, @ParamAlias("addr") Address address) {
  //...
}
```

方法二：直接在 ```ParamAlias``` 注解中指定值转换器。
这种做法的好处是不需要你操作```CommandRouter``` ，但缺点是每个该类型参数的 ```ParamAlias``` 注解都需要指定值转换器。

```java
@CommandAlias("locate")
public void showAddress(Object context,
                        @ParamAlias(value = "addr", converter = AddressConverter.class) Address address) {
  //...
}
```


### 处理未知命令的调用请求

随着软件版本迭代，旧版本可能无法识别未来的命令，**CommandRouter** 已经为你提供了处理办法。

- 如果是遇到未知的模块（找不到对应的 ```CommandHandler```），CommandRouter会把命令路由到拥有 ```DefaultHandler``` 注解的```CommandHandler```。
- 如果是遇到未知的命令方法，CommandRouter会把命令路由到 ```onUnknownCommand()``` 方法。

因此一个完备的未知命令处理方式是：

```java
@DefaultHandler
@HandlerAlias("trial")
public class TestHandler extends CommandHandler {
    //...

    @Override
    protected void onUnknownCommand(Object context, String command) {
        System.out.println("unknown command: " + command);
    }
}
```

### 构建文本命令

除了解析文本命令并实现对相应方法的调用外，**CommandRouter** 还支持反向构建出文本命令。
首先，你需要构建 ```CommandRouter.Op``` 对象，该对象用于存储一条命令的格式无关的信息：

```java
CommandRouter.Op op = new CommandRouter.Op(null, "trial", "showText");
op.addArgument("msg", "hello,world");
```

接着使用```CommandRouter```的```buildCommand()```方法转换并输出后的命令即可：

```java
String commandText = (String) router.buildCommand(op);
// 如果正在使用UriDriver作为解析器的话，commandText将会是"//trial/showText?msg=hello,world"
```


### 使用返回值

每个命令方法都是支持返回值的，返回值将会最后在```executeCommand```方法返回，例如：

```java
@CommandAlias()
public void sayHello(Object context) {
    // some code ...
    return "hello, world";
}
```

```java
String text = (String) router.executeCommand("cmd://trial/sayHello");
// text is "hello, world"
```

### 解析自定义格式的命令

要让 **CommandRouter** “读懂”你的自定义格式的命令，你需要实现你自己的命令解析器，
解析结果需要存储在 ```CommandRouter.Op``` 对象中。例如：

```java
public class CustomDriver extends AbstractDriver {
    @Override
    public CommandRouter.Op parseCommand(Object context, Object... rawArgs) {
       // rawArgs就是输入的内容
       CommandRouter.Op op = new CommandRouter.Op(context, handlerName, commandName);
       // 填充该命令的参数内容
       op.addArgument(key, value);
       return op;
    }
}
```

> 建议你参考源码 ```com.imasson.commandrouter.driver``` 中内建解析器的实现。


## 许可证

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
