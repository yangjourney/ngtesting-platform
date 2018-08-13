package com.ngtesting.platform.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ngtesting.platform.config.Constant;
import com.ngtesting.platform.model.TstCustomField;
import com.ngtesting.platform.model.TstCustomFieldProjectRelation;
import com.ngtesting.platform.model.TstUser;
import com.ngtesting.platform.service.CustomFieldProjectRelationService;
import com.ngtesting.platform.service.CustomFieldService;
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
import java.util.UUID;


@Controller
@RequestMapping(Constant.API_PATH_CLIENT + "custom_field/")
public class CustomFieldAction extends BaseAction {
	@Autowired
    CustomFieldService customFieldService;
	@Autowired
	CustomFieldProjectRelationService  customFieldProjectRelationService;

	@RequestMapping(value = "list", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> list(HttpServletRequest request, @RequestBody JSONObject json) {
		Map<String, Object> ret = new HashMap<String, Object>();

		TstUser userVo = (TstUser) request.getSession().getAttribute(Constant.HTTP_SESSION_USER_KEY);
		Integer orgId = userVo.getDefaultOrgId();

		List<TstCustomField> vos = customFieldService.list(orgId);

		ret.put("code", Constant.RespCode.SUCCESS.getCode());
		ret.put("data", vos);
		return ret;
	}

	@RequestMapping(value = "get", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> get(HttpServletRequest request, @RequestBody JSONObject json) {
		Map<String, Object> ret = new HashMap<String, Object>();

		TstUser userVo = (TstUser) request.getSession().getAttribute(Constant.HTTP_SESSION_USER_KEY);
		Integer orgId = userVo.getDefaultOrgId();

		Integer customFieldId = json.getInteger("id");

		TstCustomField vo = null;
		if (customFieldId == null) {
			vo = new TstCustomField();
			vo.setMyColumn(customFieldService.getLastUnusedColumn(orgId));
			vo.setCode(UUID.randomUUID().toString());
		} else {
			vo = customFieldService.get(customFieldId);
		}

		if (vo.getMyColumn() == null) {
            ret.put("code", Constant.RespCode.BIZ_FAIL.getCode());
            ret.put("msg", "自定义字段不能超过20个");
        }

		List<String> applyToList = customFieldService.listApplyTo();
		List<String> typeList = customFieldService.listType();
		List<String> formatList = customFieldService.listFormat();
		List<TstCustomFieldProjectRelation> relations = customFieldProjectRelationService.listRelationsByField(orgId, customFieldId);

        ret.put("data", vo);
        ret.put("applyToList", applyToList);
        ret.put("typeList", typeList);
        ret.put("formatList", formatList);
        ret.put("relations", relations);

		ret.put("code", Constant.RespCode.SUCCESS.getCode());
		return ret;
	}

	@RequestMapping(value = "save", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> save(HttpServletRequest request, @RequestBody JSONObject json) {
		Map<String, Object> ret = new HashMap<String, Object>();

		TstUser userVo = (TstUser) request.getSession().getAttribute(Constant.HTTP_SESSION_USER_KEY);
		Integer orgId = userVo.getDefaultOrgId();

		TstCustomField customField = JSON.parseObject(JSON.toJSONString(json.get("model")), TstCustomField.class);
		List<TstCustomFieldProjectRelation> relations = (List<TstCustomFieldProjectRelation>) json.get("relations");

		TstCustomField po = customFieldService.save(customField, orgId);
		boolean success = customFieldProjectRelationService.saveRelationsByField(orgId, po.getId(), relations);

		ret.put("code", Constant.RespCode.SUCCESS.getCode());
		return ret;
	}

	@RequestMapping(value = "delete", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> delete(HttpServletRequest request, @RequestBody JSONObject json) {
		Map<String, Object> ret = new HashMap<String, Object>();

		Integer id = json.getInteger("id");

		boolean success = customFieldService.delete(id);

		ret.put("code", Constant.RespCode.SUCCESS.getCode());
		return ret;
	}

	@RequestMapping(value = "changeOrder", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> changeOrder(HttpServletRequest request, @RequestBody JSONObject json) {
		Map<String, Object> ret = new HashMap<String, Object>();

		TstUser userVo = (TstUser) request.getSession().getAttribute(Constant.HTTP_SESSION_USER_KEY);
		Integer orgId = userVo.getDefaultOrgId();
		Integer id = json.getInteger("id");
		String act = json.getString("act");

		boolean success = customFieldService.changeOrderPers(id, act, orgId);

		List<TstCustomField> vos = customFieldService.list(orgId);

        ret.put("data", vos);
		ret.put("code", Constant.RespCode.SUCCESS.getCode());

		return ret;
	}

}
