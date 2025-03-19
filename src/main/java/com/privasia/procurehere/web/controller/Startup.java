/**
 * 
 */
package com.privasia.procurehere.web.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseDate;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.Trim;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import com.privasia.procurehere.core.dao.AccessRightsDao;
import com.privasia.procurehere.core.dao.BusinessUnitDao;
import com.privasia.procurehere.core.dao.CompanyStatusDao;
import com.privasia.procurehere.core.dao.CountryDao;
import com.privasia.procurehere.core.dao.CurrencyDao;
import com.privasia.procurehere.core.dao.EmailSettingsDao;
import com.privasia.procurehere.core.dao.EventIdSettingsDao;
import com.privasia.procurehere.core.dao.FavoriteSupplierDao;
import com.privasia.procurehere.core.dao.NaicsCodesDao;
import com.privasia.procurehere.core.dao.ProductContractDao;
import com.privasia.procurehere.core.dao.ScoreRatingDao;
import com.privasia.procurehere.core.dao.StateDao;
import com.privasia.procurehere.core.dao.SupplierPerformanceFormDao;
import com.privasia.procurehere.core.dao.SupplierPerformanceTemplateDao;
import com.privasia.procurehere.core.dao.TimeZoneDao;
import com.privasia.procurehere.core.dao.UomDao;
import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.dao.UserRoleDao;
import com.privasia.procurehere.core.entity.AccessRights;
import com.privasia.procurehere.core.entity.AuctionRules;
import com.privasia.procurehere.core.entity.BusinessUnit;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.CompanyStatus;
import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.entity.Currency;
import com.privasia.procurehere.core.entity.EmailSettings;
import com.privasia.procurehere.core.entity.NaicsCodes;
import com.privasia.procurehere.core.entity.Owner;
import com.privasia.procurehere.core.entity.ScoreRating;
import com.privasia.procurehere.core.entity.State;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.SupplierPerformanceCriteria;
import com.privasia.procurehere.core.entity.SupplierPerformanceEvaluatorUser;
import com.privasia.procurehere.core.entity.SupplierPerformanceForm;
import com.privasia.procurehere.core.entity.SupplierPerformanceFormCriteria;
import com.privasia.procurehere.core.entity.SupplierPerformanceTemplate;
import com.privasia.procurehere.core.entity.TimeZone;
import com.privasia.procurehere.core.entity.Uom;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.entity.UserRole;
import com.privasia.procurehere.core.enums.BuyerStatus;
import com.privasia.procurehere.core.enums.DurationMinSecType;
import com.privasia.procurehere.core.enums.SupperPerformanceEvaluatorStatus;
import com.privasia.procurehere.core.enums.SupplierPerformanceFormStatus;
import com.privasia.procurehere.core.enums.SupplierStatus;
import com.privasia.procurehere.core.enums.TenantType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.ExcelParseException;
import com.privasia.procurehere.core.parsers.FileParser;
import com.privasia.procurehere.core.parsers.FileParserFactory;
import com.privasia.procurehere.core.pojo.SupplierPerformanceErpRequestPojo;
import com.privasia.procurehere.core.supplier.dao.OwnerDao;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.job.DutchAuctionJob;
import com.privasia.procurehere.service.BuyerService;
import com.privasia.procurehere.service.RfaEventService;
import com.privasia.procurehere.service.RfiEnvelopService;
import com.privasia.procurehere.service.UserRoleService;
import com.privasia.procurehere.service.supplier.SupplierService;

/**
 * <p>
 * This is a singleton class that checks application "first run" and if identified, initializes the app database with
 * some initial required data.
 * </p>
 * <p>
 * If needed, it will sync the master data with external systems
 * </p>
 * 
 * @author Nitin Otageri
 */

@Service
@Scope("singleton")
@PropertySource(value = { "classpath:application.properties" })
public class Startup {
	private static final Logger LOG = LogManager.getLogger(Startup.class);

	@Autowired
	ApplicationContext applicationContext;

	@Autowired
	AccessRightsDao accessRightsDao;

	@Autowired
	UserDao userDao;

	@Autowired
	UserRoleDao userRoleDao;

	@Autowired
	UserRoleService userRoleService;

	@Autowired
	OwnerDao ownerDao;

	@Autowired
	UomDao uomDao;

	@Autowired
	BuyerService buyerService;

	@Autowired
	SupplierService supplierService;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	EmailSettingsDao emailSettingDao;

	@Autowired
	NaicsCodesDao naicsCodeDao;

	@Autowired
	CountryDao countryDao;

	@Autowired
	CompanyStatusDao companyStatusDao;

	@Autowired
	StateDao stateDao;

	@Autowired
	TimeZoneDao timeZoneDao;

	@Autowired
	CurrencyDao currencyDao;

	@Autowired
	EventIdSettingsDao eventIdSettingDao;

	@Autowired
	SchedulerFactoryBean schedulerFactoryBean;

	@Autowired
	RfaEventService rfaEventService;

	@Autowired
	private Environment env;

	@Autowired
	RfiEnvelopService rfiEnvelopService;
	
	// ---- RELATED TO SPM DATA MIGRATION START ----
	@Autowired
	FavoriteSupplierDao favoriteSupplierDao;

	@Autowired
	BusinessUnitDao businessUnitDao;

	@Autowired
	EventIdSettingsDao eventIdSettingsDao;

	@Autowired
	SupplierPerformanceFormDao supplierPerformanceFormDao;

	@Autowired
	SupplierPerformanceTemplateDao supplierPerformanceTemplateDao;

	@Autowired
	ScoreRatingDao scoreRatingDao;
	// ---- RELATED TO SPM DATA MIGRATION END ----

	@PreDestroy
	public void doShutdown() {
		try {
			schedulerFactoryBean.destroy();
		} catch (Exception e) {
			LOG.error("Error during application shutdown : " + e.getMessage());
		}
	}
	
	@Autowired
	ProductContractDao productContractDao;

	@PostConstruct
	public void doInit() {

		try {
			LOG.info("                                                 ");
			LOG.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			LOG.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			LOG.info("    INITIALIZING APPLICATION MASTER DATA.....  ");
			LOG.info("    PERFORMING DATA SYNC IF REQUIRED.....        ");
			LOG.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			LOG.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			LOG.info("                                                 ");
			try {
				File file = new File(Global.FILE_PATH);
				if (!file.exists()) {
					if (!file.mkdirs()) {
						LOG.fatal("***************************************************************************");
						LOG.fatal("THE TEMP DIRECTORY REQUIRED FOR APPLICATION COULD NOT BE CREATED OR DOES NOT EXIST.");
						LOG.fatal("EXPECTED TEMP DIRECTORY : " + Global.FILE_PATH);
						LOG.fatal("***************************************************************************");
					}
				}
			} catch (Exception e) {
				LOG.error(e);
				LOG.fatal("***************************************************************************");
				LOG.fatal("THE TEMP DIRECTORY REQUIRED FOR APPLICATION COULD NOT BE CREATED OR DOES NOT EXIST.");
				LOG.fatal("EXPECTED TEMP DIRECTORY : " + Global.FILE_PATH);
				LOG.fatal("***************************************************************************");
			}
			// JRSwapFileVirtualizer virtualizer = null;
			// File file = new File("envelope.zip");
			// FileOutputStream os = new FileOutputStream(file);
			// try {
			// LOG.info("Starting downlaod file at " + file.getAbsolutePath());
			//// ByteArrayOutputStream baos = new ByteArrayOutputStream();
			// try (ZipOutputStream zos = new ZipOutputStream(os)) {
			// virtualizer = new JRSwapFileVirtualizer(100, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048,
			// 1024), false);
			// String filename = rfiEnvelopService.generateEnvelopeZip("2c9fde4573ed00060173fa72b84b4db7",
			// "2c9fde4573ed01c80173faad07e50458", zos, false, null, virtualizer, "GMT+8");
			// zos.flush();
			// zos.close();
			// }
			//
			// } catch (Exception e) {
			// e.printStackTrace();
			// } finally {
			// if (virtualizer != null) {
			// virtualizer.cleanup();
			// virtualizer = null;
			// }
			// os.flush();
			// os.close();
			// }

//			migrateSupplierPerformanceData();

			// Please coment below block before merge
//			 if (1 == 1) {
//			 return;
//			 }

			// TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
			LOG.info("Starting scheduler in 10 secs...");
			schedulerFactoryBean.getScheduler().startDelayed(10);
			schedulerFactoryBean.getScheduler().start();

			checkAclData();
			// checkDefaultUserSetup(true, false, false); // create default owner
			// checkIndustryCategoryData();
			// checkCountryData();
			// checkStateData();
			// checkTimeZoneData();
			// checkCurrencyData();
			// checkCompanyStatusData();
			// checkDefaultUserSetup(false, true, true); // create default buyer and supplier
			// checkUomData();

			LOG.info("Creating viewes....");
			// emailSettingDao.createViews();
			// emailSettingDao.createViewForDashboard();
			LOG.info("Viewes Created....");

		} catch (Exception e) {
			LOG.error("Error initializing application : " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	private void checkAclData() throws ExcelParseException, IOException {
		Resource resource = applicationContext.getResource("classpath:acl-list.xls");
		try {
			FileParser<AccessRights> aclFileParser = FileParserFactory.getParserForType(Global.ACL_LIST_EXCEL_PARSER);
			File tempFile = File.createTempFile("acl-list", "xls");
			tempFile.deleteOnExit();
			FileOutputStream out = new FileOutputStream(tempFile);
			IOUtils.copy(resource.getInputStream(), out);
			List<AccessRights> list = aclFileParser.parse(tempFile);
			accessRightsDao.checkAccessControlListMasterData(list);
		} catch (Exception e) {
			LOG.error("Error loading ACL into db : " + e.getMessage(), e);
		}
	}

	private void migrateSupplierPerformanceData() {

		try {
			LOG.info("--- LOADING SUPPLIER PERFORMANCE DATA START ----");
			ICsvBeanReader beanReader = new CsvBeanReader(new FileReader("/home/nitin/Production-Migration-Batch2.csv"), CsvPreference.STANDARD_PREFERENCE);

			// First column is header
			beanReader.getHeader(true);

			// the header elements are used to map the values to the bean
			// final String[] headers = beanReader.getHeader(true);
			final String[] headers = new String[] { "refNumber", "supplierName", "supplierCode", "businessUnit", "price", "quality", "delivery", "service", "overallScore", "concludeDate" };
			final CellProcessor[] processors = getProcessors();

			
			// PROD Environment
			String tenantId = "2c9fde456a979a59016af8e15d4a7d6f"; //FGV HOLDINGS BERHAD
			String formOwnerId = "2c9fde456b85a4f8016b91e093ab2922"; //FGVADMIN@FGVHOLDINGS.COM
			String spTemplateId = "2c9fde458226b7a1018234b24c341147"; //Supplier Performance Evaluation for Data Migration
			
			// DEMO Environment
//			String tenantId = "2c9f140c6a5f5c6d016a95cfa70b11e3"; //FGV HOLDINGS
//			String formOwnerId = "2c9f140c6f577dd3016f59f2dba10069"; //FGV.DEMO4@PROCUREHERE.COM
//			String spTemplateId = "2c9f140c823a4b1901823e7da54b0276"; //Supplier Performance Evaluation for Data Migration
			
			// TESTTWO Environment
//			String tenantId = "2c9fa58b5ca1b4dc015caed5cb1a1bb0"; //Procurehere Test Account
//			String formOwnerId = "2c9fa58b5ca1b4dc015caed5cb8d1bb3"; //BUYER.TEST@PROCUREHERE.COM
//			String spTemplateId = "2c9fa58b81b9416f0181ba0567200032"; //Migration Template
			
			// Local database
//			String tenantId = "2c9fa58b5ca1b4dc015caed5cb1a1bb0";
//			String formOwnerId = "2c9fa58b7174738a01717477fd4e0001";
//			String spTemplateId = "2c9fa58b813dcbc30181419c192400e0";

			Buyer buyer = new Buyer();
			buyer.setId(tenantId);

			User formOwner = new User();
			formOwner.setId(formOwnerId);

			SupplierPerformanceTemplate template = supplierPerformanceTemplateDao.findById(spTemplateId);
			// template.setId(spTemplateId);

			if (template == null) {
				throw new ApplicationException("Template Not Found by ID : " + spTemplateId);
			}

			SupplierPerformanceErpRequestPojo data;
			int totalPerformance = 0;
			int totalPerformanceErrors = 0;
			int totalPerformanceSuccess = 0;
			while ((data = beanReader.read(SupplierPerformanceErpRequestPojo.class, headers, processors)) != null) {

				totalPerformance++;
				// LOG.info(data);
				SupplierPerformanceForm sp = new SupplierPerformanceForm();

				Supplier sup = favoriteSupplierDao.getSupplierByVendorCodeAndBuyerId(data.getSupplierCode(), tenantId);
				if (sup == null) {
					totalPerformanceErrors++;
					LOG.warn(">>> No Supplier found by Vendor Code : " + data.getSupplierCode());
					continue;
				}

				BusinessUnit bu = businessUnitDao.findByUnitCode(tenantId, data.getBusinessUnit());
				if (bu == null) {
					totalPerformanceErrors++;
					LOG.warn(">>> No Business Unit found by Unit Code : " + data.getBusinessUnit());
					continue;
				}

				String formId = eventIdSettingsDao.generateEventIdByBusinessUnit(tenantId, "SP", bu);
				sp.setFormId(formId);
				sp.setAwardedSupplier(sup);
				sp.setBusinessUnit(bu);
				sp.setBuyer(buyer);
				sp.setCreatedBy(formOwner);
				sp.setFormOwner(formOwner);
				sp.setCreatedDate(data.getConcludeDate());
				sp.setConcludeDate(data.getConcludeDate());
				sp.setEvaluationStartDate(data.getConcludeDate());
				sp.setEvaluationEndDate(data.getConcludeDate());
				sp.setFormStatus(SupplierPerformanceFormStatus.CONCLUDED);
				sp.setIsRecurrenceEvaluation(Boolean.FALSE);
				sp.setOverallScore(new BigDecimal(data.getOverallScore()));
				sp.setReferenceName(data.getRefNumber());
				sp.setReferenceNumber(data.getRefNumber());
				sp.setRemarks("Data migrated from SAP");
				sp.setSupDetailCompleted(Boolean.TRUE);
				sp.setTemplate(template);
				sp.setFormName(template.getTemplateName());

				SupplierPerformanceFormCriteria cri1 = new SupplierPerformanceFormCriteria();
				cri1.setForm(sp);
				cri1.setLevel(1);
				cri1.setOrder(0);
				cri1.setMaximumScore(new BigDecimal(100));
				cri1.setWeightage(new BigDecimal(25));
				cri1.setName("Price");
				cri1.setAverageScore(new BigDecimal(data.getPrice()));
				cri1.setTotalScore(cri1.getAverageScore().multiply(cri1.getWeightage().divide(new BigDecimal(100), 2, RoundingMode.HALF_UP)));

				SupplierPerformanceFormCriteria cri2 = new SupplierPerformanceFormCriteria();
				cri2.setForm(sp);
				cri2.setLevel(2);
				cri2.setOrder(0);
				cri2.setMaximumScore(new BigDecimal(100));
				cri2.setWeightage(new BigDecimal(25));
				cri2.setName("Quality");
				cri2.setAverageScore(new BigDecimal(data.getQuality()));
				cri2.setTotalScore(cri2.getAverageScore().multiply(cri2.getWeightage().divide(new BigDecimal(100), 2, RoundingMode.HALF_UP)));

				SupplierPerformanceFormCriteria cri3 = new SupplierPerformanceFormCriteria();
				cri3.setForm(sp);
				cri3.setLevel(3);
				cri3.setOrder(0);
				cri3.setMaximumScore(new BigDecimal(100));
				cri3.setWeightage(new BigDecimal(25));
				cri3.setName("Delivery");
				cri3.setAverageScore(new BigDecimal(data.getDelivery()));
				cri3.setTotalScore(cri3.getAverageScore().multiply(cri3.getWeightage().divide(new BigDecimal(100), 2, RoundingMode.HALF_UP)));

				SupplierPerformanceFormCriteria cri4 = new SupplierPerformanceFormCriteria();
				cri4.setForm(sp);
				cri4.setLevel(4);
				cri4.setOrder(0);
				cri4.setMaximumScore(new BigDecimal(100));
				cri4.setWeightage(new BigDecimal(25));
				cri4.setName("Service");
				cri4.setAverageScore(new BigDecimal(data.getService()));
				cri4.setTotalScore(cri4.getAverageScore().multiply(cri4.getWeightage().divide(new BigDecimal(100), 2, RoundingMode.HALF_UP)));

				sp.setCriteria(new ArrayList<>());
				sp.getCriteria().add(cri1);
				sp.getCriteria().add(cri2);
				sp.getCriteria().add(cri3);
				sp.getCriteria().add(cri4);

				SupplierPerformanceEvaluatorUser usr = new SupplierPerformanceEvaluatorUser();
				usr.setEvaluationStatus(SupperPerformanceEvaluatorStatus.APPROVED);
				usr.setOverallScore(sp.getOverallScore());
				usr.setApprovedDate(data.getConcludeDate());
				usr.setBuyer(buyer);
				usr.setCreatedDate(data.getConcludeDate());
				usr.setEnablePerformanceEvaluationApproval(Boolean.FALSE);
				usr.setEvaluateDate(data.getConcludeDate());
				usr.setEvaluator(formOwner);
				usr.setForm(sp);
				usr.setPreviewDate(data.getConcludeDate());
				usr.setCriteria(new ArrayList<>());

				SupplierPerformanceCriteria c1 = new SupplierPerformanceCriteria();
				c1.setForm(sp);
				c1.setDescription(cri1.getDescription());
				c1.setEvaluationUser(usr);
				// Total Score = (Sum of Sub Criteria Score/100) x Criteria Weightage
				//c1.setEvaluatorTotalScore(cri1.getTotalScore().divide(new BigDecimal(100)).multiply(cri1.getWeightage()).setScale(0, RoundingMode.HALF_UP));
				// Set the avg score as total eval score
				c1.setEvaluatorTotalScore(cri1.getAverageScore());
				c1.setLevel(cri1.getLevel());
				c1.setOrder(cri1.getOrder());
				c1.setMaximumScore(cri1.getMaximumScore());
				c1.setName(cri1.getName());
				c1.setWeightage(cri1.getWeightage());

				SupplierPerformanceCriteria c2 = new SupplierPerformanceCriteria();
				c2.setForm(sp);
				c2.setDescription(cri2.getDescription());
				c2.setEvaluationUser(usr);
				// Total Score = (Sum of Sub Criteria Score/100) x Criteria Weightage
				//c2.setEvaluatorTotalScore(cri2.getTotalScore().divide(new BigDecimal(100)).multiply(cri2.getWeightage()).setScale(0, RoundingMode.HALF_UP));
				// Set the avg score as total eval score
				c2.setEvaluatorTotalScore(cri2.getAverageScore());
				c2.setLevel(cri2.getLevel());
				c2.setOrder(cri2.getOrder());
				c2.setMaximumScore(cri2.getMaximumScore());
				c2.setName(cri2.getName());
				c2.setWeightage(cri2.getWeightage());

				SupplierPerformanceCriteria c3 = new SupplierPerformanceCriteria();
				c3.setForm(sp);
				c3.setDescription(cri3.getDescription());
				c3.setEvaluationUser(usr);
				// Total Score = (Sum of Sub Criteria Score/100) x Criteria Weightage
				//c3.setEvaluatorTotalScore(cri3.getTotalScore().divide(new BigDecimal(100)).multiply(cri3.getWeightage()).setScale(0, RoundingMode.HALF_UP));
				// Set the avg score as total eval score
				c3.setEvaluatorTotalScore(cri3.getAverageScore());
				c3.setLevel(cri3.getLevel());
				c3.setOrder(cri3.getOrder());
				c3.setMaximumScore(cri3.getMaximumScore());
				c3.setName(cri3.getName());
				c3.setWeightage(cri3.getWeightage());

				SupplierPerformanceCriteria c4 = new SupplierPerformanceCriteria();
				c4.setForm(sp);
				c4.setDescription(cri4.getDescription());
				c4.setEvaluationUser(usr);
				// Total Score = (Sum of Sub Criteria Score/100) x Criteria Weightage
				//c4.setEvaluatorTotalScore(cri4.getTotalScore().divide(new BigDecimal(100)).multiply(cri4.getWeightage()).setScale(0, RoundingMode.HALF_UP));
				// Set the avg score as total eval score
				c4.setEvaluatorTotalScore(cri4.getAverageScore());
				c4.setLevel(cri4.getLevel());
				c4.setOrder(cri4.getOrder());
				c4.setMaximumScore(cri4.getMaximumScore());
				c4.setName(cri4.getName());
				c4.setWeightage(cri4.getWeightage());

				usr.getCriteria().add(c1);
				usr.getCriteria().add(c2);
				usr.getCriteria().add(c3);
				usr.getCriteria().add(c4);

				sp.setEvaluators(new ArrayList<>());
				sp.getEvaluators().add(usr);

				ScoreRating scoreRating = scoreRatingDao.getScoreRatingForScoreAndTenant(tenantId, sp.getOverallScore());
				sp.setScoreRating(scoreRating);

				sp = supplierPerformanceFormDao.save(sp);

				totalPerformanceSuccess++;
				LOG.info("Performance saved for Supplier : " + sup.getCompanyName() + ", Score: " + data.getOverallScore() + ", Form Id : " + formId);
			}

			LOG.info("Total Performance Processed : " + totalPerformance);
			LOG.info("Total Performance Errors : " + totalPerformanceErrors);
			LOG.info("Total Performance Success : " + totalPerformanceSuccess);

		} catch (Exception e) {
			LOG.error("Error loading Supplier Performance Data : " + e.getMessage(), e);
		}

		LOG.info("--- LOADING SUPPLIER PERFORMANCE DATA END ----");
	}

	/**
	 * Sets up the processors used for the examples.
	 */
	private static CellProcessor[] getProcessors() {
		final CellProcessor[] processors = new CellProcessor[] { //
				new NotNull(new Trim()), // Reference No
				new Optional(), // Supplier Name
				new NotNull(new Trim()), // Supplier Code
				new NotNull(new Trim()), // Business Unit
				new NotNull(new Trim()), // Criteria 1
				new NotNull(new Trim()), // Criteria 2
				new NotNull(new Trim()), // Criteria 3
				new NotNull(new Trim()), // Criteria 4
				new NotNull(new Trim(new ParseInt())), // Overall Score
				new NotNull(new Trim(new ParseDate("yyyyMMdd"))) // Conclusion Date
		};
		return processors;
	}

	@SuppressWarnings("unchecked")
	private void checkIndustryCategoryData() throws ExcelParseException, IOException {
		Resource resource = applicationContext.getResource("classpath:NAICS.xls");
		try {
			FileParser<NaicsCodes> icFileParser = FileParserFactory.getParserForType(Global.INDUSTRY_CATEGORY_EXCEL_PARSER);
			File tempFile = File.createTempFile("NAICS", "xls");
			tempFile.deleteOnExit();
			FileOutputStream out = new FileOutputStream(tempFile);
			IOUtils.copy(resource.getInputStream(), out);
			List<NaicsCodes> list = icFileParser.parse(tempFile);
			if (CollectionUtil.isNotEmpty(list)) {
				LOG.info("Loading NAICS Codes, Total top level categories : " + list.size());
				User createdByUser = userDao.getAdminUser();
				naicsCodeDao.loadIndustryCatgoryData(list, createdByUser);
			} else {
				LOG.warn("No NAICS Codes to load!!!!");
			}
		} catch (Exception e) {
			LOG.error("Error loading NAICS codes into db : " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	private void checkCountryData() throws ExcelParseException, IOException {
		Resource resource = applicationContext.getResource("classpath:countries.xls");
		try {
			FileParser<Country> aclFileParser = FileParserFactory.getParserForType(Global.COUNTRY_EXCEL_PARSER);
			File tempFile = File.createTempFile("countries", "xls");
			tempFile.deleteOnExit();
			FileOutputStream out = new FileOutputStream(tempFile);
			IOUtils.copy(resource.getInputStream(), out);
			List<Country> list = aclFileParser.parse(tempFile);
			User createdByUser = userDao.getAdminUser();
			countryDao.loadCountryMasterData(list, createdByUser);
		} catch (Exception e) {
			LOG.error("Error loading Country Master Data into db : " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	private void checkStateData() throws ExcelParseException, IOException {
		Resource resource = applicationContext.getResource("classpath:states.xls");
		try {
			FileParser<State> aclFileParser = FileParserFactory.getParserForType(Global.STATE_EXCEL_PARSER);
			File tempFile = File.createTempFile("states", "xls");
			tempFile.deleteOnExit();
			FileOutputStream out = new FileOutputStream(tempFile);
			IOUtils.copy(resource.getInputStream(), out);
			List<State> list = aclFileParser.parse(tempFile);
			User createdByUser = userDao.getAdminUser();
			stateDao.loadStateMasterData(list, createdByUser);
		} catch (Exception e) {
			LOG.error("Error loading Country Master Data into db : " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	private void checkTimeZoneData() throws ExcelParseException, IOException {
		Resource resource = applicationContext.getResource("classpath:timezones.xls");
		try {
			FileParser<TimeZone> timezoneFileParser = FileParserFactory.getParserForType(Global.TIMEZONE_EXCEL_PARSER);
			File tempFile = File.createTempFile("timezones", "xls");
			tempFile.deleteOnExit();
			FileOutputStream out = new FileOutputStream(tempFile);
			IOUtils.copy(resource.getInputStream(), out);
			List<TimeZone> list = timezoneFileParser.parse(tempFile);
			User createdByUser = userDao.getAdminUser();
			timeZoneDao.loadTimeZoneMasterData(list, createdByUser);
		} catch (Exception e) {
			LOG.error("Error loading TimeZone Master Data into db : " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	private void checkCurrencyData() throws ExcelParseException, IOException {
		Resource resource = applicationContext.getResource("classpath:currency.xls");
		try {
			FileParser<Currency> currencyFileParser = FileParserFactory.getParserForType(Global.CURRENCY_EXCEL_PARSER);
			File tempFile = File.createTempFile("currency", "xls");
			tempFile.deleteOnExit();
			FileOutputStream out = new FileOutputStream(tempFile);
			IOUtils.copy(resource.getInputStream(), out);
			List<Currency> list = currencyFileParser.parse(tempFile);
			User createdByUser = userDao.getAdminUser();
			currencyDao.loadCurrencyMasterData(list, createdByUser);
		} catch (Exception e) {
			LOG.error("Error loading Currency Master Data into db : " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	private void checkCompanyStatusData() throws ExcelParseException, IOException {
		Resource resource = applicationContext.getResource("classpath:companystatus.xls");
		try {
			FileParser<CompanyStatus> aclFileParser = FileParserFactory.getParserForType(Global.COMPANYSTATUS_EXCEL_PARSER);
			File tempFile = File.createTempFile("companystatus", "xls");
			tempFile.deleteOnExit();
			FileOutputStream out = new FileOutputStream(tempFile);
			IOUtils.copy(resource.getInputStream(), out);
			List<CompanyStatus> list = aclFileParser.parse(tempFile);
			User createdByUser = userDao.getAdminUser();
			companyStatusDao.loadCompanyStatusMasterData(list, createdByUser);
		} catch (Exception e) {
			LOG.error("Error loading CompanyStatus Master Data into db : " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	private void checkUomData() throws ExcelParseException, IOException {
		Resource resource = applicationContext.getResource("classpath:uom.xls");
		try {
			FileParser<Uom> aclFileParser = FileParserFactory.getParserForType(Global.UOM_EXCEL_PARSER);
			File tempFile = File.createTempFile("uom", "xls");
			tempFile.deleteOnExit();
			FileOutputStream out = new FileOutputStream(tempFile);
			IOUtils.copy(resource.getInputStream(), out);
			List<Uom> list = aclFileParser.parse(tempFile);
			User createdByUser = userDao.getAdminUser();
			uomDao.loadUomMasterData(list, createdByUser);
		} catch (Exception e) {
			LOG.error("Error loading Uom Master Data into db : " + e.getMessage(), e);
		}
	}

	/**
	 * 
	 */
	@Transactional(readOnly = false)
	private void checkDefaultUserSetup(boolean createOwner, boolean createBuyer, boolean createSupplier) {
		BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
		String password = enc.encode("admin123");

		// Create Default Owner
		if (createOwner) {
			Owner owner = ownerDao.findByProperty("loginEmail", "admin@procurehere.com");
			if (owner == null) {
				owner = new Owner();
				owner.setCommunicationEmail("admin@procurehere.com");
				owner.setLoginEmail("admin@procurehere.com");
				owner.setCompanyName("Privasia Sdn Bhd");
				owner.setCompanyRegistrationNumber("11121");
				owner.setCompanyContactNumber("+60379679600");
				owner.setMobileNumber("+60379679600");
				owner = ownerDao.saveOrUpdate(owner);
			}

			AccessRights acl1 = accessRightsDao.findByAccessControl("ROLE_USER");
			if (acl1 == null) {
				acl1 = new AccessRights();
				acl1.setAclName("Application User");
				acl1.setAclValue("ROLE_USER");
				// acl1.setIsMenuLink(false);
				acl1.setOwner(Boolean.TRUE);
				acl1.setSupplier(Boolean.TRUE);
				acl1.setBuyer(Boolean.TRUE);
				accessRightsDao.save(acl1);
			}

			AccessRights acl2 = accessRightsDao.findByAccessControl("ROLE_SUPER_ADMIN");

			if (acl2 == null) {
				acl2 = new AccessRights();
				acl2.setAclName("Super Administrator");
				acl2.setAclValue("ROLE_SUPER_ADMIN");
				// acl2.setIsMenuLink(false);
				acl1.setOwner(Boolean.TRUE);
				accessRightsDao.save(acl2);
			}

			List<AccessRights> acl = new ArrayList<AccessRights>();
			acl.add(acl1);
			acl.add(acl2);

			UserRole userRole = userRoleDao.findByUserRoleAndTenantId("Administrator".toUpperCase(), owner.getId());
			if (userRole == null) {
				userRole = new UserRole();
				userRole.setRoleName("Administrator".toUpperCase());
				userRole.setRoleDescription("System Administrator");

				List<AccessRights> aclList = accessRightsDao.getAccessControlListForOwner(false);
				for (AccessRights rights : acl) {
					if (!aclList.contains(rights)) {
						aclList.add(rights);
					}
				}
				userRole.setAccessControlList(aclList);

				userRole.setTenantId(owner.getId());
				userRoleDao.save(userRole);
			}

			User admin = userDao.findByUser("admin@procurehere.com".toUpperCase());
			if (admin == null) {
				LOG.warn(">>>>>>>> No admin account found... probably running the system for the first time...");
				LOG.warn(">>>>>>>> Creating default admin account");

				admin = new User();
				admin.setLoginId("admin@procurehere.com".toUpperCase());
				admin.setCommunicationEmail("admin@procurehere.com".toUpperCase());

				admin.setPassword(password);
				admin.setActive(true);
				admin.setCreatedDate(new Date());
				admin.setDeleted(false);
				admin.setLocked(false);
				admin.setLastPasswordChangedDate(new Date());
				admin.setName("Application Admin");
				admin.setTenantId(owner.getId());
				admin.setOwner(owner);
				admin.setTenantType(TenantType.OWNER);
				admin.setUserRole(userRole);

				userDao.save(admin);
			}
		}

		// Create Supplier
		if (createSupplier) {
			LOG.info("Creating default Supplier....");
			Supplier supplier = new Supplier();

			User admin = userDao.findByUser("admin@procurehere.com".toUpperCase());

			supplier.setCompanyName("Supplier Co. Sdn. Bhd.");
			Country registrationOfCountry = countryDao.findByProperty("countryCode", "MY");
			supplier.setRegistrationOfCountry(registrationOfCountry);
			supplier.setCompanyRegistrationNumber("1-112233");

			// Create the Default Supplier only if it doesn't exist yet.
			if (!supplierService.isExists(supplier)) {
				supplier.setActionBy(admin);
				supplier.setActionDate(new Date());
				supplier.setCity("Kuala Lumpur");
				supplier.setCommunicationEmail("supplier@procurehere.com");
				supplier.setCompanyContactNumber("12345678");
				List<CompanyStatus> csList = companyStatusDao.findAll(CompanyStatus.class);
				if (CollectionUtil.isNotEmpty(csList)) {
					supplier.setCompanyStatus(csList.get(0));
				}
				supplier.setCompanyWebsite("www.suppliercompanya.com");

				supplier.setCountries(new ArrayList<Country>());
				supplier.getCountries().add(registrationOfCountry);
				supplier.setCreatedBy(admin);
				supplier.setCreatedDate(new Date());
				supplier.setDeclaration(Boolean.TRUE);
				supplier.setDesignation("CEO");
				supplier.setFaxNumber("1234657578");
				supplier.setFullName("Supplier Person A");

				List<NaicsCodes> naicsCodes = naicsCodeDao.findForLevel(3);
				supplier.setNaicsCodes(naicsCodes);

				supplier.setLine1("Somewhere on this earth ");
				supplier.setLine2("Somewhere in Solar System");
				supplier.setLoginEmail("supplier@procurehere.com");
				supplier.setMobileNumber("+13 3434345354");
				supplier.setPassword(password);
				supplier.setRegistrationComplete(Boolean.TRUE);
				supplier.setRegistrationCompleteDate(new Date());
				supplier.setRegistrationOfCountry(registrationOfCountry);
				supplier.setRemarks("I want to do business using this platform");

				State state = stateDao.findByProperty("stateName", "Kedah");

				supplier.setState(state);
				supplier.setStates(new ArrayList<State>());
				supplier.getStates().add(state);
				supplier.setStatus(SupplierStatus.APPROVED);
				supplier.setSupplierTrackDesc("Done plenty of projects all over the world. Trust me!");
				supplier.setTermsOfUseAccepted(Boolean.TRUE);
				supplier.setTermsOfUseAcceptedDate(new Date());
				supplier.setYearOfEstablished(2010);

				try {
					supplier = supplierService.confirmSupplier(supplier, true);
					LOG.info("Default supplier created...");
				} catch (Exception e) {
					LOG.error("Error creating default supplier : " + e.getMessage(), e);
				}
			}
		}

		// Create Buyer
		if (createBuyer) {

			LOG.info("Creating default buyer....");
			Buyer buyer = new Buyer();
			Country registrationOfCountry = countryDao.findByProperty("countryCode", "MY");
			buyer.setRegistrationOfCountry(registrationOfCountry);
			// if (CollectionUtil.isNotEmpty(registrationOfCountry.getStates())) {
			// buyer.setState(registrationOfCountry.getStates().get(0));
			// }
			buyer.setCompanyRegistrationNumber("REG-1101");

			// Create the Default Buyer only if it doesn't exist yet.
			if (!buyerService.isExists(buyer)) {
				buyer.setCity("My City");
				if ("POSTGRESQL".equals(env.getRequiredProperty("db.jpa.type"))) {
					buyer.setCommunicationEmail("buyer@procurehere.com");
				} else {
					buyer.setCommunicationEmail("buyer@smebank.com");
				}
				buyer.setCompanyContactNumber("+12 34567898");
				buyer.setCompanyName("Buyer Company A");

				List<CompanyStatus> csList = companyStatusDao.findAll(CompanyStatus.class);
				if (CollectionUtil.isNotEmpty(csList)) {
					buyer.setCompanyStatus(csList.get(0));
				}
				buyer.setCompanyWebsite("www.buyercompanya.com");
				buyer.setFaxNumber("+10 293948548");
				buyer.setFullName("Buyer Person A");
				buyer.setLine1("Somewhere on this earth ");
				buyer.setLine2("Somewhere in Solar System");
				if ("POSTGRESQL".equals(env.getRequiredProperty("db.jpa.type"))) {
					buyer.setLoginEmail("buyer@procurehere.com");
				} else {
					buyer.setLoginEmail("buyer@smebank.com");
				}
				buyer.setMobileNumber("+12 3343453453");
				buyer.setNoOfEvents(500);
				buyer.setNoOfUsers(999);
				buyer.setPassword(password);
				buyer.setRegistrationComplete(Boolean.TRUE);

				buyer.setStatus(BuyerStatus.ACTIVE);
				buyer.setSubscriptionDate(new Date());
				buyer.setSubscriptionFrom(new Date());
				buyer.setSubscriptionTo(new Date());
				buyer.setTermsOfUseAccepted(Boolean.TRUE);
				buyer.setTermsOfUseAcceptedDate(new Date());
				buyer.setYearOfEstablished(2016);

				try {
					User buyerUser = buyerService.saveBuyer(buyer);

					buyerUser.setPassword(password);
					userDao.update(buyerUser);
					// EventIdSettings idSettings = eventIdSettingDao.findByProperty("tenantId",
					// buyerUser.getTenantId());
					// if (idSettings == null) {
					// idSettings = new EventIdSettings();
					// idSettings.setTenantId(buyerUser.getTenantId());
					// // RFT
					// idSettings.setRftIdPerfix("RFT");
					// idSettings.setRftIdDelimiter("/");
					// idSettings.setRftIdDatePattern("MMYYYY");
					// idSettings.setRftIdDelimiter("/");
					// idSettings.setRftIdSequence(1);
					//
					// // PR
					// idSettings.setPrIdPerfix("PR");
					// idSettings.setPrIdDelimiter("/");
					// idSettings.setPrIdDatePattern("MMYYYY");
					// idSettings.setPrIdDelimiter("/");
					// idSettings.setPrIdSequence(1);
					//
					// idSettings = eventIdSettingDao.saveOrUpdate(idSettings);
					// }
					LOG.info("Default Buyer created...");
				} catch (Exception e) {
					LOG.error("Error creating default Buyer : " + e.getMessage(), e);
				}
			} else {
				LOG.info("Default Buyer already exists... skipping...");
			}
		}

		if (emailSettingDao.loadEmailSettings() == null) {
			EmailSettings settings = new EmailSettings();
			settings.setSupplierSignupNotificationEmailAccount("arc@recstech.com");
			emailSettingDao.save(settings);
		}

	}

	private void scheduleAuction(AuctionRules auctionRulesData) {

		try {
			AuctionRules auctionRules = rfaEventService.getAuctionRulesWithEventById("402880e55887bb9f015887bf78aa0000");

			if (auctionRules == null) {
				return;
			}

			schedulerFactoryBean.getScheduler().pauseAll();

			final Set<TriggerKey> triggerKeys = schedulerFactoryBean.getScheduler().getTriggerKeys(GroupMatcher.anyTriggerGroup());
			for (TriggerKey triggerKey : triggerKeys) {
				LOG.info("Trigger of Group : " + triggerKey.getGroup() + ", Trigger Key : " + triggerKey);
				schedulerFactoryBean.getScheduler().deleteJob(schedulerFactoryBean.getScheduler().getTrigger(triggerKey).getJobKey());
			}
			JobKey jobKey = new JobKey("JOB" + auctionRules.getId(), "DUTCHAUCTION");
			JobDetail jobDetail = null;
			if (jobKey != null) {
				jobDetail = (JobDetail) schedulerFactoryBean.getScheduler().getJobDetail(jobKey);
			}
			TriggerKey triggerKey = new TriggerKey("TRIGGER" + auctionRules.getId(), "DUTCHAUCTION");
			SimpleTriggerImpl trigger = null;
			if (triggerKey != null) {
				trigger = (SimpleTriggerImpl) schedulerFactoryBean.getScheduler().getTrigger(triggerKey);
			}

			LOG.info("Scheduling auction : " + auctionRules.getEvent().getEventName());
			if (jobDetail == null) {
				JobDetailFactoryBean jobDetailBean = new JobDetailFactoryBean();
				jobDetailBean.setJobClass(DutchAuctionJob.class);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("auctionId", auctionRules.getId());
				jobDetailBean.setJobDataAsMap(map);
				jobDetailBean.setGroup("DUTCHAUCTION");
				jobDetailBean.setName("JOB" + auctionRules.getId());
				jobDetailBean.afterPropertiesSet();
				jobDetail = jobDetailBean.getObject();
			}

			if (trigger == null) {
				SimpleTriggerFactoryBean triggerBean = new SimpleTriggerFactoryBean();
				triggerBean.setName("TRIGGER" + auctionRules.getId());
				triggerBean.setGroup("DUTCHAUCTION");
				triggerBean.setJobDetail(jobDetail);
				if (auctionRules.getIntervalType() == DurationMinSecType.MINUTE) {
					triggerBean.setStartTime(new Date());
					triggerBean.setRepeatInterval(auctionRules.getInterval() * 1000 * 60);
					triggerBean.setRepeatCount(auctionRules.getDutchAuctionTotalStep());
					LOG.info("Schedule a job If Minute : ");
				} else if (auctionRules.getIntervalType() == DurationMinSecType.SECONDS) {
					triggerBean.setStartTime(new Date());
					triggerBean.setRepeatInterval(auctionRules.getInterval() * 1000);
					triggerBean.setRepeatCount(auctionRules.getDutchAuctionTotalStep());
					LOG.info("Schedule a job If Seconds : ");
				}

				triggerBean.afterPropertiesSet();
				trigger = (SimpleTriggerImpl) triggerBean.getObject();
				schedulerFactoryBean.getScheduler().scheduleJob(jobDetail, trigger);
			} else {
				if (auctionRules.getIntervalType() == DurationMinSecType.MINUTE) {
					trigger.setStartTime(new Date());
					trigger.setRepeatInterval(auctionRules.getInterval() * 1000 * 60);
					trigger.setRepeatCount(auctionRules.getDutchAuctionTotalStep());
					LOG.info("ReSchedule a job If Minute : ");
				} else if (auctionRules.getIntervalType() == DurationMinSecType.SECONDS) {
					trigger.setStartTime(new Date());
					trigger.setRepeatInterval(auctionRules.getInterval() * 1000);
					trigger.setRepeatCount(auctionRules.getDutchAuctionTotalStep());
					LOG.info("ReSchedule a job If Seconds : ");
				}
				schedulerFactoryBean.getScheduler().rescheduleJob(triggerKey, trigger);
			}

			schedulerFactoryBean.getScheduler().resumeAll();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
