package kernel;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.tool.command.Command;

public class Mapid extends Command<Player> {

	public Mapid(String name) {
		super(name);
	}

	@Override
	public void action(Player arg0, String[] arg1) {
		if(arg0 == null)
			return;
		
		int mapid = arg0.getCurMap().getId();
		arg0.sendText("L'id de la carte actuelle est " + mapid);
	}	
}