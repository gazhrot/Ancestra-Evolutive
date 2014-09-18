package org.ancestra.evolutive.object;

import java.io.Serializable;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.ConditionParser;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.Server;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.object.action.ObjectActionCallback;
import org.ancestra.evolutive.object.action.ObjectActionManager;
import org.ancestra.evolutive.object.action.ObjectActionResult;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

public class ObjectAction implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String type;
	private String args;
	private String cond;
	private static Logger logger = (Logger) LoggerFactory.getLogger(ObjectAction.class);
	
	public ObjectAction(String type, String args, String cond) {
		this.type = type;
		this.args = args;
		this.cond = cond;
	}

	public void apply(Player caster, Player target, int objet, int cellid) {
		if(caster == null)
			return;
		
		if(!caster.isOnline() || caster.getFight() != null 
				|| caster.getAccount().getGameClient() == null)
			return;

		Player player = caster;
		
		if(target != null)
			player = target;
		
		if(!this.cond.equalsIgnoreCase("") && !this.cond.equalsIgnoreCase("-1")&& !ConditionParser.validConditions(caster, this.cond)) {
			SocketManager.GAME_SEND_Im_PACKET(caster, "119");
			return;
		}
		
		Object obj = World.data.getObject(objet);
		
		if(obj == null) {
			SocketManager.GAME_SEND_MESSAGE(caster, "Error object null. Merci de pr�venir un administrateur est d'indiquer le message.", Server.config.getMotdColor());
			return;
		}
		
		if(caster.getLevel() < obj.getTemplate().getLevel()) {
			SocketManager.GAME_SEND_Im_PACKET(caster, "119");
			return;
		}
		
		boolean isOk = true, send = true;
		int turn = 0;
		String arg = "";
		
		try {
			for(String type: this.type.split("\\;")) {
				if(!this.args.isEmpty())
					arg = args.split("\\|", 2)[turn];
				
				int typeNumber = Integer.parseInt(type);
				ObjectActionCallback callback = ObjectActionManager.getInstance().getPluginCallback(typeNumber);
						
				if (callback == null)
					callback = ObjectActionManager.getInstance().getOriginCallback(typeNumber);
				
				if (callback != null) {
					ObjectActionResult result =	callback.execute(player, typeNumber, arg, obj, cellid);
					isOk = result.isOk();
					send = result.isSend();
				} else if(Server.config.isDebug()) {
					logger.debug("- Action id "+type+" non implant� dans le syst�me !");
				}
				
				turn++;
			}
		} catch(Exception e) {
			logger.error(e.toString());
		}
		
		boolean effect = this.haveEffect(obj.getTemplate().getId(), player);
		
		switch(obj.getTemplate().getId()) {
			case 7799:
				send = false;
				break;
		}
		
		if(effect)
			isOk = true;
		if(isOk)
			effect = true;
		if(this.type.split("\\;").length > 1)
			isOk = true;
		if(objet != -1)	{
			if(send)
				SocketManager.GAME_SEND_Im_PACKET(caster, "022;" + 1 + "~" + obj.getTemplate().getId());
			if(isOk && effect && obj.getTemplate().getId() != 7799)
				caster.removeItem(objet, 1, true, true);			
		}
	}	

	private boolean haveEffect(int id, Player player) {
		switch(id) {
			case 7799://Le Saut Sifflard
				player.toogleOnMount();
				return false;
				
			case 10832://Craqueloroche
				player.getMap().spawnNewGroup(true, player.getCell(), "483,1,1000", "MiS=" + player.getId());
				return true;
				
			case 10664://Abragland
				player.getMap().spawnNewGroup(true, player.getCell(), "47,1,1000", "MiS=" + player.getId());
				return true;
				
			/*case 10665://Coffre de Jorbak
				perso.setCandy(10688);
				return true;
			
			case 10670://Parchemin de persimol
				perso.setBenediction(10682);
				return true;
			*/
			case 8435://Ballon Rouge Magique
				player.getMap().send("GA;208;" + player.getId() + ";" + player.getCell().getId() + ",2906,11,8,1");
				return true;
				
			case 8624://Ballon Bleu Magique
				player.getMap().send("GA;208;" + player.getId() + ";" + player.getCell().getId() + ",2907,11,8,1");
				return true;
				
			case 8625://Ballon Vert Magique
				player.getMap().send("GA;208;" + player.getId() + ";" + player.getCell().getId() + ",2908,11,8,1");
				return true;
				
			case 8430://Ballon Jaune Magique
				player.getMap().send("GA;208;" + player.getId() + ";" + player.getCell().getId() + ",2909,11,8,1");
				return true;
			/*
			case 8621://Cawotte Maudite
				perso.set_gfxID(1109);
				SocketManager.GAME_SEND_ALTER_GM_PACKET(perso.getMap(), perso);
				return true;
				
			case 8626://Nisitik Miditik
				perso.set_gfxID(1046);	
				SocketManager.GAME_SEND_ALTER_GM_PACKET(perso.getMap(), perso);
				return true;
			
			case 10833://Chapain
				perso.set_gfxID(9001);	
				SocketManager.GAME_SEND_ALTER_GM_PACKET(perso.getMap(), perso);
				return true;*/
			
			case 10839://Monstre Pain
				player.getMap().spawnNewGroup(true, player.getCell(), "2787,1,1000", "MiS=" + player.getId());
				return true;
				
			/*case 8335://Cadeau 1
				Noel.getRandomObjectOne(perso);
				return true;
			case 8336://Cadeau 2
				Noel.getRandomObjectTwo(perso);
				return true;
			case 8337://Cadeau 3
				Noel.getRandomObjectTree(perso);
				return true;
			case 8339://Cadeau 4
				Noel.getRandomObjectFour(perso);
				return true;
			case 8340://Cadeau 5
				Noel.getRandomObjectFive(perso);
				return true;
			case 10912://Cadeau nowel 1
				return false;
			case 10913://Cadeau nowel 2
				return false;
			case 10914://Cadeau nowel 3
				return false;*/	
		}
		return false;
	}
}
