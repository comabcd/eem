package com.wwm.eems.aspect;


import com.wwm.eems.annotation.DataScope;
import com.wwm.eems.model.User;
import com.wwm.eems.model.request.EmployeeQueryRequest;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DataScopeAspect {
    
    @Before("@annotation(dataScope)")
    public void doBefore(JoinPoint point, DataScope dataScope) {
        handleDataScope(point, dataScope);
    }
    
    private void handleDataScope(JoinPoint point, DataScope dataScope) {
        // 获取当前用户
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        
        StringBuilder sqlString = new StringBuilder();
        
        // 如果是系统管理员，不进行数据过滤
        if (user.getRole() == 1) {
            return;
        }
        
        String deptAlias = dataScope.deptAlias();
        
        // 部门管理员，只能查看本部门及下级部门数据
        if (user.getRole() == 2) {
            sqlString.append(String.format(
                " AND (%s.dept_id IN (SELECT id FROM t_department WHERE FIND_IN_SET(id, getDeptChildList(%d)))" +
                " OR %s.dept_id = %d)", 
                deptAlias, user.getDeptId(), deptAlias, user.getDeptId()));
        }
        // 人力资源管理员，可以查看所有部门数据
        else if (user.getRole() == 3) {
            return;
        }
        // 普通员工，只能查看本人数据
        else {
            sqlString.append(String.format(" AND %s.user_id = %d", dataScope.userAlias(), user.getId()));
        }
        
        // 获取参数并设置数据范围
        if (point.getArgs().length > 0) {
            Object arg = point.getArgs()[0];
            if (arg instanceof EmployeeQueryRequest) {
                EmployeeQueryRequest request = (EmployeeQueryRequest) arg;
//                request.setDataScope(sqlString.toString());
            }
        }
    }
} 