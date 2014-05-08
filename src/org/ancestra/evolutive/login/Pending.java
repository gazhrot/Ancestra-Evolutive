package org.ancestra.evolutive.login;


import org.ancestra.evolutive.client.Account;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.tool.time.waiter.Waiter;


public class Pending {

    /*
     TODO : Gestion du paquet Af + position dans la file d'attente.
    */
	private static Waiter waiter = new Waiter(); //très laid mais ce putin de Pending est à refaire !
	
    public static void PendingSystem(final Account account) {
        if(account == null) 
        	return;
        if(account.getPosition() <= 1)
        {
        	waiter.addNext(new Runnable() {
        		public void run() {
        			if(account == null || account.getLoginClient() == null) 
        				return;
        			
                	SocketManager.MULTI_SEND_Af_PACKET(account.getLoginClient(),1,LoginServer._totalAbo,LoginServer._totalNonAbo,""+1,LoginServer._queueID);
                	account.setPosition(-1);
                	LoginServer._totalAbo--;
        		}
        	}, 750);
        } else {
        	waiter.addNext(new Runnable() {
        		public void run() {
        			if(account == null ||  account.getLoginClient() == null) 
        				return;
        			
            		SocketManager.MULTI_SEND_Af_PACKET(account.getLoginClient(),1,LoginServer._totalAbo,LoginServer._totalNonAbo,""+1,LoginServer._queueID);
            		account.setPosition(-1);
            		LoginServer._totalAbo--;
        		}
        	}, 750*account.getPosition());
        }
    }
}