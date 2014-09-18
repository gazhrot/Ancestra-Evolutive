package org.ancestra.evolutive.util.exchange;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.util.Couple;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.object.Object;

public class PlayerExchange extends Exchange {

	private Player player1;
	private Player player2;
	
	public PlayerExchange(Player player1, Player player2) {
		super(player1, player2);
		
		this.player1 = player1;
		this.player2 = player2;
	}

	@Override
	protected void doCancel() {
		if(this.player1.getAccount() != null)
			if(this.player1.getAccount().getGameClient() != null)
				this.player1.send("EV");
		
		if(this.player2.getAccount() != null)
			if(this.player2.getAccount().getGameClient() != null)
				this.player1.send("EV");
		
		this.player1.setIsTradingWith(0);
		this.player2.setIsTradingWith(0);
		this.player1.setCurExchange(null);
		this.player2.setCurExchange(null);
	}

	@Override
	protected void doApply() {
		this.player1.addKamas((- this.exchanger1.getKamas() + this.exchanger2.getKamas()));
		this.player2.addKamas((- this.exchanger2.getKamas() + this.exchanger1.getKamas()));
		
		for(Couple<Integer, Integer> couple : this.exchanger1.getObjects())	{
			if(couple.getValue() == 0)
				continue;
			
			if(!this.player1.hasItemGuid(couple.getKey())) {//Si le perso n'a pas l'item (Ne devrait pas arriver)
				couple.setValue(0);//On met la quantite a 0 pour eviter les problemes
				continue;
			}	
			
			Object object = World.data.getObject(couple.getKey());
			
			if((object.getQuantity() - couple.getValue()) < 1) {//S'il ne reste plus d'item apres l'�change
				this.player1.removeItem(couple.getKey());
				couple.setValue(object.getQuantity());
				SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(this.player1, couple.getKey());
				if(!this.player2.addObject(object, true))//Si le joueur avait un item similaire
					World.data.removeObject(couple.getKey());//On supprime l'item inutile
			} else {
				object.setQuantity(object.getQuantity() - couple.getValue());
				SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(this.player1, object);
				Object newObject = Object.getClone(object, couple.getValue());
				if(this.player2.addObject(newObject, true))//Si le joueur n'avait pas d'item similaire
					World.data.addObject(newObject, true);//On ajoute l'item au World
			}
		}
		
		for(Couple<Integer, Integer> couple : this.exchanger2.getObjects())	{
			if(couple.getValue() == 0)
				continue;
			
			if(!this.player2.hasItemGuid(couple.getKey())) {//Si le perso n'a pas l'item (Ne devrait pas arriver)
				couple.setValue(0);//On met la quantite a 0 pour eviter les problemes
				continue;
			}	
			
			Object object = World.data.getObject(couple.getKey());
			
			if((object.getQuantity() - couple.getValue()) < 1) {//S'il ne reste plus d'item apres l'�change
				this.player2.removeItem(couple.getKey());
				couple.setValue(object.getQuantity());
				SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(this.player2, couple.getKey());
				if(!this.player1.addObject(object, true))//Si le joueur avait un item similaire
					World.data.removeObject(couple.getKey());//On supprime l'item inutile
			} else {
				object.setQuantity(object.getQuantity() - couple.getValue());
				SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(this.player2, object);
				Object newObject = Object.getClone(object, couple.getValue());
				if(this.player1.addObject(newObject, true))//Si le joueur n'avait pas d'item similaire
					World.data.addObject(newObject, true);//On ajoute l'item au World
			}
		}
		
		this.player1.setIsTradingWith(0);
		this.player2.setIsTradingWith(0);
		this.player1.setCurExchange(null);
		this.player2.setCurExchange(null);
		
		SocketManager.GAME_SEND_Ow_PACKET(this.player1);
		SocketManager.GAME_SEND_Ow_PACKET(this.player2);
		SocketManager.GAME_SEND_STATS_PACKET(this.player1);
		SocketManager.GAME_SEND_STATS_PACKET(this.player2);
		SocketManager.GAME_SEND_EXCHANGE_VALID(this.player1.getAccount().getGameClient(),'a');
		SocketManager.GAME_SEND_EXCHANGE_VALID(this.player2.getAccount().getGameClient(),'a');
		
		this.player1.save();
		this.player2.save();
	}

	@Override
	protected void doAddObject(int idObject, int quantity, int idPlayer) {
		this.exchanger1.setOk(false);
		this.exchanger2.setOk(false);
		
		this.sendOk(this.exchanger1);
		this.sendOk(this.exchanger2);
		
		Object object = World.data.getObject(idObject);

		if(object == null)
			return;
		
		String str = idObject + "|" + quantity;
		String add = "|" + object.getTemplate().getId() + "|" + object.parseStatsString();
		
		if(this.player1.getId() == idPlayer) {
			Couple<Integer, Integer> couple = getCoupleInList(this.exchanger1.getObjects(), idObject);
			
			if(couple != null) {
				couple.setValue(couple.getValue() + quantity);
				this.sendMoveOk(this.player1, this.player2, 'O', "+", idObject + "|" + couple.getValue(), add);
				return;
			}
			this.sendMoveOk(this.player1, this.player2, 'O', "+", str, add);
			this.exchanger1.getObjects().add(new Couple<>(idObject, quantity));
		} else if(this.player2.getId() == idPlayer) {
			Couple<Integer, Integer> couple = getCoupleInList(this.exchanger2.getObjects(), idObject);
			
			if(couple != null) {
				couple.setValue(couple.getValue() + quantity);
				this.sendMoveOk(this.player2, this.player1, 'O', "+", idObject + "|" + couple.getValue(), add);
				return;
			}
			
			this.sendMoveOk(this.player2, this.player1, 'O', "+", str, add);
			this.exchanger2.getObjects().add(new Couple<>(idObject, quantity));
		}
	}

	@Override
	protected void doRemoveObject(int idObject, int quantity, int idPlayer) {		
		this.exchanger1.setOk(false);
		this.exchanger2.setOk(false);
		
		this.sendOk(this.exchanger1);
		this.sendOk(this.exchanger2);
		
		Object obj = World.data.getObject(idObject);
		
		if(obj == null)
			return;
		
		String add = "|" + obj.getTemplate().getId() + "|" + obj.parseStatsString();
		
		if(this.player1.getId() == idPlayer) {
			Couple<Integer,Integer> couple = getCoupleInList(this.exchanger1.getObjects(), idObject);
			int newQua = couple.getValue() - quantity;
			
			if(newQua < 1) {
				this.exchanger1.getObjects().remove(couple);
				this.sendMoveOk(this.player1, this.player2, 'O', "-", String.valueOf(idObject), "");
			} else {
				couple.setValue(newQua);
				this.sendMoveOk(this.player1, this.player2, 'O', "+", idObject + "|" + newQua, add);
			}
		} else if(this.player2.getId() == idPlayer) {
			Couple<Integer,Integer> couple = getCoupleInList(this.exchanger2.getObjects(), idObject);
			int newQua = couple.getValue() - quantity;
			
			if(newQua <1) {
				this.exchanger2.getObjects().remove(couple);
				this.sendMoveOk(this.player2, this.player1, 'O', "-", String.valueOf(idObject), "");
			} else {
				couple.setValue(newQua);
				this.sendMoveOk(this.player2, this.player1, 'O', "+", idObject + "|" + newQua, add);
			}
		}
	}

	@Override
	protected void doToogleOk(int id) {		
		if(this.player1.getId() == id) {
			this.exchanger1.setOk(!this.exchanger1.isOk());
			this.sendOk(this.exchanger1);
		} else if(this.player2.getId() == id) {
			this.exchanger2.setOk(!this.exchanger2.isOk());
			this.sendOk(this.exchanger2);
		} else {
			return;
		}
		
		if(this.exchanger1.isOk() && this.exchanger2.isOk())
			apply();
	}
	
	@Override
	protected void doEditKamas(int idPlayer, long kamas) {
		this.exchanger1.setOk(false);
		this.exchanger2.setOk(false);

		this.sendOk(this.exchanger1);
		this.sendOk(this.exchanger2);
		
		if(this.player1.getId() == idPlayer) {
			this.exchanger1.setKamas(kamas);
			this.sendMoveOk(this.player1, this.player2, 'G', "", String.valueOf(kamas), "");
		} else if(this.player2.getId() == idPlayer) {
			this.exchanger2.setKamas(kamas);
			this.sendMoveOk(this.player2, this.player1, 'G', "", String.valueOf(kamas), "");
		}
	}

	private void sendOk(Exchanger exchanger) {
		String packet = "EK" + (exchanger.isOk() ? "1" : "0") + exchanger.getCreature().getId();
		this.player1.send(packet);
		this.player2.send(packet);	
	}
	
	private void sendMoveOk(Player player1, Player player2, char type, String signe, String str, String add) {
		if(player1 != null) {
			String packet1 = "EMK" + type + signe + (!str.equals("") ? str : "");
			player1.send(packet1);
		}
		if(player2 != null) {
			String packet2 = "EmK" + type + signe + (!str.equals("") ? str : "") + add;
			player2.send(packet2);	
		}
	}
}