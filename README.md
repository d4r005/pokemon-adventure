# Pokémon Aventura: Todas las Regiones

Juego de aventura Pokémon para Android, hecho en **Kotlin nativo (Android Studio)**, con un
estilo libre inspirado en la línea *Leyendas* (Pokémon Legends: Arceus / Z-A):
mundo abierto por región, portales de distorsión para viajar y estilos de lucha
**Ágil** y **Fuerte** en combate.

## Características

- **9 regiones jugables**: Kanto, Johto, Hoenn, Sinnoh, Unova, Kalos, Alola, Galar y Paldea,
  cada una con su propia paleta, mapa, pueblo, niveles y tabla de encuentros.
- **Portales de distorsión**: desde el pueblo de cualquier región puedes viajar a todas las demás.
- **Comienzas en Kalos** (homenaje a Z-A) eligiendo inicial entre Bulbasaur, Charmander y Squirtle.
- **Combates por turnos** con tipos, eficacias, STAB, precisión y golpes críticos simulados.
- **Estilos de lucha estilo Leyendas**: Normal, Ágil (actúas primero, menos potencia) y Fuerte
  (máxima potencia, actúas al final).
- **Captura** con Poké Ball / Super Ball / Ultra Ball, experiencia, subida de nivel,
  aprendizaje de movimientos y caja cuando el equipo está lleno.
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

- [ ] Batallas de entrenadores y rival
- [ ] Distorsiones espaciotemporales con apariciones masivas (estilo Leyendas)
- [ ] Pokédex con registro de vistos/capturados
- [ ] Evoluciones por nivel
- [ ] Música y efectos de sonido
- [ ] Ciclo día/noche
- [ ] Misiones y objetos del mundo

## Aviso legal

Proyecto **fan game no oficial, sin ánimo de lucro y con fines educativos**.
Pokémon y sus criaturas son propiedad de Nintendo / Game Freak / Creatures Inc.
Todo el código y el arte de este proyecto son originales (dibujados por código);
no se incluyen sprites, música ni materiales de los juegos oficiales.
