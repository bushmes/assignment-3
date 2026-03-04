import java.util.*;

/**
 * Represent a rectangular grid of field positions.
 * Each position is able to store a single animal/object.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 7.0
 */
public class Field
{
    // A random number generator for providing random locations.
    private static final Random rand = Randomizer.getRandom();
    
    // The dimensions of the field.
    private final int depth, width;
    // Animals mapped by location.
    private final Map<Location, Animal> field = new HashMap<>();
    // The animals.
    private final List<Animal> animals = new ArrayList<>();


    // Amount of plant food available at each location
    private final int[][] plants;

    // Plant configuraton
    private static final int MAX_PLANT_LEVEL = 6;
    private static final double PLANT_GROWTH_PROBABILITY = 0.05;

    //Store weather in field state
    private Weather weather = Weather.SUNNY;

    /**
     * Represent a field of the given dimensions.
     * @param depth The depth of the field.
     * @param width The width of the field.
     */
    public Field(int depth, int width)
    {
        this.depth = depth;
        this.width = width;
        this.plants = new int[depth][width];
    }

    // getters and setters for weather
    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    public void copyWeatherFrom(Field other) {
        this.weather = other.weather;
    }

    // copy plant levels from another field
    public void copyPlantsFrom(Field other){
        for(int i = 0;i < depth; i++){
            System.arraycopy(other.plants[i], 0, this.plants[i], 0, width);
        }
    }

    // initialize the field with plants based on a given initial probability
    public void seedPlants(double initialProbability){
        for(int i = 0; i < depth; i++){
            for(int j = 0; j < width; j++){
                if(rand.nextDouble() <= initialProbability){
                    plants[i][j] = 1 +rand.nextInt(Math.max(1, MAX_PLANT_LEVEL / 2));
                } else {
                    plants[i][j] = 0;
                }
            }
        }
    }

    // method to grow plants based on the current weather conditions
    public void growPlants(Weather weather){
        double growthProb = PLANT_GROWTH_PROBABILITY;

        if(weather == Weather.RAINY) growthProb *= 2.0;
        else if(weather == Weather.FOGGY) growthProb *= 0.7;

        for (int i = 0; i < depth; i++) {
            for (int j = 0; j < width; j++) {
                if (plants[i][j] < MAX_PLANT_LEVEL && rand.nextDouble() <= growthProb) {
                    plants[i][j]++;
                }
            }
        }
    }

    // method to get the current plant level at a given location
    public int getPlantLevelAt(Location location){
        return plants[location.row()][location.col()];
    }

    // method to consume a plant at a given location, returns true if successful
    public boolean consumePlantAt(Location location)
    {
        int i = location.row();
        int j = location.col();
        if (plants[i][j] > 0) {
            plants[i][j]--;
            return true;
        }
        return false;
    }

    /**
     * Place an animal at the given location.
     * If there is already an animal at the location it will
     * be lost.
     * @param anAnimal The animal to be placed.
     * @param location Where to place the animal.
     */
    public void placeAnimal(Animal anAnimal, Location location)
    {
        assert location != null;
        Object other = field.get(location);
        if(other != null) {
            animals.remove(other);
        }
        field.put(location, anAnimal);
        animals.add(anAnimal);
    }
    
    /**
     * Return the animal at the given location, if any.
     * @param location Where in the field.
     * @return The animal at the given location, or null if there is none.
     */
    public Animal getAnimalAt(Location location)
    {
        return field.get(location);
    }

    /**
     * Get a shuffled list of the free adjacent locations.
     * @param location Get locations adjacent to this.
     * @return A list of free adjacent locations.
     */
    public List<Location> getFreeAdjacentLocations(Location location)
    {
        List<Location> free = new LinkedList<>();
        List<Location> adjacent = getAdjacentLocations(location);
        for(Location next : adjacent) {
            Animal anAnimal = field.get(next);
            if(anAnimal == null) {
                free.add(next);
            }
            else if(!anAnimal.isAlive()) {
                free.add(next);
            }
        }
        return free;
    }

    /**
     * Return a shuffled list of locations adjacent to the given one.
     * The list will not include the location itself.
     * All locations will lie within the grid.
     * @param location The location from which to generate adjacencies.
     * @return A list of locations adjacent to that given.
     */
    public List<Location> getAdjacentLocations(Location location)
    {
        // The list of locations to be returned.
        List<Location> locations = new ArrayList<>();
        if(location != null) {
            int row = location.row();
            int col = location.col();
            for(int roffset = -1; roffset <= 1; roffset++) {
                int nextRow = row + roffset;
                if(nextRow >= 0 && nextRow < depth) {
                    for(int coffset = -1; coffset <= 1; coffset++) {
                        int nextCol = col + coffset;
                        // Exclude invalid locations and the original location.
                        if(nextCol >= 0 && nextCol < width && (roffset != 0 || coffset != 0)) {
                            locations.add(new Location(nextRow, nextCol));
                        }
                    }
                }
            }
            
            // Shuffle the list. Several other methods rely on the list
            // being in a random order.
            Collections.shuffle(locations, rand);
        }
        return locations;
    }

    // method to get a list of adjacent animals to a given location
    public List<Animal> getAdjacentAnimals(Location location){
        List<Animal> adjacentAnimals = new ArrayList<>();
        for(Location loc : getAdjacentLocations(location)) {
            Animal a = getAnimalAt(loc);
            if(a != null && a.isAlive()) {
                adjacentAnimals.add(a);
            }
        }
        return adjacentAnimals;
    }

    /**
     * Print out the number of leopards, deers, gazzels, tigers and lions in the field.
     */
    public void fieldStats() {
    // 1. Initialize counters for both genders for every species
    int mLeopards = 0, fLeopards = 0;
    int mDeers = 0, fDeers = 0;
    int mGazzels = 0, fGazzels = 0;
    int mTigers = 0, fTigers = 0;
    int mLions = 0, fLions = 0;

    // 2. Iterate through the field
    for (Animal anAnimal : field.values()) {
        // We only care if the animal is alive
        if (anAnimal.isAlive()) {
            
            // LEOPARDS
            if (anAnimal instanceof leopard) {
                if (anAnimal.getGender() == 1) {
                    mLeopards++;
                } else {
                    fLeopards++;
                }
            } 
            // DEERS
            else if (anAnimal instanceof deer) {
                if (anAnimal.getGender() == 1) {
                    mDeers++;
                } else {
                    fDeers++;
                }
            }
            // GAZZELS
            else if (anAnimal instanceof gazzell) {
                if (anAnimal.getGender() == 1) {
                    mGazzels++;
                } else {
                    fGazzels++;
                }
            }
            // TIGERS
            else if (anAnimal instanceof tiger) {
                if (anAnimal.getGender() == 1) {
                    mTigers++;
                } else {
                    fTigers++;
                }
            }
            // LIONS
            else if (anAnimal instanceof lion) {
                if (anAnimal.getGender() == 1) {
                    mLions++;
                } else {
                    fLions++;
                }
            }
        }
    }

    // 3. Print the results clearly
    System.out.println("Leopards: M-" + mLeopards + " F-" + fLeopards +
                       " | Deers: M-" + mDeers + " F-" + fDeers +
                       " | Gazzels: M-" + mGazzels + " F-" + fGazzels +
                       " | Tigers: M-" + mTigers + " F-" + fTigers +
                       " | Lions: M-" + mLions + " F-" + fLions);
}

    /**
     * Empty the field.
     */
    public void clear()
    {
        field.clear();
    }

    /**
     * Return whether there is at least two or more species in the field.
     * @return true if there is at least two or more species in the field.
     */
    public boolean isViable()
    {
        boolean leopardFound = false;
        boolean deerFound = false;
        boolean gazzellFound = false;
        boolean tigerFound = false;
        boolean lionFound = false;
        int numSpecies = 0;
        Iterator<Animal> it = field.values().iterator();
        while(it.hasNext() && ! (leopardFound && deerFound && gazzellFound && tigerFound && lionFound)) {
            Animal anAnimal = it.next();
            if(anAnimal instanceof leopard leopard) {
                if(leopard.isAlive()) {
                    leopardFound = true;
                }
            }
            else if(anAnimal instanceof deer deer) {
                if(deer.isAlive()) {
                    deerFound = true;
                }
            }
            else if(anAnimal instanceof gazzell gazzell) {
                if(gazzell.isAlive()) {
                    gazzellFound = true;
                }
            }
            else if(anAnimal instanceof tiger tiger) {
                if(tiger.isAlive()) {
                    tigerFound = true;
                }
            }
            else if(anAnimal instanceof lion lion) {
                if(lion.isAlive()) {
                    lionFound = true;
                }
            }
        }
        if(leopardFound) {
            numSpecies++;
        }
        if(deerFound) {
            numSpecies++;
        }
        if(gazzellFound) {
            numSpecies++;
        }
        if(tigerFound) {
            numSpecies++;
        }
        if(lionFound) {
            numSpecies++;
        }
        // if there is at least two or more species in the field, return true.
        boolean viable = false;
        if(numSpecies >= 2) {
            viable = true;
        }
        return viable;
    }
    
    /**
     * Get the list of animals.
     */
    public List<Animal> getAnimals()
    {
        return animals;
    }

    /**
     * Return the depth of the field.
     * @return The depth of the field.
     */
    public int getDepth()
    {
        return depth;
    }
    
    /**
     * Return the width of the field.
     * @return The width of the field.
     */
    public int getWidth()
    {
        return width;
    }
}
