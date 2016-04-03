package minegame;

import javax.swing.*;

import java.awt.*;
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
        add(stage);
        setVisible(true);
        stage.start();
    }

    public static void main(String[] args) throws IOException{
        GoldMiner goldMiner = new GoldMiner();
    }

}
