package personajes;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

import cuerpos.Cuerpo;
import utiles.Global;
import utiles.Utiles;

public class Guardia extends Jugador {
	
	public Guardia(Cuerpo cuerpo, String sprite) {
		super(cuerpo, sprite);
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
		Utiles.hs.enviarMensajeATodos("actualizar%x%" + cuerpo.getPosition().x + "%G");
		Utiles.hs.enviarMensajeATodos("actualizar%y%" + cuerpo.getPosition().y + "%G");
	}
	public void setDireccion(Vector2 xy) {
		super.setDireccion(xy);
	}
}
