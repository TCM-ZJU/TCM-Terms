import java.util.regex.Pattern;


public class regex {
	public static void main(String[] args) throws Exception{

	Pattern p=null; //正则表达式

	//Matcher m=null; //操作的字符串

	//boolean b; //字符串匹配，这是不符合的

	//p = Pattern.compile("[A-Za-z0-9_]+([-+.][A-Za-z0-9_]+)*@[A-Za-z0-9_]+([-.][A-Za-z0-9_]+)*\\.[A-Za-z0-9_]+([-.][A-Za-z0-9_]+)*");
	p = Pattern.compile(";?([0-9A-Z\\.]*(;|(, )))*A10\\.2\\.21\\.[0-9]+((;|(, ))[0-9A-Z\\.]*)*");

	//m = p.matcher("123+321@sina-com.cn");

	//b = m.matches();

	//System.out.println(b+"< br>");
	System.out.println(p.matcher(";A10.2.21").matches());
	System.out.println();
	System.out.println(p.matcher(";A10.2.21.255;B10.12").matches());
	System.out.println(p.matcher(";B10.12;A10.2.21.255;TB10.9.3").matches());
	System.out.println(p.matcher("A10.2.21.255, B10.12").matches());
	System.out.println(p.matcher("C10.2, A10.2.21.255, B10.12").matches());
	System.out.println();
	System.out.println(p.matcher(";A10.2.21.255;B10").matches());
	System.out.println(p.matcher(";A10.2.21.255;B").matches());
	System.out.println(p.matcher(";A10.2.21.255;10").matches());
	System.out.println();
	System.out.println(p.matcher(";A10.2.21.255;").matches());
	System.out.println(p.matcher(";A10.2.21.255").matches());
	System.out.println(p.matcher("A10.2.21.255").matches());
	System.out.println();
	System.out.println(p.matcher(";A10.2.21.255.255").matches());
	System.out.println(p.matcher(";A10,2.21.255").matches());	
	System.out.println(p.matcher("A10.2.21, B10.12").matches());
}
}
