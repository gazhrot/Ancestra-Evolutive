package fr.edofus.ancestra.evolutive.login;


import fr.edofus.ancestra.evolutive.client.Account;
import fr.edofus.ancestra.evolutive.common.SocketManager;
import fr.edofus.ancestra.evolutive.tool.time.waiter.Waiter;

public class Pending {

    /*
     TODO : Gestion du paquet Af + position dans la file d'attente.
    */
	private static Waiter waiter = new Waiter(); //très laid mais ce putin de Pending est à refaire !
    public static void PendingSystem(final Account C)
    {
        if(C == null) return;
        if(C._position <= 1)
        {
        	waiter.addNext(new Runnable() {
        		public void run() {
        			if(C == null || C.getRealmThread() == null) return;
                	SocketManager.MULTI_SEND_Af_PACKET(C.getRealmThread(),1,LoginServer._totalAbo,LoginServer._totalNonAbo,""+1,LoginServer._queueID);
                	C._position = -1;
                	LoginServer._totalAbo--;
        		}
        	}, 750);
        } else {
        	waiter.addNext(new Runnable() {
        		public void run() {
        			if(C == null ||  C.getRealmThread() == null) return;
            		SocketManager.MULTI_SEND_Af_PACKET(C.getRealmThread(),1,LoginServer._totalAbo,LoginServer._totalNonAbo,""+1,LoginServer._queueID);
            		C._position = -1;
            		LoginServer._totalAbo--;
        		}
        	}, 750*C._position);
        }
    }
}