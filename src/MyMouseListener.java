import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MyMouseListener extends MouseAdapter {
	
	private World w;
	private ImageFrame imgfrm;
	
	public MyMouseListener(World world, ImageFrame im){
		
		this.w = world;
		this.imgfrm = im;
	}
	
    public void mouseClicked(MouseEvent me) {
    	
        Point clicked = me.getPoint();
        if(imgfrm.contains(clicked)){
        	
        	int x1 = clicked.x*w._dx/imgfrm.getWidth(); //coordonnées en prenant en compte la redimension
        	int y1 = clicked.y*w._dy/imgfrm.getHeight();
        	w.setFireCell(x1, y1);
        	if(w.isWaterCell(x1,y1)||w.isGrassCell(x1, y1)){
        		w.add(new Tornado(x1,y1,w));
        	}
        	if(w.isCrater(x1, y1)&&!w.eruptionOn){
    			w.startEruption();
    			w.lavaPlacement();
        	}
        	else if(w.isCrater(x1, y1)&&w.eruptionOn){
        		w.stopEruption();
        	}
        	

        }
    }


}
