import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 * A simple model of a rabbit.
 * Rabbits age, move, breed, and die.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 7.1
 */
public class leopard extends prey
{
    // Characteristics shared by all rabbits (class variables).
    // The age at which a rabbit can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a rabbit can live.
    private static final int MAX_AGE = 100;
    // The likelihood of a rabbit breeding.
    private static double BREEDING_PROBABILITY = 0.22;
    //value upon eating a rabbit 
    private static final int FOOD_VALUE = 19;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 3;
    // The maximum food level a leopard can have
    private static final int MAX_FOOD_LEVEL = 22;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    
    // The rabbit's age.
    private int age;
    // The leopard's food level, which is increased by eating.
    private int foodLevel;

    /**
     * Create a new rabbit. A rabbit may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the rabbit will have a random age.
     * @param location The location within the field.
     */
    public leopard(boolean randomAge, Location location)
    {
        super(location);
        age = 0;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
        foodLevel = rand.nextInt(MAX_FOOD_LEVEL);
    }
    
    /**
     * This is what the leopard does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param currentField The field occupied.
     * @param nextFieldState The updated field.
     * @param steps The current step number in the simulation.
     */
    public void act(Field currentField, Field nextFieldState, int steps)
    {
        int leo = 0;
        incrementAge();
        incrementHunger();
        if(!nightTime(steps)) { // Deer only act during the day, because they are nocturnal animals
            if(isAlive()) {
                List<Location> freeLocations = 
                    nextFieldState.getFreeAdjacentLocations(getLocation());
                if(!freeLocations.isEmpty()) {
                    List<Animal> animals = currentField.getAnimals();
                    for (Animal animal : animals) {
                        if (animal instanceof leopard Leopard) {
                            leo++;
                        }
                    }
                    if (leo > 10000) { // If there are more than 10000 gazelles in the field, decrease the breeding probability to prevent overpopulation
                        BREEDING_PROBABILITY = 0.1;
                    }else if(leo <100){
                        BREEDING_PROBABILITY = 0.65; // increase the breeding probability to aviod extinction
                    }else if(leo <1000){
                        BREEDING_PROBABILITY = 0.30; // increase the breeding probability to encourage population growth when the population is under control
                    } else {
                        BREEDING_PROBABILITY = 0.22; // Reset the breeding probability to its original value when the population is under control
                    }
                    giveBirth(currentField, nextFieldState, freeLocations);
                }
                // Try to move into a free location.
                Location nextLocation = findFood(currentField);
                if(nextLocation == null && ! freeLocations.isEmpty()) {
                    // No food found - try to move to a free location.
                    nextLocation = freeLocations.remove(0);
                }
                // See if it was possible to move.
                if(nextLocation != null) {
                    setLocation(nextLocation);
                    nextFieldState.placeAnimal(this, nextLocation);
                }
                else {
                    // Overcrowding.
                    setDead();
                }
            }
        }
    }


    /**
     * Make this fox more hungry. This could result in the fox's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }


    
    public int getEaten() {
        setDead();
        return FOOD_VALUE; // Return a the food value of the eaten rabbit
    }

    @Override
    public String toString() {
        return "leopard{" +
                "age=" + age +
                ", alive=" + isAlive() +
                ", location=" + getLocation() +
                '}';
    }

    /**
     * Increase the age.
     * This could result in the rabbit's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * get the age of the animal.
     */
    public int getAge() {
        return age;
    }



    /**
     * Look for rabbits adjacent to the current location.
     * Only the first live rabbit is eaten.
     * @param field The field currently occupied.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood(Field field)
    {
        List<Location> adjacent = field.getAdjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        Location foodLocation = null;
        while(foodLocation == null && it.hasNext()) {
            Location loc = it.next();
            Animal animal = field.getAnimalAt(loc);
            if(animal instanceof prey prey && !(animal instanceof leopard)) {
                if(prey.isAlive()) {
                    int foodValue = prey.getEaten();
                    foodLevel += foodValue;
                    foodLocation = loc;
                }
            }
            
        }
        return foodLocation;
    }
    
    /**
     * Look for a mate adjacent to the current location.
     * Only the first live mate is considered.
     * @param field The field currently occupied.
     * @return true if a mate was found, false otherwise.
     */
    private boolean findMate(Field field)
    {
        List<Location> adjacent = field.getAdjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location loc = it.next();
            Animal animal = field.getAnimalAt(loc);
            if(animal instanceof leopard Leopard && Leopard.getGender() == 1) {
                if(Leopard.isAlive() && Leopard.getAge() >= BREEDING_AGE) {
                    return true;
                }
            }
            
        }
        return false;
    }
    
    /**
     * Check whether this fox is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param freeLocations The locations that are free in the current field.
     */
    private void giveBirth(Field currentField, Field nextFieldState, List<Location> freeLocations)
    {
        // New foxes are born into adjacent locations.
        // Get a list of adjacent free locations.
        int births = breed(currentField);  
        if(births > 0) { 
            for (int b = 0; b < births && ! freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                leopard young = new leopard(false, loc);
                nextFieldState.placeAnimal(young, loc);
            }
        }
    }
        
    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    private int breed(Field currentField)
    {
        int births;
        if(canBreed(currentField) && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        else {
            births = 0;
        }
        return births;
    }

    /**
     * A fox can breed if it has reached the breeding age.
     */
    protected boolean canBreed(Field currentField)
    {
        return age >= BREEDING_AGE && findMate(currentField) && getGender() == 2; // Only a female lion can give birth, and only if both are above the age limit and there is a male lion adjacent in the current field 
    }
}
