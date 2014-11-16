
public abstract class Agent {

	World _world;
	
	static int redId   = 0;
	static int greenId = 1;
	static int blueId  = 2;
	
	int 	_x;
	int 	_y;
	int 	_orient;
	int 	_etat;
	
	int 	_redValue;
	int 	_greenValue;
	int 	_blueValue;
	int 	satiety;
	boolean _alive;
	boolean _male;
	int dx;
	int dy;
	
	protected int speedcpt; 
	protected int speed; // Plus speed est grand, plus l'agent est rapide
	protected int idlespeedcpt;
	protected int idlespeed; // Plus idlespeed est grand, plus l'agent est lent en mode 'repos'
	protected int swimmingAbilities; // determine l'abilite a nager, si il vaut zero alors l'agent ne sait pas nager
	protected int N,L; // N : indice de reproduction, L : indice de vie
	protected int sightRadius; // valeur du rayon du champ de vision
	protected int cptAnimation; // compteur de loop pour l'animation de certains agents
	protected int tornadoRadius; // taille des agents de type tornade
	protected double altitude; // entre -1 et 1, 0 determine le niveau de la mer
	

	
	public Agent( int __x, int __y, World __w )
	{
		_x = __x;
		_y = __y;
		_world = __w;
		dx = _world.getHeight();
		dy = _world.getWidth();
		
		_redValue = 0;
		_greenValue = 0;
		_blueValue = 0;
		
		_orient = 0;
		_alive = true;
	}
	
	abstract public void step( );
	
	abstract public boolean isObstacleCell(int _x, int _y);
}
