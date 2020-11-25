package pantallas;

import utiles.Global;
import utiles.Render;
import utiles.Utiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import cuerpos.Cuerpo;
import cuerpos.Plataforma;
import personajes.Guardia;
import personajes.Jugador;
import personajes.Ladron;
import personajes.SpriteInfo;
import powerUps.*;


public class PantallaRonda2 extends PantallaRonda{

	private PowerUp[] powerUp;
	private Plataforma[] plataformaMovil;
	private float tiempo;
	public float fuerzaXGuardia, fuerzaYGuardia;
	public float fuerzaXLadron, fuerzaYLadron;
	public boolean keyDownGuardia, keyDownLadron;
	
	public PantallaRonda2(Vector2 gravedad, String rutaMapa) {
		super(gravedad, rutaMapa, 2);
	}
	@Override
	public void show() {
		plataformaMovil = new Plataforma[mapa.getPlataformasInicioPosition().length];
		powerUp = new PowerUp[mapa.getPowerUps().length];
		Utiles.hs.setApp(this);
		crearjugadores();
		crearPlataformas();
		crearPowerUps();
		
		mundo.setContactListener(new ContactListener() {
			@Override
			public void beginContact(Contact contact) {
				Object o1 = contact.getFixtureA().getBody().getUserData();
				Object o2 = contact.getFixtureB().getBody().getUserData();
				try {
					if (o2 instanceof PowerUp && o1 instanceof Jugador) {
						switch (((PowerUp) o2).getNumeroPowerUp()) {
						case 1:
							((Jugador) o1).setModificadorX(((PowerUp) o2).getEfecto());
							((PowerUp) o2).setActivo(false);
							break;
						case 2:
							//((Jugador) o1).setModificadorX(((PowerUp) o2).getEfecto());
							((PowerUp) o2).setActivo(false);
							break;
						case 3:
							//((Jugador) o1).setModificadorY(((PowerUp) o2).getEfecto());
							((PowerUp) o2).setActivo(false);
							break;
						}
					}
					if (o1 instanceof PowerUp && o2 instanceof Jugador) {
						switch (((PowerUp) o1).getNumeroPowerUp()) {
						case 1:
							((Jugador) o2).setModificadorX(((PowerUp) o1).getEfecto());
							((PowerUp) o1).setActivo(false);
							break;
						case 2:
							//((Jugador) o2).setModificadorX(((PowerUp) o1).getEfecto());
							((PowerUp) o1).setActivo(false);
							break;
						case 3:
							//((Jugador) o2).setModificadorY(((PowerUp) o1).getEfecto());
							((PowerUp) o1).setActivo(false);
							break;
						}
					}
				} catch (Exception e) {
				}
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
		if(Global.ronda==2) {
			update(delta);
			
			System.out.println(tiempo);
			tmr.setView(camera);
			tmr.render();
			b2dr.render(mundo, camera.combined);
			
			stage.act();
			stage.draw();

			movimiento();
			
			for (int i = 0; i < plataformaMovil.length; i++) {
				plataformaMovil[i].moverPlataforma(delta);	
				Utiles.hs.enviarMensajeATodos("plataforma%" + i 
													  + "%" + (plataformaMovil[i].getPosition().x - (plataformaMovil[i].getSize().x/2))
													  + "%" + (plataformaMovil[i].getPosition().y - (plataformaMovil[i].getSize().y/2)) );
			}
			
			Render.batch.setProjectionMatrix(camera.combined);
	     	Gdx.input.setInputProcessor(stage);
		}
	}

	@Override
	public void dispose() {
		super.dispose();
	}
	private void update(float delta) {
		mundo.step(1 / 60f, 6, 2);
		
		for (int i = 0; i < powerUp.length; i++) {
			if (!powerUp[i].isActivo()) {
				powerUp[i].setWorldActive(false);
				Utiles.hs.enviarMensajeATodos("powerUps%desactivar%" + i);
			}
			if (powerUp[i].isActivo()) {
				powerUp[i].setWorldActive(true);
				Utiles.hs.enviarMensajeATodos("powerUps%activar%" + i);
			}
		}
		
		camera.update();
	}
	private void crearjugadores() {
		this.jugadorLadron = new Ladron(new Cuerpo(mundo, 16, 15, BodyType.DynamicBody, 50, 160),
										SpriteInfo.values()[SpriteInfo.getIndiceLadron()].getFilename(),
										SpriteInfo.values()[SpriteInfo.getIndiceLadron()].getApariencia());
		stage.addActor(this.jugadorLadron);
		this.jugadorGuardia = new Guardia(new Cuerpo(mundo, 16, 15, BodyType.DynamicBody, 100, 100),
										  "personajes/badlogic.jpg");
		stage.addActor(this.jugadorGuardia);
	}
	public void crearPlataformas() {
		float[] duracion = new float[] {2f, 2f};
		for (int i = 0; i < plataformaMovil.length; i++) {
			plataformaMovil[i] = new Plataforma(mundo, mapa.getPlataformasSize()[i].x, mapa.getPlataformasSize()[i].y
											   ,mapa.getPlataformasInicioPosition()[i].x, mapa.getPlataformasInicioPosition()[i].y);
			plataformaMovil[i].setMovimiento(duracion[i], mapa.getPlataformasInicioPosition()[i], 
														  mapa.getPlataformasFinalPosition()[i]);
		}
	}
	private void crearPowerUps() {
		for (int i = 0; i < powerUp.length; i++) {
			for (int j = 0; j < PowerUpsEnum.values().length; j++) {
				if(mapa.getPowerUps()[i].equals(PowerUpsEnum.values()[j].getNombre())) {
					powerUp[i] = PowerUpsEnum.values()[j].crearpowerUp(mundo, mapa.getPowerUpsPosition()[i].x, 
																			  mapa.getPowerUpsPosition()[i].y);
					stage.addActor(powerUp[i]);
				}
			}
		}
	}
	public void movimiento() {
		if(keyDownGuardia) {
			jugadorGuardia.getCuerpo().setLinearVelocity(fuerzaXGuardia, fuerzaYGuardia);
		}
		if(keyDownLadron) {
			jugadorLadron.getCuerpo().setLinearVelocity(fuerzaXLadron, fuerzaYLadron);
		}
	}
}
