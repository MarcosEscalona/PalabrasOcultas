package edu.uclm.esi.MarcosEscalona.PalabrasOcultas.DAO;

import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value="/gamews")
public class WSServer {
	private static Sessions sessions=new Sessions();
	
}
