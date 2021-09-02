package it.polimi.tiw.auctions.beans;

public class Product {
	private int id;
	private String code;
	private String name;
	private String desc;
	private String image;
	
	public Product(int id) {
		this.id = id;
	}
	
	public Product(int id, String code, String name, String desc, String image) {
		this.id = id;
		this.code = code;
		this.name = name;
		this.desc = desc;
		this.image = image;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getImage() {
		return image;
	}
	public void setImageurl(String image) {
		this.image = image;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
}
