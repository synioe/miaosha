package com.miaoshaproject.dao;

import com.miaoshaproject.dataobject.SequenceDao;
import org.springframework.stereotype.Repository;

@Repository
public interface SequenceDaoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sequence_info
     *
     * @mbg.generated Fri Jul 19 15:46:24 CST 2019
     */
    int deleteByPrimaryKey(String name);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sequence_info
     *
     * @mbg.generated Fri Jul 19 15:46:24 CST 2019
     */
    int insert(SequenceDao record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sequence_info
     *
     * @mbg.generated Fri Jul 19 15:46:24 CST 2019
     */
    int insertSelective(SequenceDao record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sequence_info
     *
     * @mbg.generated Fri Jul 19 15:46:24 CST 2019
     */
    SequenceDao selectByPrimaryKey(String name);

    SequenceDao getSequenceByName(String name);
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sequence_info
     *
     * @mbg.generated Fri Jul 19 15:46:24 CST 2019
     */
    int updateByPrimaryKeySelective(SequenceDao record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sequence_info
     *
     * @mbg.generated Fri Jul 19 15:46:24 CST 2019
     */
    int updateByPrimaryKey(SequenceDao record);
}