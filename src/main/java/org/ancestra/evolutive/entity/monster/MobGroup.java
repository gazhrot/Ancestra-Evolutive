package org.ancestra.evolutive.entity.monster;

import org.ancestra.evolutive.common.Constants;
import org.ancestra.evolutive.common.Formulas;
import org.ancestra.evolutive.core.Server;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.entity.Creature;
import org.ancestra.evolutive.map.Case;
import org.ancestra.evolutive.map.Maps;

import java.util.*;
import java.util.Map.Entry;

public class MobGroup extends Creature {
	private static final Random random = new Random();
    private static final int defaultMaxGroup = 8;

    private int align = -1;
	private int aggroDistance = 0;
	private boolean isFix = false;
	private String condition = "";
	private Timer timer;
	private Map<Integer, MobGrade> mobs = new TreeMap<>();

    /**
     *
     * @param id
     * @param align
     * @param possibles
     * @param map
     * @param cell
     * @param maxSize
     */
	public MobGroup(int id,int align, ArrayList<MobGrade> possibles, Maps map, Case cell, int maxSize) {
		this(id,map,cell,"",align,false,false);
        int groupSize = (maxSize == -1)?defaultMaxGroup:random.nextInt(maxSize)+1;

		//On v�rifie qu'il existe des monstres de l'alignement demand� pour �viter les boucles infinies
		boolean haveSameAlign = false;
		for(MobGrade mob : possibles)
			if(mob.getTemplate().getAlign() == align)
				haveSameAlign = true;
		
		if(!haveSameAlign)
			return;
		
		int guid = -1, maxLevel = 0;
		
		for(int a = 0; a < groupSize; a++) {
			MobGrade Mob = null;
			do {
				//on prend un mob au hasard dans le tableau
				Mob = possibles.get(random.nextInt(possibles.size())).getCopy();
			} while(Mob.getTemplate().getAlign() != align);
			
			if(Mob.getLevel() > maxLevel)
				maxLevel = Mob.getLevel();
			
			this.mobs.put(guid, Mob);
			guid--;
		}
		

		if(align != Constants.ALIGNEMENT_NEUTRE)
			this.aggroDistance = 15;
		else
            aggroDistance = Constants.getAggroByLevel(maxLevel);
	}

    public MobGroup(int id,Maps map,Case cell,String group){
        this(id,map,cell,group,"");
    }

	public MobGroup(int id,Maps map,Case cell, String group,String condition) {
        this(id,map,cell,condition,Constants.ALIGNEMENT_NEUTRE,false,true);
		aggroDistance = Constants.getAggroByLevel(0);
        int guid = -1;

        for(String data : group.split("\\;")) {
            String[] infos = data.split("\\,");
            try	{
                int uid = Integer.parseInt(infos[0]);
                int min = Integer.parseInt(infos[1]);
                int max = Integer.parseInt(infos[2]);
                MobTemplate m = World.data.getMonstre(uid);
                List<MobGrade> mgs = new ArrayList<>();

                //on ajoute a la liste les grades possibles
                for(MobGrade MG : m.getGrades().values())
                    if(MG.getLevel() >= min && MG.getLevel() <= max)
                        mgs.add(MG);

                if(mgs.isEmpty())
                    continue;

                //On prend un grade au hasard entre 0 et size -1 parmis les mobs possibles
                mobs.put(guid, mgs.get(Formulas.getRandomValue(0, mgs.size() - 1)));
                guid--;
            } catch(Exception e) {}
        }

	}

    public MobGroup(int id,Maps map,Case cell,String condition,int alignement,boolean timer,boolean fix){
        super(id,"Group id " + id + " on map "+ map.getId(), map,cell,random.nextInt(7));
        this.isFix = fix;
        this.condition = condition;
        this.align = alignement;
        if(timer){
            startTimer();
        }
        helper = new MobGroupHelper(this);
    }

	public int getAlign() {
		return align;
	}

	public int getAggroDistance() {
		return aggroDistance;
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

	public Map<Integer, MobGrade> getMobs() {
		return mobs;
	}

	public void startTimer() {
		this.timer = new Timer();
		this.timer.schedule(new TimerTask() {
			@Override
			public void run() {
				condition = "";
			}
		}, Server.config.getArenaTimer());
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
		this.getCell().getId();
		toReturn.append("+").append(this.getCell().getId()).append(";").append(this.getOrientation()).append(";0;").append(this.getId())
				.append(";").append(id).append(";-3;").append(gfx).append(";").append(level).append(";").append(color);
		
		return toReturn.toString();
	}
}