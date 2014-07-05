package org.ancestra.evolutive.entity.monster;

import org.ancestra.evolutive.common.Pathfinding;
import org.ancestra.evolutive.core.Server;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.entity.Creature;
import org.ancestra.evolutive.enums.Alignement;
import org.ancestra.evolutive.map.Case;
import org.ancestra.evolutive.map.Maps;

import java.util.*;

import static org.ancestra.evolutive.enums.Alignement.NEUTRE;

public class MobGroup extends Creature {
	private static final Random random = new Random();
    private static final int defaultMaxGroup = 8;

    private final Alignement alignement;
    private final boolean isFix;
    private int aggroDistance;
	private String condition = "";
	private Timer timer;
	private Map<Integer, MobGrade> mobs = new TreeMap<>();

    /**
     * Creer un nouveau mobGroup
     * @param id id du mobGroup
     * @param alignement alignement
     * @param possibles mob possibles
     * @param map map initiale
     * @param cell cell actuelle
     * @param maxSize taille maximale
     */
	public MobGroup(int id,Alignement alignement, ArrayList<MobGrade> possibles, Maps map, Case cell, int maxSize) {
		this(id,map,cell,"", alignement,false,false);
        int groupSize = (maxSize == -1)?defaultMaxGroup:random.nextInt(maxSize)+1;
        possibles = getPossibleMob(alignement,possibles);
		if(!possibles.isEmpty()){
            for(int a = 0; a < groupSize; a++) {
                MobGrade Mob = possibles.get(random.nextInt(possibles.size())).getCopy();
                this.mobs.put(-(mobs.size()), Mob);
            }
        }
        this.aggroDistance = generateAggroDistance(possibles);
	}

    public MobGroup(int id,Maps map,Case cell,String group,boolean fix){
        this(id,map,cell,group,"",fix);
    }

    public MobGroup(int id,Maps map,Case cell,String group){
        this(id,map,cell,group,"",false);
    }

    public MobGroup(int id,Maps map,Case cell,String group,String condition){
        this(id,map,cell,group,condition,false);
    }

	public MobGroup(int id,Maps map,Case cell, String group,String condition,boolean fix) {
        this(id, map, cell, condition,NEUTRE, false, fix);
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
                mobs.put(-(mobs.size()+1), mgs.get(random.nextInt(mgs.size())));
            } catch(Exception e) {}
        }
        this.aggroDistance = generateAggroDistance(mobs.values());
	}

    public Alignement getAlignement() {
		return alignement;
	}

	public int getAggroDistance() {
		return aggroDistance;
	}

	public boolean isFix() {
		return isFix;
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

    @Override
    protected boolean onMoveCell(Case oldCell,Case newCell) {
        if(isFix) return true;
        for(MobGrade mob : mobs.values()){
            if(cell != null) {
                mob.setCell(newCell);
            }
        }
        String pathStr = Pathfinding.getShortestStringPathBetween(oldCell.getMap(), oldCell.getId(), newCell.getId(), 0);
        if (pathStr != null) {
            newCell.getMap().send("GA0;1;" + this.getId() +";"+ pathStr);
        }

        return true;
    }


    private MobGroup(int id,Maps map,Case cell,String condition,Alignement alignement,boolean timer,boolean fix){
        super(id,"Group id " + id + " on map "+ map.getId(), map,cell,random.nextInt(7));
        this.isFix = fix;
        this.condition = condition;
        this.alignement = alignement;
        if(timer){
            startTimer();
        }
        helper = new MobGroupHelper(this);
    }

    /**
     * Retourne une liste de mob ou seuls ceux avec le bon alignement on ete choisi
     * @param alignement alignement requis
     * @param mobs mobs initiaux
     * @return mobs purges de ceux d un mauvaise alignement
     */
    private ArrayList<MobGrade> getPossibleMob(Alignement alignement,ArrayList<MobGrade> mobs){
        ArrayList<MobGrade> cleanMobs = new ArrayList<>();
        for(MobGrade mob : mobs){
            if(mob.getTemplate().getAlignement() == alignement){
                cleanMobs.add(mob);
            }
        }
        return cleanMobs;
    }

    /**
     * Liste de mobs dont la distance doit etre trouvee
     * @param mobs liste des mobs possibles
     * @return retourne la distance d aggro
     */
    private int generateAggroDistance(Collection<MobGrade> mobs){
        if(this.alignement != NEUTRE) return 15;
        int maxLevel = 0;
        for(MobGrade mob : mobs){
            if(mob.getLevel() > maxLevel){
                maxLevel = mob.getLevel();
            }
        }
        return maxLevel>500?3:maxLevel/50;
    }
}