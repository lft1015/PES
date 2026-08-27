package com.pes.aspect;

import com.pes.annotation.LogOperation;
import com.pes.entity.SysOperLog;
import com.pes.service.SysOperLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 操作日志切面
 * 通过 AOP 环绕通知拦截带有 @LogOperation 注解的方法，
 * 自动记录操作人、操作描述、类名、方法名、耗时以及执行状态到数据库
 *
 * 功能说明:
 * - 使用 @Around 环绕通知，在方法执行前后进行日志采集
 * - 从 Spring Security 上下文中获取当前登录用户信息
 * - 记录方法执行耗时，单位为毫秒
 * - 方法执行成功时 status=1，失败时 status=0
 * - 无论成功或失败，都会将日志持久化到数据库
 */
@Aspect
@Component
public class LogAspect {

    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    /**
     * 操作日志服务，用于将日志记录持久化到数据库
     */
    private final SysOperLogService sysOperLogService;
    private final HttpServletRequest request;

    public LogAspect(SysOperLogService sysOperLogService, HttpServletRequest request) {
        this.sysOperLogService = sysOperLogService;
        this.request = request;
    }

    /**
     * 环绕通知：拦截所有标注了 @LogOperation 注解的方法
     * 在方法执行前记录开始时间，执行后计算耗时并保存操作日志
     *
     * @param joinPoint 切点对象，包含被拦截方法的相关信息
     * @return 被拦截方法的原始返回值
     * @throws Throwable 被拦截方法抛出异常时，记录失败日志后继续向上抛出
     */
    @Around("@annotation(com.pes.annotation.LogOperation)")
    public Object logOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取方法签名与注解信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        LogOperation logOperation = method.getAnnotation(LogOperation.class);
        String operation = logOperation.value();

        // 从 Spring Security 上下文中获取当前登录用户名，未登录时默认为 anonymous
        String username = "anonymous";
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() != null) {
            username = auth.getName();
        }

        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = signature.getName();

        // 构建操作日志对象，初始状态为成功
        SysOperLog operLog = new SysOperLog();
        operLog.setUsername(username);
        operLog.setOperation(operation);
        operLog.setClassName(className);
        operLog.setMethodName(methodName);
        operLog.setIp(getClientIp());
        operLog.setStatus(1);

        long startTime = System.currentTimeMillis();
        try {
            // 执行原始方法
            Object result = joinPoint.proceed();
            // 记录成功耗时并保存日志
            operLog.setTime(System.currentTimeMillis() - startTime);
            sysOperLogService.save(operLog);
            logger.info("[操作日志] 用户: {}, 操作: {}, 耗时: {}ms", username, operation, operLog.getTime());
            return result;
        } catch (Throwable e) {
            // 记录失败耗时，更新状态为失败并保存日志
            operLog.setTime(System.currentTimeMillis() - startTime);
            operLog.setStatus(0);
            sysOperLogService.save(operLog);
            logger.error("[操作日志] 用户: {}, 操作: {}, 耗时: {}ms, 异常: {}", username, operation, operLog.getTime(), e.getMessage());
            throw e;
        }
    }

    private String getClientIp() {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}