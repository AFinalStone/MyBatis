package com.shi.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.shi.domain.User;
import com.shi.service.UserServiceI;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;


/**
 * @author gacl
 * @WebServlet是Servlet3.0提供的注解，目的是将一个继承了HttpServlet类的普通java类标注为一个Servlet
 * UserServlet使用了@WebServlet标注之后，就不需要在web.xml中配置了
 */
@WebServlet("/UserServlet")
public class UserServlet extends HttpServlet {

    //处理业务逻辑的userService
    private UserServiceI userService;
    
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //获取所有的用户信息
        List<User> lstUsers = userService.getAllUser();
        request.setAttribute("lstUsers", lstUsers);
        request.getRequestDispatcher("/src/main/webapp/index.jsp").forward(request, response);
    }
    
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        this.doGet(request, response);
    }

    public void init() throws ServletException {
        //在Servlet初始化时获取Spring上下文对象(ApplicationContext)
        ApplicationContext ac = WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());
        //从ApplicationContext中获取userService
        userService = (UserServiceI) ac.getBean("userService");
    }
}