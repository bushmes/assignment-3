
/**
 * Common elements of foxes and rabbits.
 *
 * @author David J. Barnes and Michael Kölling
 * @version 7.0
 */
import java.util.Random;

public abstract class Animal
{

    private boolean alive;
    // The animal's position.
    private Location location;
    // shared random generator for subclasses
    protected static final Random rand = Randomizer.getRandom();
    // gender: 1 = male, 2 = female
    protected int gender;

    /**
     * Constructor for objects of class Animal.
     * @param location The animal's location.
     */
    public Animal(Location location)
    {
        this.alive = true;
        this.location = location;
        // assign random gender to every new animal
        this.gender = rand.nextInt(2) + 1;
    }
    
    /**
     * Act.
     * @param currentField The current state of the field.
     * @param nextFieldState The new state being built.
     */
    abstract public void act(Field currentField, Field nextFieldState);

    /**
     * check whether the animal can breed. by checking the gender and age of the animal.
     * @return true if the animal can breed, false otherwise.
     */
    abstract protected boolean canBreed(Field currentField);

    
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

    /**
     * Return the animal's gender: 1 = male, 2 = female.
     */
    public int getGender()
    {
        return gender;
    }
}
