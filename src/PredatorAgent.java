
public class PredatorAgent extends GroundAgent {
	

	public PredatorAgent( int __x, int __y, World __w )
	{
		super(__x,__y,__w);
		
		this.N = (int) (Math.random()*100);
		this.L = (int) (Math.random()*300) +100;
		sightRadius = 20+(int)(Math.random()*5);
		idlespeedcpt = 1;
		idlespeed = 3;
		speedcpt = 1;
		speed = 15 + (int)(Math.random()*50);
		swimmingAbilities = (int)(Math.random()*5); // capacité à nager
		satiety = 0; // Lorsque la sati�t� est � 0, le pr�dateur chasse, sinon il se balade
	}
	
	public boolean isObstacleCellChasing(int _x, int _y){
		if(swimmingAbilities < 2){
			return isObstacleCell(_x,_y);
		}
		else{
			if (!_world.checkBoundsBool(_x, _y)) return true;
			else{ 
				return _world.altitude[_x][_y]<-0.0005*swimmingAbilities || _world.isCrater(_x, _y) ;
			}
		}
	}
	
	public void step( )
	{
		
		if( speedcpt%speed==0 ){
			speedcpt=1;
			return;
		}
		speedcpt++;
		
		if (L==0) {
			_alive=false;
			L--;
		}
		
		if(_world.isFireCell(_x, _y)||_world.isLava(_x, _y)){
			_alive=false;
		}
		
		if(_alive){
		L--;
		// met a jour l'agent
		//Predateur et proie sur la même case
		if(satiety<=0){
			for(int i=_world.agents.size()-1; i>=0 ; i--){
				if((_world.agents.get(i) instanceof PreyAgent) && _world.agents.get(i)._alive == true && _world.agents.get(i)._x == this._x && _world.agents.get(i)._y == this._y){
					_world.agents.remove(_world.agents.get(i));
					satiety = 150; // La sati�t� monte de 150 points lorsque le pr�dateur a mang� une proie, et d�croit de 1 � chaque it�ration
					this.L += 1000; // Il regagne 1000 point de vie
					_redValue = 100; // Sa couleur devient rouge pour le distinguer
					_greenValue = 0;
					_blueValue = 0;
				}
			}
		}
		
		//Reproduction : 
		
		boolean partnerpresence = false; double distance1 = Double.MAX_VALUE;
		if (N <= 0){ // Si N est à 0, agent en quête de reproduction
			for(int i=_world.agents.size()-1; i>=0 ; i--) { // Parcours de la liste des agents
				if((_world.agents.get(i) instanceof PredatorAgent) && _world.agents.get(i)._alive == true // Si l'élement de la liste est un predateur vivant						&& _world.agents.get(i)._x == _x && _world.agents.get(i)._y == _y
					&& _world.agents.get(i).N<=0 && _world.agents.get(i)._male == !this._male){
					PredatorAgent newborn = new PredatorAgent(this._x,this._y, _world);
					newborn.N = 50;
					newborn.sightRadius = (this.sightRadius+_world.agents.get(i).sightRadius)/2;
					newborn.speed = (this.speed+_world.agents.get(i).speed)/2;
					newborn.swimmingAbilities = (this.swimmingAbilities+_world.agents.get(i).swimmingAbilities)/2;
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
							if((_world.agents.get(i) instanceof PredatorAgent) && agent._alive == true // Si l'élement de la liste est un predateur vivant
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
		
		
		boolean preypresence = false; double distance = Double.MAX_VALUE;
		int xprey=_x, yprey=_y;
		// Déplacement si un prédateur voit une proie
		if(satiety == 0){
			for ( int k = - sightRadius ; k <= + sightRadius ; k++ ) { // Parcours des alentour du predateur
				for ( int l = - (sightRadius - Math.abs(k)) ; l <=  sightRadius - Math.abs(k) ; l++ ) {
					if(k==0&&l==0)continue;	
					for(int i=_world.agents.size()-1; i>=0 ; i--) { // Parcours de la liste des agents
						if((_world.agents.get(i) instanceof PreyAgent) && _world.agents.get(i)._alive == true // Si l'élement de la liste est une proie vivante
								&& _world.agents.get(i)._x == (k+_x+dx)%dx && _world.agents.get(i)._y == (l+_y+dy)%dy && !_world.isWaterCell(_world.agents.get(i)._x, _world.agents.get(i)._y)) // Si ses coordonnées correspondent à celles des alentours
						{ 
						preypresence = true;
						double thisdistance = Math.abs(k) + Math.abs(l);
						if(thisdistance>distance)break;
						distance = thisdistance;
						xprey =  _world.agents.get(i)._x;
						yprey =  _world.agents.get(i)._y;
						}
					}
				}
			}
		}
		
		if(preypresence){
            int[][] obstacleMap = new int[dx][dy];
            for(int i =0; i<dx; i++)
            	for(int j=0; j<dy; j++){
            		if(isObstacleCell(i,j))
            			obstacleMap[i][j] = 1;
            		else
            			obstacleMap[i][j] = 0;
            	}
            AreaMap map = new AreaMap(dx, dy, obstacleMap);
            AStarHeuristic heuristic = new ClosestHeuristic();
            AStar pathFinder = new AStar(map, heuristic);
            pathFinder.calcShortestPath(_x, _y, xprey, yprey);
            int direction = 5;
            if(pathFinder.getshortestPath()!=null)
            	direction = pathFinder.copyPathFirstStep(_x,_y,dx,dy); //   8 
            														   // 4   6 differentes directions renseign� par direction
            														   //   2 
            switch (direction){
            case 2:
            	_orient = 2;
            	break;
            case 4:
            	_orient = 3;
            	break;
            case 6:
            	_orient = 1;
            	break;
            case 8:
            	_orient = 0;
            	break;
            }
		}
		
		
		
		if(!preypresence||satiety>0){
			if(satiety==1){ // Redevient noir quand le pr�dateur a de nouveau faim
				_redValue = 0;
				_greenValue = 0;
				_blueValue = 0;
			}
			
			if(satiety!=0)
				satiety--;
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
		if(!preypresence&&!_world.isWaterCell(_x,_y)){
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
			while((_orient==0 && isObstacleCellChasing(_x, ( _y - 1 )))
					|| (_orient==1 && isObstacleCellChasing(( _x + 1 ),_y))
					|| (_orient==2 && isObstacleCellChasing(_x, ( _y + 1 )))
					|| (_orient==3 && isObstacleCellChasing(( _x - 1 ),_y))){
						
					if(nbchoixDirections>10
							||((_orient==0 && isObstacleCellChasing(_x, ( _y - 1 )))
							&& (_orient==1 && isObstacleCellChasing(( _x + 1 ),_y))
							&& (_orient==2 && isObstacleCellChasing(_x, ( _y + 1 )))
							&& (_orient==3 && isObstacleCellChasing(( _x - 1 ),_y)))){ // Si toutes les issues sont bloquées, reste sur place
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