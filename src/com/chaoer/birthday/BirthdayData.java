package com.chaoer.birthday;

import java.io.Serializable;

import com.chaoer.birthday.LunarCalendar;

public class BirthdayData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int _id = 0;
	public String mName = null; // "***"
	public String mbir_date = null; // "1993-02-03"
	public int mbir_type = 0; // 0: for solar(公历), 1: for lunar(农历)
	
	private final static String DEFAULT_NAME = "小明";
	private final static String DEFAULT_BIRDATE = "1993-02-14";
	private final static int    DEFAULT_BIRTYPE = 0;
	private final static String SOLAR = "公历";
	private final static String LUNAR = "农历";

	public BirthdayData()
	{
		_id = 0;
		mName = DEFAULT_NAME;
		mbir_date = DEFAULT_BIRDATE;
		mbir_type = DEFAULT_BIRTYPE;
	}
	
	public BirthdayData(String name, String birdate, int birType)
	{
		_id = 0;
		mName = name;
		mbir_date = birdate;
		mbir_type = birType;
	}
	
	public String toString()
	{
		// 根据字符串获取时间
		String birdata;	
		if(mbir_type==0)
		{
			birdata = "Name: "+mName
					+"\n BirthdayDate:("+ SOLAR +") " + mbir_date + "\n";
		}
		else
		{
			birdata = "Name: "+mName
					+"\n BirthdayDate:(" + LUNAR +") "
					+ getYear() + "-"
					+ getMonth() +"-"
					+ getDay() + "\n";
		}
			
		return birdata;
	}
	
	public void setName(String name)
	{
		this.mName = name;
	}
	
	public String getName()
	{
		return this.mName;
	}
	
	public void setBirDate(String date)
	{
		this.mbir_date = date;
	}
	
	public String getBirDate()
	{
		return this.mbir_date;
	}
	
	public int[] getIntDate()
	{
		int[] intDate = null;
		String []sdateSpilt = this.mbir_date.split("-");
		if(sdateSpilt==null)
		{
			return LunarCalendar.getCurrentDate(this.mbir_type);
		}
		intDate = new int[3];
		intDate[0] = Integer.valueOf(sdateSpilt[0]).intValue();
		intDate[1] = Integer.valueOf(sdateSpilt[1]).intValue();
		intDate[2] = Integer.valueOf(sdateSpilt[2]).intValue();
		return intDate;
	}
	
	public boolean isRecent()
	{
		return LunarCalendar.isRecent(this.mbir_date, this.mbir_type);
	}
	
	public boolean isToday()
	{
		return LunarCalendar.isToday(this.mbir_date, this.mbir_type);
	}
	
	public String getYear()
	{
		// 根据字符串获取时间
		String []sdateSpilt = this.mbir_date.split("-");
		if(sdateSpilt==null)
		{
			return "Unknown";
		}
		if(this.mbir_type == 1)
		{
			return LunarCalendar.getChineseYear(this.mbir_date);
		}
		else
		{
			return sdateSpilt[0];
		}
	}
	
	public String getMonth()
	{
		// 根据字符串获取时间
		String []sdateSpilt = this.mbir_date.split("-");
		if(sdateSpilt==null)
		{
			return "Unknown";
		}
		if(this.mbir_type == 1)
		{
			return LunarCalendar.getChineseMonth(this.mbir_date);
		}
		else
		{
			return sdateSpilt[1];
		}
	}
	
	public String getDay()
	{
		// 根据字符串获取时间
		String []sdateSpilt = this.mbir_date.split("-");
		if(sdateSpilt==null)
		{
			return "Unknown";
		}
		if(this.mbir_type == 1)
		{
			return LunarCalendar.getChineseDays(this.mbir_date);
		}
		else
		{
			return sdateSpilt[2];
		}
	}

	public void setBirType(int type)
	{
		this.mbir_type = type;
	}
	
	public int getBirType()
	{
		return this.mbir_type;
	}
	
	public String getAge()
	{
		return LunarCalendar.years(this.mbir_date, this.mbir_type) + " (" 
				+ LunarCalendar.getChineseZodiac(this.mbir_date, this.mbir_type) + ")";
	}
	
	public String getInfo()
	{
		if(LunarCalendar.isToday(this.mbir_date, this.mbir_type))
		{
			return "今天是TA的生日";
		}
		else if(LunarCalendar.isRecent(this.mbir_date, this.mbir_type))
		{
			return LunarCalendar.days(this.mbir_date, this.mbir_type);
		}
		else
		{
			String birdata;			
			if(mbir_type==0)
			{
				birdata = this.mbir_date;
			}
			else
			{
				birdata = getYear()+"-"+getMonth()+"-"+getDay();
			}			
			return birdata;
		}
	}
	
	public String getDateinfo()
	{
		String birdata;			
		if(mbir_type==0)
		{
			birdata = "(" + SOLAR + ")" +this.mbir_date;
		}
		else
		{
			birdata = "(" + LUNAR + ")" +this.mbir_date;
		}			
		return birdata;
	}
}
