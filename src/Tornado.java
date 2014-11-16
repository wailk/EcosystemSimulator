
public class Tornado extends Agent{
	
	public Tornado( int __x, int __y, World __w )
	{
		super(__x,__y,__w);
		
		_redValue = 100;
		_greenValue = 100;
		_blueValue = 100;
		L = 150;
		idlespeedcpt = 1;
		idlespeed = 3+(int)(Math.random()*5);
		tornadoRadius = 10;
	}
	
	public boolean isObstacleCell(int _x,int _y)
	{
		return false;
	}
	
	public void step( )
	{
		if (L==0) {
			_alive=false;
			L--;
		}
		
		if(_alive){
		L--;
		idlespeedcpt = 1;
		if(Math.random()>0.9){
			_orient = (int)(Math.random()*9);
			if(_orient==4){
				if(Math.random()>0.5)
					_orient++;
				else
					_orient--;
				}
			}	
		
		// Deplace des agents pris dans la tornade :
		
		for(int i=_world.agents.size()-1; i>=0 ; i--) {
			if(!(_world.agents.get(i) instanceof Tornado) && Math.abs(_world.agents.get(i)._x-_x)+Math.abs(_world.agents.get(i)._y-_y)<tornadoRadius)
			{
				Agent agent = _world.agents.get(i);
				int x,y;
				do
				{
					x = (int)(Math.random()*tornadoRadius*2) -tornadoRadius;
					y = (int)(Math.random()*tornadoRadius*2) -tornadoRadius;
				}
				while(!_world.checkBoundsBool(x+agent._x,y+agent._y-_y) || (x+agent._x-_x)*(x+agent._x-_x)+(y+agent._y-_y)*(y+agent._y-_y)>tornadoRadius*tornadoRadius-2*tornadoRadius);
				_world.agents.get(i)._x = x+agent._x;
				_world.agents.get(i)._y = y+agent._y;
			}
		}
		
		
		// deracinement des arbres pris dans la tornade :
		
		for(int i=-tornadoRadius ; i< tornadoRadius+1 ; i++){
			for(int j=-tornadoRadius ; j< tornadoRadius +1 ; j++)
			{
				if(_world.checkBoundsBool(i+_x,j+_y)&&((i)*(i)+(j)*(j)<tornadoRadius*tornadoRadius+tornadoRadius)&&_world.isTreeCell(i+_x, j+_y)&&Math.random()<0.4)
				{
					_world.setDirtCell(i+_x, j+_y, 1);
				}
			}
		}
		
		// met a jour: la position de l'agent (depend de l'orientation)
				 switch ( _orient ) 
				 {
		         	case 0: // nord	
		         		_y = ( _y - 1 );
		         		break;
		         	case 1:	// est
		         		_x = ( _x + 1 );
		 				break;
		         	case 2:	// sud
		         		_y = ( _y + 1 );
		 				break;
		         	case 3:	// ouest
		         		_x = ( _x - 1 );
		 				break;
		         	case 4:
		         		//no movement
		         		break;
		         	case 5: // nord est
		         		_y = ( _y - 1 );
		         		_x = ( _x + 1 );
		    			break;
		         	case 6: // nord ouest
		         		_y = ( _y - 1 );
		         		_x = ( _x - 1 );
		    			break;
		         	case 7: // sud est
		         		_y = ( _y + 1 );
		         		_x = ( _x + 1 );
		    			break;
		         	case 8: // sud ouest
		         		_y = ( _y + 1 );
		         		_x = ( _x - 1 );
		    			break;
				 }
				 
				 
				}
				else{
					_world.agents.remove(this);
				}
			}

}
