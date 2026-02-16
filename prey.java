
/**
 * Common elements of foxes and rabbits.
 *
 * @author David J. Barnes and Michael Kölling
 * @version 7.0
 */
public abstract class prey extends Animal
{


    public prey(Location location)
    {
        super(location); 
    }
    
    /**
     * getEaten.
     */
    abstract public int getEaten();
}
