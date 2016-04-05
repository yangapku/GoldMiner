package minegame;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;

class Bomb {
	double x;
	double y;
	double r;
	Stage stage;
	
	Bomb(double x, double y, double r, Stage stage) {
		this.x = x;
		this.y = y;
		this.r = r;
		this.stage = stage;
	}
	
	void paint(Graphics g) {
		Image icon = new ImageIcon("res/images/mine_tnt.png").getImage();
		g.drawImage(icon, (int)(x-r), (int)(y-r), (int)(2*r), (int)(2*r), null);
	}
	void explode(Stage stage, int index) {
		for (int i = 0, len = stage.mineralList.size(); i < len; ++i) {
			if (distance(stage.mineralList.get(i).x, 
					stage.mineralList.get(i).y, x, y) < 10 * r) {
				stage.mineralList.remove(i);
				--len;
				--i;
			}
		}
		stage.bombList.remove(index);
	}
	private static double distance(double x1, double y1, double x2, double y2){
        return Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
    }
}
