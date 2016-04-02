import javax.swing.*;
import java.awt.*;
/**
 * Created by lzc on 4/2/16.
 */

public class GoldMiner extends JFrame{
    Stage stage;
    static final double TIME_STEP = 1.0; //单位事件步长
    static final double PERIOD = 100.0;

    public GoldMiner(){
        setSize(800,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        stage = new Stage();
        add(stage);
        setVisible(true);
        stage.start();
    }

    public static void main(String[] args){
        GoldMiner goldMiner = new GoldMiner();
    }

}
