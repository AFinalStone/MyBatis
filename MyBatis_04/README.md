MyBatis学习总结(四)——解决字段名与实体类属性名不相同的冲突

>[github同步更新](https://github.com/AFinalStone?tab=repositories)
[博客同步更新](http://blog.csdn.net/abc6368765)
[简书同步更新](http://www.jianshu.com/u/0e4907a8f36b)
项目地址:[传送门](https://github.com/AFinalStone/MyBatis)

　　在平时的开发中，我们表中的字段名和表对应实体类的属性名称不一定都是完全相同的，下面来演示一下这种情况下的如何解决字段名与实体类属性名不相同的冲突。

#### 一、准备演示需要使用的表和数据

```db

CREATE TABLE orders(
    order_id INT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(20), 
    order_price FLOAT
);
INSERT INTO orders(order_no, order_price) VALUES('aaaa', 23);
INSERT INTO orders(order_no, order_price) VALUES('bbbb', 33);
INSERT INTO orders(order_no, order_price) VALUES('cccc', 22);

```
#### 二、定义实体类

```java
package com.shi.mybatis;

/**
 * @author AFinalStone
 * 定义orders表对应的实体类
 */
public class Order {
    /**
     * 
    CREATE TABLE orders(
        order_id INT PRIMARY KEY AUTO_INCREMENT,
        order_no VARCHAR(20), 
        order_price FLOAT
    );
     */
    
    //Order实体类中属性名和orders表中的字段名是不一样的
    private int id;                //id===>order_id
    private String orderNo;        //orderNo===>order_no
    private float price;        //price===>order_price

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Order [id=" + id + ", orderNo=" + orderNo + ", price=" + price+ "]";
    }
}
```
#### 三、编写测试代码

3.1、编写SQL的xml映射文件

- 1、创建一个orderMapper.xml文件，orderMapper.xml的内容如下：

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<!-- 为这个mapper指定一个唯一的namespace，namespace的值习惯上设置成包名+sql映射文件名，这样就能够保证namespace的值是唯一的
例如namespace="com.shi.mapping.orderMapper"就是com.shi.mapping(包名)+orderMapper(orderMapper.xml文件去除后缀)
 -->
<mapper namespace="com.shi.mapping.orderMapper">

    <!--
        根据id查询得到一个order对象，使用这个查询是查询不到我们想要的结果的，
        这主要是因为实体类的属性名和数据库的字段名对应不上的原因，因此无法查询出对应的记录
     -->
    <select id="getOrderById" parameterType="int"
            resultType="Order">
        select * from orders where order_id=#{id}
    </select>

    <!--
        根据id查询得到一个order对象，使用这个查询是可以正常查询到我们想要的结果的，
        这是因为我们将查询的字段名都起一个和实体类属性名相同的别名，这样实体类的属性名和查询结果中的字段名就可以一一对应上
     -->
    <select id="selectOrder" parameterType="int"
            resultType="Order">
        select order_id id, order_no orderNo,order_price price from orders where order_id=#{id}
    </select>

    <!--
    根据id查询得到一个order对象，使用这个查询是可以正常查询到我们想要的结果的，
    这是因为我们通过<resultMap>映射实体类属性名和表的字段名一一对应关系 -->
    <select id="selectOrderResultMap" parameterType="int" resultMap="orderResultMap">
        select * from orders where order_id=#{id}
    </select>
    <!--通过<resultMap>映射实体类属性名和表的字段名对应关系 -->
    <resultMap type="Order" id="orderResultMap">
        <!-- 用id属性来映射主键字段 -->
        <id property="id" column="order_id"/>
        <!-- 用result属性来映射非主键字段 -->
        <result property="orderNo" column="order_no"/>
        <result property="price" column="order_price"/>
    </resultMap>

</mapper>
```
- 2、在conf.xml文件中注册orderMapper.xml映射文件

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
        userMapper.xml位于com.shi.mapping这个包下，所以resource写成com/shi/mapping/orderMapper.xml.xml-->
        <mapper resource="com/shi/mapping/orderMapper.xml"/>
    </mappers>


</configuration>

```

3.2、编写单元测试代码

```java
package com.shi.mybatis;


import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

public class TestOrder {
    
    @Test
    public void testGetOrderById(){
        SqlSession sqlSession = MyBatisUtil.getSqlSession();
        /**
         * 映射sql的标识字符串，
         * com.shi.mapping.orderMapper是orderMapper.xml文件中mapper标签的namespace属性的值，
         * getOrderById是select标签的id属性值，通过select标签的id属性值就可以找到要执行的SQL
         */
        String statement = "com.shi.mapping.orderMapper.getOrderById";//映射sql的标识字符串
        //执行查询操作，将查询结果自动封装成Order对象返回
        Order order = sqlSession.selectOne(statement,1);//查询orders表中id为1的记录
        //使用SqlSession执行完SQL之后需要关闭SqlSession
        sqlSession.close();
        System.out.println(order);//打印结果：null，也就是没有查询出相应的记录
    }
    
    @Test
    public void testGetOrderById2(){
        SqlSession sqlSession = MyBatisUtil.getSqlSession();
        /**
         * 映射sql的标识字符串，
         * com.shi.mapping.orderMapper是orderMapper.xml文件中mapper标签的namespace属性的值，
         * selectOrder是select标签的id属性值，通过select标签的id属性值就可以找到要执行的SQL
         */
        String statement = "com.shi.mapping.orderMapper.selectOrder";//映射sql的标识字符串
        //执行查询操作，将查询结果自动封装成Order对象返回
        Order order = sqlSession.selectOne(statement,1);//查询orders表中id为1的记录
        //使用SqlSession执行完SQL之后需要关闭SqlSession
        sqlSession.close();
        System.out.println(order);//打印结果：Order [id=1, orderNo=aaaa, price=23.0]
    }
    
    @Test
    public void testGetOrderById3(){
        SqlSession sqlSession = MyBatisUtil.getSqlSession();
        /**
         * 映射sql的标识字符串，
         * com.shi.mapping.orderMapper是orderMapper.xml文件中mapper标签的namespace属性的值，
         * selectOrderResultMap是select标签的id属性值，通过select标签的id属性值就可以找到要执行的SQL
         */
        String statement = "com.shi.mapping.orderMapper.selectOrderResultMap";//映射sql的标识字符串
        //执行查询操作，将查询结果自动封装成Order对象返回
        Order order = sqlSession.selectOne(statement,1);//查询orders表中id为1的记录
        //使用SqlSession执行完SQL之后需要关闭SqlSession
        sqlSession.close();
        System.out.println(order);//打印结果：Order [id=1, orderNo=aaaa, price=23.0]
    }
}
```
　　执行单元测试的结果：

　　1、testGetOrderById方法执行查询后返回一个null。

　　2、testGetOrderById2方法和testGetOrderById3方法执行查询后可以正常得到想要的结果。

#### 四、总结

　　上面的测试代码演示当实体类中的属性名和表中的字段名不一致时，使用MyBatis进行查询操作时无法查询出相应的结果的问题以及针对问题采用的两种办法：

　　解决办法一: 通过在查询的sql语句中定义字段名的别名，让字段名的别名和实体类的属性名一致，这样就可以表的字段名和实体类的属性名一一对应上了，这种方式是通过在sql语句中定义别名来解决字段名和属性名的映射关系的。

　　解决办法二: 通过<resultMap>来映射字段名和实体类属性名的一一对应关系。这种方式是使用MyBatis提供的解决方式来解决字段名和属性名的映射关系的。