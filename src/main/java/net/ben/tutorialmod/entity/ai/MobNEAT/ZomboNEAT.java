package net.ben.tutorialmod.entity.ai.MobNEAT;

import net.ben.tutorialmod.NEAT.Environment;
import net.ben.tutorialmod.NEAT.Genome;
import net.ben.tutorialmod.NEAT.Pool;
import net.ben.tutorialmod.NEAT.Species;
import net.ben.tutorialmod.NEAT.config.NEAT_Config;
import net.ben.tutorialmod.TutorialModClient;
import net.ben.tutorialmod.entity.custom.ZomboEntity;
import net.minecraft.util.math.MathHelper;
import java.util.ArrayList;

/**
 * Created by vishnughosh on 05/03/17.
 */
public class ZomboNEAT implements Environment {

    protected  boolean generatingBest = false;
    protected boolean generateBestNext = false;
    protected boolean generationEvaluationDone = false;
    protected int currentGenome = 0;
    protected Pool pool;
    protected ArrayList<Genome> genomes = new ArrayList<>();
    protected int generation = 0;
    protected int genomesEvaluated = 0;
    public float lastBestScore = 0;
    protected int bestGenome = 0;
    public int villagerNum = 0;




    /*
    @Override
    public void evaluateFitness(ArrayList<Genome> population) {

        for (Genome gene: population) {
            float fitness = 0;
            gene.setFitness(0);
            for (int i = 0; i < 2; i++)
                for (int j = 0; j < 2; j++) {
                    float inputs[] = {i, j};
                    float output[] = gene.evaluateNetwork(inputs);
                    int expected = i^j;
                    //                  System.out.println("Inputs are " + inputs[0] +" " + inputs[1] + " output " + output[0] + " Answer : " + (i ^ j));
                    //if (output[0] == (i ^ j))
                    fitness +=  (1 - Math.abs(expected - output[0]));
                }
            fitness = fitness * fitness;

            gene.setFitness(fitness);

        }

    }
    */

    public void assignNetwork(ZomboEntity zomboEntity){
        if (currentGenome == 0 && generatingBest == false){
            lastBestScore = 0;
        }
        if (generatingBest){
            zomboEntity.genomeNum = bestGenome;
        } else {
            zomboEntity.genomeNum = currentGenome;
            if (currentGenome!=genomes.size()) {
                currentGenome ++;
            }

        }
    }

    public ZomboNEAT(){
        this.pool = new Pool();
        this.pool.initializePool();

        for(Species s: this.pool.getSpecies()){
            for(Genome g: s.getGenomes()){
                genomes.add(g);
            }
        }
        bestGenome = 0;
    }

    public boolean populationComplete(){
        return currentGenome==genomes.size();
    }

    public boolean generationComplete() {return genomesEvaluated==genomes.size();}

    public Genome getMobGenome(int genomeNum){

        while (genomeNum >= genomes.size()){
            genomeNum--;
        }

        Genome mobGenome = genomes.get(genomeNum);

        return mobGenome;
    }

    public void assignFitness(ZomboEntity zomboEntity){

        genomesEvaluated ++;
        if (zomboEntity.world.getGameRules().getBoolean(TutorialModClient.GENERATE_BEST_ZOMBOS)){
            generateBestNext = true;
        } else {
            generateBestNext = false;
        }

        if (generatingBest == false){
            Genome genome = getMobGenome(zomboEntity.genomeNum);
            float fitness = 0;
            if (zomboEntity.totalDistanceToTarget/zomboEntity.ticksSurvived<3){
                fitness += 0.05*(1-(zomboEntity.totalDistanceToTarget/(zomboEntity.ticksSurvived*5)));
            }
            //fitness += 0.1*Math.pow(zomboEntity.successfulHits, 1.5);
            //fitness += 1-Math.exp(-0.1*zomboEntity.successfulHits);
            fitness += (1/(1+Math.exp(-0.6*(zomboEntity.successfulHits-5))))-0.0474;
            genome.setFitness(fitness);
            if (fitness>lastBestScore){
                lastBestScore = fitness;
            }

        }

        if (genomesEvaluated  == genomes.size()){
            doGeneration();
            genomesEvaluated = 0;
        }
    }

    public float getTopScorer(){
        Genome topGenome = pool.getTopGenome();
        return topGenome.getPoints();
    }

    public int getGeneration(){
        return generation;
    }

    public int getCurrentGenome(){
        return currentGenome;
    }

    public int getGenomesSize(){
        return genomes.size();
    }

    private void doGeneration(){
        if (generationEvaluationDone==false){
            pool.evaluateFitness(this);

            Genome topGenome;
            topGenome = pool.getTopGenome();
            bestGenome = genomes.indexOf(topGenome);
            generationEvaluationDone = true;
        }
        if (generateBestNext){
            generatingBest = true;
            currentGenome --;
        } else {
            generationEvaluationDone = false;
            generatingBest = false;

            pool.breedNewGeneration();

            ArrayList<Genome> Temp = new ArrayList<>();

            for(Species s: this.pool.getSpecies()){
                for(Genome g: s.getGenomes()){
                    Temp.add(g);
                }
            }

            genomes = Temp;

            this.generation++;

            currentGenome = 0;
        }



    }

    public void getGenomeOutput(ZomboEntity zomboEntity){
        Genome genome = getMobGenome(zomboEntity.genomeNum);
        float inputs[] = zomboEntity.getZomboInputs();
        float output[] = genome.evaluateNetwork(inputs);
        zomboEntity.setTryAttack(output[0]>=0.5);
        zomboEntity.setTryWalk(output[1]>=0.5);
        zomboEntity.setAllYaw(output[2]*360);
    }
/*
    public static void main(String arg0[]){
        ZomboNEAT zomboNEAT = new ZomboNEAT();

        Pool pool = new Pool();
        pool.initializePool();

        Genome topGenome = new Genome();
        int generation = 0;
        while(true){
            //pool.evaluateFitness();
            pool.evaluateFitness(zomboNEAT);
            topGenome = pool.getTopGenome();
            System.out.println("TopFitness : " + topGenome.getPoints());

            if(topGenome.getPoints()>15){
                break;
            }
//            System.out.println("Population : " + pool.getCurrentPopulation() );
            System.out.println("Generation : " + generation );
            //           System.out.println("Total number of matches played : "+TicTacToe.matches);
            //           pool.calculateGenomeAdjustedFitness();

            pool.breedNewGeneration();
            generation++;

        }
        System.out.println(topGenome.evaluateNetwork(new float[]{1,0})[0]);
    }*/

    @Override
    public void evaluateFitness(ArrayList<Genome> population) {

    }
}
