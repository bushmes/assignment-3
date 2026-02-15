
/**
 * Common elements of foxes and rabbits.
 *
 * @author David J. Barnes and Michael Kölling
 * @version 7.0
 */
public abstract class Animal
{

    private boolean alive;
    // The animal's position.
    private Location location;
    
    private boolean infected = false;
    private int infectedSteps = 0;


    /**
     * Constructor for objects of class Animal.
     * @param location The animal's location.
     */
    public Animal(Location location)
    {
        this.alive = true;
        this.location = location;
    }
    
    /**
     * Act.
     * @param currentField The current state of the field.
     * @param nextFieldState The new state being built.
     */
    abstract public void act(Field currentField, Field nextFieldState);
    
    /**
     * Check whether the animal is alive or not.
     * @return true if the animal is still alive.
     */
    public boolean isAlive()
    {
        return alive;
    }

    /**
     * Indicate that the animal is no longer alive.
     */
    protected void setDead()
    {
        alive = false;
        location = null;
    }
    
    /**
     * Return the animal's location.
     * @return The animal's location.
     */
    public Location getLocation()
    {
        return location;
    }
    
    /**
     * Set the animal's location.
     * @param location The new location.
     */
    protected void setLocation(Location location)
    {
        this.location = location;
    }

    public boolean isInfected() {
        return infected;
    }

    public void infect() {
        infected = true;
        infectedSteps = 0;
    }

    
     // Update disease progression for one step.
    protected void progressDisease(double deathProbAfterIncubation, int incubationSteps) {
        if(!infected) return;

        infectedSteps++;

        if(infectedSteps >= incubationSteps) {
            if(Randomizer.getRandom().nextDouble() <= deathProbAfterIncubation) {
                setDead();
            }
        }
    }

}
