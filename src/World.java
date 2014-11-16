import java.util.ArrayList;


public class World {

	int _dx;
	int _dy;
	
	
	static final int TRUE = 1;
	static final int FALSE = 0;
	
	int Buffer0[][][];
	int Buffer1[][][];
	
	double Buffer2[][][];
	double Buffer3[][][];

	// Index du buffer d'entier
	static final int INDEX_TREE = 3;
	static final int INDEX_FIRE = 4;
	static final int INDEX_FIRELIFE = 5;
	static final int INDEX_ASHES = 6;
	static final int INDEX_ASHESLIFE = 7;
	static final int INDEX_COLOR_R = 8;
	static final int INDEX_COLOR_G = 9;
	static final int INDEX_COLOR_B = 10;
	static final int INDEX_GRASS = 11;
	static final int INDEX_PLANT = 12;
	static final int INDEX_DIRT = 13;
	static final int INDEX_WATER = 14;
	static final int INDEX_SAND = 15;
	static final int INDEX_VOLCANO = 16;
	static final int INDEX_CRATER = 17;
	static final int INDEX_MAGMA = 18;
	static final int INDEX_LAVA = 19;
	static final int INDEX_LAVAFLUID = 20;
	static final int INDEX_LAVALIFE = 21;
	// Index du buffer de doubles
	
	static final int INDEX_SHADING1 = 0;
	static final int INDEX_SHADING2 = 1;
	
	double altitude[][];
	
	boolean buffering;
	boolean cloneBuffer; // if buffering, clone buffer after switch
	
	int activeIndex;
	
	boolean eruptionOn;
	double densite = 0.2; //0.55; // seuil de percolation � 0.55
	double p1 = 0.0001, p2 = 0.000001; // p1 : poussee d'arbre p2 : feu de foret
	double p3 = 0.00001; // p3 : poussee de plante
	double shoreHeight = 0.1; //Hauteur de la mer
	ArrayList<Agent> agents;
	int[] date;
	int hour;
	int testSeasonchange;
	int volcanoRadius;
	int cptSeason;
	int cptWateranimation;
	int lavaFluid;
	int lavaspeed;
	
	int numTuna=0, numSeagull=0, numWhale=0;
	public World ( int __dx , int __dy, boolean __buffering, boolean __cloneBuffer )
	{
		_dx = __dx;
		_dy = __dy;
		
		buffering = __buffering;
		cloneBuffer = __cloneBuffer;
	
		Buffer0 = new int[_dx][_dy][22]; // Les 3 premieres valeures de la 3e dimension renseignent sur la couleur
		Buffer1 = new int[_dx][_dy][22]; // Les suivantes sur l'etat de la case de valeur int 1:vrai, 0:false c'est donc un booleen de type entier comme en C
		/*
		 * 	0 : R
		 *  1 : G
		 *  2 : B
		 *  3 : Booleen Arbre
		 *  4 : Booleen est en Feu
		 *  5 : Duree de vie du Feu
		 *  6 : Booleen Cendres
		 *  7 : Duree de vie Cendres
		 *  8-9-10 : Index couleurs de la case avant un feu
		 *  11 : Booleen herbe
		 *  12 : booleen plante
		 *  13 : Booleen terre
		 *  14 : Booleen eau
		 *  15 : Booleen sable
		 *  16 : Booleen volcan
		 *  17 : Booleen cratere d'un volcan
		 *  18 : Booleen magma (si de la lave en fusion se cree dans le cratere
		 *  19 : Booleen lave
		 *  20 : Quantitee de lave presente sur une case
		 *  21 : Duree de vie lave
		 */
		
		Buffer2 = new double[_dx][_dy][2];
		Buffer3 = new double[_dx][_dy][2];
		
		activeIndex = 0;
		
		agents = new ArrayList<Agent>();
		
		cptWateranimation = 0;
		
		//Initialisation de la date et du temps
	    date = new int[3];
	    date[0] =1; //Jour
	    date[1] =3; // Mois
	    date[2] = 2000; // Annee
	    hour = 0;
	    testSeasonchange = getSeason();
	    cptSeason=0;
	    for ( int x = 0 ; x != _dx ; x++ )
	    	for ( int y = 0 ; y != _dy ; y++ )
	    	{
    			Buffer0[x][y][0]=255;
    			Buffer0[x][y][1]=255;
    			Buffer0[x][y][2]=255;
    			Buffer1[x][y][0]=255;
    			Buffer1[x][y][1]=255;
    			Buffer1[x][y][2]=255;
	    	}
	    
	    //Initialisation altitude 
	    
	    altitude = ImprovedNoise.noiseArray(_dx, _dy, 1.5);
	    
	    //Initialisation EAU : 
	    
	    for(int x = 0; x< _dx ;x++){
	    	for(int y = 0; y<_dy; y++){
	    		if(altitude[x][y]<shoreHeight){
	    			setWaterCell(x,y);
	    		}
	    	}
	    }
	    
	    
	    int xS=0,yS=0; // coordonn�e plus haut point de la map
	    
	    // Initialisation HERBE ET PLANTES ET SABLE
	    
	    for ( int x = 0 ; x != _dx ; x++ )
	    	for ( int y = 0 ; y != _dy ; y++ )
	    	{
	    		
	    		if(isInitWaterCell(x,y)){
	    			continue;
	    		}
	    		
	    		if(altitude[x][y]>shoreHeight&&altitude[x][y]<shoreHeight+Math.abs(0.5*shoreHeight)){
	    			setSandCell(x,y);
	    		}
	    		else{
	    			if(Math.random()>0.001){
	    				setGrassCell(x,y);
	    			}
	    			else{
	    				setPlantCell(x,y,TRUE);
	    			}
    			
	    		}
	    		if(altitude[x][y]==ImprovedNoise.maxValue){
	    			xS = x;
	    			yS = y;
	    		}
	    	}
	    
	    
	    // Initialisation ARBRES PAR PAQUETS
	    int nbArbreInit=(int)(_dx*_dy*densite);
	    int nbPaquetArbres= (int)(Math.random()*5)+3;
	    for( int i=0 ; i<nbPaquetArbres ; i++ ) {
	    	int x = (int)(_dx*Math.random());
    		int y = (int)(_dy*Math.random());
	    	while(isInitWaterCell(x,y)||isInitSandCell(x,y)){
	    		x = (int)(_dx*Math.random());
	    		y = (int)(_dy*Math.random());
	    	}
	    	int nbArbresPaquet = (nbArbreInit/nbPaquetArbres)+(int)(Math.random()*2*nbArbreInit/100)-nbArbreInit/100;
	    	double rayonPolaire = (_dx/2)*(Math.random())*(nbArbresPaquet/nbArbreInit)+_dy/2;
	    	for(int j = 0 ; j<nbArbresPaquet ; j++){
	    		double angle = Math.random()*2*Math.PI;
	    		double rayon = Math.random()*rayonPolaire;
	    		int x1 = (int)(rayon*Math.cos(angle))+x;
	    		int y1 = (int)(rayon*Math.sin(angle))+y;
	    		if(isInitWaterCell((x1+_dx)%_dx,(y1+_dy)%_dy)||(isInitSandCell((x1+_dx)%_dx,(y1+_dy)%_dy))){
	    			j++;
	    			continue;
	    		}
	    		setTreeCell((x1+_dx)%_dx,(y1+_dy)%_dy,TRUE);
	    	}
	    }
	    
	    //Initialisation VOLCAN
	    this.volcanoRadius = _dx/10;
	    setVolcano(xS,yS);
	    eruptionOn = false;
	    lavaspeed = 0;
	    
	    
	}
	
	/*
	 * METHODES :
	 * note : les methodes comportant le mot "init" sont des methodes annexes qui marchent lors de l'initialisation du monde, quand l'un des deux buffers est encore vide
	 */
	
	
	// Utilitaire : sorting algorithm
	public double[] insertionSort(double[] data){
		  int len = data.length;
		  double key = 0;
		  int i = 0;
		  for(int j = 1;j<len;j++){
		    key = data[j];
		    i = j-1;
		    while(i>=0 && data[i]>key){
		      data[i+1] = data[i];
		      i = i-1;
		      data[i+1]=key;
		    }
		  }
		  return data;
		}
		
		
	
	public boolean checkBoundsBool( int __x , int __y ) // booleen qui retourne vrai SI les coordonnees sont a l'interieur du monde
	{
		if ( __x < 0 || __x >= _dx || __y < 0 || __y >= _dy )
		{
			return false;
		}
		return true;
	}
	
	public int[] getInitialCellState ( int __x, int __y ) 
	{
		if(!checkBoundsBool(__x,__y))return null;
		
		int color[] = new int[3];

		if ( buffering == false )
		{
			color[0] = Buffer0[__x][__y][0];
			color[1] = Buffer0[__x][__y][1];
			color[2] = Buffer0[__x][__y][2];
		}
		else
		{
			if ( activeIndex == 0 ) // read old buffer
			{
				color[0] = Buffer0[__x][__y][0];
				color[1] = Buffer0[__x][__y][1];
				color[2] = Buffer0[__x][__y][2];
			}
			else
			{
				color[0] = Buffer1[__x][__y][0];
				color[1] = Buffer1[__x][__y][1];
				color[2] = Buffer1[__x][__y][2];
			}
		}
		
		return color;
	}
	
	public int[] getCellState ( int __x, int __y )
	{
		if(!checkBoundsBool(__x,__y))return null;
		
		int color[] = new int[3];

		if ( buffering == false )
		{
			color[0] = Buffer0[__x][__y][0];
			color[1] = Buffer0[__x][__y][1];
			color[2] = Buffer0[__x][__y][2];
		}
		else
		{
			if ( activeIndex == 1 ) // read old buffer
			{
				color[0] = Buffer0[__x][__y][0];
				color[1] = Buffer0[__x][__y][1];
				color[2] = Buffer0[__x][__y][2];
			}
			else
			{
				color[0] = Buffer1[__x][__y][0];
				color[1] = Buffer1[__x][__y][1];
				color[2] = Buffer1[__x][__y][2];
			}
		}
		
		return color;
	}
	
	public int getRedValue( int _x, int _y)
	{
		int color[] = getCellState(_x,_y);
		return color[0];
	}
	public int getGreenValue( int _x, int _y)
	{
		int color[] = getCellState(_x,_y);
		return color[1];
	}
	public int getBlueValue( int _x, int _y)
	{
		int color[] = getCellState(_x,_y);
		return color[2];
	}
	
	public void setCellState ( int __x, int __y, int __r, int __g, int __b )
	{
		if(!checkBoundsBool(__x,__y))return;
		
		if ( buffering == false )
		{
			Buffer0[__x][__y][0] = __r;
			Buffer0[__x][__y][1] = __g;
			Buffer0[__x][__y][2] = __b;
		}
		else
		{
			if ( activeIndex == 0 ) // write new buffer
			{
				Buffer0[__x][__y][0] = __r;
				Buffer0[__x][__y][1] = __g;
				Buffer0[__x][__y][2] = __b;
			}
			else
			{
				Buffer1[__x][__y][0] = __r;
				Buffer1[__x][__y][1] = __g;
				Buffer1[__x][__y][2] = __b;
			}
		}
	}
	
	public void setCellState ( int __x, int __y, int __color[] )
	{
		if(!checkBoundsBool(__x,__y))return;
			
		if ( buffering == false )
		{
			Buffer0[__x][__y][0] = __color[0];
			Buffer0[__x][__y][1] = __color[1];
			Buffer0[__x][__y][2] = __color[2];
		}
		else
		{
			if ( activeIndex == 0 )
			{
				Buffer0[__x][__y][0] = __color[0];
				Buffer0[__x][__y][1] = __color[1];
				Buffer0[__x][__y][2] = __color[2];
			}
			else
			{
				Buffer1[__x][__y][0] = __color[0];
				Buffer1[__x][__y][1] = __color[1];
				Buffer1[__x][__y][2] = __color[2];
			}	
		}
	}
	
	public void removeProperties(int _x, int _y){ // met a 0 tout les buffers correspondant aux propriete d'une case du monde
		if(!checkBoundsBool(_x,_y))return;
		
		for (int i = 0; i < 20; i++)
		{
			Buffer0[_x][_y][i]=0;
			Buffer1[_x][_y][i]=0;
		}
	}
	
	public boolean isColorCell(int _x, int _y, int _r, int _g, int _b){ // renvoie vrai si la couleur de la case correspond a celle en argument
		
		if(!checkBoundsBool(_x,_y))return false;
		
		int[] cell = getCellState(_x,_y);
		return (cell[0]==_r && cell[1] == _g && cell[2] == _b);
	}
	
	public boolean isInitialColorCell(int _x, int _y, int _r, int _g, int _b){ 
		
		if(!checkBoundsBool(_x,_y))return false;
		
		int[] cell = getInitialCellState(_x,_y);
		return (cell[0]==_r && cell[1] == _g && cell[2] == _b);
	}
	
	public void setTreeCell( int _x, int _y, int bool) // Place un arbre au point en argument
	{
		if(!checkBoundsBool(_x,_y))return;
		
		if ( buffering == false )
		{
			Buffer0[_x][_y][INDEX_TREE]=bool;
		}
		else
		{
			if ( activeIndex == 0 ) // read old buffer
			{
				Buffer0[_x][_y][INDEX_TREE]=bool;
			}
			else
			{
				Buffer1[_x][_y][INDEX_TREE]=bool;
			}
		}
		if(bool==TRUE){
			setCellState(_x,_y,0,150,0);
		}
	}
	
	public boolean isTreeCell(int _x, int _y){  
		
		if(!checkBoundsBool(_x,_y))return false;
		
		
		if ( buffering == false )
		{
			return Buffer0[_x][_y][INDEX_TREE]==1;
		}
		
		if ( activeIndex == 1 ) // read old buffer
		{
				return Buffer0[_x][_y][INDEX_TREE]==1;
		}
		else
		{
				return Buffer1[_x][_y][INDEX_TREE]==1;
		}

	}
	
	public boolean isGrassCell(int _x, int _y){ 
		if(!checkBoundsBool(_x,_y))return false;
		
		
		if ( buffering == false )
		{
			return Buffer0[_x][_y][INDEX_GRASS]==1;
		}
		
		if ( activeIndex == 1 ) // read old buffer
		{
				return Buffer0[_x][_y][INDEX_GRASS]==1;
		}
		else
		{
				return Buffer1[_x][_y][INDEX_GRASS]==1;
		}

	}
	
	public void setGrassCell( int _x, int _y) // place de l'herbe
	{
		if(!checkBoundsBool(_x,_y))return;
		if(isLava(_x,_y))return;
		removeProperties(_x,_y);

		double alt = Math.abs((altitude[_x][_y]-ImprovedNoise.minValue)/(ImprovedNoise.maxValue-ImprovedNoise.minValue));
		if ( buffering == false )
		{
			Buffer0[_x][_y][INDEX_GRASS]=1;
		}
		else
		{
			if ( activeIndex == 0 ) // read old buffer
			{
				Buffer0[_x][_y][INDEX_GRASS]=1;
			}
			else
			{
				Buffer1[_x][_y][INDEX_GRASS]=1;
			}
		}
		if(getSeason() == 1) setCellState(_x,_y,(int)(15*alt)*9+(int)(Math.random()*5),(int)(15*alt)*9+70+(int)(Math.random()*5),(int)(15*alt)*9+(int)(Math.random()*5));
		if(getSeason() == 2) setCellState(_x,_y,(int)(15*alt)*2,(int)(10*alt)*10+110+(int)(Math.random()*20),(int)(15*alt)*3+20+(int)(Math.random()*5));
		if(getSeason() == 3) setCellState(_x,_y,200+(int)(Math.random()*20) + (int)(5*alt)*6,(int)(Math.random()*60)+20+(int)(5*alt)*20,(int)(Math.random()*30+10+(int)(alt*5)*4));
		if(getSeason() == 4) setCellState(_x,_y,(int)(10*alt)*5+170+(int)(Math.random()*10),(int)(10*alt)*5+(int)(Math.random()*30)+170,(int)(10*alt)*5+(int)(Math.random()*10+170));
		
	}
	
	public void setSandCell( int _x, int _y) // place du sable
	{
		if(!checkBoundsBool(_x,_y))return;
		removeProperties(_x,_y);
		if ( buffering == false )
		{
			Buffer0[_x][_y][INDEX_SAND] = 1;
		}
		else
		{
			if ( activeIndex == 0 ) // read old buffer
			{
				Buffer0[_x][_y][INDEX_SAND] = 1;
			}
			else
			{
				Buffer1[_x][_y][INDEX_SAND] = 1;
			}
		}
		
		setCellState(_x,_y,(int)(Math.random()*50+190),(int)(Math.random()*30+180),(int)(Math.random()*20+130));
		
	}
	
	public boolean isSandCell( int _x, int _y)
	{
		if(!checkBoundsBool(_x,_y))return false;
		
		if ( buffering == false )
		{
			return Buffer0[_x][_y][INDEX_SAND] == 1;
		}
		else
		{
			if ( activeIndex == 1 ) // read old buffer
			{
				return Buffer0[_x][_y][INDEX_SAND] == 1;
			}
			else
			{
				return Buffer1[_x][_y][INDEX_SAND] == 1;
			}
		}
	}
	
	public boolean isInitSandCell( int _x, int _y)
	{
		if(!checkBoundsBool(_x,_y))return false;
		
		if ( buffering == false )
		{
			return Buffer0[_x][_y][INDEX_SAND] == 1;
		}
		else
		{
			if ( activeIndex == 0 ) // read old buffer
			{
				return Buffer0[_x][_y][INDEX_SAND] == 1;
			}
			else
			{
				return Buffer1[_x][_y][INDEX_SAND] == 1;
			}
		}
	}
	
	public void setAshesCell(int _x, int _y, int bool)
	{
		if(!checkBoundsBool(_x,_y))return;
		
		
		int colors[] = getCellState(_x,_y);
		setBeforeFireColor(_x,_y,colors);
		if ( buffering == false )
		{
			Buffer0[_x][_y][INDEX_FIRE]=0;
			Buffer0[_x][_y][INDEX_ASHES]=bool;
			Buffer2[_x][_y][INDEX_SHADING1]=1.0;
			Buffer2[_x][_y][INDEX_SHADING2]=0;
		}
		else
		{
			if ( activeIndex == 0 ) // read old buffer
			{
				Buffer0[_x][_y][INDEX_FIRE]=0;
				Buffer0[_x][_y][INDEX_ASHES]=bool;
				Buffer2[_x][_y][INDEX_SHADING1]=1.0;
				Buffer2[_x][_y][INDEX_SHADING2]=0;
			}
			else
			{
				Buffer1[_x][_y][INDEX_FIRE]=0;
				Buffer1[_x][_y][INDEX_ASHES]=bool;
				Buffer3[_x][_y][INDEX_SHADING1]=1.0;
				Buffer3[_x][_y][INDEX_SHADING2]=0;
			}
		}
		if(bool==TRUE){
			setCellState(_x,_y,20,20,20);
		}
		else{
			setDirtCell(_x,_y,TRUE);
		}
	}
	
	public boolean isAshesCell( int _x, int _y)
	{
		if(!checkBoundsBool(_x,_y))return false;
		
		if ( buffering == false )
		{
			return Buffer0[_x][_y][INDEX_ASHES]==1;
		}
		
		if ( activeIndex == 1 ) // read old buffer
		{
				return Buffer0[_x][_y][INDEX_ASHES]==1;
		}
		else
		{
				return Buffer1[_x][_y][INDEX_ASHES]==1;
		}
	}
	
	public int[] getBeforeFireColor(int _x, int _y) // rend la couleur d'une case avant qu'elle ait prise feu
	{
		if(!checkBoundsBool(_x,_y))return null;
		int color[] = new int[3];
		
		if ( buffering == false )
		{
			color[0] = Buffer0[_x][_y][INDEX_COLOR_R];
			color[1] = Buffer0[_x][_y][INDEX_COLOR_G];
			color[2] = Buffer0[_x][_y][INDEX_COLOR_B];
		}
		else
		{
			if ( activeIndex == 1 ) // read old buffer
			{
				color[0] = Buffer0[_x][_y][INDEX_COLOR_R];
				color[1] = Buffer0[_x][_y][INDEX_COLOR_G];
				color[2] = Buffer0[_x][_y][INDEX_COLOR_B];
			}
			else
			{
				color[0] = Buffer1[_x][_y][INDEX_COLOR_R];
				color[1] = Buffer1[_x][_y][INDEX_COLOR_G];
				color[2] = Buffer1[_x][_y][INDEX_COLOR_B];
			}
		}
		return color;
	}
	
	public void setBeforeFireColor(int _x, int _y, int[] color)
	{
		if(!checkBoundsBool(_x,_y))return;
		
		if ( buffering == false )
		{

			Buffer0[_x][_y][INDEX_COLOR_R]=color[0];
			Buffer0[_x][_y][INDEX_COLOR_G]=color[1];
			Buffer0[_x][_y][INDEX_COLOR_B]=color[2];

		}
		else
		{
			if ( activeIndex == 0 ) // read old buffer
			{

				Buffer0[_x][_y][INDEX_COLOR_R]=color[0];
				Buffer0[_x][_y][INDEX_COLOR_G]=color[1];
				Buffer0[_x][_y][INDEX_COLOR_B]=color[2];

			}
			else
			{

				Buffer1[_x][_y][INDEX_COLOR_R]=color[0];
				Buffer1[_x][_y][INDEX_COLOR_G]=color[1];
				Buffer1[_x][_y][INDEX_COLOR_B]=color[2];

			}
		}
	}
	
	public double[] getShadingCoeff(int _x, int _y) // rend les coefficients qui permettent d'obtenir un degrade simulant les cendres apres un feu
	{
		if(!checkBoundsBool(_x,_y))return null;
		double shading[] = new double[2];
		
		if ( buffering == false )
		{
			shading[0] = Buffer2[_x][_y][INDEX_SHADING1];
			shading[1] = Buffer2[_x][_y][INDEX_SHADING2];
		}
		else
		{
			if ( activeIndex == 1 ) // read old buffer
			{
				shading[0] = Buffer2[_x][_y][INDEX_SHADING1];
				shading[1] = Buffer2[_x][_y][INDEX_SHADING2];			}
			else
			{
				shading[0] = Buffer3[_x][_y][INDEX_SHADING1];
				shading[1] = Buffer3[_x][_y][INDEX_SHADING2];			}
		}
		return shading;
	}
	
	public void setShadingCoeff(int _x, int _y, double[] shading)
	{
		if(!checkBoundsBool(_x,_y))return;;
		
		if ( buffering == false )
		{

			Buffer2[_x][_y][INDEX_SHADING1]=shading[0];
			Buffer2[_x][_y][INDEX_SHADING2]=shading[1];
		}
		else
		{
			if ( activeIndex == 0 ) // read old buffer
			{
				Buffer2[_x][_y][INDEX_SHADING1]=shading[0];
				Buffer2[_x][_y][INDEX_SHADING2]=shading[1];
			}
			else
			{
				Buffer3[_x][_y][INDEX_SHADING1]=shading[0];
				Buffer3[_x][_y][INDEX_SHADING2]=shading[1];
			}
		}
	}
	
	public void updateAshes( int _x, int _y)
	{
		if(!checkBoundsBool(_x,_y))return;
		int[] color = getBeforeFireColor(_x,_y);
		double[] shading = getShadingCoeff(_x,_y);
		if(shading[1]>=1){
			setAshesCell(_x,_y,FALSE);
			return;
		}
		shading[0]-=0.05;
		shading[1]+=0.05;
		setShadingCoeff(_x,_y,shading);
		setCellState(_x,_y,(int)(shading[0]*20+0.5*shading[1]*color[0]), (int)(shading[0]*20+0.5*shading[1]*color[1]), (int)(shading[0]*20+0.5*shading[1]*color[2]));
	}
		
	public boolean isFireCell( int _x, int _y)
	{
		if(!checkBoundsBool(_x,_y))return false;
		
		if ( buffering == false )
		{
			return Buffer0[_x][_y][INDEX_FIRE]==1;
		}
		
		if ( activeIndex == 1 ) // read old buffer
		{
				return Buffer0[_x][_y][INDEX_FIRE]==1;
		}
		else
		{
				return Buffer1[_x][_y][INDEX_FIRE]==1;
		}

	}
	
	public int getFireLifeSpan( int _x, int _y)
	{
		if(!checkBoundsBool(_x,_y))return 0;
		
		if ( buffering == false )
		{
			return Buffer0[_x][_y][INDEX_FIRELIFE];
		}
		
		if ( activeIndex == 1 ) // read old buffer
		{
				return Buffer0[_x][_y][INDEX_FIRELIFE];
		}
		else
		{
				return Buffer1[_x][_y][INDEX_FIRELIFE];
		}
	}
	
	public void decreaseFireLifeSpan( int _x, int _y)
	{
		if(!checkBoundsBool(_x,_y))return;
		
		if ( buffering == false )
		{
			Buffer0[_x][_y][INDEX_FIRELIFE]--;
		}
		
		if ( activeIndex == 0 ) // read old buffer
		{
				Buffer0[_x][_y][INDEX_FIRELIFE]--;
		}
		else
		{
				Buffer1[_x][_y][INDEX_FIRELIFE]--;
		}
	}
	
	public void setFireCell(int _x, int _y) // Si la case n'est pas en feu, cree un feu
	{
		if(!checkBoundsBool (_x,_y)||!isTreeCell(_x,_y))return;
		
		removeProperties(_x,_y);
			if ( buffering == false )
			{
				Buffer0[_x][_y][INDEX_FIRE]=1;
				Buffer0[_x][_y][INDEX_FIRELIFE]=10+(int)(Math.random()*15);

			}
			else
			{
				if ( activeIndex == 0 ) // read old buffer
				{
					Buffer0[_x][_y][INDEX_FIRE]=1;
					Buffer0[_x][_y][INDEX_FIRELIFE]=10+(int)(Math.random()*15);

				}
				else
				{
					Buffer1[_x][_y][INDEX_FIRE]=1;
					Buffer1[_x][_y][INDEX_FIRELIFE]=10+(int)(Math.random()*15);
				}
			}
			setCellState(_x, _y, (int)(Math.random()*20)+230, (int)(Math.random()*10)+20, 20);
		
	}
	
	public void extinctFire(int _x, int _y)
	{
		if(!checkBoundsBool(_x,_y))return;
		if ( buffering == false )
		{
			Buffer0[_x][_y][INDEX_FIRE]=0;

		}
		else
		{
			if ( activeIndex == 0 ) // read old buffer
			{
				Buffer0[_x][_y][INDEX_FIRE]=0;

			}
			else
			{
				Buffer1[_x][_y][INDEX_FIRE]=0;

			}
		}
	}
	
	public void updateFire(int _x, int _y)
	{
		if(!checkBoundsBool(_x,_y))return;;
		
		int firelifespan = getFireLifeSpan(_x,_y);
		if(firelifespan==0){
			extinctFire(_x,_y);
			setAshesCell(_x,_y,TRUE);
			return;
		}
		decreaseFireLifeSpan(_x,_y);
		int colors[] = getCellState(_x,_y);
		if(firelifespan>5){
			if(colors[1]+30<255)
				colors[1]+= 2+(int)(Math.random()*10);
			colors[0]=(int)(Math.random()*50)+200;
		}
		else{
			colors[0]-=15;
			colors[1]-=2;
		}
		setCellState(_x,_y,colors);
		
		
	}
	
	public boolean isPlantCell(int _x, int _y)
	{
		if(!checkBoundsBool(_x,_y))return false;
		
		if ( buffering == false )
		{
			return Buffer0[_x][_y][INDEX_PLANT]==1;
		}
		
		if ( activeIndex == 1 ) // read old buffer
		{
				return Buffer0[_x][_y][INDEX_PLANT]==1;
		}
		else
		{
				return Buffer1[_x][_y][INDEX_PLANT]==1;
		}
	}
	
	public void setPlantCell(int _x, int _y, int bool)
	{
		if(!checkBoundsBool(_x,_y))return;
		
		if ( buffering == false )
		{
			Buffer0[_x][_y][INDEX_PLANT]=bool;
		}
		
		if ( activeIndex == 0 ) // read old buffer
		{
				Buffer0[_x][_y][INDEX_PLANT]=bool;
		}
		else
		{
				Buffer1[_x][_y][INDEX_PLANT]=bool;
		}
		
		if(bool==TRUE)
			setCellState(_x,_y,165,255,0);
		else
			setGrassCell(_x,_y);
	}
		
	public void setDirtCell(int _x, int _y, int bool){
		if(!checkBoundsBool(_x,_y))return;
		removeProperties(_x,_y);
		if ( buffering == false )
		{
			Buffer0[_x][_y][INDEX_DIRT]=bool;
		}
		
		if ( activeIndex == 0 ) // read old buffer
		{
				Buffer0[_x][_y][INDEX_DIRT]=bool;
		}
		else
		{
				Buffer1[_x][_y][INDEX_DIRT]=bool;
		}
		
		if(bool == FALSE){
			setGrassCell(_x,_y);
		}
		else{
			setCellState(_x,_y,(int)(Math.random()*10)+140,(int)(Math.random()*10)+140,(int)(Math.random()*10)+30);
		}
	}
	
	public boolean isDirtCell(int _x, int _y)
	{
		if(!checkBoundsBool(_x,_y))return false;

		if ( buffering == false )
		{
			return Buffer0[_x][_y][INDEX_DIRT]==1;
		}
		
		if ( activeIndex == 1 ) // read old buffer
		{
				return Buffer0[_x][_y][INDEX_DIRT]==1;
		}
		else
		{
				return Buffer1[_x][_y][INDEX_DIRT]==1;
		}
	}
	
	public boolean isWaterCell(int _x, int _y){ 
		if(!checkBoundsBool(_x,_y))return false;
		
		if ( buffering == false )
		{
			return Buffer0[_x][_y][INDEX_WATER]==1;
		}
		
		if ( activeIndex == 1 ) // read old buffer
		{
				return Buffer0[_x][_y][INDEX_WATER]==1;
		}
		else
		{
				return Buffer1[_x][_y][INDEX_WATER]==1;
		}
	}
	
	public boolean isInitWaterCell(int _x, int _y){ 
		if(!checkBoundsBool(_x,_y))return false;
		
		if ( buffering == false )
		{
			return Buffer0[_x][_y][INDEX_WATER]==1;
		}
		
		if ( activeIndex == 0 ) // read old buffer
		{
				return Buffer0[_x][_y][INDEX_WATER]==1;
		}
		else
		{
				return Buffer1[_x][_y][INDEX_WATER]==1;
		}
	}
	
	public void setWaterCell(int _x, int _y){
		if(!checkBoundsBool(_x,_y))return;
		removeProperties(_x,_y);
		double alt = Math.abs(altitude[_x][_y]-ImprovedNoise.minValue)/(ImprovedNoise.maxValue-ImprovedNoise.minValue);
		if ( buffering == false )
		{
			Buffer0[_x][_y][INDEX_WATER]=1;
		}
		
		if ( activeIndex == 0 ) // read old buffer
		{
				Buffer0[_x][_y][INDEX_WATER]=1;
		}
		else
		{
				Buffer1[_x][_y][INDEX_WATER]=1;
		}
		
		setCellState(_x,_y,(int)(15*alt)*7+(int)(Math.random()*5),(int)(15*alt)*7+(int)(Math.random()*5),120+(int)(15*alt)*8+(int)(Math.random()*5));
		
	}
	
	public void updateWater(int _x, int _y){
		if(!checkBoundsBool(_x,_y))return;
		double alt = Math.abs(altitude[_x][_y]-ImprovedNoise.minValue)/(ImprovedNoise.maxValue-ImprovedNoise.minValue);
		if(cptWateranimation < 5){
			setCellState(_x,_y,(int)(15*alt)*7+(int)(Math.random()*5),(int)(15*alt)*7+(int)(Math.random()*5),120+(int)(15*alt)*8+(int)(Math.random()*5));
		}
		if(cptWateranimation >=5 && cptWateranimation < 10){
			setCellState(_x,_y,(int)(14.999999999*alt)*7+(int)(Math.random()*5),(int)(14*alt)*7+(int)(Math.random()*5),120+(int)(14*alt)*8+(int)(Math.random()*5));
		}
		
	}
		
	public boolean isVolcano(int _x,int _y)
	{
		if(!checkBoundsBool(_x,_y))return false;
		
		if ( buffering == false )
		{
			return Buffer0[_x][_y][INDEX_VOLCANO]==1;
		}
		
		if ( activeIndex == 0 ) // read old buffer
		{
			return Buffer0[_x][_y][INDEX_VOLCANO]==1;
		}
		else
		{
			return Buffer1[_x][_y][INDEX_VOLCANO]==1;
		}
	}
	
	public boolean isCrater(int _x,int _y)
	{
		if(!checkBoundsBool(_x,_y))return false;
		
		if ( buffering == false )
		{
			return Buffer0[_x][_y][INDEX_CRATER]==1;
		}
		
		if ( activeIndex == 0 ) // read old buffer
		{
			return Buffer0[_x][_y][INDEX_CRATER]==1;
		}
		else
		{
			return Buffer1[_x][_y][INDEX_CRATER]==1;
		}
	}
		
	public void setVolcanoProperty(int _x, int _y, int crater) // place les propriete de volcan, cratere ou non
	{
		
		removeProperties(_x,_y);
		if ( buffering == false )
		{
			Buffer0[_x][_y][INDEX_VOLCANO]=1;
			if(crater==1)Buffer0[_x][_y][INDEX_CRATER]=1;
		}
		
		if ( activeIndex == 0 ) // read old buffer
		{
				Buffer0[_x][_y][INDEX_VOLCANO]=1;
				if(crater==1)Buffer0[_x][_y][INDEX_CRATER]=1;
		}
		else
		{
				Buffer1[_x][_y][INDEX_VOLCANO]=1;
				if(crater==1)Buffer1[_x][_y][INDEX_CRATER]=1;
		}
	}
	
	public void setVolcano(int _x, int _y)
	{
		if(!checkBoundsBool(_x,_y))return;
		double altSummit = Math.abs(altitude[_x][_y]);
		
		for(int x = _x - volcanoRadius; x < _x + volcanoRadius ; x++)
			for(int y = _y - volcanoRadius ; y < _y + volcanoRadius ; y++)
			{
				if(!checkBoundsBool(x,y))continue;
				double currentAlt = altitude[x][y];
				double coeffAlt = (currentAlt-altSummit + Math.abs(altSummit)/(volcanoRadius))/(Math.abs(altSummit)/(volcanoRadius));
				if (checkBoundsBool(x,y)&&currentAlt <= altSummit && currentAlt > altSummit - 2*Math.abs(altSummit)/(volcanoRadius)){
					if(currentAlt > altSummit - Math.abs(altSummit)/(3*volcanoRadius)){
						setVolcanoProperty(x,y,TRUE);
						altitude[x][y] = ImprovedNoise.maxValue;
					}
					else
						setVolcanoProperty(x,y,FALSE);
					if(isCrater(x,y))
						setCellState(x,y,0,0,0);
					else
						setCellState(x,y,(int)(5*coeffAlt)*5+50,(int)(Math.random()*10),(int)(Math.random()*10)+10);
					
					
				}
				
			}
		
	}
	
	public void setMagma(int _x, int _y, int bool)
	{
		if(!checkBoundsBool(_x,_y))return;
		if ( buffering == false )
		{
			Buffer0[_x][_y][INDEX_MAGMA]=bool;
		}
		
		if ( activeIndex == 0 ) // read old buffer
		{
				Buffer0[_x][_y][INDEX_MAGMA]=bool;
		}
		else
		{
				Buffer1[_x][_y][INDEX_MAGMA]=bool;
		}
		
		if(bool == TRUE){
			setCellState(_x,_y,(int)(Math.random()*100)+160,(int)(Math.random()*50)+40,(int)(Math.random()*10)+30);
		}
		else
			setCellState(_x,_y,0,0,0);
	}
	
	public boolean isMagma(int _x, int _y)
	{
		if(!checkBoundsBool(_x,_y))return false;
		if ( buffering == false )
		{
			return Buffer0[_x][_y][INDEX_MAGMA]==1;
		}
		
		if ( activeIndex == 1 ) // read old buffer
		{
				return Buffer0[_x][_y][INDEX_MAGMA]==1;
		}
		else
		{
				return Buffer1[_x][_y][INDEX_MAGMA]==1;
		}
	}
	
	public boolean isMagmaInit(int _x, int _y)
	{
		if(!checkBoundsBool(_x,_y))return false;
		if ( buffering == false )
		{
			return Buffer0[_x][_y][INDEX_MAGMA]==1;
		}
		
		if ( activeIndex == 0 ) // read old buffer
		{
				return Buffer0[_x][_y][INDEX_MAGMA]==1;
		}
		else
		{
				return Buffer1[_x][_y][INDEX_MAGMA]==1;
		}
	}
			
	public void updateMagma(){
		for(int _x = 0; _x != _dx ; _x++)
			for( int _y = 0; _y != _dy; _y++)
				if(isMagma(_x,_y))setCellState(_x,_y,(int)(Math.random()*100)+160,(int)(Math.random()*50)+40,(int)(Math.random()*10)+30);

	}
	
	public void startEruption()
	{
		for(int i = 0; i < _dx; i++)
			for( int j = 0; j< _dy; j++)
				if(isCrater(i,j))
					setMagma(i,j,TRUE);
		eruptionOn = true;
		lavaFluid = 5000; 
		
	}
	
	public void stopEruption()
	{
		for(int i = 0; i < _dx; i++)
			for( int j = 0; j< _dy; j++)
				if(isCrater(i,j))
					setMagma(i,j,FALSE);
		eruptionOn = false;
	}
	
	public void setLava(int _x, int _y, int bool, int fluid){ 
		if(!checkBoundsBool(_x,_y)||isWaterCell(_x,_y))return;
		if ( buffering == false )
		{
			Buffer0[_x][_y][INDEX_LAVA]=bool;
		}
		
		if ( activeIndex == 0 ) // read old buffer
		{
				Buffer0[_x][_y][INDEX_LAVA]=bool;
		}
		else
		{
				Buffer1[_x][_y][INDEX_LAVA]=bool;
		}
		
		if(bool == 1)
		{
			setCellState(_x,_y,(int)(Math.random()*100)+160,(int)(Math.random()*20)+40,(int)(Math.random()*10)+30);
			addLavaFluid(_x,_y,fluid);
			setLavaLife(_x,_y,30);
		}
		else
			if(isVolcano(_x,_y)){
				setVolcanoProperty(_x,_y,FALSE);
			}
			else if (isGrassCell(_x,_y)){
				setDirtCell(_x,_y,TRUE);
			}
			else if (isSandCell(_x,_y)){
				setSandCell(_x,_y);
			}
	
	}
	
	public void setLavaLife(int _x, int _y, int amount){
		if(!checkBoundsBool(_x,_y))return;
		if ( buffering == false )
		{
			Buffer0[_x][_y][INDEX_LAVALIFE]=amount;
		}
		
		if ( activeIndex == 0 ) // read old buffer
		{
				Buffer0[_x][_y][INDEX_LAVALIFE]=amount;
		}
		else
		{
				Buffer1[_x][_y][INDEX_LAVALIFE]=amount;
		}
	}
	
	public int getLavaLife(int _x, int _y){
		if(!checkBoundsBool(_x,_y))return 0;
		if ( buffering == false )
		{
			return Buffer0[_x][_y][INDEX_LAVALIFE];
		}
		
		if ( activeIndex == 1 ) // read old buffer
		{
				return Buffer0[_x][_y][INDEX_LAVALIFE];
		}
		else
		{
				return Buffer1[_x][_y][INDEX_LAVALIFE];
		}
	}
	
	public void decreaseLavaLife(int _x, int _y){
		if(!checkBoundsBool(_x,_y))return;
		if ( buffering == false )
		{
			Buffer0[_x][_y][INDEX_LAVALIFE]--;
		}
		
		if ( activeIndex == 0 ) // read old buffer
		{
				Buffer0[_x][_y][INDEX_LAVALIFE]--;
		}
		else
		{
				Buffer1[_x][_y][INDEX_LAVALIFE]--;
		}
	}
		
	public void addLavaFluid(int _x, int _y, int amount)
	{
		if(lavaFluid<=0)return;
		if ( buffering == false )
		{
			Buffer0[_x][_y][INDEX_LAVAFLUID]+= amount;
		}
		
		if ( activeIndex == 0 ) // read old buffer
		{
				Buffer0[_x][_y][INDEX_LAVAFLUID]+= amount;
		}
		else
		{
				Buffer1[_x][_y][INDEX_LAVAFLUID]+= amount;
		}
		lavaFluid -= amount;
	}
			
	public void removeLavaFluid(int _x, int _y, int amount)
	{
		if ( buffering == false )
		{
			Buffer0[_x][_y][INDEX_LAVAFLUID]-= amount;
		}
		
		if ( activeIndex == 0 ) // read old buffer
		{
				Buffer0[_x][_y][INDEX_LAVAFLUID]-= amount;
		}
		else
		{
				Buffer1[_x][_y][INDEX_LAVAFLUID]-= amount;
		}
		lavaFluid += amount;
	}
	
	public int getLavaFluid(int _x,int _y)
	{
		if ( buffering == false )
		{
			return Buffer0[_x][_y][INDEX_LAVAFLUID];
		}
		
		if ( activeIndex == 1 ) // read old buffer
		{
				return Buffer0[_x][_y][INDEX_LAVAFLUID];
		}
		else
		{
				return Buffer1[_x][_y][INDEX_LAVAFLUID];
		}
	}
		
	public boolean isLava(int _x, int _y){
		if(!checkBoundsBool(_x,_y))return false;
		if ( buffering == false )
		{
			return Buffer0[_x][_y][INDEX_LAVA]==1;
		}
		
		if ( activeIndex == 1 ) // read old buffer
		{
				return Buffer0[_x][_y][INDEX_LAVA]==1;
		}
		else
		{
				return Buffer1[_x][_y][INDEX_LAVA]==1;
		}
	}
	
	public void lavaPlacement(){ // Place la lave dans 3 points pres du cratere en erruption
		int cpt = 0;
		int[] matrixDx = new int[_dx];
		int[] matrixDy = new int[_dy];
		for(int i = 0; i<_dx ; i++)
			matrixDx[i]=i;
		for(int i = 0; i<_dy ; i++)
			matrixDy[i]=i;
		ImprovedNoise.shuffleArray(matrixDx);
		ImprovedNoise.shuffleArray(matrixDy);
		for(int x : matrixDx)
			for(int y : matrixDy)
				if(isVolcano(x,y)){
					int nbVoisins=0;
					for(int i = x-1 ; i <= x+1 ; i++)
						for(int j = y-1 ; j <= y+1 ; j++)
						{
							if(i==x&&j==y)continue;
							if(isMagmaInit((i+_dx)%_dx,(j+_dy)%_dy))
								nbVoisins++;
						}
					
					if(nbVoisins==3&&cpt<3){
						setLava(x,y,TRUE,lavaFluid/3);
						cpt++;
					}
				}
	}
	
	public void updateLava(){
		
		int[] matrixDx = new int[_dx];
		int[] matrixDy = new int[_dy];
		for(int i = 0; i<_dx ; i++)
			matrixDx[i]=i;
		for(int i = 0; i<_dy ; i++)
			matrixDy[i]=i;
		ImprovedNoise.shuffleArray(matrixDx);
		ImprovedNoise.shuffleArray(matrixDy);
		if(lavaspeed%3!=0){
			lavaspeed+=1%3;
			for(int i : matrixDx)
				for(int j : matrixDy){
					if(isLava(i,j)){
						updateLavastatic(i,j);	
					}
				}
			return;
		}
		for(int i : matrixDx)
			for(int j : matrixDy){
				if(isLava(i,j)){
					updateLava(i,j);	
				}
			}
		lavaspeed+=1%3;
		
	}
	
	public void updateLavastatic(int _x, int _y){ // met a jour la lave lorsque qu'elle ne bouge pas
		setCellState(_x,_y,(int)(Math.random()*100)+160,(int)(Math.random()*50)+40,(int)(Math.random()*10)+30);
		
	}
	
	public void updateLava(int _x, int _y){
		
		double height = altitude[_x][_y];
		double[][] voisinage = new double[3][3];
				
		for (int x = _x -1 ; x < _x+2; x++)
		{
			for(int y = _y-1 ; y < _y+2 ; y++)
			{
				if(!checkBoundsBool(x,y)){
					voisinage[x-_x+1][y-_y+1] = 0;
				}
				else
					voisinage[x-_x+1][y-_y+1] = height-altitude[x][y];
			}
		}


		// voisinage est une matrice 3x3 contenant les differences d'altitude (on bannit les negatifs qui ont une altitude superieure
		double sum = 0;
		int nbDirections=0;
		for(int x = 0; x < 3; x++)
			for(int y = 0; y<3; y++){
				if(voisinage[x][y]>0){
					nbDirections++;
					sum+= voisinage[x][y];
				}
			}
		if(nbDirections==0)return;
		double[] tab = new double[nbDirections];
		int i = 0;
		for(int x = 0; x < 3; x++)
			for(int y = 0; y<3; y++){
				if(voisinage[x][y]>0){
					tab[i]=voisinage[x][y];
					i++;
				}
			}
		insertionSort(tab);
		double rand = Math.random()*sum;
		i=0;
		while(rand>tab[i]){
			rand-=tab[i];
			i++;
		}
		int x=0,y=0;
		
		for(int k = 0; k < 3; k++)
			for(int l = 0; l<3; l++){
				if(voisinage[k][l]==tab[i]){
					x = k+_x-1;
					y = l+_y-1;
				}
			}
		setLava(x,y,TRUE,300);
		removeLavaFluid(_x,_y,100);
		if(getLavaFluid(_x,_y)<=0){
			setLava(_x,_y,FALSE,300);
		}
		else if(getLavaLife(_x,_y)<=0){
			setLava(_x,_y,FALSE,300);
		}
		else if(getLavaLife(_x,_y)>0)
		{
			decreaseLavaLife(_x,_y);
		}
		
	}
	
	public void updateTime(){
		int a = hour;
		if(this.hour<24){
			hour=(hour+5)%24;
		}
		if(a<hour){}
		else{
			if(date[1]==1||date[1]==3||date[1]==5||date[1]==7||date[1]==8||date[1]==10||date[1]==12){ // mois à 31 jours
				if(date[0]<31){ // Si on est entre le 1 et le 30
					date[0]++;
				}
				else{ // Si on est le 31 du mois
					date[0]=1;
					date[1] = (date[1])%12+1; // Mois suivant
					if(date[1]==1){ // Si on revient en janvier, changement d'annee
						date[2]++;
					}
				}
				
			}
			else{
				if(date[1]==4||date[1]==6||date[1]==9||date[1]==11){// mois à 30 jours
					if(date[0]<30){ // Si on est entre le 1 et le 29
						date[0]++;
					}
					else{ // Si on est le 30 du mois
						date[0]=1;
						date[1] = date[1]+1; // Mois suivan
					}
				}
				else{ //En fevrier
					if(date[0]<28){ // Si on est entre le 1 et le 27
						date[0]++;
					}
					else{ // Si on est le 28 du mois
						date[0]=1;
						date[1] = date[1]+1; // Mois suivant
					}
				}
			}
		}
		
	}
	
	public void dispTime(){
		System.out.println(date[0]+"/"+date[1]+"/"+date[2]+" " + hour + ":00");
	}
	
	public String dispTimeString(){
		return "" + date[0]+"/"+date[1]+"/"+date[2]+" " + hour + ":00";
	}
	
	public int getSeason(){
		if(date[1]>=3&&date[1]<6) return 1; //PRINTEMPS
		if(date[1]>=6&&date[1]<9) return 2; //ETE
		if(date[1]>=9&&date[1]<12) return 3; //AUTOMNE
		if(date[1]==12||(date[1]>=1&&date[1]<3)) return 4; // HIVER
		return 0;
	}
	
	
	// display agents methods : 
	
	public void displayGroundAgent(int x, int y, Agent agent, CAImageBuffer image)
	{
		if(checkBoundsBool(x,y))image.setPixel(x, y, agent._redValue, agent._greenValue, agent._blueValue);
		if(isWaterCell(x, y)){
			if(checkBoundsBool(x-1,y))image.setPixel(x-1, y, (int)(getRedValue(x-1,y)*1.3),(int)(getGreenValue(x-1,y)*1.3),(int)(getBlueValue(x-1,y)*1.1));
			if(checkBoundsBool(x+1,y))image.setPixel(x+1, y, (int)(getRedValue(x+1,y)*1.3),(int)(getGreenValue(x+1,y)*1.3),(int)(getBlueValue(x+1,y)*1.1));
			if(checkBoundsBool(x,y+1))image.setPixel(x, y+1, (int)(getRedValue(x,y+1)*1.3),(int)(getGreenValue(x,y+1)*1.3),(int)(getBlueValue(x,y+1)*1.1));
			if(checkBoundsBool(x,y-1))image.setPixel(x, y-1, (int)(getRedValue(x,y-1)*1.3),(int)(getGreenValue(x,y-1)*1.3),(int)(getBlueValue(x,y-1)*1.1));
		}
	}
	
	public void displayWhale(int x, int y, Agent agent, CAImageBuffer image)
	{
		for(int k = x - 4; k <= x + 4 ; k++)
			for ( int l = y - 4 ; l <= y + 4 ; l ++)
				if(checkBoundsBool(k,l))image.setPixel(k, l, agent._redValue, agent._greenValue, agent._blueValue);
		if(agent._orient==0 || agent._orient==5 ){
			if(checkBoundsBool(x,y+5))image.setPixel(x,y+5,agent._redValue, agent._greenValue, agent._blueValue);
			if(checkBoundsBool(x,y+6))image.setPixel(x,y+6,agent._redValue, agent._greenValue, agent._blueValue);
			if(checkBoundsBool(x,y+7))image.setPixel(x,y+7,agent._redValue, agent._greenValue, agent._blueValue);
			if(checkBoundsBool(x-1,y+7))image.setPixel(x-1,y+7,agent._redValue, agent._greenValue, agent._blueValue);
			if(checkBoundsBool(x+1,y+7))image.setPixel(x+1,y+7,agent._redValue, agent._greenValue, agent._blueValue);
			if(checkBoundsBool(x,y-2))image.setPixel(x,y-2,agent._redValue, agent._greenValue, agent._blueValue);
		}
		if(agent._orient==1 || agent._orient==7 ){
			if(checkBoundsBool(x-5,y))image.setPixel(x-5,y,agent._redValue, agent._greenValue, agent._blueValue);
			if(checkBoundsBool(x-6,y))image.setPixel(x-6,y,agent._redValue, agent._greenValue, agent._blueValue);
			if(checkBoundsBool(x-7,y))image.setPixel(x-7,y,agent._redValue, agent._greenValue, agent._blueValue);
			if(checkBoundsBool(x-7,y-1))image.setPixel(x-7,y-1,agent._redValue, agent._greenValue, agent._blueValue);
			if(checkBoundsBool(x-7,y+1))image.setPixel(x-7,y+1,agent._redValue, agent._greenValue, agent._blueValue);
			if(checkBoundsBool(x+2,y-3))image.setPixel(x+2,y-3,agent._redValue, agent._greenValue, agent._blueValue);
		}
		if(agent._orient==2 || agent._orient==8 ){
			if(checkBoundsBool(x,y-5))image.setPixel(x,y-5,agent._redValue, agent._greenValue, agent._blueValue);
			if(checkBoundsBool(x,y-6))image.setPixel(x,y-6,agent._redValue, agent._greenValue, agent._blueValue);
			if(checkBoundsBool(x,y-7))image.setPixel(x,y-7,agent._redValue, agent._greenValue, agent._blueValue);
			if(checkBoundsBool(x-1,y-7))image.setPixel(x-1,y-7,agent._redValue, agent._greenValue, agent._blueValue);
			if(checkBoundsBool(x+1,y-7))image.setPixel(x+1,y-7,agent._redValue, agent._greenValue, agent._blueValue);
			if(checkBoundsBool(x,y+2))image.setPixel(x,y+2,agent._redValue, agent._greenValue, agent._blueValue);
		}
		if(agent._orient==3 || agent._orient==6){
			if(checkBoundsBool(x+5,y))image.setPixel(x+5,y,agent._redValue, agent._greenValue, agent._blueValue);
			if(checkBoundsBool(x+6,y))image.setPixel(x+6,y,agent._redValue, agent._greenValue, agent._blueValue);
			if(checkBoundsBool(x+7,y))image.setPixel(x+7,y,agent._redValue, agent._greenValue, agent._blueValue);
			if(checkBoundsBool(x+7,y-1))image.setPixel(x+7,y-1,agent._redValue, agent._greenValue, agent._blueValue);
			if(checkBoundsBool(x+7,y+1))image.setPixel(x+7,y+1,agent._redValue, agent._greenValue, agent._blueValue);
			if(checkBoundsBool(x-2,y-3))image.setPixel(x-2,y-3,agent._redValue, agent._greenValue, agent._blueValue);
		}
	}
	
	public void displayTuna(int x, int y, Agent agent, CAImageBuffer image)
	{

		for(int k = x - 1; k <= x + 1 ; k++)
			for ( int l = y - 1 ; l <= y + 1 ; l ++)
				if(checkBoundsBool(k,l))image.setPixel(k, l, agent._redValue, agent._greenValue, agent._blueValue);
		if(agent._orient==0 || agent._orient==5 || agent._orient==4){ // Position vers le nord
			if(checkBoundsBool(x,y+2))image.setPixel(x,y+2,agent._redValue, agent._greenValue, agent._blueValue);
			if(checkBoundsBool(x+1,y+2))image.setPixel(x+1,y+2,agent._redValue, agent._greenValue, agent._blueValue);
			if(checkBoundsBool(x-1,y+2))image.setPixel(x-1,y+2,agent._redValue, agent._greenValue, agent._blueValue);
			if(checkBoundsBool(x,y+3))image.setPixel(x,y+3,agent._redValue, agent._greenValue, agent._blueValue);
			if(checkBoundsBool(x+1,y+5))image.setPixel(x+1,y+5,agent._redValue, agent._greenValue, agent._blueValue);
			if(checkBoundsBool(x,y+4))image.setPixel(x,y+4,agent._redValue, agent._greenValue, agent._blueValue);
			if(checkBoundsBool(x-1,y+5))image.setPixel(x-1,y+5,agent._redValue, agent._greenValue, agent._blueValue);
		}
		if(agent._orient==1 || agent._orient==7 ){ // Position vers l'Est
			if(checkBoundsBool(x-2,y))image.setPixel(x-2,y,agent._redValue, agent._greenValue, agent._blueValue);
			if(checkBoundsBool(x-2,y+1))image.setPixel(x-2,y+1,agent._redValue, agent._greenValue, agent._blueValue);
			if(checkBoundsBool(x-2,y-1))image.setPixel(x-2,y-1,agent._redValue, agent._greenValue, agent._blueValue);
			if(checkBoundsBool(x-3,y))image.setPixel(x-3,y,agent._redValue, agent._greenValue, agent._blueValue);
			if(checkBoundsBool(x-4,y))image.setPixel(x-4,y,agent._redValue, agent._greenValue, agent._blueValue);
			if(checkBoundsBool(x-5,y+1))image.setPixel(x-5,y+1,agent._redValue, agent._greenValue, agent._blueValue);
			if(checkBoundsBool(x-5,y-1))image.setPixel(x-5,y-1,agent._redValue, agent._greenValue, agent._blueValue);
		}
		if(agent._orient==2 || agent._orient==8 ){ // Position vers le Sud
			if(checkBoundsBool(x,y-2))image.setPixel(x,y-2,agent._redValue, agent._greenValue, agent._blueValue);
			if(checkBoundsBool(x+1,y-2))image.setPixel(x+1,y-2,agent._redValue, agent._greenValue, agent._blueValue);
			if(checkBoundsBool(x-1,y-2))image.setPixel(x-1,y-2,agent._redValue, agent._greenValue, agent._blueValue);
			if(checkBoundsBool(x,y-3))image.setPixel(x,y-3,agent._redValue, agent._greenValue, agent._blueValue);
			if(checkBoundsBool(x+1,y-5))image.setPixel(x+1,y-5,agent._redValue, agent._greenValue, agent._blueValue);
			if(checkBoundsBool(x,y-4))image.setPixel(x,y-4,agent._redValue, agent._greenValue, agent._blueValue);
			if(checkBoundsBool(x-1,y-5))image.setPixel(x-1,y-5,agent._redValue, agent._greenValue, agent._blueValue);
		}
		if(agent._orient==3 || agent._orient==6){ // Position vers l'Ouest
			if(checkBoundsBool(x+2,y))image.setPixel(x+2,y,agent._redValue, agent._greenValue, agent._blueValue);
			if(checkBoundsBool(x+2,y+1))image.setPixel(x+2,y+1,agent._redValue, agent._greenValue, agent._blueValue);
			if(checkBoundsBool(x+2,y-1))image.setPixel(x+2,y-1,agent._redValue, agent._greenValue, agent._blueValue);
			if(checkBoundsBool(x+3,y))image.setPixel(x+3,y,agent._redValue, agent._greenValue, agent._blueValue);
			if(checkBoundsBool(x+4,y))image.setPixel(x+4,y,agent._redValue, agent._greenValue, agent._blueValue);
			if(checkBoundsBool(x+5,y+1))image.setPixel(x+5,y+1,agent._redValue, agent._greenValue, agent._blueValue);
			if(checkBoundsBool(x+5,y-1))image.setPixel(x+5,y-1,agent._redValue, agent._greenValue, agent._blueValue);
		}
	}
	
	public void displaySeagull(int x, int y, Agent agent, CAImageBuffer image)
	{

		double height; //hauteur
		int gap;
		if(checkBoundsBool(x,y)&&altitude[x][y]>0){
			height = agent.altitude - altitude[x][y];
			gap = (int)(height/(ImprovedNoise.maxValue+1)*11);
		}
		else{
			height = agent.altitude;
			gap = (int)(height/(ImprovedNoise.maxValue+1)*11);
		}
		int colors[] = new int[3];
		if(checkBoundsBool(x,y)){
			colors = getCellState(x,y);
		}
		if(checkBoundsBool(x,y))image.setPixel(x , y, agent._redValue, agent._greenValue, agent._blueValue);
		
		if(agent.cptAnimation==0)
		{
			// Ombres
			if(checkBoundsBool(x+gap,y+gap))image.setPixel(x+gap,y+gap,colors[0]/4, colors[1]/4, colors[2]/4);
			if(checkBoundsBool(x+1+gap,y-1+gap))image.setPixel(x+1+gap,y-1+gap,colors[0]/4, colors[1]/4, colors[2]/4);
			if(checkBoundsBool(x-1+gap,y-1+gap))image.setPixel(x-1+gap,y-1+gap,colors[0]/4, colors[1]/4, colors[2]/4);
			if(checkBoundsBool(x+2+gap,y-2+gap))image.setPixel(x+2+gap,y-2+gap,colors[0]/4, colors[1]/4, colors[2]/4);
			if(checkBoundsBool(x-2+gap,y-2+gap))image.setPixel(x-2+gap,y-2+gap,colors[0]/4, colors[1]/4, colors[2]/4);
			//Mouette
			if(checkBoundsBool(x+1,y-1))image.setPixel(x+1,y-1,agent._redValue, agent._greenValue, agent._blueValue);
			if(checkBoundsBool(x-1,y-1))image.setPixel(x-1,y-1,agent._redValue, agent._greenValue, agent._blueValue);
			if(checkBoundsBool(x+2,y-2))image.setPixel(x+2,y-2,agent._redValue, agent._greenValue, agent._blueValue);
			if(checkBoundsBool(x-2,y-2))image.setPixel(x-2,y-2,agent._redValue, agent._greenValue, agent._blueValue);
			
		}
		if(agent.cptAnimation==1)
		{
			// Ombres
			if(checkBoundsBool(x+gap,y+gap))image.setPixel((x+gap),(y+gap),colors[0]/5, colors[1]/5, colors[2]/5);
			if(checkBoundsBool(x+1+gap,y-1+gap))image.setPixel((x+1+gap),(y-1+gap),colors[0]/5, colors[1]/5, colors[2]/5);
			if(checkBoundsBool(x-1+gap,y-1+gap))image.setPixel((x-1+gap),(y-1+gap),colors[0]/5, colors[1]/5, colors[2]/5);
			if(checkBoundsBool(x+2+gap,y-1+gap))image.setPixel((x+2+gap),(y-1+gap),colors[0]/5, colors[1]/5, colors[2]/5);
			if(checkBoundsBool(x-2+gap,y-1+gap))image.setPixel((x-2+gap),(y-1+gap),colors[0]/5, colors[1]/5, colors[2]/5);
			//Mouette
			if(checkBoundsBool(x+1,y-1))image.setPixel(x+1,y-1,agent._redValue, agent._greenValue, agent._blueValue);
			if(checkBoundsBool(x-1,y-1))image.setPixel(x-1,y-1,agent._redValue, agent._greenValue, agent._blueValue);
			if(checkBoundsBool(x+2,y-1))image.setPixel(x+2,y-1,agent._redValue, agent._greenValue, agent._blueValue);
			if(checkBoundsBool(x-2,y-1))image.setPixel(x-2,y-1,agent._redValue, agent._greenValue, agent._blueValue);
			
		}
		if(agent.cptAnimation==2)
		{
			// Ombres
			if(checkBoundsBool(x+gap,y+gap))image.setPixel(x+gap,y+gap,colors[0]/5, colors[1]/5, colors[2]/5);
			if(checkBoundsBool(x+1+gap,y-1+gap))image.setPixel(x+1+gap,y-1+gap,colors[0]/5, colors[1]/5, colors[2]/5);
			if(checkBoundsBool(x-1+gap,y-1+gap))image.setPixel(x-1+gap,y-1+gap,colors[0]/5, colors[1]/5, colors[2]/5);
			if(checkBoundsBool(x+2+gap,y+gap))image.setPixel(x+2+gap,y+gap,colors[0]/5, colors[1]/5, colors[2]/5);
			if(checkBoundsBool(x-2+gap,y+gap))image.setPixel(x-2+gap,y+gap,colors[0]/5, colors[1]/5, colors[2]/5);
			// Mouette
			if(checkBoundsBool(x+1,y-1))image.setPixel(x+1,y-1,agent._redValue, agent._greenValue, agent._blueValue);
			if(checkBoundsBool(x-1,y-1))image.setPixel(x-1,y-1,agent._redValue, agent._greenValue, agent._blueValue);
			if(checkBoundsBool(x+2,y))image.setPixel(x+2,y,agent._redValue, agent._greenValue, agent._blueValue);
			if(checkBoundsBool(x-2,y))image.setPixel(x-2,y,agent._redValue, agent._greenValue, agent._blueValue);
			
		}
		if(agent.cptAnimation==3)
		{
			// Ombres
			if(checkBoundsBool(x+gap,y+gap))image.setPixel((x+gap),(y+gap),colors[0]/5, colors[1]/5, colors[2]/5);
			if(checkBoundsBool(x+1+gap,y-1+gap))image.setPixel((x+1+gap),(y-1+gap),colors[0]/5, colors[1]/5, colors[2]/5);
			if(checkBoundsBool(x-1+gap,y-1+gap))image.setPixel((x-1+gap),(y-1+gap),colors[0]/5, colors[1]/5, colors[2]/5);
			if(checkBoundsBool(x+2+gap,y-1+gap))image.setPixel((x+2+gap),(y-1+gap),colors[0]/5, colors[1]/5, colors[2]/5);
			if(checkBoundsBool(x-2+gap,y-1+gap))image.setPixel((x-2+gap),(y-1+gap),colors[0]/5, colors[1]/5, colors[2]/5);
			//Mouette
			if(checkBoundsBool(x+1,y-1))image.setPixel(x+1,y-1,agent._redValue, agent._greenValue, agent._blueValue);
			if(checkBoundsBool(x-1,y-1))image.setPixel(x-1,y-1,agent._redValue, agent._greenValue, agent._blueValue);
			if(checkBoundsBool(x+2,y-1))image.setPixel(x+2,y-1,agent._redValue, agent._greenValue, agent._blueValue);
			if(checkBoundsBool(x-2,y-1))image.setPixel(x-2,y-1,agent._redValue, agent._greenValue, agent._blueValue);
			
		}
	}
	
	public void displayTornado(int x, int y, Agent agent, CAImageBuffer image)
	{
		if(checkBoundsBool(x,y))image.setPixel(x, y, agent._redValue, agent._greenValue, agent._blueValue);
		for(int i = x-agent.tornadoRadius ; i <= x+agent.tornadoRadius ; i ++)
			for (int j = y-agent.tornadoRadius ; j <= y+agent.tornadoRadius ; j++)
			{
				if(checkBoundsBool(i,j)&&Math.random()<0.5){
					if((i-x)*(i-x)+(j-y)*(j-y) < Math.pow(agent.tornadoRadius,2))
					{ 
						int gray = (int)(Math.random()*30)+120;
						image.setPixel(i, j, gray, gray, gray);
				
					}
				}
			}
				

	}
	
	/**
	 * Update the world state and return an array for the current world state (may be used for display)
	 * @return
	 */
	
	
	
	
	public void step ( )
	{
		stepWorld();
		stepAgents();
		
		if ( buffering && cloneBuffer )
		{
			if ( activeIndex == 0 )
				for ( int x = 0 ; x != _dx ; x++ )
					for ( int y = 0 ; y != _dy ; y++ )
					{
						for ( int z = 0 ; z != 22 ; z ++){
							Buffer1[x][y][z] = Buffer0[x][y][z];
						}
						for ( int z = 0 ; z != 2 ; z ++){
							Buffer3[x][y][z] = Buffer2[x][y][z];
						}
					}
			else
				for ( int x = 0 ; x != _dx ; x++ )
					for ( int y = 0 ; y != _dy ; y++ )
					{
						for ( int z = 0 ; z != 22 ; z ++){
							Buffer0[x][y][z] = Buffer1[x][y][z];
						}
						for ( int z = 0 ; z != 2 ; z ++){
							Buffer2[x][y][z] = Buffer3[x][y][z];
						}
					}

			activeIndex = (activeIndex + 1 ) % 2; // switch buffer index
		}

	}
	
	public int[][][] getCurrentBuffer()
	{
		if ( activeIndex == 0 || buffering == false ) 
			return Buffer0;
		else
			return Buffer1;		
	}
	
	public int getWidth()
	{
		return _dx;
	}
	
	public int getHeight()
	{
		return _dy;
	}
	
	public void add (Agent agent)
	{
		agents.add(agent);
	}
	
	public void stepWorld() // world THEN agents
	{
		dispTime();
		updateTime();
		boolean seasonChange = testSeasonchange!=getSeason();
		if(seasonChange) testSeasonchange = getSeason();
		cptWateranimation = (cptWateranimation+1)%10;
		
	
		if(eruptionOn){
			updateMagma();
		
		}
		updateLava();
		for ( int x = 0 ; x != _dx ; x++ )
			for ( int y = 0 ; y != _dy ; y++ )
			{
				if(isWaterCell(x,y)){
					updateWater(x,y);
				}
				if(isDirtCell(x,y)){
					if(Math.random()<0.01)
						setGrassCell(x,y);
				}
				if(seasonChange){
					cptSeason = _dx*_dy*2;
				}
				if(cptSeason>0){
					if(!isTreeCell(x,y)&&isGrassCell(x,y)&&!isWaterCell(x,y)&&!isPlantCell(x,y)&&!isSandCell(x,y)&&!isFireCell(x,y)&&!isVolcano(x,y)&&!isDirtCell(x,y)&&Math.random()<0.05){
						setGrassCell(x,y);
						cptSeason--;
					}
				}
				if(isGrassCell(x,y)&&!isFireCell(x,y)&&!isAshesCell(x,y)&&!isWaterCell(x,y)&&!isInitWaterCell(x,y)&&!isVolcano(x,y)&&!isSandCell(x,y)) { 
					if(Math.random()<=p1) setTreeCell(x,y,TRUE); // Si la case courante est de l'herbe, elle a p1 probabilite de devenir un arbre
					if(Math.random()<=p3) setPlantCell(x,y,TRUE);
				}
				if(getSeason()==4&&isPlantCell(x,y))
					setPlantCell(x,y,FALSE);
				if(isTreeCell(x,y)||isPlantCell(x,y)){ //Si arbre ou plante
					if(Math.random()<p2){ // p2 probabilite de prendre feu
						setFireCell(x,y);
					}
					for ( int k = -1; k <= 1; k ++ ){
						for ( int l = -1 ; l <= 1; l ++){
							if(k==0&&l==0||!checkBoundsBool(k+x,l+y))continue;
							if(isFireCell(k+x,l+y)||isLava(k+x,l+y)){
								if((k==0&&(l==-1||l==1))||(l==0&&(k==-1||k==1))){
									if(Math.random()<0.7){
										setFireCell(x,y);
										break;
									}
								}
								else{
									if(Math.random()<0.3){
									setFireCell(x,y);
									break;
									}
								}
							}
						}
					}
				}
				
				if(isFireCell(x,y))
				{
					updateFire(x,y);
					continue;
				}
				if(isAshesCell(x,y))
				{
					updateAshes(x,y);
					continue;
				}

				
			}
	}
	
	public void stepAgents() // world THEN agents
	{
		for ( int i = agents.size()-1 ; i >= 0 ; i-- )
		{
			synchronized ( Buffer0 ) {
				agents.get(i).step();
			}
			
		}
	}
	
	
	
	public void display( CAImageBuffer image )
	{
		image.update(this.getCurrentBuffer());

		for ( int i = 0 ; i != agents.size() ; i++ ){
			if(agents.get(i) instanceof GroundAgent)
			{
				displayGroundAgent(agents.get(i)._x,agents.get(i)._y,agents.get(i),image);
			}
			if(agents.get(i) instanceof Whale){ 
				displayWhale(agents.get(i)._x,agents.get(i)._y,agents.get(i),image);
				this.numWhale++;
			}
			
			if(agents.get(i) instanceof Tuna){
				displayTuna(agents.get(i)._x,agents.get(i)._y,agents.get(i),image);
				this.numTuna++;
			}
			
		}
		for ( int i = 0 ; i != agents.size() ; i++ ){
			
			if(agents.get(i) instanceof Seagull){
				displaySeagull(agents.get(i)._x,agents.get(i)._y,agents.get(i),image);
				this.numSeagull++;
			}
			if(agents.get(i) instanceof Tornado)
			{
				displayTornado(agents.get(i)._x,agents.get(i)._y,agents.get(i),image);
				
			}
		}
		
		
	}
	
}

