package com.ngtesting.platform.service;

import com.ngtesting.platform.model.TstUser;

public interface CaseAttachmentService extends BaseService {

    Boolean save(Integer caseId, String name, String path, TstUser user);
    Boolean delete(Integer id, TstUser user);

}
