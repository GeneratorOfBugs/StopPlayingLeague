package simon.portfolio.stopplaying_eague;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ArrayList;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.Request;
import okhttp3.Response;
import okhttp3.OkHttpClient;
import okhttp3.Response;


/**
 * Hello world!
 *
 */
public class App {
	
    private static final String API_KEY = "someKey";
    private static final String SUMMONER_BY_NAME_URL = "https://europe.api.riotgames.com/riot/account/v1/accounts/by-riot-id/";
    private static final String MATCHES_BY_PUUID_PREFIX = "https://europe.api.riotgames.com/lol/match/v5/matches/by-puuid/";
    private static final String MATCHES_BY_PUUID_SUFFIX = "/ids?";

    
    
    public static void main( String[] args ) {
        String summonerName = "SomeUser";
        String tag = "someTag";

        try {
            JsonObject summonerInfo = getSummonerByName(summonerName, tag);
            
            String puuid = summonerInfo.get("puuid").getAsString();
            System.out.println(puuid);

            long nowTime = System.currentTimeMillis() / 1000L;
            long dayAgoTime = (System.currentTimeMillis() - 24 * 60 * 60 * 1000L) / 1000L;

            String matches = getMatchesByPuuid(dayAgoTime,nowTime,puuid);
            System.out.println(convertStringToList(matches).size());
            
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static JsonObject getSummonerByName(String summonerName, String tag) throws IOException {
        OkHttpClient client = new OkHttpClient();
        
        String url = SUMMONER_BY_NAME_URL + summonerName + "/" + tag;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Riot-Token", API_KEY)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            String responseData = response.body().string();
            return JsonParser.parseString(responseData).getAsJsonObject();
        }
    }
    
    
    private static String getMatchesByPuuid(long startTime,long endTime,String puuid) throws IOException {
        OkHttpClient client = new OkHttpClient();
        
        
        String commands ="startTime="+Long.toString(startTime)+"&endTime="+Long.toString(endTime)+"&start=0&count=100";
        
        
        //String encodedSummonerName = URLEncoder.encode(summonerName, StandardCharsets.UTF_8.toString());
        String url = MATCHES_BY_PUUID_PREFIX + puuid + MATCHES_BY_PUUID_SUFFIX + commands;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Riot-Token", API_KEY)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            String responseData = response.body().string();
            return responseData;
        }
    }
    
    public static List<String> convertStringToList(String input) {
        // Remove the square brackets
        String trimmedInput = input.substring(1, input.length() - 1);
        
        // Split the string by commas and remove quotes
        String[] items = trimmedInput.split(",(?=\")");
        
        // Create a list and add the items
        List<String> list = new ArrayList<String>();
        for (String item : items) {
            list.add(item.replaceAll("^\"|\"$", "")); // Remove leading and trailing quotes
        }
        
        return list;
    }
    
    
}
