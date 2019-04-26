package xiaochengxu.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import xiaochengxu.pojo.Pay;
import xiaochengxu.pojo.PayExample;

public interface PayMapper {
    int countByExample(PayExample example);

    int deleteByExample(PayExample example);

    int insert(Pay record);

    int insertSelective(Pay record);

    List<Pay> selectByExample(PayExample example);

    int updateByExampleSelective(@Param("record") Pay record, @Param("example") PayExample example);

    int updateByExample(@Param("record") Pay record, @Param("example") PayExample example);
}