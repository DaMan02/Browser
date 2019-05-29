package com.innovvscript.rinobrowser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.innovvscript.rinobrowser.events.StartPageLoading;
import com.innovvscript.rinobrowser.web.MyWebChromeClient;
import com.innovvscript.rinobrowser.web.MyWebViewClient;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity{

    private WebView webView;
    private WebSettings webSettings;
    private AppCompatEditText searchBar;
    private final String TAG = MainActivity.class.getSimpleName();
    private ImageView secureIcon,urlClearBtn, incognitoIcon,main_menu;
    private ProgressBar loadingProgress;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();             // TODO - remove keep screen on from activity_main parent layout

        loadPage("https://www.google.com");

        handleSearchbar();
        swipeRefreshBehaviour();
        new PrivateMode(webView).privateModeSettings();
    }

    private void swipeRefreshBehaviour() {
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadPage(searchBar.getText().toString()); //TODO
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefresh.setRefreshing(false);
                    }
                },8000);
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void handleSearchbar() {

        searchBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.performClick();
                return false;
            }
        });
        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                loadPage(searchBar.getText().toString());
                return false;
            }
        });
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                toggleClearButton(charSequence);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                urlClearBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        searchBar.setText("");
                    }
                });
                toggleClearButton(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        searchBar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!view.hasFocus()){
//                    urlClearBtn.setVisibility(View.GONE);
                    titleBarUnfocused();

                }
                else{
                    titleBarFocused();
                }
            }
        });

    }

    private void titleBarUnfocused() {
        hideKeyboard();
        incognitoIcon.setVisibility(View.VISIBLE);
        main_menu.setVisibility(View.VISIBLE);
        urlClearBtn.setVisibility(View.GONE);
    }

    private void titleBarFocused() {
        secureIcon.setVisibility(View.GONE);
        incognitoIcon.setVisibility(View.GONE);
        main_menu.setVisibility(View.GONE);
        urlClearBtn.setVisibility(View.VISIBLE);
    }

    void toggleClearButton(CharSequence charSequence){
        if(charSequence.length()>0)
            urlClearBtn.setVisibility(View.VISIBLE);
        else
            urlClearBtn.setVisibility(View.GONE);
    }

    private void loadPage(String searchTerm) {
        if(URLUtil.isValidUrl(searchTerm)) {
            webView.loadUrl(URLUtil.guessUrl(searchTerm));
        }
        else
            webView.loadUrl("https://www.google.co.in/search?q=" + searchTerm);
//        onPageLoading();
    }

    public void hideKeyboard() {
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(webView.getWindowToken(),0);
    }

    private void initViews() {
        searchBar = findViewById(R.id.search_bar);
        secureIcon = findViewById(R.id.secure_icon);
        urlClearBtn = findViewById(R.id.url_clear_id);
        loadingProgress = findViewById(R.id.progressBar);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        incognitoIcon = findViewById(R.id.incognito_icon);
        main_menu = findViewById(R.id.options_menu);
        webView = findViewById(R.id.webview);
        webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setDatabaseEnabled(false);
        webSettings.setUseWideViewPort(true);
        if(Build.VERSION.SDK_INT >= 26)
            webSettings.setSafeBrowsingEnabled(true);

//        TODO
//        allow when a secure origin attempts to load a resource from an insecure origin?
//        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);

//        webSettings.setForceDark(FORCE_DARK_AUTO);  // android Q

        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ONLY);

        webView.setWebViewClient(new MyWebViewClient(this));
        webView.setWebChromeClient(new MyWebChromeClient(this));

        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.blueAccent));
    }

    public void setProgressbar(int progress){
        loadingProgress.setVisibility(View.VISIBLE);

        if(Build.VERSION.SDK_INT >= 24)
            loadingProgress.setProgress(progress,true);
        else
            loadingProgress.setProgress(progress);
    }

    public void onPageLoaded(){
        Log.d(TAG,"onPageLoaded()");
        loadingProgress.setVisibility(View.INVISIBLE);
        swipeRefresh.setRefreshing(false);
    }

    @Override
    public void onBackPressed() {

        if(webView.canGoBack()){
            webView.goBack();
        }
        else  super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStartLoading(StartPageLoading loadingEvent) {
        secureIcon.setVisibility(View.VISIBLE);
        searchBar.setText(loadingEvent.url);
        titleBarUnfocused();
        urlClearBtn.setVisibility(View.GONE);
        if(URLUtil.isHttpsUrl(loadingEvent.url))
            secureIcon.setImageResource(R.drawable.ic_secure);
        else secureIcon.setImageResource(R.drawable.ic_insecure);
    }
}
