package xiaochengxu.ctrl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;

import xiaochengxu.mapper.WxUserMapper;
import xiaochengxu.pojo.WxUser;
import xiaochengxu.pojo.WxUserExample;
import xiaochengxu.util.HttpClientUtil;
import xiaochengxu.util.WxUrl;

@RestController
@RequestMapping("/wxUser")
public class WxUserCtrl {

	
	@Autowired
	WxUserMapper wxUserMapper;
	
	@RequestMapping("/userInfo")
	public WxUser  userInfo(@RequestBody Map<String,Object> map) throws Exception {
		 String code =(String) map.get("code");
		 //获取用户的openid.小程序和公众号不同，公众号可以获取用户的详细信息，小程序仅能获取openid,其他信息在前端获取
		 String userInfoUrl=WxUrl.GET_USER_INFO_URL.replace("APPID", WxUrl.APP_ID).replace("SECRET", WxUrl.APP_SECRET).replace("JSCODE", code);
		 String infoStr = HttpClientUtil.doGet(userInfoUrl);
		 String openId=JSONObject.parseObject(infoStr).getString("openid");
		 /*根据openid查询数据库中是否有用户信息*/
		 WxUserExample wxUserExa=new WxUserExample(); 
		 wxUserExa.createCriteria().andOpenidEqualTo(openId);
		 List<WxUser> selectByExa = wxUserMapper.selectByExample(wxUserExa);
		 if(selectByExa.size()>0) {
			 return selectByExa.get(0);
		 }
		 /*获取token*/
		 String tokenUrl=WxUrl.GET_ACCESS_TOKEN_URL.replace("APPID", WxUrl.APP_ID).replace("APPSECRET", WxUrl.APP_SECRET);
		 String tokenStr=HttpClientUtil.doGet(tokenUrl);
		 String accessToken =JSONObject.parseObject(tokenStr).getString("access_token");
		 
		 /*获取二维码图片：注意二维码图片只有在小程序上架之后才能获取的到*/
		 JSONObject mapQR=new JSONObject();
		 mapQR.put("access_token", accessToken);
		 mapQR.put("scene", openId);//封装场景值
		 mapQR.put("page", "pages/product/main");
		 String QRurl=WxUrl.GET_WXACodeUNLIMIT_URL.replace("ACCESS_TOKEN", accessToken);
		 File QRFile = HttpClientUtil.doPostAndResultImg(QRurl, mapQR.toJSONString());
		 FileInputStream fis=new FileInputStream(QRFile);
		 JSONObject uploadQN = uploadQN(fis);
		 System.out.println(uploadQN.getString("url"));
		 WxUser wxUser=new WxUser();
		 wxUser.setErImg(uploadQN.getString("url"));
		 wxUser.setOpenid(openId);
		 wxUserMapper.insertSelective(wxUser);
		 
		 /*将用户信息返回给前端*/
		 WxUserExample  wxUserExample=new WxUserExample();
		 wxUserExample.createCriteria().andOpenidEqualTo(openId);
		 List<WxUser> selectByExample = wxUserMapper.selectByExample(wxUserExample);
		 return selectByExample.get(0);
	}
	
	
	
	
	 
	
	
	/*更新用户信息*/
	@RequestMapping("/updateUser")
	public WxUser updateUser(@RequestBody WxUser user){
		WxUser selectByPrimaryKey = wxUserMapper.selectByPrimaryKey(user.getId());
		if(StringUtils.isNotBlank(selectByPrimaryKey.getPid())) {
			user.setPid(null);//不修改推荐人
		}
		wxUserMapper.updateByPrimaryKeySelective(user);
		Long id = user.getId();
		return wxUserMapper.selectByPrimaryKey(id);
	}
	
	
	
	
	public JSONObject uploadQN(FileInputStream input) throws IOException {
		//构造一个带指定Zone对象的配置类
		//构造一个带指定Zone对象的配置类
		
		
		
				Configuration cfg = new Configuration(Zone.zone0());
				//...其他参数参考类注释
				UploadManager uploadManager = new UploadManager(cfg);
				 String ACCESS_KEY = "mJ6qZUvu_-0lfUIXCJR72IXbcb3dPOIFK4T3oTLh";
			     String SECRET_KEY = "YDVkKKUna6tNFqQZbNJmr7fy9gU-5qWc7OSRawAO";
			     //要上传的空间
			     String bucketname = "xinyue";
		         String key=UUID.randomUUID()+".jpg";	      
			   //原始名称
			   //默认不指定key的情况下，以文件内容的hash值作为文件名
			     Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
			     String upToken = auth.uploadToken(bucketname);
			     Response response = uploadManager.put(input,key,upToken,null, null);
			   //解析上传成功的结果
			        DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
			        JSONObject json=new JSONObject();
			        json.put("url","http:"+File.separator+File.separator+"xinyue.shanjiezhifu.com"+File.separator+putRet.key );
			        return json;
	}
}
