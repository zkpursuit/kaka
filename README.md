# kaka-notice-lib

#### 项目介绍
全局事件通知框架，无任何第三方依赖，源码中已包含使用范例，不懂的可联系QQ：568049460，微信：zkpursuit。

#### 软件架构
1. 核心为单例 + 观察者模式模式，包含标准版本和多核版本，标准版可直接使用Facade中的静态常量facade，多核版则使用Facade.getInstance("core name")。
2. 本项目的核心是解耦业务，通过Startup.scan类扫描器扫描Command、Proxy、Mediator子类的注解，并将其注册到Facade中，由Facade处理事件流向。
3. 在本人的使用过程中，Command作为业务处理器处理业务，Proxy为数据模型，Command中可通过getProxy方法获得Proxy数据模型。
4. Command只能监听注册到Facade中的事件，可多个事件注册同一个Command，而Mediator则是监听多个自身感兴趣的事件，具体对哪些事件感兴趣则由listMessageInterests方法的返回值决定。
5. Command、Proxy、Mediator中都能通过sendMessage向外派发事件。
6. 此框架的事件数据类型尽可能的使用int和String。


#### 安装教程

1. 将dist/kaka-notice-lib.jar直接导入项目即可使用。
2. 或者将src源码复制到项目中使用。

#### 使用说明

1. 可参看test中的使用范例，个人认为比google的EventBus更加强大。
2. 开源不易，请在使用源码过程中注明出处，也请不要抹除源码注释中相关作者的信息，谢谢！
3. 重点是感谢支持，让本人在开源之余能感受到一丝成就感！

#### 参与贡献

1. Fork 本项目
2. 新建 Feat_xxx 分支
3. 提交代码
4. 新建 Pull Request


#### 码云特技

1. 使用 Readme\_XXX.md 来支持不同的语言，例如 Readme\_en.md, Readme\_zh.md
2. 码云官方博客 [blog.gitee.com](https://blog.gitee.com)
3. 你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解码云上的优秀开源项目
4. [GVP](https://gitee.com/gvp) 全称是码云最有价值开源项目，是码云综合评定出的优秀开源项目
5. 码云官方提供的使用手册 [https://gitee.com/help](https://gitee.com/help)
6. 码云封面人物是一档用来展示码云会员风采的栏目 [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)