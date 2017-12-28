package com.shi.mapping.demo02;

import com.shi.mybatis.MyBatisUtil;
import com.shi.mybatis.User;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.List;

public class TestCRUD {

    @Test
    public void testAdd(){
        SqlSession sqlSession = MyBatisUtil.getSqlSession(true);
        //得到UserMapperIByXml接口的实现类对象，UserMapperIByXml接口的实现类对象由sqlSession.getMapper(UserMapperIByXml.class)动态构建出来
        UserMapperIByXml mapper = sqlSession.getMapper(UserMapperIByXml.class);
        User user = new User();
        user.setName("用户xdp");
        user.setAge(20);
        int add = mapper.addUser(user);
        //使用SqlSession执行完SQL之后需要关闭SqlSession
        sqlSession.close();
        System.out.println(add);
    }
    
    @Test
    public void testUpdate(){
        SqlSession sqlSession = MyBatisUtil.getSqlSession(true);
        //得到UserMapperIByXml接口的实现类对象，UserMapperIByXml接口的实现类对象由sqlSession.getMapper(UserMapperIByXml.class)动态构建出来
        UserMapperIByXml mapper = sqlSession.getMapper(UserMapperIByXml.class);
        User user = new User();
        user.setId(3);
        user.setName("孤傲苍狼_xdp");
        user.setAge(26);
        //执行修改操作
        int retResult = mapper.updateUser(user);
        //使用SqlSession执行完SQL之后需要关闭SqlSession
        sqlSession.close();
        System.out.println(retResult);
    }
    
    @Test
    public void testDelete(){
        SqlSession sqlSession = MyBatisUtil.getSqlSession(true);
        //得到UserMapperIByXml接口的实现类对象，UserMapperIByXml接口的实现类对象由sqlSession.getMapper(UserMapperIByXml.class)动态构建出来
        UserMapperIByXml mapper = sqlSession.getMapper(UserMapperIByXml.class);
        //执行删除操作
        int retResult = mapper.deleteUser(7);
        //使用SqlSession执行完SQL之后需要关闭SqlSession
        sqlSession.close();
        System.out.println(retResult);
    }
    
    @Test
    public void testGetUser(){
        SqlSession sqlSession = MyBatisUtil.getSqlSession();
        //得到UserMapperIByXml接口的实现类对象，UserMapperIByXml接口的实现类对象由sqlSession.getMapper(UserMapperIByXml.class)动态构建出来
        UserMapperIByXml mapper = sqlSession.getMapper(UserMapperIByXml.class);
        //执行查询操作，将查询结果自动封装成User返回
        User user = mapper.getUser(8);
        //使用SqlSession执行完SQL之后需要关闭SqlSession
        sqlSession.close();
        System.out.println(user);
    }
    
    @Test
    public void testGetAll(){
        SqlSession sqlSession = MyBatisUtil.getSqlSession();
        //得到UserMapperIByXml接口的实现类对象，UserMapperIByXml接口的实现类对象由sqlSession.getMapper(UserMapperIByXml.class)动态构建出来
        UserMapperIByXml mapper = sqlSession.getMapper(UserMapperIByXml.class);
        //执行查询操作，将查询结果自动封装成List<User>返回
        List<User> lstUsers = mapper.getAllUsers();
        //使用SqlSession执行完SQL之后需要关闭SqlSession
        sqlSession.close();
        System.out.println(lstUsers);
    }
}