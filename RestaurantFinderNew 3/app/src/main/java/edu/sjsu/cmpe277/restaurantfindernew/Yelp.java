package edu.sjsu.cmpe277.restaurantfindernew;

import android.content.Context;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;


public class Yelp {

  OAuthService service;
  Token accessToken;
  String radiusLimit = "16093"; //10 miles
  String numOfResultsLimit = "20";
  String category = "restaurants";

  public static Yelp getYelp(Context context) {
	  return new Yelp(context.getString(R.string.consumer_key), context.getString(R.string.consumer_secret),
			  context.getString(R.string.token), context.getString(R.string.token_secret));
  }

  public Yelp(String consumerKey, String consumerSecret, String token, String tokenSecret) {
    this.service = new ServiceBuilder().provider(YelpApi.class).apiKey(consumerKey).apiSecret(consumerSecret).build();
    this.accessToken = new Token(token, tokenSecret);
  }

  public String searchLoc(String term, String location) {
    OAuthRequest request = new OAuthRequest(Verb.GET, "http://api.yelp.com/v2/search");
    request.addQuerystringParameter("term", term);
    request.addQuerystringParameter("location", location);
    request.addQuerystringParameter("radius_filer", radiusLimit);
    request.addQuerystringParameter("limit", numOfResultsLimit);
    request.addQuerystringParameter("category_filter", category);
    this.service.signRequest(this.accessToken, request);
    Response response = request.send();
    return response.getBody();
  }

  public String searchLoc(String term, String location, String sort) {
    OAuthRequest request = new OAuthRequest(Verb.GET, "http://api.yelp.com/v2/search");
    request.addQuerystringParameter("term", term);
    request.addQuerystringParameter("location", location);
    request.addQuerystringParameter("radius_filer", radiusLimit);
    request.addQuerystringParameter("limit", numOfResultsLimit);
    request.addQuerystringParameter("sort", sort);
    request.addQuerystringParameter("category_filter", category);
    this.service.signRequest(this.accessToken, request);
    Response response = request.send();
    return response.getBody();
  }

  public String searchLatLng(String term, String latLng) {
    OAuthRequest request = new OAuthRequest(Verb.GET, "http://api.yelp.com/v2/search");
    request.addQuerystringParameter("term", term);
    request.addQuerystringParameter("ll", latLng);
    request.addQuerystringParameter("radius_filer", radiusLimit);
    request.addQuerystringParameter("limit", numOfResultsLimit);
    request.addQuerystringParameter("category_filter", category);
    this.service.signRequest(this.accessToken, request);
    Response response = request.send();
    return response.getBody();
  }

  public String searchLatLng(String term, String latLng, String sort) {
    OAuthRequest request = new OAuthRequest(Verb.GET, "http://api.yelp.com/v2/search");
    request.addQuerystringParameter("term", term);
    request.addQuerystringParameter("ll", latLng);
    request.addQuerystringParameter("radius_filer", radiusLimit);
    request.addQuerystringParameter("limit", numOfResultsLimit);
    request.addQuerystringParameter("sort", sort);
    request.addQuerystringParameter("category_filter", category);
    this.service.signRequest(this.accessToken, request);
    Response response = request.send();
    return response.getBody();
  }


  public String searchBusiness(String id) {
    String requestStr = "http://api.yelp.com/v2/business/"+id;
    OAuthRequest request = new OAuthRequest(Verb.GET, requestStr);
    this.service.signRequest(this.accessToken, request);
    Response response = request.send();
    return response.getBody();
  }


  public static void main(String[] args) {
    String consumerKey = "";
    String consumerSecret = "";
    String token = "";
    String tokenSecret = "";

    Yelp yelp = new Yelp(consumerKey, consumerSecret, token, tokenSecret);
    //String response = yelp.search("burritos", 30.361471, -87.164326);
    //String response = yelp.search("taco","san jose","1");
    String response = yelp.searchBusiness("my-che-san-jose");

    System.out.println(response);
  }
}
