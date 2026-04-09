package game;

import model.Tablero;
import model.Pieza;
import java.util.List;
import java.util.ArrayList;

//Logica principal del juego, gestiona las reglas del juego 
public class Juego {
    private Tablero tablero;

    public Juego(){
        tablero = new Tablero();
    }

    public void nuevoJuego(){
        tablero.reiniciarTablero();
    }

    public Tablero getTablero(){
        return tablero;
    }

    // Metodo para obtener movimientos sin procesar (sin validar jaque, etc.)
    private List<int[]> getRawMoves(int fila, int columna){
        Pieza pieza = tablero.getPieza(fila, columna);
        if(pieza == null){
            return List.of();
        }

        List<int[]> movimientos = new ArrayList<>();

        switch(pieza.tipo){
            case KNIGHT -> movimientosCaballo(movimientos, fila, columna, pieza.blanco);
            case KING -> movimientosRey(movimientos, fila, columna, pieza.blanco);
        }

        return movimientos;
    }

    private void movimientosCaballo(List<int[]> movimientos, int fila, int columna, boolean blanco){
        int [] df = {-2, -2, -1, -1, 1, 1, 2, 2};
        int [] dc = {-1, 1, -2, 2, -2, 2, -1, 1};

        for(int i = 0; i < df.length; i++){
            int nf = fila + df[i], nc = columna + dc[i];
            if(Tablero.posicionValida(nf, nc)){
                Pieza objetivo = tablero.getPieza(nf, nc);
                if(objetivo == null || objetivo.blanco != blanco){
                    movimientos.add(new int[]{nf, nc});
                }
            }
        }
    }

    private void movimientosRey(List<int[]> movimientos, int fila, int columna, boolean blanco){
        for(int df = -1; df <= 1; df++){
            for(int dc = -1; dc <= 1; dc++){
                if(df == 0 && dc == 0) continue;
                int nf = fila + df, nc = columna + dc;
                if(Tablero.posicionValida(nf, nc)){
                    Pieza objetivo = tablero.getPieza(nf, nc);
                    if(objetivo == null || objetivo.blanco != blanco){
                        movimientos.add(new int[]{nf, nc});
                    }
                }
            }
        }
    }
}