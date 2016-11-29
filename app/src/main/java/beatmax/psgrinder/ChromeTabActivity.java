package beatmax.psgrinder;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;

public class ChromeTabActivity extends AppCompatActivity
{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */


    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private Toolbar mToolbar;
    private EditText mUrlField;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chrome_tab);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setShowHideAnimationEnabled(true);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mUrlField = (EditText) findViewById(R.id.urlField);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setOffscreenPageLimit(5);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageSelected(int position)
            {

            }

            @Override
            public void onPageScrollStateChanged(int state)
            {
                setTitle();
            }

        });

        mToolbar.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                showUrlField();
                return false;
            }
        });

        mUrlField.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    hideUrlField();
                }
            }
        });

        mUrlField.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    mToolbar.setTitle("");
                    getCurrentFragment().loadUrl(mUrlField.getText().toString());
                    View view = ChromeTabActivity.this.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    return true;
                }
                return false;
            }
        });




//        String url = "https://paul.kinlan.me/";
//        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
//        CustomTabsIntent customTabsIntent = builder.build();
//        customTabsIntent.launchUrl(this, Uri.parse(url));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chrome_tab, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void hideToolbar(){
        getSupportActionBar().hide();
    }

    public void showToolbar(){
        getSupportActionBar().show();
    }

    public void setTitle(){
        PlaceholderFragment fragment = getCurrentFragment();
        if(fragment.getSiteTitle()==""){
            mToolbar.setTitle(fragment.getSiteUrl());
        }
        else{
            mToolbar.setTitle(fragment.getSiteTitle());
        }
    }

    public void setUrl(){
        PlaceholderFragment fragment = (PlaceholderFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + mViewPager.getCurrentItem());
        mUrlField.setText(fragment.getSiteUrl());
    }

    public void setTitle(String title){
        mToolbar.setTitle(title);
    }

    public void showUrlField(){
        mUrlField.setVisibility(View.VISIBLE);
        mUrlField.requestFocus();
    }
    public void hideUrlField(){
        mUrlField.setVisibility(View.GONE);
    }

    private PlaceholderFragment getCurrentFragment()
    {
        return (PlaceholderFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + mViewPager.getCurrentItem());
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment
    {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */

        private final String mFavoritesUrl= "https://de.pokerstrategy.com/forum/usercp.php?action=favorites";
        private final String mAllNewThreadsUrl="https://de.pokerstrategy.com/forum/search.php?action=new";
        private final String mHomeUrl="https://de.pokerstrategy.com/home/";

        private static final String ARG_SECTION_NUMBER = "section_number";
        private ObservableWebView mWebView;

        private int mSCrollCounter = 0;
        private int mDownCount = 0;
        private int mUpCount = 0;

        private String mSiteTitle = "";
        private String mSiteUrl = "";

        private ChromeTabActivity mParentActivity;


        public PlaceholderFragment()
        {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber)
        {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState)
        {
            View rootView = inflater.inflate(R.layout.fragment_chrome_tab, container, false);
//            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
//            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));

            int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);

            mParentActivity = (ChromeTabActivity) getActivity();


            if(sectionNumber == 1){
                mSiteUrl = mFavoritesUrl;
            }
            if (sectionNumber == 2){
                mSiteUrl = mAllNewThreadsUrl;
            }
            if (sectionNumber == 3){
                mSiteUrl = mHomeUrl;
            }

            mParentActivity.setTitle();
            mParentActivity.setUrl();


            mWebView = (ObservableWebView) rootView.findViewById(R.id.webView);
            // Enable javascript
            mWebView.getSettings().setJavaScriptEnabled(true);

            mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

            // Set WebView client
            mWebView.setWebChromeClient(new WebChromeClient());

            mWebView.setWebViewClient(new WebViewClient() {

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    mSiteTitle = view.getTitle();
                    mParentActivity.setTitle();
                }


            });

            // Load the webpage
            mWebView.loadUrl(mSiteUrl);

            mWebView.setOnKeyListener(new View.OnKeyListener()
            {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event)
                {
                    if(event.getAction() == KeyEvent.ACTION_DOWN)
                    {
                        WebView webView = (WebView) v;

                        switch(keyCode)
                        {
                            case KeyEvent.KEYCODE_BACK:
                                if(webView.canGoBack())
                                {
                                    webView.goBack();
                                    return true;
                                }
                                break;
                        }
                    }

                    return false;
                }
            });

            mWebView.setOnScrollChangedCallback(new ObservableWebView.OnScrollChangedCallback()
            {
                @Override
                public void onScroll(int l, int t)
                {
                    int scrollThreshold = 16;

                    if(t < mSCrollCounter ){
                        mDownCount++;
                        mUpCount = 0;
                    }
                    else if (t >mSCrollCounter){
                        mUpCount++;
                        mDownCount = 0;
                    }

                    if(mDownCount > scrollThreshold || t == 0){
                        mParentActivity.showToolbar();
                    }

                    if(mUpCount > scrollThreshold){
                        mParentActivity.hideToolbar();
                    }

                    mSCrollCounter = t;
                }
            });

            FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
            fab.setAlpha(0.25f);
            fab.setBackgroundColor(Color.TRANSPARENT);
            fab.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    mWebView.loadUrl(mWebView.getUrl());
                    mWebView.reload();

//                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();
                }
            });




            return rootView;
        }

        @Override
        public void onResume()
        {
            super.onResume();
        }

        @Override
        public void onSaveInstanceState(Bundle outState)
        {
            super.onSaveInstanceState(outState);

            mWebView.saveState(outState);
        }

        @Override
        public void onViewStateRestored(@Nullable Bundle savedInstanceState)
        {
            super.onViewStateRestored(savedInstanceState);
            mWebView.restoreState(savedInstanceState);
        }

        public String getSiteTitle(){
            return mSiteTitle;
        }
        public String getSiteUrl(){
            return mSiteUrl;
        }


        public void loadUrl(String url)
        {
            mWebView.loadUrl(url);
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter
    {

        public SectionsPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount()
        {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }


}
