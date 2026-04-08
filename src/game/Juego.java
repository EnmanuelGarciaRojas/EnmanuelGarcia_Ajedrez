package game;

import model.Tablero;

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
}
