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

    public Juego(){
        tablero = new Tablero();
        turnoBlanco = true;
    }

    public void nuevoJuego(){
        tablero.reiniciarTablero();
        turnoBlanco = true;
    }

    public Tablero getTablero(){
        return tablero;
    }

    public boolean TurnoBlanco(){
        return turnoBlanco;
    }

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

        if(pieza.tipo == TipoPieza.PAWN && (hastaFila == 0 || hastaFila == 7)){
            TipoPieza promocionPeon = TipoPieza.QUEEN;
            tablero.setPieza(hastaFila, hastaColumna, new Pieza(promocionPeon, pieza.blanco));
        }

        turnoBlanco = !turnoBlanco;
        return true;
    }

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

    public boolean Jaque(boolean blanco){
        return jaqueEnTablero(tablero, blanco);
    }

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

    public EstadoJuego getEstadoJuego(){
        boolean enJaque = Jaque(turnoBlanco);
        boolean hayMovimientos = hayMovimientosLegales(turnoBlanco);

        if(enJaque && !hayMovimientos) return EstadoJuego.JAQUE_MATE;
        if(!enJaque && !hayMovimientos) return EstadoJuego.EMPATE;
        if(enJaque) return EstadoJuego.JAQUE;

        return EstadoJuego.JUGANDO;
    }

    public enum EstadoJuego {
        JUGANDO,
        JAQUE,
        JAQUE_MATE,
        EMPATE
    }
}