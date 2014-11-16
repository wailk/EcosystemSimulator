
public abstract class GroundAgent extends Agent{

	public GroundAgent( int __x, int __y, World __w )
	{
		super(__x,__y,__w);
	}
	
	abstract public void step( );
	
	public boolean isObstacleCell(int _x, int _y){
		return _world.isTreeCell(_x, _y) || _world.isWaterCell(_x, _y) || _world.isCrater(_x, _y) || !_world.checkBoundsBool(_x, _y);
	}
	
	public boolean isInitObstacleCell(int _x, int _y){
		return _world.isTreeCell(_x, _y) || _world.isInitWaterCell(_x, _y) || _world.isCrater(_x, _y) || !_world.checkBoundsBool(_x, _y);
	}
}
