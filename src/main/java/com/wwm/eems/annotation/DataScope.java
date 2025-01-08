package com.wwm.eems.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScope {
    /**
     * 部门表的别名
     */
    String deptAlias() default "d";
    
    /**
     * 用户表的别名
     */
    String userAlias() default "u";
} 