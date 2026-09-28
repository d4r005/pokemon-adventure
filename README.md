# Pokémon Aventura: Todas las Regiones

Juego de aventura Pokémon para Android, hecho en **Kotlin nativo (Android Studio)**, con un
estilo libre inspirado en la línea *Leyendas* (Pokémon Legends: Arceus / Z-A):
mundo abierto por región, portales de distorsión para viajar y estilos de lucha
**Ágil** y **Fuerte** en combate.

## Características

- **9 regiones jugables**: Kanto, Johto, Hoenn, Sinnoh, Unova, Kalos, Alola, Galar y Paldea,
  cada una con su propia paleta, mapa, pueblo, niveles y tabla de encuentros.
- **Cada especie solo aparece en su región de origen**: las 82 especies de las
  9 generaciones son capturables en su región correspondiente.
- **Portales de distorsión**: desde el pueblo de cualquier región puedes viajar a todas las demás.
- **Comienzas en Kanto** con Pikachu como inicial, como en **Pokémon Amarillo**;
  además, tres NPCs del pueblo te regalan **Bulbasaur, Charmander y Squirtle**.
- **Combates por turnos** con tipos, eficacias, STAB, precisión y golpes críticos simulados.
- **Estilos de lucha estilo Leyendas**: Normal, Ágil (actúas primero, menos potencia) y Fuerte
  (máxima potencia, actúas al final).
- **Captura** con Poké Ball / Super Ball / Ultra Ball, experiencia, subida de nivel,
  aprendizaje de movimientos y caja cuando el equipo está lleno.
- **Evoluciones completas**: al alcanzar el nivel indicado, tus Pokémon evolucionan
  (Pikachu → Raichu, Bulbasaur → Ivysaur → Venusaur, Chikorita → Bayleef → Meganium,
  Magikarp → Gyarados, Dreepy → Dragapult, Rookidee → Corviknight, etc.).
- **TODOS los combates de entrenadores**, con el conteo literal de cada entrega
  (3,229 batallas en total). En cada región hay 5 célebres (rival, 2 líderes,
  Alto Mando y campeón), el jefe y 2 reclutas del **equipo villano** correspondiente
  (Rocket, Aqua/Magma, Galaxia, Plasma, Flare, Skull, Yell y Star)
  y **cientos de entrenadores de ruta** repartidos por mapas de 144x144,
  con niveles que crecen según la distancia al inicio, igual que en los juegos clásicos.
  No puedes huir de ellos ni robar sus Pokémon, y recuerdan si ya los venciste.

| Región | Juego de referencia | Combates originales | Batallas aquí |
|---|---|---|---|
| Kanto | FireRed/LeafGreen | ~458 | **458** |
| Johto | HeartGold/SoulSilver | ~400 | **400** |
| Hoenn | Emerald | ~350 | **350** |
| Sinnoh | Platinum | ~700 (con revanchas) | **700** |
| Unova | Black/White | 412 | **412** |
| Kalos | X/Y | 377 | **377** |
| Alola | Ultra Sun/Ultra Moon | ~250 | **250** |
| Galar | Sword/Shield | ~150 | **154** |
| Paldea | Scarlet/Violet | ~100-150 | **128** |

(cada región: 5 célebres + jefe villano + 2 reclutas + cientos de entrenadores de ruta,
generados de forma determinista por región)
- **Pokédex** con registro de vistos y capturados (82 especies de las 9 generaciones),
  accesible desde el menú de pausa.
- **Centro Pokémon** (curación gratuita) y **tienda** en cada pueblo, con dinero ganado en combates.
- **Guardado** de la partida en el dispositivo (JSON en SharedPreferences).
- **Todo el arte se dibuja por código** (Canvas): sin assets externos, APK ligero.
- Controles táctiles (D-pad virtual + botones) y también **teclado** (WASD/flechas, Enter, Esc)
  para emulador.

## Cómo compilar

1. Abre la carpeta del proyecto en **Android Studio** (File > Open).
2. Espera la sincronización de Gradle (descarga Gradle 8.2 y las dependencias automáticamente).
3. Conecta un dispositivo o emulador y pulsa **Run**.

Requisitos: Android Studio Hedgehog o superior, JDK 17, minSdk 24 (Android 7.0+), targetSdk 34.

Para compilar por consola (con el wrapper generado):

```bash
./gradlew assembleDebug
```

## Controles

| Acción | Táctil | Teclado |
|---|---|---|
| Moverse | D-pad virtual | WASD / flechas |
| Interactuar / aceptar | Botón A | Enter / Espacio |
| Menú de pausa | Botón MENÚ | Esc / M |
| Elegir acciones | Toque en los botones | Pantalla táctil |

## Estructura del código

```
app/src/main/java/com/dario/pokemonadventure/
├── MainActivity.kt          # Activity a pantalla completa
├── GameView.kt             # SurfaceView + hilo del juego + entrada
├── game/GameEngine.kt      # Máquina de estados, lógica del mundo, botones
├── data/PokemonData.kt     # Tipos, tabla de eficacias, movimientos, Pokédex
├── world/Region.kt        # Definición de las 9 regiones y el jugador
├── world/TileMap.kt       # Generación procedural de mapas y NPCs
├── battle/Battle.kt       # Sistema de combate y estilos
├── ui/                    # Renderizado del mundo, batalla y menús
└── save/SaveManager.kt    # Guardado y carga
```

## Hoja de ruta

- [x] Batallas de entrenadores y rival
- [x] Pokédex con registro de vistos/capturados
- [x] Evoluciones por nivel
- [ ] Distorsiones espaciotemporales con apariciones masivas (estilo Leyendas)
- [ ] Música y efectos de sonido
- [ ] Ciclo día/noche
- [ ] Misiones y objetos del mundo

## Aviso legal

Proyecto **fan game no oficial, sin ánimo de lucro y con fines educativos**.
Pokémon y sus criaturas son propiedad de Nintendo / Game Freak / Creatures Inc.
Todo el código y el arte de este proyecto son originales (dibujados por código);
no se incluyen sprites, música ni materiales de los juegos oficiales.
