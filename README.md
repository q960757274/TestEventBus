# TestEventBus
##Demo包括：
1.EventBus2.4版本基于反射实现
2.EventBus3.0版本基于注解实现


     本文从4个方面讲解EventBus，讲解来源于GitHub作者的文档和自己的理解。

 - 简单认识EventBus
 - 为什么要使用EventBus
 - EventBus的全部用法
 - 和其他事件总线框架的对比



一、简单认识EventBus
==============

   EventBus是安卓发布/订阅事件总线的优化
 ![这里写图片描述](http://img.blog.csdn.net/20160115104247820)

EventBus的优点
-----------


**1.	简化组件间的通信**
（1）.对发送和接受事件解耦
（2）.可以在Activity，Fragment，和后台线程间执行
（3）.避免了复杂的和容易出错的依赖和生命周期问题
**2.	让你的代码更简洁**
**3.	更快**
**4.	更轻量（jar包小于50K）**
**5.	实践证明已经有一亿多的APP中集成了EventBus**
**6.	拥有先进的功能比如线程分发，用户优先级等等**

EventBus如何集成到项目中：
-----------------

**1.	下载EventBus到本地**
**2.	远程库：**
Gradle：compile 'de.greenrobot:eventbus:2.4.0'
Maven：
       <dependency>
           <groupId>de.greenrobot</groupId>
          <artifactId>eventbus</artifactId>
          <version>2.4.0</version>
       </dependency>

EventBus在项目中如何使用：
-----------------

**1.	定义事件**

```
public class MessageEvent { /* Additional fields if needed */ }
```

**2.	准备订阅**

```
EventBus.getDefault().register(this);
public void onEvent(AnyEventType event) {/* Do something */};
```

**3.	发布事件**

```
EventBus.getDefault.post(event);
```

二、为什么使用EventBus（EventBus可以做什么？）
===============

**1.简化子线程和主线程之间或两个子线程之间的消息传递**
     通常我们都会在一个线程中做一些耗时操作得到数据之后需要发送到另一个线程去处理数据（这个线程可以是主线程去更新UI，或子线程继续处理接下来的任务）。这些通信都需要用到Handler。我们需要定义Handler，并重写handleMessage方法去处理数据，在使用处定义Message对象携带数据并发送到指定的Handler中
**2.代替BroadcastReceiver/Intent**
    不同于安卓的BroadcastReceiver/Intent，EventBus就是普通的java类，它提供很简单易用的API调用。EventBus适用于更多的场景，并不需要你麻烦的设置Intent，设置携带数据，然后从Intent中取出数据，或者定义广播和广播接受者。同时，EventBus的开销会低，对于输出传送方和接收方没有那么高的耦合。
**3.简化数据传输**
比如：（1）在一个Activity中横向同时加载两个Fragment，左边的ragment是列表，右边的Fragment是详情展示。这时候涉及到Activity和Fragment，两个Fragment之间的通信（2）当使用startService启动一个Service时，你需要和Service相互通信时（3）有些场景需要使用接口处理问题时（4）在应用程序运行时可能有多个界面，多处都需要使用到一个数据时，这个数据只是需要临时存储，并不需要落地时。

三、具体的应用
=======

普通用法
----

**1.	定义一个事件**
    需要定义的这个事件其实就是一个普通的Java Object（POJO）,并没有特殊的要求
Eg:
  

```
 public class MessageEvent {
      public final String message;

      public MessageEvent(String message) {
          this.message = message;
      }
  }
```

**2.	准备订阅**
订阅者需要实现一个onEvent方法，这个方法会在接收到事件时被调用。当然订阅者也需要注册和反注册
   Eg:
   

```
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

 // This method will be called when a MessageEvent       is       posted
    public void onEvent(MessageEvent event){
        Toast.makeText(getActivity(), event.message, Toast.LENGTH_SHORT).show();
    }

    // This method will be called when a SomeOtherEvent is posted
    public void onEvent(SomeOtherEvent event){
        doSomethingWith(event);
    }
```

**3.	发布事件**
可以从代码的任何一个部分发布事件，所有订阅这个类型的事件的订阅者都可以收到这个事件。
   Eg：

```
EventBus.getDefault().post(new MessageEvent("Hello   everyone!"));
```

线程分发和线程模型
---------

 Event可以为你处理线程：事件可以被发布到与发布线程不同的线程中。
 一般用法是处理UI，在安卓中UI的处理需要在主线程中完成，其他处理比如网络请求，耗时操作不能在主线程中处理。EventBus帮助你处理这些任务并且同步到UI线程（不用深入研究线程转换，使用AsyncTask等等）
       
       在EvenyBus中，在将要调用事件处理方法onEvent时可以使用一个ThreadMode去定义这个方法调用的线程。
**ThreadMode有四种：**
**PostThread，MainThread，BackgroundThread，Async.**

**PostThread**:订阅者将会被调用在与发布线程同样的线程中。这是默认的，事件的分发意味着最小的开销，因为这种模式避免了线程切换。对于简单任务来说这是被推荐的用法。使用这个mode需要快速返回结果，避免锁住主线程，因为他可能在主线程执行。

```
// Called in the same thread (default)
    public void onEvent(MessageEvent event) {
        log(event.message);
    }
```

	**MainThread：**订阅者将被回调在安卓的主线程中。如果发布线程是主线程那事件的处理会马上被执行。同样使用这个mode需要快速返回结果，避免锁住主线程。

```
// Called in Android UI's main thread
    public void onEventMainThread(MessageEvent           event)  {
        textField.setText(event.message);
     }
```

	**BackgroundThread：**订阅者将会被回调在子线程中。如果发布线程不是主线程，事件处理会马上被执行在发布线程中。如果发布线程是主线程，EventBus会使用一个单独的子线程顺序处理事件。虽然是子线程，但是也需要尽快返回结果，避免锁住线程。

```
// Called in the background thread
public void onEventBackgroundThread
(MessageEvent event){
        saveToDisk(event.message);
    }
```

	**Async：**事件处理方法会在一个单独的线程中调用。这个线程永远独立与发布线程和主线程。如果需要处理耗时任务时事件处理方法应该使用这个mode。比如网络请求。避免在短时间内引发大量的长时间运行的异步任务EventBus使用线程池有效的控制线程数并重用线程

```
// Called in a separate thread
    public void onEventAsync(MessageEvent event){
        backend.send(event.message);
    }
```

    注意：*EventBus负责在适当的线程中调用onEvent方法取决于方法的后缀。*

订阅优先级
-----

 你可以在注册订阅者的时候通过设置优先级改变时间分发的顺序。
```
 int priority = 1;
    EventBus.getDefault().register(this,priority);
```
     在同一个分发线程中，高优先级的订阅者会比低优先级的订阅者先得到事件的分发。在不同ThreadMode的订阅者中优先级是没有效果的

使用EventBuilder配置EventBus
------------------------

  EventBus在2.3版本中添加了EventBuilder去配置EventBus的各方各面。比如：这是如何去构建一个在发布事件时没有订阅者时保持沉默的EventBus。

```
EventBus eventBus = EventBus.builder()
.logNoSubscriberMessages(false)
.sendNoSubscriberEvent(false)
.build();
```

  另一个例子是订阅失败时会抛出一个异常。默认情况下,EventBus捕获异常抛出的onEvent方法并发送

```
SubscriberExceptionEvent但不需要处理。
EventBus eventBus = EventBus.builder()
.throwSubscriberException(true)
.build();
```

配置默认的EventBus实例
---------------

使用EventBus.getDefault()是一个简单的方法去获取一个单例的EventBus实例。EventBusBuilder也允许使用installDefaultEventBus方法去配置默认的EventBus实例。
    
 例如,可以配置默认的EventBus实例在onEvent方法中重新抛出异常,但是我们这只有在测试是才会这样配置,因为这样会导致应用程序崩溃。
```
EventBus.builder().throwSubscriberException(BuildConfig.DEBUG).installDefaultEventBus();
```
    注意：*这个方法的调用需要在EventBus实例化之前，最好是在安卓的Application中去调用。*
取消事件分发
------

 可以在订阅者的事件处理方法（onEvent）中调用cancelEventDelivery(Object event)这个方法去取消事件的分发,任何进一步的事件分发都会被取消，后续的订阅者不会再收到此类事件。

```
// Called in the same thread (default)
    public void onEvent(MessageEvent event){
        // Process the event 
        ...
        EventBus.getDefault()
.cancelEventDelivery(event) ;
    }
```
        注意：*事件通常被高优先级的订阅者取消。取消只能在ThreadMode为PostThread时，也就是onEvent方法中取消。*

粘性的事件
-----

  一些event携带的消息是在event被发布后。有这些场景：你需要通过一个event去做初始化，或如果你有传感器或一些本地数据并且你想要保存最近的值。你可以使用粘性事件去代替你自己的缓存。EventBus会把最后一个确定类型的粘性事件保存到内存中。粘性事件可以被订阅者接收或者根据事件类型去查询获取。因此不需要特定的逻辑去验证可用数据。

```
    EventBus.getDefault()
    .postSticky(new MessageEvent("Hello everyone!"));
```

这段代码之后，一个新的Activity启动了，可以使用registerSticky去注册EventBus，这个注册操作会马上获得之前的发布的事件，在onEvent方法中执行。
 

```
 @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().registerSticky(this);
    }

    public void onEventMainThread(MessageEvent event) {
        textField.setText(event.message);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
```

**也可以根据确定的类型去获取一个粘性事件**

```
EventBus.getDefault().getStickyEvent(Class<?> eventType)
```

    *注意：可以使用removeStickyEvent方法移除之前发布的粘性事件。可以通过事件对象或者事件的类去移除。也可以去构建一个自定义的事件。但是要记住一个类型的事件只有一个会被保存*

AsyncExecutor
-------------

    申明: AsyncExecutor是一个非核心的工具类。它也许会减少你在子线程中错误处理的代码，但是它并不是EventBus的核心类。
AsyncExecutor与线程池类似，并且它有错误处理。失败时抛出异常，这个异常信息会包含在event中的，由AsyncExecutor自动发布。
通常你可以调用AsyncExecutor.create()来创建一个AsyncExecutor的实例并把它保存在Application的范围内。需要执行某些任务时，需要实现RunnableEx接口，可以通过RunnableEx的execute去执行。不同于Runnable，RunnableEx可能会抛出异常。
    如果RunnableEx的实现类抛出一个异常，这个异常将会被缓存并包装在一个ThrowableFailureEvent中，并被发布。
**执行RunnableEx的代码示例：**
   

```
 AsyncExecutor.create().execute(
        new RunnableEx {
        public void run throws LoginException {
        remote.login();
        EventBus.getDefault().postSticky(
        new  LoggedInEvent());
       // No need to catch Exception
         }
        }
    }
```

**接收事件的代码示例：**
 

```
  public void onEventMainThread(LoggedInEvent event) {
      // Change some UI
   }

   public void onEventMainThread(
   ThrowableFailureEvent   event) {
   // Show error in UI
}
```

AsyncExecutor Builder
---------------------

如果你需要自定义你的AsyncExecutor实例，可以使用静态方法AsyncExecutor.builder().它将会返回一个builder对象使用它可以自定义你的EventBus实例，线程池，和错误event的类
另一个自定义选项是失败event的执行范围，需要提供上下文信息。比如，一个失败的event可能只和一个特定的Activity或是类有关。如果你的自定义的失败事件类实现了HasExecutionScope接口，HasExecutionScope会自动设置执行范围。就像这样，你的订阅者在自己的执行范围内可以查询到这个错误event。
混淆
**因为onEvent方法的执行是使用反射的，所以方法名不能被混淆：**
   

```
 -keepclassmembers class ** {
    public void onEvent*(***);
    }

    # Only required if you use AsyncExecutor
-keepclassmembers class * extends de.greenrobot.event.util.ThrowableFailureEvent{
    <init>(java.lang.Throwable);
 }
```

四、与其他事件总线相比
===========

1.功能比较
------

     除EventBus之外还有一个事件总线的框架Otto,是Square开发的。Otto与EventBus共享一些语义，比如：register, post, unregister, ...。但是还有很大的不同：
 ![这里写图片描述](http://img.blog.csdn.net/20160115110934400)

**通过表格可以看出为什么EventBus优于Otto：**

    1.事件处理方法的定义，EventBus是命名规范，而Otto是注解，EventBus通过反射出来符合命名的方法去执行，Otto使用注解效率会很低。
    
    2.订阅者EventBus可继承，Otto并不可继承，少了很多灵活性
    
    3.EventBus可以存储最近的事件通过post sticky events，但是Otto不可。对于这个可以适用于公共数据的内存存储，在程序中供很多页面调用。这个在实际的业务需求中还是有很多应用场景的。
    
    4.EventBus提供线程切换，我不管发布线程是什么线程我可以选择在主线程或子线程处理。但是Otto并不可以。在安卓中子线程处理数据，然后需要主线程去更新UI的场景很多，就这一点Otto就弱了很多。

2.性能比较
------

**除了功能比较外，还有性能比较。这个Greenrobot官方给出了例子可以运行查看效果。**
	![这里写图片描述](http://img.blog.csdn.net/20160115111037003)




未完待续
====

**最后：这个只是暂时的整理文档性的EventBus的使用，以及它的一些优势。之后我会做一个尽量完整的case覆盖全面的EventBus使用Demo。请长期关注。**


