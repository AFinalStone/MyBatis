MyBatis学习总结(五)——实现关联表查询

#### 一、一对一关联

1.1、提出需求

　　根据班级id查询班级信息(带老师的信息)

1.2、创建表和数据

　　创建一张教师表和班级表，这里我们假设一个老师只负责教一个班，那么老师和班级之间的关系就是一种一对一的关系。
```db
CREATE TABLE teacher(
    t_id INT PRIMARY KEY AUTO_INCREMENT, 
    t_name VARCHAR(20)
);
CREATE TABLE class(
    c_id INT PRIMARY KEY AUTO_INCREMENT, 
    c_name VARCHAR(20), 
    teacher_id INT
);
ALTER TABLE class ADD CONSTRAINT fk_teacher_id FOREIGN KEY (teacher_id) REFERENCES teacher(t_id);    

INSERT INTO teacher(t_name) VALUES('teacher1');
INSERT INTO teacher(t_name) VALUES('teacher2');

INSERT INTO class(c_name, teacher_id) VALUES('class_a', 1);
INSERT INTO class(c_name, teacher_id) VALUES('class_b', 2);

```
　表之间的关系如下：

![关系图](/pic/relationship.png)

1.3、定义实体类

　　1、Teacher类，Teacher类是teacher表对应的实体类。

```java
package com.shi.mybatis;

/**
 * @author AFinalStone
 * 定义teacher表对应的实体类
 */
public class Teacher {

    //定义实体类的属性，与teacher表中的字段对应
    private int id;            //id===>t_id
    private String name;    //name===>t_name

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Teacher [id=" + id + ", name=" + name + "]";
    }
}

```
　　2、Classes类，Classes类是class表对应的实体类

```java

```

　　在conf.xml文件中注册classMapper.xml
```xm;
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
        <!-- 注册classMapper.xml文件，
        userMapper.xml位于com.shi.mapping这个包下，所以resource写成com/shi/mapping/orderMapper.xml.xml-->
        <mapper resource="com/shi/mapping/classMapper.xml"/>
    </mappers>



</configuration>


```