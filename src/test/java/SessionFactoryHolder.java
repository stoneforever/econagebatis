import com.flowyun.cornerstone.db.mybatis.adaptation.MybatisConfiguration;
import com.flowyun.cornerstone.db.mybatis.adaptation.MybatisGlobalAssistant;
import com.flowyun.cornerstone.db.mybatis.enums.DBType;
import com.flowyun.cornerstone.db.mybatis.handlers.DefaultEnumTypeHandler;
import com.flowyun.cornerstone.db.mybatis.handlers.LocaleTypeHandler;
import com.flowyun.cornerstone.db.mybatis.monitor.StatementMonitor;
import entity.TestMapper;
import entity.TestShardingMapper;
import org.apache.ibatis.datasource.pooled.PooledDataSourceFactory;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.apache.ibatis.type.JdbcType;

import java.time.Duration;
import java.util.Locale;
import java.util.Properties;

public class SessionFactoryHolder {
    private static final SessionFactoryHolder holder = new SessionFactoryHolder();
    public static SessionFactoryHolder getInstance(){
        return holder;
    }

    private SqlSessionFactory sqlSessionFactory;
    public StatementMonitor monitor = new StatementMonitor(Duration.ofMillis(1));


    public SessionFactoryHolder() {
        this.sqlSessionFactory = new SqlSessionFactoryBuilder().build(getConfiguration());
    }
    private Configuration getConfiguration(){
        MybatisConfiguration configuration = new MybatisConfiguration();

        MybatisGlobalAssistant globalAssistant = new MybatisGlobalAssistant(configuration);
        globalAssistant.setDbType(DBType.MYSQL8);
        globalAssistant.setStatementMonitor(monitor);

        configuration.setGlobalAssistant(globalAssistant);

        PooledDataSourceFactory pooledDataSourceFactory = new PooledDataSourceFactory();

        Properties properties = new Properties();
        properties.setProperty("driver","org.mariadb.jdbc.Driver");
        properties.setProperty("url","jdbc:mariadb://192.168.0.59:3306/test");
        properties.setProperty("username","newsoft");
        properties.setProperty("password","econage123");
        pooledDataSourceFactory.setProperties(properties);

        JdbcTransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("environment", transactionFactory,pooledDataSourceFactory.getDataSource());
        configuration.setEnvironment(environment);
        //禁止懒加载，服务器api化后，意义不大
        configuration.setLazyLoadingEnabled(false);
        configuration.setAggressiveLazyLoading(false);
        //禁止多结果
        configuration.setMultipleResultSetsEnabled(false);
        configuration.setDefaultExecutorType(ExecutorType.SIMPLE);
        configuration.setMapUnderscoreToCamelCase(true);
        //禁用二级缓存
        configuration.setCacheEnabled(false);
        configuration.setDefaultEnumTypeHandler(DefaultEnumTypeHandler.class);
        //configuration.addInterceptor(new PaginationInterceptor());
        configuration.getTypeHandlerRegistry().register(Locale.class, JdbcType.VARCHAR,new LocaleTypeHandler());
        configuration.setLogImpl(StdOutImpl.class);


        configuration.addMapper(TestMapper.class);
        configuration.addMapper(TestShardingMapper.class);

        return configuration;
    }


    public SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }
}
