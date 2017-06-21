### MyBatis学习总结(五)——实现关联表查询

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

![关系图](https://raw.githubusercontent.com/AFinalStone/MyBatis/master/MyBatis_05/pic/relationship.png)

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
　　2、Classe01s类，Classes01类是class表对应的实体类

```java
package com.shi.mybatis;

/**
 * @author Afinalstone
 * 定义class表对应的实体类
 */
public class Classes01 {

    //定义实体类的属性，与class表中的字段对应
    private int id;            //id===>c_id
    private String name;    //name===>c_name
    
    /**
     * class表中有一个teacher_id字段，所以在Classes类中定义一个teacher属性，
     * 用于维护teacher和class之间的一对一关系，通过这个teacher属性就可以知道这个班级是由哪个老师负责的
     */
    private Teacher teacher;

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

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    @Override
    public String toString() {
        return "Classes01 [id=" + id + ", name=" + name + ", teacher=" + teacher+ "]";
    }
}
```

　　在conf.xml文件中注册classMapper.xml
```xml

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
        userMapper.xml位于com.shi.mapping这个包下，所以resource写成com/shi/mapping/orderMapper.xml.xml-->
        <mapper resource="com/shi/mapping/classMapper.xml"/>
    </mappers>
    
</configuration>

```

1.4、定义sql映射文件classMapper.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<!-- 为这个mapper指定一个唯一的namespace，namespace的值习惯上设置成包名+sql映射文件名，这样就能够保证namespace的值是唯一的
例如namespace="com.shi.mapping.classMapper"就是com.shi.mapping(包名)+classMapper(classMapper.xml文件去除后缀)
 -->
<mapper namespace="com.shi.mapping.classMapper">

    <!--
        根据班级id查询班级信息(带老师的信息)
        ##1. 联表查询
        SELECT * FROM class c,teacher t WHERE c.teacher_id=t.t_id AND c.c_id=1;

        ##2. 执行两次查询
        SELECT * FROM class WHERE c_id=1;  //teacher_id=1
        SELECT * FROM teacher WHERE t_id=1;//使用上面得到的teacher_id
     -->

    <!--
    方式一：嵌套结果：使用嵌套结果映射来处理重复的联合结果的子集
             封装联表查询的数据(去除重复的数据)
        select * from class c, teacher t where c.teacher_id=t.t_id and c.c_id=1
    -->
    <select id="getClass" parameterType="int" resultMap="ClassResultMap">
        select * from class c, teacher t where c.teacher_id = t.t_id and c.c_id = #{id}
    </select>

    <!-- 使用resultMap映射实体类和字段之间的一一对应关系 -->
    <resultMap type="com.shi.mapping.Classes01" id="ClassResultMap">
        <id property="id" column="c_id"/>
        <result property="name" column="c_name"/>
        <association property="teacher" javaType="com.shi.mapping.Teacher">
            <id property="id" column="t_id"/>
            <result property="name" column="t_name"/>
        </association>
    </resultMap>

    <!--
    方式二：嵌套查询：通过执行另外一个SQL映射语句来返回预期的复杂类型
        SELECT * FROM class WHERE c_id=1;
        SELECT * FROM teacher WHERE t_id=1   //1 是上一个查询得到的teacher_id的值
    -->
    <select id="getClass2" parameterType="int" resultMap="ClassResultMap2">
        select * from class where c_id=#{id}
    </select>
    <!-- 使用resultMap映射实体类和字段之间的一一对应关系 -->
    <resultMap type="com.shi.mapping.Classes" id="ClassResultMap2">
        <id property="id" column="c_id"/>
        <result property="name" column="c_name"/>
        <association property="teacher" column="teacher_id" select="getTeacher"/>
    </resultMap>

    <select id="getTeacher" parameterType="int" resultType="com.shi.mapping.Teacher">
        SELECT t_id id, t_name name FROM teacher WHERE t_id=#{id}
    </select>

</mapper>

```

1.5、编写单元测试代码

```java
package com.shi.mybatis;

import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

public class Test3 {
    
    @Test
    public void testGetClass(){
        SqlSession sqlSession = MyBatisUtil.getSqlSession();
        /**
         * 映射sql的标识字符串，
         * me.gacl.mapping.classMapper是classMapper.xml文件中mapper标签的namespace属性的值，
         * getClass是select标签的id属性值，通过select标签的id属性值就可以找到要执行的SQL
         */
        String statement = "me.gacl.mapping.classMapper.getClass";//映射sql的标识字符串
        //执行查询操作，将查询结果自动封装成Classes对象返回
        Classes clazz = sqlSession.selectOne(statement,1);//查询class表中id为1的记录
        //使用SqlSession执行完SQL之后需要关闭SqlSession
        sqlSession.close();
        System.out.println(clazz);
    }
    
    @Test
    public void testGetClass2(){
        SqlSession sqlSession = MyBatisUtil.getSqlSession();
        /**
         * 映射sql的标识字符串，
         * me.gacl.mapping.classMapper是classMapper.xml文件中mapper标签的namespace属性的值，
         * getClass2是select标签的id属性值，通过select标签的id属性值就可以找到要执行的SQL
         */
        String statement = "me.gacl.mapping.classMapper.getClass2";//映射sql的标识字符串
        //执行查询操作，将查询结果自动封装成Classes对象返回
        Classes clazz = sqlSession.selectOne(statement,1);//查询class表中id为1的记录
        //使用SqlSession执行完SQL之后需要关闭SqlSession
        sqlSession.close();
        System.out.println(clazz);
    }
}
```

1.6、MyBatis一对一关联查询总结

　　MyBatis中使用association标签来解决一对一的关联查询，association标签可用的属性如下：

- property:对象属性的名称
- javaType:对象属性的类型
- column:所对应的外键字段名称
- select:使用另一个查询封装的结果


#### 二、一对多关联
2.1、提出需求

　　根据classId查询对应的班级信息,包括学生,老师

2.2、创建表和数据

　　在上面的一对一关联查询演示中，我们已经创建了班级表和教师表，因此这里再创建一张学生表

```db
CREATE TABLE student(
    s_id INT PRIMARY KEY AUTO_INCREMENT, 
    s_name VARCHAR(20), 
    class_id INT
);
INSERT INTO student(s_name, class_id) VALUES('student_A', 1);
INSERT INTO student(s_name, class_id) VALUES('student_B', 1);
INSERT INTO student(s_name, class_id) VALUES('student_C', 1);
INSERT INTO student(s_name, class_id) VALUES('student_D', 2);
INSERT INTO student(s_name, class_id) VALUES('student_E', 2);
INSERT INTO student(s_name, class_id) VALUES('student_F', 2);
```

![关系图](https://raw.githubusercontent.com/AFinalStone/MyBatis/master/MyBatis_05/pic/relationship02.png)

2.3、定义实体类

　　1、Student类

```java
package com.shi.mybatis;

/**
 * @author AFinalStone
 * 定义student表所对应的实体类
 */
public class Student {

    //定义属性，和student表中的字段对应
    private int id;            //id===>s_id
    private String name;    //name===>s_name
    
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
        return "Student [id=" + id + ", name=" + name + "]";
    }
}
```

　　2、修改Classes类，添加一个List<Student> students属性，使用一个List<Student>集合属性表示班级拥有的学生，如下：

```java

package com.shi.mybatis;

import java.util.List;

/**
 * @author AFinalStone
 * 定义class表对应的实体类
 */
public class Classes02 {

    //定义实体类的属性，与class表中的字段对应
    private int id;            //id===>c_id
    private String name;    //name===>c_name
    
    /**
     * class表中有一个teacher_id字段，所以在Classes类中定义一个teacher属性，
     * 用于维护teacher和class之间的一对一关系，通过这个teacher属性就可以知道这个班级是由哪个老师负责的
     */
    private Teacher teacher;
    //使用一个List<Student>集合属性表示班级拥有的学生
    private List<Student> students;

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

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    @Override
    public String toString() {
        return "Classes02 [id=" + id + ", name=" + name + ", teacher=" + teacher
                + ", students=" + students + "]";
    }
}

```
2.4、修改sql映射文件classMapper.xml

　　添加如下的SQL映射信息

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<!-- 为这个mapper指定一个唯一的namespace，namespace的值习惯上设置成包名+sql映射文件名，这样就能够保证namespace的值是唯一的
例如namespace="com.shi.mapping.classMapper"就是com.shi.mapping(包名)+classMapper(classMapper.xml文件去除后缀)
 -->
<mapper namespace="com.shi.mapping.classMapper">

    <!--
        根据班级id查询班级信息(带老师的信息)
        ##1. 联表查询
        SELECT * FROM class c,teacher t WHERE c.teacher_id=t.t_id AND c.c_id=1;

        ##2. 执行两次查询
        SELECT * FROM class WHERE c_id=1;  //teacher_id=1
        SELECT * FROM teacher WHERE t_id=1;//使用上面得到的teacher_id
     -->

    <!--
    方式一：嵌套结果：使用嵌套结果映射来处理重复的联合结果的子集
             封装联表查询的数据(去除重复的数据)
        select * from class c, teacher t where c.teacher_id=t.t_id and c.c_id=1
    -->
    <select id="getClass" parameterType="int" resultMap="ClassResultMap">
        select * from class c, teacher t where c.teacher_id=t.t_id and c.c_id=#{id}
    </select>
    <!-- 使用resultMap映射实体类和字段之间的一一对应关系 -->
    <resultMap type="com.shi.mapping.Classes" id="ClassResultMap">
        <id property="id" column="c_id"/>
        <result property="name" column="c_name"/>
        <association property="teacher" javaType="com.shi.mapping.Teacher">
            <id property="id" column="t_id"/>
            <result property="name" column="t_name"/>
        </association>
    </resultMap>

    <!--
    方式二：嵌套查询：通过执行另外一个SQL映射语句来返回预期的复杂类型
        SELECT * FROM class WHERE c_id=1;
        SELECT * FROM teacher WHERE t_id=1   //1 是上一个查询得到的teacher_id的值
    -->
    <select id="getClass2" parameterType="int" resultMap="ClassResultMap2">
        select * from class where c_id=#{id}
    </select>
    <!-- 使用resultMap映射实体类和字段之间的一一对应关系 -->
    <resultMap type="com.shi.mapping.Classes" id="ClassResultMap2">
        <id property="id" column="c_id"/>
        <result property="name" column="c_name"/>
        <association property="teacher" column="teacher_id" select="getTeacher"/>
    </resultMap>

    <select id="getTeacher" parameterType="int" resultType="com.shi.mapping.Teacher">
        SELECT t_id id, t_name name FROM teacher WHERE t_id=#{id}
    </select>


    <!--第二次添加的部分-->

    <!--
        根据classId查询对应的班级信息,包括学生,老师
     -->
    <!--
    方式一: 嵌套结果: 使用嵌套结果映射来处理重复的联合结果的子集
    SELECT * FROM class c, teacher t,student s WHERE c.teacher_id=t.t_id AND c.C_id=s.class_id AND  c.c_id=1
     -->
    <select id="getClass3" parameterType="int" resultMap="ClassResultMap3">
        select * from class c, teacher t,student s where c.teacher_id=t.t_id and c.C_id=s.class_id and  c.c_id=#{id}
    </select>
    <resultMap type="me.gacl.domain.Classes" id="ClassResultMap3">
        <id property="id" column="c_id"/>
        <result property="name" column="c_name"/>
        <association property="teacher" column="teacher_id" javaType="me.gacl.domain.Teacher">
            <id property="id" column="t_id"/>
            <result property="name" column="t_name"/>
        </association>
        <!-- ofType指定students集合中的对象类型 -->
        <collection property="students" ofType="me.gacl.domain.Student">
            <id property="id" column="s_id"/>
            <result property="name" column="s_name"/>
        </collection>
    </resultMap>

    <!--
        方式二：嵌套查询：通过执行另外一个SQL映射语句来返回预期的复杂类型
            SELECT * FROM class WHERE c_id=1;
            SELECT * FROM teacher WHERE t_id=1   //1 是上一个查询得到的teacher_id的值
            SELECT * FROM student WHERE class_id=1  //1是第一个查询得到的c_id字段的值
     -->
    <select id="getClass4" parameterType="int" resultMap="ClassResultMap4">
        select * from class where c_id=#{id}
    </select>
    <resultMap type="me.gacl.domain.Classes" id="ClassResultMap4">
        <id property="id" column="c_id"/>
        <result property="name" column="c_name"/>
        <association property="teacher" column="teacher_id" javaType="me.gacl.domain.Teacher" select="getTeacher2"></association>
        <collection property="students" ofType="me.gacl.domain.Student" column="c_id" select="getStudent"></collection>
    </resultMap>

    <select id="getTeacher2" parameterType="int" resultType="me.gacl.domain.Teacher">
        SELECT t_id id, t_name name FROM teacher WHERE t_id=#{id}
    </select>

    <select id="getStudent" parameterType="int" resultType="me.gacl.domain.Student">
        SELECT s_id id, s_name name FROM student WHERE class_id=#{id}
    </select>
</mapper>
```

2.5、编写单元测试代码

```java
package com.shi.mybatis;

import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
import org.junit.internal.Classes;

public class Test4 {
    
    @Test
    public void testGetClass3(){
        SqlSession sqlSession = MyBatisUtil.getSqlSession();
        /**
         * 映射sql的标识字符串，
         * me.gacl.mapping.classMapper是classMapper.xml文件中mapper标签的namespace属性的值，
         * getClass3是select标签的id属性值，通过select标签的id属性值就可以找到要执行的SQL
         */
        String statement = "me.gacl.mapping.classMapper.getClass3";//映射sql的标识字符串
        //执行查询操作，将查询结果自动封装成Classes对象返回
        Classes clazz = sqlSession.selectOne(statement,1);//查询class表中id为1的记录
        //使用SqlSession执行完SQL之后需要关闭SqlSession
        sqlSession.close();
        //打印结果：Classes [id=1, name=class_a, teacher=Teacher [id=1, name=teacher1], students=[Student [id=1, name=student_A], Student [id=2, name=student_B], Student [id=3, name=student_C]]]
        System.out.println(clazz);
    }
    
    @Test
    public void testGetClass4(){
        SqlSession sqlSession = MyBatisUtil.getSqlSession();
        /**
         * 映射sql的标识字符串，
         * me.gacl.mapping.classMapper是classMapper.xml文件中mapper标签的namespace属性的值，
         * getClass4是select标签的id属性值，通过select标签的id属性值就可以找到要执行的SQL
         */
        String statement = "me.gacl.mapping.classMapper.getClass4";//映射sql的标识字符串
        //执行查询操作，将查询结果自动封装成Classes对象返回
        Classes clazz = sqlSession.selectOne(statement,1);//查询class表中id为1的记录
        //使用SqlSession执行完SQL之后需要关闭SqlSession
        sqlSession.close();
        //打印结果：Classes [id=1, name=class_a, teacher=Teacher [id=1, name=teacher1], students=[Student [id=1, name=student_A], Student [id=2, name=student_B], Student [id=3, name=student_C]]]
        System.out.println(clazz);
    }
}
```

2.6、MyBatis一对多关联查询总结

　　MyBatis中使用collection标签来解决一对多的关联查询，ofType属性指定集合中元素的对象类型。