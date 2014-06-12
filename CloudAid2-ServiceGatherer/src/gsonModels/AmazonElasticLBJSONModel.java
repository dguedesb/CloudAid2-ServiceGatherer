package gsonModels;

public class AmazonElasticLBJSONModel {
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
		private String rate;
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

		public String getRate() {
			return rate;
		}

		public void setRate(String rate) {
			this.rate = rate;
		}

	}
	
	public static class Region {
		private String region;
		private Type[] types;
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
	}
	
	public static class Type {
		private Value[] values;

		public Value[] getValues() {
			return values;
		}

		public void setValues(Value[] values) {
			this.values = values;
		}
	}
	
	public static class Value {
		private Price prices;
		private String rate;
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
