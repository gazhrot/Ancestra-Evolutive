package org.ancestra.evolutive.entity.monster;

import org.ancestra.evolutive.common.Constants;
import org.ancestra.evolutive.common.Formulas;
import org.ancestra.evolutive.core.Server;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.map.Maps;

import java.util.*;
import java.util.Map.Entry;

public class MobGroup {
	
	private int id;
	private int cellid;
	private int orientation = 2;
	private int align = -1;
	private int aggroDistance = 0;
	private boolean isFix = false;
	private String condition = "";
	private Timer timer;
	private Map<Integer, MobGrade> mobs = new TreeMap<>();
	
	public MobGroup(int id,int align, ArrayList<MobGrade> possibles, Maps map, int cell, int maxSize) {
		this.id = id;
		this.align = align;
		
		int rand = 0, nbr = 0;
		
		switch(maxSize) {
			case 0:
				return;
			case 1:
				nbr = 1;
				break;
			case 2:
				nbr = Formulas.getRandomValue(1,2);	//1:50%	2:50%
				break;
			case 3:
				nbr = Formulas.getRandomValue(1,3);	//1:33.3334%	2:33.3334%	3:33.3334%
				break;
			case 4:
				rand = Formulas.getRandomValue(0, 99);
				if(rand < 22)		//1:22%
					nbr = 1;
				else if(rand < 48)	//2:26%
					nbr = 2;
				else if(rand < 74)	//3:26%
					nbr = 3;

				else				//4:26%
					nbr = 4;
				break;
			case 5:
				rand = Formulas.getRandomValue(0, 99);
				if(rand < 15)		//1:15%
					nbr = 1;
				else if(rand < 35)	//2:20%
					nbr = 2;
				else if(rand < 60)	//3:25%
					nbr = 3;
				else if(rand < 85)	//4:25%
					nbr = 4;
				else				//5:15%
					nbr = 5;
				break;
			case 6:
				rand = Formulas.getRandomValue(0, 99);
				if(rand < 10)		//1:10%
					nbr = 1;
				else if(rand < 25)	//2:15%
					nbr = 2;
				else if(rand < 45)	//3:20%
					nbr = 3;
				else if(rand < 65)	//4:20%
					nbr = 4;
				else if(rand < 85)	//5:20%
					nbr = 5;
				else				//6:15%
					nbr = 6;
				break;
			case 7:
				rand = Formulas.getRandomValue(0, 99);
				if(rand < 9)		//1:9%
					nbr = 1;
				else if(rand < 20)	//2:11%
					nbr = 2;
				else if(rand < 35)	//3:15%
					nbr = 3;
				else if(rand < 55)	//4:20%
					nbr = 4;
				else if(rand < 75)	//5:20%
					nbr = 5;
				else if(rand < 91)	//6:16%
					nbr = 6;
				else				//7:9%
					nbr = 7;
				break;
			default:
				rand = Formulas.getRandomValue(0, 99);
				if(rand < 9)		//1:9%
					nbr = 1;
				else if(rand<20)	//2:11%
					nbr = 2;
				else if(rand<33)	//3:13%
					nbr = 3;
				else if(rand<50)	//4:17%
					nbr = 4;
				else if(rand<67)	//5:17%
					nbr = 5;
				else if(rand<80)	//6:13%
					nbr = 6;
				else if(rand<91)	//7:11%
					nbr = 7;
				else				//8:9%
					nbr = 8;
				break;
		}
		
		//On v�rifie qu'il existe des monstres de l'alignement demand� pour �viter les boucles infinies
		boolean haveSameAlign = false;
		for(MobGrade mob : possibles)
			if(mob.getTemplate().getAlign() == align)
				haveSameAlign = true;
		
		if(!haveSameAlign)
			return;
		
		int guid = -1, maxLevel = 0;
		
		for(int a = 0; a < nbr; a++) {
			MobGrade Mob = null;
			do {
				int random = Formulas.getRandomValue(0, possibles.size() - 1);//on prend un mob au hasard dans le tableau
				Mob = possibles.get(random).getCopy();	
			} while(Mob.getTemplate().getAlign() != align);
			
			if(Mob.getLevel() > maxLevel)
				maxLevel = Mob.getLevel();
			
			this.mobs.put(guid, Mob);
			guid--;
		}
		
		this.aggroDistance = Constants.getAggroByLevel(maxLevel);
		
		if(align != Constants.ALIGNEMENT_NEUTRE)
			this.aggroDistance = 15;
		
		this.cellid = (cell == -1 ? map.getRandomFreeCell() : cell);
		
		if(this.cellid == 0)
			return;
		
		this.orientation = Formulas.getRandomValue(0, 3)*2;
		this.isFix = false;
	}
	
	public MobGroup(int id, int cellid, String group) {
		this.id = id;
		this.align = Constants.ALIGNEMENT_NEUTRE;
		this.cellid = cellid;
		this.aggroDistance = Constants.getAggroByLevel(0);
		this.isFix = true;
		
		int guid = -1;
		
		for(String data : group.split("\\;")) {
			String[] infos = data.split("\\,");
			try	{
				int uid = Integer.parseInt(infos[0]);
				int min = Integer.parseInt(infos[1]);
				int max = Integer.parseInt(infos[2]);
				MobTemplate m = World.data.getMonstre(uid);
				List<MobGrade> mgs = new ArrayList<MobGrade>();
				
				//on ajoute a la liste les grades possibles
				for(MobGrade MG : m.getGrades().values())
					if(MG.getLevel() >= min && MG.getLevel() <= max)
						mgs.add(MG);
				
				if(mgs.isEmpty())
					continue;
				
				//On prend un grade au hasard entre 0 et size -1 parmis les mobs possibles
				this.mobs.put(guid, mgs.get(Formulas.getRandomValue(0, mgs.size() - 1)));
				guid--;
			} catch(Exception e) {}
		}
		
		this.orientation = (Formulas.getRandomValue(0, 3) * 2) + 1;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCellid() {
		return cellid;
	}

	public void setCellid(int cellid) {
		this.cellid = cellid;
	}

	public int getOrientation() {
		return orientation;
	}

	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}

	public int getAlign() {
		return align;
	}

	public void setAlign(int align) {
		this.align = align;
	}

	public int getAggroDistance() {
		return aggroDistance;
	}

	public void setAggroDistance(int aggroDistance) {
		this.aggroDistance = aggroDistance;
	}

	public boolean isFix() {
		return isFix;
	}

	public void setFix(boolean isFix) {
		this.isFix = isFix;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public Map<Integer, MobGrade> getMobs() {
		return mobs;
	}

	public void setMobs(Map<Integer, MobGrade> mobs) {
		this.mobs = mobs;
	}

	public void startTimer() {
		this.timer = new Timer();
		this.timer.schedule(new TimerTask() {
			public void run() {
				condition = "";
			}
		}, Server.config.getArenaTimer());
	}
	
	public void stopTimer() {
		try {
			this.timer.cancel();
		} catch(Exception e) {}
	}
	
	public String parseGM() {
		StringBuilder id = new StringBuilder();
		StringBuilder gfx = new StringBuilder();
		StringBuilder level = new StringBuilder();
		StringBuilder color = new StringBuilder();
		StringBuilder toReturn = new StringBuilder();
		
		boolean isFirst = true;
		
		if(this.getMobs().isEmpty())
			return "";
		
		for(Entry<Integer, MobGrade> entry : this.getMobs().entrySet()) {
			if(!isFirst) {
				id.append(",");
				gfx.append(",");
				level.append(",");
			}
			id.append(entry.getValue().getTemplate().getId());
			gfx.append(entry.getValue().getTemplate().getGfx()).append("^100");
			level.append(entry.getValue().getLevel());
			color.append(entry.getValue().getTemplate().getColors()).append(";0,0,0,0;");
			
			isFirst = false;
		}
		
		toReturn.append("+").append(this.getCellid()).append(";").append(this.getOrientation()).append(";0;").append(this.getId())
				.append(";").append(id).append(";-3;").append(gfx).append(";").append(level).append(";").append(color);
		
		return toReturn.toString();
	}		
}