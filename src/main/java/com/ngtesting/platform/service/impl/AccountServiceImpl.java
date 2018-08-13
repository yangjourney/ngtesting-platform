package com.ngtesting.platform.service.impl;

import com.ngtesting.platform.config.Constant;
import com.ngtesting.platform.dao.AccountDao;
import com.ngtesting.platform.dao.AccountVerifyCodeDao;
import com.ngtesting.platform.dao.UserDao;
import com.ngtesting.platform.model.TstUser;
import com.ngtesting.platform.model.TstUserVerifyCode;
import com.ngtesting.platform.service.AccountService;
import com.ngtesting.platform.service.AccountVerifyCodeService;
import com.ngtesting.platform.service.MailService;
import com.ngtesting.platform.service.PropService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private AccountVerifyCodeDao verifyCodeDao;

    @Autowired
    private UserDao userDao;
    @Autowired
    private AccountVerifyCodeService accountVerifyCodeService;
    @Autowired
    private PropService propService;
    @Autowired
    private MailService mailService;

    @Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.DEFAULT,timeout=36000,rollbackFor=Exception.class)
    @Override
    public TstUser register(TstUser user) {
        TstUser existUser = userDao.getByEmail(user.getEmail());
        if (existUser != null) {
            return null;
        }

        accountDao.register(user);
        TstUser po = userDao.get(user.getId());

        if (po != null) {
            accountDao.initUser(user.getId());

            String verifyCode = accountVerifyCodeService.genVerifyCode(po.getId());
            String sys = propService.getSysName();

            Map<String, String> map = new HashMap<String, String>();
            map.put("name", user.getNickname());
            map.put("vcode", verifyCode);

            String url = propService.getUrlLogin();
            if (!url.startsWith("http")) {
                url = Constant.WEB_ROOT + url;
            }
            map.put("url", url);
            mailService.sendTemplateMail("[\"" + sys + "\"]注册成功", "register-success.ftl",
                    user.getEmail(), map);
        }

        return po;
    }

    @Override
    @Transactional
    public TstUser loginWithVerifyCode(String vcode) {
        TstUserVerifyCode code = verifyCodeDao.getByCode(vcode);
        if (code == null) {
            return null;
        }

        TstUser user = userDao.get(code.getRefId());
        if (user == null) {
            return null;
        }

        verifyCodeDao.disableCode(code.getId());

        String newToken = UUID.randomUUID().toString();
        user.setToken(newToken);
        accountDao.loginWithVerifyCode(user);

        return user;
    }

    @Override
    public TstUser login(String email, String password, Boolean rememberMe) {
        TstUser user = userDao.getByEmailAndPassword(email, password);
        if (user == null) {
            return user;
        }

        String newToken = UUID.randomUUID().toString();
        accountDao.login(user.getId(), newToken, new Date());
        user.setToken(newToken);

        return user;
    }

    @Override
    public Boolean logout(String email) {
        Integer matched = accountDao.logout(email);
        return matched > 0;
    }

    @Override
    public Boolean changePassword(Integer userId, String oldPassword, String password) {
        Integer matched = accountDao.changePassword(userId, oldPassword, password);
        return matched > 0;
    }

    @Override
    public String forgotPassword(TstUser user) {
        String verifyCode = accountVerifyCodeService.genVerifyCode(user.getId());

        String sys = propService.getSysName();

        Map<String, String> map = new HashMap<String, String>();
        map.put("name", user.getNickname());
        map.put("vcode", verifyCode);

        String url = propService.getUrlResetPassword();
        if (!url.startsWith("http")) {
            url = Constant.WEB_ROOT + url;
        }

        map.put("url", url);
        mailService.sendTemplateMail("[\"" + sys + "\"]忘记密码", "forgot-password.ftl", user.getEmail(), map);

        return verifyCode;
    }

    @Override
    public Boolean beforResetPassword(String verifyCode) {
        TstUserVerifyCode code = verifyCodeDao.getByCode(verifyCode);

       return code != null;
    }

    @Override
    @Transactional
    public TstUser resetPassword(String verifyCode, String password) {
        TstUserVerifyCode code = verifyCodeDao.getByCode(verifyCode);
        if (code == null) {
            return null;
        }

        TstUser user = userDao.get(code.getRefId());
        if (user == null) {
            return null;
        }

        verifyCodeDao.disableCode(code.getId());

        String newToken = UUID.randomUUID().toString();
        user.setToken(newToken);
        user.setPassword(password);
        accountDao.resetPassword(user);

        return user;
    }

}
