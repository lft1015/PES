package com.pes.aspect;

import com.pes.annotation.RequirePermission;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 权限校验切面
 * 通过 AOP 环绕通知拦截带有 @RequirePermission 注解的方法，
 * 在方法执行前校验当前用户是否具备所需的权限标识
 *
 * 功能说明:
 * - 使用 @Around 环绕通知，在方法执行前进行权限校验
 * - 从 Spring Security 上下文中获取当前用户的认证信息与权限集合
 * - 权限校验通过后放行执行原方法，不通过则抛出 AccessDeniedException
 * - 若注解的 value 为空字符串，则跳过权限校验直接放行
 */
@Aspect
@Component
public class PermissionAspect {

    /**
     * 环绕通知：拦截所有标注了 @RequirePermission 注解的方法
     * 从注解中获取所需权限标识，与当前用户拥有的权限集合进行比对，
     * 匹配成功则放行，否则抛出访问拒绝异常
     *
     * @param joinPoint 切点对象，包含被拦截方法的相关信息
     * @return 被拦截方法的原始返回值
     * @throws Throwable 权限不足或未登录时抛出 AccessDeniedException
     */
    @Around("@annotation(com.pes.annotation.RequirePermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取方法签名与 @RequirePermission 注解中的权限标识
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequirePermission requirePermission = method.getAnnotation(RequirePermission.class);
        String requiredPerm = requirePermission.value();

        // 若未配置权限标识，则跳过校验直接放行
        if (requiredPerm.isEmpty()) {
            return joinPoint.proceed();
        }

        // 获取当前用户的认证信息，未登录则抛出异常
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new AccessDeniedException("未登录");
        }

        // 提取当前用户拥有的所有权限标识
        Object principal = auth.getPrincipal();
        Set<String> permissions = auth.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .collect(Collectors.toSet());

        // 比对所需权限是否在用户权限集合中，不存在则抛出异常
        if (!permissions.contains(requiredPerm)) {
            throw new AccessDeniedException("权限不足");
        }

        // 权限校验通过，执行原方法
        return joinPoint.proceed();
    }
}