package flappyBird;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.Timer;

public class FlappyBird implements ActionListener, MouseListener, KeyListener {

    public static FlappyBird flappyBird;

    public final int WIDTH = 800, HEIGHT = 800;

    public Renderer renderer;

    public Rectangle bird;

    public int ticks, yMotion, score; //motions of bird's

    public ArrayList<Rectangle> columns;

    public boolean gameOver, started;

    public Random rand;

    public FlappyBird() {

        JFrame jframe = new JFrame();
        Timer timer = new Timer(20, this);

        renderer = new Renderer();
        rand = new Random();

        jframe.add(renderer);
        jframe.setTitle("A Crappy Bird");
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setSize(WIDTH, HEIGHT);
        jframe.addMouseListener(this);
        jframe.addKeyListener(this);
        jframe.setVisible(true);
        jframe.setResizable(false);

        bird = new Rectangle(WIDTH / 2 - 10, HEIGHT / 2 - 10, 20, 20); //Define the size of bird. Notice: In java the coordinate (0,0) is at the top left corner.
        columns = new ArrayList<Rectangle>();

        addColumn(true);
        addColumn(true);
        addColumn(true);
        addColumn(true);

        timer.start();
    }

    public void addColumn(boolean start) { //define the size of column
        int space = 300; //space between each column
        int width = 150; //the width of column
        int height = 50 + rand.nextInt(300); //the height of column, that varies from 50 to 350

        if (start) {
            columns.add(new Rectangle(WIDTH + width + columns.size() * 300,
                    HEIGHT - 120 - height, width, height)); //the bottom column
            columns.add(
                    new Rectangle(WIDTH + width + (columns.size() - 1) * 300, 0,
                            width, HEIGHT - space - height)); //the upper column
        } else {
            columns.add(new Rectangle(columns.get(columns.size() - 1).x + 600,
                    HEIGHT - 120 - height, width, height)); //the bottom column
            columns.add(new Rectangle(columns.get(columns.size() - 1).x, 0,
                    width, HEIGHT - space - height)); //the upper column
        }

    }

    public void paintColum(Graphics g, Rectangle column) {
        g.setColor(Color.green.darker().darker()); //the  color of column, pillars
        g.fillRect(column.x, column.y, column.width, column.height);
    }

    public void jump() {
        if (gameOver) {

            bird = new Rectangle(WIDTH / 2 - 10, HEIGHT / 2 - 10, 20, 20); //if game is over, reset bird to default position
            columns.clear();
            yMotion = 0; //control bird's vertical movement
            score = 0;

            addColumn(true);
            addColumn(true);
            addColumn(true);
            addColumn(true);

            gameOver = false;
        }

        if (!started) {
            started = true;
        } else if (!gameOver) {
            if (yMotion > 0) {//从设鸟的移动距离 in vertical
                yMotion = 0;
            }
            yMotion -= 10;//没点此鼠标，bird上升的距离
        }
    }

    public void actionPerformed(ActionEvent e) {

        int speed = 10;

        ticks++;

        if (started) {
            //????
            for (int i = 0; i < columns.size(); i++) {
                Rectangle column = columns.get(i);
                column.x -= speed;//bird的移动速度
            }

            //Not sure why I did this logic here
            if ((ticks % 2 == 0) && (yMotion < 15)) {
                yMotion += 2; //自然时间内，bird下落的距离量 
            }

            //????
            for (int i = 0; i < columns.size(); i++) {
                Rectangle column = columns.get(i);

                if (column.x + column.width < 0) {
                    columns.remove(column);

                    if (column.y == 0) {
                        addColumn(false);
                    }
                }
            }

            bird.y += yMotion; //

            for (Rectangle column : columns) {

                if (column.y == 0
                        && (bird.x + bird.width / 2 > column.x
                                + column.width / 2 - 5)
                        && (bird.x + bird.width / 2 < column.x
                                + column.width / 2 + 5)) {
                    score++;
                }

                if (column.intersects(bird)) {//如果鸟的坐标和柱子坐标相同
                    gameOver = true;
                    
                    if(bird.x<=column.x) {
                    bird.x = column.x - bird.width;//if game is over, bird will be remove with column
                    }else {
                        if(column.y!= 0) {
                            bird.y = column.y - bird.height;
                        }else if(bird.y<column.height) {
                            bird.y = column.height;
                        }
                    }
                }
            }

            if (bird.y > HEIGHT - 120 || bird.y < 0) {
                gameOver = true;
            }
            if (bird.y + yMotion >= HEIGHT - 120) {
                bird.y = HEIGHT - 120 - bird.height;//so bird won't fall out of Window, if bird ascend too fast 
            }

            renderer.repaint();
        }
    }

    public void repaint(Graphics g) {

        g.setColor(Color.CYAN); //the color of panel
        g.fillRect(0, 0, WIDTH, HEIGHT);

        g.setColor(Color.ORANGE); //the color of ground
        g.fillRect(0, HEIGHT - 120, WIDTH, 120);

        g.setColor(Color.green); //the color of grass
        g.fillRect(0, HEIGHT - 120, WIDTH, 20);

        g.setColor(Color.RED); //the color of bird
        g.fillOval(bird.x, bird.y, bird.width, bird.height);//(bird.x, bird.y) 是鸟的坐标, bird.width and bird height是鸟的size

        for (Rectangle column : columns) {
            paintColum(g, column);
        }
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", 1, 100));

        if (!started) {
            g.drawString("Click to Start!", 100, HEIGHT / 2 - 50); //The coordinate where we draw these Strings
        }
        if (gameOver) {
            g.drawString("Game Over!", 100, HEIGHT / 2 - 50);
        }
        if (!gameOver && started) {
            g.drawString(String.valueOf(score), WIDTH / 2 - 25, 100);
        }

    }

    public void mouseClicked(MouseEvent e) {
        jump();
    }
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            jump();
        }
    }

    public static void main(String[] args) {
        
        flappyBird = new FlappyBird();
        
    }
    
    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }


    public void keyTyped(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }

}
