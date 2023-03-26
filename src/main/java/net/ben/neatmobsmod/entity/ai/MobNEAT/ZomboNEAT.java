package net.ben.neatmobsmod.entity.ai.MobNEAT;

import net.ben.neatmobsmod.NEAT.Environment;
import net.ben.neatmobsmod.NEAT.Genome;
import net.ben.neatmobsmod.NEAT.Pool;
import net.ben.neatmobsmod.NEAT.Species;
import net.ben.neatmobsmod.NEATMobsModClient;
import net.ben.neatmobsmod.entity.custom.ZomboEntity;
import java.util.ArrayList;


public class ZomboNEAT implements Environment {

    // shows if best are genomes currently being showcased
    private  boolean generatingBest = false;
    /*
    shows if best genomes should be showcased in
    the next generation
     */
    private boolean generateBestNext = false;


    private boolean generationEvaluationDone = false;
    private int currentGenome = 0;
    private Pool pool;
    private ArrayList<Genome> genomes = new ArrayList<>();
    private int generation = 0;
    private int genomesEvaluated = 0;
    private float lastBestScore = 0;
    private int bestGenome = 0;
    private int villagerNum = 0;


    /*
    set the genome number of a Zombo to the value of the current genome
    or the value of the best genome in the generation
     */
    public void assignNetwork(ZomboEntity zomboEntity){
        if (currentGenome == 0 && generatingBest == false){
            lastBestScore = 0;
        }
        //checks if network is training or showcasing best network
        if (generatingBest){
            zomboEntity.setGenomeNum(bestGenome);
        } else {
            zomboEntity.setGenomeNum(currentGenome);
        }
        /*
        increments value of current networks unless it equals the
        total number of networks
         */
        if (currentGenome!=genomes.size()) {
            currentGenome ++;
        }


    }

    /*
    initialises the first generation of networks and
    stores each network in an array list
     */

    public ZomboNEAT(){
        this.pool = new Pool();
        this.pool.initializePool();

        for(Species s: this.pool.getSpecies()){
            for(Genome g: s.getGenomes()){
                genomes.add(g);
            }
        }
    }

    /*
     returns boolean based on whether all genomes of the current generation have been assigned
     */
    public boolean populationComplete(){
        return currentGenome==genomes.size();
    }


    /*
    returns the genome at the passed index.
    due to multiple instances of Zombos being created simultaneously
    it is possible (but rare) for Zombos to have genomes that are out of range.
    the method assigns Zombos the genome at greatest index.
     */
    public Genome getMobGenome(int genomeNum){

        while (genomeNum >= genomes.size()){
            genomeNum--;
        }

        Genome mobGenome = genomes.get(genomeNum);

        return mobGenome;
    }


    /*
    assigns the genome of the provided Zombo a fitness
    based on the Zombo's performance
     */
    public void assignFitness(ZomboEntity zomboEntity){

        genomesEvaluated ++;

        //checks if the next generation is due to showcase the best genome
        if (zomboEntity.world.getGameRules().getBoolean(NEATMobsModClient.GENERATE_BEST_ZOMBOS)){
            generateBestNext = true;
        } else {
            generateBestNext = false;
        }

        /*
         fitness is only evaluated when training,
         not when demonstrating the best performing
         genome
         */
        if (generatingBest == false){
            Genome genome = getMobGenome(zomboEntity.getGenomeNum());

            double averageDistanceToTarget = zomboEntity.getAverageDistanceToTarget();

            float fitness = 0;
            // score only assined when average distance to target is < 3 blocks
            if (averageDistanceToTarget<3){
                /*
                 linear function, returns value between 0 and 0.05 which is
                 highest when average distance to target is lowest.
                 averageDistanceToTarget multiplied by a third
                 to ensure the value is between 0 and 1
                 */
                fitness += 0.05*(1-(averageDistanceToTarget)*(1/3));
            }

            /*
            sigmoid function, returns a value between 0 and 1
            5 successful hits will award the average score
             */
            fitness += (1/(1+Math.exp(-0.6*(zomboEntity.getSuccessfulHits()-5))))-0.0474;

            genome.setFitness(fitness);

            //keeps track of the highest perfoming network
            if (fitness>lastBestScore){
                lastBestScore = fitness;
            }

        }

        if (genomesEvaluated  == genomes.size()){
            doGeneration();
            genomesEvaluated = 0;
        }
    }

    //basic accessor and mutator methods
    public int getGeneration(){
        return generation;
    }

    public int getCurrentGenome(){
        return currentGenome;
    }

    public int getVillagerNum(){
        return villagerNum;
    }

    public void setVillagerNum(int newVillagerNum){
        villagerNum = newVillagerNum;
    }

    public float getLastBestScore(){
        return lastBestScore;
    }


    //creates the next generation of genomes
    private void doGeneration(){
        /*
        evaluates the generation (if it has
        not been evaluated already)
         */
        if (generationEvaluationDone==false){
            pool.evaluateFitness(this);

            Genome topGenome;
            topGenome = pool.getTopGenome();

            bestGenome = genomes.indexOf(topGenome);
            generationEvaluationDone = true;
        }
        /*
        if best genomes are to be showcased
        no new genomes need to be created
         */
        if (generateBestNext){
            generatingBest = true;
        } else {
            generationEvaluationDone = false;
            generatingBest = false;

            //new generation if genomes is created
            pool.breedNewGeneration();

            ArrayList<Genome> Temp = new ArrayList<>();

            //new genomes are locally stored
            for(Species s: this.pool.getSpecies()){
                for(Genome g: s.getGenomes()){
                    Temp.add(g);
                }
            }

            genomes = Temp;

            this.generation++;
        }

        currentGenome = 0;



    }

    /*
    takes the current inputs from a Zombo
    and sets the attributes that decide what
    it will do based on the outputs of its
    assigned neural network
     */
    public void getGenomeOutput(ZomboEntity zomboEntity){
        Genome genome = getMobGenome(zomboEntity.getGenomeNum());
        float inputs[] = zomboEntity.getZomboInputs();

        //creates output array from neural network outputs
        float output[] = genome.evaluateNetwork(inputs);

        /*
        Minecraft stores angles as a value between -180 and 180
        where -180 is not included in the range (since it would be
        the same as 180). To avoid causing an error outputs of
        zero will be changed to 1 (since the angle faced will
        not be affected)
         */
        if (output[2] == 0){
            output[2] = 1;
        }

        // sets Zombo's attributes
        zomboEntity.setTryAttack(output[0]>=0.5);
        zomboEntity.setTryWalk(output[1]>=0.5);
        zomboEntity.setAllYaw(output[2]*360-180);
        /*
        jumping was not included as an output as training
        occurs on a flat plane
         */
    }

    /*
    the github codebase normally uses this method to
    assign fitness to genomes all at once.
    since this is not needed for my project the method
    is overridden to be empty
     */
    @Override
    public void evaluateFitness(ArrayList<Genome> population) {
        ;
    }
}
