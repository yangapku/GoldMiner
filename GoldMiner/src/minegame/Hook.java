package minegame;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;


/**
 * Created by lzc on 4/2/16.
 * Updated by czj
 */
public class Hook {
    private double sourceX; //悬挂点
    private double sourceY; //悬挂点
    private double theta=0.0; //角度
    private double d=0.0; //绳长
    final double r = 30.0; //钩子大小
    private double weight=800.0; //钩子本身的重量

    private Mineral mineral;//钩到的物体
    

    HookState state; //钩子的行进状态
    enum HookState{WAIT, FORWARD, BACKWARD}
    
    int hookWaitDirection = 1; //控制钩子晃动的方向

    public Hook(double width, double height){
        sourceX = width/2;
        sourceY = height; //需要根据背景调节到合适的起始高度

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
    /*TODO 计算速度的参数可以调整*/
    double getPullVelocity(){
    	return 40000.0 / getWeight();
    }    
    double getPushVelocity(){
    	return 20.0;
    }
    
    boolean hasMineral() {
    	return mineral != null;
    }

    /*逐个判断是否钩到物体*/
    /*TODO 钩子触发的范围可以调整*/
    boolean hookMineral(Mineral m){
        if(distance(getX(),getY(),m.x,m.y) < (r/2 + m.r)){
            mineral = m;
            state = HookState.BACKWARD;
            return true;
        } else {
        	return false;
        }
    }
    
    /*逐个判断是否触碰炸弹*/
    boolean explodeBomb(Bomb b){
    	if(distance(getX(), getY(), b.x, b.y) < (r/2 + b.r)){
    		state = HookState.BACKWARD;
    		return true;
    	} else {
    		return false;
    	}
    }
    
    /*每次时间循环时更新钩子位置和角度, 速度与钩子重量有关; 判断是否抓到矿物 */
    void refresh(Stage stage){
        switch (state){
            case WAIT:
            	theta += hookWaitDirection * Math.PI / GoldMiner.PERIOD;
            	
            	/*控制钩子的方向，到达边界转向*/
            	/*TODO 钩子晃动的范围可以调整*/
            	if (theta >= Math.PI * 9 / 10) {
            		hookWaitDirection = -1;
            	}
            	else if (theta <= Math.PI / 10) {
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
                    Mineral testMineral = stage.mineralList.get(i);
                	if(hookMineral(testMineral)){
                    	testMineral.hooked(stage,i);
                    	break;
                    }
                }
                
                /*判断是否碰到炸弹*/
                for(int i=0; i<stage.bombList.size(); ++i) {
                	Bomb testBomb = stage.bombList.get(i);
                	if (explodeBomb(testBomb)) {
                		testBomb.explode(stage, i);
                		/*播放声音*/
                    	Thread playSound = new Thread(new SoundPlayer("res/sounds/explosive.wav")); 
        				playSound.start();
        				break;
                	}
                }
                break;
                
            case BACKWARD:
            	d -= getPullVelocity();
            	
            	/*需要判断是超出边界造成的拉回还是钩到东西造成的拉回*/
            	if (mineral != null){
            		/*给钩到的东西加了个位移*/
            		/*TODO 加位移的多少可以调整*/
            		mineral.refresh(getX() + r * Math.cos(theta), 
            				getY() + r * Math.sin(theta));
            	}
            	
            	/*到达后加分并回到等待状态*/
            	/*TODO 判断高分物体和低分物体的界限可以调整*/
            	if (d <= 0){
            		if (mineral != null) {
            			stage.score += mineral.value;
            			
            			/*播放声音*/
            			String soundName; //指定播放声音文件名
            			if (mineral.value < 50) {
            				soundName = "res/sounds/low-value.wav";
            			} else if (mineral.value >= 300) {
            				soundName = "res/sounds/high-value.wav";
            			} else {
            				soundName = "res/sounds/normal-value.wav";
            			}
            			
            			/*在新线程中播放声音*/
            			Thread playSound = new Thread(new SoundPlayer(soundName)); 
        				playSound.start();
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
    			mineral.paint(g);	// 先画钩到的物体，再进入default画钩子和线
    		}    		
    	default:
    		/*画线*/
    		Graphics2D g2= (Graphics2D)g;
    		g2.setStroke(new BasicStroke(2.0f)); // 设置线条粗细
        	g2.drawLine((int)sourceX, (int)(sourceY), (int)getX(), (int)getY());
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

/*播放声音的类，在独立线程中执行*/
class SoundPlayer implements Runnable {
	private String soundName;
	
	SoundPlayer(String soundName) {
		this.soundName = soundName;
	}
	
	public void run() {
		final File file = new File(soundName);

        try {
            final AudioInputStream in = 
            		AudioSystem.getAudioInputStream(file);
            
            final int ch = in.getFormat().getChannels();  
            final float rate = in.getFormat().getSampleRate();  
            final AudioFormat outFormat = new AudioFormat(
            		AudioFormat.Encoding.PCM_SIGNED, rate,
            		16, ch, ch * 2, rate, false);
            
            final DataLine.Info info = 
            		new DataLine.Info(SourceDataLine.class, outFormat);
            final SourceDataLine line = 
            		(SourceDataLine) AudioSystem.getLine(info);

            if (line != null) {
                line.open(outFormat);
                line.start();
                final byte[] buffer = new byte[65536];  
                for (int n = 0; n != -1;
                		n = AudioSystem
                				.getAudioInputStream(outFormat, in)
                				.read(buffer, 0, buffer.length)) {  
                    line.write(buffer, 0, n);
                }
                line.drain();
                line.stop();
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
	}
}
