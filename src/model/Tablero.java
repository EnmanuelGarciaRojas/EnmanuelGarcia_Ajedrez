package model;

import util.Constantes;

//Gestiona el tablero del juego

public class Tablero {
    private Pieza[][] piezas;

    public Tablero(){
        //Inicializa el tablero con las piezas en su posicion inicial
        piezas = new Pieza[Constantes.TAMANO_TABLERO][Constantes.TAMANO_TABLERO];
        InicializarTablero();
    }
    
    private void InicializarTablero(){
        //Piezas negras(filas 0 y 1)
        piezas[0][0] = new Pieza(TipoPieza.ROOK, false);
        piezas[0][1] = new Pieza(TipoPieza.KNIGHT, false);
        piezas[0][2] = new Pieza(TipoPieza.BISHOP, false);
        piezas[0][3] = new Pieza(TipoPieza.QUEEN, false);
        piezas[0][4] = new Pieza(TipoPieza.KING, false);
        piezas[0][5] = new Pieza(TipoPieza.BISHOP, false);
        piezas[0][6] = new Pieza(TipoPieza.KNIGHT, false);
        piezas[0][7] = new Pieza(TipoPieza.ROOK, false);

        //Posicion de los peones negros
        for(int i = 0; i < Constantes.TAMANO_TABLERO; i++){
            piezas[1][i] = new Pieza(TipoPieza.PAWN, false);
        }

        //Piezas blancas(filas 6 y 7)
        piezas[7][0] = new Pieza(TipoPieza.ROOK, true);
        piezas[7][1] = new Pieza(TipoPieza.KNIGHT, true);
        piezas[7][2] = new Pieza(TipoPieza.BISHOP, true);
        piezas[7][3] = new Pieza(TipoPieza.QUEEN, true);
        piezas[7][4] = new Pieza(TipoPieza.KING, true);
        piezas[7][5] = new Pieza(TipoPieza.BISHOP, true);
        piezas[7][6] = new Pieza(TipoPieza.KNIGHT, true);
        piezas[7][7] = new Pieza(TipoPieza.ROOK, true);

        //Posicion de los peones blancos
        for(int i = 0; i < Constantes.TAMANO_TABLERO; i++){
            piezas[6][i] = new Pieza(TipoPieza.PAWN, true);
        }
    }

    //Devuelve el estado actual del tablero
    public Pieza[][] getPiezas(){
        return piezas;
    }

    //Verifica si una posicion dada esta dentro de los limites del tablero
    public static boolean posicionValida(int fila, int columna){
        return(fila >= 0 && fila < Constantes.TAMANO_TABLERO 
              && columna >= 0 && columna < Constantes.TAMANO_TABLERO);
    }

    public void reiniciarTablero(){
        piezas = new Pieza[Constantes.TAMANO_TABLERO][Constantes.TAMANO_TABLERO];
        InicializarTablero();
    }

    public Tablero copiarTablero(){
        Tablero copiar = new Tablero();

        //Limpia el tablero copiado para evitar la inicializacion estandar
        for(int i = 0; i < Constantes.TAMANO_TABLERO; i++){
            for(int j = 0; j < Constantes.TAMANO_TABLERO; j++){
                copiar.piezas[i][j] = null;
            }
        }

        //Copia las piezas del tablero original al tablero copiado
        for(int i = 0; i < Constantes.TAMANO_TABLERO; i++){
            for(int j = 0; j < Constantes.TAMANO_TABLERO; j++){
                if(piezas[i][j] != null){
                    copiar.piezas[i][j] = new Pieza(piezas[i][j].tipo, piezas[i][j].blanco);
                }
            }
        }
        return copiar;
    }

    public void limpiarTablero(int fila, int columna){
        if(posicionValida(fila, columna)){
            piezas[fila][columna] = null;
        }
    }

    public void moverPieza(int filaOrigen, int columnaOrigen, int filaDestino, int columnaDestino){
        if(posicionValida(filaOrigen, columnaOrigen) && posicionValida(filaDestino, columnaDestino)){
            piezas[filaDestino][columnaDestino] = piezas[filaOrigen][columnaOrigen];
            piezas[filaOrigen][columnaOrigen] = null;
        }
    }

    //Devuelve la pieza en la posicion dada, o null si la posicion es invalida
    public Pieza getPieza(int fila, int columna){
        if(!posicionValida(fila, columna)){
            return null;
        }
        return piezas[fila][columna];
    }

    //Coloca una pieza en la posicion dada, si la posicion es valida
    public void setPieza(int fila, int columna, Pieza pieza){
        if(posicionValida(fila, columna)){
            piezas[fila][columna] = pieza;
        }
    }
}