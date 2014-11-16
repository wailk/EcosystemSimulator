
public abstract class MarineAgent extends Agent{

	public MarineAgent( int __x, int __y, World __w )
	{
		super(__x,__y,__w);
		do{
			this._orient = (int)(Math.random()*9);
		}
		while(this._orient==4);
	}
	
	abstract public void step( );

	public boolean isObstacleCell(int _x, int _y){
		return !_world.checkBoundsBool(_x, _y) || _world.isSandCell(_x, _y) || _world.isGrassCell(_x, _y) || _world.isLava(_x, _y);
	}
}
