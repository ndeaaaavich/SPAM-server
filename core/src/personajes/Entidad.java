package personajes;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import cuerpos.Cuerpo;

public abstract class Entidad extends Actor{
	
	protected Cuerpo cuerpo;
	protected String sprite;
	protected Vector2 fuerzas = new Vector2();

	protected int ultimoIndice, sala = -1;
	protected float duracion, fuerzaY = 0, fuerzaX = 0, velocidad = 1;
	protected boolean derecha = true, cambioDeFuerzas; // por default todos los pj aparecen mirando a la derecha
	protected EstadoMovimiento estado, estadoPrevio;

	protected int numEstado;

	public Entidad(Cuerpo cuerpo, String sprite) {
		this.cuerpo = cuerpo;
		this.sprite = sprite;
		
		setBounds(cuerpo.getPosition().x,cuerpo.getPosition().y,
				  cuerpo.getAncho(),cuerpo.getAlto());
	}
	//--------------------------------------------------------------------------------------------------------------------------------------
	//-------------------------------------------------------------SCENE 2D-----------------------------------------------------------------
	//--------------------------------------------------------------------------------------------------------------------------------------
	public abstract void draw(Batch batch, float parentAlpha);
	public abstract void act(float delta);
	//--------------------------------------------------------------------------------------------------------------------------------------
	//-------------------------------------------------------------GETTERS------------------------------------------------------------------
	//--------------------------------------------------------------------------------------------------------------------------------------
	public int getSala() {
		return this.sala;
	}
	public float getAncho() {
		return cuerpo.getAncho();
	}
	public float getAlto() {
		return cuerpo.getAlto();
	}
	public Cuerpo getCuerpo() {
		return cuerpo;
	}
	public boolean isDerecha() {
		return derecha;
	}
	public float getDuracion() {
		return duracion;
	}
	public Vector2 getPosition() {
		return cuerpo.getPosition();
	}
	public String getSprite() {
		return sprite;
	}
	public float getVelocidad() {
		return velocidad;
	}
	public EstadoMovimiento getEstado() {
		return estado;
	}
	public EstadoMovimiento getEstadoPrevio() {
		return estadoPrevio;
	}
	//--------------------------------------------------------------------------------------------------------------------------------------
	//-------------------------------------------------------------SETTERS------------------------------------------------------------------
	//--------------------------------------------------------------------------------------------------------------------------------------
	public void setSala(int sala) {
		this.sala = sala;
	}
	public void setDuracion(float duracion) {
		this.duracion = duracion;
	}
	public void setDerecha(boolean derecha) {
		this.derecha = derecha;
	}
	public void setFuerzaX(float fuerzaX) {
		this.fuerzaX = fuerzaX;
	}
	public void setFuerzaY(float fuerzaY) {
		this.fuerzaY = fuerzaY;
	}
	public void setPosition(float x, float y) {
		cuerpo.setPosition(x, y);
		super.setPosition(x, y);
	}
	public void setEstado(EstadoMovimiento estado) {
		this.estado = estado;
	}
	public void setEstadoPrevio(EstadoMovimiento estadoPrevio) {
		this.estadoPrevio = estadoPrevio;
	}
}
