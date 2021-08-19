package com.springmvc.servlet;

import com.springmvc.annotation.Controller;
import com.springmvc.annotation.RequestMapping;
import com.springmvc.annotation.RequestParam;
import com.springmvc.context.WebApplicationContext;
import com.springmvc.exception.ContextException;
import com.springmvc.handler.MyHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DispatcherServlet extends HttpServlet {

    private WebApplicationContext webApplicationContext;
    Map<String,MyHandler> handlerMap = new ConcurrentHashMap<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //super.doPost(req, resp);
        executeDispatch(req, resp);
    }

    private void executeDispatch(HttpServletRequest req, HttpServletResponse resp) {
        MyHandler handler = getHandler(req);
        try {
            if (handler == null){
                resp.getWriter().print("<h1>404 NOT FOUND</h1>");
            }else{
                Type[] parameterTypes = handler.getMehod().getGenericParameterTypes();
                for (int i = 0; i < parameterTypes.length; i++) {
                    Type type = parameterTypes[i];
                    String typeName = type.getTypeName();

                }
                Object[] param = new Object[parameterTypes.length];
                for (Map.Entry<String, String[]> entry : req.getParameterMap().entrySet()) {
                    String[] value = entry.getValue();
                    param[2] = value[0];
                }
                param[0] = req;
                param[1] = resp;

                handler.getMehod().invoke(handler.getController(),param);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    public MyHandler getHandler(HttpServletRequest request){
        String requestURI = request.getRequestURI();
        MyHandler handler = handlerMap.get(requestURI);
        return handler;
    }



    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    public void init() throws ServletException {
        // servlet初始化的时候，读取初始化参数，classpath :springmvc.xml
        String contextConfigLocation = this.getServletConfig().getInitParameter("contextConfigLocation");

        webApplicationContext = new WebApplicationContext(contextConfigLocation);
        webApplicationContext.refresh();
        //初始化请求映射
        initHandlerMapping();

        System.out.println(handlerMap);



    }

    private void initHandlerMapping() {
        if (webApplicationContext.locMap.isEmpty()) {
            throw new ContextException("spring容器为空！");
        }else{
            for (Map.Entry<String, Object> entry : webApplicationContext.locMap.entrySet()) {
                Class<?> aClass = entry.getValue().getClass();
                if (aClass.isAnnotationPresent(Controller.class)){
                    Method[] methods = aClass.getMethods();
                    for (Method method : methods) {
                        if (method.isAnnotationPresent(RequestMapping.class)){
                            RequestMapping annotation = method.getDeclaredAnnotation(RequestMapping.class);
                            String url = annotation.value();
                            MyHandler handler = new MyHandler(url,entry.getValue(),method);
                            handlerMap.put(url,handler);
                        }
                    }
                }

            }
        }

    }
}
