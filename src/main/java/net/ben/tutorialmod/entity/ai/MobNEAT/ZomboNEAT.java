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

    protected static final int population = NEAT_Config.POPULATION;
    protected static int currentGenome = 0;
    protected Pool pool;
    protected int generation = 0;
    protected boolean generationDone = false;

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
        int totalGeneomes = 0;

        for(Species s: this.pool.getSpecies()){
            totalGeneomes += s.getGenomes().size();
        }

        zomboEntity.genomeNum = currentGenome;
        if (currentGenome+1==totalGeneomes) {
            currentGenome = 0;
            generationDone = true;

        } else {
            currentGenome ++;
        }
    }

    public ZomboNEAT(){
        this.pool = new Pool();
        this.pool.initializePool();
    }

    public Genome getMobGenome(int genomeNum){
        ArrayList<Genome> allGenome = new ArrayList<>();

        for(Species s: this.pool.getSpecies()){
            for(Genome g: s.getGenomes()){
                allGenome.add(g);
            }
        }

        Genome mobGenome = allGenome.get(genomeNum);

        return mobGenome;
    }

    public void assignFitness(ZomboEntity zomboEntity){
        Genome genome = getMobGenome(zomboEntity.genomeNum);
        float fitness = 0;
        fitness += 0.2*(1-(zomboEntity.totalDistanceToPlayer/(zomboEntity.ticksSurvived*35)));
        fitness -= 0.05*(zomboEntity.damageTaken*0.05);
        fitness += 1-Math.exp(-0.1*zomboEntity.successfulHits);
        genome.setFitness(fitness);
        if (generationDone){
            doGeneration();
        }
    }

    private void doGeneration(){

        Genome topGenome = new Genome();
        topGenome = pool.getTopGenome();
        System.out.println("TopFitness : " + topGenome.getPoints());
        pool.breedNewGeneration();
        this.generation++;


    }

    public void getGenomeOutput(ZomboEntity zomboEntity){
        Genome genome = getMobGenome(zomboEntity.genomeNum);
        float inputs[] = zomboEntity.getZomboInputs();
        float output[] = genome.evaluateNetwork(inputs);
        zomboEntity.setTryJump(output[0]>0.8);
        zomboEntity.setTryAttack(output[1]>0.8);
        zomboEntity.setTryWalk(output[2]>0.8);
        zomboEntity.setYaw(output[3]*360);
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
