package game;

import model.Tablero;
import model.Pieza;
import model.TipoPieza;
import util.Constantes;
import java.util.List;
import java.util.ArrayList;

//Logica principal del juego, gestiona las reglas del juego 
public class Juego {
    private Tablero tablero;
    private boolean turnoBlanco;

    //Constructor que inicializa el juego con un tablero vacio y turno de blancas
    public Juego(){
        tablero = new Tablero();
        turnoBlanco = true;
    }

    //Inicia una nueva partida reseteando el tablero y el turno
    public void nuevoJuego(){
        tablero.reiniciarTablero();
        turnoBlanco = true;
    }

    //Retorna el tablero actual del juego
    public Tablero getTablero(){
        return tablero;
    }

    //Indica si es turno de las blancas
    public boolean TurnoBlanco(){
        return turnoBlanco;
    }

    //Realiza un movimiento en el tablero. Retorna true si el movimiento fue valido.
    public boolean hacerMovimiento(int desdeFila, int desdeColumna, int hastaFila, int hastaColumna){
        Pieza pieza = tablero.getPieza(desdeFila, desdeColumna);
        if(pieza == null || pieza.blanco != turnoBlanco){
            return false;
        }

        List<int[]> movimientosLegales = getMovimientoLegales(desdeFila, desdeColumna);
        boolean valido = movimientosLegales.stream().anyMatch(m -> m[0] == hastaFila && m[1] == hastaColumna);
        if(!valido){
            return false;
        }

        tablero.moverPieza(desdeFila, desdeColumna, hastaFila, hastaColumna);

        //promocion de peon
        if(pieza.tipo == TipoPieza.PAWN && (hastaFila == 0 || hastaFila == 7)){
            TipoPieza promocionPeon = TipoPieza.QUEEN;
            tablero.setPieza(hastaFila, hastaColumna, new Pieza(promocionPeon, pieza.blanco));
        }

        turnoBlanco = !turnoBlanco;
        return true;
    }

    /* 
    *Obtiene todos los movimientos legales para una pieza en una posicion dada,
    *filtrando los que dejarian al rey en jaque
    */
    public List<int[]> getMovimientoLegales(int fila, int columna){
        Pieza pieza = tablero.getPieza(fila, columna);
        if(pieza == null){
            return List.of();
        }

        List<int[]> movimientos = getRawMoves(fila, columna);
        List<int[]> filtrar = new ArrayList<>();

        for(int[] movimiento : movimientos){
            Tablero copiar = tablero.copiarTablero();
            copiar.moverPieza(fila, columna, movimiento[0], movimiento[1]);
            if(!jaqueEnTablero(copiar, pieza.blanco)){
                filtrar.add(movimiento);
            }
        }

        return filtrar;
    }

    // Metodo para obtener movimientos sin procesar (sin validar jaque, etc.)
    private List<int[]> getRawMoves(int fila, int columna){
        Pieza pieza = tablero.getPieza(fila, columna);
        if(pieza == null){
            return List.of();
        }

        List<int[]> movimientos = new ArrayList<>();

        switch(pieza.tipo){
            case PAWN -> movimientosPeon(movimientos, fila, columna, pieza.blanco);
            case KNIGHT -> movimientosCaballo(movimientos, fila, columna, pieza.blanco);
            case BISHOP -> movimientosRayo(movimientos, fila, columna, new int[][]{{-1, -1}, {-1, 1}, {1, -1}, {1, 1}}, pieza.blanco);
            case ROOK -> movimientosRayo(movimientos, fila, columna, new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}}, pieza.blanco);
            case QUEEN -> movimientosRayo(movimientos, fila, columna, new int[][]{{-1, -1}, {-1, 1}, {1, -1}, {1, 1}, {-1, 0}, {1, 0}, {0, -1}, {0, 1}}, pieza.blanco);
            case KING -> movimientosRey(movimientos, fila, columna, pieza.blanco);
        }

        return movimientos;
    }

    //Agrega los movimientos posibles para un peon
    private void movimientosPeon(List<int[]> movimientos, int fila, int columna, boolean blanco){
        int direccion = blanco ? -1 : 1;
        int inicio = blanco ? 6 : 1;

        if(Tablero.posicionValida(fila + direccion, columna) && tablero.getPieza(fila + 2 * direccion, columna) == null){
            movimientos.add(new int[]{fila + direccion, columna});
            if(fila == inicio && Tablero.posicionValida(fila + 2 * direccion, columna) && tablero.getPieza(fila + 2 * direccion, columna) == null){
                movimientos.add(new int[]{fila + 2 * direccion, columna});
            }
        }

        for(int dc : new int[]{-1, 1}){
            int nf = fila + direccion, nc = columna + dc;
            if(Tablero.posicionValida(nf, nc)){
                Pieza objetivo = tablero.getPieza(nf, nc);
                if(objetivo != null && objetivo.blanco != blanco){
                    movimientos.add(new int[]{nf, nc});
                }
            }
        }
    }

    //Agrega los movimientos posibles para un caballo
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

    //Agrega movimientos en rayo para arfil, torre y reyna
    private void movimientosRayo(List<int[]> movimientos, int fila, int columna, int[][] direcciones, boolean blanco){
        for(int[] d : direcciones){
            int nf = fila + d[0], nc = columna + d[1];
            while(Tablero.posicionValida(nf, nc)){
                Pieza objetivo = tablero.getPieza(nf, nc);
                if(objetivo == null){
                    movimientos.add(new int[]{nf, nc});
                }else {
                    if(objetivo.blanco != blanco){
                        movimientos.add(new int[]{nf, nc});
                    }
                    break;
                }

                nf += d[0];
                nc += d[1];
            }
        }
    }

    //Agrega los movimientos posibles para el rey
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

    //Verifica si el rey del bando dado esta en jaque en el tablero actual
    public boolean Jaque(boolean blanco){
        return jaqueEnTablero(tablero, blanco);
    }

    //Verifica si el rey del bando dado esta en jaque en el tablero especificado
    private boolean jaqueEnTablero(Tablero comprobar, boolean blanco){
        int filaRey = -1, columnaRey = -1;
        for(int fila = 0; fila < Constantes.TAMANO_TABLERO; fila++){
            for(int columna = 0; columna < Constantes.TAMANO_TABLERO; columna++){
                Pieza pieza = comprobar.getPieza(fila, columna);
                if(pieza != null && pieza.tipo == TipoPieza.KING && pieza.blanco == blanco){
                    filaRey = fila;
                    columnaRey = columna;
                }
            }
        }

        if(filaRey == -1){
            return true;
        }

        for(int fila = 0; fila < Constantes.TAMANO_TABLERO; fila++){
            for(int columna = 0; columna < Constantes.TAMANO_TABLERO; columna++){
                Pieza pieza = comprobar.getPieza(fila, columna);
                if(pieza != null && pieza.blanco != blanco){
                    List<int[]> movimientoEnemigo = getRawMovesEnTablero(comprobar, fila, columna);
                    for(int[] movimiento : movimientoEnemigo){
                        if(movimiento[0] == filaRey && movimiento[1] == columnaRey){
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    //Obtiene los movimientos crudos en el tablero, sin cambiar el tablero actual
    private List<int[]> getRawMovesEnTablero(Tablero comprobar, int fila, int columna){
        Pieza pieza = comprobar.getPieza(fila, columna);
        if(pieza == null){
            return List.of();
        }

        Tablero original = tablero;
        tablero = comprobar;
        List<int[]> movimientos = getRawMoves(fila, columna);
        tablero = original;
        return movimientos;
    }

    //Verifica si hay algun movimiento legal disponibles para el bando dado
    public boolean hayMovimientosLegales(boolean blanco){
        for(int fila = 0; fila < Constantes.TAMANO_TABLERO; fila++){
            for(int columna = 0; columna < Constantes.TAMANO_TABLERO; columna++){
                Pieza pieza = tablero.getPieza(fila, columna);
                if(pieza != null && pieza.blanco == blanco && !getMovimientoLegales(fila, columna).isEmpty()){
                    return true;
                }
            }
        }

        return false;
    }

    //Obtiene el estado actual del juego
    public EstadoJuego getEstadoJuego(){
        boolean enJaque = Jaque(turnoBlanco);
        boolean hayMovimientos = hayMovimientosLegales(turnoBlanco);

        if(enJaque && !hayMovimientos) return EstadoJuego.JAQUE_MATE;
        if(!enJaque && !hayMovimientos) return EstadoJuego.EMPATE;
        if(enJaque) return EstadoJuego.JAQUE;

        return EstadoJuego.JUGANDO;
    }

    //Enumeracion que representa los posibles estados del juego
    public enum EstadoJuego {
        JUGANDO,
        JAQUE,
        JAQUE_MATE,
        EMPATE
    }
}