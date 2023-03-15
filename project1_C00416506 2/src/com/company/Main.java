package com.company;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.Semaphore;


public class Main extends Thread {

    public static void main(String[] args) {

        if (args[0].equals("-A")) {

            if (args.length == 1) {
                System.out.println("Missing Argument, Please include 1, 2, or 3 after -S");
            } else if (args[1].equals("1")) {
                callPhilosophers();
            } else if (args[1].equals("2")) {
                //mailbox
                callPerson();
            } else if (args[1].equals("3")) {
                //reader writers
                callRW();
            } else {
                System.out.println("Unexpected Argument. Please try again.");
            }
        } else {
            System.out.println("Unexpected Tag. Please try again.");
        }
    }

    public static void callPhilosophers() {

        System.out.println("********* You're running Dining Philosopher Problem. *********");

        /** TASK 1 */

        Scanner input = new Scanner(System.in);

        int p;
        int m;
        /** input validation for PHILOSOPHERS */

        while (true) {
            try {
                System.out.print("How many philosophers would you like present? (2-10): ");
                p = input.nextInt();

                //can't have only 1 philosopher since there will only be 1 chopstick
                while (p <= 1 || p > 10) {

                    if (p == 1 || p == 0) {
                        System.out.println("Choose more than 1 philosopher!");
                    }

                    System.out.print("How many philosophers would you like present? (2-10) : ");
                    p = input.nextInt();
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println("ENTER INTEGERS ONLY!");
                input.nextLine();
            }
        }


        /** input validation for MEALS */

        while (true) {
            try {
                System.out.print("How many meals would you like them to eat? (1-100) : ");
                m = input.nextInt();

                if(m > 100){
                    System.out.print("Enter within range. How many meals would you like them to eat? (1-100) : ");
                    m = input.nextInt();
                }

                while (m < 0) {
                    System.out.print("Enter at least 1 meal. How many meals would you like them to eat? : ");
                    m = input.nextInt();
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println("ENTER INTEGERS ONLY!");
                input.nextLine();
            }
        }

        System.out.println("This is the number of philosophers: " + p);
        System.out.println("This is the number of meals: " + m);

        System.out.println("Starting Task 1");
        System.out.println("-----------------------------------------");
        System.out.println();

        /** storing threads in an array so i can start them at the same time */
        Semaphore start_barrier = new Semaphore(0);

        /** creating an arrayOfSemaphores */
        Semaphore[] chopsticks = new Semaphore[p];

        for (int i = 0; i < chopsticks.length; i++) {
            //creates a chopstick object with a mutex
            chopsticks[i] = new Semaphore(1);
        }

        //passing chopstick object to philosopher so it can be used by philosopher

        Thread[] startingThreads = new Thread[p];
        for (int i = 0; i < p; i++) {

            Philosopher philosophers = new Philosopher(i, p, start_barrier, m, chopsticks[i], chopsticks[(i + 1) % p]);
            startingThreads[i] = new Thread(philosophers);

                                /*
                                Another way of doing the above two lines of code
                                */
            //startingThreads[i] = new Thread(new Philosopher(i, p, start_barrier, m, chopsticks[i], chopsticks[(i + 1) % p]));
        }

        //starting them in a separate loop so there is no confusion

        for (Thread startingThread : startingThreads) {
            startingThread.start();
        }

    }

    public static void callPerson() {
        Scanner input = new Scanner(System.in);

        System.out.println(" ********* You're running Producer Consumer Problem. Hint Unfinished. *********");


        /** TASK 2 */

        //Post office Simulation


        int n; //number of people
        int s; //number of messages a person's mailbox can hold
        int totalM; //total number of messages to be sent before the simulation ends


        //int n

        while (true) {
            try {
                System.out.print("How many people would you like present? (1-10): ");
                n = input.nextInt();

                while (n < 1 || n > 10) {
                    if (n == 0) {
                        System.out.println("Choose at least 1 person!");
                    }

                    System.out.print("Enter within range. How many people would you like present? (1-10) : ");
                    n = input.nextInt();
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println("ENTER INTEGERS ONLY!");
                input.nextLine();
            }
        }


        //int s

        while (true) {
            try {
                System.out.print("Number of slots per person's mailbox? (0-5): ");
                s = input.nextInt();

                while (s < 1 || s > 5) {

                    if (s == 0) {
                        System.out.println("Enter at least 1 slot in a mailbox.");

                    }

                    System.out.print("Enter within the range. Number of slots per person's mailbox? (0-5): ");
                    s = input.nextInt();

                }
                break;
            } catch (InputMismatchException e) {
                System.out.println("ENTER INTEGERS ONLY!");
                input.nextLine();
            }
        }

        // int m

        int maxM = (n * s);
        while (true) {
            try {
                System.out.print("How many total messages? (1-" + maxM + ") Can choose 0 to exit simulation: ");
                totalM = input.nextInt();

                //can't have only 1 philosopher since there will only be 1 chopstick
                while (totalM < 1 || totalM > maxM) {

                    if (totalM == 0) {
                        System.out.println("You chose 0. No messages to send. Exiting...");
                        System.exit(0);
                        break;
                    }

                    System.out.print("Enter within range. How many total messages? (1-" + maxM + ") : ");
                    totalM = input.nextInt();
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println("ENTER INTEGERS ONLY!");
                input.nextLine();
            }
        }


        System.out.println("Total number of persons: " + n);
        System.out.println("Total number of slots per mailbox: " + s);
        System.out.println("Total number of mail: " + totalM);


        System.out.println("Task 2: Starting ...");
        System.out.println("---------------------------");
        System.out.println();


        Semaphore emptySpaces = new Semaphore(n);

        //start p threads
        for(int i = 0; i < n; i++){
            Person person = new Person(i, n, emptySpaces, s, totalM);
            Thread t = new Thread(person);
            t.start();
        }

    }
    public static void callRW(){

        Scanner input = new Scanner(System.in);
        System.out.println("********* You're running Readers Writer Problem. *********");

        /** TASK 3 */


        int totalReaders;
        int totalWriters;
        int readersInBuffer;


        /**  max readers */

        while (true) {
            try {
                System.out.print("How many readers would you like present? (0-10): ");
                totalReaders = input.nextInt();

                while (totalReaders < 0 || totalReaders > 10) {

                    System.out.print("Out of bounds. Stay within the range of (0-10): ");
                    totalReaders = input.nextInt();
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println("ENTER INTEGERS ONLY!");
                input.nextLine();
            }
        }


        /**  writers  */

        while (true) {
            try {
                System.out.print("How many writers would like present? (1-5): ");
                totalWriters = input.nextInt();

                while (totalWriters < 1 || totalWriters > 5) {

                    if(totalWriters == 0){
                        System.out.println("You need at least ONE writer!");
                    }

                    System.out.print("Out of bounds. Enter within range (1-5): ");
                    totalWriters = input.nextInt();
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println("ENTER INTEGERS ONLY!");
                input.nextLine();
            }
        }


        /**  max reader threads in buffer   */

        while (true) {
            try {
                System.out.print("How many reader threads would you like present in the shared area buffer? : ");
                readersInBuffer = input.nextInt();

                while ((readersInBuffer > totalReaders)){
                    if (totalReaders != 0) {
                        System.out.print("Integer needs to be within the range of (1-" + totalReaders + "): ");
                    }
                    else{
                        System.out.print("No reader threads present. Enter 0: ");
                    }
                    readersInBuffer = input.nextInt();

                }
                break;
            } catch (InputMismatchException e) {
                System.out.println("ENTER INTEGERS ONLY!");
                input.nextLine();
            }
        }


        System.out.println("This is the total number of readers: " + totalReaders);
        System.out.println("This is the total number of writers: " + totalWriters);
        System.out.println("This is the max number of reader threads in the buffer: " + readersInBuffer);


        System.out.println("Start of task 3 program: ");
        System.out.println("------------------------------------");
        System.out.println();

        /** Below is the regular thread creation **/

        int totalThreads = (totalReaders + totalWriters);
        Semaphore readersInBuffSem = new Semaphore(readersInBuffer, true);



        //forking readers
        for(int i =0; i < totalReaders; i++){
            ReaderWriter readerThread = new ReaderWriter("reader",totalThreads, readersInBuffer , readersInBuffSem, totalReaders, totalWriters);
            Thread t = new Thread(readerThread);
            t.start();
        }

        //forking writers
        for(int j = 0; j < totalWriters; j++){
            ReaderWriter writerThread = new ReaderWriter("writer",totalThreads, readersInBuffer, readersInBuffSem, totalReaders, totalWriters);
            Thread t = new Thread(writerThread);
            t.start();
        }
    }
}