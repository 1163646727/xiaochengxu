package xiaochengxu.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import xiaochengxu.pojo.TakeAddress;
import xiaochengxu.pojo.TakeAddressExample;

public interface TakeAddressMapper {
    int countByExample(TakeAddressExample example);

    int deleteByExample(TakeAddressExample example);

    int deleteByPrimaryKey(Long id);

    int insert(TakeAddress record);

    int insertSelective(TakeAddress record);

    List<TakeAddress> selectByExample(TakeAddressExample example);

    TakeAddress selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") TakeAddress record, @Param("example") TakeAddressExample example);

    int updateByExample(@Param("record") TakeAddress record, @Param("example") TakeAddressExample example);

    int updateByPrimaryKeySelective(TakeAddress record);

    int updateByPrimaryKey(TakeAddress record);
}