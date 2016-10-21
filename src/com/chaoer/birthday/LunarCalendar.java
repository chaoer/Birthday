package com.chaoer.birthday;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.annotation.SuppressLint;

/**
 * 工具类，实现公农历互转
 */
public class LunarCalendar {
	/**
	 * 计算的基准年份
	 */
	public static final int BASE_YEAR = 1901;
	/**
	 * 支持转换的最小农历年份
	 */
	public static final int MIN_YEAR = 1900;
	/**
	 * 支持转换的最大农历年份
	 */
	public static final int MAX_YEAR = 2099;

	/**
	 * 公历每月前的天数
	 */
	private static final int DAYS_BEFORE_MONTH[] = { 0, 31, 59, 90, 120, 151,
			181, 212, 243, 273, 304, 334, 365 };

	/**
	 * 用来表示1900年到2099年间农历年份的相关信息，共24位bit的16进制表示，其中： 1. 前4位表示该年闰哪个月； 2.
	 * 5-17位表示农历年份13个月的大小月分布，0表示小，1表示大； 3. 最后7位表示农历年首（正月初一）对应的公历日期。
	 * 
	 * 以2014年的数据0x955ABF为例说明： 1001 0101 0101 1010 1011 1111 闰九月 农历正月初一对应公历1月31号
	 */
	private static final int LUNAR_INFO[] = { 0x84B6BF,/* 1900 */
	0x04AE53, 0x0A5748, 0x5526BD, 0x0D2650, 0x0D9544, 0x46AAB9, 0x056A4D,
			0x09AD42, 0x24AEB6, 0x04AE4A,/* 1901-1910 */
			0x6A4DBE, 0x0A4D52, 0x0D2546, 0x5D52BA, 0x0B544E, 0x0D6A43,
			0x296D37, 0x095B4B, 0x749BC1, 0x049754,/* 1911-1920 */
			0x0A4B48, 0x5B25BC, 0x06A550, 0x06D445, 0x4ADAB8, 0x02B64D,
			0x095742, 0x2497B7, 0x04974A, 0x664B3E,/* 1921-1930 */
			0x0D4A51, 0x0EA546, 0x56D4BA, 0x05AD4E, 0x02B644, 0x393738,
			0x092E4B, 0x7C96BF, 0x0C9553, 0x0D4A48,/* 1931-1940 */
			0x6DA53B, 0x0B554F, 0x056A45, 0x4AADB9, 0x025D4D, 0x092D42,
			0x2C95B6, 0x0A954A, 0x7B4ABD, 0x06CA51,/* 1941-1950 */
			0x0B5546, 0x555ABB, 0x04DA4E, 0x0A5B43, 0x352BB8, 0x052B4C,
			0x8A953F, 0x0E9552, 0x06AA48, 0x6AD53C,/* 1951-1960 */
			0x0AB54F, 0x04B645, 0x4A5739, 0x0A574D, 0x052642, 0x3E9335,
			0x0D9549, 0x75AABE, 0x056A51, 0x096D46,/* 1961-1970 */
			0x54AEBB, 0x04AD4F, 0x0A4D43, 0x4D26B7, 0x0D254B, 0x8D52BF,
			0x0B5452, 0x0B6A47, 0x696D3C, 0x095B50,/* 1971-1980 */
			0x049B45, 0x4A4BB9, 0x0A4B4D, 0xAB25C2, 0x06A554, 0x06D449,
			0x6ADA3D, 0x0AB651, 0x095746, 0x5497BB,/* 1981-1990 */
			0x04974F, 0x064B44, 0x36A537, 0x0EA54A, 0x86B2BF, 0x05AC53,
			0x0AB647, 0x5936BC, 0x092E50, 0x0C9645,/* 1991-2000 */
			0x4D4AB8, 0x0D4A4C, 0x0DA541, 0x25AAB6, 0x056A49, 0x7AADBD,
			0x025D52, 0x092D47, 0x5C95BA, 0x0A954E,/* 2001-2010 */
			0x0B4A43, 0x4B5537, 0x0AD54A, 0x955ABF, 0x04BA53, 0x0A5B48,
			0x652BBC, 0x052B50, 0x0A9345, 0x474AB9,/* 2011-2020 */
			0x06AA4C, 0x0AD541, 0x24DAB6, 0x04B64A, 0x6a573D, 0x0A4E51,
			0x0D2646, 0x5E933A, 0x0D534D, 0x05AA43,/* 2021-2030 */
			0x36B537, 0x096D4B, 0xB4AEBF, 0x04AD53, 0x0A4D48, 0x6D25BC,
			0x0D254F, 0x0D5244, 0x5DAA38, 0x0B5A4C,/* 2031-2040 */
			0x056D41, 0x24ADB6, 0x049B4A, 0x7A4BBE, 0x0A4B51, 0x0AA546,
			0x5B52BA, 0x06D24E, 0x0ADA42, 0x355B37,/* 2041-2050 */
			0x09374B, 0x8497C1, 0x049753, 0x064B48, 0x66A53C, 0x0EA54F,
			0x06AA44, 0x4AB638, 0x0AAE4C, 0x092E42,/* 2051-2060 */
			0x3C9735, 0x0C9649, 0x7D4ABD, 0x0D4A51, 0x0DA545, 0x55AABA,
			0x056A4E, 0x0A6D43, 0x452EB7, 0x052D4B,/* 2061-2070 */
			0x8A95BF, 0x0A9553, 0x0B4A47, 0x6B553B, 0x0AD54F, 0x055A45,
			0x4A5D38, 0x0A5B4C, 0x052B42, 0x3A93B6,/* 2071-2080 */
			0x069349, 0x7729BD, 0x06AA51, 0x0AD546, 0x54DABA, 0x04B64E,
			0x0A5743, 0x452738, 0x0D264A, 0x8E933E,/* 2081-2090 */
			0x0D5252, 0x0DAA47, 0x66B53B, 0x056D4F, 0x04AE45, 0x4A4EB9,
			0x0A4D4C, 0x0D1541, 0x2D92B5 /* 2091-2099 */
	};

	/**
	 * 农历的一些东西
	 */
	private final static String MONTH_OF_ALMANAC[] = { "正月", "二月", "三月", "四月",
			"五月", "六月", "七月", "八月", "九月", "十月", "冬月", "腊月" };
	private final static String DAYS_OF_ALMANAC[] = { "初一", "初二", "初三", "初四",
			"初五", "初六", "初七", "初八", "初九", "初十", "十一", "十二", "十三", "十四", "十五",
			"十六", "十七", "十八", "十九", "二十", "廿一", "廿二", "廿三", "廿四", "廿五", "廿六",
			"廿七", "廿八", "廿九", "三十" };

	/**
	 * 天干、地支
	 */
	private final static String[] STEM_NAMES = { "甲", "乙", "丙", "丁", "戊", "己",
			"庚", "辛", "壬", "癸" };
	private final static String[] BRANCH_NAMES = { "子", "丑", "寅", "卯", "辰",
			"巳", "午", "未", "申", "酉", "戌", "亥" };

	/**
	 * 生肖
	 */
	private final static String[] ANIMAL_NAMES = { "鼠", "牛", "虎", "兔", "龙",
			"蛇", "马", "羊", "猴", "鸡", "狗", "猪" };

	/**
	 * 二十四节气表
	 */
	private static final int SPRING_MONTH = 2; // 立春
	private static final char[][] SECTIONAL_TERM_MAP = {
			{ 7, 6, 6, 6, 6, 6, 6, 6, 6, 5, 6, 6, 6, 5, 5, 6, 6, 5, 5, 5, 5, 5,
					5, 5, 5, 4, 5, 5 },
			{ 5, 4, 5, 5, 5, 4, 4, 5, 5, 4, 4, 4, 4, 4, 4, 4, 4, 3, 4, 4, 4, 3,
					3, 4, 4, 3, 3, 3 },
			{ 6, 6, 6, 7, 6, 6, 6, 6, 5, 6, 6, 6, 5, 5, 6, 6, 5, 5, 5, 6, 5, 5,
					5, 5, 4, 5, 5, 5, 5 },
			{ 5, 5, 6, 6, 5, 5, 5, 6, 5, 5, 5, 5, 4, 5, 5, 5, 4, 4, 5, 5, 4, 4,
					4, 5, 4, 4, 4, 4, 5 },
			{ 6, 6, 6, 7, 6, 6, 6, 6, 5, 6, 6, 6, 5, 5, 6, 6, 5, 5, 5, 6, 5, 5,
					5, 5, 4, 5, 5, 5, 5 },
			{ 6, 6, 7, 7, 6, 6, 6, 7, 6, 6, 6, 6, 5, 6, 6, 6, 5, 5, 6, 6, 5, 5,
					5, 6, 5, 5, 5, 5, 4, 5, 5, 5, 5 },
			{ 7, 8, 8, 8, 7, 7, 8, 8, 7, 7, 7, 8, 7, 7, 7, 7, 6, 7, 7, 7, 6, 6,
					7, 7, 6, 6, 6, 7, 7 },
			{ 8, 8, 8, 9, 8, 8, 8, 8, 7, 8, 8, 8, 7, 7, 8, 8, 7, 7, 7, 8, 7, 7,
					7, 7, 6, 7, 7, 7, 6, 6, 7, 7, 7 },
			{ 8, 8, 8, 9, 8, 8, 8, 8, 7, 8, 8, 8, 7, 7, 8, 8, 7, 7, 7, 8, 7, 7,
					7, 7, 6, 7, 7, 7, 7 },
			{ 9, 9, 9, 9, 8, 9, 9, 9, 8, 8, 9, 9, 8, 8, 8, 9, 8, 8, 8, 8, 7, 8,
					8, 8, 7, 7, 8, 8, 8 },
			{ 8, 8, 8, 8, 7, 8, 8, 8, 7, 7, 8, 8, 7, 7, 7, 8, 7, 7, 7, 7, 6, 7,
					7, 7, 6, 6, 7, 7, 7 },
			{ 7, 8, 8, 8, 7, 7, 8, 8, 7, 7, 7, 8, 7, 7, 7, 7, 6, 7, 7, 7, 6, 6,
					7, 7, 6, 6, 6, 7, 7 } };
	private static final char[][] SECTIONAL_TERM_YEAR = {
			{ 13, 49, 85, 117, 149, 185, 201, 250, 250 },
			{ 13, 45, 81, 117, 149, 185, 201, 250, 250 },
			{ 13, 48, 84, 112, 148, 184, 200, 201, 250 },
			{ 13, 45, 76, 108, 140, 172, 200, 201, 250 },
			{ 13, 44, 72, 104, 132, 168, 200, 201, 250 },
			{ 5, 33, 68, 96, 124, 152, 188, 200, 201 },
			{ 29, 57, 85, 120, 148, 176, 200, 201, 250 },
			{ 13, 48, 76, 104, 132, 168, 196, 200, 201 },
			{ 25, 60, 88, 120, 148, 184, 200, 201, 250 },
			{ 16, 44, 76, 108, 144, 172, 200, 201, 250 },
			{ 28, 60, 92, 124, 160, 192, 200, 201, 250 },
			{ 17, 53, 85, 124, 156, 188, 200, 201, 250 } };
	private static final char[][] PRINCIPLE_TERM_MAP = {
			{ 21, 21, 21, 21, 21, 20, 21, 21, 21, 20, 20, 21, 21, 20, 20, 20,
					20, 20, 20, 20, 20, 19, 20, 20, 20, 19, 19, 20 },
			{ 20, 19, 19, 20, 20, 19, 19, 19, 19, 19, 19, 19, 19, 18, 19, 19,
					19, 18, 18, 19, 19, 18, 18, 18, 18, 18, 18, 18 },
			{ 21, 21, 21, 22, 21, 21, 21, 21, 20, 21, 21, 21, 20, 20, 21, 21,
					20, 20, 20, 21, 20, 20, 20, 20, 19, 20, 20, 20, 20 },
			{ 20, 21, 21, 21, 20, 20, 21, 21, 20, 20, 20, 21, 20, 20, 20, 20,
					19, 20, 20, 20, 19, 19, 20, 20, 19, 19, 19, 20, 20 },
			{ 21, 22, 22, 22, 21, 21, 22, 22, 21, 21, 21, 22, 21, 21, 21, 21,
					20, 21, 21, 21, 20, 20, 21, 21, 20, 20, 20, 21, 21 },
			{ 22, 22, 22, 22, 21, 22, 22, 22, 21, 21, 22, 22, 21, 21, 21, 22,
					21, 21, 21, 21, 20, 21, 21, 21, 20, 20, 21, 21, 21 },
			{ 23, 23, 24, 24, 23, 23, 23, 24, 23, 23, 23, 23, 22, 23, 23, 23,
					22, 22, 23, 23, 22, 22, 22, 23, 22, 22, 22, 22, 23 },
			{ 23, 24, 24, 24, 23, 23, 24, 24, 23, 23, 23, 24, 23, 23, 23, 23,
					22, 23, 23, 23, 22, 22, 23, 23, 22, 22, 22, 23, 23 },
			{ 23, 24, 24, 24, 23, 23, 24, 24, 23, 23, 23, 24, 23, 23, 23, 23,
					22, 23, 23, 23, 22, 22, 23, 23, 22, 22, 22, 23, 23 },
			{ 24, 24, 24, 24, 23, 24, 24, 24, 23, 23, 24, 24, 23, 23, 23, 24,
					23, 23, 23, 23, 22, 23, 23, 23, 22, 22, 23, 23, 23 },
			{ 23, 23, 23, 23, 22, 23, 23, 23, 22, 22, 23, 23, 22, 22, 22, 23,
					22, 22, 22, 22, 21, 22, 22, 22, 21, 21, 22, 22, 22 },
			{ 22, 22, 23, 23, 22, 22, 22, 23, 22, 22, 22, 22, 21, 22, 22, 22,
					21, 21, 22, 22, 21, 21, 21, 22, 21, 21, 21, 21, 22 } };
	private static final char[][] PRINCIPLE_TERM_YEAR = {
			{ 13, 45, 81, 113, 149, 185, 201 },
			{ 21, 57, 93, 125, 161, 193, 201 },
			{ 21, 56, 88, 120, 152, 188, 200, 201 },
			{ 21, 49, 81, 116, 144, 176, 200, 201 },
			{ 17, 49, 77, 112, 140, 168, 200, 201 },
			{ 28, 60, 88, 116, 148, 180, 200, 201 },
			{ 25, 53, 84, 112, 144, 172, 200, 201 },
			{ 29, 57, 89, 120, 148, 180, 200, 201 },
			{ 17, 45, 73, 108, 140, 168, 200, 201 },
			{ 28, 60, 92, 124, 160, 192, 200, 201 },
			{ 16, 44, 80, 112, 148, 180, 200, 201 },
			{ 17, 53, 88, 120, 156, 188, 200, 201 } };

	/**
	 * 将农历日期转换为公历日期
	 * 
	 * @param year
	 *            农历年份
	 * @param month
	 *            农历月
	 * @param monthDay
	 *            农历日
	 * @param isLeapMonth
	 *            该月是否是闰月 返回农历日期对应的公历日期，year0, month1, day2.
	 */
	public static final int[] lunarToSolar(int year, int month, int monthDay,
			boolean isLeapMonth) {
		int dayOffset;
		int leapMonth;
		int i;

		if (year < MIN_YEAR || year > MAX_YEAR || month < 1 || month > 12
				|| monthDay < 1 || monthDay > 30) {
			throw new IllegalArgumentException(
					"Illegal lunar date, must be like that:\n\t"
							+ "year : 1900~2099\n\t" + "month : 1~12\n\t"
							+ "day : 1~30");
		}

		dayOffset = (LUNAR_INFO[year - MIN_YEAR] & 0x001F) - 1;

		if (((LUNAR_INFO[year - MIN_YEAR] & 0x0060) >> 5) == 2)
			dayOffset += 31;

		for (i = 1; i < month; i++) {
			if ((LUNAR_INFO[year - MIN_YEAR] & (0x80000 >> (i - 1))) == 0) {
				dayOffset += 29;
			} else {
				dayOffset += 30;
			}
		}

		dayOffset += monthDay;
		leapMonth = (LUNAR_INFO[year - MIN_YEAR] & 0xf00000) >> 20;

		// 这一年有闰月
		if (leapMonth != 0) {
			if (month > leapMonth || (month == leapMonth && isLeapMonth)) {
				if ((LUNAR_INFO[year - MIN_YEAR] & (0x80000 >> (month - 1))) == 0) {
					dayOffset += 29;
				} else {
					dayOffset += 30;
				}
			}
		}

		if (dayOffset > 366 || (year % 4 != 0 && dayOffset > 365)) {
			year += 1;
			if (year % 4 == 1) {
				dayOffset -= 366;
			} else {
				dayOffset -= 365;
			}
		}

		int[] solarInfo = new int[3];
		for (i = 1; i < 13; i++) {
			int iPos = DAYS_BEFORE_MONTH[i];
			if (year % 4 == 0 && i > 2) {
				iPos += 1;
			}

			if (year % 4 == 0 && i == 2 && iPos + 1 == dayOffset) {
				solarInfo[1] = i;
				solarInfo[2] = dayOffset - 31;
				break;
			}

			if (iPos >= dayOffset) {
				solarInfo[1] = i;
				iPos = DAYS_BEFORE_MONTH[i - 1];
				if (year % 4 == 0 && i > 2) {
					iPos += 1;
				}
				if (dayOffset > iPos) {
					solarInfo[2] = dayOffset - iPos;
				} else if (dayOffset == iPos) {
					if (year % 4 == 0 && i == 2) {
						solarInfo[2] = DAYS_BEFORE_MONTH[i]
								- DAYS_BEFORE_MONTH[i - 1] + 1;
					} else {
						solarInfo[2] = DAYS_BEFORE_MONTH[i]
								- DAYS_BEFORE_MONTH[i - 1];
					}
				} else {
					solarInfo[2] = dayOffset;
				}
				break;
			}
		}
		solarInfo[0] = year;

		return solarInfo;
	}

	/**
	 * 将公历日期转换为农历日期，且标识是否是闰月
	 * 
	 * @param year
	 * @param month
	 * @param monthDay
	 * @return 返回公历日期对应的农历日期，year0，month1，day2，leap3
	 */
	public static final int[] solarToLunar(int year, int month, int monthDay) {
		int[] lunarDate = new int[4];
		Date baseDate = new GregorianCalendar(1900, 0, 31).getTime();
		Date objDate = new GregorianCalendar(year, month - 1, monthDay)
				.getTime();
		int offset = (int) ((objDate.getTime() - baseDate.getTime()) / 86400000L);

		// 用offset减去每农历年的天数计算当天是农历第几天
		// iYear最终结果是农历的年份, offset是当年的第几天
		int iYear, daysOfYear = 0;
		for (iYear = MIN_YEAR; iYear <= MAX_YEAR && offset > 0; iYear++) {
			daysOfYear = daysInLunarYear(iYear);
			offset -= daysOfYear;
		}
		if (offset < 0) {
			offset += daysOfYear;
			iYear--;
		}

		// 农历年份
		lunarDate[0] = iYear;

		int leapMonth = leapMonth(iYear); // 闰哪个月,1-12
		boolean isLeap = false;
		// 用当年的天数offset,逐个减去每月（农历）的天数，求出当天是本月的第几天
		int iMonth, daysOfMonth = 0;
		for (iMonth = 1; iMonth <= 13 && offset > 0; iMonth++) {
			daysOfMonth = daysInLunarMonth(iYear, iMonth);
			offset -= daysOfMonth;
		}
		// 当前月超过闰月，要校正
		if (leapMonth != 0 && iMonth > leapMonth) {
			--iMonth;

			if (iMonth == leapMonth) {
				isLeap = true;
			}
		}
		// offset小于0时，也要校正
		if (offset < 0) {
			offset += daysOfMonth;
			--iMonth;
		}

		lunarDate[1] = iMonth;
		lunarDate[2] = offset + 1;
		lunarDate[3] = isLeap ? 1 : 0;

		return lunarDate;
	}

	/**
	 * 传回农历year年month月的总天数
	 * 
	 * @param year
	 *            要计算的年份
	 * @param month
	 *            要计算的月
	 * @return 传回天数
	 */
	final public static int daysInMonth(int year, int month) {
		return daysInMonth(year, month, false);
	}

	/**
	 * 传回农历year年month月的总天数
	 * 
	 * @param year
	 *            要计算的年份
	 * @param month
	 *            要计算的月
	 * @param leap
	 *            当月是否是闰月
	 * @return 传回天数，如果闰月是错误的，返回0.
	 */
	public static final int daysInMonth(int year, int month, boolean leap) {
		int leapMonth = leapMonth(year);
		int offset = 0;

		// 如果本年有闰月且month大于闰月时，需要校正
		if (leapMonth != 0 && month > leapMonth) {
			offset = 1;
		}

		// 不考虑闰月
		if (!leap) {
			return daysInLunarMonth(year, month + offset);
		} else {
			// 传入的闰月是正确的月份
			if (leapMonth != 0 && leapMonth == month) {
				return daysInLunarMonth(year, month + 1);
			}
		}

		return 0;
	}

	/**
	 * 传回农历 year年的总天数
	 *
	 * @param year
	 *            将要计算的年份
	 * @return 返回传入年份的总天数
	 */
	private static int daysInLunarYear(int year) {
		int i, sum = 348;
		if (leapMonth(year) != 0) {
			sum = 377;
		}
		int monthInfo = LUNAR_INFO[year - MIN_YEAR] & 0x0FFF80;
		for (i = 0x80000; i > 0x7; i >>= 1) {
			if ((monthInfo & i) != 0) {
				sum += 1;
			}
		}
		return sum;
	}

	/**
	 * 传回农历 year年month月的总天数，总共有13个月包括闰月
	 *
	 * @param year
	 *            将要计算的年份
	 * @param month
	 *            将要计算的月份
	 * @return 传回农历 year年month月的总天数
	 */
	private static int daysInLunarMonth(int year, int month) {
		if ((LUNAR_INFO[year - MIN_YEAR] & (0x100000 >> month)) == 0)
			return 29;
		else
			return 30;
	}

	/**
	 * 传回农历 year年闰哪个月 1-12 , 没闰传回 0
	 * 
	 * @param year
	 *            将要计算的年份
	 * @return 传回农历 year年闰哪个月1-12, 没闰传回 0
	 */
	private static int leapMonth(int year) {
		return (int) ((LUNAR_INFO[year - MIN_YEAR] & 0xF00000)) >> 20;
	}

	/**
	 * 把阴历的日期转换为公历的日期
	 */
	public static final String dateConvertLunToSol(String lundate) {
		String soldate = "UnKnown";
		Date time = toDate(lundate);
		if (time == null) {
			return soldate;
		}
		String paramDate = dateFormater2.get().format(time);

		// 根据字符串获取时间
		String[] sdateSpilt = paramDate.split("-");
		if (sdateSpilt == null) {
			return soldate;
		}
		int lunyears = Integer.valueOf(sdateSpilt[0]).intValue();
		int lunMonth = Integer.valueOf(sdateSpilt[1]).intValue();
		int lunDays = Integer.valueOf(sdateSpilt[2]).intValue();

		int[] soltime = lunarToSolar(lunyears, lunMonth, lunDays, false);
		soldate = soltime[0] + "-" + soltime[1] + "-" + soltime[2];
		return soldate;
	}

	/**
	 * 把阴历的日期转换为公历的日期
	 */
	public static final String dateConvertSolToLun(String soldate) {
		String lundate = "UnKnown";
		Date time = toDate(soldate);
		if (time == null) {
			return lundate;
		}
		String paramDate = dateFormater2.get().format(time);

		// 根据字符串获取时间
		String[] sdateSpilt = paramDate.split("-");
		if (sdateSpilt == null) {
			return lundate;
		}
		int solyears = Integer.valueOf(sdateSpilt[0]).intValue();
		int solMonth = Integer.valueOf(sdateSpilt[1]).intValue();
		int solDays = Integer.valueOf(sdateSpilt[2]).intValue();

		int[] luntime = solarToLunar(solyears, solMonth, solDays);
		lundate = luntime[0] + "-" + luntime[1] + "-" + luntime[2];
		return lundate;
	}

	/**
	 * 将字符串转位日期类型
	 * 
	 * @param sdate
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	private final static Date toDate(String sdate) {
		try {
			return dateFormater2.get().parse(sdate);
		} catch (ParseException e) {
			return null;
		}
	}

	/*
	 * private final static ThreadLocal<SimpleDateFormat> dateFormater = new
	 * ThreadLocal<SimpleDateFormat>() {
	 * 
	 * @SuppressLint("SimpleDateFormat")
	 * 
	 * @Override protected SimpleDateFormat initialValue() { return new
	 * SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); } };
	 */

	private final static ThreadLocal<SimpleDateFormat> dateFormater2 = new ThreadLocal<SimpleDateFormat>() {
		@SuppressLint("SimpleDateFormat")
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd");
		}
	};

	public final static int[] getCurrentDate(int type) {
		int[] currentDate = null;
		// 获取当前的时间
		Calendar cal = Calendar.getInstance();
		int curYears = cal.get(Calendar.YEAR);
		int curMonth = cal.get(Calendar.MONTH) + 1;
		int curDays = cal.get(Calendar.DAY_OF_MONTH);
		if (type == 0) {
			currentDate = new int[3];
			currentDate[0] = curYears;
			currentDate[1] = curMonth;
			currentDate[2] = curDays;
		} else {
			currentDate = solarToLunar(curYears, curMonth, curDays);
		}
		return currentDate;
	}

	public final static boolean isToday(String sdate, int type) {
		Date time = toDate(sdate);
		if (time == null) {
			return false;
		}
		String paramDate = dateFormater2.get().format(time);

		// 获取当前的时间
		int[] curDate = getCurrentDate(type);
		int curMonth = curDate[1];
		int curDays = curDate[2];

		// 根据字符串获取时间
		String[] sdateSpilt = paramDate.split("-");
		if (sdateSpilt == null) {
			return false;
		}
		int sMonth = Integer.valueOf(sdateSpilt[1]).intValue();
		int sDays = Integer.valueOf(sdateSpilt[2]).intValue();

		// 判断
		if (curMonth == sMonth && curDays == sDays) {
			return true;
		}
		return false;
	}

	public final static boolean isRecent(String sdate, int type) {
		Date time = toDate(sdate);
		if (time == null) {
			return false;
		}
		String paramDate = dateFormater2.get().format(time);

		// 获取当前的时间
		int[] curDate = getCurrentDate(type);
		int curMonth = curDate[1];
		int curDays = curDate[2];

		// 根据字符串获取时间
		String[] sdateSpilt = paramDate.split("-");
		if (sdateSpilt == null) {
			return false;
		}
		int sMonth = Integer.valueOf(sdateSpilt[1]).intValue();
		int sDays = Integer.valueOf(sdateSpilt[2]).intValue();

		// 判断
		if (curMonth == sMonth && (sDays - curDays) >= 0
				&& (sDays - curDays) <= 5) {
			return true;
		}
		return false;
	}

	public final static int recentDays(String sdate, int type) {
		Date time = toDate(sdate);
		if (time == null) {
			return -1;
		}
		String paramDate = dateFormater2.get().format(time);

		// 获取当前的时间
		int[] curDate = getCurrentDate(type);
		int curMonth = curDate[1];
		int curDays = curDate[2];

		// 根据字符串获取时间
		String[] sdateSpilt = paramDate.split("-");
		if (sdateSpilt == null) {
			return -1;
		}
		int sMonth = Integer.valueOf(sdateSpilt[1]).intValue();
		int sDays = Integer.valueOf(sdateSpilt[2]).intValue();

		// 判断
		if (curMonth == sMonth && (sDays - curDays) >= 0
				&& (sDays - curDays) <= 5) {
			return sDays - curDays;
		}
		return -1;
	}

	public final static boolean isPassed(String sdate, int type) {
		Date time = toDate(sdate);
		if (time == null) {
			return false;
		}
		String paramDate = dateFormater2.get().format(time);
		// 根据字符串获取时间
		String[] sdateSpilt = paramDate.split("-");
		if (sdateSpilt == null) {
			return false;
		}
		int sMonth = Integer.valueOf(sdateSpilt[1]).intValue();
		int sDays = Integer.valueOf(sdateSpilt[2]).intValue();

		// 获取当前的时间
		int[] curDate = getCurrentDate(type);
		int curMonth = curDate[1];
		int curDays = curDate[2];

		// 判断
		if (curMonth > sMonth) {
			return true;
		} else if (curMonth == sMonth && curDays >= sDays) {
			return true;
		} else {
			return false;
		}
	}

	public final static String years(String sdate, int type) {
		String ftime = "";
		// 当前年 xxxx-00-00
		int[] curDate = getCurrentDate(type);
		int curYears = curDate[0];

		// 需要比较的的年 xxxx-00-00
		String soldate = sdate;
		String[] sdateSpilt = soldate.split("-");
		if (sdateSpilt == null) {
			return ftime;
		}
		int solyears = Integer.valueOf(sdateSpilt[0]).intValue();

		int years = curYears - solyears;

		// 当年还没有过
		if (!isPassed(sdate, type)) {
			years--;
		} else {
			// years++;
		}
		ftime = years + "岁";

		return ftime;
	}

	public final static String days(String sdate, int type) {
		String daytime = "Unknown";
		Date time = toDate(sdate);
		if (time == null) {
			return daytime;
		}
		String paramDate = dateFormater2.get().format(time);

		// 获取当前的时间
		int[] curDate = getCurrentDate(type);
		int curMonth = curDate[1];
		int curDays = curDate[2];

		// 根据字符串获取时间
		String[] sdateSpilt = paramDate.split("-");
		if (sdateSpilt == null) {
			return daytime;
		}
		int sMonth = Integer.valueOf(sdateSpilt[1]).intValue();
		if (sMonth != curMonth) {
			return daytime;
		}

		int sDays = Integer.valueOf(sdateSpilt[2]).intValue();
		int dayElipse = sDays - curDays;
		if (dayElipse == 1) {
			daytime = "明天";
		} else if (dayElipse == 2) {
			daytime = "后天";
		} else if (dayElipse == 3) {
			daytime = "大后天";
		} else {
			daytime = dayElipse + "天后";
		}
		return daytime;
	}

	/**
	 * 计算阳历日期对应的节气
	 * 
	 * @param y
	 * @param m
	 * @return
	 */
	public static final int sectionalTerm(int y, int m) {
		if (y < 1901 || y > 2100) {
			return 0;
		}
		int index = 0;
		int ry = y - BASE_YEAR + 1;
		while (ry >= SECTIONAL_TERM_YEAR[m - 1][index]) {
			index++;
		}
		int term = SECTIONAL_TERM_MAP[m - 1][4 * index + ry % 4];
		if ((ry == 121) && (m == 4)) {
			term = 5;
		}
		if ((ry == 132) && (m == 4)) {
			term = 5;
		}
		if ((ry == 194) && (m == 6)) {
			term = 6;
		}
		return term;
	}

	public static final int principleTerm(int y, int m) {
		if (y < 1901 || y > 2100) {
			return 0;
		}

		int index = 0;
		int ry = y - BASE_YEAR + 1;
		while (ry >= PRINCIPLE_TERM_YEAR[m - 1][index]) {
			index++;
		}
		int term = PRINCIPLE_TERM_MAP[m - 1][4 * index + ry % 4];
		if ((ry == 171) && (m == 3)) {
			term = 21;
		}
		if ((ry == 181) && (m == 5)) {
			term = 21;
		}
		return term;
	}

	/**
	 * 获得天干
	 * 
	 * @param sdate
	 * @return
	 */
	private final static String getChineseStem(String sdate, int type) {
		// 根据字符串获取时间
		String chineseStem = "Unknown";
		String[] sdateSpilt = sdate.split("-");
		if (sdateSpilt == null) {
			return chineseStem;
		}
		int Years = Integer.valueOf(sdateSpilt[0]).intValue();
		int Month = Integer.valueOf(sdateSpilt[1]).intValue();
		int Days = Integer.valueOf(sdateSpilt[2]).intValue();
		// 防止阴历的错误转换
		if (type == 1 && Days > 30) {
			return chineseStem;
		}

		int lunYears = 0; // 对应的阴历年
		int solMonth = 0, solDays = 0; // 对应的阳历 月，日
		int springDay = 0; // 对应的节气编号, 0 是立春
		if (type == 1) {
			// 把这个阴历需要先转换为阳历
			int[] soltime = lunarToSolar(Years, Month, Days, false);
			springDay = sectionalTerm(soltime[0], SPRING_MONTH);
			lunYears = Years;
			solMonth = soltime[1];
			solDays = soltime[2];
		} else {
			// 是阳历的话就直接计算
			springDay = sectionalTerm(Years, SPRING_MONTH);
			// 　需要得到对应的阴历
			int[] luntime = solarToLunar(Years, Month, Days);
			lunYears = luntime[0];
			solMonth = Month;
			solDays = Days;
		}

		if (solMonth <= SPRING_MONTH && solDays <= springDay) // 还没有过春分，还是上一年
		{
			// chineseStem = STEM_NAMES[(lunYears-BASE_YEAR+6)%10];
			chineseStem = STEM_NAMES[(lunYears - BASE_YEAR + 7) % 10]; // 为了保持一致
		} else // 过了春分, 则是新的一年
		{
			chineseStem = STEM_NAMES[(lunYears - BASE_YEAR + 7) % 10];
		}
		return chineseStem;
	}

	/**
	 * 获得地支
	 * 
	 * @param sdate
	 * @return
	 */
	private final static String getChineseBranch(String sdate, int type) {
		// 根据字符串获取时间
		String chineseBranch = "Unknown";
		String[] sdateSpilt = sdate.split("-");
		if (sdateSpilt == null) {
			return chineseBranch;
		}
		int Years = Integer.valueOf(sdateSpilt[0]).intValue();
		int Month = Integer.valueOf(sdateSpilt[1]).intValue();
		int Days = Integer.valueOf(sdateSpilt[2]).intValue();
		// 防止阴历的错误转换
		if (type == 1 && Days > 30) {
			return chineseBranch;
		}
		int lunYears = 0; // 对应的阴历年
		int solMonth = 0, solDays = 0; // 对应的阳历 月，日
		int springDay = 0; // 对应的节气编号, 1是春分
		if (type == 1) {
			// 把这个阴历需要先转换为阳历
			int[] soltime = lunarToSolar(Years, Month, Days, false);
			springDay = sectionalTerm(soltime[0], SPRING_MONTH);
			lunYears = Years;
			solMonth = soltime[1];
			solDays = soltime[2];
		} else {
			// 是阳历的话就直接计算
			springDay = sectionalTerm(Years, SPRING_MONTH);
			// 　需要得到对应的阴历
			int[] luntime = solarToLunar(Years, Month, Days);
			lunYears = luntime[0];
			solMonth = Month;
			solDays = Days;
		}

		if (solMonth <= SPRING_MONTH && solDays <= springDay) // 还没有过春分，还是上一年
		{
			// chineseBranch = BRANCH_NAMES[(lunYears-BASE_YEAR+0)%12];
			chineseBranch = BRANCH_NAMES[(lunYears - BASE_YEAR + 1) % 12]; // 为了保持一致
		} else // 过了春分, 则是新的一年
		{
			chineseBranch = BRANCH_NAMES[(lunYears - BASE_YEAR + 1) % 12];
		}
		return chineseBranch;
	}

	public final static String getChineseZodiac(String sdate, int type) {
		// 根据字符串获取时间
		String chineseZodiac = "Unknown";
		String[] sdateSpilt = sdate.split("-");
		if (sdateSpilt == null) {
			return chineseZodiac;
		}
		int Years = Integer.valueOf(sdateSpilt[0]).intValue();
		int Month = Integer.valueOf(sdateSpilt[1]).intValue();
		int Days = Integer.valueOf(sdateSpilt[2]).intValue();

		int lunYears = 0; // 对应的阴历年
		if (type == 1) {
			lunYears = Years;
		} else {
			// 　需要得到对应的阴历
			int[] luntime = solarToLunar(Years, Month, Days);
			lunYears = luntime[0];
		}
		chineseZodiac = BRANCH_NAMES[(lunYears - BASE_YEAR + 1) % 12]
				+ ANIMAL_NAMES[(lunYears - BASE_YEAR + 1) % 12];
		return chineseZodiac;
	}

	public final static String getChineseYear(String sdate) {
		// String []sdateSpilt = sdate.split("-");
		// int year = Integer.valueOf(sdateSpilt[0]).intValue();
		// return STEM_NAMES[(year-BASE_YEAR+7)%10] +
		// BRANCH_NAMES[(year-BASE_YEAR+1)%12] + "年";
		return getChineseStem(sdate, 1) + getChineseBranch(sdate, 1) + "年";
	}

	public final static String getChineseMonth(String sdate) {
		String[] sdateSpilt = sdate.split("-");
		int day = Integer.valueOf(sdateSpilt[2]).intValue();
		int month = Integer.valueOf(sdateSpilt[1]).intValue();
		if (day == 31) // 农历没有31
		{
			month++; // 则认为新加1
		}
		return MONTH_OF_ALMANAC[month - 1];
	}

	public final static String getChineseDays(String sdate) {
		String[] sdateSpilt = sdate.split("-");
		int day = Integer.valueOf(sdateSpilt[2]).intValue();
		if (day == 31) // 农历没有31
		{
			day = 1; // 则认为是下一月的1号
		}
		return DAYS_OF_ALMANAC[day - 1];
	}
}
