package com.example.lab6;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.OnBackPressedCallback;

import com.bumptech.glide.Glide;

import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private final String API_KEY = "Enter your Key";
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText editText = findViewById(R.id.editTextMovie);
        Button buttonSearch = findViewById(R.id.buttonSearch);
        Button buttonTrailer = findViewById(R.id.buttonTrailer);
        ImageView poster = findViewById(R.id.imagePoster);
        ImageView backdrop = findViewById(R.id.imageBackdrop);

        TextView labelTitle = findViewById(R.id.labelTitle);
        TextView labelOriginalTitle = findViewById(R.id.labelOriginalTitle);
        TextView labelRating = findViewById(R.id.labelRating);
        TextView labelRelease = findViewById(R.id.labelRelease);
        TextView labelLanguage = findViewById(R.id.labelLanguage);
        TextView labelGenres = findViewById(R.id.labelGenres);
        TextView labelRuntime = findViewById(R.id.labelRuntime);
        TextView labelTagline = findViewById(R.id.labelTagline);

        TextView valueTitle = findViewById(R.id.valueTitle);
        TextView valueOriginalTitle = findViewById(R.id.valueOriginalTitle);
        TextView valueRating = findViewById(R.id.valueRating);
        TextView valueRelease = findViewById(R.id.valueRelease);
        TextView valueLanguage = findViewById(R.id.valueLanguage);
        TextView valueGenres = findViewById(R.id.valueGenres);
        TextView valueRuntime = findViewById(R.id.valueRuntime);
        TextView valueTagline = findViewById(R.id.valueTagline);

        TextView overview = findViewById(R.id.textOverview);
        View overviewContainer = findViewById(R.id.overviewContainer);
        View backdropContainer = findViewById(R.id.backdropContainer);

        webView = findViewById(R.id.webView);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService api = retrofit.create(ApiService.class);

        poster.setVisibility(View.GONE);
        backdrop.setVisibility(View.GONE);
        buttonTrailer.setVisibility(View.GONE);

        labelTitle.setVisibility(View.GONE);
        labelOriginalTitle.setVisibility(View.GONE);
        labelRating.setVisibility(View.GONE);
        labelRelease.setVisibility(View.GONE);
        labelLanguage.setVisibility(View.GONE);
        labelGenres.setVisibility(View.GONE);
        labelRuntime.setVisibility(View.GONE);
        labelTagline.setVisibility(View.GONE);

        valueTitle.setVisibility(View.GONE);
        valueOriginalTitle.setVisibility(View.GONE);
        valueRating.setVisibility(View.GONE);
        valueRelease.setVisibility(View.GONE);
        valueLanguage.setVisibility(View.GONE);
        valueGenres.setVisibility(View.GONE);
        valueRuntime.setVisibility(View.GONE);
        valueTagline.setVisibility(View.GONE);

        overviewContainer.setVisibility(View.GONE);

        buttonSearch.setOnClickListener(v -> {

            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            String query = editText.getText().toString().trim();
            if (query.isEmpty()) {
                Toast.makeText(this, "Enter film name", Toast.LENGTH_SHORT).show();
                return;
            }

            api.searchMovie(API_KEY, query).enqueue(new Callback<MovieResponse>() {
                @Override
                public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                    if (response.body() != null && !response.body().results.isEmpty()) {

                        Movie movie = response.body().results.get(0);

                        poster.setVisibility(View.VISIBLE);
                        backdrop.setVisibility(View.VISIBLE);
                        buttonTrailer.setVisibility(View.VISIBLE);
                        backdropContainer.setVisibility(View.VISIBLE);

                        labelTitle.setVisibility(View.VISIBLE);
                        labelOriginalTitle.setVisibility(View.VISIBLE);
                        labelRating.setVisibility(View.VISIBLE);
                        labelRelease.setVisibility(View.VISIBLE);
                        labelLanguage.setVisibility(View.VISIBLE);
                        labelGenres.setVisibility(View.VISIBLE);
                        labelRuntime.setVisibility(View.VISIBLE);
                        labelTagline.setVisibility(View.VISIBLE);

                        valueTitle.setVisibility(View.VISIBLE);
                        valueOriginalTitle.setVisibility(View.VISIBLE);
                        valueRating.setVisibility(View.VISIBLE);
                        valueRelease.setVisibility(View.VISIBLE);
                        valueLanguage.setVisibility(View.VISIBLE);
                        valueGenres.setVisibility(View.VISIBLE);
                        valueRuntime.setVisibility(View.VISIBLE);
                        valueTagline.setVisibility(View.VISIBLE);

                        overviewContainer.setVisibility(View.VISIBLE);

                        valueTitle.setText(movie.title);
                        valueOriginalTitle.setText(movie.original_title);
                        valueRating.setText(String.valueOf(movie.vote_average));
                        valueRelease.setText(movie.release_date);
                        valueLanguage.setText(movie.original_language);
                        overview.setText(movie.overview);

                        if (movie.poster_path != null) {
                            Glide.with(MainActivity.this)
                                    .load("https://image.tmdb.org/t/p/w500" + movie.poster_path)
                                    .into(poster);
                        }

                        if (movie.backdrop_path != null) {
                            Glide.with(MainActivity.this)
                                    .load("https://image.tmdb.org/t/p/w780" + movie.backdrop_path)
                                    .into(backdrop);
                        }

                        buttonTrailer.setOnClickListener(v1 -> {
                            webView.setVisibility(View.VISIBLE);
                            String url = "https://www.youtube.com/results?search_query=" + movie.title + "+trailer";
                            webView.loadUrl(url);
                        });

                        api.getMovieDetails(movie.id, API_KEY, "videos").enqueue(new Callback<Movie>() {
                            @Override
                            public void onResponse(Call<Movie> call, Response<Movie> response2) {
                                Movie details = response2.body();

                                if (details != null) {

                                    valueRuntime.setText(details.runtime + " mins");
                                    valueTagline.setText(details.tagline);

                                    if (details.genres != null && !details.genres.isEmpty()) {
                                        StringBuilder genresStr = new StringBuilder();
                                        for (int i = 0; i < details.genres.size(); i++) {
                                            genresStr.append(details.genres.get(i).name);
                                            if (i < details.genres.size() - 1) {
                                                genresStr.append(", ");
                                            }
                                        }
                                        valueGenres.setText(genresStr.toString());
                                    } else {
                                        valueGenres.setText("Unknown");
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<Movie> call, Throwable t) {
                                t.printStackTrace();
                            }
                        });

                    } else {
                        Toast.makeText(MainActivity.this, "Film not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<MovieResponse> call, Throwable t) {
                    t.printStackTrace();
                    Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        this.getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (webView.getVisibility() == View.VISIBLE && webView.canGoBack()) {
                    webView.goBack();
                } else if (webView.getVisibility() == View.VISIBLE) {
                    webView.setVisibility(View.GONE);
                } else {
                    setEnabled(false);
                    MainActivity.super.onBackPressed();
                }
            }
        });
    }
}