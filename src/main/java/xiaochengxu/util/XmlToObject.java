package xiaochengxu.util;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
 

public class XmlToObject {
	
	 
	
	
	
	/*解析微信发过来的xml格式的信息---》封装到map集合中*/
	public static  Map<String,Object> xmlToMap(HttpServletRequest request)  {
		Map<String,Object> map=null;
		ServletInputStream inputStream=null;;
		try {
			map = new HashMap<String, Object>();
			  inputStream = request.getInputStream();
			  SAXReader sax = new SAXReader();
			Document doc = sax.read(inputStream);//获取document对象
			Element root = doc.getRootElement();//获取xml的根元素
			List<Element> list = root.elements();//获取根元素的所有子元素
			for (Element e : list) {
				System.out.println(e.getName() + "------========" + e.getText());
				map.put(e.getName(), e.getText());
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
		    try {
		    	if(inputStream!=null) {
		    		inputStream.close();//这个流是必须要关的，因为我们在很多地方都使用了此流，如果说此流不关，那么第一次使用时没有错误，但是当你第二次使用时就会抛出文件提前结束
				}
			} catch (Exception e2) {
				// TODO: handle exception
				e2.printStackTrace();
			}
		}
		return map;
	}

	 
	   
}
