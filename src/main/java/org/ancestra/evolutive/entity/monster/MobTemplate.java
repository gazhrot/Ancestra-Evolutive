package org.ancestra.evolutive.entity.monster;

import org.ancestra.evolutive.other.Drop;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class MobTemplate {
	private int id;
	private int gfx;
	private int align;
	private String colors;
	private int ia = 0;
	private int minKamas;
	private int maxKamas;
	private Map<Integer, MobGrade> grades = new TreeMap<>();
	private ArrayList<Drop> drops = new ArrayList<>();
	private boolean isCapturable;

	public MobTemplate(int id, int gfx, int align, String colors, String grades, String spells, String stats,
			String pdvs, String points, String initiatives, int minKamas, int maxKamas, String xps, int ia, boolean isCapturable) {
		this.id = id;
		this.gfx = gfx;
		this.align = align;
		this.colors = colors;
		this.minKamas = minKamas;
		this.maxKamas = maxKamas;
		this.ia = ia;
		this.isCapturable = isCapturable;
		int grade = 1;
		
		for(int n = 0; n < 11; n++) {
			try	{
				//Grades
				String[] infos = grades.split("\\|")[n].split("@");
				String stat =  stats.split("\\|")[n];
				String spell =  spells.split("\\|")[n];
				String resistance = infos[1];
				
				
				if(spell.equals("-1"))
					spell = "";
				
				int pdvmax = 1, init = 1, level = Integer.parseInt(infos[0]);
				
				try	{
					pdvmax = Integer.parseInt(pdvs.split("\\|")[n]);
					init = Integer.parseInt(initiatives.split("\\|")[n]);
				} catch(Exception e) {}
	
				int pa = 3, pm = 3, xp = 10;
				
				try	{
					String[] pts = points.split("\\|")[n].split(";");
					try	{
						pa = Integer.parseInt(pts[0]);
					} catch(Exception e1) {}
					try	{
						pm = Integer.parseInt(pts[1]);
					} catch(Exception e1) {}
					try {
						xp = Integer.parseInt(xps.split("\\|")[n]);
					} catch(Exception e1) {}
				} catch(Exception e) {
					e.printStackTrace();
				}
				
				this.grades.put(grade, new MobGrade(
							this, grade, level, pa, pm, resistance, stat,	spell, pdvmax, init, xp));
				grade++;
			} catch(Exception e) {
				continue;
			}
		}	
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getGfx() {
		return gfx;
	}

	public int getAlign() {
		return align;
	}

	public String getColors() {
		return colors;
	}

	public int getIa() {
		return ia;
	}

	public int getMinKamas() {
		return minKamas;
	}

	public int getMaxKamas() {
		return maxKamas;
	}

	public Map<Integer, MobGrade> getGrades() {
		return grades;
	}

	public ArrayList<Drop> getDrops() {
		return drops;
	}

	public boolean isCapturable() {
		return isCapturable;
	}

	public MobGrade getGradeByLevel(int level) {
		for(Entry<Integer,MobGrade> grade : this.getGrades().entrySet())
			if(grade.getValue().getLevel() == level) 
				return grade.getValue();
		return null;
	}
	
	public MobGrade getRandomGrade() {
		int randomgrade = (int)(Math.random() * (6 - 1)) + 1, i = 1;
		
		for(Entry<Integer, MobGrade> grade : this.getGrades().entrySet()) {
			if(i == randomgrade)
				return grade.getValue();
			i++;
		}
		return null;
	}
}
