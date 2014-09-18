package org.ancestra.evolutive.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import org.ancestra.evolutive.entity.Mount;
import org.ancestra.evolutive.object.Object;

public class PlayerMigration implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int id;
	private Mount mount;
	public ArrayList<Object> objects = new ArrayList<>();
	
	public PlayerMigration(int id, Mount mount, Collection<Object> objects) {
		this.id = id;
		this.mount = mount;
		this.objects.addAll(objects);
	}	
	
	public void desezialize() {
		
	}
}