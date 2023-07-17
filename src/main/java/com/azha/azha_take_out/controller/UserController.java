package com.azha.azha_take_out.controller;

import com.azha.azha_take_out.Utils.ValidateCodeUtils;
import com.azha.azha_take_out.common.R;
import com.azha.azha_take_out.entity.User;
import com.azha.azha_take_out.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jacoco.agent.rt.internal_f3994fa.core.data.ISessionInfoVisitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    /*
    发送手机验证码
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取手机号
        String phone=user.getPhone();
        if(StringUtils.isNotEmpty(phone)){
            //生成四位随机数
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            //调用阿里云短信服务,这里我没申请，就不管了，直接从log里面读
            log.info("验证码是：{}",code);
            //把验证码存到session里面
            session.setAttribute(phone,code);
            return R.success("发送成功");
        }
        return R.error("发送失败");
    }
    /*
    登陆
    */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map userMap, HttpSession session){
        log.info(userMap.toString());
        //获取手机号和验证码以及Session中保存的验证码
        String phone=userMap.get("phone").toString();
        String code=userMap.get("code").toString();
        Object codeInSession=session.getAttribute(phone);
        //把提交上来的验证码和session里面进行对比
        log.info("code:{},codeInSession:{}",code,codeInSession);
        if(codeInSession !=null && codeInSession.equals(code)){
            //对比成功后，判断是不是新用户

            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(queryWrapper);
            if(user == null){
                //是的话自动注册
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            //登陆校验的是用户ID，所以要放一份到session里面
            session.setAttribute("user",user.getId());
            return R.success(user);
        }
        return R.error("登陆失败");
    }
}
