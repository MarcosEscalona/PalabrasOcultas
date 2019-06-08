package edu.uclm.esi.MarcosEscalona.PalabrasOcultas.DAO;

import javax.websocket.Session;

public class WSSession {
	private Session session;

	public WSSession(Session session) {
		this.session=session;
	}

	public Session getSession() {
		return this.session;
	}
	
	public String getId() {
		return this.session.getId();
	}

}

