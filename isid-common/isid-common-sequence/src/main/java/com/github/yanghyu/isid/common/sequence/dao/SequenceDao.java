package com.github.yanghyu.isid.common.sequence.dao;


import com.github.yanghyu.isid.common.sequence.mapper.SequenceMapper;
import com.github.yanghyu.isid.common.sequence.model.Sequence;
import com.github.yanghyu.isid.common.sequence.model.SequenceStepSize;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import javax.sql.DataSource;

public class SequenceDao {

    private final static String SEQUENCE_MAPPER_CLASS_NAME = SequenceMapper.class.getName();

    private final SqlSessionFactory sqlSessionFactory;

    public SequenceDao(DataSource dataSource) {
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("leaf", transactionFactory, dataSource);
        Configuration configuration = new Configuration(environment);
        configuration.addMapper(SequenceMapper.class);
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
    }

    public Sequence get(String key) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()){
            return sqlSession.selectOne(SEQUENCE_MAPPER_CLASS_NAME + ".get", key);
        }
    }

    public int insert(Sequence sequence) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()){
            int num = sqlSession.insert(SEQUENCE_MAPPER_CLASS_NAME + ".insert", sequence);
            sqlSession.commit();
            return num;
        }
    }

    public Sequence updateByStepSize(SequenceStepSize sequenceStepSize) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()){
            sqlSession.update(SEQUENCE_MAPPER_CLASS_NAME + ".updateByStepSize", sequenceStepSize);
            Sequence sequence = sqlSession.selectOne(SEQUENCE_MAPPER_CLASS_NAME + ".get", sequenceStepSize.getKey());
            sqlSession.commit();
            return sequence;
        }
    }

}
