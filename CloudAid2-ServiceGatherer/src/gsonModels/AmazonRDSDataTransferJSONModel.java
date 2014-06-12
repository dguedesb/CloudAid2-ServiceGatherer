package gsonModels;

public class AmazonRDSDataTransferJSONModel {
	private float vers;
	private Config config;
	
	public float getVers() {
		return vers;
	}

	public void setVers(float vers) {
		this.vers = vers;
	}
	
	public Config getConfig() {
		return config;
	}

	public void setConfig(Config config) {
		this.config = config;
	}

	//config
	public static class Config 
	{
		private String[] currencies;
		private String[] valueColumns;
		private Region[] regions;
		private String rate;
		public String[] getCurrencies() {
			return currencies;
		}
		public void setCurrencies(String[] currencies) {
			this.currencies = currencies;
		}
		public String[] getValueColumns() {
			return valueColumns;
		}
		public void setValueColumns(String[] valueColumns) {
			this.valueColumns = valueColumns;
		}
		public Region[] getRegions() {
			return regions;
		}
		public void setRegions(Region[] regions) {
			this.regions = regions;
		}
		public String getRate() {
			return rate;
		}
		public void setRate(String rate) {
			this.rate = rate;
		}
	}
	
	public static class Region{
		private String region;
		private Type[] types;
		private AzDATATransfer azDataTransfer;
		private RegionalDataTransfer regionalDataTransfer;
		private ElasticLBDataTransfer elasticLBDataTransfer;
		public String getRegion() {
			return region;
		}
		public void setRegion(String region) {
			this.region = region;
		}
		public Type[] getTypes() {
			return types;
		}
		public void setTypes(Type[] types) {
			this.types = types;
		}
		public AzDATATransfer getAzDataTransfer() {
			return azDataTransfer;
		}
		public void setAzDataTransfer(AzDATATransfer azDataTransfer) {
			this.azDataTransfer = azDataTransfer;
		}
		public RegionalDataTransfer getRegionalDataTransfer() {
			return regionalDataTransfer;
		}
		public void setRegionalDataTransfer(RegionalDataTransfer regionalDataTransfer) {
			this.regionalDataTransfer = regionalDataTransfer;
		}
		public ElasticLBDataTransfer getElasticLBDataTransfer() {
			return elasticLBDataTransfer;
		}
		public void setElasticLBDataTransfer(ElasticLBDataTransfer elasticLBDataTransfer) {
			this.elasticLBDataTransfer = elasticLBDataTransfer;
		}
	}
	
	public static class AzDATATransfer{
		private Price prices;

		public Price getPrices() {
			return prices;
		}

		public void setPrices(Price prices) {
			this.prices = prices;
		}
	}
	
	public static class RegionalDataTransfer {
		private Price prices;

		public Price getPrices() {
			return prices;
		}

		public void setPrices(Price prices) {
			this.prices = prices;
		}
	}
	
	public static class ElasticLBDataTransfer{
		private String rate;
		private Price prices;

		public Price getPrices() {
			return prices;
		}

		public void setPrices(Price prices) {
			this.prices = prices;
		}

		public String getRate() {
			return rate;
		}

		public void setRate(String rate) {
			this.rate = rate;
		}
	}
	
	public static class Type{
		private String name;
		private Tier[] tiers;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Tier[] getTiers() {
			return tiers;
		}
		public void setTiers(Tier[] tiers) {
			this.tiers = tiers;
		}
	}
	
	public static class Tier{
		private String name;
		private Price prices;
		public Price getPrices() {
			return prices;
		}
		public void setPrices(Price prices) {
			this.prices = prices;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	}
	
	public static class Price {
		private String USD;

		public String getUSD() {
			return USD;
		}

		public void setUSD(String uSD) {
			USD = uSD;
		}
	}
}
