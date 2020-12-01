# kaka

#### 项目介绍
1. kaka-core模块为全局事件通知框架，无任何第三方依赖。
2. kaka-aopwear模块为依赖于kaka-core和cglib实现的AOP框架。
3. 在无需AOP功能且仅需要事件模型时，可仅引入kaka-core.jar；当引入kaka-aopwear、cglib后，将直接支持AOP而无需增加或者改动任何代码和配置。
4. kaka-test模块为使用范例，个人认为kaka-core就事件机制而言比google的EventBus更加强大。
5. 本项目为本人十年左右的游戏后端框架中的核心部分，可解耦业务，简化程序复杂性，提高代码可读性，降低开发维护成本。
6. 支持同步或者异步获取事件处理结果。
7. 有任何问题可联系QQ：568049460，微信：zkpursuit。

https://my.oschina.net/zkpursuit/blog/2989186 此博文为本框架 + servlet基于json通信的一个简单接口服务范例，此范例模式已应用于实际生产环境！
更多精彩博文，敬请期待！

规划愿景：打造一款全新的微服务框架。望大家多多支持、鼓励，多多star！！！

#### 软件架构
1. kaka-core事件模型为单例 + 观察者 + 命令模式，包含标准版本和多核版本，标准版可直接使用Facade中的静态常量facade，多核版则使用Facade.getInstance("core name")。标准版能应付绝大部分情况。
2. 本项目的核心思想是解耦业务，通过Startup实例的scan方法扫描Command、Proxy、Mediator子类的注解，并将其注册到Facade中，由Facade处理事件流向。
3. Command、Mediator一般作为业务处理器处理业务，Proxy为数据模型（比如作为数据库service层），Command、Mediator中可通过getProxy方法获得Proxy数据模型。
4. Command只能监听注册到Facade中的事件，可多个事件注册同一个Command（也可理解为一个Command可监听多个事件），而Mediator则是监听多个自身感兴趣的事件，具体对哪些事件感兴趣则由listMessageInterests方法的返回值决定（总结：一个事件只能对应一个Command，一个Command可以对应多个事件；一个事件可以对应多个Mediator，一个Mediator可以对应多个事件；一个事件可以同时对应一个Command和多个Mediator；Command为动态创建，但可池化，Mediator为全局唯一）；Command、Mediator是功能非常相似的事件监听器和事件派发器，强烈建议多使用Command。
5. Command、Proxy、Mediator中都能通过sendMessage方法向外派发事件，也可在此框架之外直接使用Facade实例调用sendMessage派发事件。
6. 此框架的事件数据类型尽可能的使用int和String。
7. Facade实例在调用initThreadPool方法配置了线程池的情况下，Facade、Command、Proxy、Mediator的sendMessage都将直接支持异步派发事件，默认为同步。
8. 统一同步或者异步获得事件处理结果，异步获取事件结果以wait、notifyAll实现。应该尽可能的少使用此方式，而改用派发事件方式。
9. kaka-aopwear为AOP功能实现，其功能生效同样需要借助Startup实例的scan扫描包。

#### 安装教程

1. 将kaka/kaka-core/target/kaka-core-{version}.jar引入项目即可使用本框架的事件领域模型。
2. 将kaka/kaka-aopwear/target/kaka-aopwear-{version}.jar引入项目将直接支持AOP功能。

#### 使用说明

1. 可参看test中的使用范例，个人认为比google的EventBus更加强大。
2. 开源不易，请在使用源码过程中注明出处，也请不要抹除源码注释中相关作者的信息，谢谢！
3. 重点是感谢支持，让本人在开源之余能感受到一丝成就感！