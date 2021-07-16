package brickbreaker;

import javafx.scene.paint.Color;
import javax.swing.JFrame;


public class BrickBreaker {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        JFrame obj= new JFrame();
        GamePlay gameplay=new GamePlay();
        obj.setBounds(10,10,700,600);
        obj.setTitle("Brick Breaker");
        obj.setResizable(false);
        obj.setVisible(true);
        obj.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        obj.add(gameplay);
        
    }
    
}


package brickbreaker;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JPanel;
import javax.swing.Timer;


public class GamePlay extends JPanel implements KeyListener, ActionListener {
    private boolean play=false;
    private int score=0;
    private int totalBricks=21;
    
    private Timer timer; //timer class
    private int delay= 8;
    
    //properties of X axis and Y axis  
    private int playerX= 310;
    private int ballposX=120;
    private int ballposY=350;
    private int balldicX=-1;
    private int balldicY=-2;
    
    //create variable for map generator class
    private MapGenerator map;
    
    //constructor 
    public GamePlay(){
        map=new MapGenerator(3,7);//create obj for map generator class
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        timer=new Timer(delay, this);
        timer.start();
    }
    
    //Create Function for graphics
    public void paint(Graphics g){
        
        
        //background
        g.setColor(Color.black);
        g.fillRect(1,1,692,592);
        
        //drawing map
        map.draw((Graphics2D)g);
        
        //borders
        g.setColor(Color.yellow);
        g.fillRect(0,0,3,592);
        g.fillRect(0,0,692,3);
        g.fillRect(691,0,3,592);
        
        //the paddle
        g.setColor(Color.blue);
        g.fillRect(playerX,550,100,10);
        
        //scores
        g.setColor(Color.white);
        g.setFont(new Font("Serif",Font.BOLD,32));
        g.drawString(""+ score, 590,30);
        
        //ball
        g.setColor(Color.green);
        g.fillOval(ballposX,ballposY,20,20);
        
        if(totalBricks <=0){
            play = false;
            balldicX = 0;
            balldicY = 0;
            g.setColor(Color.red);
            g.setFont(new Font("Serif",Font.BOLD,30));
            g.drawString("You Won",290,300);
            
            g.setFont(new Font("Serif", Font.BOLD,20));
            g.drawString("Press Enter to Restart",260,350);
        }
        
        if(ballposY > 570){
            play = false;
            balldicX = 0;
            balldicY = 0;
            g.setColor(Color.red);
            g.setFont(new Font("Serif",Font.BOLD,30));
            g.drawString("Game Over",280,300);
            
            g.setFont(new Font("Serif", Font.BOLD,20));
            g.drawString("Press Enter to Restart",260,350);
        }
        
        g.dispose();

        
    }

    @Override
    public void keyTyped(KeyEvent ke) {}
    @Override
    public void keyReleased(KeyEvent ke) {}


    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode()==KeyEvent.VK_RIGHT){
           if(playerX>=600){
               playerX=600;
           } 
           else{
               moveRight();
           }
        }
        if(e.getKeyCode()==KeyEvent.VK_LEFT){
           if(playerX<10){
               playerX=10;
           } 
           else{
               moveLeft();
           }
        }
        
        if(e.getKeyCode() == KeyEvent.VK_ENTER){
            if(!play){
                play=true;
                ballposX=120;
                ballposY=350;
                balldicX= -1;
                balldicY= -2;
                playerX=310;
                score = 0;
                totalBricks = 21;
                map = new MapGenerator(3,7);

                repaint();
            }
        }
  
    }
    
    public void moveRight(){
        play=true;
        playerX+=20; //press right to move +20 pixel to right
    }
    public void moveLeft(){
        play=true;
        playerX-=20; //press left to move -20 pixel to left
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        timer.start();
        
        //for intersecting ball and paddle
        if(play){
            if(new Rectangle(ballposX, ballposY,20,20).intersects(new Rectangle(playerX,550,100,8))){
                balldicY= -balldicY;
                
            }
            
            
            A:for(int i=0; i<map.map.length; i++){
                for(int j=0; j<map.map[0].length; j++){
                    if(map.map[i][j]>0){
                        int brickX = j*map.brickWidth+80;
                        int brickY= i*map.brickHeight+50;
                        int brickWidth=map.brickWidth;
                        int brickHeight=map.brickHeight;
                        
                        Rectangle rect= new Rectangle(brickX, brickY, brickWidth, brickHeight);
                        Rectangle ballRect = new Rectangle(ballposX, ballposY,20,20);
                        Rectangle brickRect= rect;
                        if(ballRect.intersects(brickRect)){
                            map.setBrickValue(0, i, j);
                            totalBricks--;
                            score+=5;
                            if(ballposX+19<=brickRect.x|| ballposX + 1 >= brickRect.x + brickRect.width){
                                balldicX=  -balldicX;
                            } else {
                                balldicY = -balldicY;
                            }
                            break A;
                        }
                    }
                    
                }
            }
            
            //for the movement of ball
            ballposX+= balldicX;
            ballposY+=balldicY;
            
            //for left border
            if(ballposX <0){
                balldicX= -balldicX;
            }
            
            //for top border
            if(ballposY<0){
                balldicY = -balldicY;
            }
            
            //for right border
            if(ballposX>670){
                balldicX = -balldicX;
            }
            
        }
        repaint(); //call the paint method 
  
    }

    
    
    
}
package brickbreaker;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class MapGenerator {
    
    public int map[][];
    public int brickWidth;
    public int brickHeight;
    
    //create constructor for recieving number of column and row
    public MapGenerator(int row,int col){
        map=new int[row][col];
        for(int i=0; i<map.length; i++){
            for(int j=0; j<map[0].length;j++){
                map[i][j]=1;
            }
        }
        brickWidth=540/col;
        brickHeight=150/row;
        
    }
    public void draw(Graphics2D g){
        
        //for draw a bricks at a particular position
        for(int i=0; i<map.length; i++){
            for(int j=0; j<map[0].length; j++){
                if(map[i][j]>0){
                g.setColor(Color.LIGHT_GRAY);
                g.fillRect(j*brickWidth +80, i*brickHeight+50, brickWidth, brickHeight);
                
                //draw brick borders
                g.setStroke(new BasicStroke(3));
                g.setColor(Color.black);
                g.drawRect(j*brickWidth +80, i*brickHeight+50, brickWidth, brickHeight);
            }
        }
    }
}
    //for value
    public void setBrickValue(int value, int row, int col){
        map[row][col]=value;
    }
}
