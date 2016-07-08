package com.quadriq.audionfc;

import android.media.AudioManager;

import com.acs.audiojack.AudioJackReader;

/**
 * Created by nilsen on 01.07.16.
 */
public class Acr3xTransmitter implements Runnable {

    private AudioJackReader mReader;
    private AudioManager mAudioManager;
    //private CallbackContext mContext;

    private boolean killMe = false;          /** Stop the polling thread? */
    private int itersWithoutResponse = 0;    /** The number of iterations that have passed with no
     response from the reader */
    private boolean readerConnected = true;  /** Is the reader currently connected? */

    private int cardType;
    private int timeout;
    private byte[] apdu;
    private Object locking;

    /**
     * @param mReader: AudioJack reader service
     * @param mAudioManager: system audio service
     * @param mContext: context for plugin results
     * @param timeout: time in <b>seconds</b> to wait for commands to complete
     * @param apdu: byte array containing the command to be sent
     * @param cardType: the integer representing card type
     */
    public Acr3xTransmitter(AudioJackReader mReader, AudioManager mAudioManager,
                            int timeout, byte[] apdu, int cardType, Object locking){
        this.mReader = mReader;
        this.mAudioManager = mAudioManager;
        this.timeout = timeout;
        this.apdu = apdu;
        this.cardType = cardType;
        this.locking = locking;
    }


    /**
     * Stops the polling thread
     */
    public void kill(){
        killMe = true;
    }


    /**
     * Updates the connection status of the reader (links to APDU response callback)
     */
    public void updateStatus(boolean status){
        readerConnected = status;
    }

    /**
     * Sends the APDU command for reading a card UID every second
     */
    @Override
    public void run() {
        try {
            /* Wait one second for stability */
            Thread.sleep(1000);

            while (!killMe) {
                synchronized(locking){

                    if(killMe){
                        continue;
                    }
                    /* If the reader is not connected, increment no. of iterations without response */
                    if(!readerConnected){
                        itersWithoutResponse++;
                    }
                    /* Else, reset the number of iterations without a response */
                    else{
                        itersWithoutResponse = 0;
                    }
                    /* Reset the connection state */
                    readerConnected = false;

                    if(itersWithoutResponse == 4) {
                        /* Communicate to the Cordova application that the reader is disconnected */
                        System.out.println("acr3x disconnected");
                        /* Kill this thread */
                        kill();
                    } else if(!mAudioManager.isWiredHeadsetOn()) {
                        System.out.println("acr3x not connected");
                        /* Kill this thread */
                        kill();
                    } else{
                        System.out.println("acr3x reading...");
                        /* Power on the PICC */
                        mReader.piccPowerOn(timeout, cardType);
                        /* Transmit the APDU */
                        mReader.piccTransmit(timeout, apdu);


                    }
                }
                /* Repeat every second */
                Thread.sleep(1000);
            }
            /* Power off the PICC */
            mReader.piccPowerOff();
            /* Set the reader asleep */
            mReader.sleep();
            /* Stop the reader service */
            mReader.stop();

            synchronized(locking){
                locking.notifyAll();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
            // TODO: add exception handling
        }
    }

}