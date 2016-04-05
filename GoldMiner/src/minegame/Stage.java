package minegame;

import javax.swing.*;

import java.applet.Applet;
import java.applet.AudioClip;
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
    final int totalOrder = 5;

    /*关卡生成加载的变量*/
    int lifetime;
    int totaltime;
    int requireScore;
    List<Mineral> mineralList = new ArrayList<Mineral>();
    List<Bomb> bombList = new ArrayList<Bomb>();

    enum StageState {MENU, PLAYING, CONFIGURE, PAUSE, GAME_OVER}

    StageState stageState;

    int score;
    
    List<ExplodeEffect> explodeEffectList = new ArrayList<ExplodeEffect>();

    Hook hook = new Hook(width, 180);
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
        totaltime=lifetime;
        requireScore=Integer.parseInt(st.nextToken());
        minercount=Integer.parseInt(st.nextToken());
        for(int i=0;i<minercount;i++){  //读矿物列表
        	linedata=br.readLine();
        	st=new StringTokenizer(linedata,",");  //格式为：矿物类型,x,y,r,value,density
        	String mineralType=st.nextToken();
        	switch(mineralType){  //根据不同的矿物类型建立不同对象
        		case "S":
        		{
                	double x=Double.parseDouble(st.nextToken());
                	double y=Double.parseDouble(st.nextToken());
                	double r=Double.parseDouble(st.nextToken());
                	int value=Integer.parseInt(st.nextToken());
        			mineralList.add(new Stone(x,y,r,value));
        			break;
        		}
        		case "G":
        		{
                	double x=Double.parseDouble(st.nextToken());
                	double y=Double.parseDouble(st.nextToken());
                	double r=Double.parseDouble(st.nextToken());
                	int value=Integer.parseInt(st.nextToken());
        			mineralList.add(new Gold(x,y,r,value));
        			break;
        		}
        		case "D":
        		{
                	double x=Double.parseDouble(st.nextToken());
                	double y=Double.parseDouble(st.nextToken());
                	double r=Double.parseDouble(st.nextToken());
                	int value=Integer.parseInt(st.nextToken());
        			mineralList.add(new Diamond(x,y,r,value));
        			break;
        		}
        		case "B":
        		{
                	double x=Double.parseDouble(st.nextToken());
                	double y=Double.parseDouble(st.nextToken());
                	double r=Double.parseDouble(st.nextToken());
        			bombList.add(new Bomb(x,y,r,this));
        			break;
        		}
        		case "M":
        		{
        			double x=Double.parseDouble(st.nextToken());
                	double y=Double.parseDouble(st.nextToken());
                	double r=Double.parseDouble(st.nextToken());
                	int value=Integer.parseInt(st.nextToken());
                	int movingDirection=Integer.parseInt(st.nextToken());
                	double movingSpeed=Double.parseDouble(st.nextToken());
                	boolean withDiamond=st.nextToken().equals("D");
        			mineralList.add(new Mouse(x,y,r,value,
        					movingDirection,movingSpeed, withDiamond));
        			break;
        		}
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
    void next() throws IOException{
        if (score < requireScore) {
            gameOver();
        } else {
            order++;
            if (order < totalOrder) {
            	bombList.clear(); //有可能上一关的炸弹还没炸完
            	load(order);
            } else {
            	order = 0;
            	score = 0;
            	bombList.clear(); //有可能上一关的炸弹还没炸完
            	load(order);
            }
        }
    }

	void refresh() throws IOException {
        if (stageState != StageState.PLAYING) return;
        
        /*如果清空了就直接判断能否进入下一关*/
        if (mineralList.isEmpty() && ! hook.hasMineral()) {
        	next();
        }

        if (lifetime <= 0) {
            timer.cancel();
            next();
        }
        lifetime--;
        /*for(int i=0; i<mineralList.size(); i++){
            mineralList.get(i).refresh();
        }*/
        hook.refresh(this);
        for (Mineral i : mineralList) {
        	/*对老鼠来说需要更新其位置*/
        	if (i instanceof Mouse) {
        		((Mouse)i).runMouse();
        	}
        }
        
        if (lifetime == 100) {
        	Thread playSound = new Thread(new SoundPlayer("res/sounds/last-10s-sound.wav"));
        	playSound.start();
        }
        
        repaint();
    }

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
    Image scoreboard=Toolkit.getDefaultToolkit().createImage("res/images/scoreboard.png");
    Image gameoverPic=Toolkit.getDefaultToolkit().createImage("res/images/gameover.jpg");
    Image gamebgPic=Toolkit.getDefaultToolkit().createImage("res/images/map_bg_0.png");
    Image timeLineBg=Toolkit.getDefaultToolkit().createImage("res/images/timebg.png");
    Image timeLineGreen=Toolkit.getDefaultToolkit().createImage("res/images/timegood.png");
    Image timeLineRed=Toolkit.getDefaultToolkit().createImage("res/images/timepoor.png");
    Image timeLineCenter=Toolkit.getDefaultToolkit().createImage("res/images/timecenter.png");
    @Override
    public void paint(Graphics g) {
        g.clearRect(0, 0, (int)width, (int)height);
        double leftPercent=1.0;
		switch (stageState) {
            case PLAYING:
            	g.drawImage(gamebgPic,0,0,(int)width,(int)height,this);
            	g.drawImage(scoreboard,30,20,145,80,this);
            	g.setFont(new Font("Tahoma", Font.BOLD, 28));
            	g.setColor(Color.white);
            	g.drawString(""+requireScore,75,50);
            	g.drawString(""+score,75,90);
            	g.drawImage(timeLineBg,15,115,165,15,this);
            	//前两秒钟不改变进度比例以避免进度条异常运动
            	if(totaltime-lifetime>20)
            		leftPercent=(1.0*lifetime)/(1.0*totaltime);
        		g.drawImage(timeLineRed,(int)(20+165*(1.0-leftPercent)),115,180,130,
								(int)((1.0-leftPercent)*timeLineGreen.getWidth(this)),0,(int)timeLineGreen.getWidth(this),(int)timeLineGreen.getHeight(this),this);
            	if((1.0*lifetime)/(1.0*totaltime)>=0.3)
            		g.drawImage(timeLineGreen,(int)(20+165*(1.0-leftPercent)),115,180,130,
            						(int)((1.0-leftPercent)*timeLineGreen.getWidth(this)),0,(int)timeLineGreen.getWidth(this),(int)timeLineGreen.getHeight(this),this);
            	g.setColor(Color.black);
            	try {
                	hook.paint(g);
                } catch (IOException error) {}
                for (Mineral m : mineralList) {
                    m.paint(g);
                }
                for (Bomb b : bombList) {
                	b.paint(g);
                }
                for (int i=0, len = explodeEffectList.size(); i < len; ++i) {
                	if (explodeEffectList.get(i) != null) {
                    	explodeEffectList.get(i).paint(g);
                    	if (explodeEffectList.get(i).isEnd()) {
                        	explodeEffectList.remove(i);
                        	--i;
                        	--len;
                        }
                    }
                }
                           
                g.setColor(Color.red);
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

