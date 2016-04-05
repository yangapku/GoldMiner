package minegame;

import java.awt.*;

import javax.swing.ImageIcon;


/**
 * Created by lzc on 4/2/16.
 * 杨安：为了构造不同类别的矿物对象简单定义了父类的构造方法以及子类
 */
public abstract class Mineral {
    double x;
    double y;
    double r;
    int value;
    int density;
    Mineral(double x,double y,double r,int value,int density){
    	this.x=x;
    	this.y=y;
    	this.r=r;
    	this.value=value;
    	this.density=density;
    }
    abstract void paint(Graphics g);
    
    /*加入了更新矿物位置的函数，在被勾住之后hook通过该函数使其位置保持同步*/
    void refresh(double newX, double newY) {
    	x = newX;
    	y = newY;
    }

    /*被钩到时的事件, 默认情况为从矿物列表中清除该矿物; 可以重写增加音效,动画,以及炸药桶爆炸的特殊事件*/
    void hooked(Stage stage, int i){
    	stage.mineralList.remove(i);
    }
}

class Stone extends Mineral{
	Stone(double x, double y, double r, int value) {
		super(x, y, r, value, 5);
	}
	void paint(Graphics g) {
		Image icon = new ImageIcon("res/images/mine_rock_b.png").getImage();
		g.drawImage(icon, (int)(x-r), (int)(y-r), (int)(2*r), (int)(2*r), null);
	}
}

class Gold extends Mineral{
	Gold(double x, double y, double r, int value) {
		super(x, y, r, value, 5);
		// TODO 自动生成的构造函数存根
	}
	void paint(Graphics g) {
		Image icon = new ImageIcon("res/images/mine_gold_b_1.png").getImage();
		g.drawImage(icon, (int)(x-r), (int)(y-r), (int)(2*r), (int)(2*r), null);
	}
}

class Diamond extends Mineral{
	Diamond(double x, double y, double r, int value) {
		super(x, y, r, value, 5);
		// TODO 自动生成的构造函数存根
	}
	void paint(Graphics g) {
		Image icon = new ImageIcon("res/images/mine_diamond_1.png").getImage();
		g.drawImage(icon, (int)(x-r), (int)(y-r), (int)(2*r), (int)(2*r), null);
	}
}

class Mouse extends Mineral {
	int movingDirection;
	double movingSpeed;
	int paintCount; //用来选择老鼠贴图，逐帧更换来制造动画效果
	boolean isHooked;
	boolean withDiamond; //用来区分带钻石的老鼠和不带钻石的老鼠
	
	Mouse(double x, double y, double r, int value,
			int movingDirection, double movingSpeed, boolean withDiamond) {
		super(x, y, r, value, 1);
		this.movingDirection = movingDirection;
		this.movingSpeed = movingSpeed;
		this.paintCount = 0;
		this.isHooked = false;
		this.withDiamond = withDiamond;
	}
	
	/*更新老鼠的位置*/
	void runMouse() {
		x += movingDirection * movingSpeed;
		if (x <= 0 || x >= 800) {
			movingDirection  = -movingDirection;
		}
	}
	
	void hooked(Stage stage, int i){
    	stage.mineralList.remove(i);
    	isHooked = true;
    }
	
	void paint(Graphics g) {
		String suffix;
		if (movingDirection > 0) {
			suffix = new String("_right.png");
		} else {
			suffix = new String("_left.png");
		}
		String prefix;
		if (withDiamond) {
			prefix = new String("res/images/WithDiamond_laoshuzou");
		} else {
			prefix = new String("res/images/laoshuzou");
		}
		Image icon = new ImageIcon(prefix + (paintCount+1) + suffix).getImage();
		
		/*只有没有被钩到的时候老鼠才会动*/
		if (!isHooked){
			paintCount += movingSpeed / 7 + 1; // 当老鼠走的速度比较快的时候腿也要动得快一些
			paintCount = paintCount % 4;
		}
		g.drawImage(icon, (int)(x-2*r), (int)(y-r), (int)(4*r), (int)(2*r), null);
	}
}
