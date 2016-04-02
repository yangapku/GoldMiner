import java.awt.*;

/**
 * Created by lzc on 4/2/16.
 */
public abstract class Mineral {
    double x;
    double y;
    double r;
    int value;
    int density;
    abstract void paint(Graphics g);
    void refresh(){} //更新矿物位置(默认为空)

    /*被钩到时的事件, 默认情况为从矿物列表中清除该矿物; 可以重写增加音效,动画,以及炸药桶爆炸的特殊事件*/
    void hooked(Stage stage, int i){
        stage.mineralList.remove(i);
    }
}
