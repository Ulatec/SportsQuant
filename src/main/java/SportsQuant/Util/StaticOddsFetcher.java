package SportsQuant.Util;

import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

public class StaticOddsFetcher {

    public static JSONObject getMarketsCloudBet(){
        try{
            Connection.Response response = Jsoup.connect("https://www.cloudbet.com/sports-api/c/v6/sports/competitions/basketball-usa-nba/events?markets=basketball.handicap&markets=basketball.moneyline&markets=basketball.totals&locale=en")
                    .method(Connection.Method.GET)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36 OPR/78.0.4093.153")
                    .header("Connection", "keep-alive")
                    .header("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6IjkwMDk1YmM2ZGM2ZDY3NzkxZDdkYTFlZWIxYTU1OWEzZDViMmM0ODYiLCJ0eXAiOiJKV1QifQ.eyJ0ZW5hbnQiOiJjbG91ZGJldCIsInV1aWQiOiJhZjBiYTQ5NS1iYmM3LTQyMmItOTViYi0xZDc3MzQxMzM3YmYiLCJpc3MiOiJodHRwczovL3NlY3VyZXRva2VuLmdvb2dsZS5jb20va3ViZXJzaG11YmVyLXByb2QtY2ItYXV0aCIsImF1ZCI6Imt1YmVyc2htdWJlci1wcm9kLWNiLWF1dGgiLCJhdXRoX3RpbWUiOjE2MzAwMzIwMDYsInVzZXJfaWQiOiJhZjBiYTQ5NS1iYmM3LTQyMmItOTViYi0xZDc3MzQxMzM3YmYiLCJzdWIiOiJhZjBiYTQ5NS1iYmM3LTQyMmItOTViYi0xZDc3MzQxMzM3YmYiLCJpYXQiOjE2MzAwNzI3ODksImV4cCI6MTYzMDA3NjM4OSwiZmlyZWJhc2UiOnsiaWRlbnRpdGllcyI6e30sInNpZ25faW5fcHJvdmlkZXIiOiJjdXN0b20ifX0.D9tWV403lYk9EElG09UW3NZTENUmMkquTf_8y2-eTqqsS3bHcrs3tMHXiXVy2EtPY0zfukc__3pMIQHYrga6TtSdkQjIB3qTf-Biemk9T0vtIhyZ_ClEiFTi3OoQ8FWPBN66jc_5DsRdm0FAQ23TmEwUB7oqTDKRZN6yC_xcc3ksAFoUIRB_9KrDi-ofXAr0VqejB4xKLrtpypxvXgHVs_rrxpA3hWg6FEPhVxQhI3LFEmUOZsB8Dg6j-UAKXiS8rON4f_-MCKk0vJemkBgYFhcbjI1rf_hdmoBB0G8cpXTjJmHAaXzF8xtsl4sYyeOI1GJeiFxuHJK19hMwacfkuw")
                    .header("Content-Type", "application/json")
                    .header("Referer", "https://www.cloudbet.com/en/sports/baseball/usa-mlb")
                    .header("x-platform-v2", "desktop")
                    .header("x-channel", "WEB")
                    .header("x-brand", "cloudbet")
                    .header("x-player-timezone", "America/Chicago")
                    .ignoreContentType(true)
                    .maxBodySize(0)
                    .execute();
            return new JSONObject(response.body());
        }catch (Exception e){
            e.printStackTrace();
        }
        return  null;
    }
}
