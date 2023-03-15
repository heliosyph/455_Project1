package com.company;

import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class Mailbox extends Thread{

    static AtomicInteger msgCount = new AtomicInteger(0);
    static AtomicInteger cycleCount = new AtomicInteger(0);

    static AtomicInteger jCounter = new AtomicInteger(0); //used to check


    public int checkMessageCount(){
        return msgCount.get();
    }


    public Semaphore[] makeArrayOfSem (int totalPeople){

        return new Semaphore[totalPeople];
    }

    public Semaphore giveMeSem(Semaphore[] semArray, int id, int slots){
        Semaphore mySem = new Semaphore(slots);

        for(int i = 0; i < semArray.length; i++){
            if(i == id){
                semArray[i] = mySem;
                break;
            }
        }

        return mySem;
    }

    public String[][] giveMeArray(int totalPeople, int slots){

        return new String[totalPeople][slots];
    }



    //check if mailbox is empty
    public boolean checkEmpty(String[][] mailbox){
        boolean empty = false;

        for(int i = 0; i< mailbox.length; i++){
            for(int j = 0; j < mailbox[0].length; j++){
                if(mailbox[i][j] == null){
                    empty = true;
                }
            }
        }
        return empty;
    }

    public void yieldForEmptyMailbox(){
        for (int i = 0; i < 10000; i++) {
            Thread.yield();
        }
    }

    public int chooseAperson(int id, int totalPeople){
        Random rand = new Random();

        int personChosen = rand.nextInt(totalPeople);

        while(id == personChosen){
            personChosen = rand.nextInt(totalPeople);
        }
        System.out.println("--------Person " + id + " chooses to send a message to Person " + personChosen);

        return personChosen;
    }

    //Produce
    public void sendMessage(int personChosen, Semaphore mySem, int id, String[][] mailbox, Semaphore emptySpaces,
                            Semaphore fullSpaces, Semaphore mutex, int totalPeopleMailBoxes, int totalSlots){

        String message = messageList();

        //producers
        System.out.println("---------Person " + id + " tries to claim emptySpaceSemaphore[" + personChosen + "].");
        emptySpaces.acquireUninterruptibly();
        System.out.println("----------Person " + id + " gets and decreases emptySpaceSemaphore[" + personChosen + "] by 1.");
        mutex.acquireUninterruptibly();
        System.out.println("-----------Person " + id + " claims mailboxSemaphore[" + personChosen + "]");

        //add item to buffer

//        int chosenSlot = (mySem.availablePermits() - 1); // 0 - 3 index
//        System.out.println("available permits: " + mySem.availablePermits());


//        System.out.println("chosen slot: " + chosenSlot + " for person: " + id);

        boolean checkIfSent = false;

        if(mySem.availablePermits() > 0){
            try{

                mySem.acquire();

                for(int j = 0; j < mailbox.length; j++){
                    for(int k = 0; k < mailbox[0].length; k++){

                        //TODO we need a chosenSlot between 0-totalslots
                        int chosenSlot = giveMeRandomInt(0, totalSlots);

                        if(mailbox[personChosen][chosenSlot] == null){
                            //if empty place message in array
                            mailbox[personChosen][chosenSlot] = message;
                            System.out.println("Person " + id + " accessed mail slot[" + chosenSlot + "] for person " + personChosen + "'s mailbox successfully. The message was: '" + message +
                                    "' and was sent to Person " + personChosen);
                            checkIfSent = true;
                            break;
                        }
                        else if((mailbox[personChosen][chosenSlot] != null)){
                            System.out.println("Person " + id + " tried to access mail slot[" + chosenSlot + "] for person " + personChosen + ", but mail slot is full.");
                        }
                    }
                    if(checkIfSent){
                        break;
                    }
                }

                System.out.println("-----------Person " + id + " successfully sent '" + message + "' to Person " + personChosen);
            }catch(Exception e){
                e.printStackTrace();
            }


            msgCount.getAndIncrement();
            System.out.println("------------Person " + id + " successfully updates the overall message count to " + msgCount.get());

            cycleCount.getAndIncrement();
            yieldForCycle(cycleCount, id);

            mutex.release();
            fullSpaces.release();

        }
        else if(mySem.availablePermits() < 1){
            System.out.println("Cannot add messages at the moment.");
        }

    }


    public void readAmessage(int id, Semaphore fullSpaces, Semaphore mutex, Semaphore emptySpaces, int totalPeople, Semaphore mySem,
                             String[][] messages, int totalSlots, int personChosenID){

        System.out.println("--Person " + id + " claims mailboxSemaphore[checking!]");
        fullSpaces.acquireUninterruptibly();
        mutex.acquireUninterruptibly();

        //beginning of code
        boolean noLetters = true;

        for(int i = 0; i< messages.length; i++){
            for(int j = 0; j < messages[0].length; j++){
                if (messages[id][jCounter.get()] != null){
                    noLetters = false;
                    String msg = messages[id][jCounter.get()];
                    System.out.println("---Person " + id + " reads 1th msg: '" + msg + "' sent by Person " + personChosenID);

                    //yield
                    yieldForCycle(cycleCount, id);


                    System.out.println("j: " + jCounter.get());
                    jCounter.getAndIncrement();
                }
                else{
                    System.out.println("-Person " + id + " checks- has 0 letters in mailbox");
                    break;
                }
            }
            if(noLetters){
                break;
            }
        }

        mySem.release();
        mutex.release();
        emptySpaces.release();

    }

    public void yieldForCycle(AtomicInteger cycle, int id){
        System.out.println("--------------Person " + id + " yields the " + cycle + "th cycles.");

        Random rand = new Random();
        int cycles = rand.nextInt(7); // 0 -6

        for (int i = 3; i < cycles; i++) {
            Thread.yield();
        }
    }

    public String messageList(){
        String[] shoutList = new String[] {"well yes", "well no", "maybe", "absolutely crazy", "unbelievable", "that's out of this world",
                "in your dreams", "magnificent!", "oh boy", "geez louise"};

        String randomText= (shoutList[new Random().nextInt(shoutList.length)]);

        return randomText;
    }

    public void leftPostOffice(int id){
        //wait 3-6 cycles before returning
        yieldForCycle(cycleCount, id);
        System.out.println("Person " + id + " has left the office!");
    }

    public int giveMeRandomInt(int min, int max){
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }





}
