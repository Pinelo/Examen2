package tiroparabolico;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

public class TiroParabolico extends JFrame implements Runnable, MouseListener, KeyListener {

    private Animacion animBalon; // Animacion del balon
    private Animacion cuadroCanasta; // Animacion de la canasta
    private Balon balon; // Objeto de la clase balon
    private Canasta canasta; // Objeto de la clase Canasta
    private long tiempoActual;  // tiempo actual
    private long tiempoInicial; // tiempo inicial
    private Image background; // Imagen de fondo de JFrame <-- Agregar Imagen
    private Image dbImage; // Imagen
    private Image gg; // Imagen de Game Over
    private Image ins; // Imagen de Instrucciones
    private Graphics dbg; // Objeto Grafico
    private int bVelx; // Velocidad en X del balon
    private int bVely; // Velocidad en Y del balon
    private int cMovx; // Movimiento en X de la canasta
    private int grav; // Gravedad
    private int vidas; // Vidas del usuario
    private int score; // Score del usuario
    private int dirCanasta; //Determina direccion para canasta
    private int iVelocidad; //Velocidad de la canasta
    private boolean click; // Booleano de click
    private boolean pausa; // Booleano de pausa
    private boolean instruc; // Booleano para desplegar instrucciones
    private boolean gameover; // Booleano para desplegar imagen gg
    private boolean mute; // Control de sonidos
    //private int score; // Puntaje del juego
    private int lives; // Vidas del jugador
    private int fouls; // Errores del jugador
    private Font myFont;
    private SoundClip fail;
    private SoundClip goal;
    private SoundClip over;
    private String datos;
    private String[] arr; // Arreglo de datos
    private String nombreArchivo;   //nomrbe de archivo donde se guardan las cosas

    /**
     * Constructor Se inicializan las variables
     */
    public TiroParabolico() {
        myFont = new Font("Serif", Font.BOLD, 30); // Estilo de fuente
        nombreArchivo = "datosJuego";
        pausa = false;
        mute = false;
        gameover = false;
        instruc = false;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1008, 758);
        click = false;
        setTitle("NBA Series!");
        dirCanasta = 0;     //La canasta no se mueve al principio
        iVelocidad = 8;     //Determina la velocidad de la canasta
        score = 0;
        lives = 5;
        fouls = 3;
        bVelx = 0;
        bVely = 0;
        grav = 1;
        vidas = 14;
        datos = "";
        background = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("images/nba.jpg"));
        ins = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("images/ins.jpg"));
        gg = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("images/gg.jpg"));

        // Carga las imagenes de la animacion del balon
        Image b0 = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("images/b0.png"));
        Image b1 = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("images/b1.png"));
        Image b2 = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("images/b2.png"));
        Image b3 = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("images/b3.png"));
        Image b4 = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("images/b4.png"));
        Image b5 = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("images/b5.png"));
        Image c = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("images/canasta.png"));

        // Se crea la animacion del balon
        animBalon = new Animacion();
        animBalon.sumaCuadro(b5, 100);
        animBalon.sumaCuadro(b4, 100);
        animBalon.sumaCuadro(b3, 100);
        animBalon.sumaCuadro(b2, 100);
        animBalon.sumaCuadro(b1, 100);
        animBalon.sumaCuadro(b0, 100);

        // Se crea la animacion de la canasta
        cuadroCanasta = new Animacion();
        cuadroCanasta.sumaCuadro(c, 200);

        // Balon
        balon = new Balon(100, 300, animBalon);

        //Canasta
        canasta = new Canasta(900, 680, cuadroCanasta);

        // Se cargan los sonidos
        fail = new SoundClip("sounds/boing2.wav");
        goal = new SoundClip("sounds/bloop_x.wav");
        over = new SoundClip("sounds/buzzer_x.wav");

        addMouseListener(this);
        //se agrega keylistener para poder detectar el teclado
        addKeyListener(this);
        Thread th = new Thread(this);
        th.start();
    }

    /**
     * Se ejecuta el Thread, el juego no continua si la pausa esta activada. El
     * juego finaliza si el numero de vidas en menor o igual que 0. El juego
     * tambien se pausa si el usuario desea ver las instrucciones.
     */
    public void run() {

        // Guarda el tiempo actual del sistema
        tiempoActual = System.currentTimeMillis();
        while (vidas >= 0) {
            checaColision();
            if(!pausa && !instruc) {
            actualiza();
            }
            repaint();
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                System.out.println("Error en " + ex.toString());
            }
        }
        if (vidas <= 0) {
            gameover = true;
            repaint();
        }
    }
    
    /*
        En este metodo se graba las variables del jeugo
    */
    public void grabaArchivo() throws IOException {
                                                          
                PrintWriter fileOut = new PrintWriter(new FileWriter(nombreArchivo));
                //se graban todas las variables
                    fileOut.println(click);
                    fileOut.println(vidas);
                    fileOut.println(score);
                    fileOut.println(lives);
                    fileOut.println(fouls);
                    fileOut.println(bVelx);
                    fileOut.println(bVely);
                    fileOut.println(grav);
                    fileOut.println(canasta.getPosX());
                    fileOut.println(canasta.getPosY());
                    fileOut.println(dirCanasta);
                    fileOut.println(iVelocidad);
                    fileOut.println(balon.getPosX());
                    fileOut.println(balon.getPosY());
                fileOut.close();
        }
    /*
        En este metodo se leen las variables grabadas
    */
    
    public void leeArchivo() throws IOException {
                                                          
                BufferedReader fileIn;
                fileIn = new BufferedReader(new FileReader(nombreArchivo));
                String dato = fileIn.readLine();
                //empieza a leer las variables
                click = Boolean.valueOf(dato);
                dato = fileIn.readLine();
                vidas = Integer.valueOf(dato);
                dato = fileIn.readLine();
                score = Integer.valueOf(dato);
                dato = fileIn.readLine();
                lives = Integer.valueOf(dato);
                dato = fileIn.readLine();
                fouls = Integer.valueOf(dato);
                dato = fileIn.readLine();
                bVelx = Integer.valueOf(dato);
                dato = fileIn.readLine();
                bVely = Integer.valueOf(dato);
                dato = fileIn.readLine();
                grav = Integer.valueOf(dato);
                dato = fileIn.readLine();
                canasta.setPosX(Integer.valueOf(dato));
                dato = fileIn.readLine();
                canasta.setPosY(Integer.valueOf(dato));
                dato = fileIn.readLine();
                dirCanasta = Integer.valueOf(dato);
                dato = fileIn.readLine();
                iVelocidad = Integer.valueOf(dato);
                dato = fileIn.readLine();
                balon.setPosX(Integer.valueOf(dato));
                dato = fileIn.readLine();
                balon.setPosX(Integer.valueOf(dato));
                            
             fileIn.close();
        }


    /**
     * En este metodo se actualiza las posiciones del balon y de la canasta.
     */
    public void actualiza() {
        grav = 6 - vidas / 3;
        if (click) {
            balon.setPosY(balon.getPosY() - bVely);
            bVely -= grav;
            balon.setPosX(balon.getPosX() + bVelx);
            long tiempoTranscurrido = System.currentTimeMillis() - tiempoActual;
            tiempoActual += tiempoTranscurrido;
            balon.getAnimacion().actualiza(tiempoTranscurrido);
        }
        
        if(dirCanasta == 1) {
            canasta.setPosX(canasta.getPosX() + iVelocidad);
        }
        else if(dirCanasta == 2) {
            canasta.setPosX(canasta.getPosX() - iVelocidad );
        }
    }
    
    public void keyPressed(KeyEvent e) {
        //Si se presiona la flecha derecha dirCanasta = 1
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            dirCanasta = 1;
        }
        //si se presiona la flecha de la izquierda, dirCanasta = 2
        else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            dirCanasta = 2;
        }
        //se cambia el estado de pausa al presionar p
        else if(e.getKeyCode() == KeyEvent.VK_P) {
            //no se peude poner pausa si ya estan las instrucciones
            if(!instruc) {
                pausa = !pausa;
            }
        }
        //se despliegan las instrucciones si se presiona la tecla I
        else if(e.getKeyCode() == KeyEvent.VK_I) {
            //no se pueden ver las instrucciones en pausa
            if(!pausa) {
                instruc = !instruc;
            }
        }
        //se guarda el juego al presionar 'g'
        else if(e.getKeyCode() == KeyEvent.VK_G) {
            if(!pausa && !instruc) {
                 try {
                     grabaArchivo();
                 } catch (IOException ex) {
                     
                 }
            }
        }
        // se cargra el juego grabado
        else if(e.getKeyCode() == KeyEvent.VK_C) {
            if(!pausa && !instruc) {
                try {
                    leeArchivo();
                } catch (IOException ex) {

                }
            }
            
        }
    }

    /**
     * Este metodo se encarga de cambiar las posiciones de lso objetos balon y
     * canasta cuando colisionan entre si.
     */
    public void checaColision() {

        // BALON VS JFRAME
        Rectangle cuadro = new Rectangle(0, 0, this.getWidth(), this.getHeight());
        if (!cuadro.intersects(balon.getPerimetro())) {
            bVelx = 0;
            bVely = 0;
            balon.setPosX(100);
            balon.setPosY(300);
            fouls--;
            if (fouls < 1) {
                lives--;
                fouls = 3;
            }
            vidas--;
            click = false;
            if (!mute) {
                fail.play();
            }
        }

        // CANASTA VS BALON
        if (canasta.getPerimetro().intersects(balon.getPerimetro())) {
            bVelx = 0;
            bVely = 0;
            balon.setPosX(100);
            balon.setPosY(300);
            score += 2;
            click = false;
            if (!mute) {
                goal.play();
            }
        }
        
        if(canasta.getPosX() <= getWidth()/2) {
            canasta.setPosX(getWidth()/2);
        }
        else if(canasta.getPosX()+canasta.getAncho() > getWidth()) {
            canasta.setPosX(getWidth() - canasta.getAncho());
        }

    }

    public void mouseReleased(MouseEvent e) {

    }

    /**
     * Evento que inicia el movimiento random del balon usando la bandera click.
     *
     * @param e Evento
     */
    public void mouseClicked(MouseEvent e) {
        if (!click) {
            if (balon.getPerimetro().contains(e.getPoint())) {
                click = true;
                bVely = (int) (Math.random() * (Math.sqrt(250 * 2 * grav) / 2)
                        + (Math.sqrt(250 * 2 * grav) / 2));

                bVelx = (int) ((((Math.random() * 500 / 2) + 250) * grav)
                        / (bVely * 2));
            }
        }
    }

    public void mousePressed(MouseEvent e) {

    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }


    /**
     * Metodo que actualiza las animaciones.
     *
     * @param g es la imagen del objeto
     */
    public void paint(Graphics g) {
        // Inicializa el DoubleBuffer
        if (dbImage == null) {
            dbImage = createImage(this.getSize().width, this.getSize().height);
            dbg = dbImage.getGraphics();
        }

        // Actualiza la imagen de fondo.
        dbg.setColor(getBackground());
        dbg.fillRect(0, 0, this.getSize().width, this.getSize().height);

        // Actualiza el Foreground.
        dbg.setColor(getForeground());
        paint1(dbg);

        // Dibuja la imagen actualizada
        g.drawImage(dbImage, 0, 0, this);
    }

    /**
     * Este metodo se encarga de pintar todos los objetos graficos del juego. Se
     * pintan los valores desplegados en el tablero
     *
     * @param g objeto grafico
     */
    public void paint1(Graphics g) {
        if(instruc == false && vidas >0) {
            g.drawImage(background, 0, 0, this);
            if (balon.getAnimacion() != null) {
            g.drawImage(balon.animacion.getImagen(), balon.getPosX(), balon.getPosY(), this);
        }
        if (canasta.getAnimacion() != null) {
            g.drawImage(canasta.animacion.getImagen(), canasta.getPosX(), canasta.getPosY(), this);
        }

        //-----IMPRESION DEL TABLERO
        g.setFont(myFont); // Aplica el estilo fuente a las string
        g.setColor(Color.yellow);
        g.drawString("" + score, 930, 98);
        g.setColor(Color.red);
        g.drawString("" + lives, 754, 99);
        g.drawString("" + fouls, 756, 178);
        }
        else if(vidas <= 0) {
            g.drawImage(gg, 0, 0, this);
        }
        else {
            g.drawImage(ins, 0, 0, this);
        }

    }

    public static void main(String[] args) {
        TiroParabolico tiro = new TiroParabolico();
        tiro.setVisible(true);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void keyReleased(KeyEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
