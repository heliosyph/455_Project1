//Reader Writer

package com.company;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class ReaderWriter implements Runnable{
    String typeThread; //is it a read or write
    static int numThreads; // total threads
    static int maxThreads; // readers that can be in the buffer
    static int totalReaders; // total READS
    static int totalWriters; // total WRITES

    static AtomicInteger threadID = new AtomicInteger(0); // 0 - total threads id

    static Semaphore mutex = new Semaphore(1, true); //protocol
    static int countReads = 0;
    static int countCurrentReads = 0;


    static Semaphore sharedBuffer = new Semaphore(1, true); //protocol

    static Semaphore writers = new Semaphore(0, true); //will need for pattern

    static Semaphore readersInBuffSem; // same as max threads but as a semaphore



    public ReaderWriter(String typeThread, int numThreads, int maxThreads, Semaphore readersInBuffSem, int totalReaders, int totalWriters){
        this.typeThread = typeThread;
        ReaderWriter.numThreads = numThreads;
        ReaderWriter.maxThreads = maxThreads;
        ReaderWriter.readersInBuffSem = readersInBuffSem;
        ReaderWriter.totalReaders = totalReaders;
        ReaderWriter.totalWriters = totalWriters;
    }

    @Override
    public void run() {

        //ONLY WRITERS
        if(typeThread.equals("writer") & totalReaders == 0){
//            System.out.println("writer threads remaining: " + totalWriters);
//            System.out.println("writer?" + "thread: " + threadID.get());
            threadID.getAndIncrement(); //total threads
            writerRemainingLoop(threadID.get());
        }


        if(typeThread.equals("reader")){

//            System.out.println("reader?" + "thread: " + threadID.get());
            threadID.getAndIncrement(); //total threads
            readerLoop(threadID.get());
        }


        if(typeThread.equals("writer")){
//            System.out.println("writer threads remaining: " + totalWriters);
//            System.out.println("writer?" + "thread: " + threadID.get());
            threadID.getAndIncrement(); //total threads
            writerLoop(threadID.get());
        }



        if(countCurrentReads == totalReaders & totalWriters == 0){
            System.exit(0);
        }

    }

    public void readerLoop(int newID){

        readersInBuffSem.acquireUninterruptibly();
        mutex.acquireUninterruptibly();
        countReads++;

        if(countReads == 1){
            sharedBuffer.acquireUninterruptibly();
        }
        mutex.release();


        simulatedReading(newID);


        mutex.acquireUninterruptibly();
        countReads--;
        countCurrentReads++;

        if(countReads == 0){
            sharedBuffer.release();
        }


        //If we only have readers left
        if(((countCurrentReads % maxThreads == 0  & countCurrentReads > 0)) & totalWriters == 0){
//            System.out.println("In readerLoop, out of writes, but have " + countCurrentReads+ " more reads!");
            //iffy
            for(int i = 0; i < maxThreads; i++){
                readersInBuffSem.release();
            }
        }


        System.out.println("--R(" + newID + ") finished reading.");
//        System.out.println("current reader threads: " + countCurrentReads + " totalThreads: " + totalReaders);

        //normal
        if(countCurrentReads % maxThreads == 0 & totalWriters > 0){
//            System.out.println("In readerLoop, reached our max, have " + totalWriters + " more writes!");
            writers.release();
        }

        //if we only have writers left
        if(countCurrentReads == totalReaders & totalWriters > 0){
//            System.out.println("In readerLoop, out of reads, but have " + totalWriters + " more writes!");
            writerRemainingLoop(newID);
        }

        mutex.release();

    }


    public void writerLoop(int newID){

        try{
            writers.acquire();
            sharedBuffer.acquireUninterruptibly();
            totalWriters--;

            System.out.println("---W(" + newID + ") is writing");
            simulatedWriting(newID);
            System.out.println("----W(" + newID + ") finished writing.");

//            System.out.println("----W(" + newID + ") finished writing.");
//            System.out.println("Writer Loop took 1 off-> writers remaining: " + totalWriters);
            sharedBuffer.release();

            //iffy
            for(int i = 0; i < maxThreads; i++){
                readersInBuffSem.release();
            }

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public void writerRemainingLoop(int newID){
        try{
            sharedBuffer.acquire();
            totalWriters--;

            System.out.println("---W(" + newID + ") is writing");
            simulatedWriting(newID);
            System.out.println("----W(" + newID + ") finished writing.");
//            System.out.println("WriterRemaining TRY-CATCH took 1 off -> writers left: " + totalWriters);
            sharedBuffer.release();

        }catch(Exception e){
            e.printStackTrace();
        }

        if(((countCurrentReads == totalReaders) & totalReaders > 0)  & totalWriters >=1){
            threadID.getAndIncrement();
            writerRemainingLoop(threadID.get());
        }
    }

    public void simulatedReading(int newID){

        System.out.println("--R(" + newID + ") is reading");
        for (int i = 0; i < 100000; i++) {
            Thread.yield();
        }
//        System.out.println("--R(" + newID + ") finished reading.");

    }

    public void simulatedWriting(int newID){

//        System.out.println("---W(" + newID + ") is writing");
        for (int i = 0; i < 100000; i++) {
            Thread.yield();
        }
//        System.out.println("----W(" + newID + ") finished writing.");



    }

}