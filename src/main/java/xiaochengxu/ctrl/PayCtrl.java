package xiaochengxu.ctrl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.github.wxpay.sdk.WXPayUtil;

import xiaochengxu.mapper.PayMapper;
import xiaochengxu.pojo.Pay;
import xiaochengxu.pojo.PayExample;
import xiaochengxu.util.HttpClient;
import xiaochengxu.util.HttpClientUtil;
import xiaochengxu.util.WXSign;
import xiaochengxu.util.WxUrl;
import xiaochengxu.util.wxTemp;

@RestController
@RequestMapping("/pay")
public class PayCtrl {

	@Autowired
	RedisTemplate redisTemplate;
	
	@Autowired
	PayMapper payMapper;
	
	@RequestMapping("/start")
	public Map<String,String> pay(@RequestBody Map<String,Object> map,HttpServletRequest request) throws Exception{
		SortedMap<String, String> hashMap = new TreeMap<String, String>();
		String orderid=new Date().getTime()+"";
		map.put("orderId", orderid);
		Pay p=addProduct(map);
		System.out.println(p.getId()+"支付id");
		hashMap.put("appid", WxUrl.APP_ID);
		hashMap.put("mch_id", WxUrl.MEACH_ID);
		hashMap.put("nonce_str", WXPayUtil.generateNonceStr());
		hashMap.put("sign_type", "MD5");
		hashMap.put("body", "超级卖家");
		hashMap.put("attach", orderid);
		hashMap.put("out_trade_no", new Date().getTime()+"");
		hashMap.put("total_fee", "1");
		hashMap.put("spbill_create_ip", getClientIp(request));
		hashMap.put("notify_url", "https://www.shanjiezhifu.com/xiaochengxu/pay/success.do");
		hashMap.put("trade_type", "JSAPI");
		hashMap.put("openid", map.get("openid")+"");
		String sign=WXSign.createSign("utf-8",hashMap,WxUrl.KEY);
		hashMap.put("sign",sign );
		String paramXML =WXPayUtil.generateSignedXml(hashMap, WxUrl.KEY);
		//发送请求---是为了获取prepay_id
	    HttpClient client=new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
	    client.setHttps(true);//为true表示是https请求
		client.setXmlParam(paramXML);
		client.post();//发送请求
		//3获取结果
		String xmlResult=client.getContent();//微信返回xml数据
		return GZMess(xmlResult,orderid);
	}
	
	public SortedMap<String, String> GZMess(String xmlResult,String orderId){
		SortedMap<String, String> map = new TreeMap<String, String>(); 
		try {
			Map<String,String> mapResult=WXPayUtil.xmlToMap(xmlResult);//将xml解析成map
			//重新分装数据，只需要将h5支付所需要的数据传到前端，因为微信返回给我的有些是敏感信息，我们不能完全传入前台
			//h5支付有2次签名，第一次是为了获取prepay_id，第二次是为了唤起h5支付
			map.put("nonceStr", mapResult.get("nonce_str"));
			map.put("appId", mapResult.get("appid"));
			map.put("timeStamp", new Date().getTime()/1000+"");
			redisTemplate.boundValueOps(orderId).set(mapResult.get("prepay_id"));
			String packages = "prepay_id="+mapResult.get("prepay_id");
			map.put("package", packages);
			map.put("signType", "MD5");
			String sign=WXSign.createSign("utf-8",map,WxUrl.KEY);
			map.put("paySign", sign);
			map.put("packages", packages);
			return map;
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return map;
		
	}
	
	
	private String getClientIp(HttpServletRequest request) {
        String remoteAddr = "";
        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
            remoteAddr = remoteAddr.split(",")[0];

        }
        return remoteAddr.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : remoteAddr;
    }
	
	
	/*增加订单*/
	public Pay addProduct(Map<String,Object> map) {
		Pay pay=new Pay();
		pay.setOpenid(map.get("openid")+"");
		pay.setProductId(Integer.parseInt(map.get("productId")+""));
		pay.setStatus(0);
		pay.setId(map.get("orderId")+"");
		payMapper.insertSelective(pay);
		return pay;
	}
	
	@RequestMapping("/success")
	public void success(HttpServletRequest request) throws Exception {
		/*InputStream inputStream = request.getInputStream();
		InputStreamReader inputStreamReader=new InputStreamReader(inputStream);
		BufferedReader br=new BufferedReader(inputStreamReader);
		String str="";
		StringBuffer sb=new StringBuffer();
		while((str=br.readLine())!=null) {
			sb.append(str);
		}*/
		Map<String, Object> xmlToMap = xiaochengxu.util.XmlToObject.xmlToMap(request);
		 String orderId=(String) xmlToMap.get("attach");
		 Pay pay=new Pay();
		 pay.setStatus(1);
		 pay.setId(orderId);
		 PayExample payExample=new PayExample();
		 payExample.createCriteria().andIdEqualTo(orderId);
		 payMapper.updateByExampleSelective(pay, payExample);
		 String  prepay_id=(String) redisTemplate.boundValueOps(orderId).get();
		 String temp_id="wtlxzE-HYL05or7tsHzU39aeQCcY-vDicKdwTRNdbEs";//模板id
		 wxTemp keyword1=new wxTemp(orderId);
		 wxTemp keyword2=new wxTemp("0.1元");
		 wxTemp keyword3=new wxTemp(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
		 wxTemp keyword4=new wxTemp("衣服");
		 JSONObject jsonobject=new JSONObject();
		 jsonobject.put("keyword1", JSONObject.toJSONString(keyword1));
		 jsonobject.put("keyword2", JSONObject.toJSONString(keyword2));
		 jsonobject.put("keyword3", JSONObject.toJSONString(keyword3));
		 jsonobject.put("keyword4", JSONObject.toJSONString(keyword4));
		 JSONObject jsondate=new JSONObject();
		 jsondate.put("data", jsondate);
		 jsondate.put("touser", xmlToMap.get("openid"));
		 jsondate.put("template_id", temp_id);
		 jsondate.put("form_id", redisTemplate.boundValueOps(orderId).get());
		 String str=JSONObject.toJSONString(jsondate);
		 System.out.println(str);
		 System.out.println(">>>>>>>>>>"+redisTemplate.boundValueOps(orderId).get());
		 /*获取token*/
		 String tokenUrl=WxUrl.GET_ACCESS_TOKEN_URL.replace("APPID", WxUrl.APP_ID).replace("APPSECRET", WxUrl.APP_SECRET);
		 String tokenStr=HttpClientUtil.doGet(tokenUrl);
		 String accessToken =JSONObject.parseObject(tokenStr).getString("access_token");
		 String doPostJson = HttpClientUtil.doPostJson(WxUrl.SEND_TEMP.replace("ACCESS_TOKEN", accessToken), str);
	     System.out.println("doPostJson:"+doPostJson);
	}
   
}
