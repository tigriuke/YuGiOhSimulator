# Simulador de Fase de Batalla — Duelo de Monstruos

Aplicación de escritorio en Java (Swing) que simula la **Fase de Batalla** de un
juego de cartas de monstruos con las reglas de cálculo de daño de Yu-Gi-Oh!:
enfrenta monstruos, calcula el daño, descuenta los Puntos de Vida (LP) y guarda
cada ataque en `historial_combate.txt`.

Examen final — Programación Orientada a Objetos, Universidad Autónoma de Occidente.

## Requisitos

- JDK 17 o superior.
- NetBeans con soporte Maven (JUnit 5 se descarga automáticamente al compilar).

## Cómo ejecutar

1. En NetBeans: **File → Open Project…** y seleccionar esta carpeta.
2. **Run Project (F6)**. La clase principal es `principal.Main`.
3. **Test Project (Alt+F6)** ejecuta las pruebas JUnit (o `mvn test` en consola).

Uso: escriba los nombres de los dos jugadores y pulse **Iniciar duelo**; invoque
monstruos (nombre, ATQ, DEF, posición, tipo y dueño); elija atacante y objetivo y
pulse **¡Atacar!**. **Terminar turno** le cede el turno al rival.

## Estructura (MVC)

```
src/main/java
├── modelo/        Reglas del juego y persistencia (sin dependencias de Swing)
├── vista/         VistaDuelo: JFrame Form (VistaDuelo.java + VistaDuelo.form)
├── controlador/   ControladorDuelo: conecta los eventos de la vista con el modelo
└── principal/     Main: arma el MVC y genera los reportes
src/test/java/modelo   Pruebas unitarias JUnit 5 del paquete modelo
docs/                  Diagrama de clases (draw.io, PNG y PDF)
historial_combate.txt  Historial generado por la aplicación
```

| Clase | Rol |
|---|---|
| `Monstruo` (abstracta) | ATQ, DEF, posición y control de ataques por turno |
| `MonstruoNormal`, `MonstruoEfecto` | Subclases; el de efecto tiene daño perforante |
| `Posicion` (enum) | ATAQUE / DEFENSA |
| `Jugador` | LP (8000 iniciales) y campo de hasta 5 monstruos |
| `ReglasBatalla` (interfaz) | Contrato del cálculo de daño |
| `CalculadoraDanio` (final) | Implementa la tabla oficial de daño |
| `ResultadoAtaque` | Resultado matemático de una batalla |
| `Duelo` | Turnos, ataques, fin del duelo e historial |
| `RegistroAtaque` | Una línea del historial de combate |
| `HistorialArchivo` | Escritura y lectura de `historial_combate.txt` |
| `DueloException` | Excepción propia para acciones que violan las reglas |

## Reglas de cálculo de daño

| Situación | Resultado |
|---|---|
| ATQ vs ATQ, atacante mayor | Defensor destruido; el rival pierde la diferencia en LP |
| ATQ vs ATQ, atacante menor | Atacante destruido; el atacante pierde la diferencia |
| ATQ vs ATQ, empate | Ambos destruidos, sin daño (con 0 ATQ ninguno se destruye) |
| ATQ vs DEF, ATQ mayor | Defensor destruido, sin daño (con daño perforante, el rival pierde la diferencia) |
| ATQ vs DEF, ATQ menor | Nadie es destruido; el atacante pierde la diferencia |
| ATQ vs DEF, empate | No pasa nada |
| Ataque directo (rival sin monstruos) | El rival pierde el ATQ completo |

Además: solo atacan los monstruos en posición de ataque, cada monstruo ataca una
vez por turno, los LP nunca bajan de 0 y el duelo termina cuando un jugador llega
a 0 LP.

## Historial de combate

Cada ataque se agrega a `historial_combate.txt` (en la carpeta del proyecto) con
el formato:

```
turno;jugador_atacante;monstruo_atacante;defensor;daño;jugador_que_recibe_daño;resultado
```

Al iniciar y al cerrar la aplicación, `Main` imprime en consola dos reportes
sobre ese historial: daño recibido por jugador y los ataques con más daño.
