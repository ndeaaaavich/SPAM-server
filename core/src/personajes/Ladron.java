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
        cuerpo.setUserData(this);
	}
	//--------------------------------------------------------------------------------------------------------------------------------------
	//-------------------------------------------------------------SCENE 2D-----------------------------------------------------------------
	//--------------------------------------------------------------------------------------------------------------------------------------
	public void draw(Batch batch, float parentAlpha){
		super.draw(batch, parentAlpha);
	}
	public void act(float delta){
		if(!Global.terminaJuego) {
			Utiles.hs.enviarMensajeATodos("actualizar%" + (cuerpo.getPosition().x - (cuerpo.getAncho()/2))
												  + "%" + (cuerpo.getPosition().y - (cuerpo.getAlto()/2))
												  + "%L");
			for (int i = 0; i < EstadoMovimiento.values().length; i++) {
				numEstado = (EstadoMovimiento.values()[i].equals(estado))? i : numEstado;
			}
			Utiles.hs.enviarMensajeATodos("actualizar%" + "estado%" + numEstado + "%L");
		}
	}
	public void setDireccion(Vector2 xy) {
		super.setDireccion(xy);
	}
	
	public int[] getApariencia() {
		return apariencia;
	}
}
