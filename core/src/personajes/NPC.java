package personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

import utiles.Global;
import utiles.Utiles;

import cuerpos.Cuerpo;
import eventos.InterfaceRobable;

public class NPC extends Entidad implements InterfaceRobable{
	private int random = 0; // 1 arriba 2 abajo 3 izquierda 4 derecha 5-40 nada
	private int randomDirec;
	private boolean finRecorrido, detectado, derecha, CambioDirec, robado = false;
	private float tiempo=0, tiempoMov=0, tiempoDetectado; 
	private int movimientoElegido;
	private boolean mover;
	private int idMensaje = 0;
	//private Sprite sprExclamation = new Sprite( new Texture("personajes/exclamacion-removebg-preview.png") );

	private int[] apariencia = new int[3]; 
	//0 pelo 
	//1 torso
	
	private int identificador;
	
	public NPC(Cuerpo cuerpo, String sprite, int[] apariencia, int identificador) {
		super(cuerpo, sprite);
		
		this.apariencia = apariencia;
		this.identificador = identificador;
		
        Utiles.addListener(this);
		cuerpo.setUserData(this);
	}
	// --------------------------------------------------------------------------------------------------------------------------------------
	// -------------------------------------------------------------ACCIONES-----------------------------------------------------------------
	// --------------------------------------------------------------------------------------------------------------------------------------
	public void detectarRobo() {
		setCambioDirec(true);
		setRobado(true);
		detectado = true;
	}
	@Override
	public void salaRobada(int sala) {
		if(getSala() == sala) {
			setRobado(true);
		}
	}
	// --------------------------------------------------------------------------------------------------------------------------------------
	// -------------------------------------------------------------SCENE 2D-----------------------------------------------------------------
	// --------------------------------------------------------------------------------------------------------------------------------------
	public void draw(Batch batch, float parentAlpha) {
		Color color = getColor();
		batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

		if(detectado){
			//sprExclamation.draw(batch);
		}
	}
	public void act(float delta) {	
		if(detectado) {
			tiempoDetectado += Gdx.graphics.getRawDeltaTime();
			if(tiempoDetectado > 5){
				tiempoDetectado = 0;
				detectado = false;
				setRobado(false);
			}
		}

		random = Utiles.r.nextInt(700)+1;
		//si este random se sacaba todo el tiempo siempre salía un numero distinto
		//asi que lo guarde en una variable distitna que se llama movimientoElegido
		//hola
		if((random < 5) && (tiempo==0.0f)) {
			randomDirec = Utiles.r.nextInt(2);
			movimientoElegido = random;
			tiempoMov = (Utiles.r.nextFloat() * 0.20f)+0.2f;
			Utiles.hs.enviarMensajeATodos("npcs%" + "tiempoMov%" + identificador + "%" + tiempoMov);//0-1-2-3	

		}else if((random > 5) && (tiempo==0.0f) ) {
			movimientoElegido = 10;
			tiempoMov=0;
			tiempo=0;

			this.enviarFuerzas(0,0);
		}
		if((tiempo < tiempoMov) || ((movimientoElegido < 5) && (tiempo==0.0f))) {
			//tiempoMov antes estaba aca
			//lo que hace que se seleccione un tiempoMov nuevo en cada iteración
			finRecorrido = false;		

			tiempo += Gdx.graphics.getRawDeltaTime();
		}else if(tiempo > tiempoMov && tiempo < tiempoMov * 2){
			finRecorrido = true;
			tiempo += Gdx.graphics.getRawDeltaTime();
		}else {
			if(Global.empiezaJuego) {
				tiempo = 0;

				this.enviarFuerzas(0,0);
			}
		}
		
		animacionMovimiento();
		if(Global.empiezaJuego) {
			if(finRecorrido) {
				this.enviarFuerzas(-1,-1);
			}else {
				this.enviarFuerzas(1,1);
			}
		}
		
		if (mover) {
			Utiles.hs.enviarMensajeATodos("npcs%" + "posicion%" + identificador + "%" + this.cuerpo.getPosition().x + "%" + this.cuerpo.getPosition().y);
		}
	}
	// --------------------------------------------------------------------------------------------------------------------------------------
	// -------------------------------------------------------------ANIMACION----------------------------------------------------------------
	// --------------------------------------------------------------------------------------------------------------------------------------
	//@Override
	protected TextureRegion animacionMovimiento() {
		switch (movimientoElegido) {
		case 1:
			if(!CambioDirec) {
				this.fuerzaX = 0;
				this.fuerzaY = 1;
			}
			setDerecha((ultimoIndice==0)?true:false);
			return null;
		case 2:
			if(!CambioDirec) {
				this.fuerzaX = 0;
				this.fuerzaY = -1;
			}
			setDerecha((ultimoIndice==0)?true:false);
			return null;
		case 3:
			if(!CambioDirec) {
				this.fuerzaY = 0;
				this.fuerzaX = -1;
			}
			if(finRecorrido) {
				setDerecha(true);
				ultimoIndice = 0;
				return null; 
			}else {
				setDerecha(false);
				ultimoIndice = 1;
				return null;		
			}
		case 4:
			if(!CambioDirec) {
				this.fuerzaY = 0;
				this.fuerzaX = 1;
			}
			if(finRecorrido) {
				setDerecha(false);
				ultimoIndice = 1;
				return null;
			}else {
				setDerecha(true);
				ultimoIndice = 0;
				return null;
			}
		default:
			fuerzaX = 0;
			fuerzaY = 0;
			return null;
		}
	}
	// --------------------------------------------------------------------------------------------------------------------------------------
	// -------------------------------------------------------------GETTERS------------------------------------------------------------------
	// --------------------------------------------------------------------------------------------------------------------------------------
	public boolean isRobado() {
		return robado;
	}
	public boolean isCambioDirec() {
		return CambioDirec;
	}
	public int[] getApariencia() {
		return apariencia;
	}
	// --------------------------------------------------------------------------------------------------------------------------------------
	// -------------------------------------------------------------SETTERS------------------------------------------------------------------
	// --------------------------------------------------------------------------------------------------------------------------------------
	public void setRobado(boolean robado) {
		this.robado = robado;
	}//hola
	public void setPosicion(float x, float y) {
    	this.cuerpo.setPosition(x,y);
    	
	}
	public void setCambioDirec(boolean parar) {
		this.CambioDirec = parar;
		tiempo = 0;
	}
	public void enviarFuerzaX(float fuerzaNueva) {
		if(fuerzaNueva!=fuerzaX){
			fuerzaX = fuerzaNueva;
			fuerzaY = 0;

			this.cuerpo.setLinearVelocity(fuerzaX,fuerzaY);
			
			if(fuerzaNueva!=0) mover = true;
			else mover = false;
			//Utiles.hs.enviarMensajeATodos("npcs%" + "fuerza%" + "x%" + identificador + "%" + fuerzaX + "%" + idMensaje);
		}
	}
	public void enviarFuerzas(int fuerzaNuevaX, int fuerzaNuevaY) {
		
		if(fuerzaNuevaX != fuerzaX){
			if(randomDirec==0)fuerzaX = fuerzaNuevaX;
		}
		
		if(fuerzaNuevaY != fuerzaY){
			if(randomDirec==1)fuerzaY = fuerzaNuevaY;
		}
		
		if(fuerzaNuevaX !=0 && fuerzaNuevaY !=0) mover = true;
		else mover = false;
		
		this.cuerpo.setLinearVelocity(fuerzaX,fuerzaY);
	}
}
