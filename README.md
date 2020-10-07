# cuteshui-blog
**项目名称：cuteshui-blog**

**项目描述**：该项目是基于SpringBoot的个人博客，具有注册登录、文章分类、文章发布收藏、用户中心、热议排行榜、搜索引擎、即时消息通知和群聊的功能。

**涉及到的技术**：SpringBoot、Mybatis、MySQL、Freemarker、Shiro、Redis、RabbitMQ、Elasticsearch等。

**系统设计**：

- 基于MyBatis plus的快速代码生成。
- 封装与自定义Freemarker标签。
- 使用Redis的Zset结构开发七天热议排行榜。
- 使用RabbitMQ+Elasticsearch，完成搜索引擎开发与文章内容同步。
- 使用t-io+websocket开发即时消息通知和群聊。

**项目收获**：通过该项目的开发，了解数据库设计在项目中的重要性；了解抽取定义前端模版提升代码复用性和可读性；了解Redis结构之间的区别和应用场景；了解搜索引擎的开发流程；了解如何使用RabbitMQ完成与数据库之间的异步同步。