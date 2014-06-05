package org.ancestra.evolutive.client;

public class AccountHelper {
    private Account account;

    AccountHelper(Account account){
        this.account = account;
    }

    public String getPlayersList(){
        StringBuilder packet = new StringBuilder();
        packet.append("ALK31536000000|").append(account.getPlayers().size());
        for(Player player : account.getPlayers().values()){
            packet.append(player.parseALK());
        }
        return packet.toString();
    }
}
