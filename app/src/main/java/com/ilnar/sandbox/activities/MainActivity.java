package com.ilnar.sandbox.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ilnar.sandbox.DictionaryAdapter;
import com.ilnar.sandbox.Util.DividerItemDecoration;
import com.ilnar.sandbox.Util.DownloadService;
import com.ilnar.sandbox.R;
import com.ilnar.sandbox.Util.KeyboardListener;
import com.ilnar.sandbox.Util.Utils;
import com.ilnar.sandbox.database.RecentQueryDBHelper;
import com.ilnar.sandbox.dictionary.Dictionary;
import com.ilnar.sandbox.dictionary.DictionaryRecord;
import com.ilnar.sandbox.dictionary.Trie;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DictionaryAdapter dictionaryAdapter;
    private List<DictionaryRecord> list;
    private Dictionary dictionary;
    private RecentQueryDBHelper recentQuery;
    private CharSequence query;
    private SearchView searchView;
    private MenuItem searchItem;
    private ProgressBar searchingProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.init(this);

        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);

        recentQuery = RecentQueryDBHelper.getInstance(getApplicationContext());
        dictionary = new Trie(Utils.getDictionaryFile(true));

        list = new ArrayList<>();

        RecyclerView.LayoutManager layoutManager =  new LinearLayoutManager(getApplicationContext());
        dictionaryAdapter = new DictionaryAdapter(list);
        searchingProgressBar = (ProgressBar) findViewById(R.id.searching_progress_bar);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(dictionaryAdapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
            recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
                @Override
                public void onClick(View v, int position) {
                    DictionaryRecord record = list.get(position);
                    Intent intent = new Intent(MainActivity.this, TranslationActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("word", record.getWord());
                    bundle.putString("translation", record.getTranslation());
                    intent.putExtras(bundle);
                    recentQuery.saveRecentQuery(record);
                    startActivity(intent);
                }

                @Override
                public void onLongClick(View v, int position) {

                }
            }));
        }
        handleIntent(getIntent());
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
        updateRecyclerView(null);
    }

    private void updateRecyclerView(final String query) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                list.clear();
                searchingProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected Void doInBackground(Void... params) {
                if (TextUtils.isEmpty(query)) {
                    list.addAll(recentQuery.getRecentQueries());
                } else {
                    list.addAll(dictionary.search(query));
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                searchingProgressBar.setVisibility(View.INVISIBLE);
                dictionaryAdapter.notifyDataSetChanged();
                TextView nothingFound = (TextView) findViewById(R.id.no_search_history);
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
        }.execute();
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
        MenuItem update = menu.findItem(R.id.update);
        update.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.d(TAG, "update clicker");
                new DownloadService(MainActivity.this).execute();
                return true;
            }
        });

        MenuItem clearHistory = menu.findItem(R.id.clear_history);
        clearHistory.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                recentQuery.clearHistory();
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
    public void setDictionary(Dictionary dictionary) {
        this.dictionary = dictionary;
        updateRecyclerView(null);
    }

    private static final String TAG = "MainActivity";
}
