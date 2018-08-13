package com.ngtesting.platform.service;

import com.alibaba.fastjson.JSONObject;
import com.ngtesting.platform.model.TstUser;
import com.ngtesting.platform.model.TstVer;

import java.util.List;

public interface TestVerService extends BaseService {
	List<TstVer> list(Integer projectId, String keywords, Boolean disabled);
	TstVer getById(Integer caseId);
	TstVer save(JSONObject json, TstUser optUser);
	void delete(Integer vo, Integer userId);

	boolean changeOrder(Integer id, String act, Integer orgId);
}
