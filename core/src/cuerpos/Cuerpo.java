package cuerpos;

import com.badlogic.gdx.math.Vector2;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import utiles.Utiles;

public class Cuerpo{
	
	private float ancho, alto;
	private Body bodyReferencia = null;
	private Fixture fixture;
	private int numeroArea= -1, cantEntidades; //si numeroZona = -1 el cuerpo no es una zona
											   //si numeroZona != -1 el cuerpo es una zona
	private boolean robado;
	
	private BodyDef def;
	
	public Cuerpo(World mundo,
				  float ancho, float alto, BodyType bodyType,
				  float positionX, float positionY) {
		this.ancho = ancho * Utiles.PPM;
		this.alto = alto * Utiles.PPM;	
		
		def = new BodyDef();
		def.type = bodyType;
		
		def.position.set((positionX * Utiles.PPM),(positionY * Utiles.PPM));
		def.fixedRotation = true;
		bodyReferencia = mundo.createBody(def);
		
		PolygonShape forma = new PolygonShape();
		forma.setAsBox((ancho * Utiles.PPM)/2, (alto * Utiles.PPM)/2);
	    
		fixture = bodyReferencia.createFixture(forma, 1f);
		fixture.setFriction(0f);
		
		
		forma.dispose();
	}
	/*public Cuerpo(World mundo,
			  	  float ancho, float alto, BodyType bodyType,
			  	  float positionX, float positionY,
			  	  boolean escenario) {
	this.ancho = ancho * Utiles.PPM;
	this.alto = alto * Utiles.PPM;	
	
	def = new BodyDef();
	def.type = bodyType;
	
	def.position.set((positionX * Utiles.PPM),(positionY * Utiles.PPM));
	def.fixedRotation = true;
	bodyReferencia = mundo.createBody(def);
	
	PolygonShape forma = new PolygonShape();
	forma.setAsBox((ancho * Utiles.PPM)/2, (alto * Utiles.PPM)/2);
	
	fixture = bodyReferencia.createFixture(forma, 1f);
	
	if(escenario) {
		setUserData(this);
	}
	forma.dispose();
	}*/
	//--------------------------------------------------------------------------------------------------------------------------------------
	//-------------------------------------------------------------SETTERS------------------------------------------------------------------
	//--------------------------------------------------------------------------------------------------------------------------------------
	public void setArea(int numeroZona){
		this.numeroArea = numeroZona;			
		fixture.setSensor(true);
		setUserData(this);
	}
	public void setUserData(Object ojeto) {
		bodyReferencia.setUserData(ojeto);
	}
	public void setRobado(boolean robado) {
		this.robado = robado;
	}
	public void setPosition(float x, float y) {
		bodyReferencia.setTransform(x, y, 0);
	}
	public void setLinearVelocity(float vX,float vY) {
		bodyReferencia.setLinearVelocity(vX, vY);
	}
	public void applyLinearImpulse(float vX,float vY) {
		bodyReferencia.applyLinearImpulse(new Vector2(vX, vY), bodyReferencia.getWorldCenter(), true);
	}
	public void sumCantEntidades() {
		this.cantEntidades ++;
	}
	public void restCantEntidades() {
		this.cantEntidades --;
	}
	//--------------------------------------------------------------------------------------------------------------------------------------
	//-------------------------------------------------------------GETTERS------------------------------------------------------------------
	//--------------------------------------------------------------------------------------------------------------------------------------
	public Vector2 getPosition() {
		return bodyReferencia.getPosition();
	}
	public float getAncho() {
		return ancho;
	}
	public float getAlto() {
		return alto;
	}
	
	public Body getBodyReferencia() {
		return bodyReferencia;
	}
	public int getZona(){
		return numeroArea;
	}
	public boolean isRobado() {
		return robado;
	}
	public Vector2 getLinearVelocity() {
		return bodyReferencia.getLinearVelocity();
	}
	public Object getUserData() {
		return bodyReferencia.getUserData();
	}
	public int getCantEntidades(){
		return cantEntidades;
	}

	public BodyDef getBodyDefinition() {
		return def;
	}
}
