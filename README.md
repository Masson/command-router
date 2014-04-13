CommandRouter
==============

A simple tool help you convert textual command to method call for Java/Android.

## Quick start
if you want to parse a textual command like this:
```
"cmd://trial/show?msg=helloworld&repeat=3 
```
in order to execute the follow code:
```
for (int i = 0; i < 3; i++) {
   System.out.println("helloworld")
}
```
You don't need to write any code to parse text and do the conditional. Use annotations of CommandRouter to define a handler:
```
@HandlerAlias("trial")
public class TestHandler extends CommandHandler {

    @CommandAlias("show")
    public void showText(Object context,
                         @ParamAlias("msg") String text,
                         @ParamAlias("repeat") int repeatTimes) {
        for (int i = 0; i < repeatTimes; i++) {
            System.out.println("helloworld")
        }
    }
}
```
and just 'execute' the command by CommandRouter:
```
router.invokeCommand(this, "cmd://trial/show?msg=helloworld&repeat=3");
```
Enjoy the clean code!

## Usage

**STEP 1: create CommandHandler and command methods**

Create subclass of ```CommandHandler```. A *Command Handler* is a group of command method. Each command method is invoke by one command.

Use these three important annotations to decorate your class:

 - **```HandlerAlias```**：define a handler's name.
 - **```CommandAlias```**: define a command's name. A command method can has mutli alias name.
 - **```ParamAlias```**: define a param of the command. There is 2 optional field:
  - ```defaultRaw```: the textual value before unmashell.
  - ```converter```: the converter class to handler this type of param.
     
```
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

> Notice: the first param of a command method must be the context Object. It is not one of the command params.

**STEP 2: build instance of CommandRouter**

Before build the ```CommandRouter```, you should determine the command format and chose the right ```CommandDriver```. The follow command format is build-in supported:

driver class       | format           | command sample
-------------------|------------------|---------------
UriDriver          | Hierarchical URI | ```cmd://trial/show?msg=helloworld``` (use host part as handler name and path part as command name)
JsonDriver*        | Json             | ```{handler:"trial", command:"show",param:{msg:"hello"}}```
CommandLineDriver* | shell-style      | ```trial show --msg helloworld```

Recommend to use ```CommandRouterBuilder``` to build router instance:
```
CommandRouter router = new CommandRouterBuilder()
                .setDriver(new UriDriver())
                .addCommandHandler(TestHandler.class)
                .addGeneralValueConverters()
                .build();
```

**STEP 3: execute the command**

Execute the textual command by CommandRouter where you need it. Your context object will pass to the command method.
```
router.invokeCommand(context, "cmd://trial/show?msg=helloworld&repeat=3");
```

**Using in android**

Ensure that this line is added to your proguard config:
```
-keepattributes *Annotation*
```

## Advanced (comming soon)

**custom value converter**

**parse custom command**

## License

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
