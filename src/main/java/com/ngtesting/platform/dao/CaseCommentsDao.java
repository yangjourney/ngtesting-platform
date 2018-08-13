package com.ngtesting.platform.dao;

import com.ngtesting.platform.model.TstCaseComments;
import org.apache.ibatis.annotations.Param;

public interface CaseCommentsDao {

    void update(TstCaseComments vo);

    void save(TstCaseComments vo);

    boolean delete(@Param("id") Integer id, @Param("userId") Integer userId);
}
