package org.ancestra.evolutive.common;

import org.ancestra.evolutive.core.Log;
import org.ancestra.evolutive.fight.fight.Fight;
import org.ancestra.evolutive.fight.Fighter;
import org.ancestra.evolutive.fight.trap.Piege;
import org.ancestra.evolutive.map.Case;
import org.ancestra.evolutive.map.Maps;
import org.ancestra.evolutive.object.ObjectType;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class Pathfinding {

	public static int isValidPath(Maps map, int cellID, AtomicReference<String> pathRef,Fight fight){
        int newPos = cellID;
        int Steps = 0;
        String path = pathRef.get();
        String newPath = "";
        for (int i = 0; i < path.length(); i += 3) {
            String SmallPath = path.substring(i, i+3);
            char dir = SmallPath.charAt(0);
            String[] aPathInfos = ValidSinglePath(newPos, SmallPath, map, fight);
            newPos = Integer.parseInt(aPathInfos[1]);
            Steps += Integer.parseInt(aPathInfos[2]);
            newPath += dir + CryptManager.cellID_To_Code(newPos);
            if(aPathInfos[0].equalsIgnoreCase("stop")) {
                break;
            }
        }
        pathRef.set(newPath);
        return Steps;
	}

    public static String[] ValidSinglePath(int CurrentPos, String Path, Maps map, Fight fight) {
        char dir = Path.charAt(0);
        int _nSteps;
        String[] result = new String[3];
        int dirCaseID = CryptManager.cellCode_To_ID(Path.substring(1));
        int lastPos = CurrentPos;
        for(_nSteps = 0; _nSteps <= 64; _nSteps++){
            result[1] = Integer.toString(lastPos);
            if(lastPos == dirCaseID){
                result[0] = "ok";
                break;
            }
            lastPos = GetCaseIDFromDirection(lastPos, dir, map, fight != null);
            if(map.getCases().get(lastPos) == null || !map.getCases().get(lastPos).isWalkable(true)){
                result[0] = "stop";
                break;
            }
            if(fight != null) {
                if(fight.isOccuped(lastPos) || getEnemyFighterArround(lastPos, map, fight) != null) {
                    result[0] = "stop";
                    break;
                }
                for(Piege p : fight.getTraps()) {
                    int dist = getDistanceBetween(map,p.get_cell().getId(),lastPos);
                    if(dist <= p.get_size()) {//on arrete le d�placement sur la 1ere case du piege
                        result[0] = "stop";
                        break;
                    }
                }
            }
        }
        result[2] = Integer.toString(_nSteps);
        return result;
    }

	public static ArrayList<Fighter> getEnemyFighterArround(int cellID,Maps map,Fight fight)
	{
		char[] dirs = {'b','d','f','h'};
		ArrayList<Fighter> enemy = new ArrayList<>();
		for(char dir : dirs){
			if(map.getCases().get(GetCaseIDFromDirection(cellID, dir, map, false)) == null)
				continue;
			Fighter f = map.getCases().get(GetCaseIDFromDirection(cellID, dir, map, false)).getFirstFighter();
			if(f != null)
				if(f.getTeam() != fight.getCurFighter().getTeam())
					enemy.add(f);
		}
		if(enemy.size() == 0 || enemy.size() == 4)
			return null;

		return enemy;
	}

	public static boolean isNextTo (int cell1, int cell2)
	{
		if(cell1 + 14 == cell2 || cell1 + 15 == cell2 || cell1 - 14 == cell2 || cell1 -15 ==cell2)
			return true;
		else
			return false;
	}

	public static int GetCaseIDFromDirection(int CaseID, char Direction, Maps map, boolean Combat)
	{
		switch (Direction)
        {
            case 'a':
                return Combat ? -1 : CaseID + 1;
            case 'b':
                return CaseID + map.getWidth();
            case 'c':
                return Combat ? -1 : CaseID + (map.getWidth() * 2 - 1);
            case 'd':
                return  CaseID + (map.getWidth() - 1);
            case 'e':
                return Combat ? -1 : CaseID - 1;
            case 'f':
                return CaseID - map.getWidth();
            case 'g':
                return Combat ? -1 : CaseID - (map.getWidth() * 2 - 1);
            case 'h':
                return  CaseID - map.getWidth() + 1;
        }
        return -1; 
	}
	
	public static int getDistanceBetween(Maps map,int id1,int id2)
	{
		if(id1 == id2)return 0;
		if(map == null)return 0;
		int diffX = Math.abs(getCellXCoord(map, id1) - getCellXCoord(map,id2));
		int diffY = Math.abs(getCellYCoord(map, id1) - getCellYCoord(map,id2));
		return (diffX + diffY);
	}

	public static int newCaseAfterPush(Maps map, Case CCase,Case TCase, int value)
	{
		//Si c'est les memes case, il n'y a pas a bouger
		if(CCase.getId() == TCase.getId())return 0;
		char c = getDirBetweenTwoCase(CCase.getId(), TCase.getId(), map, true);
		int id = TCase.getId();
		if(value <0)
		{
			c = getOpositeDirection(c);
			value = -value;
		}
		for(int a = 0; a<value;a++)
		{
			int nextCase = GetCaseIDFromDirection(id, c, map, true);
			if(map.getCases().get(nextCase) != null && map.getCases().get(nextCase).isWalkable(true) && map.getCases().get(nextCase).getFighters().isEmpty())
				id = nextCase;
			else
				return -(value-a);
		}
		
		if(id == TCase.getId())
			id = 0;
		return id;
	}
	
	private static char getOpositeDirection(char c)
	{
		switch(c)
		{
			case 'a':
				return 'e';
			case 'b':
				return 'f';
			case 'c':
				return 'g';
			case 'd':
				return 'h';
			case 'e':
				return 'a';
			case 'f':
				return 'b';
			case 'g':
				return 'c';
			case 'h':
				return 'd';
		}
		return 0x00;
	}

	public static boolean casesAreInSameLine(Maps map,int c1,int c2,char dir)
	{
		if(c1 == c2)
			return true;
		
		if(dir != 'z')//Si la direction est d�finie
		{
			for(int a = 0;a<70;a++)
			{
				if(GetCaseIDFromDirection(c1, dir, map, true) == c2)
					return true;
				if(GetCaseIDFromDirection(c1, dir, map, true) == -1)
					break;
				c1 = GetCaseIDFromDirection(c1, dir, map, true);
			}
		}else//Si on doit chercher dans toutes les directions
		{
			char[] dirs = {'b','d','f','h'};
			for(char d : dirs)
			{
				int c = c1;
				for(int a = 0;a<70;a++)
				{
					if(GetCaseIDFromDirection(c, d, map, true) == c2)
						return true;
					c = GetCaseIDFromDirection(c, d, map, true);
				}
			}
		}
		return false;
	}

	public static ArrayList<Fighter> getCiblesByZoneByWeapon(Fight fight, ObjectType type, Case cell, int castCellID)
	{
		ArrayList<Fighter> cibles = new ArrayList<Fighter>();
		char c = getDirBetweenTwoCase(castCellID,cell.getId(),fight.getMap(),true);
		if(c == 0)
		{
			//On cible quand meme le fighter sur la case
			if(cell.getFirstFighter() != null) cibles.add(cell.getFirstFighter());
			return cibles;
		}
		
		switch(type) {
			//Cases devant celle ou l'on vise
			case MARTEAU:
				Fighter f = getFighter2CellBefore(castCellID,c,fight.getMap());
				if(f != null)
					cibles.add(f);
				Fighter g = get1StFighterOnCellFromDirection(fight.getMap(),castCellID,(char)(c-1));
				if(g != null)
					cibles.add(g);//Ajoute case a gauche
				Fighter h = get1StFighterOnCellFromDirection(fight.getMap(),castCellID,(char)(c+1));
				if(h != null)
					cibles.add(h);//Ajoute case a droite
				Fighter i = cell.getFirstFighter();
				if(i != null)
					cibles.add(i);
			break;
			case BATON:
				Fighter j = get1StFighterOnCellFromDirection(fight.getMap(),castCellID,(char)(c-1));
				if(j != null)
					cibles.add(j);//Ajoute case a gauche
				Fighter k = get1StFighterOnCellFromDirection(fight.getMap(),castCellID,(char)(c+1));
				if(k != null)
					cibles.add(k);//Ajoute case a droite
				
				Fighter l = cell.getFirstFighter();
				if(l != null)
					cibles.add(l);//Ajoute case cible
			break;
			case PIOCHE:
			case EPEE:
			case FAUX:
			case DAGUES:
			case BAGUETTE:
			case PELLE:
			case ARC:
			case HACHE:
				Fighter m = cell.getFirstFighter();
				if(m != null)
					cibles.add(m);
			break;	
		}
		return cibles;
	}

	private static Fighter get1StFighterOnCellFromDirection(Maps map, int id, char c)
	{ 
		if(c == (char)('a'-1))
			c = 'h';
		if(c == (char)('h'+1))
			c = 'a';
		return map.getCases().get(GetCaseIDFromDirection(id, c, map, false)).getFirstFighter();
	}

	private static Fighter getFighter2CellBefore(int CellID, char c,Maps map)
	{
		int new2CellID = GetCaseIDFromDirection(GetCaseIDFromDirection(CellID, c, map, false), c, map, false);
		return map.getCases().get(new2CellID).getFirstFighter();
	}

	public static char getDirBetweenTwoCase(int cell1ID, int cell2ID,Maps map, boolean Combat)
	{
		ArrayList<Character> dirs = new ArrayList<Character>();
		dirs.add('b');
		dirs.add('d');
		dirs.add('f');
		dirs.add('h');
		if(!Combat)
		{
			dirs.add('a');
			dirs.add('b');
			dirs.add('c');
			dirs.add('d');
		}
		for(char c : dirs)
		{
			int cell = cell1ID;
			for(int i = 0; i <= 64; i++)
			{
				if(GetCaseIDFromDirection(cell, c, map, Combat) == cell2ID)
					return c;
				cell = GetCaseIDFromDirection(cell, c, map, Combat);
			}
		}
		return 0;
	}

	public static ArrayList<Case> getCellListFromAreaString(Maps map,int cellID,int castCellID, String zoneStr, int PONum, boolean isCC)
	{
		ArrayList<Case> cases = new ArrayList<Case>();
		int c = PONum;
		if(map.getCases().get(cellID) == null)return cases;
		cases.add(map.getCases().get(cellID));
		
		int taille = CryptManager.getIntByHashedValue(zoneStr.charAt(c+1));
		switch(zoneStr.charAt(c))
		{
			case 'C'://Cercle
				for(int a = 0; a < taille;a++)
				{
					char[] dirs = {'b','d','f','h'};
					ArrayList<Case> cases2 = new ArrayList<Case>();//on �vite les modifications concurrentes
					cases2.addAll(cases);
					for(Case aCell : cases2)
					{
						for(char d : dirs)
						{
							Case cell = map.getCases().get(Pathfinding.GetCaseIDFromDirection(aCell.getId(), d, map, true));
							if(cell == null)continue;
							if(!cases.contains(cell))
								cases.add(cell);
						}
					}
				}
			break;
			
			case 'X'://Croix
				char[] dirs = {'b','d','f','h'};
				for(char d : dirs)
				{
					int cID = cellID;
					for(int a = 0; a< taille; a++)
					{
						cases.add(map.getCases().get(GetCaseIDFromDirection(cID, d, map, true)));
						cID = GetCaseIDFromDirection(cID, d, map, true);
					}
				}
			break;
			
			case 'L'://Ligne
				char dir = Pathfinding.getDirBetweenTwoCase(castCellID, cellID, map,true);
				for(int a = 0; a< taille; a++)
				{
					cases.add(map.getCases().get(GetCaseIDFromDirection(cellID, dir, map, true)));
					cellID = GetCaseIDFromDirection(cellID, dir, map, true);
				}
			break;
			
			case 'P'://Player?
				
			break;
			
			default:
				Log.addToLog("[FIXME]Type de port�e non reconnue: "+zoneStr.charAt(0));
			break;
		}
		return cases;
	}

	public static int getCellXCoord(Maps map, int cellID)
	{
		if(map == null) return 0;
		int w = map.getWidth();
		return ((cellID - (w -1) * getCellYCoord(map,cellID)) / w);
	}
	
	public static int getCellYCoord(Maps map, int cellID)
	{
		int w = map.getWidth();
		int loc5 = (cellID/ ((w*2) -1));
		int loc6 = cellID - loc5 * ((w * 2) -1);
		int loc7 = loc6 % w;
		return (loc5 - loc7);
	}
	
	public static boolean checkLoS(Maps map, int cell1, int cell2,Fighter fighter, boolean isPeur, ArrayList<Fighter> Fighters)
	{
		if(fighter != null && fighter.getPersonnage() != null){ 
			return true;
		}
		ArrayList<Integer> CellsToConsider = new ArrayList<Integer>();
		CellsToConsider = getLoSBotheringIDCases(map, cell1, cell2, true);
		if(CellsToConsider == null) {
			return true;
		}
		for(Integer cellID : CellsToConsider) {
			if(map.getCases().get(cellID) != null){
				if(!map.getCases().get(cellID).blockLoS() || ( !map.getCases().get(cellID).isWalkable(false) && isPeur )) {
					return false;
				}
			}
		}
		return true;
	}
	
	private static ArrayList<Integer> getLoSBotheringIDCases(Maps map, int cellID1, int cellID2, boolean Combat) {
		ArrayList<Integer> toReturn = new ArrayList<Integer>();
		int consideredCell1 = cellID1;
		int consideredCell2 = cellID2;
		char dir = 'b';
		int diffX = 0;
		int diffY = 0;
		int c1 = 0;
		int compteur = 0;
		ArrayList<Character> dirs = new ArrayList<Character>();
		dirs.add('b');
		dirs.add('d');
		dirs.add('f');
		dirs.add('h');
		
		while(getDistanceBetween(map, consideredCell1, consideredCell2) > 2 && compteur < 300) {
			diffX= getCellXCoord(map, consideredCell1) - getCellXCoord(map, consideredCell2);
			diffY= getCellYCoord(map, consideredCell1) - getCellYCoord(map, consideredCell2);
			if(Math.abs(diffX) > Math.abs(diffY)) { // si il ya une plus grande diff�rence pour la premi�re coordonn�e
				if(diffX > 0)
					dir = 'f';
				else dir = 'b';
				consideredCell1 = GetCaseIDFromDirection(consideredCell1, dir, map, Combat); // on avance le chemin d'obstacles possibles
				consideredCell2 = GetCaseIDFromDirection(consideredCell2, getOpositeDirection(dir), map, Combat); // des deux c�t�s
				if(consideredCell1 != -1 && map.getCases().get(consideredCell1) != null) toReturn.add(consideredCell1); // la liste des cases potentiellement obstacles
				if(consideredCell2 != -1 && map.getCases().get(consideredCell2) != null) toReturn.add(consideredCell2); // la liste des cases potentiellement obstacles
			} else if(Math.abs(diffX) < Math.abs(diffY)) { // si il y a une plus grand diff�rence pour la seconde
				if(diffY > 0) // d�termine dans quel sens
					dir = 'h';
				else dir = 'd';
				consideredCell1 = GetCaseIDFromDirection(consideredCell1, dir, map, Combat); // on avance le chemin d'obstacles possibles
				consideredCell2 = GetCaseIDFromDirection(consideredCell2, getOpositeDirection(dir), map, Combat); // des deux c�t�s
				if(consideredCell1 != -1 && map.getCases().get(consideredCell1) != null) toReturn.add(consideredCell1); // la liste des cases potentiellement obstacles
				if(consideredCell2 != -1 && map.getCases().get(consideredCell2) != null) toReturn.add(consideredCell2); // la liste des cases potentiellement obstacles
			} else {
				if(compteur == 0) // si on est en diagonale parfaite
					return getLoSBotheringCasesInDiagonal(map, cellID1, cellID2, diffX, diffY);
				if(dir == 'f' || dir == 'b') // on change la direction dans le cas o� on se retrouve en diagonale
					if(diffY > 0)
						dir = 'h';
					else dir = 'd';
				else if(dir == 'h' || dir == 'd')
					if(diffX > 0)
						dir = 'f';
					else dir = 'b';
				consideredCell1 = GetCaseIDFromDirection(consideredCell1, dir, map, Combat); // on avance le chemin d'obstacles possibles
				consideredCell2 = GetCaseIDFromDirection(consideredCell2, getOpositeDirection(dir), map, Combat); // des deux c�t�s
				if(consideredCell1 != -1 && map.getCases().get(consideredCell1) != null) toReturn.add(consideredCell1); // la liste des cases potentiellement obstacles
				if(consideredCell2 != -1 && map.getCases().get(consideredCell2) != null) toReturn.add(consideredCell2); // la liste des cases potentiellement obstacles
			}
			compteur++;			
		}
		if(getDistanceBetween(map, consideredCell1, consideredCell2) == 2) {
			dir = 0;
			diffX= getCellXCoord(map, consideredCell1) - getCellXCoord(map, consideredCell2);
			diffY= getCellYCoord(map, consideredCell1) - getCellYCoord(map, consideredCell2);
			if(diffX == 0)
				if(diffY > 0)
					dir = 'h';
				else dir = 'd';
			if(diffY == 0)
				if(diffX > 0)
					dir = 'f';
				else dir = 'b';
			if(dir != 0)
				c1 = GetCaseIDFromDirection(consideredCell1, dir, map, Combat);
				if(map.getCases().get(c1) != null) toReturn.add(c1);
		}
		return toReturn;
	}
	
	private static ArrayList<Integer> getLoSBotheringCasesInDiagonal(Maps map, int cellID1, int cellID2, int diffX, int diffY) {
		ArrayList<Integer> toReturn = new ArrayList<Integer>();
		char dir = 'a';
		if(diffX > 0 && diffY > 0)
			dir = 'g';
		if(diffX > 0 && diffY < 0)
			dir = 'e';
		if(diffX < 0 && diffY > 0)
			dir = 'a';
		if(diffX < 0 && diffY < 0)
			dir = 'c';
		int consideredCell = cellID1, compteur = 0;
		while(consideredCell != -1 && compteur < 100) {
			consideredCell = GetCaseIDFromDirection(consideredCell, dir, map, true);
			if(consideredCell == cellID2)
				return toReturn;
			toReturn.add(consideredCell);
			compteur++;
		}
		return toReturn;
	}
	
	public static boolean checkLoS(Maps map, int cell1, int cell2, Fighter fighter)
	{
		if(fighter.getPersonnage() != null)return true;
		int dist = getDistanceBetween(map,cell1,cell2);
		ArrayList <Integer> los = new ArrayList <Integer>();
		if(dist > 2)
			los = getLoS(cell1,cell2);
		if(los != null && dist > 2)
		{
			for(int i : los)
			{
				if(i != cell1 && i != cell2 && !map.getCases().get(i).blockLoS() )
					return false;
			}
		}
		if(dist > 2)
		{
			int cell = getNearestCellAround(map,cell2,cell1,null);
			if(cell != -1 && !map.getCases().get(cell).blockLoS())
				return false;
		}
		
		return true;
	}

	public static int getNearestCellAround(Maps map,int startCell, int endCell, ArrayList<Case> forbidens)
	{
		//On prend la cellule autour de la cible, la plus proche
		int dist = 1000;
		int cellID = startCell;
		if(forbidens == null)forbidens = new ArrayList<Case>();
		char[] dirs = {'b','d','f','h'};
		for(char d : dirs)
		{
			int c = Pathfinding.GetCaseIDFromDirection(startCell, d, map, true);
			int dis = Pathfinding.getDistanceBetween(map, endCell, c);
			if(map.getCases().get(c) == null)
				continue;
			if(dis < dist && map.getCases().get(c).isWalkable(true) && map.getCases().get(c).getFirstFighter() == null && !forbidens.contains(map.getCases().get(c)))
			{
				dist = dis;
				cellID = c;
			}
		}
		//On renvoie -1 si pas trouv�
		return cellID==startCell?-1:cellID;
	}
	public static ArrayList<Case> getShortestPathBetween(Maps map, int start, int dest, int distMax)
	{	
		ArrayList<Case> curPath = new ArrayList<Case>();
		ArrayList<Case> curPath2 = new ArrayList<Case>();
		ArrayList<Case> closeCells = new ArrayList<Case>();
		int limit = 1000;
		//int oldCaseID = start;
		Case curCase = map.getCases().get(start);
		int stepNum = 0;
		boolean stop = false;
		
		while(!stop && stepNum++ <= limit)
		{
			int nearestCell = getNearestCellAround(map,curCase.getId(),dest,closeCells);
			if(nearestCell == -1)
			{
				closeCells.add(curCase);
				if(curPath.size() > 0)
				{
				 	curPath.remove(curPath.size()-1);
				 	if(curPath.size()>0)curCase = curPath.get(curPath.size()-1);
				 	else curCase = map.getCases().get(start);
				}
				else
				{
					curCase = map.getCases().get(start);
				}
			}else if(distMax == 0 && nearestCell == dest)
			{
			 	curPath.add(map.getCases().get(dest));
			 	break;
			}else if(distMax > Pathfinding.getDistanceBetween(map, nearestCell, dest))
			{
			 	curPath.add(map.getCases().get(dest));
			 	break; 
			}else//on continue
			{
				curCase = map.getCases().get(nearestCell);
				closeCells.add(curCase);
				curPath.add(curCase);
			}
		}
		
		curCase = map.getCases().get(start);
		closeCells.clear();
		if(!curPath.isEmpty())
		{
			closeCells.add(curPath.get(0));
		}
		
		while(!stop && stepNum++ <= limit)
		{
			
			int nearestCell = getNearestCellAround(map,curCase.getId(),dest,closeCells);
			if(nearestCell == -1)
			{
				closeCells.add(curCase);
				if(curPath2.size() > 0)
				{
					curPath2.remove(curPath2.size()-1);
				 	if(curPath2.size()>0)curCase = curPath2.get(curPath2.size()-1);
				 	else curCase = map.getCases().get(start);
				}
				else//Si retour a zero
				{
					curCase = map.getCases().get(start);
				}
			}else if(distMax == 0 && nearestCell == dest)
			{
				curPath2.add(map.getCases().get(dest));
			 	break;
			}else if(distMax > Pathfinding.getDistanceBetween(map, nearestCell, dest))
			{
			 	curPath2.add(map.getCases().get(dest));
			 	break; 
			}else//on continue
			{
				curCase = map.getCases().get(nearestCell);
				closeCells.add(curCase);
				curPath2.add(curCase);
			}
		}
		
		if((curPath2.size() < curPath.size() && curPath2.size() > 0) || curPath.isEmpty())
			curPath = curPath2;
		return curPath;
	}
	
	public static ArrayList<Integer> getListCaseFromFighter(Fight fight, Fighter fighter)
	{
		ArrayList<Integer> cells = new ArrayList<Integer>();
		int start = fighter.get_fightCell(true).getId();
		int[] curPath;
		int i = 0;
		if(fighter.getCurPM(fight) > 0)
			curPath = new int[fighter.getCurPM(fight)];
		else
			return null;
		if(curPath.length == 0)
			return null;
		while(curPath[0] != 5)
		{
			curPath[i]++;
			if(curPath[i] == 5 && i != 0)
			{
				curPath[i] = 0;
				i--;
			}
			else 
			{
				int curCell = getCellFromPath(start,curPath);
				if(fight.getMap().getCases().get(curCell) != null && fight.getMap().getCases().get(curCell).isWalkable(true)
						&& fight.getMap().getCases().get(curCell) != null
						&& fight.getMap().getCases().get(curCell).getFirstFighter() == null)
				{
					if(!cells.contains(curCell))
					{
						cells.add(curCell);
						if(i < curPath.length - 1)
							i++;
					}
				}
			}
		}
		
		return triCellList(fight, fighter,cells);
	}
	
	public static int getCellFromPath(int start,int[] path)
	{
		int cell = start,i = 0;
		while(i < path.length)
		{
			if(path[i] == 1)
				cell -= 15;
			if(path[i] == 2)
				cell -= 14;
			if(path[i] == 3)
				cell += 15;
			if(path[i] == 4)
				cell += 14;
			i++;
		}
		return cell;
	}
	
	public static ArrayList<Integer> triCellList(Fight fight, Fighter fighter, ArrayList<Integer> cells)
	{
		ArrayList<Integer> Fcells = new ArrayList<Integer>();
		ArrayList<Integer> copie = cells;
		int dist = 100;
		int curCell = 0;
		int curIndex = 0;
		while(copie.size() > 0)
		{
			dist = 100;
			for(int i : copie)
			{
				int d = getDistanceBetween(fight.getMap(), fighter.get_fightCell(true).getId(), i);
				if(dist > d)
				{
					dist = d;
					curCell = i;
					curIndex = copie.indexOf(i);
				}
			}
			Fcells.add(curCell);
			copie.remove(curIndex);
		}
		
		return Fcells;
	}
	
	public static boolean isBord1(int id)
	{
		int[] bords = {1,30,59,88,117,146,175,204,233,262,291,320,349,378,407,436,465,15,44,73,102,131,160,189,218,247,276,305,334,363,392,421,450,479};
		ArrayList <Integer> test = new ArrayList <Integer>();
		for(int i : bords)
		{
			test.add(i);
		}
		
		if(test.contains(id))
			return true;
		else 
			return false;
	}
	
	public static boolean isBord2(int id)
	{
		int[] bords = {16,45,74,103,132,161,190,219,248,277,306,335,364,393,422,451,29,58,87,116,145,174,203,232,261,290,319,348,377,406,435,464};
		ArrayList <Integer> test = new ArrayList <Integer>();
		for(int i : bords)
		{
			test.add(i);
		}
		
		if(test.contains(id))
			return true;
		else 
			return false;
	}
	
	public static ArrayList<Integer> getLoS (int cell1, int cell2)
	{
		ArrayList<Integer> Los = new ArrayList<Integer>();
		int cell = cell1;
		boolean next = false;
		int[] dir1 = {1,-1,29,-29,15,14,-15,-14};
		
		for(int i : dir1)
		{
			Los.clear();
			cell = cell1;
			Los.add(cell);
			next = false;
			while(!next)
			{
				cell += i;
				Los.add(cell);
				if(isBord2(cell) || isBord1(cell) || cell <= 0 || cell >= 480)
					next=true;
				if(cell == cell2)
				{
					return Los;
				}
			}
		}
		return null;
	}
	
	public static int getCellAfterPushDerobade(Maps map, Case CCase, Case TCase, int value, Fight fight, Fighter target) {
		if (CCase.getId() == TCase.getId())
			return 0;
		char c = getDirBetweenTwoCase(CCase.getId(), TCase.getId(), map, true);
		int id = TCase.getId();
		if (value < 0) {
			c = getOpositeDirection(c);
			value = -value;
		}
		for (int a = 0; a < value; a++) {
			int nextCase = GetCaseIDFromDirection(id, c, map, true);
			
			if (map.getCases().get(nextCase) != null && map.getCases().get(nextCase).isWalkable(true)
					&& map.getCases().get(nextCase).getFighters().isEmpty()) {
				id = nextCase;
				for(Piege p : fight.getTraps()) {
					int dist = Pathfinding.getDistanceBetween(fight.getMap(), p.get_cell().getId(), id);
					if (dist <= p.get_size()) {
						p.onTraped(target);
						return id;
					}
				}
			} else
				return - (value - a);
		}
		if (id == TCase.getId())
			id = 0;
		return id;
	}

    public static String getShortestStringPathBetween(Maps map, int start, int dest, int distMax)
    {
        if (start == dest) return null;
        ArrayList<Case> path = getShortestPathBetween(map, start, dest, distMax);
        if (path == null) return null;
        String pathstr = "";
        int curCaseID = start;
        char curDir = '\000';
        for (Case c : path)
        {
            char d = getDirBetweenTwoCase(curCaseID, c.getId(), map, true);
            if (d == 0) return null;
            if (curDir != d)
            {
                if (path.indexOf(c) != 0)
                    pathstr = pathstr + CryptManager.cellID_To_Code(curCaseID);
                pathstr = pathstr + d;
                curDir = d;
            }
            curCaseID = c.getId();
        }
        if (curCaseID != start)
        {
            pathstr = pathstr + CryptManager.cellID_To_Code(curCaseID);
        }
        if (pathstr == "") return null;
        return "a" + CryptManager.cellID_To_Code(start) + pathstr;
    }
}
