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