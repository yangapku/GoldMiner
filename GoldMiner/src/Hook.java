import java.awt.*;

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
            weight += m.r * m.r * m.density;

            state = HookState.BACKWARD;
            return true;
        }else return false;
    }

    /*TODO:每次时间循环时更新钩子位置和角度, 速度与钩子重量有关; 判断是否抓到矿物 */
    void refresh(Stage stage){
        //System.out.println("Hook Fresh");
        switch (state){
            case WAIT:
                theta += Math.PI / GoldMiner.PERIOD;
                break;
            case FORWARD:
                /*TODO*/
                for(int i=0; i<stage.mineralList.size(); i++){
                    if(hookMineral(stage.mineralList.get(i))){
                        stage.mineralList.get(i).hooked(stage,i);
                        break;
                    }
                }
                break;
            case BACKWARD:break;
        }
    }

    /*TODO:画线, 钩子, 钩到的物体*/
    void paint(Graphics g){

    }

    void launch(){
        if(state==HookState.WAIT)
            state = HookState.FORWARD;
    }

    private static double distance(double x1, double y1, double x2, double y2){
        return Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
    }

}

