package com.example.testmovies.api;


import com.example.testmovies.pojo.critic.CriticResponse;
import com.example.testmovies.pojo.review.ReviewResponse;


import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @GET("reviews/search.json???&api-key=ne7vLZIYgiWQymPHQ9rbTwdgmre3exUO")
    Observable<ReviewResponse> getReviews(@Query("offset") int offset, @Query("publication-date") String publicationDate, @Query("query") String query);

    @GET("critics/{reviewer}.json?&api-key=ne7vLZIYgiWQymPHQ9rbTwdgmre3exUO")
    Observable<CriticResponse> getCritics(@Path("reviewer") String reviewer);

    @GET("reviews/search.json?&api-key=ne7vLZIYgiWQymPHQ9rbTwdgmre3exUO")
    Observable<ReviewResponse> getReviewsByCritic(@Query("offset") int offset, @Query("reviewer") String reviewer);
}
