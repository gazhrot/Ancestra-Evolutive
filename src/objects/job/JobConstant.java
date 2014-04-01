package objects.job;

import java.util.ArrayList;

public class JobConstant {
	
	//Jobs
	public static final int JOB_BASE				= 1;
	public static final int JOB_BUCHERON			= 2;
	public static final int JOB_F_EPEE				= 11;
	public static final int JOB_S_ARC				= 13;
	public static final int JOB_F_MARTEAU			= 14;
	public static final int JOB_CORDONIER			= 15;
	public static final int JOB_BIJOUTIER			= 16;
	public static final int JOB_F_DAGUE				= 17;
	public static final int JOB_S_BATON				= 18;
	public static final int JOB_S_BAGUETTE			= 19;
	public static final int JOB_F_PELLE				= 20;
	public static final int JOB_MINEUR				= 24;
	public static final int JOB_BOULANGER			= 25;
	public static final int JOB_ALCHIMISTE			= 26;
	public static final int JOB_TAILLEUR			= 27;
	public static final int JOB_PAYSAN				= 28;
	public static final int JOB_F_HACHES			= 31;
	public static final int JOB_PECHEUR				= 36;
	public static final int JOB_CHASSEUR			= 41;
	public static final int JOB_FM_DAGUE			= 43;
	public static final int JOB_FM_EPEE				= 44;
	public static final int JOB_FM_MARTEAU			= 45;
	public static final int JOB_FM_PELLE			= 46;
	public static final int JOB_FM_HACHES			= 47;
	public static final int JOB_SM_ARC				= 48;
	public static final int JOB_SM_BAGUETTE			= 49;
	public static final int JOB_SM_BATON			= 50;
	public static final int JOB_BOUCHER				= 56;
	public static final int JOB_POISSONNIER			= 58;
	public static final int JOB_F_BOUCLIER			= 60;
	public static final int JOB_CORDOMAGE			= 62;
	public static final int JOB_JOAILLOMAGE			= 63;
	public static final int JOB_COSTUMAGE			= 64;
	public static final int JOB_BRICOLEUR			= 65;
	public static final int JOB_JOAILLER			= 66;
	public static final int JOB_BIJOUTIER2			= 67;
	//INTERACTIVE OBJET
	public static final int IOBJECT_STATE_FULL		= 1;
	public static final int IOBJECT_STATE_EMPTYING	= 2;
	public static final int IOBJECT_STATE_EMPTY		= 3;
	public static final int IOBJECT_STATE_EMPTY2	= 4;
	public static final int IOBJECT_STATE_FULLING	= 5;
	
	//Action de Métier {skillID,objetRecolté,objSpécial}
	public static final int[][] JOB_ACTION =
	{
		//Bucheron
		{101},{6,303},{39,473},{40,476},{10,460},{141,2357},{139,2358},{37,471},{154,7013},{33,461},{41,474},{34,449},{174,7925},{155,7016},{38,472},{35,470},{158,7014},
		//Mineur
		{48},{32},{24,312},{25,441},{26,442},{28,443},{56,445},{162,7032},{55,444},{29,350},{31,446},{30,313},{161,7033},
		//Pêcheur
		{133},
		//Riviére
		{124,1782},
		{124,1844},
		{124,603},
		{125,1844},
		{125,603},
		{125,1847},
		{125,1794},
		{126,603},
		{126,1847},
		{126,1794},
		{126,1779},
		{127,1847},
		{127,1794},
		{127,1779},
		{127,1801},
		//Mer
		{128,598},
		{128,1757},
		{128,1750},
		{129,1757},
		{129,1805},
		{129,600},
		
		{130,1805},
		{130,1750},
		{130,1784},
		{130,600},
		{131,600},
		{131,1805},
		{131,602},
		{131,1784},
		//OTHER
		{136,2187},
		{140,1759},
		{140,1799},
		//Alchi
		{23},{68,421},{69,428},{71,395},{72,380},{73,593},{74,594},{160,7059},
		//Paysan{122},{47},{45,289,2018},{53,400,2032},{57,533,2036},{46,401,2021},{50,423,2026},{52,532,2029},{159,7018},{58,405},{54,425,2035},
		{122},{47},{45,289},{53,400},{57,533},{46,401},{50,423},{52,532},{159,7018},{58,405},{54,425},
		//Boulanger
		{109},{27},
		//Poissonier
		{135},
		//Boucher
		{134},
		//Chasseur
		{132},
		//Tailleur
		{64},{123},{63},
		//Bijoutier
		{11},{12},
		//Cordonnier
		{13},{14},
		//Forgeur Epée
		{145},{20},
		//Forgeur Marteau
		{144},{19},
		//Forgeur Dague
		{142},{18},
		//Forgeur Pelle
		{146},{21},
		//Forgeur Hache
		{65},{143},
		//Forgemage de Hache
		{115},
		//Forgemage de dagues
		{1},
		//Forgemage de marteau
		{116},
		//Forgemage d'épée
		{113},
		//Forgemage Pelle
		{117},
		//SculpteMage baton
		{120},
		//Sculptemage de baguette
		{119},
		//Sculptemage d'arc
		{118},
		//Costumage
		{165},{166},{167},
		//Cordomage
		{163},{164},
		//Joyaumage
		{169},{168},
		//Bricoleur
		{171},{182},
		//Sculpteur de Arc 
		{15},{149},
		//Sculpteur de Baton
		{17},{147},
		//Sculpteur de Baguette
		{16},{148},
		//Forgeur de bouclier
		{156}
	};
	
	public static final int[][] JOB_PROTECTORS = //{protectorId, itemId}
	{
		{684,289},{684,2018},{685,400},{685,2032},{686,533},{686,1671},{687,401},{687,2021},{688,423},{688,2026},
		{689,532},{689,2029},{690,7018},{691,405},{692,425},{692,2035},{693,39},{694,441},{695,442},{696,443},
		{697,445},{698,444},{699,7032},{700,350},{701,446},{702,313},{703,7033},{704,421},{705,428},{706,395},
		{707,380},{708,593},{709,594},{710,7059},{711,303},{712,473},{713,476},{714,460},{715,2358},{716,2357},
		{717,471},{718,461},{719,7013},{720,7925},{721,474},{722,449},{723,7016},{724,470},{725,7014},{726,1782},
		{726,1790},{727,607},{727,1844},{727,1846},{728,603},{729,598},{730,1757},{730,1759},{731,1750},{732,1847},
		{732,1749},{733,1794},{733,1796},{734,1805},{734,1807},{735,600},{735,1799},{736,1779},{736,1792},{737,1784},
		{737,1788},{738,1801},{738,1803},{739,602},{739,1853}
	};
	
	public static int getTotalCaseByJobLevel(int lvl)
	{
		if(lvl < 10)
			return 2;
		if(lvl == 100)
			return 9;
		return (int)(lvl/20)+3;
	}
	
	public static int getChanceForMaxCase(int lvl)
	{
		if(lvl < 10)
			return 50;
		return  54 + (int)((lvl/10)-1)*5;
	}

	public static boolean isJobAction(int a)
	{
		for(int v = 0;v < JOB_ACTION.length;v++)
		{
			if(JOB_ACTION[v][0] == a)
				return true;
		}
		return false;
	}

	public static int getObjectByJobSkill(int skID)
	{
		for(int v = 0;v < JOB_ACTION.length;v++)
		{
			if(JOB_ACTION[v][0] == skID)
			{
				if(JOB_ACTION[v].length>1)
					return JOB_ACTION[v][1];
			}
		}
		return -1;
	}

	public static int getChanceByNbrCaseByLvl(int lvl, int nbr)
	{
		if(nbr <= getTotalCaseByJobLevel(lvl)-2)return 100;//99.999... normalement, mais osef
		return getChanceForMaxCase(lvl);
	}

	public static boolean isMageJob(int id)
	{
		if((id>42 && id <51) || (id>61 && id <65))
			return true;
		return false;
	}

	public static String actionMetier(int oficio) 
	{
		switch (oficio)
		{
			case 62:
				return "163;164";
			case 63:
				return "169;168";
			case 64:
				return "165;166;167";
			case 45:
				return "116";
			case 46:
				return "117";
			case 67:
				return "115";
			case 43:
				return "1";
			case 44:
				return "113";
			case 48:
				return "118";
			case 49:
				return "119";
			case 50:
				return "120";
		}
		return "";
	}
	
	public static int getStatIDRune(int statID) {
		int multi = 1;
		if (statID == 118 || statID == 126 || statID == 125 || statID == 119 || statID == 123 || statID == 158 || statID == 174)// Fo
		{
			multi = 1;
		} else if (statID == 138 || statID == 666 || statID == 226 || statID == 220)// Do %
		{
			multi = 2;
		} else if (statID == 124 || statID == 176)// Sage, prosp
		{
			multi = 3;
		} else if (statID == 240 || statID == 241 || statID == 242 || statID == 243 || statID == 244)// Resist
		{
			multi = 4;
		} else if (statID == 210 || statID == 211 || statID == 212 || statID == 213 || statID == 214)// Resist
		{
			multi = 5;
		} else if (statID == 225)// Piege
		{
			multi = 6;
		} else if (statID == 178 || statID == 112)// Heal,Do
		{
			multi = 7;
		} else if (statID == 115 || statID == 182)// CC
		{
			multi = 8;
		} else if (statID == 117)// PO
		{
			multi = 9;
		} else if (statID == 128)// PM
		{
			multi = 10;
		} else if (statID == 111)// PA
		{
			multi = 10;
		}
		return multi;
	}
	
	public static int getProtectorLvl(int lvl) 
	{
		if(lvl < 40)
			return 10;
		if(lvl < 80) 
			return 20;
		if(lvl < 120) 
			return 30;
		if(lvl < 160) 
			return 40;
		if(lvl < 200) 
			return 50;
		return 50;
	}
	
	public static ArrayList<JobAction> getPosActionsToJob(int tID, int lvl)
	{
		ArrayList<JobAction> list = new ArrayList<JobAction>();
		int timeWin = lvl*100;
		int dropWin = lvl / 5;
		switch(tID)
		{
			case JOB_BIJOUTIER:
				//Faire Anneau 
				list.add(new JobAction(11,getTotalCaseByJobLevel(lvl),0,true,getChanceForMaxCase(lvl),-1));
				//Faire Amullette
				list.add(new JobAction(12,getTotalCaseByJobLevel(lvl),0,true,getChanceForMaxCase(lvl),-1));
			break;
			case JOB_TAILLEUR:
				//Faire Sac
				list.add(new JobAction(64,getTotalCaseByJobLevel(lvl),0,true,getChanceForMaxCase(lvl),-1));
				//Faire Cape
				list.add(new JobAction(123,getTotalCaseByJobLevel(lvl),0,true,getChanceForMaxCase(lvl),-1));
				//Faire Chapeau
				list.add(new JobAction(63,getTotalCaseByJobLevel(lvl),0,true,getChanceForMaxCase(lvl),-1));
			break;
			case JOB_F_BOUCLIER:
				//Forger Bouclier
				list.add(new JobAction(156,getTotalCaseByJobLevel(lvl),0,true,getChanceForMaxCase(lvl),-1));
			break;
			case JOB_BRICOLEUR:
				//Faire clef
				list.add(new JobAction(171,getTotalCaseByJobLevel(lvl),0,true,getChanceForMaxCase(lvl),-1));
				//Faire objet brico
				list.add(new JobAction(182,getTotalCaseByJobLevel(lvl),0,true,getChanceForMaxCase(lvl),-1));
			break;
			case JOB_CORDONIER:
				//Faire botte
				list.add(new JobAction(13,getTotalCaseByJobLevel(lvl),0,true,getChanceForMaxCase(lvl),-1));
				//Faire ceinture
				list.add(new JobAction(14,getTotalCaseByJobLevel(lvl),0,true,getChanceForMaxCase(lvl),-1));
			break;
			case JOB_S_ARC:
				//Sculter Arc
				list.add(new JobAction(15,getTotalCaseByJobLevel(lvl),0,true,getChanceForMaxCase(lvl),-1));
				//ReSculter Arc
				list.add(new JobAction(149,3,0,true,getChanceForMaxCase(lvl),-1));
			break;
			case JOB_S_BATON:
				//Sculter Baton
				list.add(new JobAction(17,getTotalCaseByJobLevel(lvl),0,true,getChanceForMaxCase(lvl),-1));
				//ReSculter Baton
				list.add(new JobAction(147,3,0,true,getChanceForMaxCase(lvl),-1));
			break;
			case JOB_S_BAGUETTE:
				//Sculter Baguette
				list.add(new JobAction(16,getTotalCaseByJobLevel(lvl),0,true,getChanceForMaxCase(lvl),-1));
				//ReSculter Baguette
				list.add(new JobAction(148,3,0,true,getChanceForMaxCase(lvl),-1));
			break;
			case JOB_CORDOMAGE:
				//FM Bottes
				list.add(new JobAction(163,3,0,true,lvl,0));
				//FM Ceinture
				list.add(new JobAction(164,3,0,true,lvl,0));
			break;
			case JOB_JOAILLOMAGE:
				//FM Anneau
				list.add(new JobAction(169,3,0,true,lvl,0));
				//FM  Amullette
				list.add(new JobAction(168,3,0,true,lvl,0));
			break;
			case JOB_COSTUMAGE:
				//FM Chapeau
				list.add(new JobAction(165,3,0,true,lvl,0));
				//FM Cape
				list.add(new JobAction(167,3,0,true,lvl,0));
				//FM Sac
				list.add(new JobAction(166,3,0,true,lvl,0));
			break;
			case JOB_F_EPEE:
				//Forger Epée
				list.add(new JobAction(20,getTotalCaseByJobLevel(lvl),0,true,getChanceForMaxCase(lvl),-1));
				//Reforger Epée
				list.add(new JobAction(145,3,0,true,getChanceForMaxCase(lvl),-1));
			break;
			case JOB_F_DAGUE:
				//Forger Dague
				list.add(new JobAction(142,3,0,true,getChanceForMaxCase(lvl),-1));
				//Reforger Dague
				list.add(new JobAction(18,getTotalCaseByJobLevel(lvl),0,true,getChanceForMaxCase(lvl),-1));
			break;
			case JOB_F_MARTEAU:
				//Forger Marteau
				list.add(new JobAction(19,getTotalCaseByJobLevel(lvl),0,true,getChanceForMaxCase(lvl),-1));
				//Reforger Marteau
				list.add(new JobAction(144,3,0,true,getChanceForMaxCase(lvl),-1));
			break;
			case JOB_F_PELLE:
				//Forger Pelle
				list.add(new JobAction(21,getTotalCaseByJobLevel(lvl),0,true,getChanceForMaxCase(lvl),-1));
				//Reforger Pelle
				list.add(new JobAction(146,3,0,true,getChanceForMaxCase(lvl),-1));
			break;
			case JOB_F_HACHES:
				//Forger Hache 
				list.add(new JobAction(65,getTotalCaseByJobLevel(lvl),0,true,getChanceForMaxCase(lvl),-1));
				//Reforger Hache
				list.add(new JobAction(143,3,0,true,getChanceForMaxCase(lvl),-1));
			break;
			case JOB_FM_HACHES:
				//Reforger une hache
				list.add(new JobAction(115,3,0,true,lvl,0));
			break;
			case JOB_FM_DAGUE:
				//Reforger une dague
				list.add(new JobAction(1,3,0,true,lvl,0));
			break;
			case JOB_FM_EPEE:
				//Reforger une épée
				list.add(new JobAction(113,3,0,true,lvl,0));
			break;
			case JOB_FM_MARTEAU:
				//Reforger une marteau
				list.add(new JobAction(116,3,0,true,lvl,0));
			break;
			case JOB_FM_PELLE:
				//Reforger une pelle
				list.add(new JobAction(117,3,0,true,lvl,0));
			break;
			case JOB_SM_ARC:
				//Resculpter un arc
				list.add(new JobAction(118,3,0,true,lvl,0));
			break;
			case JOB_SM_BATON:
				//Resculpter un baton
				list.add(new JobAction(120,3,0,true,lvl,0));
			break;
			case JOB_SM_BAGUETTE:
				//Resculpter une baguette
				list.add(new JobAction(119,3,0,true,lvl,0));
			break;
			case JOB_CHASSEUR:
				//Préparer
				list.add(new JobAction(132,getTotalCaseByJobLevel(lvl),0,true,getChanceForMaxCase(lvl),-1));
			break;
			case JOB_BOUCHER:
				//Préparer une Viande
				list.add(new JobAction(134,getTotalCaseByJobLevel(lvl),0,true,getChanceForMaxCase(lvl),-1));
			break;			
			case JOB_POISSONNIER:
				//Preparer un Poisson
				list.add(new JobAction(135,getTotalCaseByJobLevel(lvl),0,true,getChanceForMaxCase(lvl),-1));
			break;			
			case JOB_BOULANGER:
				//Cuir le Pain
				list.add(new JobAction(27,getTotalCaseByJobLevel(lvl),0,true,getChanceForMaxCase(lvl),-1));
				//Faire des Bonbons
				list.add(new JobAction(109,3,0,true,100,-1));
			break;
			case JOB_MINEUR:
				if(lvl > 99)
				{
				//Miner Dolomite
				list.add(new JobAction(161,-19 + dropWin,-18 + dropWin,false,12000-timeWin,60));
				}
				if(lvl > 79)
				{
				//Miner Or
				list.add(new JobAction(30,-15 + dropWin,-14 + dropWin,false,12000-timeWin,55));
				}
				if(lvl > 69)
				{
				//Miner Bauxite
				list.add(new JobAction(31,-13 + dropWin,-12 + dropWin,false,12000-timeWin,50));
				}
				if(lvl > 59)
				{
				//Miner Argent
				list.add(new JobAction(29,-11 + dropWin,-10 + dropWin,false,12000-timeWin,40));
				}
				if(lvl > 49)
				{
				//Miner Etain
				list.add(new JobAction(55,-9 + dropWin,-8 + dropWin,false,12000-timeWin,35));
				//Miner Silicate
				list.add(new JobAction(162,-9 + dropWin,-8 + dropWin,false,12000-timeWin,35));
				}
				if(lvl > 39)
				{
				//Miner Manganèse
				list.add(new JobAction(56,-7 + dropWin,-6 + dropWin,false,12000-timeWin,30));
				}
				if(lvl >29)
				{
				//Miner Kobalte
				list.add(new JobAction(28,-5 + dropWin,-4 + dropWin,false,12000-timeWin,25));
				}
				if(lvl >19)
				{
				//Miner Bronze
				list.add(new JobAction(26,-3 + dropWin,-2 + dropWin,false,12000-timeWin,20));
				}
				if(lvl >9)
				{
				//Miner Cuivre
				list.add(new JobAction(25,-1 + dropWin,0 + dropWin,false,12000-timeWin,15));
				}
				//Miner Fer
				list.add(new JobAction(24,1 + dropWin,2 + dropWin,false,12000-timeWin,10));
				//Fondre
				list.add(new JobAction(32,getTotalCaseByJobLevel(lvl),0,true,getChanceForMaxCase(lvl),-1));
				//Polir
				list.add(new JobAction(48,getTotalCaseByJobLevel(lvl),0,true,getChanceForMaxCase(lvl),-1));
			break;			
			case JOB_PECHEUR:
				if(lvl > 74)
				{
				//Pêcher Poissons géants de mer
				list.add(new JobAction(131,0,1,false,12000-timeWin,35));
				}
				if(lvl > 69)
				{
				//Pêcher Poissons géants de rivière
				list.add(new JobAction(127,0,1,false,12000-timeWin,35));
				}
				if(lvl > 49)
				{
				//Pêcher Gros poissons de mers
				list.add(new JobAction(130,0,1,false,12000-timeWin,30));
				}
				if(lvl >39)
				{
				//Pêcher Gros poissons de rivière
				list.add(new JobAction(126,0,1,false,12000-timeWin,25));
				}
				if(lvl >19)
				{
				//Pêcher Poissons de mer
				list.add(new JobAction(129,0,1,false,12000-timeWin,20));
				}
				if(lvl >9)
				{
				//Pêcher Poissons de rivière
				list.add(new JobAction(125,0,1,false,12000-timeWin,15));
				}
				//Pêcher Ombre Etrange
				list.add(new JobAction(140,0,1,false,12000-timeWin,50));
				//Pêcher Pichon
				list.add(new JobAction(136,1,1,false,12000-timeWin,5));
				//Pêcher Petits poissons de rivière
				list.add(new JobAction(124,0,1,false,12000-timeWin,10));
				//Pêcher Petits poissons de mer
				list.add(new JobAction(128,0,1,false,12000-timeWin,10));
				//Vider
				list.add(new JobAction(133,getTotalCaseByJobLevel(lvl),0,true,getChanceForMaxCase(lvl),-1));
				break;			
				case JOB_ALCHIMISTE:
				if(lvl > 49)
				{
				//Cueillir Graine de Pandouille
				list.add(new JobAction(160,-9 + dropWin,-8 + dropWin,false,12000-timeWin,35));
				//Cueillir Edelweiss
				list.add(new JobAction(74,-9 + dropWin,-8 + dropWin,false,12000-timeWin,35));
				}
				if(lvl > 39)
				{
				//Cueillir Orchidée
				list.add(new JobAction(73,-7 + dropWin,-6 + dropWin,false,12000-timeWin,30));
				}
				if(lvl >29)
				{
				//Cueillir Menthe
				list.add(new JobAction(72,-5 + dropWin,-4 + dropWin,false,12000-timeWin,25));
				}
				if(lvl >19)
				{
				//Cueillir Trèfle
				list.add(new JobAction(71,-3 + dropWin,-2 + dropWin,false,12000-timeWin,20));
				}
				if(lvl >9)
				{
				//Cueillir Chanvre
				list.add(new JobAction(69,-1 + dropWin,0 + dropWin,false,12000-timeWin,15));
				}
				//Cueillir Lin
				list.add(new JobAction(68,1 + dropWin,2 + dropWin,false,12000-timeWin,10));
				//Fabriquer une Potion
				list.add(new JobAction(23,getTotalCaseByJobLevel(lvl),0,true,getChanceForMaxCase(lvl),-1));
			break;			
			case JOB_BUCHERON:
				if(lvl > 99)
				{
				//Couper Bambou Sacré
				list.add(new JobAction(158,-19 + dropWin,-18 + dropWin,false,12000-timeWin,75));
				}
				if(lvl > 89)
				{
				//Couper Orme
				list.add(new JobAction(35,-17 + dropWin,-16 + dropWin,false,12000-timeWin,70));
				}
				if(lvl > 79)
				{
				//Couper Charme
				list.add(new JobAction(38,-15 + dropWin,-14 + dropWin,false,12000-timeWin,65));
				//Couper Bambou Sombre
				list.add(new JobAction(155,-15 + dropWin,-14 + dropWin,false,12000-timeWin,65));
				}
				if(lvl > 74)
				{
				//Couper Kalyptus
				list.add(new JobAction(174,-14 + dropWin,-13 + dropWin,false,12000-timeWin,55));
				}
				if(lvl > 69)
				{
				//Couper Ebène
				list.add(new JobAction(34,-13 + dropWin,-12 + dropWin,false,12000-timeWin,50));
				}
				if(lvl > 59)
				{
				//Couper Merisier
				list.add(new JobAction(41,-11 + dropWin,-10 + dropWin,false,12000-timeWin,45));
				}
				if(lvl > 49)
				{
				//Couper If
				list.add(new JobAction(33,-9 + dropWin,-8 + dropWin,false,12000-timeWin,40));
				//Couper Bambou
				list.add(new JobAction(154,-9 + dropWin,-8 + dropWin,false,12000-timeWin,40));
				}
				if(lvl > 39)
				{
				//Couper Erable
				list.add(new JobAction(37,-7 + dropWin,-6 + dropWin,false,12000-timeWin,35));
				}
				if(lvl> 34)
				{
				//Couper Bombu
				list.add(new JobAction(139,-6 + dropWin,-5 + dropWin,false,12000-timeWin,30));
				//Couper Oliviolet
				list.add(new JobAction(141,-6 + dropWin,-5 + dropWin,false,12000-timeWin,30));
				}
				if(lvl >29)
				{
				//Couper Chêne
				list.add(new JobAction(10,-5 + dropWin,-4 + dropWin,false,12000-timeWin,25));
				}
				if(lvl >19)
				{
				//Couper Noyer
				list.add(new JobAction(40,-3 + dropWin,-2 + dropWin,false,12000-timeWin,20));
				}
				if(lvl >9)
				{
				//Couper Châtaignier
				list.add(new JobAction(39,-1 + dropWin,0 + dropWin,false,12000-timeWin,15));
				}
				//Couper Frêne
				list.add(new JobAction(6,1 + dropWin,2 + dropWin,false,12000-timeWin,10));
				//Scie
				list.add(new JobAction(101,getTotalCaseByJobLevel(lvl),0,true,getChanceForMaxCase(lvl),-1));
			break;
			
			case JOB_PAYSAN:
				if(lvl > 69)
				{
				//Faucher Chanvre
				list.add(new JobAction(54,-13 + dropWin,-12 + dropWin,false,12000-timeWin,45));
				}
				if(lvl > 59)
				{
				//Faucher Malt
				list.add(new JobAction(58,-11 + dropWin,-10 + dropWin,false,12000-timeWin,40));
				}
				if(lvl > 49)
				{
				//Faucher Riz
				list.add(new JobAction(159,-9 + dropWin,-8 + dropWin,false,12000-timeWin,35));
				//Faucher Seigle
				list.add(new JobAction(52,-9 + dropWin,-8 + dropWin,false,12000-timeWin,35));
				}
				if(lvl> 39)
				{
				//Faucher Lin
				list.add(new JobAction(50,-7 + dropWin,-6 + dropWin,false,12000-timeWin,30));
				}
				if(lvl >29)
				{
				//Faucher Houblon
				list.add(new JobAction(46,-5 + dropWin,-4 + dropWin,false,12000-timeWin,25));
				}
				if(lvl >19)
				{
				//Faucher Avoine
				list.add(new JobAction(57,-3 + dropWin,-2 + dropWin,false,12000-timeWin,20));
				}
				if(lvl >9)
				{
				//Faucher Orge
				list.add(new JobAction(53,-1 + dropWin,0 + dropWin,false,12000-timeWin,15));
				}
				//Faucher blé
				list.add(new JobAction(45,1 + dropWin,2 + dropWin,false,12000-timeWin,10));
				//Moudre
				list.add(new JobAction(47,getTotalCaseByJobLevel(lvl),0,true,getChanceForMaxCase(lvl),-1));
				//Egrener 100% 1 case tout le temps ?
				list.add(new JobAction(122,1,0,true,100,10));
			break;
		}
		return list;
	}
	
	public static int statRune(int stat, int cant) 
	{
		switch (stat)
		{
			case 111:// Pa
				return 1557;// runa PA
			case 112: // do
				return 7435;// runa do
			case 115:
				return 7433;
			case 117:// porte
				return 7438;// runa portee
			case 118:// fo
				if (cant > 70)
					return 1551;
				else if (cant <= 70 && cant > 20)
					return 1545;
				else
					return 1519;// rune fo 1
			case 119: // Agi
				if (cant > 70)
					return 1555;
				else if (cant <= 70 && cant > 20)
					return 1549;
				else
					return 1524;// rune fo 1
			case 123:// chan
				if (cant > 70)
					return 1556;
				else if (cant <= 70 && cant > 20)
					return 1550;
				else
					return 1525;// rune fo 1
			case 124:// Sage
				if (cant > 30)
					return 1552;
				else if (cant <= 30 && cant > 10)
					return 1546;
				else
					return 1521;// runa fo 1
			case 125:// vita
				if (cant > 230)
					return 1554;
				else if (cant <= 230 && cant > 60)
					return 1548;
				else
					return 1523;// runa fo 1
			case 126: // intel
				if (cant > 70)
					return 1553;
				else if (cant <= 70 && cant > 20)
					return 1547;
				else
					return 1522;// runa fo 1
			case 128:// PM
				return 1558;// runa PM 1
			case 138:// Do %
				return 7436;// runa porcDo
			case 158:// pods
				if (cant > 300)
					return 7445;
				else if (cant <= 300 && cant > 100)
					return 7444;
				else
					return 7443;
			case 174:// init
				if (cant > 300)
					return 7450;
				else if (cant <= 300 && cant > 100)
					return 7449;
				else
					return 7448;// runa fo
			case 176:// prospec
				if (cant > 5)
					return 10662;
				else
					return 7451;// runa prospec
			case 178://
				return 7434;// runa soin
			case 182:// invoca
				return 7442;// runa invo
			case 220:
				return 7437;// runa renvoie
			case 225:
				return 7446;// runa do piege
			case 226:
				return 7447;// runa do porc piege
			case 240:
				return 7452;// runa re feu
			case 241:
				return 7453;// runa re air
			case 242:
				return 7454;// runa re cha
			case 243:
				return 7455;// runa re fo
			case 244:
				return 7456;// runa re neu
			case 210:
				return 7457;// runa re %feu
			case 211:
				return 7458;// runa re %air
			case 212:
				return 7560;// runa re %cha
			case 213:
				return 7459;// runa re %fo
			case 214:
				return 7460;// runa re %neu
			default:
				return 0;
		}
	}
}