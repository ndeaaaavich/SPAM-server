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
import powerUps.AumentoVelocidad;
import powerUps.DobleSalto;
import powerUps.PowerUp;
import powerUps.Ralentizacion;


public class PantallaRonda2 extends PantallaRonda{

	private PowerUp[] powerUp;
	private Plataforma[] plataformaMovil;
	private float tiempo;
	
	public PantallaRonda2(Vector2 gravedad, String rutaMapa) {
		super(gravedad, rutaMapa, 2);
	}
	@Override
	public void show() {
		Utiles.hs.setApp(this);
		plataformaMovil = new Plataforma[mapa.getPlataformasInicioPosition().length];
		powerUp = new PowerUp[mapa.getPowerUps().length];
		crearjugadores();
		crearPlataformas();
		PlataformaspowerUps();
	}
	@Override
	public void render(float delta) {
		Render.limpiarPantalla();
		
		mundo.step(1 / 60f, 6, 2);
		update(delta);
		
		tmr.setView(camera);
		tmr.render();
		b2dr.render(mundo, camera.combined);
		
		stage.act();
		stage.draw();
		
		Render.batch.setProjectionMatrix(camera.combined);
     	Gdx.input.setInputProcessor(stage);	
		for (int i = 0; i < plataformaMovil.length; i++) {
			plataformaMovil[i].moverPlataforma(delta);	
		}
	}

	@Override
	public void dispose() {
		super.dispose();
	}
	private void update(float delta) {
		mundo.step(1 / 60f, 6, 2);
		
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
	private void PlataformaspowerUps() {
		for (int i = 0; i < powerUp.length; i++) {
			if(mapa.getPowerUps()[i].equals("DobleSalto")) {
				powerUp[i] = new DobleSalto(mundo, mapa.getPowerUpsPosition()[i].x, mapa.getPowerUpsPosition()[i].y);
			}else if(mapa.getPowerUps()[i].equals("AumentoVelocidad")) {
				powerUp[i] = new AumentoVelocidad(mundo, mapa.getPowerUpsPosition()[i].x, mapa.getPowerUpsPosition()[i].y);
			}else if(mapa.getPowerUps()[i].equals("Ralentizacion")) {
				powerUp[i] = new Ralentizacion(mundo, mapa.getPowerUpsPosition()[i].x, mapa.getPowerUpsPosition()[i].y);
			}
			stage.addActor(powerUp[i]);
		}
	}
	
}
