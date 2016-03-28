package app.com.phamsang.wftwitter;

import android.content.Context;
import android.util.Log;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

/**
 * Created by Quang Quang on 3/25/2016.
 */
public class TwitterClient extends OAuthBaseClient {
    public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class;
    public static final String REST_URL = "https://api.twitter.com/1.1";
    public static final String REST_CONSUMER_KEY = "JVu9T3URIJuP0CYszvYuaC1Jp";
    public static final String REST_CONSUMER_SECRET = "ofUXK5fbQ1hMh8JMr26MEXU2aYEqMUTKO529ysMSNDb2WSpDLV";
    public static final String REST_CALLBACK_URL = "x-oauthflow-twitter://phamsang.com";

    private static TwitterClient sClient;
    public TwitterClient(Context context) {
        super(context, REST_API_CLASS, REST_URL,
                REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
    }

    // ENDPOINTS BELOW

    public void getHomeTimeline(long sinceId, long maxId, int count, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/home_timeline.json");
        RequestParams params = new RequestParams();
        if(sinceId > 0){
            params.put("since_id",String.valueOf(sinceId));
        }
        if(maxId > 0){
            params.put("max_id",String.valueOf(maxId));
        }
        client.get(apiUrl, params, handler);
    }

    //UPDATE TWEET statuses/update.json

    public void updateStatus(String status, long replyTo, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();
        if(status.length()>140){
            status = status.substring(0,139);
        }

        params.put("status",status);
        if(replyTo>0){
            params.put("in_reply_to_status_id",replyTo);
        }
        client.post(apiUrl, params, handler);
    }

    public void reTweet(long id, AsyncHttpResponseHandler handler){
        String apiUrl = getApiUrl("statuses/retweet/holderID.json");
        String url = apiUrl.replaceAll("holderID",String.valueOf(id));
        Log.d(TwitterClient.class.getSimpleName(),url);
        RequestParams params = new RequestParams();
        params.put("trim_user",1);
        client.post(url,params,handler);
    }
    public void unRetweet(long id, AsyncHttpResponseHandler handler){
        String apiUrl = getApiUrl("statuses/unretweet/holderID.json");
        String url = apiUrl.replaceAll("holderID",String.valueOf(id));
        Log.d(TwitterClient.class.getSimpleName(),url);
        RequestParams params = new RequestParams();
        params.put("trim_user",1);
        client.post(url,params,handler);
    }

    public void like(long id, AsyncHttpResponseHandler handler){
        String apiUrl = getApiUrl("favorites/create.json");
        RequestParams params = new RequestParams();
        params.put("id",String.valueOf(id));
        params.put("include_entities",false);
        client.post(apiUrl,params,handler);
    }

    public void unLike(long id, AsyncHttpResponseHandler handler){
        String apiUrl = getApiUrl("favorites/destroy.json");
        RequestParams params = new RequestParams();
        params.put("id",String.valueOf(id));
        params.put("include_entities",false);
        client.post(apiUrl,params,handler);
    }

    public void getAccount(AsyncHttpResponseHandler handler){
        String apiUrl = getApiUrl("account/verify_credentials.json");
        RequestParams params = new RequestParams();
        params.put("skip_status",String.valueOf(true));
        params.put("include_entities",false);
        client.get(apiUrl,params,handler);
    }

    public static TwitterClient getInstance(Context c){
        if(sClient==null){
            sClient = (TwitterClient)TwitterClient.getInstance(TwitterClient.class,c);
        }
        return sClient;
    }

}
