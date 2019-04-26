package xiaochengxu.ctrl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import xiaochengxu.mapper.ProductMapper;
import xiaochengxu.mapper.TakeAddressMapper;
import xiaochengxu.pojo.Product;
import xiaochengxu.pojo.TakeAddress;
import xiaochengxu.pojo.TakeAddressExample;

@RestController
@RequestMapping("/product")
public class ProductCtrl {

	
	@Autowired
	ProductMapper productMapper;
	
	@Autowired
	TakeAddressMapper takeAddressMapper;
	
	@RequestMapping("/pageList")
	public Map<String,Object> pageList(@RequestBody Map<String,Object> map){
		Map<String,Object> m=new HashMap<String, Object>();
		int pageNum=(Integer) map.get("pageNum");
		int pageSize=(Integer) map.get("pageSize");
		PageHelper.startPage(pageNum, pageSize);
		List<Product> selectByExample = productMapper.selectByExample(null);
		PageInfo<Product> info=new PageInfo<Product>(selectByExample);
		int page=info.getPages();
		m.put("page", page);
		m.put("list", selectByExample);
		return m;
	}
	
	/*根据id获取详情*/
	@RequestMapping("/getinfoById/{id}/{openid}")
	public Map<String,Object> getinfoById(@PathVariable("id")Integer id, @PathVariable("openid") String openid) {
		System.out.println(openid);
		Product P=productMapper.selectByPrimaryKey(id);
		Map<String,Object> m=new HashMap<String, Object>();
		m.put("product", P);
		/*根据openid查询收货地址*/
		TakeAddressExample takeAddressExample=new TakeAddressExample();
		takeAddressExample.createCriteria().andOpenidEqualTo(openid);
		List<TakeAddress> selectByExample = takeAddressMapper.selectByExample(takeAddressExample);
		if(selectByExample.size()==0) {
			m.put("flg", false);
		}else {
			m.put("flg", true);
			m.put("takeAddress", selectByExample.get(0));
		}
		return m;
	}
}
