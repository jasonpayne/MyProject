package com.xinchao.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.regex.Matcher;

import static java.util.regex.Pattern.compile;

public class updateBeach {
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
			str	= new String(buffer,"UTF-8").replace("&nbsp;","").replace(" ", "");
//			str	= new String(buffer,"UTF-8").replace('　', ' ');
			System.out.println(str+"\n==================================================");
			Elements elements = new Elements();
			Document document = Jsoup.parse(str);
			String text = document.body().text().trim();
			String[] textArr = document.body().text().split("\\&nbsp;");
//			Tag tag = document.tag();
//			elements = document.select("div[align=\"center\"]");
			//			elements =  document.select("tr").select("td:[bgcolor=#E6E6DF]");  <div align="center">
//			elements = document.select("tr:nth-child(1) > td").select("[width=100%],[bgcolor=#E6E6DF],[height=20]");
			elements = document.select("td[width=100%],[bgcolor=#F7F7F4]").select("td[width=100%],[bgcolor=#E6E6DF],[height=20]");
			elements = elements.first().children().select("div:not(div[style=display: none;])").remove();
			elements = elements.select("h4>a");
			/*Page page = new Page();
			page.setRawText(str);
			page.getUrl();

			ResultItems resultItems = page.getResultItems();*/

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
