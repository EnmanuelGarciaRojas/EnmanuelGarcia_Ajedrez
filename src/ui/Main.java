package ui;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import java.util.List;

import game.Juego;
import model.Pieza;
import util.Constantes;

/*
*Clase principal de la interfaz grafica del juego.
*Maneja la ventana, el tablero visual y las interacciones del usuario
*/
public class Main extends Application{
    private static final Color COLOR_CELDA_CLARA = Color.web("#c8e8ff", 0.55);
    private static final Color COLOR_CELDA_NEGRA = Color.web("#000010", 0.90);
    private static final Color COLOR_SELECCIONADO = Color.web("#00eaff", 0.75);
    private static final Color COLOR_MOVIMIENTO_LEGAL = Color.web("#39ff14", 0.70);

    private Juego juego;            //Logica del juego
    private GridPane tableroGrid;   //Panel del tablero   
    private StackPane[][] celdas;   //Celdas del tablero
    private Label turnoLabel;       //Label del turno
    private Label estadoLabel;      //Label de estado
    private BorderPane root;        //root de la escena

    private int filaSeleccionada = -1;
    private int columnaSeleccionada = -1;

    //Metodo principal de javaFX que inicia la aplicacion, configura la escena y muestra el menu inicial
    @Override
    public void start(Stage stage){
        juego = new Juego();
        celdas = new StackPane[Constantes.TAMANO_TABLERO][Constantes.TAMANO_TABLERO];
        root = new BorderPane();

        Scene scene = new Scene(root, Constantes.ANCHO_VENTANA, Constantes.ALTURA_VENTANA);
        scene.getStylesheets().add(getClass().getResource("/Tablero.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Juego de Ajedrez");

        mostrarMenu();
        stage.show();
    }

    //Muestra el menu inicial con opciones para jugar o salir
    private void mostrarMenu(){
        Label titulo = new Label("Ajedrez");
        titulo.getStyleClass().add("titulo-menu");

        Button jugar = new Button("Jugar");
        jugar.setOnAction(e -> mostrarTablero());

        Button salir = new Button("Salir");
        salir.setOnAction(e -> System.exit(0));

        VBox menu = new VBox(16, titulo, jugar, salir);
        menu.setAlignment(Pos.CENTER);
        menu.getStyleClass().add("menu-box");

        root.setCenter(menu);
        root.setTop(null);
        root.setBottom(null);
    }

    //Muestra el tablero de juego con pieza y controles
    private void mostrarTablero(){
        juego.nuevoJuego();
        tableroGrid = new GridPane();
        tableroGrid.setAlignment(Pos.CENTER);

        for(int fila = 0; fila < Constantes.TAMANO_TABLERO; fila++){
            for(int columna = 0; columna < Constantes.TAMANO_TABLERO; columna++){
                StackPane celda = crearCeldasTablero(fila, columna);
                celdas[fila][columna] = celda;
                tableroGrid.add(celda, columna, fila);
            }
        }

        renderizarTablero();

        Button reiniciar = new Button("Reiniciar partida");
        reiniciar.setOnAction(e -> mostrarTablero());

        Button menu = new Button("Menu");
        menu.setOnAction(e -> mostrarMenu());

        turnoLabel = new Label("Turno: Blancas");
        estadoLabel = new Label("Jugada nueva");

        BorderPane arriba = new BorderPane();
        arriba.setLeft(turnoLabel);
        arriba.setRight(estadoLabel);
        arriba.getStyleClass().add("barra-arriba");

        BorderPane abajo = new BorderPane();
        abajo.setLeft(menu);
        abajo.setCenter(reiniciar);
        abajo.getStyleClass().add("barra-abajo");

        root.setCenter(tableroGrid);
        root.setTop(arriba);
        root.setBottom(abajo);
    }

    //Crea una celda del tablero con rectangulo y vista de imagen
    private StackPane crearCeldasTablero(int fila, int columna){
        StackPane celda = new StackPane();

        Rectangle rect = new Rectangle(Constantes.TAMANO_CELDA, Constantes.TAMANO_CELDA);
        rect.setStroke(Color.TRANSPARENT);
        celda.getChildren().add(rect);

        ImageView verImagen = new ImageView();
        verImagen.setFitWidth(Constantes.TAMANO_PIEZA);
        verImagen.setFitHeight(Constantes.TAMANO_PIEZA);
        verImagen.setPreserveRatio(true);
        celda.getChildren().add(verImagen);

        final int f = fila;
        final int c = columna;
        celda.setOnMouseClicked(event -> clickEnCelda(f, c));

        return celda;
    }

    //Maneja el click en una celda del tablero seleccion, deseleccion y movimientos
    public void clickEnCelda(int fila, int columna){
        Pieza clickPieza = juego.getTablero().getPieza(fila, columna);

        if(filaSeleccionada == -1){
            if(clickPieza != null && clickPieza.blanco == juego.TurnoBlanco()){
                filaSeleccionada = fila;
                columnaSeleccionada = columna;
            }
        }else {
            if(filaSeleccionada == fila && columnaSeleccionada == columna){
                quitarSeleccion();
            }else if(clickPieza != null && clickPieza.blanco == juego.TurnoBlanco()){
                filaSeleccionada = fila;
                columnaSeleccionada = columna;
            }else {
                boolean mover = juego.hacerMovimiento(filaSeleccionada, columnaSeleccionada, fila, columna);
                quitarSeleccion();
                if(mover){
                    comprobarEstado();
                }
            }
        }

        renderizarTablero();
    }

    //Verifica el estado del jueg y actualiza la interfaz, muestra alertas para jaque, jaque mate o tablas
    private void comprobarEstado(){
        Juego.EstadoJuego estado = juego.getEstadoJuego();
        actualizarTurno();

        estadoLabel.getStyleClass().remove("label-jaque");

        switch(estado){
            case JAQUE_MATE -> {
                String ganador = juego.TurnoBlanco() ? "Negras" : "Blancas";
                String mensaje = "Jaque mate. " + ganador + " ganan";
                estadoLabel.setText(mensaje);
                estadoLabel.getStyleClass().add("label-jaque");
                mostrarAlerta(Alert.AlertType.INFORMATION, "Jaque mate", mensaje);
            }
            case EMPATE -> {
                estadoLabel.setText("Tablas por ahogado");
                mostrarAlerta(Alert.AlertType.INFORMATION, "Tablas", "Tablas por ahogado");
            }
            case JAQUE -> {
                String jugador = juego.TurnoBlanco() ? "Jugador blanco" : "Jugador negro";
                estadoLabel.setText("Jaque");
                estadoLabel.getStyleClass().add("Label-jaque");
                mostrarAlerta(Alert.AlertType.WARNING, "Jaque", "Jaque al " + jugador);
            }
            case JUGANDO -> {
                estadoLabel.setText("En juego");
            }
        }
    }

    //Actiualiza el Label que muestra el turno actual
    public void actualizarTurno(){
        turnoLabel.setText(juego.TurnoBlanco() ? "Turno: Blancas" : "Turno: Negras");
    }

    //Renderiza todo el tablero actualizando cada celda
    private void renderizarTablero(){
        for(int fila = 0; fila < Constantes.TAMANO_TABLERO; fila++){
            for(int columna = 0; columna < Constantes.TAMANO_TABLERO; columna++){
                actualizarCelda(fila, columna);
            }
        }
    }

    //Actualiza el contenido visual de una celda especifica
    private void actualizarCelda(int fila, int columna){
        StackPane celda = celdas[fila][columna];
        Rectangle rect = (Rectangle) celda.getChildren().get(0);
        ImageView verImagen = (ImageView) celda.getChildren().get(1);

        rect.setFill((fila + columna) % 2 == 0 ? COLOR_CELDA_CLARA : COLOR_CELDA_NEGRA);
        
        if(filaSeleccionada == fila && columnaSeleccionada == columna){
            rect.setFill(COLOR_SELECCIONADO);
        }

        //Resaltar movimientos legales de la pieza seleccionada
        if(filaSeleccionada != -1){
            List<int[]> movimientosLegales = juego.getMovimientoLegales(filaSeleccionada, columnaSeleccionada);
            for(int[] movimiento : movimientosLegales){
                if(movimiento[0] == fila && movimiento[1] == columna){
                    rect.setFill(COLOR_MOVIMIENTO_LEGAL);
                }
            }
        }

        Pieza p = juego.getTablero().getPieza(fila, columna);
        if(p != null){
            try{
                Image imagen = new Image("file:images/" + p.nombreImagen());
                verImagen.setImage(imagen);
            }catch(Exception e){
                verImagen.setImage(null);
            }
        }else{
            verImagen.setImage(null);
        }
    }

    //Deselecciona cualquier celda seleccionada
    public void quitarSeleccion(){
        filaSeleccionada = -1;
        columnaSeleccionada = -1;
    }

    //Muestra un dialogo de alerta con el tema aplicado
    
    public void mostrarAlerta(Alert.AlertType tipo, String titulo, String contenido){
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(contenido);

        alerta.getDialogPane().getStylesheets().add(
            getClass().getResource("/Tablero.css").toExternalForm()
        );
        alerta.showAndWait();
    }
    public static void main(String[] args){
        launch(args);
    }
}