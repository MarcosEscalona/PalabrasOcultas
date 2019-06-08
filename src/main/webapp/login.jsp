<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="org.json.*, edu.uclm.esi.MarcosEscalona.PalabrasOcultas.Dominio.Manager" %>

<%
String p = request.getParameter("p");

JSONObject resultado = new JSONObject();

try {
	if(!request.getMethod().equals("POST"))
		throw new Exception("El método no está soportado");
	
	JSONObject jso=new JSONObject(p);
	
	if(!jso.getString("type").equals("Login")) {
		resultado.put("type", "error");
		resultado.put("message", "Mensaje no esperado");
		
		
	} else {
		String userName = jso.getString("userName");
		String pwd=jso.getString("pwd");
		
		Player player = Manager.get().login(userName, pwd);
		session.setAttribute("player", player);
		
		JSONObject jsoRespuesta = JSONer.toJSON(player);
		resultado.put("resultado", jsoRespuesta);
		resultado.put("type", "OK");
	}
	
}
	catch (Exception e) {
		resultado.put("type", "error");
		resultado.put("message", e.getMessage());
	}

%>

<%= resultado.toString() %>
