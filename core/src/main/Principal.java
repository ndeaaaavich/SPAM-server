package main;

import com.badlogic.gdx.Game;

import pantallas.PantallaMenu;
import utiles.Utiles;

public class Principal extends Game {
//	PantallaRonda1 ronda1 = new PantallaRonda1();
//	PantallaRonda2 ronda2 = new PantallaRonda2();
	
	@Override
	public void create () {
		Utiles.principal = this;
		this.setScreen(new PantallaMenu());
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
	}
}
