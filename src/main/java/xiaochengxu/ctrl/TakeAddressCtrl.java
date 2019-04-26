package xiaochengxu.ctrl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import xiaochengxu.mapper.TakeAddressMapper;
import xiaochengxu.pojo.TakeAddress;

@RestController
@RequestMapping("/address")
public class TakeAddressCtrl {

	
	
	@Autowired
	TakeAddressMapper takeAddressMapper;
	
	
	@RequestMapping("/add")
	public int add(@RequestBody TakeAddress record) {
		return takeAddressMapper.insertSelective(record);
	}
}
