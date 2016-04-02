import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lzc on 4/2/16.
 */
public class Stage extends JPanel {
    double width = 800;
    double height = 600;

    int order;//关数

    /*关卡生成加载的变量*/
    int lifetime;
    int requireScore;
    List<Mineral> mineralList = new ArrayList<Mineral>();

    enum StageState {MENU, PLAYING, CONFIGURE, PAUSE, GAME_OVER}

    StageState stageState;

    int score;

    Hook hook = new Hook(width, height);
    Timer timer;

    /*TODO*/
    void load(int order) {
        this.order = order;
        mineralList.clear();
        //TODO,加载关卡时间, 所需分数, 矿物列表
    }


    /* 注册键盘事件 */
    public Stage() {
        /*测试时直接从第一关开始*/
        load(0);

        this.requestFocus();
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_SPACE:
                        hook.launch();
                        break;
                    case KeyEvent.VK_P:
                        pause();
                        break;
                    case KeyEvent.VK_ESCAPE:
                        configure();
                        break;
                }
            }
        });
    }

    void pause() {
        if (stageState == StageState.PLAYING)
            stageState = StageState.PAUSE;
        else if (stageState == StageState.PAUSE) {
            stageState = StageState.PLAYING;
        }
    }

    void configure() {
        if (stageState == StageState.CONFIGURE)
            stageState = StageState.PLAYING;
        else if (stageState == StageState.PLAYING)
            stageState = StageState.CONFIGURE;
    }

    void gameOver() {
        stageState = StageState.GAME_OVER;
    }

    /*下一关*/
    void next() {
        if (score < requireScore) {
            gameOver();
        } else {
            order++;
            load(order);
            start();
        }
    }

    void refresh() {
        if (stageState != StageState.PLAYING) return;

        if (lifetime <= 0) {
            timer.cancel();
            next();
        }
        lifetime--;

        for(int i=0; i<mineralList.size(); i++){
            mineralList.get(i).refresh();
        }
        hook.refresh(this);

        repaint();
    }

    public void paintComponent(Graphics g) {
        super.paint(g);
        hook.paint(g);
        for (Mineral m : mineralList) {
            m.paint(g);
        }
    }

    void start() {
        stageState = StageState.PLAYING;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                refresh();
            }
        }, 0, 100);
    }

    @Override
    public void paint(Graphics g) {
        switch (stageState) {
            case PLAYING:
                hook.paint(g);
                for (Mineral m : mineralList) {
                    m.paint(g);
                }
                /*TODO: 绘制界面其它元素*/
                break;
            case MENU:
                break;
            case PAUSE:
                break;
            case CONFIGURE:
                break;
            case GAME_OVER:
                break;
        }
    }

}
