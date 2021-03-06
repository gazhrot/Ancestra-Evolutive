package org.ancestra.evolutive.common;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.core.Console;
import org.ancestra.evolutive.core.Server;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.entity.creature.collector.Collector;
import org.ancestra.evolutive.enums.Classe;
import org.ancestra.evolutive.fight.fight.Fight;
import org.ancestra.evolutive.fight.Fighter;
import org.ancestra.evolutive.fight.spell.SpellEffect;
import org.ancestra.evolutive.guild.Guild;
import org.ancestra.evolutive.guild.GuildMember;
import org.ancestra.evolutive.map.Maps;
import org.ancestra.evolutive.object.Object;
import org.ancestra.evolutive.object.ObjectPosition;
import org.ancestra.evolutive.util.Couple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class Formulas {

	public static int getRandomValue(int i1,int i2)
	{
		Random rand = new Random();
		return (rand.nextInt((i2-i1)+1))+i1;
	}
	
	public static int getRandomJet(String jet)//1d5+6
	{
		try
		{
			int num = 0;
			int des = Integer.parseInt(jet.split("d")[0]);
			int faces = Integer.parseInt(jet.split("d")[1].split("\\+")[0]);
			int add = Integer.parseInt(jet.split("d")[1].split("\\+")[1]);
			for(int a=0;a<des;a++) {
				num += getRandomValue(1,faces);
			}
			num += add;
			return num;
		}catch(NumberFormatException e){return -1;}
	}
	public static int getMiddleJet(String jet)//1d5+6
	{
		try
		{
			int num = 0;
			int des = Integer.parseInt(jet.split("d")[0]);
			int faces = Integer.parseInt(jet.split("d")[1].split("\\+")[0]);
			int add = Integer.parseInt(jet.split("d")[1].split("\\+")[1]);
			num += ((1+faces)/2)*des;//on calcule moyenne
			num += add;
			return num;
		}catch(NumberFormatException e){return 0;}
	}
	public static int getTacleChance(Fighter tacleur, ArrayList<Fighter> tacle)
	{
		int agiTR = tacleur.getTotalStats().getEffect(Constants.STATS_ADD_AGIL);
		int agiT = 0;
		for(Fighter T : tacle) 
		{
			agiT += T.getTotalStats().getEffect(Constants.STATS_ADD_AGIL);
		}
		int a = agiTR+25;
		int b = agiTR+agiT+50;
		int chance = (int)((long)(300*a/b)-100);
		if(chance <10)chance = 10;
		if(chance >90)chance = 90;
		return chance;
	}

	public static int calculFinalHeal(Player caster,int jet)
	{
		int statC = caster.getTotalStats().getEffect(Constants.STATS_ADD_INTE);
		int soins = caster.getTotalStats().getEffect(Constants.STATS_ADD_SOIN);
		if(statC<0)statC=0;
		return (jet * (100 + statC) / 100 + soins);
	}
	
	public static int calculFinalDommage(Fight fight,Fighter caster,Fighter target,int statID,int jet,boolean isHeal, boolean isCaC, int spellid)
	{
		float i = 0;//Bonus maitrise
		float j = 100; //Bonus de Classe
		float a = 1;//Calcul
		float num = 0;
		float statC = 0, domC = 0, perdomC = 0, resfT = 0, respT = 0;
		int multiplier = 0;
		if(!isHeal) {
			domC = caster.getTotalStats().getEffect(Constants.STATS_ADD_DOMA);
			perdomC = caster.getTotalStats().getEffect(Constants.STATS_ADD_PERDOM);
			multiplier = caster.getTotalStats().getEffect(Constants.STATS_MULTIPLY_DOMMAGE);
		}
        else {
			domC = caster.getTotalStats().getEffect(Constants.STATS_ADD_SOIN);
		}
		
		switch(statID)
		{
			case Constants.ELEMENT_NULL://Fixe
				statC = 0;
				resfT = 0;
				respT = 0;
				respT = 0;
			break;
			case Constants.ELEMENT_NEUTRE://neutre
				statC = caster.getTotalStats().getEffect(Constants.STATS_ADD_FORC);
				resfT = target.getTotalStats().getEffect(Constants.STATS_ADD_R_NEU);
				respT = target.getTotalStats().getEffect(Constants.STATS_ADD_RP_NEU);
                respT += target.getTotalStats().getEffect(Constants.STATS_ADD_RP_PVP_NEU);
                resfT += target.getTotalStats().getEffect(Constants.STATS_ADD_R_PVP_NEU);
				//on ajoute les dom Physique
				domC += caster.getTotalStats().getEffect(142);
				resfT += target.getTotalStats().getEffect(184);
			break;
			case Constants.ELEMENT_TERRE://force
				statC = caster.getTotalStats().getEffect(Constants.STATS_ADD_FORC);
				resfT = target.getTotalStats().getEffect(Constants.STATS_ADD_R_TER);
				respT = target.getTotalStats().getEffect(Constants.STATS_ADD_RP_TER);
                respT += target.getTotalStats().getEffect(Constants.STATS_ADD_RP_PVP_TER);
                resfT += target.getTotalStats().getEffect(Constants.STATS_ADD_R_PVP_TER);
				//on ajout les dom Physique
				domC += caster.getTotalStats().getEffect(142);
				resfT += target.getTotalStats().getEffect(184);
			break;
			case Constants.ELEMENT_EAU://chance
				statC = caster.getTotalStats().getEffect(Constants.STATS_ADD_CHAN);
				resfT = target.getTotalStats().getEffect(Constants.STATS_ADD_R_EAU);
				respT = target.getTotalStats().getEffect(Constants.STATS_ADD_RP_EAU);
                respT += target.getTotalStats().getEffect(Constants.STATS_ADD_RP_PVP_EAU);
                resfT += target.getTotalStats().getEffect(Constants.STATS_ADD_R_PVP_EAU);
				resfT += target.getTotalStats().getEffect(183);
			break;
			case Constants.ELEMENT_FEU://intell
				statC = caster.getTotalStats().getEffect(Constants.STATS_ADD_INTE);
				resfT = target.getTotalStats().getEffect(Constants.STATS_ADD_R_FEU);
				respT = target.getTotalStats().getEffect(Constants.STATS_ADD_RP_FEU);
                respT += target.getTotalStats().getEffect(Constants.STATS_ADD_RP_PVP_FEU);
                resfT += target.getTotalStats().getEffect(Constants.STATS_ADD_R_PVP_FEU);
				resfT += target.getTotalStats().getEffect(183);
			break;
			case Constants.ELEMENT_AIR://agilit�
				statC = caster.getTotalStats().getEffect(Constants.STATS_ADD_AGIL);
				resfT = target.getTotalStats().getEffect(Constants.STATS_ADD_R_AIR);
				respT = target.getTotalStats().getEffect(Constants.STATS_ADD_RP_AIR);
                respT += target.getTotalStats().getEffect(Constants.STATS_ADD_RP_PVP_AIR);
                resfT += target.getTotalStats().getEffect(Constants.STATS_ADD_R_PVP_AIR);
				resfT += target.getTotalStats().getEffect(183);
			break;
		}
		//On bride la resistance a 50% si c'est un joueur 
		if(target.getMob() == null && respT >50)respT = 50;
		
		if(statC<0)statC=0;

        if(caster.getPersonnage() != null && isCaC && caster.getPersonnage().getObjectByPos(ObjectPosition.ARME) != null) {

			int ArmeType = caster.getPersonnage().getObjectByPos(ObjectPosition.ARME).getTemplate().getType().getValue();

            if((caster.getSpellValueBool(392)) && (ArmeType == 2)){//ARC
				i = caster.getMaitriseDmg(392);
			}
			if((caster.getSpellValueBool(390)) && (ArmeType == 4))//BATON
			{
				i = caster.getMaitriseDmg(390);
			}
			if((caster.getSpellValueBool(391)) && (ArmeType == 6))//EPEE
			{
				i = caster.getMaitriseDmg(391);
			}
			if((caster.getSpellValueBool(393)) && (ArmeType == 7))//MARTEAUX
			{
				i = caster.getMaitriseDmg(393);
			}
			if((caster.getSpellValueBool(394)) && (ArmeType == 3))//BAGUETTE
			{
				i = caster.getMaitriseDmg(394);
			}
			if((caster.getSpellValueBool(395)) && (ArmeType == 5))//DAGUES
			{
				i = caster.getMaitriseDmg(395);
			}
			if((caster.getSpellValueBool(396)) && (ArmeType == 8))//PELLE
			{
				i = caster.getMaitriseDmg(396);
			}
			if((caster.getSpellValueBool(397)) && (ArmeType == 19))//HACHE
			{
				i = caster.getMaitriseDmg(397);
			}
            a = (((100+i)/100)*(j/100));
        }
			
        num = a*(jet * ((100 + statC + perdomC + (multiplier*100)) / 100 ))+ domC;//d�gats bruts
			
		//Poisons
		if(spellid != -1)
		{
			switch(spellid)
			{
				/* 
				 * case [SPELLID]: 
				 * statC = caster.getTotalStats().getEffect([EFFECT]) 
				 * num = (jet * ((100 + statC + perdomC + (multiplier*100)) / 100 ))+ domC; 
				 * return (int) num; 
				 */
				case 66 : 
				statC = caster.getTotalStats().getEffect(Constants.STATS_ADD_AGIL);
				num = (jet * ((100 + statC + perdomC + (multiplier*100)) / 100 ))+ domC;
				if(target.hasBuff(105)){
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 105, caster.getId()+"", target.getId()+","+target.getBuff(105).getValue());
					return 0;
				}
				if(target.hasBuff(184)){
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 105, caster.getId()+"", target.getId()+","+target.getBuff(184).getValue());
					return 0;
				}
				return (int) num;
				
				case 71 :
				case 196:
				case 219:
					statC = caster.getTotalStats().getEffect(Constants.STATS_ADD_FORC);
					num = (jet * ((100 + statC + perdomC + (multiplier*100)) / 100 ))+ domC;
					if(target.hasBuff(105))
					{
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 105, caster.getId()+"", target.getId()+","+target.getBuff(105).getValue());
						return 0;
					}
					if(target.hasBuff(184))
					{
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 105, caster.getId()+"", target.getId()+","+target.getBuff(184).getValue());
						return 0;
					}
				return (int) num;
				
				case 181:
				case 200:
					statC = caster.getTotalStats().getEffect(Constants.STATS_ADD_INTE);
					num = (jet * ((100 + statC + perdomC + (multiplier*100)) / 100 ))+ domC;
					if(target.hasBuff(105))
					{
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 105, caster.getId()+"", target.getId()+","+target.getBuff(105).getValue());
						return 0;
					}
					if(target.hasBuff(184))
					{
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 105, caster.getId()+"", target.getId()+","+target.getBuff(184).getValue());
						return 0;
					}
				return (int) num;
			}
		}
		//Renvoie
		int renvoie = target.getTotalStatsLessBuff().getEffect(Constants.STATS_RETDOM);
		if(renvoie >0 && !isHeal)
		{
			if(renvoie > num)renvoie = (int)num;
			num -= renvoie;
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 107, "-1", target.getId()+","+renvoie);
			if(renvoie>caster.getPDV())renvoie = caster.getPDV();
			if(num<1)num =0;
			caster.removePDV(renvoie);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()+"", caster.getId()+",-"+renvoie);
		}
		
		if(!isHeal)num -= resfT;//resis fixe
		int reduc =	(int)((num/100)*respT);//Reduc %resis
		if(!isHeal)num -= reduc;
		
		int armor = getArmorResist(target,statID);
		if(!isHeal)num -= armor;
		if(!isHeal)if(armor > 0)SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 105, caster.getId()+"", target.getId()+","+armor);
		//d�gats finaux
		if(num < 1)num=0;
		
		// D�but Formule pour les MOBs
		if(caster.getPersonnage() == null && !caster.isPerco())
		{
			if(caster.getGfx() == 116 )//Sacrifi� Dommage = PDV*2
			{
				return (int)((num/25)*caster.getPDVMAX());
			}else
			{
			int niveauMob = caster.getLvl();
			double CalculCoef = ((niveauMob*0.5)/100);
			int Multiplicateur = (int) Math.ceil(CalculCoef);
			return (int)num*Multiplicateur;
			}
		}
		// Fin Formule pour les MOBs
		else
		{
			return (int)num;
		}
	}

	public static int calculZaapCost(Maps map1,Maps map2){
		return (10*(Math.abs(map2.getX()-map1.getX())+Math.abs(map2.getY()-map1.getY())-1));
	}

	private static int getArmorResist(Fighter target, int statID)
	{
		int armor = 0;
		for(SpellEffect SE : target.getBuffsByEffectID(265))
		{
			Fighter fighter;
			
			switch(SE.getSpell())
			{
				case 1://Armure incandescente
					//Si pas element feu, on ignore l'armure
					if(statID != Constants.ELEMENT_FEU)continue;
					//Les stats du f�ca sont prises en compte
					fighter = SE.getCaster();
				break;
				case 6://Armure Terrestre
					//Si pas element terre/neutre, on ignore l'armure
					if(statID != Constants.ELEMENT_TERRE && statID != Constants.ELEMENT_NEUTRE)continue;
					//Les stats du f�ca sont prises en compte
					fighter = SE.getCaster();
				break;
				case 14://Armure Venteuse
					//Si pas element air, on ignore l'armure
					if(statID != Constants.ELEMENT_AIR)continue;
					//Les stats du f�ca sont prises en compte
					fighter = SE.getCaster();
				break;
				case 18://Armure aqueuse
					//Si pas element eau, on ignore l'armure
					if(statID != Constants.ELEMENT_EAU)continue;
					//Les stats du f�ca sont prises en compte
					fighter = SE.getCaster();
				break;
				
				default://Dans les autres cas on prend les stats de la cible et on ignore l'element de l'attaque
					fighter = target;
				break;
			}
			int intell = fighter.getTotalStats().getEffect(Constants.STATS_ADD_INTE);
			int carac = 0;
			switch(statID)
			{
				case Constants.ELEMENT_AIR:
					carac = fighter.getTotalStats().getEffect(Constants.STATS_ADD_AGIL);
				break;
				case Constants.ELEMENT_FEU:
					carac = fighter.getTotalStats().getEffect(Constants.STATS_ADD_INTE);
				break;
				case Constants.ELEMENT_EAU:
					carac = fighter.getTotalStats().getEffect(Constants.STATS_ADD_CHAN);
				break;
				case Constants.ELEMENT_NEUTRE:
				case Constants.ELEMENT_TERRE:
					carac = fighter.getTotalStats().getEffect(Constants.STATS_ADD_FORC);
				break;
			}
			int value = SE.getValue();
			int a = value * (100 + (intell/2) + (carac/2))/100;
			armor += a;
		}
		for(SpellEffect SE : target.getBuffsByEffectID(105))
		{
			int intell = target.getTotalStats().getEffect(Constants.STATS_ADD_INTE);
			int carac = 0;
			switch(statID)
			{
				case Constants.ELEMENT_AIR:
					carac = target.getTotalStats().getEffect(Constants.STATS_ADD_AGIL);
				break;
				case Constants.ELEMENT_FEU:
					carac = target.getTotalStats().getEffect(Constants.STATS_ADD_INTE);
				break;
				case Constants.ELEMENT_EAU:
					carac = target.getTotalStats().getEffect(Constants.STATS_ADD_CHAN);
				break;
				case Constants.ELEMENT_NEUTRE:
				case Constants.ELEMENT_TERRE:
					carac = target.getTotalStats().getEffect(Constants.STATS_ADD_FORC);
				break;
			}
			int value = SE.getValue();
			int a = value * (100 + (intell/2) + (carac/2))/100;
			armor += a;
		}
		return armor;
	}

	public static int getPointsLost(char z, int value, Fighter caster,Fighter target)
	{
		float esquiveC = z=='a'?caster.getTotalStats().getEffect(Constants.STATS_ADD_AFLEE):caster.getTotalStats().getEffect(Constants.STATS_ADD_MFLEE);
		float esquiveT = z=='a'?target.getTotalStats().getEffect(Constants.STATS_ADD_AFLEE):target.getTotalStats().getEffect(Constants.STATS_ADD_MFLEE);
		float ptsMax = z=='a'?target.getTotalStatsLessBuff().getEffect(Constants.STATS_ADD_PA):target.getTotalStatsLessBuff().getEffect(Constants.STATS_ADD_PM);
		
		int retrait = 0;

		for(int i = 0; i < value;i++)
		{
			if(ptsMax == 0 && target.getMob() != null)
			{
				ptsMax= z=='a'?target.getDefaultPA():target.getDefautPM();
			}
			
			float pts = z =='a'?target.getDefaultPA():target.getDefautPM();
			float ptsAct = pts - retrait;
			
			if(esquiveT == 0)esquiveT=1;
			if(esquiveC == 0)esquiveC=1;

			float a = (esquiveC/esquiveT);
			float b = (ptsAct/ptsMax);

			float pourcentage = (a*b*50);
			int chance = (int)Math.ceil(pourcentage);
			
			/*
			Console.instance.println("Esquive % : "+a+" Facteur PA/PM : "+b);
			Console.instance.println("ptsMax : "+ptsMax+" ptsAct : "+ptsAct);
			Console.instance.println("Chance d'esquiver le "+(i+1)+" eme PA/PM : "+chance);
			*/
			
			if(chance <0)chance = 0;
			if(chance >100)chance = 100;

			int jet = getRandomValue(0, 99);
			if(jet<chance)
			{
				retrait++;
			}
		}
		return retrait;
	}
	
	public static long getXpWinPerco(Collector perco, Collection<Fighter> winners,Collection<Fighter> loosers,long groupXP)
	{
			Guild G = perco.getGuild();
			float sag = G.getStat(Constants.STATS_ADD_SAGE);
			float coef = (sag + 100)/100;
			int taux = Server.config.getRateXpPvm();
			long xpWin = 0;
			int lvlmax = 0;
			for(Fighter entry : winners)
			{
				if(entry.getLvl() > lvlmax)
					lvlmax = entry.getLvl();
			}
			int nbbonus = 0;
			for(Fighter entry : winners)
			{
				if(entry.getLvl() > (lvlmax / 3))
					nbbonus += 1;				
			}
			
			double bonus = 1;
			if(nbbonus == 2)
				bonus = 1.1;
			if(nbbonus == 3)
				bonus = 1.3;
			if(nbbonus == 4)
				bonus = 2.2;
			if(nbbonus == 5)
				bonus = 2.5;
			if(nbbonus == 6)
				bonus = 2.8;
			if(nbbonus == 7)
				bonus = 3.1;
			if(nbbonus >= 8)
				bonus = 3.5;
			
			int lvlLoosers = 0;
			for(Fighter entry : loosers)
				lvlLoosers += entry.getLvl();
			int lvlWinners = 0;
			for(Fighter entry : winners)
				lvlWinners += entry.getLvl();
			double rapport = 1+((double)lvlLoosers/(double)lvlWinners);
			if (rapport <= 1.3)
				rapport = 1.3;
			/*
			if (rapport > 5)
				rapport = 5;
			//*/
			int lvl = G.getLevel();
			double rapport2 = 1 + ((double)lvl / (double)lvlWinners);

			xpWin = (long) (groupXP * rapport * bonus * taux *coef * rapport2);
			
			/*/ DEBUG XP
			Console.instance.println("=========");
			Console.instance.println("groupXP: "+groupXP);
			Console.instance.println("rapport1: "+rapport);
			Console.instance.println("bonus: "+bonus);
			Console.instance.println("taux: "+taux);
			Console.instance.println("coef: "+coef);
			Console.instance.println("rapport2: "+rapport2);
			Console.instance.println("xpWin: "+xpWin);
			Console.instance.println("=========");
			//*/
			return xpWin;	
	}
	
	public static long getXpWinPvm2(Fighter perso, Collection<Fighter> winners,Collection<Fighter> loosers,long groupXP)
	{
		if(perso.getPersonnage()== null)return 0;
		if(winners.contains(perso))//Si winner
		{
			float sag = perso.getTotalStats().getEffect(Constants.STATS_ADD_SAGE);
			float coef = (sag + 100)/100;
			int taux = Server.config.getRateXpPvm();
			long xpWin = 0;
			int lvlmax = 0;
			for(Fighter entry : winners)
			{
				if(entry.getLvl() > lvlmax)
					lvlmax = entry.getLvl();
			}
			int nbbonus = 0;
			for(Fighter entry : winners)
			{
				if(entry.getLvl() > (lvlmax / 3))
					nbbonus += 1;				
			}
			
			double bonus = 1;
			if(nbbonus == 2)
				bonus = 1.1;
			if(nbbonus == 3)
				bonus = 1.3;
			if(nbbonus == 4)
				bonus = 2.2;
			if(nbbonus == 5)
				bonus = 2.5;
			if(nbbonus == 6)
				bonus = 2.8;
			if(nbbonus == 7)
				bonus = 3.1;
			if(nbbonus >= 8)
				bonus = 3.5;
			
			int lvlLoosers = 0;
			for(Fighter entry : loosers)
				lvlLoosers += entry.getLvl();
			int lvlWinners = 0;
			for(Fighter entry : winners)
				lvlWinners += entry.getLvl();
			double rapport = 1+((double)lvlLoosers/(double)lvlWinners);
			if (rapport <= 1.3)
				rapport = 1.3;
			/*
			if (rapport > 5)
				rapport = 5;
			//*/
			int lvl = perso.getLvl();
			double rapport2 = 1 + ((double)lvl / (double)lvlWinners);

			xpWin = (long) (groupXP * rapport * bonus * taux *coef * rapport2);
			return xpWin;	
		}
		return 0;
	}
	
	public static long getXpWinPvm(Fighter perso, ArrayList<Fighter> team,ArrayList<Fighter> loose, long groupXP)
	{
		int lvllos = 0;
		for(Fighter entry : loose)lvllos += entry.getLvl();
		float bonusSage = (perso.getTotalStats().getEffect(Constants.STATS_ADD_SAGE)+100)/100;
		/* Formule 1
		float taux = perso.getLvl()/lvlwin;
		long xp = (long)(groupXP * taux * bonusSage * perso.getLvl());
		//*/
		//* Formule 2
		long sXp = groupXP*lvllos;
		long gXp = 2 * groupXP * perso.getLvl();
        long xp = (long)((sXp + gXp)*bonusSage);
		//*/
		return xp*Server.config.getRateXpPvm();
	}
	public static long getXpWinPvP(Fighter perso, ArrayList<Fighter> winners, ArrayList<Fighter> looser)
	{
		if(perso.getPersonnage()== null)return 0;
		if(winners.contains(perso.getId()))//Si winner
		{
			int lvlLoosers = 0;
			for(Fighter entry : looser)
				lvlLoosers += entry.getLvl();
		
			int lvlWinners = 0;
			for(Fighter entry : winners)
				lvlWinners += entry.getLvl();
			int taux = Server.config.getRateXpPvp();
			float rapport = (float)lvlLoosers/(float)lvlWinners;
			long xpWin = (long)(
						(
							rapport
						*	getXpNeededAtLevel(perso.getPersonnage().getLevel())
						/	100
						)
						*	taux
					);
			//DEBUG
			Console.instance.println("Taux: "+taux);
			Console.instance.println("Rapport: "+rapport);
			Console.instance.println("XpNeeded: "+getXpNeededAtLevel(perso.getPersonnage().getLevel()));
			Console.instance.println("xpWin: "+xpWin);
			//*/
			return xpWin;
		}
		return 0;
	}
	
	private static long getXpNeededAtLevel(int lvl)
	{
		long xp = (World.data.getPersoXpMax(lvl) - World.data.getPersoXpMin(lvl));
		Console.instance.println("Xp Max => "+World.data.getPersoXpMax(lvl));
		Console.instance.println("Xp Min => "+World.data.getPersoXpMin(lvl));
		
		return xp;
	}

	public static long getGuildXpWin(Fighter perso, AtomicReference<Long> xpWin)
	{
		if(perso.getPersonnage()== null)return 0;
		if(perso.getPersonnage().getGuildMember() == null)return 0;
		

		GuildMember gm = perso.getPersonnage().getGuildMember();
		
		double xp = xpWin.get(), Lvl = perso.getLvl(),LvlGuild = perso.getPersonnage().getGuild().getLevel(),pXpGive = (double)gm.getXpGive()/100;
		
		double maxP = xp * pXpGive * 0.10;	//Le maximum donn� � la guilde est 10% du montant pr�lev� sur l'xp du combat
		double diff = Math.abs(Lvl - LvlGuild);	//Calcul l'�cart entre le niveau du personnage et le niveau de la guilde
		double toGuild;
		if(diff >= 70)
		{
			toGuild = maxP * 0.10;	//Si l'�cart entre les deux level est de 70 ou plus, l'experience donn�e a la guilde est de 10% la valeur maximum de don
		}
		else if(diff >= 31 && diff <= 69)
		{
			toGuild = maxP - ((maxP * 0.10) * (Math.floor((diff+30)/10)));
		}
		else if(diff >= 10 && diff <= 30)
		{
			toGuild = maxP - ((maxP * 0.20) * (Math.floor(diff/10))) ;
		}
		else	//Si la diff�rence est [0,9]
		{
			toGuild = maxP;
		}
		xpWin.set((long)(xp - xp*pXpGive));
		return Math.round(toGuild);
	}
	
	public static long getMountXpWin(Fighter perso, AtomicReference<Long> xpWin)
	{
		if(perso.getPersonnage()== null)return 0;
		if(perso.getPersonnage().getMount() == null)return 0;
		

		int diff = Math.abs(perso.getLvl() - perso.getPersonnage().getMount().getLevel());
		
		double coeff = 0;
		double xp = xpWin.get();
		double pToMount = (double)perso.getPersonnage().getMountXp() / 100 + 0.2;
		
		if(diff >= 0 && diff <= 9)
			coeff = 0.1;
		else if(diff >= 10 && diff <= 19)
			coeff = 0.08;
		else if(diff >= 20 && diff <= 29)
			coeff = 0.06;
		else if(diff >= 30 && diff <= 39)
			coeff = 0.04;
		else if(diff >= 40 && diff <= 49)
			coeff = 0.03;
		else if(diff >= 50 && diff <= 59)
			coeff = 0.02;
		else if(diff >= 60 && diff <= 69)
			coeff = 0.015;
		else
			coeff = 0.01;
		
		if(pToMount > 0.2)
			xpWin.set((long)(xp - (xp*(pToMount-0.2))));
		
		return Math.round(xp * pToMount * coeff);
	}

	public static int getKamasWin(Fighter i, Collection<Fighter> winners, int maxk, int mink){
		maxk++;
		int rkamas = (int)(Math.random() * (maxk-mink)) + mink;
		return rkamas*Server.config.getRateKamas();
	}
	
	public static int getKamasWinPerco(int maxk, int mink)
	{
		maxk++;
		int rkamas = (int)(Math.random() * (maxk-mink)) + mink;
		return rkamas*Server.config.getRateKamas();
	}
	
	public static int calculElementChangeChance(int lvlM,int lvlA,int lvlP)
	{
		int K = 350;
		if(lvlP == 1)K = 100;
		else if (lvlP == 25)K = 175;
		else if (lvlP == 50)K = 350;
		return ((lvlM*100)/(K + lvlA));
	}

	public static int calculHonorWin(Collection<Fighter> winners,Collection<Fighter> loosers,Fighter F)
	{
		float totalGradeWin = 0;
		float totalLevelWin = 0;
		float totalGradeLoose = 0;
		float totalLevelLoose = 0;
		for(Fighter f : winners)
		{
			if(f.getPersonnage() == null )continue;
			totalLevelWin += f.getLvl();
			totalGradeWin += f.getPersonnage().getGrade();

		}
		for(Fighter f : loosers)
		{
			if(f.getPersonnage() == null)continue;
			totalLevelLoose += f.getLvl();
			totalGradeLoose += f.getPersonnage().getGrade();

		}
		
		if(totalLevelWin-totalLevelLoose > Server.config.getAverageLevelPvp()) return 0;

		int base = (int)(100 * (totalGradeLoose/totalGradeWin))/winners.size();
		if(loosers.contains(F))base = -base;
		return base * Server.config.getRateHonor();
	}
	
	public static Couple<Integer, Integer> decompPierreAme(Object toDecomp)
	{
		Couple<Integer, Integer> toReturn;
		String[] stats = toDecomp.parseStatsString().split("#");
		int lvlMax = Integer.parseInt(stats[3],16);
		int chance = Integer.parseInt(stats[1],16);
		toReturn = new Couple<Integer,Integer>(chance,lvlMax);
		
		return toReturn;
	}
	
	public static int totalCaptChance(int pierreChance, Player p)
	{
		int sortChance = 0;

		switch(p.getSortStatBySortIfHas(413).getLevel())
		{
			case 1:
				sortChance = 1;
				break;
			case 2:
				sortChance = 3;
				break;
			case 3:
				sortChance = 6;
				break;
			case 4:
				sortChance = 10;
				break;
			case 5:
				sortChance = 15;
				break;
			case 6:
				sortChance = 25;
				break;
		}
		
		return sortChance + pierreChance;
	}
	
	public static String parseReponse(String reponse)
	{
		StringBuilder toReturn = new StringBuilder("");
		
		String[] cut = reponse.split("[%]");
		
		if(cut.length == 1)return reponse;
		
		toReturn.append(cut[0]);
		
		char charact;
		for (int i = 1; i < cut.length; i++)
		{
			charact = (char) Integer.parseInt(cut[i].substring(0, 2),16);
			toReturn.append(charact).append(cut[i].substring(2));
		}
		
		return toReturn.toString();
	}
	
	public static int spellCost(int nb)
	{
		int total = 0;
		for (int i = 1; i < nb ; i++)
		{
			total += i;
		}
		
		return total;
	}
	
	public static int getTraqueXP(int lvl)
	{
		if(lvl < 50)return 10000 * Server.config.getRateXpPvm();
		if(lvl < 60)return 65000 * Server.config.getRateXpPvm();
		if(lvl < 70)return 90000 * Server.config.getRateXpPvm();
		if(lvl < 80)return 120000 * Server.config.getRateXpPvm();
		if(lvl < 90)return 160000 * Server.config.getRateXpPvm();
		if(lvl < 100)return 210000 * Server.config.getRateXpPvm();
		if(lvl < 110)return 270000 * Server.config.getRateXpPvm();
		if(lvl < 120)return 350000 * Server.config.getRateXpPvm();
		if(lvl < 130)return 440000 * Server.config.getRateXpPvm();
		if(lvl < 140)return 540000 * Server.config.getRateXpPvm();
		if(lvl < 150)return 650000 * Server.config.getRateXpPvm();
		if(lvl < 155)return 760000 * Server.config.getRateXpPvm();
		if(lvl < 160)return 880000 * Server.config.getRateXpPvm();
		if(lvl < 165)return 1000000 * Server.config.getRateXpPvm();
		if(lvl < 170)return 1130000 * Server.config.getRateXpPvm();
		if(lvl < 175)return 1300000 * Server.config.getRateXpPvm();
		if(lvl < 180)return 1500000 * Server.config.getRateXpPvm();
		if(lvl < 185)return 1700000 * Server.config.getRateXpPvm();
		if(lvl < 190)return 2000000 * Server.config.getRateXpPvm();
		if(lvl < 195)return 2500000 * Server.config.getRateXpPvm();
		if(lvl < 200)return 3000000 * Server.config.getRateXpPvm();
		return 0;
	}
	
	public static int getLoosEnergy(int lvl, boolean isAgression, boolean isPerco)
	{
		int returned = 25*lvl;
		if(isAgression) returned *= (7/4);
		if(isPerco) returned *= (3/2);
		return returned;
	}
	
	public static int calculXpWinCraft(int lvl,int numCase)
	{
		if(lvl == 100)
			return 0;
		switch(numCase)
		{
			case 1:
				if(lvl<10)return 1;
			return 0;
			case 2:
				if(lvl<60)return 10;
			return 0;
			case 3:
				if(lvl>9 && lvl<80)return 25;
			return 0;
			case 4:
				if(lvl > 19)return 50;
			return 0;
			case 5:
				if(lvl > 39)return 100;
			return 0;
			case 6:
				if(lvl > 59)return 250;
			return 0;
			case 7:
				if(lvl > 79)return 500;
			return 0;
			case 8:
				if(lvl > 99)return 1000;
			return 0;
		}
		return 0;
	}
	
	public static int calculXpLooseCraft(int lvl,int numCase)
	{
		if(lvl == 100)
			return 0;
		switch(numCase)
		{
			case 1:
				if(lvl<10)return 1;
			return 0;
			case 2:
				if(lvl<60)return 5;
			return 0;
			case 3:
				if(lvl>9 && lvl<80)return 12;
			return 0;
			case 4:
				if(lvl > 19)return 25;
			return 0;
			case 5:
				if(lvl > 39)return 50;
			return 0;
			case 6:
				if(lvl > 59)return 125;
			return 0;
			case 7:
				if(lvl > 79)return 250;
			return 0;
			case 8:
				if(lvl > 99)return 500;
			return 0;
		}
		return 0;
	}
	
	public static int calculChanceByElement(int lvlJob, int lvlObject, int lvlRune)
	{
		int K = 1;
		if (lvlRune == 1)
			K = 100;
		else if (lvlRune == 25)
			K = 175;
		else if (lvlRune == 50)
			K = 350;
		return lvlJob * 100 / (K + lvlObject);
	}
	
	public static int chanceFM(int WeightTotalBase, int currentWeithTotal, int currentWeightStats, int weight, int diff, float coef) 
	{
		float chance = 0.0F;
		float a = ((WeightTotalBase + diff) * coef);
		float b = (float) (Math.sqrt(currentWeithTotal + currentWeightStats) + weight);
		if (b < 1.0)
			b = 1.0F;
		chance = a / b;
		return (int) chance;
	}
	
	public static int calculFinalSoin(Fighter soigneur, int range) {
		int statC = soigneur.getTotalStats().getEffect(Constants.STATS_ADD_INTE);
		int soins = soigneur.getTotalStats().getEffect(Constants.STATS_ADD_SOIN);
		if(statC < 0)
			statC = 0;
		int adic = 200;
		if(soigneur.getPersonnage() != null) 
			if(soigneur.getPersonnage().getClasse() == Classe.ENIRIPSA)
				adic = 100;
		return (range * ((100 + statC) / adic) + soins);
	}
}
