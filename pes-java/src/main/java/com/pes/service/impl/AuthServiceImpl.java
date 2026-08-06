package com.pes.service.impl;

import com.pes.dto.request.LoginReq;
import com.pes.dto.request.RegisterReq;
import com.pes.dto.response.CaptchaResp;
import com.pes.dto.response.LoginResp;
import com.pes.entity.SysLoginLog;
import com.pes.entity.SysMenu;
import com.pes.entity.SysRole;
import com.pes.entity.SysRoleMenu;
import com.pes.entity.SysUser;
import com.pes.entity.SysUserRole;
import com.pes.exception.BusinessException;
import com.pes.exception.ErrorCode;
import com.pes.mapper.SysMenuMapper;
import com.pes.mapper.SysRoleMapper;
import com.pes.mapper.SysRoleMenuMapper;
import com.pes.mapper.SysUserMapper;
import com.pes.mapper.SysUserRoleMapper;
import com.pes.security.utils.JwtUtils;
import com.pes.service.AuthService;
import com.pes.service.SysLoginLogService;
import com.pes.utils.CaptchaStore;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pes.utils.CaptchaUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 认证服务实现
 * 提供登录、登出、验证码生成、注册等核心认证功能
 */
@Service
public class AuthServiceImpl implements AuthService {

    /** Spring Security 认证管理器 */
    private final AuthenticationManager authenticationManager;
    /** JWT 工具类 */
    private final JwtUtils jwtUtils;
    /** 用户 Mapper */
    private final SysUserMapper sysUserMapper;
    /** 用户-角色关联 Mapper */
    private final SysUserRoleMapper sysUserRoleMapper;
    /** 角色 Mapper */
    private final SysRoleMapper sysRoleMapper;
    /** 角色-菜单关联 Mapper */
    private final SysRoleMenuMapper sysRoleMenuMapper;
    /** 菜单 Mapper */
    private final SysMenuMapper sysMenuMapper;
    /** 验证码生成工具 */
    private final CaptchaUtils captchaUtils;
    /** 验证码存储器（Redis） */
    private final CaptchaStore captchaStore;
    /** 密码编码器 */
    private final PasswordEncoder passwordEncoder;
    /** 登录日志服务 */
    private final SysLoginLogService sysLoginLogService;

    /** JWT 过期时间（秒），从配置文件中读取 */
    @Value("${jwt.expire}")
    private Long expire;

    /**
     * 构造器注入所有依赖
     */
    public AuthServiceImpl(AuthenticationManager authenticationManager, JwtUtils jwtUtils,
                          SysUserMapper sysUserMapper, SysUserRoleMapper sysUserRoleMapper,
                          SysRoleMapper sysRoleMapper, SysRoleMenuMapper sysRoleMenuMapper,
                          SysMenuMapper sysMenuMapper, CaptchaUtils captchaUtils,
                          CaptchaStore captchaStore, PasswordEncoder passwordEncoder,
                          SysLoginLogService sysLoginLogService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.sysUserMapper = sysUserMapper;
        this.sysUserRoleMapper = sysUserRoleMapper;
        this.sysRoleMapper = sysRoleMapper;
        this.sysRoleMenuMapper = sysRoleMenuMapper;
        this.sysMenuMapper = sysMenuMapper;
        this.captchaUtils = captchaUtils;
        this.captchaStore = captchaStore;
        this.passwordEncoder = passwordEncoder;
        this.sysLoginLogService = sysLoginLogService;
    }

    /**
     * 用户登录
     * 流程：校验验证码 → Spring Security 认证 → 生成 JWT → 查询角色和权限 → 记录登录日志 → 返回 Token 和用户信息
     */
    @Override
    public LoginResp login(LoginReq req) {
        String storedCaptcha = captchaStore.getAndRemove("captcha:" + req.getCaptchaKey());
        if (storedCaptcha == null || !storedCaptcha.equalsIgnoreCase(req.getCaptcha())) {
            throw new BusinessException(ErrorCode.CAPTCHA_ERROR);
        }

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
        } catch (Exception e) {
            SysLoginLog failLog = new SysLoginLog();
            failLog.setUsername(req.getUsername());
            failLog.setStatus(0);
            sysLoginLogService.save(failLog);
            throw e;
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Map<String, Object> claims = new HashMap<>();
        String token = jwtUtils.generateToken(req.getUsername(), claims);

        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, req.getUsername()));

        List<String> roleCodes = new ArrayList<>();
        List<String> roleNames = new ArrayList<>();
        List<String> permissions = new ArrayList<>();

        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, user.getId()));
        for (SysUserRole userRole : userRoles) {
            SysRole role = sysRoleMapper.selectById(userRole.getRoleId());
            if (role != null) {
                roleCodes.add(role.getCode());
                roleNames.add(role.getName());

                List<SysRoleMenu> roleMenus = sysRoleMenuMapper.selectList(
                        new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, role.getId()));
                List<Long> menuIds = roleMenus.stream()
                        .map(SysRoleMenu::getMenuId)
                        .collect(Collectors.toList());
                if (!menuIds.isEmpty()) {
                    List<SysMenu> menus = sysMenuMapper.selectBatchIds(menuIds);
                    for (SysMenu menu : menus) {
                        if (menu.getPermission() != null && !menu.getPermission().isEmpty()) {
                            permissions.add(menu.getPermission());
                        }
                    }
                }
            }
        }

        SysLoginLog loginLog = new SysLoginLog();
        loginLog.setUsername(user.getUsername());
        loginLog.setStatus(1);
        sysLoginLogService.save(loginLog);

        return LoginResp.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(expire)
                .username(user.getUsername())
                .nickname(user.getNickname())
                .roles(roleCodes)
                .roleNames(roleNames)
                .permissions(permissions)
                .build();
    }

    /**
     * 用户登出：清除 SecurityContext 中的认证信息
     */
    @Override
    public void logout() {
        SecurityContextHolder.clearContext();
    }

    /**
     * 用户注册
     * 校验验证码 → 检查用户名唯一性 → 加密密码 → 创建用户 → 默认分配普通用户角色（roleId=2）
     */
    @Override
    public void register(RegisterReq req) {
        if (req.getCaptchaKey() != null && !req.getCaptchaKey().isEmpty()) {
            String storedCaptcha = captchaStore.getAndRemove("captcha:" + req.getCaptchaKey());
            if (storedCaptcha == null || !storedCaptcha.equalsIgnoreCase(req.getCaptcha())) {
                throw new BusinessException(ErrorCode.CAPTCHA_ERROR);
            }
        }

        SysUser existingUser = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, req.getUsername()));
        if (existingUser != null) {
            throw new BusinessException(ErrorCode.USERNAME_EXISTS);
        }

        SysUser user = new SysUser();
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setNickname(req.getNickname() != null ? req.getNickname() : req.getUsername());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setStatus(1);
        sysUserMapper.insert(user);

        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(2L);
        sysUserRoleMapper.insert(userRole);
    }

    /** 验证码图片宽度 */
    private static final int CAPTCHA_WIDTH = 130;
    /** 验证码图片高度 */
    private static final int CAPTCHA_HEIGHT = 48;
    /** 验证码字符颜色池 */
    private static final Color[] COLORS = {
        new Color(0, 121, 190), new Color(220, 80, 80), new Color(0, 160, 80),
        new Color(230, 150, 0), new Color(130, 60, 180), new Color(0, 140, 160)
    };

    /**
     * 生成图形验证码
     * 使用 Java Graphics2D 绘制含干扰线/干扰点的验证码图片，返回 Base64 编码的图片和验证码 Key
     */
    @Override
    public CaptchaResp generateCaptcha() {
        String captchaCode = captchaUtils.generateCaptcha(4);
        String captchaKey = UUID.randomUUID().toString();

        // 用 Java Graphics2D 生成验证码图片
        BufferedImage image = new BufferedImage(CAPTCHA_WIDTH, CAPTCHA_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        try {
            // 背景
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setColor(new Color(245, 248, 250));
            g.fillRect(0, 0, CAPTCHA_WIDTH, CAPTCHA_HEIGHT);

            Random rand = new Random();

            // 干扰线
            g.setStroke(new BasicStroke(1.5f));
            for (int i = 0; i < 3; i++) {
                g.setColor(new Color(180 + rand.nextInt(50), 180 + rand.nextInt(50), 190 + rand.nextInt(40)));
                int x1 = rand.nextInt(CAPTCHA_WIDTH / 3);
                int y1 = rand.nextInt(CAPTCHA_HEIGHT);
                int x2 = CAPTCHA_WIDTH / 3 + rand.nextInt(CAPTCHA_WIDTH * 2 / 3);
                int y2 = rand.nextInt(CAPTCHA_HEIGHT);
                g.drawLine(x1, y1, x2, y2);
            }

            // 干扰点
            for (int i = 0; i < 30; i++) {
                g.setColor(new Color(160 + rand.nextInt(80), 170 + rand.nextInt(70), 200, 120));
                g.fillOval(rand.nextInt(CAPTCHA_WIDTH), rand.nextInt(CAPTCHA_HEIGHT), 2, 2);
            }

            // 绘制文字
            Font[] fonts = {
                new Font("Arial", Font.BOLD, 28),
                new Font("Courier New", Font.BOLD, 28),
                new Font("Verdana", Font.BOLD, 26)
            };
            for (int i = 0; i < captchaCode.length(); i++) {
                g.setFont(fonts[rand.nextInt(fonts.length)]);
                g.setColor(COLORS[rand.nextInt(COLORS.length)]);
                // 每个字符轻微旋转
                double angle = (rand.nextDouble() - 0.5) * 0.4;
                g.rotate(angle, 20 + i * 28, 30);
                g.drawString(String.valueOf(captchaCode.charAt(i)), 12 + i * 28, 32);
                g.rotate(-angle, 20 + i * 28, 30);
            }

            // 输出为 base64
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                ImageIO.write(image, "png", baos);
                String base64 = "data:image/png;base64," + Base64.getEncoder().encodeToString(baos.toByteArray());

                // 验证码文本存入 CaptchaStore，5 分钟过期（小写，忽略大小写）
                captchaStore.put("captcha:" + captchaKey, captchaCode.toLowerCase(), 5, TimeUnit.MINUTES);

                return CaptchaResp.builder()
                        .key(captchaKey)
                        .image(base64)
                        .build();
            }
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR);
        } finally {
            g.dispose();
        }
    }
}