package minegame;

import java.awt.*;


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
    void refresh(){} //更新矿物位置(默认为空)

    /*被钩到时的事件, 默认情况为从矿物列表中清除该矿物; 可以重写增加音效,动画,以及炸药桶爆炸的特殊事件*/
    void hooked(Stage stage, int i){
        stage.mineralList.remove(i);
    }
}

class Stone extends Mineral{
	Stone(double x, double y, double r, int value, int density) {
		super(x, y, r, value, density);
	}
	void paint(Graphics g) {}
}

class Gold extends Mineral{
	Gold(double x, double y, double r, int value, int density) {
		super(x, y, r, value, density);
		// TODO 自动生成的构造函数存根
	}
	void paint(Graphics g) {}
}

class Diamond extends Mineral{
	Diamond(double x, double y, double r, int value, int density) {
		super(x, y, r, value, density);
		// TODO 自动生成的构造函数存根
	}
	void paint(Graphics g) {}
}

