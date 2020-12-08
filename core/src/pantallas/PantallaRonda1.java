package pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import server.HiloServidor;
import utiles.Global;
import utiles.Render;
import utiles.Utiles;
import cuerpos.Cuerpo;
import personajes.*;

public class PantallaRonda1 extends PantallaRonda{

	public NPC[] npcs = new NPC[17];

	private Vector2 posicion = new Vector2(0,0);
	private Vector2 puntoLlegada;
	private Vector2 puntoSalida;
	private Interpolation interpol = Interpolation.circle;
	
	private float tiempo, duracion = 2.5f;
	private boolean ladronCreado = false;
	
	public int indiceLadron;
	public float posXLadron, posYLadron;
	
	
	public PantallaRonda1(Vector2 gravedad, String rutaMapa) {
		super(gravedad, rutaMapa);
	}
	
	@Override
	public void show() {
		jugadorGuardia = new Guardia(new Cuerpo(mundo, 16, 15, BodyType.DynamicBody,0,0),"personajes/badlogic.jpg");
		stage.addActor(jugadorGuardia);
		int sala = Utiles.r.nextInt(mapa.getVectorZonas().length);
		jugadorGuardia.setPosition(mapa.getVectorZonas()[sala].getPosition().x, 
								   mapa.getVectorZonas()[sala].getPosition().y);
		jugadorGuardia.setSala(sala);
		//hilo server
		if(Global.ronda == 1) {
			Utiles.hs = new HiloServidor(this);
			Utiles.hs.start();
		}else {
			Utiles.hs.setApp(this);
		}
		Global.terminaRonda = false;
		//eventos
		stage.setKeyboardFocus(jugadorGuardia);
		
		// eventos
		mundo.setContactListener(new ContactListener() {
			@Override
			public void beginContact(Contact contact) {
				Object o1 = contact.getFixtureA().getBody().getUserData();
				Object o2 = contact.getFixtureB().getBody().getUserData();
				// HAY QUE HACER TODAS LAS COMPROBACIONES 2 VECES UNA CON o1 Y OTRA CON o2
				try {
					if (o2 instanceof Cuerpo) {// contactos zonas
						if (o1 instanceof Jugador) {// comprueba si el objeto que choca es el jugador
							
							if (((Jugador) o1).getSala() != -1) {
								
								((Jugador) o1).salaAnterior = ((Jugador) o1).getSala();
								
								if(((Jugador) o1).getCuerpo().getUserData() instanceof Guardia) {
									
									Utiles.hs.enviarMensaje("sala%anterior%" + ((Jugador) o1).getSala(), 
															Utiles.hs.getClientes()[0].getIp(), 
															Utiles.hs.getClientes()[0].getPuerto());
								}else {
									
									Utiles.hs.enviarMensaje("sala%anterior%" + ((Jugador) o1).getSala(), 
															Utiles.hs.getClientes()[1].getIp(), 
															Utiles.hs.getClientes()[1].getPuerto());	
								}
							}
							
							((Jugador) o1).setSala(((Cuerpo) o2).getZona());// cambia la sala del ladron a la sala
																			// en la que está
							
							((Jugador) o1).cambiarSala = true;
							
							if(((Jugador) o1).getCuerpo().getUserData() instanceof Guardia) {
								
								Utiles.hs.enviarMensaje("sala%" + ((Jugador) o1).getSala(), 
														Utiles.hs.getClientes()[0].getIp(), 
														Utiles.hs.getClientes()[0].getPuerto());
								Utiles.hs.enviarMensaje("sala%cambiar%" + true, 
														Utiles.hs.getClientes()[0].getIp(), 
														Utiles.hs.getClientes()[0].getPuerto());
							}else {
								
								Utiles.hs.enviarMensaje("sala%" + ((Jugador) o1).getSala(), 
														Utiles.hs.getClientes()[1].getIp(), 
														Utiles.hs.getClientes()[1].getPuerto());	
								Utiles.hs.enviarMensaje("sala%cambiar%" + true, 
														Utiles.hs.getClientes()[1].getIp(), 
														Utiles.hs.getClientes()[1].getPuerto());	
							}
						}
						if (o1 instanceof NPC) {// comprueba si el objeto que choca es el NPC
							((NPC) o1).setSala(((Cuerpo) o2).getZona());// cambia la sala del NPC a la sala
																		// en la que está
							if(((Cuerpo) o2).getCantEntidades() == 1) {
								ultimoNPC( ((NPC) o1).getSala(), false);
							}
							((Cuerpo) o2).sumCantEntidades();
							if (((Cuerpo) o2).isRobado()) {// si en la sala ya se realizó un robo el atributo
															// robado del cuerpo que representa a la sala será true

								((NPC) o1).setRobado(true); // no se podrá robar en esta sala asi que se pone el
															// atributo robado del NPC en true
							}
						}
					}

					if (o1 instanceof Cuerpo) {// contactos zonas
						if (o2 instanceof Jugador) {// comprueba si el objeto que choca es el ladron
							
							if (((Jugador) o2).getSala() != -1) {
								
								((Jugador) o2).salaAnterior = ((Jugador) o2).getSala();
								
								System.out.println(((Jugador) o2).getCuerpo().getUserData());

								if( ((Jugador) o2).getCuerpo().getUserData() instanceof Guardia) {
									
									Utiles.hs.enviarMensaje("sala%anterior%" + ((Jugador) o2).getSala(), 
															Utiles.hs.getClientes()[0].getIp(), 
															Utiles.hs.getClientes()[0].getPuerto());
								}else {
									
									Utiles.hs.enviarMensaje("sala%anterior%" + ((Jugador) o2).getSala(), 
															Utiles.hs.getClientes()[1].getIp(), 
															Utiles.hs.getClientes()[1].getPuerto());	
								}
							}
							
							((Jugador) o2).setSala(((Cuerpo) o1).getZona());// cambia la sala del ladron a la sala
																			// en la que está

							((Jugador) o2).cambiarSala = true;
							
							if(((Jugador) o2).getCuerpo().getUserData() instanceof Guardia) {
								
								Utiles.hs.enviarMensaje("sala%" + ((Jugador) o2).getSala(), 
														Utiles.hs.getClientes()[0].getIp(), 
														Utiles.hs.getClientes()[0].getPuerto());
								Utiles.hs.enviarMensaje("sala%cambiar%" + true, 
														Utiles.hs.getClientes()[0].getIp(), 
														Utiles.hs.getClientes()[0].getPuerto());
							}else {
								
								Utiles.hs.enviarMensaje("sala%" + ((Jugador) o2).getSala(), 
														Utiles.hs.getClientes()[1].getIp(), 
														Utiles.hs.getClientes()[1].getPuerto());	
								Utiles.hs.enviarMensaje("sala%cambiar%" + true, 
														Utiles.hs.getClientes()[1].getIp(), 
														Utiles.hs.getClientes()[1].getPuerto());
							}
						}
						if (o2 instanceof NPC) {// comprueba si el objeto que choca es el NPC
							((NPC) o2).setSala(((Cuerpo) o1).getZona());// cambia la sala del NPC a la sala
																		// en la que está
							if(((Cuerpo) o1).getCantEntidades() == 1) {
								ultimoNPC( ((NPC) o2).getSala(), false);
							}
							((Cuerpo) o1).sumCantEntidades();
							if (((Cuerpo) o1).isRobado()) {// si en la sala ya se realizó un robo el atributo
														   // robado del cuerpo que representa a la sala será true

								((NPC) o2).setRobado(true); // no se podrá robar en esta sala asi que se pone el
															// atributo robado del NPC en true
								}
						}
					}

					if (o1 == null || o1 instanceof NPC) {
						if (o1 instanceof NPC) {
							((NPC) o1).setCambioDirec(true);
						}
					}
					if (o2 == null || o2 instanceof NPC) {
						if (o2 instanceof NPC) {
							((NPC) o2).setCambioDirec(true);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			@Override
			public void endContact(Contact contact) {
				Object o1 = contact.getFixtureA().getBody().getUserData();
				Object o2 = contact.getFixtureB().getBody().getUserData();
				try {
					if (o1 instanceof NPC && o2 instanceof Cuerpo ) {
						((Cuerpo) o2).restCantEntidades();
						if(((Cuerpo) o2).getCantEntidades() == 1) {
							ultimoNPC(((NPC) o1).getSala(), true);
						}
					}
					if (o2 instanceof NPC && o1 instanceof Cuerpo ) {
						((Cuerpo) o1).restCantEntidades();
						if(((Cuerpo) o1).getCantEntidades() == 1) {
							ultimoNPC(((NPC) o2).getSala(), true);
						}
					}
				}catch (Exception e) {
				}
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
		
		if(Global.empiezaJuego && !Global.terminaJuego) {
			
			if (!Global.terminaRonda) {
				update(delta);
				tmr.setView(camera);
				tmr.render();
				b2dr.render(mundo, camera.combined);
				stage.act();
				stage.draw();
				
				Render.batch.setProjectionMatrix(camera.combined);
				Gdx.input.setInputProcessor(stage);
			}else{
				Utiles.principal.setScreen(new PantallaRonda1(new Vector2(0, 0), ("mapas/escenario.tmx")));
			}
		}
	}
	
	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
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
	//--------------------------------------------------------------------------------------------------------------------------------------
	//-------------------------------------------------------------NPC COSAS----------------------------------------------------------------
	//--------------------------------------------------------------------------------------------------------------------------------------
	public void crearNPCs(int minporsala, int maxporsala) {
		
		System.out.println("hola");
		
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
				jugadorLadron.setPosition(posX, posY);
				ladronCreado = true;
				i--;
				posXLadron = posX;
				posYLadron = posY;
			}else {
				npcs[i].setPosicion(posX,posY);
			}
			
		}
	}
	public void crearLadron(int indiceLadron) {
		SpriteInfo.setLadron(indiceLadron);
		jugadorLadron = new Ladron(new Cuerpo(mundo, 16, 15, BodyType.DynamicBody, 0, 0),
								   SpriteInfo.values()[indiceLadron].getFilename(),
								   SpriteInfo.values()[indiceLadron].getApariencia());
		stage.addActor(jugadorLadron);
		int sala = Utiles.r.nextInt(mapa.getVectorZonas().length);
		
		jugadorLadron.setPosition(mapa.getVectorZonas()[sala].getPosition().x, 
								  mapa.getVectorZonas()[sala].getPosition().y);
		jugadorLadron.setSala(sala);
	}
	public void ultimoNPC(int sala, boolean ultimo) {
		for (int i = 0; i < npcs.length; i++) {
			if(npcs[i].getSala() == sala) {
				npcs[i].setUltimoNPC(ultimo);
			}
		}
	}
}