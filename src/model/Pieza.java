package model;

//Representacion de una pieza en el tablero

public class Pieza {
    public final TipoPieza tipo;
    public final boolean blanco;

    //Constructor para crear pieza
    public Pieza(TipoPieza tipo, boolean blanco){
        this.tipo = tipo;
        this.blanco = blanco;
    }

    /*Retorna el nombre del archivo de la imagen correspondiente a esta pieza.
    *Los archivos en la carpeta "images" son: B.PNG(blancos) y N.png(negros).
    */

    public String nombreImagen(){
        String color = blanco ? "B" : "N";
        return switch(tipo){
            case KING -> "Rey" + color + ".png";
            case QUEEN -> "Reyna" + color + ".png";
            case ROOK -> "Torre" + color + ".png";
            case BISHOP -> "Arfil" + color + ".png";
            case KNIGHT -> "Caballo" + color + ".png";
            case PAWN -> "Peon" + color + ".png";
        };
    }

    //Crea una copia de la pieza

    public Pieza copy(){
        return new Pieza(this.tipo, this.blanco);
    }
}