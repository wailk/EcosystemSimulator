
public abstract class FlyingAgent extends Agent{
	
	
	public FlyingAgent( int __x, int __y, World __w )
	{
		super(__x,__y,__w);
	}
	
	abstract public void step( );
	
	public boolean isObstacleCell(int _x, int _y)
	{
		return !_world.checkBoundsBool(_x, _y);
	}
}
