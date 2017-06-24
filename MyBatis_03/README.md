MyBatis学习总结(三)——优化MyBatis配置文件中的配置

>[github同步更新](https://github.com/AFinalStone?tab=repositories)
[博客同步更新](http://blog.csdn.net/abc6368765)
[简书同步更新](http://www.jianshu.com/u/0e4907a8f36b)

[参考原文地址](http://www.cnblogs.com/xdp-gacl/p/4261895.html)
项目地址:[传送门](https://github.com/AFinalStone/MyBatis)
一、连接数据库的配置单独放在一个properties文件中
　　之前，我们是直接将数据库的连接配置信息写在了MyBatis的conf.xml文件中，如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration PUBLIC "-//mybatis.org//DTD Config 3.0//EN" "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <environments default="development">
        <environment id="development">
            <transactionManager type="JDBC" />
            <!-- 配置数据库连接信息 -->
            <dataSource type="POOLED">
                <property name="driver" value="com.mysql.jdbc.Driver" />
                <property name="url" value="jdbc:mysql://localhost:3306/mybatis" />
                <property name="username" value="root" />
                <property name="password" value="123456" />
            </dataSource>
        </environment>
    </environments>


    <mappers>
        <!-- 注册userMapper.xml文件，
        userMapper.xml位于com.shi.mapping这个包下，所以resource写成com/shi/mapping/userMapper.xml-->
        <mapper resource="com/shi/mapping/userMapper.xml"/>
        <!-- 注册UserMapper映射接口-->
        <mapper class="com.shi.mapping.UserMapperI"/>
    </mappers>

</configuration>


```
　　其实我们完全可以将数据库的连接配置信息写在一个properties文件中，然后在conf.xml文件中引用properties文件，具体做法如下：

   1、在src目录下新建一个db.properties文件，把关于数据库链接的配置信息保存进去：
   
```db
driver=com.mysql.jdbc.Driver
url=jdbc:mysql://localhost:3306/mybatis
name=root
password=123456
```

具体配置信息如下图所示：

 ![添加properties](pic/properties.png)
 
　2、在MyBatis的conf.xml文件中引用db.properties文件，如下：

```mxl
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration PUBLIC "-//mybatis.org//DTD Config 3.0//EN" "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>

    <!-- 引用db.properties配置文件 -->
    <properties resource="db.properties"/>

    <!--
        development : 开发模式
        work : 工作模式
     -->
    <environments default="development">
        <environment id="development">
            <transactionManager type="JDBC"/>
            <!-- 配置数据库连接信息 -->
            <dataSource type="POOLED">
                <!-- value属性值引用db.properties配置文件中配置的值 -->
                <property name="driver" value="${driver}"/>
                <property name="url" value="${url}"/>
                <property name="username" value="${name}"/>
                <property name="password" value="${password}"/>
            </dataSource>
        </environment>
    </environments>


    <mappers>
        <!-- 注册userMapper.xml文件，
        userMapper.xml位于com.shi.mapping这个包下，所以resource写成com/shi/mapping/userMapper.xml-->
        <mapper resource="com/shi/mapping/userMapper.xml"/>
        <!-- 注册UserMapper映射接口-->
        <mapper class="com.shi.mapping.UserMapperI"/>
    </mappers>

</configuration>
```

二、为实体类定义别名，简化sql映射xml文件中的引用

　　之前，我们在sql映射xml文件中的引用实体类时，需要写上实体类的全类名(包名+类名)，如下：
```xml
    <!-- 创建用户(Create) -->
    <insert id="addUser" parameterType="com.shi.mybatis.User">
        insert into users(name,age) values(#{name},#{age})
    </insert>
```
parameterType="com.shi.mybatis.User"这里写的实体类User的全类名com.shi.mybatis.User，每次都写这么一长串内容挺麻烦的，
而我们希望能够简写成下面的形式:

```xml
<insert id="addUser" parameterType="_User">
    insert into users(name,age) values(#{name},#{age})
</insert>
```
parameterType="_User"这样写就简单多了，为了达到这种效果，我们需要在conf.xml文件中为实体类="com.shi.mapping.User"定义一个别名为"_User"，具体做法如下：
　　在conf.xml文件中<configuration></configuration>标签中添加如下配置：

```xml
<typeAliases>
    <typeAlias type="com.shi.mapping.User" alias="_User"/>
</typeAliases>
```
注意typeAliases的位置，不要放在<configuration/>的头部或者尾部，否则会出错，我这里是放在了properties之后。具体如下：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration PUBLIC "-//mybatis.org//DTD Config 3.0//EN" "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>

    <!-- 引用db.properties配置文件 -->
    <properties resource="db.properties"/>

    <!--不要把typeAliases放在头部或者尾部，否则会报错-->
    <typeAliases>
         <typeAlias type="com.shi.mybatis.User" alias="UserModel"/>
    </typeAliases>

    <!--
        development : 开发模式
        work : 工作模式
     -->
    <environments default="development">
        <environment id="development">
            <transactionManager type="JDBC"/>
            <!-- 配置数据库连接信息 -->
            <dataSource type="POOLED">
                <!-- value属性值引用db.properties配置文件中配置的值 -->
                <property name="driver" value="${driver}"/>
                <property name="url" value="${url}"/>
                <property name="username" value="${name}"/>
                <property name="password" value="${password}"/>
            </dataSource>
        </environment>
    </environments>


    <mappers>
        <!-- 注册userMapper.xml文件，
        userMapper.xml位于com.shi.mapping这个包下，所以resource写成com/shi/mapping/userMapper.xml-->
        <mapper resource="com/shi/mapping/userMapper.xml"/>
        <!-- 注册UserMapper映射接口-->
        <mapper class="com.shi.mapping.UserMapperI"/>
    </mappers>

</configuration>

```

这样就可以为com.shi.mybatis.User类定义了一个别名为_User，以后_User就代表了com.shi.mybatis.User类，这样sql映射xml文件中的凡是需要引用com.shi.mybatis.User类的地方都可以使用_User来代替，这就达到了一个简化实体类引用的目的。

　　除了可以使用**<typeAlias type="com.shi.mybatis.User" alias="_User"/>**这种方式单独为某一个实体类设置别名之外，我们还可以使用如下的方式批量为某个包下的所有实体类设置别名，如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration PUBLIC "-//mybatis.org//DTD Config 3.0//EN" "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>

    <!-- 引用db.properties配置文件 -->
    <properties resource="db.properties"/>

    <!--不要把typeAliases放在头部或者尾部，否则会报错-->
    <typeAliases>
        <!--     <typeAlias type="com.shi.mybatis.User" alias="UserModel"/>-->
        <package name="com.shi.mybatis"/>
    </typeAliases>

    <!--
        development : 开发模式
        work : 工作模式
     -->
    <environments default="development">
        <environment id="development">
            <transactionManager type="JDBC"/>
            <!-- 配置数据库连接信息 -->
            <dataSource type="POOLED">
                <!-- value属性值引用db.properties配置文件中配置的值 -->
                <property name="driver" value="${driver}"/>
                <property name="url" value="${url}"/>
                <property name="username" value="${name}"/>
                <property name="password" value="${password}"/>
            </dataSource>
        </environment>
    </environments>


    <mappers>
        <!-- 注册userMapper.xml文件，
        userMapper.xml位于com.shi.mapping这个包下，所以resource写成com/shi/mapping/userMapper.xml-->
        <mapper resource="com/shi/mapping/userMapper.xml"/>
        <!-- 注册UserMapper映射接口-->
        <mapper class="com.shi.mapping.UserMapperI"/>
    </mappers>



</configuration>

```

<package name="com.shi.mybatis"/>就表示为这个包下面的所有实体类设置别名。MyBatis默认的设置别名的方式就是去除类所在的包后的简单的类名，比如com.shi.mapping.User这个实体类的别名就会被设置成User。

项目地址:[传送门](https://github.com/AFinalStone/MyBatis)