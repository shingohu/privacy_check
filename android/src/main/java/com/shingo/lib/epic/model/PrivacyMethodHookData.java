package com.shingo.lib.epic.model;

import java.util.List;

public class PrivacyMethodHookData {

    ///hook的包名 必传
    public String className;
    ///hook的方法  如果是构造非必传,如果不是构造,必传
    public String methodName;
    ///是否hook构造函数
    public boolean isHookConstructor ;

    ///hook信息 可选
    public String message;

    ///hook的方法参数 可选
    public List<Class> parameterTypes;
    ///过滤信息(比如某个方法 有很多地方调用,可以用这个来过滤下)可选
    public String filter;


    public PrivacyMethodHookData(String className, String methodName,boolean isHookConstructor, List<Class> parameterTypes, String message, String filter) {
        this.className = className;
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
        this.message = message;
        this.filter = filter;
        this.isHookConstructor = isHookConstructor;
    }

}
