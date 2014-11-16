
public class Whale extends MarineAgent{
	
	public Whale( int __x, int __y, World __w )
	{
		super(__x,__y,__w);
		
		_redValue = 128;
		_greenValue = 128;
		_blueValue = 128;
		
		this.N = (int) (Math.random()*500) +200;
		this.L = (int) (Math.random()*10000) +10000;
		sightRadius = 20;
		idlespeedcpt = 1;
		idlespeed = 5;
		satiety = 0;
	}
	public void step( )
	{
		if (L==0) {
			_alive=false;
			L--;
		}
		
		if(_alive){
		L--;
		// met a jour l'agent
		//Reproduction : 
		
		
		if(idlespeedcpt%idlespeed!=0){
			idlespeedcpt++;
			return;
		}
		else{
			idlespeedcpt = 1;
			if(Math.random()>0.9){
				if (Math.random() > 0.5) // au hasard
					_orient = (_orient + 1) % 9;
				else
					_orient = (_orient - 1 + 9) % 9;
				if(_orient==4){
					if(Math.random()>0.5)
						_orient++;
					else
						_orient--;
				}
			}
			
			
		}
		
		//Prise en compte des obstacles et modificiation de la trajectoire en fonction de ce paramètre :
		int nbchoixDirections=0;
		while(	   (_orient==0 && isObstacleCell(_x, ( _y - 1 + _world.getHeight() ) % _world.getHeight()))
				|| (_orient==1 && isObstacleCell(( _x + 1 + _world.getWidth() ) % _world.getWidth(),_y))
				|| (_orient==2 && isObstacleCell(_x, ( _y + 1 + _world.getHeight() ) % _world.getHeight()))
				|| (_orient==3 &&isObstacleCell(( _x - 1 + _world.getWidth() ) % _world.getWidth(),_y))
				|| (_orient==5 && isObstacleCell(( _x + 1 + _world.getWidth() ) % _world.getWidth(),( _y - 1 + _world.getHeight() ) % _world.getHeight()))
				|| (_orient==6 && isObstacleCell(( _x - 1 + _world.getWidth() ) % _world.getWidth(), ( _y - 1 + _world.getHeight() ) % _world.getHeight()))
				|| (_orient==7 &&isObstacleCell(( _x + 1 + _world.getWidth() ) % _world.getWidth(),( _y + 1 + _world.getHeight() ) % _world.getHeight()))
				|| (_orient==8 && isObstacleCell(( _x - 1 + _world.getWidth() ) % _world.getWidth(),( _y + 1 + _world.getHeight() ) % _world.getHeight()))){
					
				if(nbchoixDirections>10
						||((_orient==0 && isObstacleCell(_x, ( _y - 1 + _world.getHeight() ) % _world.getHeight()))
						&& (_orient==1 && isObstacleCell(( _x + 1 + _world.getWidth() ) % _world.getWidth(),_y))
						&& (_orient==2 && isObstacleCell(_x, ( _y + 1 + _world.getHeight() ) % _world.getHeight()))
						&& (_orient==3 && isObstacleCell(( _x - 1 + _world.getWidth() ) % _world.getWidth(),_y))
						&& (_orient==5 && isObstacleCell(( _x + 1 + _world.getWidth() ) % _world.getWidth(),( _y - 1 + _world.getHeight() ) % _world.getHeight()))
						&& (_orient==6 && isObstacleCell(( _x - 1 + _world.getWidth() ) % _world.getWidth(), ( _y - 1 + _world.getHeight() ) % _world.getHeight()))
						&& (_orient==7 &&isObstacleCell(( _x + 1 + _world.getWidth() ) % _world.getWidth(),( _y + 1 + _world.getHeight() ) % _world.getHeight()))
						&& (_orient==8 && isObstacleCell(( _x - 1 + _world.getWidth() ) % _world.getWidth(),( _y + 1 + _world.getHeight() ) % _world.getHeight())))){ // Si toutes les issues sont bloquées, reste sur place
						_orient=4;
						break;
				}
				int choix = (int)(Math.random()*8);
				nbchoixDirections++;
				if(choix==0){
					_orient=0;
				}
				if(choix==1){
					_orient=1;
				}
				if(choix==2){
					_orient=2;
				}
				if(choix==3){
					_orient=3;
				}
				if(choix==4){
					_orient=5;
				}
				if(choix==5){
					_orient=6;
				}
				if(choix==6){
					_orient=7;
				}
				if(choix==7){
					_orient=8;
				}
		}
		// met a jour: la position de l'agent (depend de l'orientation)
		 switch ( _orient ) 
		 {
         	case 0: // nord	
         		_y = ( _y - 1 + _world.getHeight() ) % _world.getHeight();
         		break;
         	case 1:	// est
         		_x = ( _x + 1 + _world.getWidth() ) % _world.getWidth();
 				break;
         	case 2:	// sud
         		_y = ( _y + 1 + _world.getHeight() ) % _world.getHeight();
 				break;
         	case 3:	// ouest
         		_x = ( _x - 1 + _world.getWidth() ) % _world.getWidth();
 				break;
         	case 4:
         		//no movement
         		break;
         	case 5: // nord est
         		_y = ( _y - 1 + _world.getHeight() ) % _world.getHeight();
         		_x = ( _x + 1 + _world.getWidth() ) % _world.getWidth();
    			break;
         	case 6: // nord ouest
         		_y = ( _y - 1 + _world.getHeight() ) % _world.getHeight();
         		_x = ( _x - 1 + _world.getWidth() ) % _world.getWidth();
    			break;
         	case 7: // sud est
         		_y = ( _y + 1 + _world.getHeight() ) % _world.getHeight();
         		_x = ( _x + 1 + _world.getWidth() ) % _world.getWidth();
    			break;
         	case 8: // sud ouest
         		_y = ( _y + 1 + _world.getHeight() ) % _world.getHeight();
         		_x = ( _x - 1 + _world.getWidth() ) % _world.getWidth();
    			break;
		 }
		 
		}
		else{
			_world.agents.remove(this);
		}
	}

}
