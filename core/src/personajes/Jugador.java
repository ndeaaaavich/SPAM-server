package personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputListener;

import cuerpos.Cuerpo;
import utiles.Utiles;

public abstract class Jugador extends Entidad{


	protected InputListener Inputlistener;
	
	public boolean cambiarSala = false, keyDown;
	public int salaAnterior;
	public float modificadorX = 0, modificadorY = 0, 
				 duracionModif = 10, tiempoTranscurrido;
	
	public Jugador(Cuerpo cuerpo, String sprite) {
		super(cuerpo, sprite);
	}
	//--------------------------------------------------------------------------------------------------------------------------------------
	//-------------------------------------------------------------SCENE 2D-----------------------------------------------------------------
	//--------------------------------------------------------------------------------------------------------------------------------------
	public void act(float delta){
		if((modificadorX != 0 || modificadorY != 0) && (int)(duracionModif % tiempoTranscurrido) == 0) {
			modificadorX = 0;
			modificadorY = 0;
			tiempoTranscurrido = 0;
		}else if(modificadorX != 0 || modificadorY != 0){
			tiempoTranscurrido += Gdx.graphics.getRawDeltaTime();
		}
	}
	public void draw(Batch batch, float parentAlpha){
		Color color = getColor();
	    batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
	}
	
	public void setDireccion(Vector2 xy) {
		cuerpo.setLinearVelocity(xy.x, xy.y);	
	}
	//--------------------------------------------------------------------------------------------------------------------------------------
	//-------------------------------------------------------------SETTERS------------------------------------------------------------------
	//--------------------------------------------------------------------------------------------------------------------------------------
	public void setModificadorX(float modificadorX) {
		if(this.modificadorX != modificadorX) {
			this.modificadorX = modificadorX;
			velocidad += velocidad * modificadorX;
		}
	}
	public void setModificadorY(float modificadorY) {
		if(this.modificadorY != modificadorY) {
			this.modificadorY = modificadorY;
			velocidad += velocidad * modificadorX;
		}
	}
	//--------------------------------------------------------------------------------------------------------------------------------------
	//-------------------------------------------------------------GETTERS------------------------------------------------------------------
	//--------------------------------------------------------------------------------------------------------------------------------------
	public int getSalaAnterior() {
		return salaAnterior;
	}
	public float getFuerzasX() {
		return super.fuerzaX;
	}
	public float getFuerzasY() {
		return super.fuerzaY;
	}
	public float getModificadorX() {
		return modificadorX;
	}
	public float getModificadorY() {
		return modificadorY;
	}	
}
