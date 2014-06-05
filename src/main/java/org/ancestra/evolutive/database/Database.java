package org.ancestra.evolutive.database;

import ch.qos.logback.classic.Logger;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.ancestra.evolutive.core.Server;
import org.ancestra.evolutive.database.data.*;
import org.ancestra.evolutive.database.data.CharacterData;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


public class Database {
	//connection
	HikariDataSource dataSource;
    private static Logger logger = (Logger) LoggerFactory.getLogger(Database.class);
	//data
	private AccountData accountData;
	private AnimationData animationData;
	private AreaData areaData;
	private AreaSubData areaSubData;
	private CharacterData characterData;
	private CollectorData collectorData;
	private GuildData guildData;
	private GuildMemberData guildMemberData;
	private HouseData houseData;
	private ItemData itemData;
	private ItemSetData itemSetData;
	private ItemTemplateData itemTemplateData;
	private JobData jobData;
	private MapData mapData;
	private MonsterData monsterData;
	private MountData mountData;
	private MountparkData mountparkData;
	private NpcAnswerData npcAnswerData;
	private NpcData npcData;
	private NpcQuestionData npcQuestionData;
	private NpcTemplateData npcTemplateData;
	private ScriptedCellData scriptedCellData;
	private SpellData spellData;
	private HdvData hdvData;
	private InteractiveObjectTemplateData ioTemplates;
	private TrunkData trunkData;
	private ExpData expData;
	private OtherData otherData;
	private DropData dropData;
	
	public void initializeData() {
		this.accountData = new AccountData(dataSource);
		this.animationData = new AnimationData(dataSource);
		this.areaData = new AreaData(dataSource);
		this.areaSubData = new AreaSubData(dataSource);
		this.characterData = new CharacterData(dataSource);
		this.collectorData = new CollectorData(dataSource);
		this.guildData = new GuildData(dataSource);
		this.guildMemberData = new GuildMemberData(dataSource);
		this.houseData = new HouseData(dataSource);
		this.itemData = new ItemData(dataSource);
		this.itemSetData = new ItemSetData(dataSource);
		this.itemTemplateData = new ItemTemplateData(dataSource);
		this.jobData = new JobData(dataSource);
		this.mapData = new MapData(dataSource);
		this.monsterData = new MonsterData(dataSource);
		this.npcAnswerData = new NpcAnswerData(dataSource);
		this.npcData = new NpcData(dataSource);
		this.npcQuestionData = new NpcQuestionData(dataSource);
		this.npcTemplateData = new NpcTemplateData(dataSource);
		this.scriptedCellData = new ScriptedCellData(dataSource);
		this.spellData = new SpellData(dataSource);
		this.hdvData = new HdvData(dataSource);
		this.ioTemplates = new InteractiveObjectTemplateData(dataSource);
		this.trunkData = new TrunkData(dataSource);
		this.expData = new ExpData(dataSource);
		this.otherData = new OtherData(dataSource);
		this.dropData = new DropData(dataSource);
		this.mountData = new MountData(dataSource);
		this.mountparkData = new MountparkData(dataSource);
	}
	
	public boolean initializeConnection() {
        logger.trace("Reading database config");
        HikariConfig config = new HikariConfig();
        config.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        config.addDataSourceProperty("serverName", Server.config.getHost());
        config.addDataSourceProperty("port",Server.config.getPort());
        config.addDataSourceProperty("databaseName",Server.config.getDatabaseName());
        config.addDataSourceProperty("user",Server.config.getUser());
        config.addDataSourceProperty("password", Server.config.getPass());


        dataSource = new HikariDataSource(config);
        if(!testConnection(dataSource)){
            logger.error("Pleaz check your username and password and database connection");
            logger.debug("If you're using a modified dbb you should look at test query in config.conf");
            System.exit(0);
        }
        logger.info("Database connection established");
        this.initializeData();
        return true;
	}
	
	public void close() {

	}
	
	public AccountData getAccountData() {
		return accountData;
	}
	public AnimationData getAnimationData() {
		return animationData;
	}
	public AreaData getAreaData() {
		return areaData;
	}
	public AreaSubData getAreaSubData() {
		return areaSubData;
	}
	public CharacterData getCharacterData() {
		return characterData;
	}
	public CollectorData getCollectorData() {
		return collectorData;
	}
	public HouseData getHouseData() {
		return houseData;
	}
	public ItemData getItemData() {
		return itemData;
	}
	public ItemSetData getItemSetData() {
		return itemSetData;
	}
	public ItemTemplateData getItemTemplateData() {
		return itemTemplateData;
	}
	public JobData getJobData() {
		return jobData;
	}
	public MapData getMapData() {
		return mapData;
	}
	public MonsterData getMonsterData() {
		return monsterData;
	}
	public MountData getMountData() {
		return mountData;
	}
	public MountparkData getMountparkData() {
		return mountparkData;
	}
	public NpcAnswerData getNpcAnswerData() {
		return npcAnswerData;
	}
	public NpcData getNpcData() {
		return npcData;
	}
	public NpcQuestionData getNpcQuestionData() {
		return npcQuestionData;
	}
	public NpcTemplateData getNpcTemplateData() {
		return npcTemplateData;
	}
	public ScriptedCellData getScriptedCellData() {
		return scriptedCellData;
	}
	public SpellData getSpellData() {
		return spellData;
	}

	public GuildData getGuildData() {
		return guildData;
	}

	public GuildMemberData getGuildMemberData() {
		return guildMemberData;
	}

	public HdvData getHdvData() {
		return hdvData;
	}

	public void setHdvData(HdvData hdvData) {
		this.hdvData = hdvData;
	}

	public InteractiveObjectTemplateData getIoTemplates() {
		return ioTemplates;
	}

	public void setIoTemplates(InteractiveObjectTemplateData ioTemplates) {
		this.ioTemplates = ioTemplates;
	}

	public TrunkData getTrunkData() {
		return trunkData;
	}

	public void setTrunkData(TrunkData trunkData) {
		this.trunkData = trunkData;
	}

	public ExpData getExpData() {
		return expData;
	}

	public void setExpData(ExpData expData) {
		this.expData = expData;
	}

	public OtherData getOtherData() {
		return otherData;
	}

	public void setOtherData(OtherData otherData) {
		this.otherData = otherData;
	}

	public DropData getDropData() {
		return dropData;
	}

	public void setDropData(DropData dropData) {
		this.dropData = dropData;
	}

    private boolean testConnection(HikariDataSource dataSource){
        try {
            Connection connection = dataSource.getConnection();
            connection.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
