package com.payne.school.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.regex.Matcher;

import static java.util.regex.Pattern.compile;

public class TestQuestionInfo {
	/**
	 * 
	 * 用OutputStreamWrite向文件写入内容
	 * 
	 * 
	 * 
	 * @param filePath
	 *
	 * */


	public static void readTxtFile(String filePath) {
		String str="";
		File file= new File(filePath);
		try {
			FileInputStream in=new FileInputStream(file);
			// size  为字串的长度 ，这里一次性读完
			int size=in.available();
			byte[] buffer=new byte[size];
			in.read(buffer);
			in.close();
			str	= new String(buffer,"UTF-8");
//			str	= new String(buffer,"UTF-8").replace('　', ' ');
			System.out.println(str+"\n==================================================");
			Elements elements = new Elements();
			Document document = Jsoup.parse(str);
			String text = document.body().text().trim();

			/*elements = document.select("td[width=100%]").select("td[bgcolor=#E6E6DF]").select("td[height=20]").
					select("td:not(td[width=25%])").select("td:not(td[width=40%])").select("td:not(td[width=20%])");*/
			elements = document.select("table[width=80%]")
					.select("table:not(table[width=100%])").select("table:not(table[width=750])");

			for(Element element : elements){
				System.out.println(element.text().trim());
			}


//			Matcher testDetailMatcher = compile("<input" + "[^<>]*?\\s" + "name=['\"]?(.*?)['\"]?(\\s.*?)?>").matcher(testDetailHtml);
 			String reg = "<input" + "[^<>]*?\\s" + "name=['\"]?(.*?)['\"]?(\\s.*?)?>";
			// 			String reg = "<input" + "[^<>]*?\\\\s" + "name=['\\"]?(.*?)['\\"]?(\\\\s.*?)?>";

//<input type="radio" value="A" name="624201011005" />A、生物医学模式</td>
//			String reg = "]*?\\s" + "name=['\"]?(.*?)['\"]?(\\s.*?)?>";
/*<td width="100%" bgcolor="#E6E6DF" height="20"> 1、南丁格尔出生于哪一年？</td>*/

//
			String bb = str.substring(str.indexOf("<tdwidth=\"100%\"bgcolor=\"#E6E6DF\"height=\"20\">"));

			for(int i=0;i<20;i++){
				String aa = bb.substring(bb.indexOf("<tdwidth=\"100%\"bgcolor=\"#E6E6DF\"height=\"20\">")  +"<tdwidth=\"100%\"bgcolor=\"#E6E6DF\"height=\"20\">".length(), bb.indexOf("</td>"));
				System.out.println(aa);
			}

			Matcher testDetailMatcher = compile(reg).matcher(str);
			while (testDetailMatcher.find()) {
				String r = testDetailMatcher.group(1);

				System.out.println(r);


				/*if(r.contains("A") || r.contains("B") || r.contains("C") || r.contains("D") || r.contains("E")
						|| r.contains("F")|| r.contains("G")|| r.contains("H")|| r.contains("i")|| r.contains("j")){
					System.out.println(r);
				}else {
					System.out.println(r);
				}*/
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String argv[]) {
		String filePath = "C:\\Users\\jasonpayne\\Desktop\\html.txt";
		readTxtFile(filePath);
	}
}
