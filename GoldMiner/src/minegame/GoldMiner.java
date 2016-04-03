package minegame;
import javax.swing.*;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
        add(stage);
        stage.start();
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
