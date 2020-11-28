package personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import utiles.Global;
import utiles.Utiles;

import cuerpos.Cuerpo;
import eventos.InterfaceRobable;

public class NPC extends Entidad implements InterfaceRobable{
	private int random = 0; // 1 arriba 2 abajo 3 izquierda 4 derecha 5-40 nada
	private int randomDirec;
	private boolean finRecorrido, detectado, CambioDirec, robado = false, salaRobada = false;
	private float tiempo=0, tiempoMov=0, tiempoDetectado; 
	private float posX , posY;
	private int movimientoElegido;
	private boolean mover;
	
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
			salaRobada = true;
		}
	}
	// --------------------------------------------------------------------------------------------------------------------------------------
	// -------------------------------------------------------------SCENE 2D-----------------------------------------------------------------
	// --------------------------------------------------------------------------------------------------------------------------------------
	public void draw(Batch batch, float parentAlpha) {
		Color color = getColor();
		batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
	}
	public void act(float delta) {	
		if(detectado) {
			tiempoDetectado += Gdx.graphics.getRawDeltaTime();
			if(tiempoDetectado > 5){
				tiempoDetectado = 0;
				detectado = false;
				setRobado(salaRobada);
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
			//se le resta la mitad del ancho y la del alto para alinear el centro de los body a la de las texturas
			posX=cuerpo.getPosition().x-(cuerpo.getAncho()/2);
			posY=cuerpo.getPosition().y-(cuerpo.getAlto()/2);
			Utiles.hs.enviarMensajeATodos("npcs%" + "posicion%" + identificador + "%" + posX + "%" + posY);
		}
		for (int i = 0; i < EstadoMovimiento.values().length; i++) {
			numEstado = (EstadoMovimiento.values()[i].equals(estado))? i : numEstado;
		}
		Utiles.hs.enviarMensajeATodos("npcs%" + "estado%" + identificador + "%" + numEstado);
	}
	// --------------------------------------------------------------------------------------------------------------------------------------
	// -------------------------------------------------------------ANIMACION----------------------------------------------------------------
	// --------------------------------------------------------------------------------------------------------------------------------------
	//@Override
	protected TextureRegion animacionMovimiento() {
		// 1 arriba 2 abajo 3 izquierda 4 derecha >5 nada
		if(movimientoElegido == 4) {// moverse a la derecha
			estado = EstadoMovimiento.corriendoDerecha;
			setDerecha(true);
			ultimoIndice = 0;
			return null;
		}
		if(movimientoElegido == 3) {// moverse a la izquierda
			estado = EstadoMovimiento.corriendoIzquierda;
			setDerecha(false);
			ultimoIndice = 1;
			return null;
		}
		if(movimientoElegido == 1 || movimientoElegido == 2) {
			estado = EstadoMovimiento.movimientoY;
			setDerecha((ultimoIndice==0)?true:false);
			return null;
		}
		estado = EstadoMovimiento.parado;
		return null;
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
		Utiles.hs.enviarMensajeATodos("npcs%salaRobada%" + identificador);
	}//hola
	public void setPosicion(float x, float y) {
    	this.cuerpo.setPosition(x,y);
	}
	public void setCambioDirec(boolean parar) {
		this.CambioDirec = parar;
		tiempo = 0;
	}
	@Override
	public void setSala(int sala) {
		this.sala = sala;
		Utiles.hs.enviarMensajeATodos("npcs%sala%" + identificador + "%" + this.sala);
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
