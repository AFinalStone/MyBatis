[MyBatis学习总结(一)——MyBatis快速入门](/MyBatis_01)

[MyBatis学习总结(二)——使用MyBatis对表执行CRUD操作](/MyBatis_02_CRUD)


[MyBatis学习总结(三)——优化MyBatis配置文件中的配置](/MyBatis_03)


[MyBatis学习总结(四)——解决字段名与实体类属性名不相同的冲突](/MyBatis_04)


[MyBatis学习总结(五)——实现关联表查询](/MyBatis_05)


[MyBatis学习总结(六)——调用存储过程](/MyBatis_06)

[MyBatis学习总结(七)——Mybatis缓存](/MyBatis_07)

[MyBatis学习总结(八)——Mybatis3.x与Spring4.x整合](/MyBatis_08)

[原文地址](http://www.cnblogs.com/xdp-gacl/p/4271627.html)


总结：

#### mybatis是什么？

mybatis是一个持久层框架，mybatis是一个不完全的ORM框架。sql语句需要程序员自己去编写，但是mybatis也有
映射(输入参数映射，输出参数映射)。
mybatiis入门门槛不高，学习成本低，让程序员把精力放在SQL语句上，对SQL语句优化非常方便，适用于需求变化、
较多的项目，比如互联网项目。

#### mybatis框架执行过程：

1、配置mybatis的配置文件，SqlMapConfig.xml(名称不固定)session
2、通过配置文件，加载mybatis运行环境，创建sqlsession会话工厂，sqlSessionFactory在实际使用时按单例子模式
3、通过sqlSessionFactory创建sqlSession，sqlSession是一个面向用户接口（提供操作数据库方法），实现对象
是线程不安全的，建议sqlSession应用场合在方法体内。
4、调用sqlSession的方法去操作数据。如果需要提交事务，需要执行SqlSession的Commit()方法
5、释放资源，关闭sqlSession

#### mybatis开发dao的方法：
1、原始dao的方法：
    需要程序员编写dao接口和实现类
    需要在dao实现类中注入一个sqlSessionFactory工厂。
2、mapper代理开发方法
    只需要程序员编写mapper接口(就是dao接口)，
    程序员在编写mapper.xml（映射文件）和mapper.java需要遵循一个开发规范：
    1、mapper.xml中namespace就是mapper.java类全路径
    2、mapper.xml中statement的id和mapper.java中方法名一致。
    3、mapper.xml中statement的parameterType指定输入参数的类型和mapper.java的方法输入参数类型一致
    
#### 关于salMapConfig.xml配置文件：

可以配置properties属性、别名、mapper加载。。。

输入映射：
        parameterType：指定输入参数类型可以是简单类型、pojo、hashmap。。。
        对于综合查询，建议parameterType使用包装的pojo，有利于系统扩展。
输出映射：
        resultType:
                查询到的列名和resultType指定的pojo的属性名一致，才能映射成功
         resultMap:
                可以通过resultMap完成一些高级映射       
    如果查询到的列名和映射的属性名不一致时，通过resultMap设置列名和属性名之间的对应关系（映射关系）
    可以完成映射。
高级映射：
   将关联查询的列映射到一个pojo属性中欧。（一对一）
   将关联查询的列映射到一个list<pojo>中。（一对多）
动态sql：
   if判断
   where
   foreach
   sql片段   
            
        
    