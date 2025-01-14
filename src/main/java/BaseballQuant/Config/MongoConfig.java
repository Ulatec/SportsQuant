package BaseballQuant.Config;


//@Configuration
//@EnableMongoRepositories(basePackages = "BaseballQuant.Repository")
//public class MongoConfig extends AbstractMongoClientConfiguration {
//
//    @Override
//    protected String getDatabaseName() {
//        return "test";
//    }
//
//    @Override
//    public MongoClient mongoClient() {
////        MongoClient mongoClient = MongoClients.create(
////                "mongodb+srv://user:com12pcom@cluster0.purn6.mongodb.net/myFirstDatabase?retryWrites=true&w=majority");
//        MongoClient mongoClient = MongoClients.create(
//                "mongodb://127.0.0.1:27017/?gssapiServiceName=mongodb");
//        MongoDatabase database = mongoClient.getDatabase("admin");
//
//        return mongoClient;
//    }
//    @Bean
//    public MongoTemplate mongoTemplate() {
//        return new MongoTemplate(mongoClient(), "test");
//    }
//
//    @Override
//    public Collection getMappingBasePackages() {
//        return Collections.singleton("BitcoinTicker");
//    }
//}
