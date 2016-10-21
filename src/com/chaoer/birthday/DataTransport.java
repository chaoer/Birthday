package com.chaoer.birthday;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.os.Environment;
import android.util.Xml;

public class DataTransport {
	private final static String ENCODING = "UTF-8";
	private final static String FILE_FOLDER = "/Birthday/";
	private final static String FILE_NAME = "myBir.xml";

	public DataTransport() {
		// TODO Auto-generated constructor stub
	}

	public static boolean export_bir_data(List<BirthdayData> birdataList) {
		File sdcardDir = Environment.getExternalStorageDirectory();
		// 得到一个路径，内容是sdcard的文件夹路径和名字
		String path = sdcardDir.getPath() + FILE_FOLDER;
		File path1 = new File(path);
		if (!path1.exists()) {
			// 若不存在，创建目录，可以在应用启动的时候创建
			path1.mkdirs();
		}
		File path2 = new File(path + FILE_NAME);
		if(path2.exists())
		{
			// 如果存在一个旧文件，那么删除它
			path2.delete();
		}
		
		File pXMLfile = new File(path + FILE_NAME);
		OutputStream pOutStream;
		try {
			pOutStream = new FileOutputStream(pXMLfile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		XmlSerializer pXMlserial = Xml.newSerializer();
		try {
			pXMlserial.setOutput(pOutStream, ENCODING);

			pXMlserial.startDocument(ENCODING, true);
			pXMlserial.startTag(null, "BIRTHDAY_DATA");
			for (int i = 0; i < birdataList.size(); i++) {
				pXMlserial.startTag(null, "BIRTHDAY");
				// ID
				pXMlserial.startTag(null, "ID");
				pXMlserial.text(birdataList.get(i)._id + "");
				pXMlserial.endTag(null, "ID");
				// NAME
				pXMlserial.startTag(null, "NAME");
				pXMlserial.text(birdataList.get(i).mName + "");
				pXMlserial.endTag(null, "NAME");
				// DATE
				pXMlserial.startTag(null, "DATE");
				pXMlserial.text(birdataList.get(i).mbir_date + "");
				pXMlserial.endTag(null, "DATE");
				// 　TYPE
				pXMlserial.startTag(null, "TYPE");
				pXMlserial.text(birdataList.get(i).mbir_type + "");
				pXMlserial.endTag(null, "TYPE");

				pXMlserial.endTag(null, "BIRTHDAY");
			}
			pXMlserial.endTag(null, "BIRTHDAY_DATA");
			pXMlserial.endDocument();

			pOutStream.flush();
			pOutStream.close();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static List<BirthdayData> import_bir_data() {
		ArrayList<BirthdayData> myBirDataList = null;
		File sdcardDir = Environment.getExternalStorageDirectory();
		// 得到一个路径，内容是sdcard的文件夹路径和名字
		String path = sdcardDir.getPath() + FILE_FOLDER;
		String xmlfilepath = path + FILE_NAME;
		File xmlfile = new File(xmlfilepath);
		if (!xmlfile.exists()) {
			return null;
		}

		FileInputStream inpStream = null;
		try {
			inpStream = new FileInputStream(xmlfilepath);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (@SuppressWarnings("hiding") IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		XmlPullParser parser = Xml.newPullParser();
		try {
			parser.setInput(inpStream, ENCODING);// 设置数据源编码
			int eventType = parser.getEventType();// 获取事件类型
			BirthdayData curBirData = null;
			int tagCnt = 0;
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:// 文档开始事件,可以进行数据初始化处理
					myBirDataList = new ArrayList<BirthdayData>();// 实例化集合类
					break;
				case XmlPullParser.START_TAG:// 开始读取某个标签
					// 通过getName判断读到哪个标签，然后通过nextText()获取文本节点值，或通过getAttributeValue(i)获取属性节点值
					if (parser.getName().equals("BIRTHDAY_DATA")) {
						tagCnt = 0;
					} else if (parser.getName().equals("BIRTHDAY")) {
						tagCnt = 0; // 表示读取一个新的记录
						curBirData = new BirthdayData();
						eventType = parser.next();
					} else if (parser.getName().equals("ID")) {
						String str = parser.nextText();
						int id = Integer.parseInt(str);
						curBirData._id = id;
						tagCnt = 1;
					} else if (parser.getName().equals("NAME")) {
						curBirData.mName = parser.nextText();
						tagCnt = 2;
					} else if (parser.getName().equals("DATE")) {
						curBirData.mbir_date = parser.nextText();
						tagCnt = 3;
					} else if (parser.getName().equals("TYPE")) {
						String str = parser.nextText();
						int type = Integer.parseInt(str);
						curBirData.mbir_type = type;
						tagCnt = 4;
					}
					break;
				case XmlPullParser.END_TAG:// 结束元素事件
					// 读完一个Person，可以将其添加到集合类中
					if (tagCnt == 4) {
						myBirDataList.add(curBirData);
						tagCnt = 0;
					}
					break;
				default:
					break;
				}
				eventType = parser.next();
			}
			inpStream.close();
			return myBirDataList;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
