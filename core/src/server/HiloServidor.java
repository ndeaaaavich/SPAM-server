package server;

import java.io.IOException;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;

import eventos.InterfaceRobable;
import utiles.Global;
import utiles.Utiles;
import pantallas.*;
import personajes.EstadoMovimiento;
import personajes.NPC;

public class HiloServidor extends Thread {

	private DatagramSocket conexion;
	private boolean fin = false;
	private DireccionRed[] clientes = new DireccionRed[2];

	private int cantClientes = 0;

	private PantallaRonda app;
	private float fuerzaX, fuerzaY;
	private NPC[] npcs;

	public float getFuerzaX() {
		return fuerzaX;
	}

	public float getFuerzaY() {
		return fuerzaY;
	}
	public DireccionRed[] getClientes() {
		return clientes;
	}
	public void setApp(PantallaRonda app) {
		this.app = app;
	}

	public HiloServidor(PantallaRonda app) {
		this.app = app;
		try {
			conexion = new DatagramSocket(42069);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	public void enviarMensaje(String msg, InetAddress ip, int puerto) {
		byte[] data = msg.getBytes();
		DatagramPacket dp = new DatagramPacket(data, data.length, ip, puerto);
		try {
			conexion.send(dp);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		do {
			byte[] data = new byte[1024];
			DatagramPacket dp = new DatagramPacket(data, data.length);
			try {
				conexion.receive(dp);
			} catch (IOException e) {
				e.printStackTrace();
			}
			procesarMensaje(dp);
		} while (!fin);
	}

	private void procesarMensaje(DatagramPacket dp) {
		String msg = (new String(dp.getData())).trim();
		String[] mensajeParametrizado = msg.split("%");
		int nroCliente = -1;

		if (cantClientes > 1) {
			for (int i = 0; i < clientes.length; i++) {
				if (dp.getPort() == clientes[i].getPuerto() && dp.getAddress().equals(clientes[i].getIp())) {
					nroCliente = i;
				}
			}
		}

		if (cantClientes < 2) {
			if (msg.equals("Conexion")) {
				if (cantClientes < 2) {
					clientes[cantClientes] = new DireccionRed(dp.getAddress(), dp.getPort());
					System.out.println("Se conecto un cliente");
					enviarMensaje("OK%" + (cantClientes + 1), clientes[cantClientes].getIp(),
							clientes[cantClientes++].getPuerto());
					if (cantClientes == 2) {
						System.out.println("Hay dos clientes conectados");
						((PantallaRonda1) app).crearNPCs(
								((PantallaRonda1) app).npcs.length / app.mapa.getVectorZonas().length - 2,
								((PantallaRonda1) app).npcs.length / app.mapa.getVectorZonas().length + 2);
						npcs = ((PantallaRonda1) app).npcs;

						System.out.println("Creando los npcs: ");
						for (int i = 0; i < npcs.length; i++) {
							enviarMensajeATodos("npcs%" + "crear%" + i + "%" + npcs[i].getSprite() + "%"
									+ npcs[i].getCuerpo().getAncho() / Utiles.PPM + "%"
									+ npcs[i].getCuerpo().getAlto() / Utiles.PPM + "%"
									+ npcs[i].getPosition().x / Utiles.PPM + "%" + npcs[i].getPosition().y / Utiles.PPM
									+ "%" + npcs[i].getApariencia()[0] + "%" + npcs[i].getApariencia()[1] + "%"
									+ npcs[i].getApariencia()[2]);
							System.out.print(i + " ");
						}

						System.out.println("Creando el ladron:");
						enviarMensajeATodos("ladron%" + ((PantallaRonda1) app).indiceLadron + "%"
								+ ((PantallaRonda1) app).posXLadron + "%" + ((PantallaRonda1) app).posYLadron);
						System.out.println("1");

						Global.conexion = true;
						enviarMensajeATodos("ConexionLista");
					}
				}
			}
		} else {
			if (nroCliente != -1) {
				if (mensajeParametrizado[0].equals("Empieza")) {
					Global.empiezaJuego = true;
				} else if (mensajeParametrizado[0].equals("movimiento")) {

					boolean keyDown = Boolean.parseBoolean(mensajeParametrizado[2]);
					
					//app.jugadorGuardia.getVelocidad() tome la velocidad del guardia sin ningun tipo de criterio
					//tanto la velocidad del guardia como la del ladron y los npc deberia ser la misma
					if(nroCliente == 0) {
						movimientoGuardia(mensajeParametrizado, keyDown);
					}else {
						movimientoLadron(mensajeParametrizado, keyDown);
					}			
					if(nroCliente == 0) {
						app.jugadorGuardia.setDireccion(new Vector2(fuerzaX, fuerzaY));
					}else {
						app.jugadorLadron.setDireccion(new Vector2(fuerzaX, fuerzaY));
					}
					
				} else if (mensajeParametrizado[0].equals("ladron")) {
					if (mensajeParametrizado[1].equals("robo")) {
						for (int i = 0; i < Utiles.getListeners().size(); i++) {
							((InterfaceRobable) Utiles.getListeners().get(i)).salaRobada(Integer.parseInt(mensajeParametrizado[2]));
						}
						enviarMensajeATodos("ladron%robo%" + mensajeParametrizado[2]);
						enviarMensaje("guardia%npcDialogo%" + mensajeParametrizado[3], clientes[0].getIp(),clientes[0].getPuerto());

					} else if (mensajeParametrizado[1].equals("gano")) {
						Global.puntajeLadron++;
						Global.ronda++;
						enviarMensajeATodos("ladron%gano%" + Global.ronda);
					} else if (mensajeParametrizado[1].equals("perdio")) {
						Global.puntajeGuardia++;
						Global.ronda++;
						enviarMensajeATodos("ladron%perdio%" + Global.ronda);
					}

				} else if (mensajeParametrizado[0].equals("guardia")) {
					if (mensajeParametrizado[1].equals("gano")) {
						Global.puntajeGuardia++;
						Global.ronda++;
						enviarMensajeATodos("guardia%gano%" + Global.ronda);
					} else if (mensajeParametrizado[1].equals("perdio")) {
						Global.puntajeLadron++;
						Global.ronda++;
						enviarMensajeATodos("guardia%perdio%" + Global.ronda);
					}
				}
			}
		}

	}

	private void movimientoLadron(String[] mensajeParametrizado, boolean keyDown) {
		if (Integer.parseInt(mensajeParametrizado[1]) == Keys.DPAD_UP ||
				Integer.parseInt(mensajeParametrizado[1]) == Keys.W) {
				if(keyDown) {
					fuerzaY = app.jugadorLadron.getVelocidad();
					app.jugadorLadron.setEstado(EstadoMovimiento.movimientoY);
				}else {
					fuerzaY = 0;
					app.jugadorLadron.setEstado(EstadoMovimiento.parado);
				}
			}
			if (Integer.parseInt(mensajeParametrizado[1]) == Keys.DPAD_DOWN||
				Integer.parseInt(mensajeParametrizado[1]) == Keys.S) {
				if(keyDown) {
					fuerzaY = -app.jugadorLadron.getVelocidad();
					app.jugadorLadron.setEstado(EstadoMovimiento.movimientoY);
				}else {
					fuerzaY = 0;
					app.jugadorLadron.setEstado(EstadoMovimiento.parado);
				}
			}
			if (Integer.parseInt(mensajeParametrizado[1]) == Keys.DPAD_RIGHT||
				Integer.parseInt(mensajeParametrizado[1]) == Keys.D) {
				if(keyDown) {
					fuerzaX = app.jugadorLadron.getVelocidad();
					app.jugadorLadron.setEstado(EstadoMovimiento.corriendoDerecha);
				}else {
					fuerzaX = 0;
					app.jugadorLadron.setEstado(EstadoMovimiento.parado);
				}
			}
			if (Integer.parseInt(mensajeParametrizado[1]) == Keys.DPAD_LEFT||
				Integer.parseInt(mensajeParametrizado[1]) == Keys.A) {
				if(keyDown) {
					fuerzaX = -app.jugadorLadron.getVelocidad();
					app.jugadorLadron.setEstado(EstadoMovimiento.corriendoIzquierda);
				}else {
					fuerzaX = 0;
					app.jugadorLadron.setEstado(EstadoMovimiento.parado);
				}
			}
	}

	private void movimientoGuardia(String[] mensajeParametrizado, boolean keyDown) {
		if (Integer.parseInt(mensajeParametrizado[1]) == Keys.DPAD_UP ||
				Integer.parseInt(mensajeParametrizado[1]) == Keys.W) {
				if(keyDown) {
					fuerzaY = app.jugadorGuardia.getVelocidad();
					app.jugadorGuardia.setEstado(EstadoMovimiento.movimientoY);
				}else {
					fuerzaY = 0;
					app.jugadorGuardia.setEstado(EstadoMovimiento.parado);
				}
			}
			if (Integer.parseInt(mensajeParametrizado[1]) == Keys.DPAD_DOWN||
				Integer.parseInt(mensajeParametrizado[1]) == Keys.S) {
				if(keyDown) {
					fuerzaY = -app.jugadorGuardia.getVelocidad();
					app.jugadorGuardia.setEstado(EstadoMovimiento.movimientoY);
				}else {
					fuerzaY = 0;
					app.jugadorGuardia.setEstado(EstadoMovimiento.parado);
				}
			}
			if (Integer.parseInt(mensajeParametrizado[1]) == Keys.DPAD_RIGHT||
				Integer.parseInt(mensajeParametrizado[1]) == Keys.D) {
				if(keyDown) {
					fuerzaX = app.jugadorGuardia.getVelocidad();
					app.jugadorGuardia.setEstado(EstadoMovimiento.corriendoDerecha);
				}else {
					fuerzaX = 0;
					app.jugadorGuardia.setEstado(EstadoMovimiento.parado);
				}
			}
			if (Integer.parseInt(mensajeParametrizado[1]) == Keys.DPAD_LEFT||
				Integer.parseInt(mensajeParametrizado[1]) == Keys.A) {
				if(keyDown) {
					fuerzaX = -app.jugadorGuardia.getVelocidad();
					app.jugadorGuardia.setEstado(EstadoMovimiento.corriendoIzquierda);
				}else {
					fuerzaX = 0;
					app.jugadorGuardia.setEstado(EstadoMovimiento.parado);
				}
			}
	}

	public void enviarMensajeATodos(String msg) {
		for (int i = 0; i < clientes.length; i++) {
			enviarMensaje(msg, clientes[i].getIp(), clientes[i].getPuerto());
		}
	}

}
