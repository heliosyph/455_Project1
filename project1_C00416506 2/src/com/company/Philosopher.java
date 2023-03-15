package com.company;

import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class Philosopher implements Runnable {
    int id;
    int threadNumber; //needed for start barrier
    Semaphore start_barrier;
    private static int meals;
    Semaphore leftChop;
    Semaphore rightChop;


    static Semaphore mealChecker = new Semaphore(1);

    static Semaphore end_barrier = new Semaphore(0);


    public Philosopher(int id, int threadNumber, Semaphore start_barrier, int meals, Semaphore leftChop, Semaphore rightChop) {
        this.id = id;
        this.threadNumber = threadNumber;
        this.start_barrier = start_barrier;
        Philosopher.meals = meals;
        this.leftChop = leftChop;
        this.rightChop = rightChop;
    }


    static AtomicInteger arriveCnt = new AtomicInteger(0);
    static AtomicInteger arriveCntMeals = new AtomicInteger(0);


    static AtomicInteger leaveCnt = new AtomicInteger(0);


    @Override
    public void run() {


/** Trying to implement a second barrier */

        //this is using the first barrier
        System.out.println("Philosopher " + id + " enters.");
        arriveCnt.getAndIncrement();

            if(arriveCnt.get() == threadNumber){
                System.out.println("All threads sit");
                start_barrier.release();
            }

            try {
                start_barrier.acquire();
                start_barrier.release();

                //starter of code
                if (mealChecker.availablePermits() > 0) {

                    while (meals > 0) {

                        meals--;

                        if (id % 2 == 0) {
                            rightChop.acquireUninterruptibly();
                            System.out.println("Philosopher" + id + "'s right chopstick is available.");


                            leftChop.acquireUninterruptibly();
                            System.out.println("Philosopher" + id + "'s left chopstick is available.");

                                System.out.println("----Philosopher " + id + " grabs both chopsticks and now HAS a pair.");
                                eat();

                        }


                        if (id % 2 != 0) {
                            leftChop.acquireUninterruptibly();
                            System.out.println("Philosopher " + id + "'s left chopstick is available.");

                            rightChop.acquireUninterruptibly();
                            System.out.println("Philosopher " + id + "'s right chopstick is available.");

                                System.out.println("----Philosopher " + id + " grabs both chopsticks and now HAS a pair.");
                                eat();
                        }


                        arriveCntMeals.getAndIncrement();
                        System.out.println("Meals ate: " + arriveCntMeals.get());
                        System.out.println("------Philosopher " + id + " is finished eating");


                        leftChop.release();
                        System.out.println("-------Philosopher " + id + " dropped his left chopstick.");


                        rightChop.release();
                        System.out.println("-------Philosopher " + id + " dropped his right chopstick.");

                        think();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        //using second barrier
        leaveCnt.getAndIncrement();

        if(leaveCnt.get() == threadNumber){
            System.out.println("All philosophers are done eating and will proceed to leave table.");
            end_barrier.release();
        }

        end_barrier.acquireUninterruptibly();
        end_barrier.release();

        System.out.println("Philosopher "+id +" is granted to leave the table.");

}


    //eat state
    public void eat() {
        Random rand = new Random();
        int cycles = rand.nextInt(7); // 0 -6


        System.out.println("-----Philosopher " + id + " is eating");
        for (int i = 3; i < cycles; i++) {
            Thread.yield();
        }
    }


    //think state
    public void think() {
        Random rand = new Random();
        int cycles = rand.nextInt(7); // 0 -6


        System.out.println("-----Philosopher " + id + " is thinking");
        for (int i = 3; i < cycles; i++) {
            Thread.yield();
        }
    }

}
