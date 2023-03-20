package com.github.yanghyu.isid.common.sequence.mapper;

import com.github.yanghyu.isid.common.sequence.model.Sequence;
import com.github.yanghyu.isid.common.sequence.model.SequenceStepSize;
import org.apache.ibatis.annotations.*;

public interface SequenceMapper {

    @Select("SELECT c_key, c_create_datetime, c_update_datetime, c_current_number, c_default_step_size, c_version " +
            "FROM t_sequence WHERE c_key = #{key}")
    @Results(value = {
            @Result(column = "c_key", property = "key"),
            @Result(column = "c_create_datetime", property = "createDatetime"),
            @Result(column = "c_update_datetime", property = "updateDatetime"),
            @Result(column = "c_current_number", property = "currentNumber"),
            @Result(column = "c_default_step_size", property = "defaultStepSize"),
            @Result(column = "c_version", property = "version")
    })
    Sequence get(@Param("key") String key);

    @Insert("INSERT INTO t_sequence " +
            "(c_key, c_create_datetime, c_update_datetime, c_current_number, c_default_step_size, c_version) " +
            "VALUES (#{key}, #{createDatetime}, #{updateDatetime}, #{currentNumber}, #{defaultStepSize}, #{version})")
    int insert(@Param("sequence") Sequence sequence);

    @Update("UPDATE t_sequence " +
            "SET c_current_number = c_current_number + #{stepSize}, c_version = c_version + 1, c_update_datetime = NOW()" +
            "WHERE c_key = #{key}")
    int updateByStepSize(@Param("sequenceStepSize") SequenceStepSize sequenceStepSize);

}
