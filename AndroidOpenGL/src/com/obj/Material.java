package com.obj;

import com.obj.Vertex;



public class Material 
{
	private String textureName;
	private Vertex Ka;
	private Vertex Kd;
	private Vertex Ks;
	private float _shininess;
	private String name;
	public String texName;
	
	public Material(String name)
	{
		Ka = null;
		Kd = null;
		Ks = null;
		textureName = null;
		name = null;
		texName = null;
		_shininess = 0;
		
		this.name = name;
	}

	public void setName( String name ) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public String getTextureName() {
		return textureName;
	}

	public void setTextureName(String texture) {
		this.textureName = texture;
	}

	public Vertex getKa() 
	{
		return Ka;
	}
	
	public Vertex getKd() {
		return Kd;
	}

	public Vertex getKs() {
		return Ks;
	}
	
	public float getShininess()
	{
		return _shininess;
	}

	public void setKa(Vertex ka) {
		Ka = ka;
	}
	
	public void setKd(Vertex kd) {
		Kd = kd;
	}
	
	public void setKs(Vertex ks) {
		Ks = ks;
	}
	
	public void setShininess( float s )
	{
		_shininess = s;
	}
	
	
}
