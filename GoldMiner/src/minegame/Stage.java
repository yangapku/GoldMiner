package minegame;

import javax.swing.*;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
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

    /*Updated by Yangan*/
    void load(int order) throws IOException {
    	int minercount=0;
        this.order = order;
        mineralList.clear();
        File filepath=new File("dat/stage"+order+".dat");
        InputStreamReader isr=new InputStreamReader(new FileInputStream(filepath));
        BufferedReader br=new BufferedReader(isr); //读入数据文件
        String linedata=br.readLine(); //读关卡时间和分数
        StringTokenizer st=new StringTokenizer(linedata,",");
        lifetime=Integer.parseInt(st.nextToken());
        requireScore=Integer.parseInt(st.nextToken());
        minercount=Integer.parseInt(st.nextToken());
        for(int i=0;i<minercount;i++){  //读矿物列表
        	linedata=br.readLine();
        	st=new StringTokenizer(linedata,",");  //格式为：矿物类型,x,y,r,value,density
        	String mineralType=st.nextToken();
        	double x=Double.parseDouble(st.nextToken());
        	double y=Double.parseDouble(st.nextToken());
        	double r=Double.parseDouble(st.nextToken());
        	int value=Integer.parseInt(st.nextToken());
        	int density=Integer.parseInt(st.nextToken());
        	switch(mineralType){  //根据不同的矿物类型建立不同对象
        		case "S":
        			mineralList.add(new Stone(x,y,r,value,density));
        			break;
        		case "G":
        			mineralList.add(new Gold(x,y,r,value,density));
        			break;
        		case "D":
        			mineralList.add(new Diamond(x,y,r,value,density));
        			break;
        	}
        }
        br.close();
        isr.close();
    }


    /* 注册键盘事件 */
    public Stage() throws IOException {
        /*测试时直接从第一关开始*/
        load(0);
        this.requestFocus();
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
    void next() throws IOException {
        if (score < requireScore) {
            gameOver();
        } else {
            order++;
            load(order);
            start();
        }
    }

    void refresh() throws IOException {
        if (stageState != StageState.PLAYING) return;

        if (lifetime <= 0) {
            timer.cancel();
            next();
        }
        lifetime--;

        /*for(int i=0; i<mineralList.size(); i++){
            mineralList.get(i).refresh();
        }*/
        hook.refresh(this);
        repaint();
    }

    /*public void paintComponent(Graphics g) {
        super.paint(g);
        try {
        	hook.paint(g);
        } catch (IOException IOEx) {
        	
        }
        for (Mineral m : mineralList) {
            m.paint(g);
        }
    }*/

    void start() {
        stageState = StageState.PLAYING;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
					refresh();
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
        }, 0, 100);
    }
    Image gameoverPic=Toolkit.getDefaultToolkit().createImage("res/images/gameover.jpg");
    Image gamebgPic=Toolkit.getDefaultToolkit().createImage("res/images/map_bg_0.png");
    @Override
    public void paint(Graphics g) {
        g.clearRect(0, 0, (int)width, (int)height);
        switch (stageState) {
            case PLAYING:
            	g.drawImage(gamebgPic,0,0,(int)width,(int)height,this);
                try {
                	hook.paint(g);
                } catch (IOException error) {}
                for (Mineral m : mineralList) {
                    m.paint(g);
                }
                g.setColor(Color.red);
                g.drawString("Remaining Time:"+(int)(lifetime/10.0)+" Score:"+score+" Goal:"+requireScore,0,15);
                break;
            case MENU:
                break;
            case PAUSE:
                break;
            case CONFIGURE:
                break;
            case GAME_OVER:
            	g.drawImage(gameoverPic,0,0,(int)width,(int)height,this);
                break;
        }
    }

}

