MyBatis学习总结(二)——使用MyBatis对表执行CRUD操作

>[github同步更新](https://github.com/AFinalStone?tab=repositories)
[博客同步更新](http://blog.csdn.net/abc6368765)
[简书同步更新](http://www.jianshu.com/u/0e4907a8f36b)

[参考原文地址](http://www.cnblogs.com/xdp-gacl/p/4261895.html)

#### 一、使用MyBatis对表执行CRUD操作——基于XML的实现
1、定义sql映射xml文件

　　userMapper.xml文件的内容如下：

```xml

<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<!-- 为这个mapper指定一个唯一的namespace，namespace的值习惯上设置成包名+sql映射文件名，这样就能够保证namespace的值是唯一的
例如namespace="com.shi.mapping.userMapper"就是com.shi.mapping(包名)+userMapper(userMapper.xml文件去除后缀)
 -->
<mapper namespace="com.shi.mapping.userMapper">
    <!-- 在select标签中编写查询的SQL语句， 设置select标签的id属性为getUser，id属性值必须是唯一的，不能够重复
    使用parameterType属性指明查询时使用的参数类型，resultType属性指明查询返回的结果集类型
    resultType="com.shi.mapping.User"就表示将查询结果封装成一个User类的对象返回
    User类就是users表所对应的实体类
    -->
    <!--
        根据id查询得到一个user对象
     -->
    <select id="getUser" parameterType="int"
            resultType="com.shi.mybatis.User">
        select * from users where id=#{id}
    </select>

    <!-- 创建用户(Create) -->
    <insert id="addUser" parameterType="com.shi.mybatis.User">
        insert into users(name,age) values(#{name},#{age})
    </insert>

    <!-- 删除用户(Remove) -->
    <delete id="deleteUser" parameterType="int">
        delete from users where id=#{id}
    </delete>

    <!-- 修改用户(Update) -->
    <update id="updateUser" parameterType="com.shi.mybatis.User">
        update users set name=#{name},age=#{age} where id=#{id}
    </update>

    <!-- 查询全部用户-->
    <select id="getAllUsers" resultType="com.shi.mybatis.User">
        select * from users
    </select>

</mapper>

```

单元测试类代码如下：
```java
package com.shi.mybatis;

import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

public class TestCRUDByXmlMapper {

    @Test
    public void testAdd(){
        //SqlSession sqlSession = MyBatisUtil.getSqlSession(false);
        SqlSession sqlSession = MyBatisUtil.getSqlSession(true);
        /**
         * 映射sql的标识字符串，
         * com.shi.mapping.userMapper是userMapper.xml文件中mapper标签的namespace属性的值，
         * addUser是insert标签的id属性值，通过insert标签的id属性值就可以找到要执行的SQL
         */
        String statement = "com.shi.mapping.userMapper.addUser";//映射sql的标识字符串
        User user = new User();
        user.setName("用户孤傲苍狼");
        user.setAge(20);
        //执行插入操作
        int retResult = sqlSession.insert(statement,user);
        //手动提交事务
        //sqlSession.commit();
        //使用SqlSession执行完SQL之后需要关闭SqlSession
        sqlSession.close();
        System.out.println(retResult);
    }

    @Test
    public void testUpdate(){
        SqlSession sqlSession = MyBatisUtil.getSqlSession(true);
        /**
         * 映射sql的标识字符串，
         * com.shi.mapping.userMapper是userMapper.xml文件中mapper标签的namespace属性的值，
         * updateUser是update标签的id属性值，通过update标签的id属性值就可以找到要执行的SQL
         */
        String statement = "com.shi.mapping.userMapper.updateUser";//映射sql的标识字符串
        User user = new User();
        user.setId(3);
        user.setName("孤傲苍狼");
        user.setAge(25);
        //执行修改操作
        int retResult = sqlSession.update(statement,user);
        //使用SqlSession执行完SQL之后需要关闭SqlSession
        sqlSession.close();
        System.out.println(retResult);
    }

    @Test
    public void testDelete(){
        SqlSession sqlSession = MyBatisUtil.getSqlSession(true);
        /**
         * 映射sql的标识字符串，
         * com.shi.mapping.userMapper是userMapper.xml文件中mapper标签的namespace属性的值，
         * deleteUser是delete标签的id属性值，通过delete标签的id属性值就可以找到要执行的SQL
         */
        String statement = "com.shi.mapping.userMapper.deleteUser";//映射sql的标识字符串
        //执行删除操作
        int retResult = sqlSession.delete(statement,5);
        //使用SqlSession执行完SQL之后需要关闭SqlSession
        sqlSession.close();
        System.out.println(retResult);
    }

    @Test
    public void testGetAll(){
        SqlSession sqlSession = MyBatisUtil.getSqlSession();
        /**
         * 映射sql的标识字符串，
         * com.shi.mapping.userMapper是userMapper.xml文件中mapper标签的namespace属性的值，
         * getAllUsers是select标签的id属性值，通过select标签的id属性值就可以找到要执行的SQL
         */
        String statement = "com.shi.mapping.userMapper.getAllUsers";//映射sql的标识字符串
        //执行查询操作，将查询结果自动封装成List<User>返回
        List<User> lstUsers = sqlSession.selectList(statement);
        //使用SqlSession执行完SQL之后需要关闭SqlSession
        sqlSession.close();
        System.out.println(lstUsers);
    }
}
```

#### 二、使用MyBatis对表执行CRUD操作——基于注解的实现

- 1、定义sql映射的接口

UserMapperI接口的代码如下：

```java

package com.shi.mapping;

import java.util.List;

import com.shi.mybatis.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @author AFinalStone
 * 定义sql映射的接口，使用注解指明方法要执行的SQL
 */
public interface UserMapperI {

    //使用@Insert注解指明add方法要执行的SQL
    @Insert("insert into users(name, age) values(#{name}, #{age})")
    public int add(User user);
    
    //使用@Delete注解指明deleteById方法要执行的SQL
    @Delete("delete from users where id=#{id}")
    public int deleteById(int id);
    
    //使用@Update注解指明update方法要执行的SQL
    @Update("update users set name=#{name},age=#{age} where id=#{id}")
    public int update(User user);
    
    //使用@Select注解指明getById方法要执行的SQL
    @Select("select * from users where id=#{id}")
    public User getById(int id);
    
    //使用@Select注解指明getAll方法要执行的SQL
    @Select("select * from users")
    public List<User> getAll();
}

```

需要说明的是，我们不需要针对UserMapperI接口去编写具体的实现类代码，这个具体的实现类由MyBatis帮我们动态构建出来，我们只需要直接拿来使用即可。

- 2、在conf.xml文件中注册这个映射接口

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

单元测试类的代码如下：

```java
package com.shi.mybatis;

import java.util.List;

import com.shi.mapping.UserMapperI;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

public class TestCRUDByAnnotationMapper {

    @Test
    public void testAdd(){
        SqlSession sqlSession = MyBatisUtil.getSqlSession(true);
        //得到UserMapperI接口的实现类对象，UserMapperI接口的实现类对象由sqlSession.getMapper(UserMapperI.class)动态构建出来
        UserMapperI mapper = sqlSession.getMapper(UserMapperI.class);
        User user = new User();
        user.setName("用户xdp");
        user.setAge(20);
        int add = mapper.add(user);
        //使用SqlSession执行完SQL之后需要关闭SqlSession
        sqlSession.close();
        System.out.println(add);
    }
    
    @Test
    public void testUpdate(){
        SqlSession sqlSession = MyBatisUtil.getSqlSession(true);
        //得到UserMapperI接口的实现类对象，UserMapperI接口的实现类对象由sqlSession.getMapper(UserMapperI.class)动态构建出来
        UserMapperI mapper = sqlSession.getMapper(UserMapperI.class);
        User user = new User();
        user.setId(3);
        user.setName("孤傲苍狼_xdp");
        user.setAge(26);
        //执行修改操作
        int retResult = mapper.update(user);
        //使用SqlSession执行完SQL之后需要关闭SqlSession
        sqlSession.close();
        System.out.println(retResult);
    }
    
    @Test
    public void testDelete(){
        SqlSession sqlSession = MyBatisUtil.getSqlSession(true);
        //得到UserMapperI接口的实现类对象，UserMapperI接口的实现类对象由sqlSession.getMapper(UserMapperI.class)动态构建出来
        UserMapperI mapper = sqlSession.getMapper(UserMapperI.class);
        //执行删除操作
        int retResult = mapper.deleteById(7);
        //使用SqlSession执行完SQL之后需要关闭SqlSession
        sqlSession.close();
        System.out.println(retResult);
    }
    
    @Test
    public void testGetUser(){
        SqlSession sqlSession = MyBatisUtil.getSqlSession();
        //得到UserMapperI接口的实现类对象，UserMapperI接口的实现类对象由sqlSession.getMapper(UserMapperI.class)动态构建出来
        UserMapperI mapper = sqlSession.getMapper(UserMapperI.class);
        //执行查询操作，将查询结果自动封装成User返回
        User user = mapper.getById(8);
        //使用SqlSession执行完SQL之后需要关闭SqlSession
        sqlSession.close();
        System.out.println(user);
    }
    
    @Test
    public void testGetAll(){
        SqlSession sqlSession = MyBatisUtil.getSqlSession();
        //得到UserMapperI接口的实现类对象，UserMapperI接口的实现类对象由sqlSession.getMapper(UserMapperI.class)动态构建出来
        UserMapperI mapper = sqlSession.getMapper(UserMapperI.class);
        //执行查询操作，将查询结果自动封装成List<User>返回
        List<User> lstUsers = mapper.getAll();
        //使用SqlSession执行完SQL之后需要关闭SqlSession
        sqlSession.close();
        System.out.println(lstUsers);
    }
}
```

用到的MyBatisUtil工具类代码如下：

```java

package com.shi.mybatis;

import java.io.InputStream;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class MyBatisUtil {

    /**
     * 获取SqlSessionFactory
     * @return SqlSessionFactory
     */
    public static SqlSessionFactory getSqlSessionFactory() {
        String resource = "conf.xml";
        InputStream is = MyBatisUtil.class.getClassLoader().getResourceAsStream(resource);
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(is);
        return factory;
    }
    
    /**
     * 获取SqlSession
     * @return SqlSession
     */
    public static SqlSession getSqlSession() {
        return getSqlSessionFactory().openSession();
    }
    
    /**
     * 获取SqlSession
     * @param isAutoCommit 
     *         true 表示创建的SqlSession对象在执行完SQL之后会自动提交事务
     *         false 表示创建的SqlSession对象在执行完SQL之后不会自动提交事务，这时就需要我们手动调用sqlSession.commit()提交事务
     * @return SqlSession
     */
    public static SqlSession getSqlSession(boolean isAutoCommit) {
        return getSqlSessionFactory().openSession(isAutoCommit);
    }
}

```

以上的相关代码是全部测试通过的，关于使用MyBatis对表执行CRUD操作的内容就这么多。

项目地址:[传送门](https://github.com/AFinalStone/MyBatis)