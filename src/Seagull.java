
public class Seagull extends FlyingAgent{
	
	
	public Seagull( int __x, int __y, World __w )
	{
		super(__x,__y,__w);
		
		_redValue = 240;
		_greenValue = 230;
		_blueValue = 250;
		this.altitude = ImprovedNoise.maxValue+Math.random();
		this.N = (int) (Math.random()*100)+100;
		this.L = (int) (Math.random()*300) +200;
		if(Math.random()>0.5)
			_male=true;
		else
			_male=false;
		if(_male){
			_redValue -=70;
			_greenValue -=70;
			_blueValue -=70;
		}
		sightRadius = 30 + (int)(Math.random()*20);
		idlespeedcpt = 1;
		idlespeed = 1 + (int)(Math.random()*2);
		speedcpt = 1;
		speed = 10 + (int)(Math.random()*100);
		satiety = 0;
		cptAnimation = 0;
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
		
		if(_alive){
		L--;
		// met a jour l'agent
		//Reproduction : 
		cptAnimation = (cptAnimation+1)%4;
		
		double distance1 = Double.MAX_VALUE;
		if (N <= 0){ // Si N est à 0, agent en quête de reproduction
			for(int i=_world.agents.size()-1; i>=0 ; i--) { // Parcours de la liste des agents
				if((_world.agents.get(i) instanceof Seagull) && _world.agents.get(i)._alive == true // Si l'élement de la liste est un partenaire vivant
					&& _world.agents.get(i)._x == _x && _world.agents.get(i)._y == _y
					&& _world.agents.get(i).N<=0 && _world.agents.get(i)._male == !this._male){
					Seagull newborn = new Seagull(this._x,this._y, _world);
					newborn.N = 150;
					newborn.sightRadius = (this.sightRadius+_world.agents.get(i).sightRadius)/2;
					newborn.idlespeed = (this.idlespeed+_world.agents.get(i).idlespeed)/2;
					newborn.speed = (this.speed+_world.agents.get(i).speed)/2;
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
						if(k==0&&l==0||!_world.checkBoundsBool(k+_x,l+_y))continue;
						for(int i=_world.agents.size()-1; i>=0 ; i--) { // Parcours de la liste des agents
							Agent agent = _world.agents.get(i);
							if((_world.agents.get(i) instanceof Seagull) && agent._alive == true // Si l'élement de la liste est un predateur vivant
									&& agent._x == (k+_x+dx)%dx && agent._y == (l+_y+dy)%dy
									&& agent.N<=0 && agent._male == !this._male) // Si ses coordonnées correspondent à celles des alentours
							{ 
								double thisdistance = Math.sqrt(Math.pow(k,2) + Math.pow(l,2)); // distance euclidienne
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
		
		
		
		
		//Predateur et proie sur la même case
		if(satiety<=0){
			for(int i=_world.agents.size()-1; i>=0 ; i--){
				if((_world.agents.get(i) instanceof Tuna) && _world.agents.get(i)._alive == true && _world.agents.get(i)._x == this._x && _world.agents.get(i)._y == this._y && this.altitude<=0){
					_world.agents.remove(_world.agents.get(i));
					satiety = 600; // La sati�t� monte de 600 points lorsque le pr�dateur a mang� une proie, et d�croit de 1 � chaque it�ration
					this.L += 1000; // Il regagne 1000 point de vie
				}
			}
		}
		
		
		boolean preypresence = false; double distance = 10000;
		// Déplacement si un prédateur voit une proie
		if(satiety == 0){
		for ( int k = - sightRadius ; k <= + sightRadius ; k++ ) { // Parcours des alentour du predateur
			for ( int l = - (sightRadius - Math.abs(k)) ; l <=  sightRadius - Math.abs(k) ; l++ ) {
				if( !_world.checkBoundsBool(k+_x, l+_y))continue; // si le champ de vision sort de la map
				for(int i=_world.agents.size()-1; i>=0 ; i--) { // Parcours de la liste des agents
					if((_world.agents.get(i) instanceof Tuna) && _world.agents.get(i)._alive == true // Si l'élement de la liste est une proie vivante
						&& _world.agents.get(i)._x == (k+_x+dx)%dx && _world.agents.get(i)._y == (l+_y+dy)%dy) // Si ses coordonnées correspondent à celles des alentours
						{ 
						preypresence = true;
						double thisdistance = Math.sqrt(Math.pow(k,2) + Math.pow(l,2));
						if(thisdistance>distance)break;
						distance = thisdistance;
						if(this.altitude>0)
							this.altitude-=0.04;
						else
							this.altitude=0;
						if(l <= 0){ // Si la proie est dans la partie nord
							if (Math.abs(k) < Math.abs(l)){ // Si elle est plus au nord que sur les côtés
								_orient = 0;
							}
							else{
								if ( k > 0 ){
									if(Math.abs(k) == Math.abs(l)){ // Si il est pile au nord Est
										_orient=5;		
									}
									else
										_orient = 1;
								}
								else{
									if(Math.abs(k) == Math.abs(l)){ // Si il est pile au nord Ouest
										if(Math.random()<=0.5)
										_orient=6;	
									}
									else
										_orient = 3;
								}
							}
						}
						else{ // Si il est dans la partie Sud
							if ( Math.abs(k) < Math.abs(l) ){ // Si elle est plus au Sud que sur les côtés
								_orient = 2;
							}
							else{ 
								if (k > 0){
									if(Math.abs(k) == Math.abs(l)){ //Si il est pile au Sud Est
										_orient=7;
									}
									else
										_orient = 1;
								}
								else{
									if(Math.abs(k) == Math.abs(l)){ // Si il est pile au Sud Ouest
										_orient = 8;
									}
									else
										_orient = 3;
								}
							}
						}
					}
				}
			}
		}
		}
		
		if(satiety>0)satiety--;
		
		if(!preypresence){
			if(this.altitude<=ImprovedNoise.maxValue+0.5)
				this.altitude+=0.02;
			if(idlespeedcpt%idlespeed!=0){
				idlespeedcpt++;
				return;
			}
			else{
				idlespeedcpt = 1;
				if(Math.random()>0.95){
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
		}
		
		//Prise en compte des obstacles et modificiation de la trajectoire en fonction de ce paramètre :
				int nbchoixDirections=0;
				while(	   (_orient==0 && isObstacleCell(_x, ( _y - 1 )))
						|| (_orient==1 && isObstacleCell(( _x + 1 ),_y))
						|| (_orient==2 && isObstacleCell(_x, ( _y )))
						|| (_orient==3 &&isObstacleCell(( _x - 1 ),_y))
						|| (_orient==5 && isObstacleCell(( _x + 1 ),( _y - 1 )))
						|| (_orient==6 && isObstacleCell(( _x - 1 ), ( _y - 1 )))
						|| (_orient==7 &&isObstacleCell(( _x + 1 ),( _y + 1 )))
						|| (_orient==8 && isObstacleCell(( _x - 1 ),( _y + 1 )))){
							
						if(nbchoixDirections>10
								||((_orient==0 && isObstacleCell(_x, ( _y - 1 )))
								&& (_orient==1 && isObstacleCell(( _x + 1 ),_y))
								&& (_orient==2 && isObstacleCell(_x, ( _y + 1 )))
								&& (_orient==3 && isObstacleCell(( _x - 1 ),_y))
								&& (_orient==5 && isObstacleCell(( _x + 1 ),( _y - 1 )))
								&& (_orient==6 && isObstacleCell(( _x - 1 ), ( _y - 1 )))
								&& (_orient==7 &&isObstacleCell(( _x + 1 ),( _y + 1 )))
								&& (_orient==8 && isObstacleCell(( _x - 1 ),( _y + 1 ))))){ // Si toutes les issues sont bloquées, reste sur place
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
