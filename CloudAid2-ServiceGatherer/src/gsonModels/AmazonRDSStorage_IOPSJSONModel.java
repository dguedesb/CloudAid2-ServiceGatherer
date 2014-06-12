package gsonModels;

public class AmazonRDSStorage_IOPSJSONModel {
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
	}
	
	public static class Region{
		private String region;
		private Rate[] rates;
		
		public String getRegion() {
			return this.region;
		}
		public void setRegion(String region) {
			this.region = region;
		}
		public Rate[] getRates() {
			return rates;
		}
		public void setRates(Rate[] rates) {
			this.rates = rates;
		}
	}
	
	public static class Rate {
		private String type;
		private Price prices;
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public Price getPrices() {
			return prices;
		}
		public void setPrices(Price prices) {
			this.prices = prices;
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
