package personajes;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

import utiles.Global;
import utiles.Utiles;

import cuerpos.Cuerpo;

public class Ladron extends Jugador{
	
	private int[] apariencia = new int[3];
	
	public Ladron(Cuerpo cuerpo, String sprite, int[] apariencia) {
		super(cuerpo, sprite);
		this.apariencia = apariencia;
	}
	//--------------------------------------------------------------------------------------------------------------------------------------
	//-------------------------------------------------------------SCENE 2D-----------------------------------------------------------------
	//--------------------------------------------------------------------------------------------------------------------------------------
	public void draw(Batch batch, float parentAlpha){
		super.draw(batch, parentAlpha);
	}
	public void act(float delta){
		Utiles.hs.enviarMensajeATodos("actualizar%x%" + cuerpo.getPosition().x + "%L");
		Utiles.hs.enviarMensajeATodos("actualizar%y%" + cuerpo.getPosition().y + "%L");
	}
	public void setDireccion(Vector2 xy) {
		super.setDireccion(xy);
	}
}
