package pantallas;

import com.badlogic.gdx.Gdx;


import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import server.HiloServidor;
import utiles.Global;
import utiles.Render;
import utiles.Utiles;

import cuerpos.Cuerpo;
import personajes.SpriteInfo;
import mapas.Mapa;
import personajes.Guardia;
import personajes.Jugador;
import personajes.Ladron;
import personajes.NPC;

public class PantallaRonda1 extends PantallaRonda{

	public NPC[] npcs = new NPC[8];
	//private boolean cerca = false;

	private Sprite sprRobo;
	private float posX = 0, posY = 0;
	private Vector2 posicion = new Vector2(0,0);
	private Vector2 puntoLlegada;
	private Vector2 puntoSalida;
	private Interpolation interpol = Interpolation.circle;
	
	private float tiempo, duracion = 2.5f;
	private boolean ladronCreado = false;
	
	public int indiceLadron;
	public float posXLadron, posYLadron;
	
	int cont;
	
	public PantallaRonda1(Vector2 gravedad, String rutaMapa) {
		super(gravedad, rutaMapa, 1);
	}
	
	@Override
	public void show() {

		int WScreen = Gdx.graphics.getWidth();
		int HScreen = Gdx.graphics.getHeight();

		//camara
		camera = new OrthographicCamera();
		camera.setToOrtho(false, (WScreen * Utiles.PPM) / 2, (HScreen * Utiles.PPM) / 2);
		//mundo
		mundo = new World(new Vector2(0, 0), true);
		
		viewport = new FitViewport((WScreen * Utiles.PPM) / 2, (HScreen * Utiles.PPM) / 2, camera);
		stage = new Stage(viewport);
		//TiledMap
		tileMap = new TmxMapLoader().load("mapas/escenario.tmx");
		b2dr = new Box2DDebugRenderer();
		tmr = new OrthogonalTiledMapRenderer(tileMap, 1 * Utiles.PPM);
		mapa = new Mapa(tileMap, mundo);
		//b2dr.setDrawBodies(false);
		
		jugadorGuardia = new Guardia(new Cuerpo(mundo, 16, 15, BodyType.DynamicBody, 200, 160), "personajes/badlogic.jpg");
		stage.addActor(jugadorGuardia);
		//hilo server
		Utiles.hs = new HiloServidor(this);
		Utiles.hs.start();
		//sprites
		

				
		sprRobo = new Sprite(new Texture("personajes/badlogic.jpg") );
		//eventos

		stage.setKeyboardFocus(jugadorGuardia);
		
		mundo.setContactListener(new ContactListener() {
			@Override
			public void beginContact(Contact contact) {
				Object o1 = contact.getFixtureA().getBody().getUserData();
				Object o2 = contact.getFixtureB().getBody().getUserData();
				//HAY QUE HACER TODAS LAS COMPROBACIONES 2 VECES UNA CON o1 Y OTRA CON o2
				try {
					if(o2 instanceof Cuerpo){//contactos zonas 
						if(o1 instanceof Jugador){//comprueba si el objeto que choca es el ladron
							
							if( ((Jugador) o1).getSala() != -1 ) {
								((Jugador) o1).salaAnterior = ((Jugador) o1).getSala();
							}
							
							((Jugador) o1).setSala( ((Cuerpo) o2).getZona() );//cambia la sala del ladron a la sala
																		     //en la que está
							
							((Jugador) o1).cambiarSala = true;
							
						}
						if(o1 instanceof NPC){//comprueba si el objeto que choca es el NPC
							((NPC) o1).setSala( ((Cuerpo) o2).getZona() );//cambia la sala del NPC a la sala
							   											//en la que está
							if( ((Cuerpo) o2).isRobado() ) {//si en la sala ya se realizó un robo el atributo
															//robado del cuerpo que representa a la sala será true

								((NPC) o1).setRobado(true); //no se podrá robar en esta sala asi que se pone el 
															//atributo robado del NPC en true
							}
						}
					}
					
					if(o1 instanceof Cuerpo){//contactos zonas 
						if(o2 instanceof Jugador){//comprueba si el objeto que choca es el ladron
							
							if( ((Jugador) o2).getSala() != -1 ) {
								((Jugador) o2).salaAnterior = ((Jugador) o2).getSala();
							}
							
							((Jugador) o2).setSala( ((Cuerpo) o1).getZona() );//cambia la sala del ladron a la sala
																		     //en la que está
							
							((Jugador) o2).cambiarSala = true;
							
						}
						if(o2 instanceof NPC){//comprueba si el objeto que choca es el NPC
							
							((NPC) o2).setSala( ((Cuerpo) o1).getZona() );//cambia la sala del NPC a la sala
							   											//en la que está
							if( ((Cuerpo) o1).isRobado() ) {//si en la sala ya se realizó un robo el atributo
															//robado del cuerpo que representa a la sala será true

								((NPC) o2).setRobado(true); //no se podrá robar en esta sala asi que se pone el 
															//atributo robado del NPC en true
							}
						}
					}

					
					if(o2 == null || o2 instanceof NPC) {
						if(o1 instanceof NPC){
							((NPC) o1).setCambioDirec(true);
						}
					}
					if(o1 == null || o1 instanceof NPC) {
						if(o2 instanceof NPC){
							((NPC) o2).setCambioDirec(true);
						}
					}
				}catch(Exception e) {}
			}
			@Override
			public void endContact(Contact contact) {
			}
			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
			}
			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
			}
		});
	}

	@Override
	public void render(float delta) {
		Render.limpiarPantalla();
		if(Global.empiezaJuego) {

			update(delta);
			
			cont ++;
			if(cont>8) {
				//Utiles.principal.setScreen(new PantallaRonda2(new Vector2(0,-9.8f), "mapas/ronda2.tmx"));
			}
			
			tmr.setView(camera);
			tmr.render();
			b2dr.render(mundo, camera.combined);
			stage.act();
			stage.draw();
			
			adelantarCuerpos();
			
			//Render.batch.begin();
			//test.draw(Render.batch);
			//Render.batch.end();
			
			Render.batch.setProjectionMatrix(camera.combined);
			Gdx.input.setInputProcessor(stage);
		}
	}
	
	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}
	@Override
	public void pause() {
	}
	@Override
	public void resume() {
	}
	@Override
	public void hide() {
	}
	@Override
	public void dispose() {
		Render.batch.dispose();
		stage.dispose();
		mundo.dispose();
	}
	//--------------------------------------------------------------------------------------------------------------------------------------
	//-------------------------------------------------------------CAMARA-------------------------------------------------------------------
	//--------------------------------------------------------------------------------------------------------------------------------------
	private void update(float delta) {
		mundo.step(1 / 60f, 6, 2);

		if(ladronCreado) {
	        if(jugadorLadron.cambiarSala){  // necesito sumar tiempo mientras se hace la transición
	        	tiempo += delta;
	
	        	camaraUpdate();
	
	        	if (tiempo>duracion) {
	            	jugadorLadron.cambiarSala = false;
	                tiempo = 0;
	            }
			}
			camera.update();
		}
	}
	private void camaraUpdate() {
		puntoLlegada = mapa.getVectorZonas()[jugadorLadron.getSala()].getPosition();
		puntoSalida = mapa.getVectorZonas()[jugadorLadron.getSalaAnterior()].getPosition();
			
		posicion.set(puntoLlegada);
		posicion.sub(puntoSalida);
		posicion.scl(interpol.apply(tiempo/duracion));
		posicion.add(puntoSalida);
		
		camera.position.set(posicion,0);
			
		camera.update();
	}
	private void adelantarCuerpos() {
		for (int i = 0; i < npcs.length; i++) {
			if(jugadorGuardia.getPosition().dst(npcs[i].getPosition()) < 30 * Utiles.PPM) {
				if(jugadorGuardia.getPosition().y > npcs[i].getPosition().y ) {
					jugadorGuardia.toBack();
				}else {	
					jugadorGuardia.toFront();
				}
			}
			if(jugadorLadron.getPosition().dst(npcs[i].getPosition()) < 30 * Utiles.PPM) {
				if(jugadorLadron.getPosition().y > npcs[i].getPosition().y ) {
					jugadorLadron.toBack();
				}else {	
					jugadorLadron.toFront();
				}
			}
		}
		if(jugadorLadron.getPosition().dst(jugadorGuardia.getPosition()) < 30 * Utiles.PPM) {
			if(jugadorLadron.getPosition().y > jugadorGuardia.getPosition().y ) {
				jugadorLadron.toBack();
			}else {	
				jugadorLadron.toFront();
			}
		}
	}
	//--------------------------------------------------------------------------------------------------------------------------------------
	//-------------------------------------------------------------NPC COSAS----------------------------------------------------------------
	//--------------------------------------------------------------------------------------------------------------------------------------
	public void crearNPCs(int minporsala, int maxporsala) {
		int i;
		int porsala[] = new int[mapa.getVectorZonas().length];
		int total = 0;
		int sala;
		boolean salaEncontrada;
		int subindice;
		// ----------------------------crear al ladron----------------------------------- //		
		indiceLadron = Utiles.r.nextInt(npcs.length+1);
		crearLadron(indiceLadron);
		// ----------------------------calcular npcs por zona---------------------------- //
			do{                 							  // antes lo que hacía era preguntar si porsala[0]+porsala[1]+porsala[2]
				total = 0;									  // para ver si llegaban a la cantidad de npcs que hay
				for (int j = 0; j < porsala.length; j++) {    
					porsala[j] = Utiles.r.nextInt(maxporsala-minporsala)+minporsala;
					total += porsala[j]; 
				}
			}while(total != npcs.length+1);
	
		// ----------------------------elegir sala para el npc---------------------------- //
		i=0;
		for(i=0;i<npcs.length;i++){
			subindice=i;
			
			if(i>=indiceLadron && ladronCreado) subindice++;
			
			if(i!=indiceLadron || (i==indiceLadron && ladronCreado)) {
				npcs[i] = new NPC(new Cuerpo(mundo,15,15,BodyType.DynamicBody,0,0),
								  SpriteInfo.values()[subindice].getFilename(),
							      SpriteInfo.values()[subindice].getApariencia(),
							      i);
				stage.addActor(npcs[i]);
			}
			
			int salaRandom = Utiles.r.nextInt(mapa.getVectorZonas().length);
			
			do{
				if(porsala[salaRandom]>0){          							                //se fija si hay "cupo" 
					if(i==indiceLadron && !ladronCreado) jugadorLadron.setSala(salaRandom);     //en la salaRandom elegida
					else npcs[i].setSala(salaRandom);                                           //y setea la sala y resta un cupo
					
					porsala[salaRandom]--;
					salaEncontrada = true;
				}else{
					salaRandom = Utiles.r.nextInt(mapa.getVectorZonas().length);
					if(i==indiceLadron && !ladronCreado) jugadorLadron.setSala(-1);        
					else npcs[i].setSala(-1);	
					
					salaEncontrada = false;
					sala = -1;
				} 
				
				//if(npcs.length == npcs.length-2) salaEncontrada = true;
			}while(!salaEncontrada);
			// ----------------------------posicionar npc---------------------------- //
			int margen = 60;

			float minimoX = mapa.getVectorZonas()[salaRandom].getPosition().x - mapa.getVectorZonas()[salaRandom].getAncho()/2;
			float minimoY = mapa.getVectorZonas()[salaRandom].getPosition().y - mapa.getVectorZonas()[salaRandom].getAlto()/2;
			
			float maximoX = ((mapa.getVectorZonas()[salaRandom].getAncho()) - (margen * Utiles.PPM)) + minimoX;
			float maximoY = ((mapa.getVectorZonas()[salaRandom].getAlto()) - (margen * Utiles.PPM)) + minimoY;
			
			float posX = (Utiles.r.nextFloat() * (maximoX - minimoX) + minimoX) + (margen/2)*Utiles.PPM;
			float posY = (Utiles.r.nextFloat() * (maximoY - minimoY) + minimoY) + (margen/2)*Utiles.PPM;
		
			if(i>=indiceLadron && ladronCreado) subindice--;
			
			if(i==indiceLadron && !ladronCreado){
				jugadorLadron.getCuerpo().setPosition(posX, posY);
				jugadorLadron.setPosition(posX, posY);
				ladronCreado = true;
				i--;
				posXLadron = posX;
				posYLadron = posY;
				System.out.println("setposition del ladron x:" + posX + posY);
			}
			
			else npcs[i].setPosicion(posX,posY);	
		}
	}
	public void crearLadron(int indiceLadron) {
		SpriteInfo.setLadron(indiceLadron);
		jugadorLadron = new Ladron(new Cuerpo(mundo, 16, 15, BodyType.DynamicBody, 0, 0),
								   SpriteInfo.values()[indiceLadron].getFilename(),
								   SpriteInfo.values()[indiceLadron].getApariencia());
		stage.addActor(jugadorLadron);
	}
}