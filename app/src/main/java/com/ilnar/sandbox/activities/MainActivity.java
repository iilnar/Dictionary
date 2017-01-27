package com.ilnar.sandbox.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ilnar.sandbox.DictionaryAdapter;
import com.ilnar.sandbox.Recorder;
import com.ilnar.sandbox.Util.DividerItemDecoration;
import com.ilnar.sandbox.R;
import com.ilnar.sandbox.Util.KeyboardListener;
import com.ilnar.sandbox.Util.Utils;
import com.ilnar.sandbox.database.RecentQueryDBHelper;
import com.ilnar.sandbox.dictionary.Dictionary;
import com.ilnar.sandbox.dictionary.DictionaryRecord;
import com.ilnar.sandbox.dictionary.ListDictionary;
import com.ilnar.sandbox.dictionary.Trie;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private CharSequence query;
    private SearchView searchView;
    private MenuItem searchItem;

    private Recorder recorder;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.init(this);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a cFragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        handleIntent(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(TAG, String.valueOf(newConfig.keyboardHidden));
        super.onConfigurationChanged(newConfig);
    }

    private void handleIntent(Intent intent) {
        Log.d(TAG + "handle", intent.getAction());
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.d("handleIntent", query);
            updateRecyclerView(query);
            return;
        }
        if (Intent.ACTION_SEND.equals(intent.getAction())) {
            if (intent.getType().equals("text/plain")) {
                query = intent.getStringExtra(Intent.EXTRA_TEXT);
                setSearchView(query);
                return;
            }
        }
    }

    private void updateRecyclerView(String query) {
        mSectionsPagerAdapter.provideSearch(query);
    }

    private void setSearchView(CharSequence query) {
        if (!TextUtils.isEmpty(query)) {
            if (searchItem != null && searchView != null) {
                searchItem.expandActionView();
                searchView.setQuery(query, true);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.search_menu, menu);
        searchItem = menu.findItem(R.id.search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        if (searchManager != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }
        searchView.getViewTreeObserver().addOnGlobalLayoutListener(new KeyboardListener(findViewById(R.id.llFooter),
                (EditText)searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text)));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("submit", query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("change", newText);
                updateRecyclerView(newText);
                return false;
            }
        });
        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                Log.d(TAG, "expand");
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                Log.d(TAG, "collapse");
                updateRecyclerView(null);
                return true;
            }
        });
        setSearchView(query);

        MenuItem clearHistory = menu.findItem(R.id.clear_history);
        clearHistory.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
//                recentQuery.clearHistory(); TODO clear history
                updateRecyclerView(null);
                return true;
            }
        });

        MenuItem addTranslation = menu.findItem(R.id.add_translation);
        addTranslation.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(MainActivity.this, AddTranslationActivity.class);
                startActivity(intent);
                return true;
            }
        });

        MenuItem recognizer = menu.findItem(R.id.mic_button);
        recognizer.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (recorder == null) {
                    recorder = new Recorder() {
                        @Override
                        public void onDone(String result) {
                            Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
                            updateRecyclerView(result);
                        }
                    };
                } else {
                    recorder.stop();
                    recorder = null;
                }
                return true;
            }
        });

        MenuItem showKeyboardCheckbox = menu.findItem(R.id.show_keyboard_checkbox);
        final SharedPreferences preferences = getSharedPreferences("preferences", MODE_PRIVATE);
        boolean isChecked = preferences.getBoolean("showKeyboard", true);
        showKeyboardCheckbox.setChecked(isChecked);
        showKeyboardCheckbox.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                item.setChecked(!item.isChecked());
                preferences.edit().putBoolean("showKeyboard", item.isChecked()).apply();
                return true;
            }
        });

        return true;
    }

    public interface ClickListener {
        void onClick(View v, int position);
        void onLongClick(View v, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
        private GestureDetector gestureDetector;
        private ClickListener clickListener;
        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
            this.clickListener = clickListener;
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }

    }

    /**
     * A placeholder cFragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        private DictionaryAdapter dictionaryAdapter;
        private List<DictionaryRecord> list;
        private Dictionary dictionary;
        private ProgressBar searchingProgressBar;
        private TextView nothingFound;
        private RecentQueryDBHelper recentQuery;

        public int sectionNumber;

        /**
         * The cFragment argument representing the section number for this
         * cFragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this cFragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            Log.d(TAG, "created " + sectionNumber);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_content, container, false);
            return rootView;
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            searchingProgressBar = (ProgressBar) view.findViewById(R.id.searching_progress_bar);
            nothingFound = (TextView) view.findViewById(R.id.no_search_history);

            sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            dictionary = new Trie(Utils.getDictionaryFile(sectionNumber, true));
            list = new ArrayList<>();

            Log.d(TAG, "onViewCreated: " + sectionNumber);

            recentQuery = RecentQueryDBHelper.getInstance(getContext());

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
            dictionaryAdapter = new DictionaryAdapter(list);

            if (recyclerView != null) {
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(dictionaryAdapter);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
                recyclerView.addOnItemTouchListener(new MainActivity.RecyclerTouchListener(getContext(), recyclerView, new MainActivity.ClickListener() {
                    @Override
                    public void onClick(View v, int position) {
                        DictionaryRecord record = list.get(position);
                        Intent intent = new Intent(getActivity(), TranslationActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("word", record.getWord());
                        bundle.putStringArray("translation", record.getTranslation());
                        intent.putExtras(bundle);
//                        recentQuery.saveRecentQuery(record);
                        startActivity(intent);
                    }

                    @Override
                    public void onLongClick(View v, int position) {

                    }
                }));
            }
            updateRecyclerView("");
            super.onViewCreated(view, savedInstanceState);
        }

        private void updateRecyclerView(String query) {
            Log.d(TAG, "Called query=" + query + " time=" + System.currentTimeMillis());
            new AsyncTask<String, Void, List<DictionaryRecord>>() {
                private String query;

                @Override
                protected void onPreExecute() {
                    list.clear();
                    searchingProgressBar.setVisibility(View.VISIBLE);
                }

                @Override
                protected List<DictionaryRecord> doInBackground(String... params) {
                    query = params.length == 0 ? null : params[0];
//                    if (TextUtils.isEmpty(query)) {
//                        return recentQuery.getRecentQueries();
//                    } else {
                    return dictionary.search(query);
//                    }
                }

                @Override
                protected void onPostExecute(List<DictionaryRecord> results) {
                    list.addAll(results);
                    searchingProgressBar.setVisibility(View.INVISIBLE);
                    dictionaryAdapter.notifyDataSetChanged();

                    if (nothingFound != null) {
                        nothingFound.setText(getString(TextUtils.isEmpty(query) ? R.string.no_search_history : R.string.no_search_result));
                    }
                    if (nothingFound != null) {
                        if (list.isEmpty()) {
                            nothingFound.setVisibility(View.VISIBLE);
                        } else {
                            nothingFound.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }.execute(query);
        }

        private static final String TAG = "PlaceholderFragment";
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a cFragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private PlaceholderFragment[] fragments;
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            fragments = new PlaceholderFragment[getCount()];
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the cFragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if (fragments[position] == null) {
                fragments[position] = PlaceholderFragment.newInstance(position);
            }
            return fragments[position];
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "тат-рус";
                case 1:
                    return "рус-тат";
            }
            return null;
        }

        public void provideSearch(String query) {
            for (int i = 0; i < getCount(); i++) {
                fragments[i].updateRecyclerView(query);
            }
        }
    }

    private static final String TAG = "MainActivity";
}
