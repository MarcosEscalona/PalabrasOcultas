package edu.uclm.esi.MarcosEscalona.PalabrasOcultas.Dominio;

public class Manager {

	private static Manager myself;
	
	public static Manager get(){
		if(myself == null)
			myself = new Manager();
		return myself;
	}	
	
	public void login(String nombre, String pwd) throws Exception {
		System.out.println("Nuevo usuario" + nombre);
		Player player = DAOPlayer.select(nombre, pwd);
		player.setJuego(juego);
		if (libres.get(nombre) != null)
			return;
		
		this.libres.put(nombre, jugador);
	}
	
	
}
