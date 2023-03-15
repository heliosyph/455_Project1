package com.company;

import java.util.concurrent.Semaphore;

public class Person extends Thread{

    public Mailbox mb = new Mailbox();
    static int totalPeople; //
    int id;
    static int totalSlots;
    static int totalMail;

    static Semaphore fullSpaces = new Semaphore(0);
    static Semaphore emptySpaces;
    static Semaphore mutex = new Semaphore(1);


    String[][] messages; //initialized in Mailbox
    Semaphore[] semArray; // semaphore objects [num of people] , each sem permit [num slots]
    Semaphore mySem; //semaphore object from Sem[]
    int personChosenID; //randomly chosen person


    public Person(int id, int totalPeople, Semaphore emptySpaces, int totalSlots, int totalMail) {
        this.id = id;
        Person.totalPeople = totalPeople;
        Person.emptySpaces = emptySpaces;
        Person.totalSlots = totalSlots;
        Person.totalMail = totalMail;
    }


    @Override
    public void run() {

        int messageCount = mb.checkMessageCount();

        if (messageCount <= totalMail){

            if (messageCount == totalMail) {
                System.out.println("!!!!!No more messages to send !!!!!");
            }

            System.out.println("Person " + id + " entered the post office.");


            //double array creation
            messages = mb.giveMeArray(totalPeople, totalSlots);

            //holds sem[0], sem[1], sem[2], sem[3] --> sem[] creation
            semArray = mb.makeArrayOfSem(totalPeople);

            //holds sem[i] with a permit of 4 --> sem object with permit of slots
            mySem = mb.giveMeSem(semArray, id, totalSlots);

            boolean checked = mb.checkEmpty(messages);

            //if mailbox is empty --> choose a person to send mail --> send mail
            if (checked) {
                System.out.println("-Person " + id + " checks - he has 0 letters in mailbox.");
                System.out.println("-Person " + id + "'s mailbox is empty.");
                mb.yieldForEmptyMailbox();
            }

            //choose anyone but themselves
            personChosenID = mb.chooseAperson(id, totalPeople);

            //send a message using the semaphore created
            mb.sendMessage(personChosenID, mySem, id, messages, emptySpaces, fullSpaces, mutex, totalPeople, totalSlots);

            //read a message
            mb.readAmessage(id, fullSpaces, mutex, emptySpaces, totalPeople, mySem, messages, totalSlots, personChosenID);

            //message person left the office
            mb.leftPostOffice(id);
        }

    }
}

