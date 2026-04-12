# Juego de Ajedrez

## 1- Descripcion del proyecto
Este proyecto es una impleentación del juego de ajedrez para dos jugadores en la misma máquina. El juego sigue las reglas estándar del ajedrez, incluyendo movimientos válidos de todas las piezas, detección de jaque, jaque mate y tablas por ahogado. La interfaz grafica permite seleccionar piezas con el mouse, resaltar movimientos legales y alternar turnos entre blancas y negras. Incluye promoción automática de peones al llegar al extremo opuesto del tablero.

## 2- Funcionalidades implementadas 
- **Tablero visual**: Muestra un tablero de 8x8 con colores alternos y piezas representadas por imágenes.

- **Selección de piezas**: Clic en una pieza para seleccionarla y resaltar sus movimientos válidos.

- **Validación de movimientos**: Solo permite movimientos legales según las reglas de cada pieza(peón, torre, caballo. arfil reina y rey).

- **Alternancia de turnos**: Cambia automáticamente entre jugador blanco y negro.

- **Detección de jaque**: Indica visualmente cuando un rey está en jaque.

- **Detección de jaque mate y tablas**: Finaliza el juego cuando hay jaque mate o tablas por ahogado, mostrando el resultado.

- **Promoción de peón**: Al llegar a la octava fila, el peón se promueve automáticamente a reina.

- **Reiniciar partida**: Botón para iniciar nueva partida desde la posición inicial.

- **Menú inicial**: Pantalla de inicio con opciones para jugar y salir.

- **Faltante**: Función para realizar enrroque 

## 3- Requisitos previos
- **Java JDK**: Versión 11 o superior (recomendado JDK 17+).

- **JavaFX SDK**: Versión compatible con el JDK instalado (Descargable por separado).

- **IDE recomendado**: Visual Studio Code con extenciones de java, o Intellij IDEA.

## 4- Cómo ejecutar el proyecto
### Opción 1: Usando línea de comandos
1. Asegúrate de tener Java JDK y JavaFX instalados y configurados en el PATH.

2. Ve al directorio raíz del proyecto('c:\Tu_proyecto').

3. Compila el proyecto:

javac -cp "lib/*" src/**/*.java -d bin

4. Ejecuta la aplicación:

java -cp "bin;lib/*" --module-path "ruta\a\javafx\lib" --add-modules javafx.controls,javafx.fxml ui.App

(Reemplaza `ruta\a\javafx\lib` con la ruta real al directorio `lib` de JavaFX).

### Opción 2: Usando un IDE
1. Abre el proyecto en tu IDE (VS Code, Intellij).

2. Configura el classpath para incluir las librerias en `lib/ `.

3. Configura JavaFx en el IDE (agrega el módulo y la ruta a JavaFX)'

4. Ejecuta la clase principal `ui.App`.

## 5. Estructura del proyecto 
- `src/ui/App.java`: Clase principal de la interfaz gráfica, maneja la ventana, tablero y eventos de usuario.

- `src/game/ChessGame.java`: Lógica principal del juego, gestiona movimientos, estados del juego y reglas.

- `src/model/Board.java`: Representa el tablero y gestiona las posiciones de las piezas.

- `src/model/Piece.java`: Clase que representa una pieza individual con su tipo y color.

- `src/model/PieceType.java`: Enumeración de los tipos de piezas (rey, reina, etc.).

- `src/util/Constantes.java`: Constantes globales como tamaño del tablero y dimensiones.

- `images/`: Carpeta con las imágenes de las piezas de ajedrez.

- `lib/`: Librerías dependientes (JavaFX y otras).

- `bin/`: Carpeta de salida compilada.

## 6. Desiciones de diseño
- **Arquitectura**: Separación entre modelo (Tablero, Pieza), vista (Main.java con JavaFx) y controlador (Juego para la lógica).

- **Uso de JavaFX**: Lo elegi por su facilidad para crear interfaces gráficas en Java, con componentes con GridPane para el tablero y ImageView para las piezas.

- **Gestión de estado**: El estado del juego se mantiene en Juego, con métodos para verificar jaque y movimientos legales.

- **Validación de movimientos**: Implementada con simulación de movimientos para evitar jaques propios, asegurando reglas precisas.

- **Promoción automática**: Simplificada a reina para evitar complejidad adicional en la UI.

- **Colores  y tena**: Tema "Imagen de Bills" con colores oscuros para un aspecto moderno.

## 7. Autor 
Enmanuel García, 12 de abril de 2026.