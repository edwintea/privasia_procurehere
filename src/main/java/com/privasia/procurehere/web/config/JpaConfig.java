/**
 *
 */
package com.privasia.procurehere.web.config;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

import javax.sql.DataSource;

import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.privasia.procurehere.job.GenericScheduleJob;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * @author Nitin Otageri
 */
@EnableEncryptableProperties
@Configuration
@ComponentScan({"com.privasia.procurehere.entity", "com.privasia.procurehere.core.enums"})
@EnableTransactionManagement
@PropertySource(value = {"classpath:application.properties"})
public class JpaConfig {

    @Autowired
    private Environment env;

    @Bean(name = "transactionManager")
    public JpaTransactionManager jpaTransMan() {
        JpaTransactionManager jtManager = new JpaTransactionManager(getEntityManagerFactoryBean().getObject());
        return jtManager;
    }

    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean getEntityManagerFactoryBean() {
        LocalContainerEntityManagerFactoryBean lcemfb = new LocalContainerEntityManagerFactoryBean();
        lcemfb.setBeanName("entityManagerFactory");
        lcemfb.setDataSource(getDataSource());
        lcemfb.setPersistenceUnitName("persistenceUnit");
        lcemfb.setJpaVendorAdapter(getJpaVendor());
        lcemfb.setPackagesToScan("com.privasia.procurehere.core.entity");
        Properties jpaProperties = new Properties();
        // jpaProperties.setProperty("hibernate.globally_quoted_identifiers", "true");
        jpaProperties.setProperty("hibernate.show_sql", "false");
        jpaProperties.setProperty("hibernate.format_sql", "true");
        // jpaProperties.setProperty("hibernate.generate_statistics", "true");
        if ("POSTGRESQL".equals(env.getRequiredProperty("db.jpa.type"))) {
            jpaProperties.setProperty("hibernate.default_schema", env.getRequiredProperty("db.schema"));
        }
        jpaProperties.setProperty("hibernate.hibernate.hbm2ddl.auto", "update");
        // jpaProperties.setProperty("hibernate.event.merge.entity_copy_observer", "allow");
        lcemfb.setJpaProperties(jpaProperties);
        LoadTimeWeaver loadTimeWeaver = new InstrumentationLoadTimeWeaver();
        lcemfb.setLoadTimeWeaver(loadTimeWeaver);
        return lcemfb;
    }

    @Bean
    public HibernateJpaVendorAdapter getJpaVendor() {
        HibernateJpaVendorAdapter jpa = new HibernateJpaVendorAdapter();
        jpa.setDatabase(Database.valueOf(env.getRequiredProperty("db.jpa.type")));
        jpa.setShowSql(false);
        jpa.setGenerateDdl(Boolean.parseBoolean(env.getRequiredProperty("db.mode")));
        jpa.setDatabasePlatform(env.getRequiredProperty("db.dialect"));
        return jpa;
    }

    /*
     * @Bean(name = "dataSource") public DataSource getDataSource() { ComboPooledDataSource dataSource = new
     * ComboPooledDataSource(); try { dataSource.setDriverClass(env.getRequiredProperty("db.driver")); } catch
     * (PropertyVetoException e) { e.printStackTrace(); } dataSource.setJdbcUrl(env.getRequiredProperty("db.url"));
     * dataSource.setUser(env.getRequiredProperty("db.username"));
     * dataSource.setPassword(env.getRequiredProperty("db.password")); dataSource.setInitialPoolSize(5);
     * dataSource.setMinPoolSize(5); dataSource.setMaxPoolSize(450); dataSource.setMaxIdleTime(3600);
     * dataSource.setAcquireIncrement(5); dataSource.setIdleConnectionTestPeriod(300); dataSource.setMaxStatements(0);
     * dataSource.setPreferredTestQuery(env.getRequiredProperty("db.test.query"));
     * dataSource.setCheckoutTimeout(Integer.parseInt(env.getRequiredProperty("db.connection.timeout"))); return
     * dataSource; }
     */

    @Bean(name = "dataSource")
    public DataSource getDataSource() {

        HikariConfig dataSource = new HikariConfig();
        dataSource.setDriverClassName(env.getRequiredProperty("db.driver"));
        dataSource.setJdbcUrl(env.getRequiredProperty("db.url"));
        dataSource.setUsername(env.getRequiredProperty("db.username"));
        dataSource.setPassword(env.getRequiredProperty("db.password"));
        dataSource.setSchema(env.getRequiredProperty("db.schema"));
        dataSource.setMinimumIdle(10);
        dataSource.setMaximumPoolSize(350);
        dataSource.setIdleTimeout(10 * 60 * 1000); // 10 mins idle
        dataSource.setConnectionTimeout(Integer.parseInt(env.getRequiredProperty("db.connection.timeout")));
        dataSource.setConnectionTestQuery(env.getRequiredProperty("db.test.query"));
        dataSource.setPoolName("ProcurehereDS");

        dataSource.addDataSourceProperty("dataSource.cachePrepStmts", "true");
        dataSource.addDataSourceProperty("dataSource.prepStmtCacheSize", "250");
        dataSource.addDataSourceProperty("dataSource.prepStmtCacheSqlLimit", "2048");
        dataSource.addDataSourceProperty("dataSource.useServerPrepStmts", "true");

        return new HikariDataSource(dataSource);
    }

    /*
     * @Bean(name = "dataSource") public DataSource getDataSource() { BoneCPDataSource dataSource = new
     * BoneCPDataSource(); dataSource.setPoolName("ProcurehereDS");
     * dataSource.setDriverClass(env.getRequiredProperty("db.driver"));
     * dataSource.setJdbcUrl(env.getRequiredProperty("db.url"));
     * dataSource.setUser(env.getRequiredProperty("db.username"));
     * dataSource.setPassword(env.getRequiredProperty("db.password")); dataSource.setMinConnectionsPerPartition(10);
     * dataSource.setMaxConnectionsPerPartition(170); dataSource.setPartitionCount(3);
     * dataSource.setIdleMaxAgeInSeconds(3600); dataSource.setAcquireIncrement(5);
     * dataSource.setIdleConnectionTestPeriodInSeconds(300); dataSource.setStatementsCacheSize(0);
     * dataSource.setServiceOrder("LIFO"); dataSource.setPoolAvailabilityThreshold(20);
     * dataSource.setConnectionTestStatement(env.getRequiredProperty("db.test.query"));
     * dataSource.setConnectionTimeoutInMs(Integer.parseInt(env.getRequiredProperty("db.connection.timeout"))); return
     * dataSource; }
     */
    @Bean(name = "schedulerFactoryBean")
    public SchedulerFactoryBean getSchedulerFactoryBean(@Autowired DataSource datasource) {
        SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
        scheduler.setAutoStartup(false);
        scheduler.setDataSource(datasource);
        scheduler.setApplicationContextSchedulerContextKey("applicationContext");
        Properties quartzProperties = new Properties();
        quartzProperties.put("org.quartz.scheduler.instanceName", "EPROCINSTANCE");
        quartzProperties.put("org.quartz.scheduler.instanceId", "AUTO");
        quartzProperties.put("org.quartz.scheduler.skipUpdateCheck", "true");
        quartzProperties.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
        quartzProperties.put("org.quartz.threadPool.threadCount", "20");
        quartzProperties.put("org.quartz.threadPool.threadPriority", "5");
        quartzProperties.put("org.quartz.jobStore.misfireThreshold", "60000");
        quartzProperties.put("org.quartz.jobStore.tablePrefix", "QRTZ_");
        quartzProperties.put("org.quartz.jobStore.isClustered", "true");
        quartzProperties.put("org.quartz.jobStore.clusterCheckinInterval", "20000");

        if ("POSTGRESQL".equals(env.getRequiredProperty("db.jpa.type"))) {
            quartzProperties.put("org.quartz.jobStore.selectWithLockSQL", "SELECT * FROM {0}LOCKS WHERE SCHED_NAME = {1} AND LOCK_NAME = ? ");
            quartzProperties.put("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.PostgreSQLDelegate");
        } else {
            quartzProperties.put("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.MSSQLDelegate");
        }

        scheduler.setQuartzProperties(quartzProperties);

        scheduler.setTriggers(getReminderMonitorJob().getObject(), //
                getEventStatusMonitorJob().getObject(), //
                getNotificationMonitorJob().getObject(), //
                getSubscriptionMonitorJob().getObject(), //
                getMeetingStatusMonitorJob().getObject(), //
                getProductStatusMonitorJob().getObject(), //
                getEventStartMonitorJob().getObject(), //
                getFreeTrialMonitorJob().getObject(), //
                getEmailBoxJob().getObject(), //
                getSupplierSuspendRemoveJob().getObject(), //
                getRfaEventStatusMonitorJob().getObject(), //
                getRfaEventActiveStatusMonitorJob().getObject(), //
                getRfiEventStatusMonitorJob().getObject(), //
                getRfiEventActiveStatusMonitorJob().getObject(), //
                getRfpEventStatusMonitorJob().getObject(), //
                getRfpEventActiveStatusMonitorJob().getObject(), //
                getRfqEventStatusMonitorJob().getObject(), //
                getRfqEventActiveStatusMonitorJob().getObject(), //
                getRftEventStatusMonitorJob().getObject(), //
                getRftEventActiveStatusMonitorJob().getObject(), //
                getAnnouncementMonitorJob().getObject(), //
                getBudgetStatusMonitorJob().getObject(), //
                getApprovalReminderNotificationJob().getObject(), //
                getContractReminderMonitorJob().getObject(), //
                getSpFormActiveStatusMonitorJob().getObject(), //
                getSpFormCloseStatusMonitorJob().getObject(), //
                getSpFormRecurrenceMonitorJob().getObject(), //
                getContractActiveAndExpiredStatusMonitorJob().getObject());


        return scheduler;
    }

    @Bean(name = "meetingStatusMonitor")
    public CronTriggerFactoryBean getMeetingStatusMonitorJob() {

        TimeZone gmt = TimeZone.getTimeZone("GMT");

        JobDetailFactoryBean job = new JobDetailFactoryBean();
        job.setName("meetingStatusMonitorJob");
        job.setGroup(Scheduler.DEFAULT_GROUP);
        Map<String, String> jobDataAsMap = new HashMap<String, String>();
        jobDataAsMap.put("batchProcessorName", "meetingStatusMonitorJob");
        job.setJobDataAsMap(jobDataAsMap);
        job.setJobClass(GenericScheduleJob.class);
        job.afterPropertiesSet();

        CronTriggerFactoryBean bean = new CronTriggerFactoryBean();
        bean.setName("meetingStatusMonitorJobTrigger");
        bean.setGroup(Scheduler.DEFAULT_GROUP);
        bean.setJobDetail(job.getObject());
        bean.setCronExpression("0 * * * * ?"); // every minute
        bean.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
        bean.setTimeZone(gmt);
        try {
            bean.afterPropertiesSet();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return bean;
    }

    @Bean(name = "supplierSuspendStatus")
    public CronTriggerFactoryBean getSupplierSuspendRemoveJob() {

        TimeZone gmt = TimeZone.getTimeZone("GMT");

        JobDetailFactoryBean job = new JobDetailFactoryBean();
        job.setName("supplierSuspend");
        job.setGroup(Scheduler.DEFAULT_GROUP);
        Map<String, String> jobDataAsMap = new HashMap<String, String>();
        jobDataAsMap.put("batchProcessorName", "supplierSuspend");
        job.setJobDataAsMap(jobDataAsMap);
        job.setJobClass(GenericScheduleJob.class);
        job.afterPropertiesSet();

        CronTriggerFactoryBean bean = new CronTriggerFactoryBean();
        bean.setName("supplierSuspendJobTrigger");
        bean.setGroup(Scheduler.DEFAULT_GROUP);
        bean.setJobDetail(job.getObject());
        bean.setCronExpression("0 0/15 * * * ?"); // every minute
        bean.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
        bean.setTimeZone(gmt);
        try {
            bean.afterPropertiesSet();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return bean;
    }

    @Bean(name = "subscriptionMonitor")
    public CronTriggerFactoryBean getSubscriptionMonitorJob() {

        TimeZone gmt = TimeZone.getTimeZone("GMT");

        JobDetailFactoryBean job = new JobDetailFactoryBean();
        job.setName("subscriptionMonitorJob");
        job.setGroup(Scheduler.DEFAULT_GROUP);
        Map<String, String> jobDataAsMap = new HashMap<String, String>();
        jobDataAsMap.put("batchProcessorName", "subscriptionMonitorJob");
        job.setJobDataAsMap(jobDataAsMap);
        job.setJobClass(GenericScheduleJob.class);
        job.afterPropertiesSet();

        CronTriggerFactoryBean bean = new CronTriggerFactoryBean();
        bean.setName("subscriptionMonitorJobTrigger");
        bean.setGroup(Scheduler.DEFAULT_GROUP);
        bean.setJobDetail(job.getObject());
        bean.setCronExpression("0 0 * * * ?"); // Every Hour
        bean.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
        bean.setTimeZone(gmt);
        try {
            bean.afterPropertiesSet();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return bean;
    }


    @Bean(name = "notificationMonitor")
    public CronTriggerFactoryBean getNotificationMonitorJob() {

        TimeZone gmt = TimeZone.getTimeZone("GMT");

        JobDetailFactoryBean job = new JobDetailFactoryBean();
        job.setName("notificationMonitorJob");
        job.setGroup(Scheduler.DEFAULT_GROUP);
        Map<String, String> jobDataAsMap = new HashMap<String, String>();
        jobDataAsMap.put("batchProcessorName", "notificationMonitorJob");
        job.setJobDataAsMap(jobDataAsMap);
        job.setJobClass(GenericScheduleJob.class);
        job.afterPropertiesSet();

        CronTriggerFactoryBean bean = new CronTriggerFactoryBean();
        bean.setName("notificationMonitorJobTrigger");
        bean.setGroup(Scheduler.DEFAULT_GROUP);
        bean.setJobDetail(job.getObject());
        bean.setCronExpression("0/30 * * * * ?"); // every 30 secs
        bean.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
        bean.setTimeZone(gmt);
        try {
            bean.afterPropertiesSet();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return bean;
    }

    @Bean(name = "reminderMonitor")
    public CronTriggerFactoryBean getReminderMonitorJob() {

        TimeZone gmt = TimeZone.getTimeZone("GMT");

        JobDetailFactoryBean job = new JobDetailFactoryBean();
        job.setName("reminderMonitorJob");
        job.setGroup(Scheduler.DEFAULT_GROUP);
        Map<String, String> jobDataAsMap = new HashMap<String, String>();
        jobDataAsMap.put("batchProcessorName", "reminderMonitorJob");
        job.setJobDataAsMap(jobDataAsMap);
        job.setJobClass(GenericScheduleJob.class);
        job.afterPropertiesSet();

        CronTriggerFactoryBean bean = new CronTriggerFactoryBean();
        bean.setName("reminderMonitorJobTrigger");
        bean.setGroup(Scheduler.DEFAULT_GROUP);
        bean.setJobDetail(job.getObject());
        bean.setCronExpression("0/30 * * * * ?"); // every 30 secs
        bean.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
        bean.setTimeZone(gmt);
        try {
            bean.afterPropertiesSet();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return bean;
    }

    @Bean(name = "eventStatusMonitor")
    public CronTriggerFactoryBean getEventStatusMonitorJob() {
        TimeZone gmt = TimeZone.getTimeZone("GMT");

        JobDetailFactoryBean job = new JobDetailFactoryBean();
        job.setName("eventStatusMonitorJob");
        job.setGroup(Scheduler.DEFAULT_GROUP);
        Map<String, String> jobDataAsMap = new HashMap<String, String>();
        jobDataAsMap.put("batchProcessorName", "eventStatusMonitorJob");
        job.setJobDataAsMap(jobDataAsMap);
        job.setJobClass(GenericScheduleJob.class);
        job.afterPropertiesSet();

        // SimpleTriggerFactoryBean trigger = new SimpleTriggerFactoryBean();
        // trigger.setJobDetail(job.getObject());
        CronTriggerFactoryBean bean = new CronTriggerFactoryBean();
        bean.setName("eventStatusMonitorJobTrigger");
        bean.setGroup(Scheduler.DEFAULT_GROUP);
        bean.setJobDetail(job.getObject());
        bean.setCronExpression("0/30 * * * * ?"); // Every 30 secs
        bean.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
        bean.setTimeZone(gmt);
        ;
        try {
            bean.afterPropertiesSet();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bean;
    }

    @Bean(name = "productStatusMonitor")
    public CronTriggerFactoryBean getProductStatusMonitorJob() {
        TimeZone gmt = TimeZone.getTimeZone("GMT");

        JobDetailFactoryBean job = new JobDetailFactoryBean();
        job.setName("productStatusMonitorJob");
        job.setGroup(Scheduler.DEFAULT_GROUP);
        Map<String, String> jobDataAsMap = new HashMap<String, String>();
        jobDataAsMap.put("batchProcessorName", "productStatusMonitorJob");
        job.setJobDataAsMap(jobDataAsMap);
        job.setJobClass(GenericScheduleJob.class);
        job.afterPropertiesSet();

        CronTriggerFactoryBean bean = new CronTriggerFactoryBean();
        bean.setName("productStatusMonitorJobTrigger");
        bean.setGroup(Scheduler.DEFAULT_GROUP);
        bean.setJobDetail(job.getObject());
        bean.setCronExpression("0 10 0 * * ?"); // Every day Morning on 12:10 AM
        bean.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
        bean.setTimeZone(gmt);
        try {
            bean.afterPropertiesSet();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return bean;
    }

    @Bean(name = "eventStartMonitor")
    public CronTriggerFactoryBean getEventStartMonitorJob() {
        TimeZone gmt = TimeZone.getTimeZone("GMT");

        JobDetailFactoryBean job = new JobDetailFactoryBean();
        job.setName("eventStartMonitorJob");
        job.setGroup(Scheduler.DEFAULT_GROUP);
        Map<String, String> jobDataAsMap = new HashMap<String, String>();
        jobDataAsMap.put("batchProcessorName", "eventStartMonitorJob");
        job.setJobDataAsMap(jobDataAsMap);
        job.setJobClass(GenericScheduleJob.class);
        job.afterPropertiesSet();

        // SimpleTriggerFactoryBean trigger = new SimpleTriggerFactoryBean();
        // trigger.setJobDetail(job.getObject());
        CronTriggerFactoryBean bean = new CronTriggerFactoryBean();
        bean.setName("eventStartMonitorJobTrigger");
        bean.setGroup(Scheduler.DEFAULT_GROUP);
        bean.setJobDetail(job.getObject());
        bean.setCronExpression("0 * * * * ?"); // Every minute
        bean.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
        bean.setTimeZone(gmt);
        try {
            bean.afterPropertiesSet();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return bean;
    }

    @Bean(name = "freeTrialMonitor")
    public CronTriggerFactoryBean getFreeTrialMonitorJob() {

        TimeZone gmt = TimeZone.getTimeZone("GMT");

        JobDetailFactoryBean job = new JobDetailFactoryBean();
        job.setName("freeTrialMonitorJob");
        job.setGroup(Scheduler.DEFAULT_GROUP);
        Map<String, String> jobDataAsMap = new HashMap<String, String>();
        jobDataAsMap.put("batchProcessorName", "freeTrialMonitorJob");
        job.setJobDataAsMap(jobDataAsMap);
        job.setJobClass(GenericScheduleJob.class);
        job.afterPropertiesSet();

        CronTriggerFactoryBean bean = new CronTriggerFactoryBean();
        bean.setName("freeTrialMonitorJobTrigger");
        bean.setGroup(Scheduler.DEFAULT_GROUP);
        bean.setJobDetail(job.getObject());
        bean.setCronExpression("0 0/5 * * * ?"); // Every 5 min
        bean.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
        bean.setTimeZone(gmt);
        try {
            bean.afterPropertiesSet();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return bean;
    }

    @Bean(name = "emailBox")
    public CronTriggerFactoryBean getEmailBoxJob() {
        TimeZone gmt = TimeZone.getTimeZone("GMT");
        JobDetailFactoryBean job = new JobDetailFactoryBean();
        job.setName("emailBoxJob");
        job.setGroup(Scheduler.DEFAULT_GROUP);
        Map<String, String> jobDataAsMap = new HashMap<String, String>();
        jobDataAsMap.put("batchProcessorName", "emailBoxJob");
        job.setJobDataAsMap(jobDataAsMap);
        job.setJobClass(GenericScheduleJob.class);
        job.afterPropertiesSet();

        CronTriggerFactoryBean bean = new CronTriggerFactoryBean();
        bean.setName("emailBoxJobTrigger");
        bean.setGroup(Scheduler.DEFAULT_GROUP);
        bean.setJobDetail(job.getObject());
        bean.setCronExpression("0 0 0 * * ?"); // Every mid night
        bean.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
        bean.setTimeZone(gmt);
        try {
            bean.afterPropertiesSet();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return bean;
    }

    // <bean name="newsSchedule" class="org.springframework.scheduling.quartz.JobDetailFactoryBean">
    // <property name="jobClass" value="com.stm.admin.schedule.jobs.GenericScheduleJob" />
    // <property name="jobDataAsMap">
    // <map>
    // <entry key="batchProcessorName" value="newsJob" />
    // </map>
    // </property>
    // </bean>
    //
    // <bean id="news" class="org.springframework.scheduling.quartz.CronTriggerFactoryBean">
    // <property name="jobDetail" ref="newsSchedule" />
    // <!-- run every day at noons -->
    // <property name="cronExpression" value="0 0 12 * * ?" />
    // <property name="misfireInstructionName" value="MISFIRE_INSTRUCTION_DO_NOTHING" />
    // <property name="timeZone" ref="timeZone" />
    // </bean>

    @Bean(name = "rfaEventStatusMonitor")
    public CronTriggerFactoryBean getRfaEventStatusMonitorJob() {
        TimeZone gmt = TimeZone.getTimeZone("GMT");

        JobDetailFactoryBean job = new JobDetailFactoryBean();
        job.setName("rfaEventStatusMonitorJob");
        job.setGroup(Scheduler.DEFAULT_GROUP);
        Map<String, String> jobDataAsMap = new HashMap<String, String>();
        jobDataAsMap.put("batchProcessorName", "rfaEventStatusMonitorJob");
        job.setJobDataAsMap(jobDataAsMap);
        job.setJobClass(GenericScheduleJob.class);
        job.afterPropertiesSet();

        // SimpleTriggerFactoryBean trigger = new SimpleTriggerFactoryBean();
        // trigger.setJobDetail(job.getObject());
        CronTriggerFactoryBean bean = new CronTriggerFactoryBean();
        bean.setName("rfaEventStatusMonitorJobTrigger");
        bean.setGroup(Scheduler.DEFAULT_GROUP);
        bean.setJobDetail(job.getObject());
        bean.setCronExpression("0/3 * * * * ?"); // Every 3 secs
        bean.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
        bean.setTimeZone(gmt);
        ;
        try {
            bean.afterPropertiesSet();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bean;
    }

    @Bean(name = "rfaEventActiveStatusMonitor")
    public CronTriggerFactoryBean getRfaEventActiveStatusMonitorJob() {
        TimeZone gmt = TimeZone.getTimeZone("GMT");

        JobDetailFactoryBean job = new JobDetailFactoryBean();
        job.setName("rfaEventActiveStatusMonitorJob");
        job.setGroup(Scheduler.DEFAULT_GROUP);
        Map<String, String> jobDataAsMap = new HashMap<String, String>();
        jobDataAsMap.put("batchProcessorName", "rfaEventActiveStatusMonitorJob");
        job.setJobDataAsMap(jobDataAsMap);
        job.setJobClass(GenericScheduleJob.class);
        job.afterPropertiesSet();

        // SimpleTriggerFactoryBean trigger = new SimpleTriggerFactoryBean();
        // trigger.setJobDetail(job.getObject());
        CronTriggerFactoryBean bean = new CronTriggerFactoryBean();
        bean.setName("rfaEventActiveStatusMonitorJobTrigger");
        bean.setGroup(Scheduler.DEFAULT_GROUP);
        bean.setJobDetail(job.getObject());
        bean.setCronExpression("0/3 * * * * ?"); // Every 3 secs
        bean.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
        bean.setTimeZone(gmt);
        ;
        try {
            bean.afterPropertiesSet();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bean;
    }

    @Bean(name = "rfiEventStatusMonitor")
    public CronTriggerFactoryBean getRfiEventStatusMonitorJob() {
        TimeZone gmt = TimeZone.getTimeZone("GMT");

        JobDetailFactoryBean job = new JobDetailFactoryBean();
        job.setName("rfiEventStatusMonitorJob");
        job.setGroup(Scheduler.DEFAULT_GROUP);
        Map<String, String> jobDataAsMap = new HashMap<String, String>();
        jobDataAsMap.put("batchProcessorName", "rfiEventStatusMonitorJob");
        job.setJobDataAsMap(jobDataAsMap);
        job.setJobClass(GenericScheduleJob.class);
        job.afterPropertiesSet();

        CronTriggerFactoryBean bean = new CronTriggerFactoryBean();
        bean.setName("rfiEventStatusMonitorJobTrigger");
        bean.setGroup(Scheduler.DEFAULT_GROUP);
        bean.setJobDetail(job.getObject());
        bean.setCronExpression("0/30 * * * * ?"); // Every 30 secs
        bean.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
        bean.setTimeZone(gmt);
        ;
        try {
            bean.afterPropertiesSet();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bean;
    }

    @Bean(name = "rfiEventActiveStatusMonitor")
    public CronTriggerFactoryBean getRfiEventActiveStatusMonitorJob() {
        TimeZone gmt = TimeZone.getTimeZone("GMT");

        JobDetailFactoryBean job = new JobDetailFactoryBean();
        job.setName("rfiEventActiveStatusMonitorJob");
        job.setGroup(Scheduler.DEFAULT_GROUP);
        Map<String, String> jobDataAsMap = new HashMap<String, String>();
        jobDataAsMap.put("batchProcessorName", "rfiEventActiveStatusMonitorJob");
        job.setJobDataAsMap(jobDataAsMap);
        job.setJobClass(GenericScheduleJob.class);
        job.afterPropertiesSet();

        CronTriggerFactoryBean bean = new CronTriggerFactoryBean();
        bean.setName("rfiEventActiveStatusMonitorJobTrigger");
        bean.setGroup(Scheduler.DEFAULT_GROUP);
        bean.setJobDetail(job.getObject());
        bean.setCronExpression("0/30 * * * * ?"); // Every 30 secs
        bean.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
        bean.setTimeZone(gmt);
        ;
        try {
            bean.afterPropertiesSet();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bean;
    }

    @Bean(name = "rfpEventStatusMonitor")
    public CronTriggerFactoryBean getRfpEventStatusMonitorJob() {
        TimeZone gmt = TimeZone.getTimeZone("GMT");

        JobDetailFactoryBean job = new JobDetailFactoryBean();
        job.setName("rfpEventStatusMonitorJob");
        job.setGroup(Scheduler.DEFAULT_GROUP);
        Map<String, String> jobDataAsMap = new HashMap<String, String>();
        jobDataAsMap.put("batchProcessorName", "rfpEventStatusMonitorJob");
        job.setJobDataAsMap(jobDataAsMap);
        job.setJobClass(GenericScheduleJob.class);
        job.afterPropertiesSet();

        CronTriggerFactoryBean bean = new CronTriggerFactoryBean();
        bean.setName("rfpEventStatusMonitorJobTrigger");
        bean.setGroup(Scheduler.DEFAULT_GROUP);
        bean.setJobDetail(job.getObject());
        bean.setCronExpression("0/30 * * * * ?"); // Every 30 secs
        bean.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
        bean.setTimeZone(gmt);
        ;
        try {
            bean.afterPropertiesSet();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bean;
    }

    @Bean(name = "rfpEventActiveStatusMonitor")
    public CronTriggerFactoryBean getRfpEventActiveStatusMonitorJob() {
        TimeZone gmt = TimeZone.getTimeZone("GMT");

        JobDetailFactoryBean job = new JobDetailFactoryBean();
        job.setName("rfpEventActiveStatusMonitorJob");
        job.setGroup(Scheduler.DEFAULT_GROUP);
        Map<String, String> jobDataAsMap = new HashMap<String, String>();
        jobDataAsMap.put("batchProcessorName", "rfpEventActiveStatusMonitorJob");
        job.setJobDataAsMap(jobDataAsMap);
        job.setJobClass(GenericScheduleJob.class);
        job.afterPropertiesSet();

        CronTriggerFactoryBean bean = new CronTriggerFactoryBean();
        bean.setName("rfpEventActiveStatusMonitorJobTrigger");
        bean.setGroup(Scheduler.DEFAULT_GROUP);
        bean.setJobDetail(job.getObject());
        bean.setCronExpression("0/30 * * * * ?"); // Every 30 secs
        bean.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
        bean.setTimeZone(gmt);
        ;
        try {
            bean.afterPropertiesSet();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bean;
    }

    @Bean(name = "rfqEventStatusMonitor")
    public CronTriggerFactoryBean getRfqEventStatusMonitorJob() {
        TimeZone gmt = TimeZone.getTimeZone("GMT");

        JobDetailFactoryBean job = new JobDetailFactoryBean();
        job.setName("rfqEventStatusMonitorJob");
        job.setGroup(Scheduler.DEFAULT_GROUP);
        Map<String, String> jobDataAsMap = new HashMap<String, String>();
        jobDataAsMap.put("batchProcessorName", "rfqEventStatusMonitorJob");
        job.setJobDataAsMap(jobDataAsMap);
        job.setJobClass(GenericScheduleJob.class);
        job.afterPropertiesSet();

        CronTriggerFactoryBean bean = new CronTriggerFactoryBean();
        bean.setName("rfqEventStatusMonitorJobTrigger");
        bean.setGroup(Scheduler.DEFAULT_GROUP);
        bean.setJobDetail(job.getObject());
        bean.setCronExpression("0/30 * * * * ?"); // Every 30 secs
        bean.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
        bean.setTimeZone(gmt);
        ;
        try {
            bean.afterPropertiesSet();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bean;
    }

    @Bean(name = "rfqEventActiveStatusMonitor")
    public CronTriggerFactoryBean getRfqEventActiveStatusMonitorJob() {
        TimeZone gmt = TimeZone.getTimeZone("GMT");

        JobDetailFactoryBean job = new JobDetailFactoryBean();
        job.setName("rfqEventActiveStatusMonitorJob");
        job.setGroup(Scheduler.DEFAULT_GROUP);
        Map<String, String> jobDataAsMap = new HashMap<String, String>();
        jobDataAsMap.put("batchProcessorName", "rfqEventActiveStatusMonitorJob");
        job.setJobDataAsMap(jobDataAsMap);
        job.setJobClass(GenericScheduleJob.class);
        job.afterPropertiesSet();

        CronTriggerFactoryBean bean = new CronTriggerFactoryBean();
        bean.setName("rfqEventActiveStatusMonitorJobTrigger");
        bean.setGroup(Scheduler.DEFAULT_GROUP);
        bean.setJobDetail(job.getObject());
        bean.setCronExpression("0/30 * * * * ?"); // Every 30 secs
        bean.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
        bean.setTimeZone(gmt);
        ;
        try {
            bean.afterPropertiesSet();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bean;
    }

    @Bean(name = "rftEventStatusMonitor")
    public CronTriggerFactoryBean getRftEventStatusMonitorJob() {
        TimeZone gmt = TimeZone.getTimeZone("GMT");

        JobDetailFactoryBean job = new JobDetailFactoryBean();
        job.setName("rftEventStatusMonitorJob");
        job.setGroup(Scheduler.DEFAULT_GROUP);
        Map<String, String> jobDataAsMap = new HashMap<String, String>();
        jobDataAsMap.put("batchProcessorName", "rftEventStatusMonitorJob");
        job.setJobDataAsMap(jobDataAsMap);
        job.setJobClass(GenericScheduleJob.class);
        job.afterPropertiesSet();

        CronTriggerFactoryBean bean = new CronTriggerFactoryBean();
        bean.setName("rftEventStatusMonitorJobTrigger");
        bean.setGroup(Scheduler.DEFAULT_GROUP);
        bean.setJobDetail(job.getObject());
        bean.setCronExpression("0/30 * * * * ?"); // Every 30 secs
        bean.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
        bean.setTimeZone(gmt);
        ;
        try {
            bean.afterPropertiesSet();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bean;
    }

    @Bean(name = "rftEventActiveStatusMonitor")
    public CronTriggerFactoryBean getRftEventActiveStatusMonitorJob() {
        TimeZone gmt = TimeZone.getTimeZone("GMT");

        JobDetailFactoryBean job = new JobDetailFactoryBean();
        job.setName("rftEventActiveStatusMonitorJob");
        job.setGroup(Scheduler.DEFAULT_GROUP);
        Map<String, String> jobDataAsMap = new HashMap<String, String>();
        jobDataAsMap.put("batchProcessorName", "rftEventActiveStatusMonitorJob");
        job.setJobDataAsMap(jobDataAsMap);
        job.setJobClass(GenericScheduleJob.class);
        job.afterPropertiesSet();

        CronTriggerFactoryBean bean = new CronTriggerFactoryBean();
        bean.setName("rftEventActiveStatusMonitorJobTrigger");
        bean.setGroup(Scheduler.DEFAULT_GROUP);
        bean.setJobDetail(job.getObject());
        bean.setCronExpression("0/30 * * * * ?"); // Every 30 secs
        bean.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
        bean.setTimeZone(gmt);
        ;
        try {
            bean.afterPropertiesSet();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bean;
    }

    @Bean(name = "announcementMonitor")
    public CronTriggerFactoryBean getAnnouncementMonitorJob() {
        TimeZone gmt = TimeZone.getTimeZone("GMT");

        JobDetailFactoryBean job = new JobDetailFactoryBean();
        job.setName("announcementMonitorJob");
        job.setGroup(Scheduler.DEFAULT_GROUP);
        Map<String, String> jobDataAsMap = new HashMap<String, String>();
        jobDataAsMap.put("batchProcessorName", "announcementMonitorJob");
        job.setJobDataAsMap(jobDataAsMap);
        job.setJobClass(GenericScheduleJob.class);
        job.afterPropertiesSet();
        CronTriggerFactoryBean bean = new CronTriggerFactoryBean();
        bean.setName("announcementMonitorJobTrigger");
        bean.setGroup(Scheduler.DEFAULT_GROUP);
        bean.setJobDetail(job.getObject());
        bean.setCronExpression("0 */2 * ? * *"); // Every 2 min
        bean.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
        bean.setTimeZone(gmt);
        try {
            bean.afterPropertiesSet();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return bean;
    }

    @Bean(name = "budgetStatusMonitor")
    public CronTriggerFactoryBean getBudgetStatusMonitorJob() {
        TimeZone gmt = TimeZone.getTimeZone("GMT");

        JobDetailFactoryBean job = new JobDetailFactoryBean();
        job.setName("budgetStatusMonitorJob");
        job.setGroup(Scheduler.DEFAULT_GROUP);
        Map<String, String> jobDataAsMap = new HashMap<String, String>();
        jobDataAsMap.put("batchProcessorName", "budgetStatusMonitorJob");
        job.setJobDataAsMap(jobDataAsMap);
        job.setJobClass(GenericScheduleJob.class);
        job.afterPropertiesSet();

        CronTriggerFactoryBean bean = new CronTriggerFactoryBean();
        bean.setName("budgetStatusMonitorJobTrigger");
        bean.setGroup(Scheduler.DEFAULT_GROUP);
        bean.setJobDetail(job.getObject());
        bean.setCronExpression("0/30 * * * * ?"); // Every 30 secs
        bean.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
        bean.setTimeZone(gmt);
        try {
            bean.afterPropertiesSet();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bean;
    }

    @Bean(name = "approvalReminderNotification")
    public CronTriggerFactoryBean getApprovalReminderNotificationJob() {
        TimeZone gmt = TimeZone.getTimeZone("GMT");

        JobDetailFactoryBean job = new JobDetailFactoryBean();
        job.setName("approvalReminderNotificationJob");
        job.setGroup(Scheduler.DEFAULT_GROUP);
        Map<String, String> jobDataAsMap = new HashMap<String, String>();
        jobDataAsMap.put("batchProcessorName", "approvalReminderNotificationJob");
        job.setJobDataAsMap(jobDataAsMap);
        job.setJobClass(GenericScheduleJob.class);
        job.afterPropertiesSet();

        CronTriggerFactoryBean bean = new CronTriggerFactoryBean();
        bean.setName("approvalReminderNotificationJobTrigger");
        bean.setGroup(Scheduler.DEFAULT_GROUP);
        bean.setJobDetail(job.getObject());
        bean.setCronExpression("0 0/5 * * * ?"); // Every 5 min
        bean.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
        bean.setTimeZone(gmt);
        try {
            bean.afterPropertiesSet();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bean;
    }

    @Bean(name = "contractReminderMonitor")
    public CronTriggerFactoryBean getContractReminderMonitorJob() {
        TimeZone gmt = TimeZone.getTimeZone("GMT");
        JobDetailFactoryBean job = new JobDetailFactoryBean();
        job.setName("contractReminderMonitorJob");
        job.setGroup(Scheduler.DEFAULT_GROUP);
        Map<String, String> jobDataAsMap = new HashMap<String, String>();
        jobDataAsMap.put("batchProcessorName", "contractReminderMonitorJob");
        job.setJobDataAsMap(jobDataAsMap);
        job.setJobClass(GenericScheduleJob.class);
        job.afterPropertiesSet();

        CronTriggerFactoryBean bean = new CronTriggerFactoryBean();
        bean.setName("contractReminderMonitorJobTrigger");
        bean.setGroup(Scheduler.DEFAULT_GROUP);
        bean.setJobDetail(job.getObject());
        bean.setCronExpression("0 0/5 * * * ?"); // Every mid night
        bean.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
        bean.setTimeZone(gmt);
        try {
            bean.afterPropertiesSet();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return bean;
    }

    @Bean(name = "contractActiveAndExpiredStatusMonitor")
    public CronTriggerFactoryBean getContractActiveAndExpiredStatusMonitorJob() {
        TimeZone gmt = TimeZone.getTimeZone("GMT");

        JobDetailFactoryBean job = new JobDetailFactoryBean();
        job.setName("contractActiveAndExpiredStatusMonitorJob");
        job.setGroup(Scheduler.DEFAULT_GROUP);
        Map<String, String> jobDataAsMap = new HashMap<String, String>();
        jobDataAsMap.put("batchProcessorName", "contractActiveAndExpiredStatusMonitorJob");
        job.setJobDataAsMap(jobDataAsMap);
        job.setJobClass(GenericScheduleJob.class);
        job.afterPropertiesSet();

        CronTriggerFactoryBean bean = new CronTriggerFactoryBean();
        bean.setName("contractActiveAndExpiredStatusMonitorJobTrigger");
        bean.setGroup(Scheduler.DEFAULT_GROUP);
        bean.setJobDetail(job.getObject());
        bean.setCronExpression("0 * * * * ?"); // Every Minute
        bean.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
        bean.setTimeZone(gmt);
        try {
            bean.afterPropertiesSet();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bean;
    }


    @Bean(name = "spFormActiveStatusMonitor")
    public CronTriggerFactoryBean getSpFormActiveStatusMonitorJob() {
        TimeZone gmt = TimeZone.getTimeZone("GMT");

        JobDetailFactoryBean job = new JobDetailFactoryBean();
        job.setName("spFormActiveStatusMonitorJob");
        job.setGroup(Scheduler.DEFAULT_GROUP);
        Map<String, String> jobDataAsMap = new HashMap<String, String>();
        jobDataAsMap.put("batchProcessorName", "spFormActiveStatusMonitorJob");
        job.setJobDataAsMap(jobDataAsMap);
        job.setJobClass(GenericScheduleJob.class);
        job.afterPropertiesSet();

        // SimpleTriggerFactoryBean trigger = new SimpleTriggerFactoryBean();
        // trigger.setJobDetail(job.getObject());
        CronTriggerFactoryBean bean = new CronTriggerFactoryBean();
        bean.setName("spFormActiveStatusMonitorJobTrigger");
        bean.setGroup(Scheduler.DEFAULT_GROUP);
        bean.setJobDetail(job.getObject());
        bean.setCronExpression("0/3 * * * * ?"); // Every 3 secs
        bean.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
        bean.setTimeZone(gmt);
        try {
            bean.afterPropertiesSet();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bean;
    }

    @Bean(name = "spFormCloseStatusMonitor")
    public CronTriggerFactoryBean getSpFormCloseStatusMonitorJob() {
        TimeZone gmt = TimeZone.getTimeZone("GMT");

        JobDetailFactoryBean job = new JobDetailFactoryBean();
        job.setName("spFormCloseStatusMonitorJob");
        job.setGroup(Scheduler.DEFAULT_GROUP);
        Map<String, String> jobDataAsMap = new HashMap<String, String>();
        jobDataAsMap.put("batchProcessorName", "spFormCloseStatusMonitorJob");
        job.setJobDataAsMap(jobDataAsMap);
        job.setJobClass(GenericScheduleJob.class);
        job.afterPropertiesSet();

        // SimpleTriggerFactoryBean trigger = new SimpleTriggerFactoryBean();
        // trigger.setJobDetail(job.getObject());
        CronTriggerFactoryBean bean = new CronTriggerFactoryBean();
        bean.setName("spFormCloseStatusMonitorJobTrigger");
        bean.setGroup(Scheduler.DEFAULT_GROUP);
        bean.setJobDetail(job.getObject());
        bean.setCronExpression("0/3 * * * * ?"); // Every 3 secs
        bean.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
        bean.setTimeZone(gmt);
        ;
        try {
            bean.afterPropertiesSet();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bean;
    }

    @Bean(name = "spFormRecurrenceMonitor")
    public CronTriggerFactoryBean getSpFormRecurrenceMonitorJob() {
        TimeZone gmt = TimeZone.getTimeZone("GMT");

        JobDetailFactoryBean job = new JobDetailFactoryBean();
        job.setName("spFormRecurrenceMonitorJob");
        job.setGroup(Scheduler.DEFAULT_GROUP);
        Map<String, String> jobDataAsMap = new HashMap<String, String>();
        jobDataAsMap.put("batchProcessorName", "spFormRecurrenceMonitorJob");
        job.setJobDataAsMap(jobDataAsMap);
        job.setJobClass(GenericScheduleJob.class);
        job.afterPropertiesSet();

        // SimpleTriggerFactoryBean trigger = new SimpleTriggerFactoryBean();
        // trigger.setJobDetail(job.getObject());
        CronTriggerFactoryBean bean = new CronTriggerFactoryBean();
        bean.setName("spFormRecurrenceMonitorJobTrigger");
        bean.setGroup(Scheduler.DEFAULT_GROUP);
        bean.setJobDetail(job.getObject());
        bean.setCronExpression("0 0/5 * * * ?"); // Every 5 min
        bean.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
        bean.setTimeZone(gmt);
        ;
        try {
            bean.afterPropertiesSet();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bean;
    }


}
