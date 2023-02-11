package net.ben.tutorialmod.entity.ai.MobNEAT;

import net.ben.tutorialmod.NEAT.Environment;
import net.ben.tutorialmod.NEAT.Genome;
import net.ben.tutorialmod.NEAT.Pool;
import net.ben.tutorialmod.NEAT.Species;
import net.ben.tutorialmod.NEAT.config.NEAT_Config;
import net.ben.tutorialmod.entity.custom.ZomboEntity;
import net.minecraft.util.math.MathHelper;
import java.util.ArrayList;

/**
 * Created by vishnughosh on 05/03/17.
 */
public class ZomboNEAT implements Environment {

    protected int currentGenome = 0;
    protected Pool pool;
    protected ArrayList<Genome> genomes = new ArrayList<>();
    protected int generation = 0;
    protected int genomesEvaluated = 0;
    protected float lastBest = 0;


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
        zomboEntity.genomeNum = currentGenome;
        if (currentGenome!=genomes.size()) {
            currentGenome ++;
        }
    }

    public ZomboNEAT(){
        this.pool = new Pool();
        this.pool.initializePool();

        for(Species s: this.pool.getSpecies()){
            for(Genome g: s.getGenomes()){
                for (int i=0; i==5; i++){
                    g.Mutate();
                }
                genomes.add(g);
            }
        }

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
        Genome genome = getMobGenome(zomboEntity.genomeNum);
        float fitness = 0;
        fitness += (1-(zomboEntity.totalDistanceToTarget/(zomboEntity.ticksSurvived*50)));
        fitness += zomboEntity.successfulHits;
        //fitness += 1-Math.exp(-0.1*zomboEntity.successfulHits);
        genome.setFitness(fitness);
        if (genomesEvaluated  == genomes.size()){
            doGeneration();
            genomesEvaluated = 0;
            currentGenome = 0;
        }
    }

    public float getTopScore(){
        Genome topGenome = pool.getTopGenome();
        return topGenome.getPoints();
    }

    public int getGeneration(){
        return generation;
    }

    public float getLastBest(){
        return lastBest;
    }

    public int getCurrentGenome(){
        return currentGenome;
    }

    public int getGenomesSize(){
        return genomes.size();
    }

    private void doGeneration(){

        Genome topGenome;

        pool.evaluateFitness(this);
        topGenome = pool.getTopGenome();
        lastBest = topGenome.getPoints();
        System.out.println(" ##################### TopFitness : " + topGenome.getPoints());
        pool.breedNewGeneration();

        ArrayList<Genome> Temp = new ArrayList<>();

        for(Species s: this.pool.getSpecies()){
            for(Genome g: s.getGenomes()){
                Temp.add(g);
            }
        }

        genomes = Temp;

        this.generation++;


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
