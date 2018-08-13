package com.ngtesting.platform.service.impl;

import com.github.pagehelper.PageHelper;
import com.ngtesting.platform.config.Constant;
import com.ngtesting.platform.dao.OrgDao;
import com.ngtesting.platform.dao.OrgUserRelationDao;
import com.ngtesting.platform.dao.ProjectDao;
import com.ngtesting.platform.dao.UserDao;
import com.ngtesting.platform.model.*;
import com.ngtesting.platform.service.*;
import com.ngtesting.platform.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(value = "userService")
public class UserServiceImpl implements UserService {
    @Autowired
    private PropService propService;
    @Autowired
    private MailService mailService;

    @Autowired
    private OrgDao orgDao;
    @Autowired
    private OrgUserRelationDao orgUserRelationDao;
    @Autowired
    private ProjectDao projectDao;
    @Autowired
    private UserDao userDao;

    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountVerifyCodeService accountVerifyCodeService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    OrgGroupUserRelationService orgGroupUserRelationService;
    @Autowired
    OrgRoleUserRelationService orgRoleUserService;

    @Override
    public List<TstUser> list(Integer orgId, String keywords, Boolean disabled, int pageNum, int pageSize) {
        List<TstUser> users = userDao.query(orgId, keywords, disabled);

        return users;
    }

    @Override
    public List<TstUser> listAllOrgUsers(Integer orgId) {
        List<TstUser> ls = userDao.query(orgId, null, false);

        return ls;
    }

    @Override
    public List<TstUser> getProjectUsers(Integer orgId, Integer projectId) {
        List<TstUser> ls = userDao.getProjectUsers(projectId);

        return ls;
    }

    @Override
    public TstUser get(Integer id) {
        TstUser user = userDao.get(id);
        return user;
    }

    @Override
    public TstUser getByToken(String token) {
        TstUser user = userDao.getByToken(token);
        return user;
    }

    @Override
    public TstUser getByPhone(String phone) {
        TstUser user = userDao.getByPhone(phone);
        return user;
    }

    @Override
    public TstUser getByEmail(String email) {
        TstUser user = userDao.getByEmail(email);
        return user;
    }

    @Override
    @Transactional
    public TstUser invitePers(TstUser user, TstUser vo, List<TstOrgGroupUserRelation> relations) {
        Integer orgId = user.getDefaultOrgId();
        Integer prjId = user.getDefaultPrjId();
        String orgName = user.getDefaultOrgName();
        String prjName = user.getDefaultPrjName();

        TstUser existUser  = getByEmail(vo.getEmail());
        boolean isNew;
        if (existUser != null) {
            isNew = false;
            vo = existUser;
        } else {
            isNew = true;
            vo.setDefaultOrgId(orgId);
            vo.setPassword(StringUtil.RandomString(6));
            vo.setAvatar("upload/sample/user/avatar.png");

            vo.setDefaultOrgId(orgId);
            vo.setDefaultPrjId(prjId);
            vo.setDefaultOrgName(orgName);
            vo.setDefaultPrjName(prjName);

            userDao.save(vo);
        }

        if (isNew || orgUserRelationDao.userInOrg(vo.getId(), orgId) == 0) { // 不在组织里
            orgUserRelationDao.addUserToOrg(vo.getId(), orgId);
            projectService.viewPers(prjId, vo);

            orgGroupUserRelationService.saveRelationsForUser(orgId, vo.getId(), relations);

            // 发送邮件
            String sys = propService.getSysName();
            Map<String, String> map = new HashMap<String, String>();
            map.put("user", user.getNickname() + "(" + user.getEmail() + ")");
            map.put("name", vo.getNickname());
            map.put("sys", sys);

            String url;
            if (isNew) {
                String verifyCode = accountVerifyCodeService.genVerifyCode(vo.getId());

                url = propService.getUrlResetPassword();
                if (!url.startsWith("http")) {
                    url = Constant.WEB_ROOT + url;
                }
                url += "/" + verifyCode;
            } else {
                url = propService.getUrlLogin();
                if (!url.startsWith("http")) {
                    url = Constant.WEB_ROOT + url;
                }
            }
            map.put("url", url);
            mailService.sendTemplateMail("来自[" + sys + "]的邀请", "invite-user.ftl",
                    vo.getEmail(), map);
            return vo;
        } else {
            return null;
        }
    }

    @Override
    public void update(TstUser record) {
        userDao.update(record);
    }

    @Override
    public void setDefaultOrg(TstUser user, Integer orgId) {
        TstOrg org = orgDao.get(orgId);
        userDao.setDefaultOrg(user.getId(), orgId, org.getName());
        user.setDefaultOrgId(orgId);
        user.setDefaultOrgName(org.getName());

        List<TstProjectAccessHistory> recentProjects = projectService.listRecentProject(orgId, user.getId());
        if (recentProjects.size() > 0) {
            TstProjectAccessHistory his = recentProjects.get(0);
            setDefaultPrj(user, his.getPrjId());

        } else {
            setDefaultPrj(user, null);
        }
    }

    @Override
    public void setDefaultPrj(TstUser user, Integer prjId) {
        if (prjId != null) {
            TstProject prj = projectDao.get(prjId);
            userDao.setDefaultPrj(user.getId(), prjId, prj.getName());

            user.setDefaultPrjId(prjId);
            user.setDefaultPrjName(prj.getName());
        } else {
            userDao.setDefaultPrj(user.getId(), null, null);

            user.setDefaultPrjId(null);
            user.setDefaultPrjName(null);
        }
    }

    @Override
    public List<TstUser> search(Integer orgId, String keywords, String exceptIds) {
        PageHelper.startPage(0, 20);
        List<TstUser> users = userDao.search(orgId, keywords, exceptIds);

        return users;
    }

    @Override
    public TstUser setLeftSizePers(TstUser user, Integer left, String prop) {
        if ("case".equals(prop)) {
            user.setLeftSizeCase(left);
        } else if ("issue".equals(prop)) {
            user.setLeftSizeIssue(left);
        }

        userDao.setLeftSize(user);
        return user;
    }

}
