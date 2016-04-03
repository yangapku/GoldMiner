package minegame;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;


/**
 * Created by lzc on 4/2/16.
 */
public class Hook {
    private double sourceX;
    private double sourceY;
    private double theta=0.0;
    private double d=0.0;
    final double r = 1.0;
    private double weight=0.0;

    private Mineral mineral;//钩到的物体

    HookState state;
    int hookWaitDirection = 1;

    enum HookState{WAIT, FORWARD, BACKWARD}

    public Hook(double width, double height){
        sourceX = width/2;
        sourceY = 0.0;

        state = HookState.WAIT;
    }

    double getX(){
        return sourceX + d * Math.cos(theta);
    }

    double getY(){
        return sourceY + d * Math.abs(Math.sin(theta));
    }

    double getWeight(){
        return mineral==null? weight: weight+ mineral.density * mineral.r * mineral.r;
    }


    /*TODO:根据钩子和矿物总重量给出钩子移速*/
    double getVelocity(){
        return 1.0 / (getWeight()+1.0);
    }

    boolean hookMineral(Mineral m){
        if(distance(getX(),getY(),m.x,m.y) < (r+m.r)){
            mineral = m;
            
            /*getWeight()的时候会加，这里不用加*/
            //weight += m.r * m.r * m.density;

            state = HookState.BACKWARD;
            return true;
        }else return false;
    }

    /*Updated by czj*/
    /*每次时间循环时更新钩子位置和角度, 速度与钩子重量有关; 判断是否抓到矿物 */
    void refresh(Stage stage){
        //System.out.println("Hook Fresh");
        switch (state){
            case WAIT:
            	theta += hookWaitDirection * Math.PI / GoldMiner.PERIOD;
            	if (theta >= Math.PI) {
            		hookWaitDirection = -1;
            	}
            	else if (theta <= 0) {
            		hookWaitDirection = 1;
            	}
                break;
            case FORWARD:
            	d += getVelocity();            	
                for(int i=0; i<stage.mineralList.size(); i++){
                    if(hookMineral(stage.mineralList.get(i))){
                        stage.mineralList.get(i).hooked(stage,i);
                        break;
                    }
                }
                break;
            case BACKWARD:
            	d -= getVelocity();
            	if (d <= 0){
            		stage.score += mineral.value;
            		d = 0;
            		mineral = null;
            		state = HookState.WAIT;
            	}
            	break;
        }
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
    
    /*Image转换成BufferedImage*/
    public static BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage)image;
         }

         image = new ImageIcon(image).getImage();

         BufferedImage bimage = null;
         GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
         try {
             int transparency = Transparency.OPAQUE;
             GraphicsDevice gs = ge.getDefaultScreenDevice();
             GraphicsConfiguration gc = gs.getDefaultConfiguration();
             bimage = gc.createCompatibleImage(
             image.getWidth(null), image.getHeight(null), transparency);
         } catch (HeadlessException e) {}
     
        if (bimage == null) {
            int type = BufferedImage.TYPE_INT_RGB;
            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        }

        Graphics g = bimage.createGraphics();

        g.drawImage(image, 0, 0, null);
        g.dispose();
     
        return bimage;
    }
    
    /*Updated by czj*/
    /*画线, 钩子, 钩到的物体*/
    void paint(Graphics g){
    	switch (state) {
    	case BACKWARD:
    		/*TODO:画钩到的物体*/    		
    	default:
    		/*画钩子*/
    		Image hookImage = new ImageIcon("res/images/gold.png").getImage();
        	BufferedImage rotatedImage = rotateImage(toBufferedImage(hookImage), theta);
        	g.drawImage(rotatedImage,
        			(int)getX() - rotatedImage.getWidth() / 2, (int)getY(), null);
        	/*画线*/
        	g.drawLine((int)sourceX, (int)sourceY, (int)getX(), (int)getY());
    	}    	
    }

    void launch(){
        if(state==HookState.WAIT)
            state = HookState.FORWARD;
    }

    private static double distance(double x1, double y1, double x2, double y2){
        return Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
    }

}
