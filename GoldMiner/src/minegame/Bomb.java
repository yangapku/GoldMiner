package minegame;

import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

/**
 * Created by czj
 */

class Bomb {
	double x;
	double y;
	double r;
	Stage stage;
	List<Bomb> chainReaction = new ArrayList<Bomb>();
	boolean marked2Explode;
	
	Bomb(double x, double y, double r, Stage stage) {
		this.x = x;
		this.y = y;
		this.r = r;
		this.stage = stage;
		this.marked2Explode = false;
	}
	
	void paint(Graphics g) {
		Image icon = new ImageIcon("res/images/mine_tnt.png").getImage();
		g.drawImage(icon, (int)(x-r), (int)(y-r), 
				(int)(2*r), (int)(2*r), null);
	}
	void explode() {
		for (int i = 0, len = stage.mineralList.size(); i < len; ++i) {
			if (distance(stage.mineralList.get(i).x, 
					stage.mineralList.get(i).y, x, y) < 5 * r) {
				stage.mineralList.remove(i);
				--len;
				--i;
			}
		}
		
		/*播放声音*/
    	Thread playSound = new Thread(new SoundPlayer("res/sounds/explosive.wav")); 
		playSound.start(); //SoundPlayer定义在Hook.java中
		
		for (int i=0, len = stage.bombList.size(); i<len; ++i) {
			Bomb testBomb = stage.bombList.get(i);
			if(distance(x,y,testBomb.x, testBomb.y) < (r + testBomb.r)
					&& testBomb.marked2Explode == false) {
				stage.bombList.remove(i);
				len = stage.bombList.size();
				--i;
				testBomb.marked2Explode = true;
				chainReaction.add(testBomb);
				/* 利用一个List来储存要爆的炸弹，目的是避免递归引爆炸弹的时候造成bombList不可预计的变化，
				 * 比如后排元素因为前排的爆炸往前移，而下标i不能准确知道递归引爆后应该从哪里继续检查可爆炸弹，
				 * 从而miss掉一些应该爆掉的炸弹*/
			}
		}
		for (int i=0; i<chainReaction.size(); ++i) {
			chainReaction.get(i).explode();
		}
		chainReaction.clear();
		stage.explodeEffectList.add(new ExplodeEffect(x, y, r));
	}
	private static double distance(double x1, double y1, double x2, double y2){
        return Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
    }
}


class ExplodeEffect {
	double x;
	double y;
	double r;
	int effectCount;
	
	ExplodeEffect(double x, double y, double r) {
		this.x = x;
		this.y = y;
		this.r = 3 * r;
		this.effectCount = 1;
	}
	
	void paint(Graphics g) {
		Image icon = new ImageIcon("res/images/effect_blast_" 
				+ effectCount + ".png").getImage();
		g.drawImage(icon, (int)(x-r), (int)(y-r), (int)(3*r), (int)(3*r), null);
		++this.effectCount;
	}
	
	boolean isEnd() {
		return effectCount == 6; //一共5张图
	}
}