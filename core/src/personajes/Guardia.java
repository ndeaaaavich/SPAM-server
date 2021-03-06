package personajes;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

import cuerpos.Cuerpo;
import utiles.Global;
import utiles.Utiles;

public class Guardia extends Jugador {
	
	public Guardia(Cuerpo cuerpo, String sprite) {
		super(cuerpo, sprite);
		cuerpo.setUserData(this);
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------------
	//-------------------------------------------------------------SCENE 2D-----------------------------------------------------------------
	//--------------------------------------------------------------------------------------------------------------------------------------	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
	}
	@Override
	public void act(float delta) {
		if(!Global.terminaJuego) {
			Utiles.hs.enviarMensajeATodos("actualizar%" + (cuerpo.getPosition().x - (cuerpo.getAncho()/2))
												  + "%" + (cuerpo.getPosition().y - (cuerpo.getAlto()/2))
												  + "%G");
			for (int i = 0; i < EstadoMovimiento.values().length; i++) {
				numEstado = (EstadoMovimiento.values()[i].equals(estado))? i : numEstado;
			}
			Utiles.hs.enviarMensajeATodos("actualizar%" + "estado%" + numEstado + "%G");
		}
	}
	public void setDireccion(Vector2 xy) {
		super.setDireccion(xy);
	}
}
