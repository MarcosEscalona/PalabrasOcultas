package edu.uclm.esi.MarcosEscalona.PalabrasOcultas.DAO;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.Session;

public class Sessions {
	private ConcurrentHashMap<String, WSSession> sessionsById = new ConcurrentHashMap<>();
	private ConcurrentHashMap<String, WSSession> sessionsByUserName = new ConcurrentHashMap<>();
	
	public WSSession findById(String id) {
		return this.sessionsById.get(id);
	}

	public WSSession find(Session session) {
		return this.findById(session.getId());
	}

	public WSSession findByUserName(String userName) {
		return this.sessionsByUserName.get(userName);
	}

	public void update(Session session, AbstractPlayer player) {
		WSSession wsSession=sessionsById.get(session.getId());
		wsSession.setPlayer(player);
		sessionsByUserName.put(player.getUserName(), wsSession);
	}

	public void add(WSSession wsSession) {
		this.sessionsById.put(wsSession.getId(), wsSession);
		AbstractPlayer player=wsSession.getPlayer();
		if (player!=null)
			this.sessionsByUserName.put(player.getUserName(), wsSession);
	}
	
	public ConcurrentHashMap<String, WSSession> getSessionsByUserName() {
		return sessionsByUserName;
	}

	public WSSession remove(Session session) {
		sessionsById.remove(session.getId());
		Enumeration<String> keys = sessionsByUserName.keys();
		String key;
		WSSession wsSession;
		while (keys.hasMoreElements()) {
			key=keys.nextElement();
			wsSession=sessionsByUserName.get(key);
			if (wsSession.getId().equals(session.getId())) {
				sessionsByUserName.remove(key);
				Manager.get().userLeaves(key);
				return wsSession;
			}
		}
		return null;
	}
}

