package com.atyun;

import com.atyun.application.annotation.Autowired;
import com.atyun.application.annotation.Component;

import java.awt.print.Pageable;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MyApplicationContext {
     

    private Map<String, Object> beanMap = new HashMap<>();

    public MyApplicationContext(String rulName) {
        try {
            String packageName = rulName.replace(".", "/");
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            URL url = classLoader.getResources(packageName).nextElement();
            if (url == null) {
                throw new RuntimeException("package is null");
            }
            File files = new File(url.getFile());
            for (File file : Objects.requireNonNull(files.listFiles())) {
                if(file.isDirectory()) {
                    dfs(file, packageName + "/" + file.getName());
                } else {
                    createBeanMethod(file, packageName);
                }
                for (Object bean : beanMap.values()) {
                    injectDependencies(bean);
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private void dfs(File files, String packageName) {
        try {
            for(File file : Objects.requireNonNull(files.listFiles())) {
                if(file.isDirectory()) {
                    dfs(file, packageName + "/" + file.getName());
                } else {
                    createBeanMethod(file, packageName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createBeanMethod(File file, String packageName) {
        try {
            if(file.getName().endsWith(".class")) {
                String className = packageName + "/" + file.getName().replace(".class", "");
                String name = className.replace("/", ".");
                Class<?> clazz = Class.forName(name);

                if(clazz.isAnnotationPresent(Component.class)) {
                    Object bean = clazz.getDeclaredConstructor().newInstance();
                    beanMap.put(clazz.getSimpleName(), bean);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public Object getBean (String className){
        return beanMap.get(className);
    }

    //依赖注入，针对于Autowired注解
    //TODO: 优化增添mapper注解
    private void injectDependencies (Object bean) throws IllegalAccessException {
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Autowired.class)) {
                field.setAccessible(true); //开启修改权限
                Class<?> beanClazz = field.getType();
                Object DiBean = beanMap.get(beanClazz.getSimpleName());
                if (Objects.nonNull(DiBean)) {
                    field.set(bean, DiBean);
                } else {
                    throw new RuntimeException("Dependency not found: " + field.getType().getSimpleName());
                }
            }
        }
    }

}
