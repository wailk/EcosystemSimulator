import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;

public class MyEcosystem_predprey extends CAtoolbox {
	
	int speed;


	public static void main(String[] args) {

		// initialisation generale
	    
		int dx = 200;
		int dy = 200;
		
		int displayWidth = dx;  // 200
		int displayHeight = dy; // 200

		// pick dimension for display
		if ( displayWidth < 200 )
			displayWidth = 200;
		else
			if ( displayWidth > 600 )
				displayWidth = 600;
			else
				if ( displayWidth < 300 )
					displayWidth = displayWidth * 2; 
		if ( displayHeight < 200 )
			displayHeight = 200;
		else
			if ( displayHeight > 600 )
				displayHeight = 600;
			else
				if ( displayHeight < 300 )
					displayHeight = displayHeight * 2; 
		
		
		//int delai = 1000;//100; // -- delay before refreshing display -- program is hold during delay, even if no screen update was requested. USE WITH CARE. 
		int nombreDePasMaximum = Integer.MAX_VALUE;
		int it = 0;
		int displaySpeed = 1;//50; // from 1 to ...
		
		CAImageBuffer image = new CAImageBuffer(dx,dy);
		ImageFrame imageFrame = new ImageFrame();
	    

	    /*Toolkit toolkit = Toolkit.getDefaultToolkit();
	    Image img = toolkit.getImage("cursor.png");
	    Cursor c = toolkit.createCustomCursor(img, new Point(imageFrame.getRootPane().getX(), imageFrame.getRootPane().getY()), "CursorMania");
	    imageFrame.getRootPane().setCursor(c);
	    */
	    // initialise l'ecosysteme
	    
		World world = new World(dx,dy,true,true);
		imageFrame =	ImageFrame.makeFrame( "My Ecosystem", image, 500, displayWidth, displayHeight,world);
		for ( int i = 0 ; i != 20 ; i++ ){
			int x=(int)(Math.random()*dx),y=(int)(Math.random()*dy);
			PredatorAgent pred = new PredatorAgent(x,y,world);
			while(pred.isInitObstacleCell(x,y)){
				x=(int)(Math.random()*dx);
				y=(int)(Math.random()*dy);
				pred._x=x;
				pred._y=y;
			}

			world.add(pred);
		}
		for ( int i = 0 ; i != 40 ; i++ ){
			int x=(int)(Math.random()*dx),y=(int)(Math.random()*dy);
			PreyAgent prey = new PreyAgent(x,y,world);
			while(prey.isInitObstacleCell(x,y)){
				x=(int)(Math.random()*dx);
				y=(int)(Math.random()*dy);
				prey._x=x;
				prey._y=y;
			}
			
			world.add(prey);
		}
		for (  int i = 0 ; i != 2 ; i++ ){
			int x=(int)(Math.random()*dx),y=(int)(Math.random()*dy);
			Whale w = new Whale(x,y,world);
			while(!world.isInitWaterCell(x,y)){
				x=(int)(Math.random()*dx);
				y=(int)(Math.random()*dy);
				w._x=x;
				w._y=y;
				
			}
			
			world.add(w);
		}
		for (  int i = 0 ; i != 20 ; i++ ){
			int x=(int)(Math.random()*dx),y=(int)(Math.random()*dy);
			Tuna t = new Tuna(x,y,world);
			while(!world.isInitWaterCell(x,y)){
				x=(int)(Math.random()*dx);
				y=(int)(Math.random()*dy);
				t._x=x;
				t._y=y;
				
			}
			
			world.add(t);
		}
		for(int i = 0; i != 7; i++)
			world.add(new Seagull((int)(Math.random()*dx),(int)(Math.random()*dy),world));
		

		
		MyMouseListener ML = new MyMouseListener(world,imageFrame);
		imageFrame.addMouseListener(ML);
		
		
	    // mise a jour de l'�tat du monde
		
		while ( it != nombreDePasMaximum )
		{
			// 1 - display
			
			if ( it % displaySpeed == 0 )
				world.display(image); 
			// 2 - update
						
			world.step();
						
			// 3 - iterate
			
			it++;
			
			imageFrame.updateLabels(world);
			
			try {
				Thread.sleep(imageFrame.getRefreshDelay());
			} catch (InterruptedException e) 
			{
			}
		}
		
	}

}
