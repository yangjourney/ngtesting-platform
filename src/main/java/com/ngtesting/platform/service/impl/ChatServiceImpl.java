package com.ngtesting.platform.service.impl;

import com.ngtesting.platform.model.TstThread;
import com.ngtesting.platform.service.ChatService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatServiceImpl extends BaseServiceImpl implements ChatService {

	@Override
	public List<TstThread> listByEvent(Integer eventId) {
//	    DetachedCriteria dc = DetachedCriteria.forClass(TstThread.class);
//        dc.add(Restrictions.eq("eventId", eventId));
//
//        dc.add(Restrictions.eq("deleted", Boolean.FALSE));
//        dc.add(Restrictions.eq("disabled", Boolean.FALSE));
//        dc.addOrder(Order.asc("id"));
//        Page listByPage = findPage(dc, 0, 10);
//
//        return listByPage.getItems();

		return null;
	}

	@Override
	public TstThread save(Integer eventId, Integer parentId, Integer clientId, String content) {
//		TstThread thread = new TstThread(eventId, clientId, parentId, content);
//
//		saveOrUpdate(thread);
//		return thread;

		return null;
	}

	@Override
	public List<TstThread> enter(Integer eventId, Integer clientId) {
		List<TstThread> list = listByEvent(eventId);

		return list;
	}

}
