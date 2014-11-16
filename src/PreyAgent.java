
public class PreyAgent extends GroundAgent {

	
	public PreyAgent( int __x, int __y, World __w )
	{
		super(__x,__y,__w);
		
		_redValue = 0;
		_greenValue = 128;
		_blueValue = 255;
		if(Math.random()>0.5)
			_male=true;
		else
			_male=false;
		this.N = 0;
		this.L = (int) (Math.random()*200) +200;
		sightRadius = 10+(int)(Math.random()*25);
		speedcpt=1;
		speed = 3+(int)(Math.random()*20); // + speed est grand, plus rapide l'agent est
		idlespeedcpt = 1;
		idlespeed = 3;
		swimmingAbilities = (int)(Math.random()*5); // capacité à nager
		satiety = 0; // Si la satiete est � 0, la proie mange les plantes qu'elle voit
	}
	
	public boolean isObstacleCellChased(int _x, int _y){
		if(swimmingAbilities < 2){
			return isObstacleCell(_x,_y);
		}
		else{
			if (!_world.checkBoundsBool(_x, _y)) return true;
			else{ 
				return _world.altitude[_x][_y]<-0.001*swimmingAbilities || _world.isCrater(_x, _y) ;
			}
		}
	}
	
	public void step( )
	{
		// met a jour l'agent
		
		
		if( speedcpt%speed==0 ){
			speedcpt=1;
			return;
		}
		speedcpt++;
		L--;
		if (L==0) {
			_alive=false;
			
		}
		if(_world.isFireCell(_x, _y)||_world.isLava(_x, _y)){
			_alive=false;
		}
		
		if(_alive == true){
			
		//Reproduction
		boolean partnerpresence = false; double distance1 = Double.MAX_VALUE;
		if (N <= 0){ // Si N est à 0, agent en quête de reproduction
			for(int i=_world.agents.size()-1; i>=0 ; i--) { // Parcours de la liste des agents
				if((_world.agents.get(i) instanceof PreyAgent) && _world.agents.get(i)._alive == true // Si l'élement de la liste est un predateur vivant
					&& _world.agents.get(i)._x == _x && _world.agents.get(i)._y == _y
					&& _world.agents.get(i).N<=0 && _world.agents.get(i)._male == !this._male){
					PreyAgent newborn = new PreyAgent(this._x,this._y, _world);
					newborn.N = 50;
					newborn.sightRadius = (this.sightRadius+_world.agents.get(i).sightRadius)/2;
					newborn.speed = (this.speed+_world.agents.get(i).speed)/2;
					newborn.swimmingAbilities = (this.swimmingAbilities + _world.agents.get(i).swimmingAbilities)/2;
					_world.add(newborn);
					N=(int) (Math.random()*300) +100;
					_world.agents.get(i).N = (int) (Math.random()*300) +100;
					break;
				}
			}
			if(N <= 0){ // Si l'agent n'a pas trouvé de partenaire sur sa case
				
				// Déplacement si une proie une autre proie veut se reproduire
				for ( int k = - sightRadius ; k <= + sightRadius ; k++ ) { // Parcours des alentour de la proie
					for ( int l = - (sightRadius - Math.abs(k)) ; l <=  sightRadius - Math.abs(k) ; l++ ) {
						if(k==0&&l==0)continue;
						for(int i=_world.agents.size()-1; i>=0 ; i--) { // Parcours de la liste des agents
							Agent agent = _world.agents.get(i);
							if((_world.agents.get(i) instanceof PreyAgent) && agent._alive == true // Si l'élement de la liste est un predateur vivant
									&& agent._x == (k+_x+dx)%dx && agent._y == (l+_y+dy)%dy
									&& agent.N<=0 && agent._male == !this._male) // Si ses coordonnées correspondent à celles des alentours
							{ 
								partnerpresence = true;
								double thisdistance = Math.sqrt(Math.pow(k,2) + Math.pow(l,2)); // FAIRE LA DISTANCE DE MANHATTAN
								if(thisdistance>distance1)break;
								distance1 = thisdistance;
								if(l <= 0){
									if (Math.abs(k) < Math.abs(l)){
										_orient = 0;
										}
									else{
										if ( k > 0 ){
											_orient = 1;
											}
										else{
											_orient = 3;
											}
									}
								}
								else{
									if ( Math.abs(k) < Math.abs(l) ){
										_orient = 2;
									}
									else{
										if (k > 0){
											_orient = 1;
										}
										else{
											_orient = 3;
											}
									}
								}
							}
						}
					}
				}
			}	
		}
		else
			if(N>0)
				N--;
		
		// Mange une plante si il y en a une sur sa case :
		double distance2 = Double.MAX_VALUE;
		if(satiety<=0){
			if(_world.isPlantCell(_x,_y)){
				_world.setPlantCell(_x,_y,0);
				L+=1000;
				satiety+=100;
			}
			else{
				for ( int k = - sightRadius ; k <= + sightRadius ; k++ ) { // Parcours des alentour de la proie
					for ( int l = - (sightRadius - Math.abs(k)) ; l <=  sightRadius - Math.abs(k) ; l++ ) {
						if(k==0&&l==0)continue;
						if(_world.isPlantCell((k+_x+dx)%dx,(l+_y+dy)%dy)) // Si ses coordonnées correspondent à celles des alentours
						{ 
							double thisdistance = Math.sqrt(Math.pow(k,2) + Math.pow(l,2)); // FAIRE LA DISTANCE DE MANHATTAN
							if(thisdistance>distance2)break;
							distance2 = thisdistance;
							if(l <= 0){
								if (Math.abs(k) < Math.abs(l)){
									_orient = 0;
								}
								else{
									if ( k > 0 ){
										_orient = 1;
									}
									else{
										_orient = 3;
									}
								}
							}
							else
							{
								if ( Math.abs(k) < Math.abs(l) )
								{
									_orient = 2;
								}
								else
								{
									if (k > 0){
										_orient = 1;
									}
									else{
										_orient = 3;
									}
								}
							}
						}
					}
				}
			}
		}
		
		if(satiety >0) satiety --;
		
		boolean predpresence = false; double distance = 10000; //Distance qui détermine le plus proche predateur
		// Déplacement si une proie voit un predateur
		for ( int k = - sightRadius ; k <= + sightRadius ; k++ ) { // Parcours des alentour de la proie
			for ( int l = - (sightRadius - Math.abs(k)) ; l <=  sightRadius - Math.abs(k) ; l++ ) {
				if(k==0&&l==0)continue;
				for(int i=_world.agents.size()-1; i>=0 ; i--) { // Parcours de la liste des agents
					Agent agent = _world.agents.get(i);
					if((_world.agents.get(i) instanceof PredatorAgent) && agent._alive == true // Si l'élement de la liste est un predateur vivant
						&& agent._x == (k+_x+dx)%dx && agent._y == (l+_y+dy)%dy) // Si ses coordonnées correspondent à celles des alentours
						{ 
						predpresence = true;
						double thisdistance = Math.sqrt(Math.pow(k,2) + Math.pow(l,2)); // FAIRE LA DISTANCE DE MANHATTAN
						if(thisdistance>distance)break;
						distance = thisdistance;
						if(l <= 0){
							if (Math.abs(k) < Math.abs(l)){
								_orient = 2;
								}
							else{
								if ( k > 0 ){
									_orient = 3;
									}
								else{
									_orient = 1;
									}
							}
						}
						else{
							if ( Math.abs(k) < Math.abs(l) ){
								_orient = 0;
							}
							else{
								if (k > 0){
									_orient = 3;
								}
								else{
									_orient = 1;
									}
							}
						}
					}
				}
			}
		}
		
		
		if(!predpresence||_world.isWaterCell(_x,_y)){
			if(idlespeedcpt%idlespeed!=0){
				idlespeedcpt++;
				return;
			}
			else{
				idlespeedcpt = 1;
				if(Math.random()>0.8){
					if (Math.random() > 0.5) // au hasard
						_orient = (_orient + 1) % 4;
					else
						_orient = (_orient - 1 + 4) % 4;
				}
			}
		}
		
		//Prise en compte des obstacles et modificiation de la trajectoire en fonction de ce paramètre :
				if(!predpresence&&!_world.isWaterCell(_x,_y)){
					int nbchoixDirections=0;
					while((_orient==0 && isObstacleCell(_x, ( _y - 1 )))
							|| (_orient==1 && isObstacleCell(( _x + 1 ),_y))
							|| (_orient==2 && isObstacleCell(_x, ( _y + 1 )))
							|| (_orient==3 && isObstacleCell(( _x - 1 ),_y))){
								
							if(nbchoixDirections>10
									||((_orient==0 && isObstacleCell(_x, ( _y - 1 )))
									&& (_orient==1 && isObstacleCell(( _x + 1 ),_y))
									&& (_orient==2 && isObstacleCell(_x, ( _y + 1 )))
									&& (_orient==3 && isObstacleCell(( _x - 1 ),_y)))){ // Si toutes les issues sont bloquées, reste sur place
									_orient=4;
									break;
							}
							int choix = (int)(Math.random()*3);
							nbchoixDirections++;
							if(choix==0){
								_orient=(_orient+1)%4;
							}
							if(choix==1){
								_orient=(_orient+2)%4;
							}
							if(choix==2){
								_orient=(_orient+3)%4;
							}
					}
				}
				else{
					int nbchoixDirections=0;
					while((_orient==0 && isObstacleCellChased(_x, ( _y - 1 )))
							|| (_orient==1 && isObstacleCellChased(( _x + 1 ),_y))
							|| (_orient==2 && isObstacleCellChased(_x, ( _y + 1 )))
							|| (_orient==3 && isObstacleCellChased(( _x - 1 ),_y))){
								
							if(nbchoixDirections>10
									||((_orient==0 && isObstacleCellChased(_x, ( _y - 1 )))
									&& (_orient==1 && isObstacleCellChased(( _x + 1 ),_y))
									&& (_orient==2 && isObstacleCellChased(_x, ( _y + 1 )))
									&& (_orient==3 && isObstacleCellChased(( _x - 1 ),_y)))){ // Si toutes les issues sont bloquées, reste sur place
									_orient=4;
									break;
							}
							int choix = (int)(Math.random()*3);
							nbchoixDirections++;
							if(choix==0){
								_orient=(_orient+1)%4;
							}
							if(choix==1){
								_orient=(_orient+2)%4;
							}
							if(choix==2){
								_orient=(_orient+3)%4;
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
				 }
				 
				}
				else{
					_world.agents.remove(this);
				}
			}
			
		}