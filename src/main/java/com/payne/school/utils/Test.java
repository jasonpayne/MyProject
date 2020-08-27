package com.payne.school.utils;

import static java.util.regex.Pattern.compile;

public class Test {

//    public static void main(String[] args) {
        /*
        List<User> list = userMapper.selectForList(new User());
        for(User aaa :list){
            aaa.setPtopId(questionService.login(aaa));
            register0(aaa);
        }
        */
//        List<String> result = new ArrayList<String>();
//        Elements elements = documentDetail.select("a[href]").select("font[color=#0000FF]");
//        String source = "<a title=中国体育报 href=''>aaa</a><a title='北京日报' href=''>bbb</a>";
//        String source = "<input type=\"radio\" value=\"B\" name=\"666101071001\">";
        /*String source = "<td width='43' height='20'>20</td>";
//        Matcher m = compile("<td[^>]*>([^<]*)</td>").matcher(source);
        Matcher m = compile("(?<=<td>).*(?=</td>)").matcher(source);
        while (m.find()) {
            String r = m.group(1);
            result.add(r);
        }
        System.out.println(result);
    }*/

    public static void main(String[] args){
        int[] arr={-5,0,1,2,3,5,7,9,10,15,20,28,50,56};
        System.out.println(binarysearch(arr,56,0,arr.length-1));
    }

//    int binarysearch(int[] arr,int start,int len,int tar)

    public static Object binarysearch(int[] arr,int start,int len, int tar) {
        int end = len -1;
        if(arr==null){
            return false;
        }

        if(tar>arr[end]||tar<arr[start]){return false;}
        
        if(end==start && arr[start]!=tar){return false;}
        
        if(arr[start]==tar){return start;}

        if(arr[end]==tar){return end;}


        int middle=start+(end-start)/2;
        for(;;){
            if(tar==arr[middle]){
                return middle;
            }
            if(end==start+1){
                continue;
            }
            if(tar>arr[middle]){
                start = middle;
            }
            if(tar<arr[middle]){
                end= middle;
            }
        }
    }

}
