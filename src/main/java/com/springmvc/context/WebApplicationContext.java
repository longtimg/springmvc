package com.springmvc.context;

import com.springmvc.annotation.AutoWired;
import com.springmvc.annotation.Controller;
import com.springmvc.annotation.Service;
import com.springmvc.exception.ContextException;
import com.springmvc.xml.Xmlparser;
import org.apache.commons.lang3.concurrent.ConcurrentException;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class WebApplicationContext {

    String contextConfigLocation;
    List<String> classNameList = new ArrayList<String>();
    public Map<String,Object> locMap = new ConcurrentHashMap<>();
    public WebApplicationContext(String contextConfigLocation){
        this.contextConfigLocation = contextConfigLocation;
    }

    /**
     * 初始化spring容器
     */
    public void refresh(){
        String basePackage = Xmlparser.getBasePackage(contextConfigLocation.split(":")[1]);
        String[] basePackages = basePackage.split(",");
        if (basePackages.length > 0){
            for (String aPackage : basePackages) {

                executeScanPackage(aPackage);
            }
        }

        System.out.println("解析到的类名："+classNameList);

        //实例化spring容器中的bean
        executeInstance();

        //loc容器中的对象
        System.out.println("对象：："+locMap);

        //实现spring容器中对象的注入
        executeAutoWired();


    }

    private void executeAutoWired() {
        try {
            if (locMap.isEmpty()){
                throw new ContextException("未找到初始化的bean对象");

            }else {
                for (Map.Entry<String, Object> entry : locMap.entrySet()) {
                    String beanName = entry.getKey();
                    Object bean = entry.getValue();
                    Field[] fields = bean.getClass().getDeclaredFields();

                    for (Field field : fields) {
                        if (field.isAnnotationPresent(AutoWired.class)){
                            AutoWired annotation = field.getAnnotation(AutoWired.class);
                            String value = annotation.value();
                            if ("".equals(value)){
                                Class<?> type = field.getType();
                                beanName = type.getSimpleName().substring(0,1).toLowerCase()+type.getSimpleName().substring(1);
                            }
                            field.setAccessible(true);
                            field.set(bean,locMap.get(beanName));
                        }

                    }

                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } finally {
        }
    }

    /**
     * 实例化spring容器中的bean对象
     */
    private void executeInstance() {
        if (classNameList.isEmpty()){
            throw new ContextException("没有要实例化的class");
        }else{
            for (String className : classNameList) {
                try {
                    Class<?> aClass = Class.forName(className);
                    if (aClass.isAnnotationPresent(Controller.class)){
                        //控制层
                        String beanName = aClass.getSimpleName().substring(0,1).toLowerCase()+aClass.getSimpleName().substring(1);
                        locMap.put(beanName,aClass.newInstance());
                    }else if (aClass.isAnnotationPresent(Service.class)){
                        Service annotation = aClass.getAnnotation(Service.class);
                        String beanName = annotation.value();
                        if ("".equals(beanName)){
                            Class<?>[] interfaces = aClass.getInterfaces();
                            for (Class<?> anInterface : interfaces) {
                                String beanName1 = anInterface.getSimpleName().substring(0,1).toLowerCase()+anInterface.getSimpleName().substring(1);

                                locMap.put(beanName1,aClass.newInstance());
                            }
                        }else {
                            locMap.put(beanName,aClass.newInstance());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {

                }

            }
        }

    }

    private void executeScanPackage(String aPackage) {
        URL resource = this.getClass().getClassLoader().getResource("/" + aPackage.replaceAll("\\.", "/"));

        assert resource != null;
        String path = resource.getFile();

        File dir = new File(path);
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (file.isDirectory()){
                //当前是一个文件目录
                executeScanPackage(aPackage+"."+file.getName());
            }else{
                String className = aPackage +"."+file.getName().replaceAll(".class","");
                classNameList.add(className);
            }
        }
    }

}
