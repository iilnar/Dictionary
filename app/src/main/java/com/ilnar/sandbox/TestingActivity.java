package com.ilnar.sandbox;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ProgressBar;
import android.widget.TextView;

import com.ilnar.sandbox.Util.DividerItemDecoration;
import com.ilnar.sandbox.Util.Utils;
import com.ilnar.sandbox.activities.MainActivity;
import com.ilnar.sandbox.activities.TranslationActivity;
import com.ilnar.sandbox.dictionary.Dictionary;
import com.ilnar.sandbox.dictionary.DictionaryRecord;
import com.ilnar.sandbox.dictionary.Trie;

import java.util.ArrayList;
import java.util.List;

public class TestingActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded cFragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.init(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);
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
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder cFragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        private DictionaryAdapter dictionaryAdapter;
        private List<DictionaryRecord> list;
        private Dictionary dictionary;
        private ProgressBar searchingProgressBar;

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


            this.sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            dictionary = new Trie(Utils.getDictionaryFile(sectionNumber, true));
            list = new ArrayList<>();

            Log.d(TAG, "onViewCreated: " + sectionNumber);

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
                    TextView nothingFound = (TextView) getActivity().findViewById(R.id.no_search_history);
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
        private PlaceholderFragment cFragment;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public PlaceholderFragment getcFragment() {
            return cFragment;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the cFragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position);
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
                    return "рус-тат";
                case 1:
                    return "тат-рус";
            }
            return null;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            if (getcFragment() != object) {
                cFragment = (PlaceholderFragment)object;
            }
            super.setPrimaryItem(container, position, object);
        }
    }
}
