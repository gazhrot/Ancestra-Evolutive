package org.ancestra.evolutive.entity.npc;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.ConditionParser;
import org.ancestra.evolutive.core.World;

public class NpcQuestion {
	
	private int id;
	private String answer;
	private String argument;
	private String condition;
	private int falseQuestion;
	
	public NpcQuestion(int id, String answer, String argument, String condition, int falseQuestion) {
		this.id = id;
		this.answer = answer;
		this.argument = argument;
		this.condition = condition;
		this.falseQuestion = falseQuestion;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getArgument() {
		return argument;
	}

	public void setArgument(String argument) {
		this.argument = argument;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public int getFalseQuestion() {
		return falseQuestion;
	}

	public void setFalseQuestion(int falseQuestion) {
		this.falseQuestion = falseQuestion;
	}

	public String parseToDQPacket(Player player) {
		if(!ConditionParser.validConditions(player, this.getCondition()))
			return World.data.getNpcQuestion(falseQuestion).parseToDQPacket(player);
		
		String str = this.getId() + "";
		
		if(!this.getArgument().equals(""))
			str += ";" + parseArguments(player);
		
		str += "|" + this.getAnswer();
		
		return str;
	}
	
	private String parseArguments(Player player) {
		String argument = this.getArgument();
		argument = argument.replace("[name]", player.getStringVar("name"));
		argument = argument.replace("[bankCost]", player.getStringVar("bankCost"));
		return argument;
	}
}