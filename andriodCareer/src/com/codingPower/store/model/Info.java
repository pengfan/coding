package com.codingPower.store.model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 信息列表类
 * @author pengfan
 *
 */
public class Info
{
	private String name;//姓名
	private boolean gender;//性别
	private String[] hobbies;//爱好
	private Date birthday;//生日
	private int age;//年龄
	private String description;//描述
	
	private static final SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
	public Info()
	{
		
	}
	
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public boolean isGender()
	{
		return gender;
	}
	public void setGender(boolean gender)
	{
		this.gender = gender;
	}
	public String[] getHobbies()
	{
		return hobbies;
	}
	public void setHobbies(String[] hobbies)
	{
		this.hobbies = hobbies;
	}
	public Date getBirthday()
	{
		return birthday;
	}
	public void setBirthday(Date birthday)
	{
		this.birthday = birthday;
	}
	public int getAge()
	{
		return age;
	}
	public void setAge(int age)
	{
		this.age = age;
	}
	public String getDescription()
	{
		return description;
	}
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	public String getBirthdayExpression()
	{
		if(birthday != null)
		{
			return formater.format(birthday);
		}
		return "";
	}
}
