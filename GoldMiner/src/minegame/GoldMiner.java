package minegame;
import javax.swing.*;

import minegame.Stage.StageState;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
/**
 * Created by lzc on 4/2/16.
 */

public class GoldMiner extends JFrame{
    Stage stage;
    static final double TIME_STEP = 1.0; //单位事件步长
    static final double PERIOD = 20.0;

    public GoldMiner() throws IOException{
        setSize(800,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        stage = new Stage();
		stage.setFocusable(true);
		stage.requestFocusInWindow();
        stage.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_SPACE:
                        stage.hook.launch();
                        break;
                    case KeyEvent.VK_P:
                        stage.pause();
                        break;
                    case KeyEvent.VK_ESCAPE:
                        stage.configure();
                        break;
                }
            }
        });

        stage.addMouseListener(new MouseAdapter() {
                                   @Override
                                   public void mouseClicked(MouseEvent e) {
                                       super.mouseClicked(e);
                                       int x=e.getX(), y=e.getY();
                                       if(stage.stageState==Stage.StageState.MENU){

                                           if(x>340&&x<490 && y>200 && y<250){
                                               try{
                                                   stage.load(0);
                                                   stage.start();
                                               }catch (IOException e1){
                                                   e1.printStackTrace();
                                               }
                                           }else if(x>340&&x<490&& y>280 && y<330){
                                               dispose();
                                           }
                                       }else if(stage.stageState== Stage.StageState.GAME_OVER){
                                           int distance = (x-412)*(x-412)+(y-432)*(y-432);
                                           if(distance<1024){
                                               try{
                                                   stage.load(0);
                                                   stage.start();
                                                   stage.hook = new Hook(stage.width, 180);
                                               }catch (IOException e1){
                                                   e1.printStackTrace();
                                               }
                                           }

                                       }
                                   }
                               }
        );

        add(stage);
        stage.stageState= Stage.StageState.MENU;
        //stage.start();
    }

    public static void main(String[] args) throws IOException{
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
			        GoldMiner goldMiner = new GoldMiner();
			        goldMiner.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
    }

}
