package com.mica_cadura.telegram_bot.application.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import com.mica_cadura.telegram_bot.application.dto.Cagon;
import com.mica_cadura.telegram_bot.application.dto.ListaDeCagones;
import com.mica_cadura.telegram_bot.application.dto.MyComparatorAnual;
import com.mica_cadura.telegram_bot.application.dto.MyComparatorMes;

@Service
public class TelegramBot extends TelegramLongPollingBot {

	@Value("${token}")
	private String token;

	@Value("${random_num}")
	private int randomValue;

	@Value("${admin_user}")
	private String admin;

	private Boolean botStarted = false;

	private Boolean reset = false;

	private Boolean administrar = false;

	private Boolean habilitarUser = false;

	private Boolean anadirCacas = false;

	private Boolean ver_estado_usuario = false;

	private ListaDeCagones listaCagones = new ListaDeCagones();

	private List<String> respuestasNormales = new ArrayList<>();

	private List<String> respuestasNocturnas = new ArrayList<>();

	private List<String> respuestasTempranas = new ArrayList<>();

	private List<String> respuestasMediodia = new ArrayList<>();

	private List<String> listaUsuariosPermitidos = new ArrayList<>();

	private String[] posiciones = { "1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣", "6️⃣", "7️⃣", "8️⃣", "9️⃣", "🔟", "1️⃣1️⃣",
			"1️⃣2️⃣", "1️⃣3️⃣", "1️⃣4️⃣", "1️⃣5️⃣" };

	private String[] meses = { "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre",
			"Octubre", "Noviembre", "Diciembre" };

	private Calendar calendar1 = Calendar.getInstance();

	private int mesActual = calendar1.get(Calendar.MONTH);

	private int anoActual = calendar1.get(Calendar.YEAR);

	int contAux = -1;

	private boolean firstMessage = true;

	@Override
	public void onUpdateReceived(Update update) {

		if (firstMessage) {
			añadirRespuestasNormales();
			añadirRespuestasNocturnas();
			añadirRespuestasTempranas();
			añadirRespuestasDeMediodia();
			firstMessage = false;
		}

		contAux++;

		Calendar calendar2 = Calendar.getInstance();
		int mesDelMensaje = calendar2.get(Calendar.MONTH);
		int anoDelMensaje = calendar2.get(Calendar.YEAR);

		int horaDelMensaje = calendar2.get(Calendar.HOUR_OF_DAY);
		int randomNum = (int) (Math.random() * randomValue);

//		System.out.println("Hora " + horaDelMensaje);
//		System.out.println("Random " + randomNum);
		// System.out.println("Mes de partida: " + mesActual);
//		int mesDelMensaje = 0;
//		int anoDelMensaje = 0;
//		if (contAux < 15) {
//			mesDelMensaje = calendar2.get(Calendar.MONTH);
//			anoDelMensaje = calendar2.get(Calendar.YEAR);
//		} else {
//			mesDelMensaje = 2;
//			anoDelMensaje = 2024;
//		}
		// System.out.println("Mes del mensaje: " + mesDelMensaje);
		

		String message = update.getMessage().getText();

		if (message.contains("@MicaCadura_bot")) {
			message = message.replace("@MicaCadura_bot", "");
		}

		String id = update.getMessage().getChatId().toString();
		String userName = update.getMessage().getFrom().getUserName();
		String userRealName = update.getMessage().getFrom().getFirstName();

		// System.out.println(message);

		SendMessage messageToUsers = new SendMessage();
		messageToUsers.setChatId(id);

		if (administrar) {

			if (message.equals("/habilitar_usuario") || habilitarUser) {

				if (!habilitarUser) {
					habilitarUser = true;

					messageToUsers.setText("Indica el '@usuario' para habilitarlo");
					executeMessage(messageToUsers);

				} else {

					if (isAdmin(userName)) {
						
						if(message.startsWith("@")) {
							
							boolean match = false;

							for (String user : listaUsuariosPermitidos) {

								if (user.equals(message)) {
									messageToUsers.setText("Usuario ya habilitado");
									executeMessage(messageToUsers);
									match = true;
									break;
								}
							}

							if (!match) {
								listaUsuariosPermitidos.add(message.replace(" ", ""));
								messageToUsers.setText("Usuario " + message + " habilitado correctamente");
								executeMessage(messageToUsers);
							}

							habilitarUser = false;

							messageToUsers.setText("¿Qué quieres hacer?" + "\n/habilitar_usuario" + "\n/agregar_cacas"
									+ "\n/ver_usuarios_permitidos" + "\n/ver_estado_usuario" + "\n/exit");
							executeMessage(messageToUsers);
							
						} else {
							
							messageToUsers.setText(message + " no es un usuario");
							executeMessage(messageToUsers);
							
							messageToUsers.setText(admin + ", indica el '@usuario' para habilitarlo");
							executeMessage(messageToUsers);
							
						}

					} else {
						
						messageToUsers.setText("Deja administrar al usuario");
						executeMessage(messageToUsers);
						
						messageToUsers.setText(admin + ", indica el '@usuario' para habilitarlo");
						executeMessage(messageToUsers);
						
					}

				}
			} else if (message.equals("/agregar_cacas") || anadirCacas) {

				if (!anadirCacas) {
					anadirCacas = true;
					messageToUsers.setText(
							"Indica el '@usuario' y los valores a añadir: cacasMes, cacaAño, cagonDelMes (seguir la estructura del ejemplo)");
					executeMessage(messageToUsers);
					messageToUsers.setText("Ejemplo: @User 1 3 0");
					executeMessage(messageToUsers);
					
				} else {
					
					if(isAdmin(userName)) {
						
						anadirCacas = false;
						processMessage(message, id);
						messageToUsers.setText("¿Qué quieres hacer?" + "\n/habilitar_usuario" + "\n/agregar_cacas"
								+ "\n/ver_usuarios_permitidos" + "\n/ver_estado_usuario" + "\n/exit");
						executeMessage(messageToUsers);
						
					} else {
						
						messageToUsers.setText("Deja administrar al usuario");
						executeMessage(messageToUsers);
						
						messageToUsers.setText(admin + ", indica el '@usuario' y los valores a añadir: cacasMes, cacaAño, cagonDelMes (seguir la estructura del ejemplo)");
						executeMessage(messageToUsers);
						
					}

				}

			} else if (message.equals("/ver_usuarios_permitidos")) {

				messageToUsers.setText("Usuarios permitidos: " + listaUsuariosPermitidos.size());
				executeMessage(messageToUsers);
				for (String user : listaUsuariosPermitidos) {
					messageToUsers.setText(user);
					executeMessage(messageToUsers);
				}

				messageToUsers.setText("¿Qué quieres hacer?" + "\n/habilitar_usuario" + "\n/agregar_cacas"
						+ "\n/ver_usuarios_permitidos" + "\n/ver_estado_usuario" + "\n/exit");
				executeMessage(messageToUsers);

			} else if (message.equals("/ver_estado_usuario") || ver_estado_usuario) {

				if (!ver_estado_usuario) {
					ver_estado_usuario = true;
					messageToUsers.setText("Indica el '@usuario' para ver su estado");
					executeMessage(messageToUsers);
					
				} else {
					
					if(isAdmin(userName)) {
						
						for (Cagon cagon : listaCagones.getListaCagones()) {

							message = message.replace("@", "");

							if (cagon.getName().equals(message.replace(" ", ""))) {
								messageToUsers
										.setText(cagon.getRealName() + ": [ Mes = " + cagon.getCacaMensual() + " , Año = "
												+ cagon.getCacaAnual() + " , Cagones = " + cagon.getCagonDelMes() + " ] ");
								executeMessage(messageToUsers);
							}

						}

						ver_estado_usuario = false;

						messageToUsers.setText("¿Qué quieres hacer?" + "\n/habilitar_usuario" + "\n/agregar_cacas"
								+ "\n/ver_usuarios_permitidos" + "\n/ver_estado_usuario" + "\n/exit");
						executeMessage(messageToUsers);
						
					} else {
						
						messageToUsers.setText("Deja administrar al usuario");
						executeMessage(messageToUsers);
						
						messageToUsers.setText(admin + ", indica el '@usuario' para ver su estado");
						executeMessage(messageToUsers);
						
					}

					

				}

			} else if (message.equals("/exit")) {

				administrar = false;
				messageToUsers.setText("Usad los siguientes comandos para interactuar conmigo:"
						+ "\n/start para iniciarme" + "\n/cacas_mensuales para mostrar el conteo de las cacas del mes"
						+ "\n/cacas_anuales para mostrrar el conteo de las cacas del año"
						+ "\n/help para mostrar la ayuda"
						+ "\n/administrar habilitar usuarios, restablecer nº de cacas..." + "\n/reset para resetearme");
				executeMessage(messageToUsers);

			} else {

				if (message.startsWith("/")) {
					messageToUsers.setText("Opción no válida");
					executeMessage(messageToUsers);
					messageToUsers.setText("¿Qué quieres hacer?" + "\n/habilitar_usuario" + "\n/agregar_cacas"
							+ "\n/ver_usuarios_permitidos" + "\n/ver_estado_usuario" + "\n/exit");
					executeMessage(messageToUsers);
				}
			}

		}

		if (botStarted && !administrar) {

			if (mesActual == mesDelMensaje) {

				if (message.equals("/reset")) {
					messageToUsers.setText("¿Seguro que quieres resetear el bot? Y/N");
					executeMessage(messageToUsers);
					botStarted = false;
					reset = true;

				} else if (message.equals("/start")) {

					messageToUsers.setText("Ya me encuentro contanto vuestras caquitas 😉");
					executeMessage(messageToUsers);

				} else {
					// Logica de usuarios y conteo 💩

					if (message.contains("💩")) {

						comprobarUsuario(userName, userRealName, id, randomNum, horaDelMensaje);

					} else if (message.equals("/cacas_mensuales")) {

						ListaDeCagones list = listaCagones;

						Collections.sort(listaCagones.getListaCagones(), new MyComparatorMes());
						
						Collections.reverse(listaCagones.getListaCagones());

						messageToUsers.setText("📊 Lista mensual de cagones");
						executeMessage(messageToUsers);

						int pos = 0;
						int numPrimero = 0;
						for (Cagon cagon : list.getListaCagones()) {

							if (pos == 0) {
								numPrimero = list.getListaCagones().get(0).getCacaMensual();
							}

							if (cagon.getCacaMensual() == numPrimero) {
								messageToUsers.setText(
										posiciones[0] + " " + cagon.getRealName() + " ➡️ " + cagon.getCacaMensual());
								executeMessage(messageToUsers);
							} else {
								messageToUsers.setText(
										posiciones[pos] + " " + cagon.getRealName() + " ➡️ " + cagon.getCacaMensual());
								executeMessage(messageToUsers);
							}
							pos++;

						}

					} else if (message.equals("/cacas_anuales")) {
						ListaDeCagones list = listaCagones;

						Collections.sort(listaCagones.getListaCagones(), new MyComparatorAnual());
						
						Collections.reverse(listaCagones.getListaCagones());

						messageToUsers.setText("📊 Lista anual de cagones");
						executeMessage(messageToUsers);

						int pos = 0;
						int numPrimero = 0;
						for (Cagon cagon : list.getListaCagones()) {

							if (pos == 0) {
								numPrimero = list.getListaCagones().get(0).getCacaAnual();
							}

							if (cagon.getCacaAnual() == numPrimero) {
								messageToUsers.setText(
										posiciones[0] + " " + cagon.getRealName() + " ➡️ " + cagon.getCacaAnual());
								executeMessage(messageToUsers);
							} else {
								messageToUsers.setText(
										posiciones[pos] + " " + cagon.getRealName() + " ➡️ " + cagon.getCacaAnual());
								executeMessage(messageToUsers);
							}
							pos++;
						}
					} else if (message.equals("/help")) {

						messageToUsers.setText(
								"Usad los siguientes comandos para interactuar conmigo:" + "\n/start para iniciarme"
										+ "\n/cacas_mensuales para mostrar el conteo de las cacas del mes"
										+ "\n/cacas_anuales para mostrrar el conteo de las cacas del año"
										+ "\n/help para mostrar la ayuda"
										+ "\n/administrar habilitar usuarios, restablecer nº de cacas..."
										+ "\n/reset para resetearme");
						executeMessage(messageToUsers);

					} else if (message.equals("/administrar")) {

						if (userName.equals(admin)) {
							administrar = true;

							messageToUsers.setText("¿Qué quieres hacer?" + "\n/habilitar_usuario" + "\n/agregar_cacas"
									+ "\n/ver_usuarios_permitidos" + "\n/ver_estado_usuario" + "\n/exit");
							executeMessage(messageToUsers);

						} else {
							messageToUsers.setText("No tienes permisos; solo mi creador puede administrar la mierda");
							executeMessage(messageToUsers);
						}

					} else if (!message.equals("/reset") && !message.equals("/start") && !message.equals("/help")
							&& !message.equals("/cacas_mensuales") && !message.equals("/cacas_anuales")
							&& !message.equals("/administrar") && !message.equals("/exit") && message.startsWith("/")) {
						messageToUsers.setText(
								userName + ", la cagas hasta para escribir los comandos. Escríbelo bien anda...");
						executeMessage(messageToUsers);
					}
				}

			} else {

				messageToUsers.setText(
						"Para para, no vayas tan rápido. Antes de seguir contando cacas y acabar de mierda hasta arriba, vamos a ver como quedó el mes pasado");
				executeMessage(messageToUsers);

				ListaDeCagones list = listaCagones;

				Collections.sort(listaCagones.getListaCagones(), new MyComparatorMes());
				
				Collections.reverse(listaCagones.getListaCagones());

				messageToUsers.setText("📊 Lista de cagones de " + meses[mesActual]);
				executeMessage(messageToUsers);

				int pos = 0;
				int numPrimero = 0;
				List<String> cagonDelMes = new ArrayList<>();

				for (Cagon cagon : list.getListaCagones()) {

					if (pos == 0) {
						numPrimero = list.getListaCagones().get(0).getCacaMensual();
					}

					if (numPrimero == cagon.getCacaMensual()) {
						messageToUsers
								.setText(posiciones[0] + " " + cagon.getRealName() + " ➡️ " + cagon.getCacaMensual());
						executeMessage(messageToUsers);
						cagonDelMes.add(cagon.getRealName());
						cagon.setCagonDelMes(cagon.getCagonDelMes() + 1);

					} else {
						messageToUsers
								.setText(posiciones[pos] + " " + cagon.getRealName() + " ➡️ " + cagon.getCacaMensual());
						executeMessage(messageToUsers);

					}

					cagon.setCacaMensual(0);

					pos++;
				}

				for (String usuarioCagon : cagonDelMes) {
					messageToUsers.setText("🏅 Felicidades " + usuarioCagon + ", eres el cagón del mes!");
					executeMessage(messageToUsers);
				}

				if (message.contains("💩")) {

					comprobarUsuario(userName, userRealName, id, randomNum, horaDelMensaje);

				}

				mesActual = mesDelMensaje;

				if (anoActual != anoDelMensaje) {

					messageToUsers.setText(
							"Un año viene y otro se va, por lo que debemos hacer recuento de este úlitmo año lleno de alegrias y mierda, sobretodo lo segundo 😉");
					executeMessage(messageToUsers);

					ListaDeCagones listAno = listaCagones;

					Collections.sort(listaCagones.getListaCagones(), new MyComparatorAnual());
					
					Collections.reverse(listaCagones.getListaCagones());

					messageToUsers.setText("📊 Lista de cagones del año " + anoActual);
					executeMessage(messageToUsers);

					int posAno = 0;
					int numPrimeroAno = 0;
					List<String> cagonDelAno = new ArrayList<>();

					for (Cagon cagon : listAno.getListaCagones()) {

						if (posAno == 0) {
							numPrimeroAno = listAno.getListaCagones().get(0).getCacaAnual();
						}

						if (numPrimeroAno == cagon.getCacaAnual()) {
							messageToUsers
									.setText(posiciones[0] + " " + cagon.getRealName() + " ➡️ " + cagon.getCacaAnual());
							executeMessage(messageToUsers);
							cagonDelAno.add(cagon.getRealName());

						} else {
							messageToUsers.setText(
									posiciones[posAno] + " " + cagon.getRealName() + " ➡️ " + cagon.getCacaAnual());
							executeMessage(messageToUsers);

						}

						cagon.setCagonDelMes(0);
						cagon.setCacaAnual(0);
						cagon.setCacaMensual(0);

						posAno++;
					}

					for (String usuarioCagon1 : cagonDelAno) {
						messageToUsers.setText("🏆 Premio al cagón del año para  " + usuarioCagon1
								+ ". Tu no eres 70% agua, tu eres 70% mierda 💩💩💩");
						executeMessage(messageToUsers);
					}

					anoActual = anoDelMensaje;
				}
			}

		} else {

			if (reset) {

				if (message.toUpperCase().equals("Y") || message.toUpperCase().equals("YES")) {

					if (userName.equals(admin)) {
						messageToUsers.setText("Me he cansado de vuestras mierdas, empezaré de nuevo");
						executeMessage(messageToUsers);

						listaCagones = new ListaDeCagones();

						botStarted = false;
						reset = false;
					} else {

						messageToUsers.setText("Lo siento " + userName
								+ ", no tienes permisos para reiniciarme. Solo luisSantos88, aka 'MiCreador' sabe como hacerlo 😉");
						executeMessage(messageToUsers);
					}

				} else if (message.toUpperCase().equals("N") || message.toUpperCase().equals("NO")) {

					messageToUsers.setText(
							"Como cuando piensas que te cagas pero solo es un pedete: falsa alarma. Todo se queda igual.");
					executeMessage(messageToUsers);

					botStarted = true;
					reset = false;

				} else {
					messageToUsers.setText("No te he entendido. Y o N, no hay mas opciones...");
					executeMessage(messageToUsers);
				}

			} else if (message.equals("/start")) {

				if (!botStarted) {
					messageToUsers.setText(
							"Hola grupo, no soy Vanesa pero me llamo Mica y estoy aqui para regular vuEstro tránsito intestinal; vamos, para contar cuantas veces cagais."
									+ "\nDe ahora en adelante, cada vez de defequeis, teneis que poner el emoji de la caca 💩 y yo me lo apuntare en mi lista particular."
									+ "\n\nUsad los siguientes comandos para interactuar conmigo:"
									+ "\n/start para iniciarme"
									+ "\n/cacas_mensuales para mostrar el conteo de las cacas del mes"
									+ "\n/cacas_anuales para mostrrar el conteo de las cacas del año"
									+ "\n/help para mostrar la ayuda"
									+ "\n/administrar habilitar usuarios, restablecer nº de cacas..."
									+ "\n/reset para resetearme");
					executeMessage(messageToUsers);

					botStarted = true;
					listaCagones = new ListaDeCagones();
					
					Cagon c1 = new Cagon();
					c1.setName("BBBB");
					c1.setRealName("BBBB");
					c1.setCacaMensual(3);
					c1.setCacaAnual(2);
					c1.setCagonDelMes(9);
					
					Cagon c2 = new Cagon();
					c2.setName("AAAA");
					c2.setRealName("AAAA");
					c2.setCacaMensual(5);
					c2.setCacaAnual(6);
					c2.setCagonDelMes(0);
					
					listaCagones.setListaCagones(c1);
					listaCagones.setListaCagones(c2);

				} else {
					messageToUsers.setText("Ya me encuentro contanto vuestras caquitas 😉");
					executeMessage(messageToUsers);
				}

			} else if (message.equals("/reset")) {

				if (listaCagones.getListaCagones().isEmpty() || listaCagones.getListaCagones() == null) {
					messageToUsers.setText("Ya me habeis reseteado antes; no tengo cagones para contar");
					executeMessage(messageToUsers);

				} else {
					messageToUsers.setText("¿Seguro que quieres resetear el bot? Y/N");
					executeMessage(messageToUsers);
					reset = true;
					botStarted = false;
				}

			} else if (message.equals("/help")) {

				messageToUsers.setText("Usad los siguientes comandos para interactuar conmigo:"
						+ "\n/start para iniciarme" + "\n/cacas_mensuales para mostrar el conteo de las cacas del mes"
						+ "\n/cacas_anuales para mostrrar el conteo de las cacas del año"
						+ "\n/help para mostrar la ayuda"
						+ "\n/administrar habilitar usuarios, restablecer nº de cacas..." + "\n/reset para resetearme");
				executeMessage(messageToUsers);

			} else if (message.equals("/administrar")) {

				if (userName.equals(admin)) {
					administrar = true;

					messageToUsers.setText("¿Qué quieres hacer?" + "\n/habilitar_usuario" + "\n/agregar_cacas"
							+ "\n/ver_usuarios_permitidos" + "\n/ver_estado_usuario" + "\n/exit");
					executeMessage(messageToUsers);

				} else {
					messageToUsers.setText("No tienes permisos; solo mi creador puede administrar la mierda");
					executeMessage(messageToUsers);
				}

			} else {

				// si el bot esta iniciado

				if (!administrar && !message.equals("/exit")) {
					if (!botStarted) {
						messageToUsers.setText(
								"Antes de nada debeis iniciarme con /start para que pueda empezar a funcionar o bien /help para abrir el menú de ayuda");
						executeMessage(messageToUsers);
					} else {

						messageToUsers.setChatId(id);
						messageToUsers.setText("Lo siento " + userRealName + ", no te he entendido. \n"
								+ "Prueba con /help para ayudarte");
						executeMessage(messageToUsers);

					}
				}

			}

		}

	}

	@Override
	public String getBotUsername() {
		// TODO Auto-generated method stub
		return "MicaCadura_bot";
	}

	@Override
	public String getBotToken() {
		// TODO Auto-generated method stub
		return token;
	}

	public void executeMessage(SendMessage message) {
		try {
			execute(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void comprobarUsuario(String name, String realName, String id, int randomNum, int horaDelMensaje) {

		boolean allowedUser = false;
		SendMessage messageToUsers = new SendMessage();
		messageToUsers.setChatId(id);

		for (String user : listaUsuariosPermitidos) {

			if (name.equals(user.replace("@", ""))) {
				allowedUser = true;
				break;
			}
		}

		if (allowedUser) {

			boolean match = false;

			for (Cagon cagon : listaCagones.getListaCagones()) {

				if (cagon.getName().equals(name)) {

					cagon.setCacaMensual(cagon.getCacaMensual() + 1);
					cagon.setCacaAnual(cagon.getCacaAnual() + 1);

					match = true;
					break;
				}

			}

			if (!match) {
				// crear cagon
				Cagon cagon = new Cagon();
				cagon.setName(name);
				cagon.setRealName(realName);
				cagon.setCacaMensual(1);
				cagon.setCacaAnual(1);
				cagon.setCagonDelMes(0);

				listaCagones.setListaCagones(cagon);
			}

			if (randomNum <= 9) {
				if (horaDelMensaje >= 1 && horaDelMensaje < 6) {
					messageToUsers.setText(respuestasNocturnas.get(randomNum));
					executeMessage(messageToUsers);
				} else if (horaDelMensaje >= 6 && horaDelMensaje < 13) {
					messageToUsers.setText(respuestasTempranas.get(randomNum));
					executeMessage(messageToUsers);
				} else if (horaDelMensaje >= 13 && horaDelMensaje < 16) {
					messageToUsers.setText(respuestasMediodia.get(randomNum));
					executeMessage(messageToUsers);
				} else {
					messageToUsers.setText(respuestasNormales.get(randomNum));
					executeMessage(messageToUsers);
				}
			}

		} else {
			messageToUsers.setText("Usuario no permitido");
			executeMessage(messageToUsers);
		}

	}

	public void processMessage(String message, String id) {

		SendMessage messageToUsers = new SendMessage();
		messageToUsers.setChatId(id);

		if (message.startsWith("@")) {

			boolean match = false;

			String usuario = filterMessage(message);
			message = message.substring(usuario.length() + 1);
			String cacaMes = filterMessage(message);
			message = message.substring(cacaMes.length() + 1);
			String cacaAno = filterMessage(message);
			message = message.substring(cacaAno.length() + 1);
			String vecesCagon = message;

			for (Cagon cagon : listaCagones.getListaCagones()) {

				if (usuario.replace("@", "").equals(cagon.getName())) {
					match = true;
					cagon.setCacaMensual(Integer.parseInt(cacaMes));
					cagon.setCacaAnual(Integer.parseInt(cacaAno));
					cagon.setCagonDelMes(Integer.parseInt(vecesCagon));
				}
			}

			if (!match) {
				messageToUsers.setText(
						"Usuario no encontrado. Sal de la administración y añade una 💩; luego vuelve a realizar este paso");
				executeMessage(messageToUsers);
			}

		} else {
			messageToUsers.setText("Añadir '@'");
			executeMessage(messageToUsers);
		}

	}

	public String filterMessage(String message) {

		int ini = 0;
		int fin = 0;
		// System.out.println("Entrada " + message);
		for (int i = 0; i < message.length(); i++) {
			// System.out.println(message.charAt(i));
			if (message.charAt(i) == ' ') {
				fin = i;
				break;
			}
		}
		// System.out.println("Salida " + message.substring(ini, fin));
		return message.substring(ini, fin);
	}

	public void añadirRespuestasNormales() {
		respuestasNormales.add("💩 otra para ti");
		respuestasNormales.add("Puaj, que asco. A nadie de los que estamos aquí le importa cuándo haces caca");
		respuestasNormales.add("¿Has tenido que hacer mucha fuerza?");
		respuestasNormales.add("¿Que nota le pones a tu mojoncito?");
		respuestasNormales.add("Dura, blanda, como conguitos... Comparte con nosotros la consistencia de tu mierda");
		respuestasNormales.add("¡Directo al manzanares!");
		respuestasNormales.add("¿Has tirado de la cadena?");
		respuestasNormales.add("Yo que tu echaba un ojo por si el mojoncillo sigue ahí");
		respuestasNormales.add("Te acabas de quitar un peso de encima");
		respuestasNormales.add("Mejor fuera que dentro");
	}

	public void añadirRespuestasNocturnas() {
		respuestasNocturnas.add("Venga, y ahora a dormir eh, que no te vuelva a ver despierto");
		respuestasNocturnas.add("Te ha pillado a contrapié");
		respuestasNocturnas.add("¿Tu que haces despiert@ a estas horas?");
		respuestasNocturnas.add("Espero que estes bien, porque no son horas de hacer lo que acabas de hacer...");
		respuestasNocturnas.add("¿Seguro que has ido al baño a soltar carga o ha sido para disimular?");
		respuestasNocturnas.add("La del nuevo vladimir, una caca y a dormir");
		respuestasNocturnas.add("Shhhhhh, que el resto queremos dormir");
		respuestasNocturnas.add("Seguro que el mojon se acaba de ir a dormir");
		respuestasNocturnas.add("Ya no sabias ni como soltarlo jajaja");
		respuestasNocturnas
				.add("Espero que te haya pillado en tu casa, porque si no la gente tiene que estar flipando");
	}

	public void añadirRespuestasTempranas() {
		respuestasTempranas.add("¡Corre que no llegas!");
		respuestasTempranas.add("Sale antes que los que ponen las calles");
		respuestasTempranas.add("Que caca tan madrugadora");
		respuestasTempranas.add("¿Qué haces cagando? Si lo mas seguro que ni hayas desayunado");
		respuestasTempranas.add("Di que si, de buena mañana");
		respuestasTempranas.add("A quién madruga, Dios le ayuda para soltar la zurulla");
		respuestasTempranas.add("No se como has sido capaz, tu culo sigue dormido");
		respuestasTempranas.add("Limpiate bien, que a estas horas todavía no eres persona");
		respuestasTempranas.add("¡Mucha mierda!");
		respuestasTempranas.add("Has hecho bien, vas a afrontar el día mucho más liger@");
	}

	public void añadirRespuestasDeMediodia() {
		respuestasMediodia.add("Cafe y cigarro, muñeco de barro");
		respuestasMediodia.add("La de después de comer (o la de antes, no sabemos)");
		respuestasMediodia.add("Ahora una siestecita para asimilar el esfuerzo (siempre que puedas)");
		respuestasMediodia.add("Que habrás comido para que hayas soltado tremendo zurullo");
		respuestasMediodia.add("Si señor, ¡Como un reloj!");
		respuestasMediodia.add("Las mierda en punto");
		respuestasMediodia.add("Con esta, ¿Cuántas llevas en el día de hoy?");
		respuestasMediodia.add("Di que si, comenzando la tarde sin mierda en tu cuerpo");
		respuestasMediodia.add("La cagada más importante del día");
		respuestasMediodia.add("Acabas de recolocar todos tus chakras");
	}

	public boolean isAdmin(String user) {

		boolean is = false;

		if (user.equals(admin)) {
			is = true;
		}

		return is;
	}
}
