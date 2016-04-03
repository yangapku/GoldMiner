package minegame;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


/**
 * Created by lzc on 4/2/16.
 * Updated by czj
 */
public class Hook {
    private double sourceX;
    private double sourceY;
    private double theta=0.0;
    private double d=0.0;
    final double r = 25.0;
    private double weight=500.0;

    private Mineral mineral;//钩到的物体

    HookState state;
    int hookWaitDirection = 1; //控制钩子晃动的方向

    enum HookState{WAIT, FORWARD, BACKWARD}

    public Hook(double width, double height){
        sourceX = width/2;
        sourceY = 180; //需要根据背景调节到合适的起始高度

        state = HookState.WAIT;
    }

    double getX(){
        return sourceX + d * Math.cos(theta);
    }

    double getY(){
        return sourceY + d * Math.sin(theta);
    }

    double getWeight(){
        return mineral == null ? weight : weight 
        		+ mineral.density * mineral.r * mineral.r;
    }

    /*分别计算拉的速度和放的速度（放的速度偏慢，不能用钩子的重量来算）*/
    double getPullVelocity(){
    	return 40000.0 / getWeight();
    }    
    double getPushVelocity(){
    	return 20.0;
    }
    
    boolean hasMineral() {
    	return mineral != null;
    }

    boolean hookMineral(Mineral m){
        if(distance(getX(),getY(),m.x,m.y) < (r+m.r)){
            mineral = m;

            state = HookState.BACKWARD;
            return true;
        }else return false;
    }

    
    /*每次时间循环时更新钩子位置和角度, 速度与钩子重量有关; 判断是否抓到矿物 */
    void refresh(Stage stage){
        switch (state){
            case WAIT:
            	theta += hookWaitDirection * Math.PI / GoldMiner.PERIOD;
            	
            	/*控制钩子的方向在0到PI之间，到达边界转向*/
            	if (theta >= Math.PI) {
            		hookWaitDirection = -1;
            	}
            	else if (theta <= 0) {
            		hookWaitDirection = 1;
            	}
                break;
                
            case FORWARD:
            	d += getPushVelocity();
            	
            	/*判断是否超出边界*/
            	if (getX() < 50 || getX() > 750 || getY() > 550) {
            		state = HookState.BACKWARD;
            		break;
            	}
            	
            	/*判断是否钩到物体*/
                for(int i=0; i<stage.mineralList.size(); i++){
                    if(hookMineral(stage.mineralList.get(i))){
                        stage.mineralList.get(i).hooked(stage,i);
                        break;
                    }
                }
                break;
                
            case BACKWARD:
            	d -= getPullVelocity();
            	
            	/*需要判断是超出边界造成的拉回还是钩到东西造成的拉回*/
            	if (mineral != null){
            		/*给钩到的东西加了个位移*/
            		mineral.refresh(getX() + r * Math.cos(theta), 
            				getY() + r * Math.sin(theta));
            	}
            	
            	/*到达后加分并回到等待状态*/
            	if (d <= 0){
            		if (mineral != null) {
            			stage.score += mineral.value;
            			mineral = null;
            		}
            		d = 0;
            		state = HookState.WAIT;
            	}
            	break;
        }
    }
    
    /*画线, 钩子, 钩到的物体*/
    void paint(Graphics g) throws IOException{
    	switch (state) {
    	case BACKWARD:
    		if (mineral != null){
    			mineral.paint(g);	//先画钩到的物体，再进入default画钩子和线
    		}    		
    	default:
    		/*画线*/
        	g.drawLine((int)sourceX, (int)(sourceY), (int)getX(), (int)getY());
    		/*画钩子*/
    		BufferedImage hookImage = ImageIO.read(new File("res/images/hook2.png"));
        	BufferedImage rotatedImage = rotateImage(hookImage, theta);
        	g.drawImage(rotatedImage,
        			(int)(getX() - r), (int)(getY() - r), 2*(int)r, 2*(int)r, null);
    	}    	
    }

    void launch(){
        if(state==HookState.WAIT)
            state = HookState.FORWARD;
    }

    private static double distance(double x1, double y1, double x2, double y2){
        return Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
    }
    
    /*旋转图片*/
    public static BufferedImage rotateImage(final BufferedImage bufferedimage,
            final double theta) {
        int w = bufferedimage.getWidth();
        int h = bufferedimage.getHeight();
        int type = bufferedimage.getColorModel().getTransparency();
        BufferedImage img;
        Graphics2D graphics2d;
        (graphics2d = (img = new BufferedImage(w, h, type))
                .createGraphics()).setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2d.rotate(theta, w / 2, h / 2);
        graphics2d.drawImage(bufferedimage, 0, 0, null);
        graphics2d.dispose();
        return img;
    }

}
