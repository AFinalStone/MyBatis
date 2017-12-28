package com.shi.mapping.demo02;

import com.shi.mybatis.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author AFinalStone
 * 定义sql映射的接口，使用注解指明方法要执行的SQL
 */
public interface UserMapperIByXml {
    //使用@Select注解指明getById方法要执行的SQL
    public User getUser(int id);

    //使用@Insert注解指明add方法要执行的SQL
    public int addUser(User user);
    
    //使用@Delete注解指明deleteById方法要执行的SQL
    public int deleteUser(int id);
    
    //使用@Update注解指明update方法要执行的SQL
    public int updateUser(User user);
    
    //使用@Select注解指明getAll方法要执行的SQL
    public List<User> getAllUsers();
}