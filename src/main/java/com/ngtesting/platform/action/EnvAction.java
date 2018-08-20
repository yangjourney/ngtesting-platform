package com.ngtesting.platform.action;

import com.alibaba.fastjson.JSONObject;
import com.ngtesting.platform.bean.websocket.WsFacade;
import com.ngtesting.platform.config.Constant;
import com.ngtesting.platform.model.TstUser;
import com.ngtesting.platform.model.TstEnv;
import com.ngtesting.platform.service.TestEnvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping(Constant.API_PATH_CLIENT + "env/")
public class EnvAction extends BaseAction {
	@Autowired
	private WsFacade optFacade;

	@Autowired
	TestEnvService envService;

    @RequestMapping(value = "list", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> list(HttpServletRequest request, @RequestBody JSONObject json) {
        Map<String, Object> ret = new HashMap<String, Object>();
        TstUser user = (TstUser) request.getSession().getAttribute(Constant.HTTP_SESSION_USER_PROFILE);
        Integer projectId = user.getDefaultPrjId();

        String keywords = json.getString("keywords");
        Boolean disabled = json.getBoolean("disabled");

        List<TstEnv> ls = envService.list(projectId, keywords, disabled);

        ret.put("data", ls);
        ret.put("code", Constant.RespCode.SUCCESS.getCode());
        return ret;
    }

    @RequestMapping(value = "get", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> get(HttpServletRequest request, @RequestBody JSONObject json) {
        Map<String, Object> ret = new HashMap<String, Object>();
        TstUser user = (TstUser) request.getSession().getAttribute(Constant.HTTP_SESSION_USER_PROFILE);
        Integer projectId = user.getDefaultPrjId();

        Integer id = json.getInteger("id");

        TstEnv vo = envService.getById(id, projectId);

        ret.put("data", vo);
        ret.put("code", Constant.RespCode.SUCCESS.getCode());
        return ret;
    }

    @RequestMapping(value = "save", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> save(HttpServletRequest request, @RequestBody JSONObject json) {
        Map<String, Object> ret = new HashMap<String, Object>();
        TstUser user = (TstUser) request.getSession().getAttribute(Constant.HTTP_SESSION_USER_PROFILE);

        TstEnv po = envService.save(json, user);
        if(po == null) {
            return authFail();
        }

        ret.put("data", po);
        ret.put("code", Constant.RespCode.SUCCESS.getCode());
        return ret;
    }

    @RequestMapping(value = "delete", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> delete(HttpServletRequest request, @RequestBody JSONObject json) {
        Map<String, Object> ret = new HashMap<String, Object>();
        TstUser user = (TstUser) request.getSession().getAttribute(Constant.HTTP_SESSION_USER_PROFILE);
        Integer projectId = user.getDefaultPrjId();

        Integer id = json.getInteger("id");

        Boolean result = envService.delete(id, projectId);
        if (!result) {
            return authFail();
        }

        ret.put("code", Constant.RespCode.SUCCESS.getCode());
        return ret;
    }

    @RequestMapping(value = "changeOrder", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> changeOrder(HttpServletRequest request, @RequestBody JSONObject json) {
        Map<String, Object> ret = new HashMap<String, Object>();
        TstUser user = (TstUser) request.getSession().getAttribute(Constant.HTTP_SESSION_USER_PROFILE);
        Integer projectId = user.getDefaultPrjId();

        Integer id = json.getInteger("id");
        String act = json.getString("act");

        Boolean result = envService.changeOrder(id, act, projectId);
        if (!result) {
            return authFail();
        }

        List<TstEnv> vos = envService.list(projectId, null, false);

        ret.put("data", vos);
        ret.put("code", Constant.RespCode.SUCCESS.getCode());

        return ret;
    }

}
