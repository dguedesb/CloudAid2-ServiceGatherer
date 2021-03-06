package gsonModels;

public class AmazonGlacier_RequestsJSONModel {
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
		
		public String[] getCurrencies() {
			return currencies;
		}

		public void setCurrencies(String[] currencies) {
			this.currencies = currencies;
		}

		public Region[] getRegions() {
			return regions;
		}

		public void setRegions(Region[] regions) {
			this.regions = regions;
		}

		public String[] getValueColumns() {
			return valueColumns;
		}

		public void setValueColumns(String[] valueColumns) {
			this.valueColumns = valueColumns;
		}
	}
	
	public static class Region {
		private String region;
		private Tier[] tiers;
		public String getRegion() {
			return region;
		}
		public void setRegion(String region) {
			this.region = region;
		}
		public Tier[] getTiers() {
			return tiers;
		}
		public void setTiers(Tier[] tiers) {
			this.tiers = tiers;
		}
	}
	
	public static class Tier {
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
