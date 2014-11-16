package me.loganrooper.hackdukef2014.linkdetect;

import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.Toast;
import android.view.ViewGroup.LayoutParams;
import android.graphics.Typeface;


public class drawer extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            default:
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.drawer, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_drawer, container, false);
            ExpandableListView elv = (ExpandableListView) rootView.findViewById(R.id.expandableListView);
            elv.setAdapter(new myListAdapter());
            return rootView;
        }

        public class myListAdapter extends BaseExpandableListAdapter {


            //Send get request for room data here.

            //Parse it.

            private String[] groups = { "Classroom 1", "Classroom 2", "Group Study 1"};

            private String[][] children = {
                    { "In use 3 minutes ago.", "+ Reserve Me", "Graph goes here."},
                    { "In use 25 minutes ago.", "+ Reserve Me" },
                    { "In use 25 minutes ago.", "+ Reserve Me" }
            };

            private boolean[] usage = {true, false , false};

            @Override
            public int getGroupCount() {
                return groups.length;
            }

            @Override
            public int getChildrenCount(int i) {
                return children[i].length;
            }

            @Override
            public Object getGroup(int i) {
                return groups[i];
            }

            @Override
            public Object getChild(int i, int i1) {
                return children[i][i1];
            }

            @Override
            public long getGroupId(int i) {
                return i;
            }

            @Override
            public long getChildId(int i, int i1) {
                return i1;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }

            @Override
            public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
                //RelativeLayout layout = new RelativeLayout(PlaceholderFragment.this.getActivity());
                //layout.setGravity(Gravity.LEFT);

                LinearLayout lay = new LinearLayout(PlaceholderFragment.this.getActivity());
                lay.setOrientation(LinearLayout.VERTICAL);


                TextView textView = new TextView(PlaceholderFragment.this.getActivity());
                textView.setText(getGroup(i).toString());
                textView.setTextSize(20);
                textView.setPadding(70, 50, 60, 0);

                //subtitle text
                TextView sub = new TextView(PlaceholderFragment.this.getActivity());
                Typeface font = Typeface.createFromAsset(PlaceholderFragment.this.getActivity().getAssets(), "fontawesome-webfont.ttf" );
                sub.setTypeface(font, Typeface.ITALIC);
                sub.setText("\uF0C0 Unknown status.");
                sub.setPadding(70, 0, 0, 50);
                sub.setTextColor(getResources().getColor(R.color.grey));

                //ImageView iv = new ImageView(PlaceholderFragment.this.getActivity());
                int d = R.drawable.yellow;

                //change pinger icon color
                if (usage[i]) {
                    d = R.drawable.green;
                    sub.setText("\uf046 Available Now");
                } else {
                    d = R.drawable.red;
                    sub.setText("\uf057 Not available.");
                }

                textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, d, 0);

                //linear image layout
                /*
                LinearLayout image_layout = new LinearLayout(PlaceholderFragment.this.getActivity());
                image_layout.addView(iv);
                image_layout.setHorizontalGravity(Gravity.RIGHT);
                image_layout.setOrientation(LinearLayout.HORIZONTAL);

                layout.addView(textView);
                layout.addView(image_layouttex);

                layout.setVerticalGravity(Gravity.CENTER);
                LayoutParams params = layout.getLayoutParams();
                params.height = 100;

                return layout;*/
                lay.addView(textView);
                lay.addView(sub);


                return lay;
            }

            @Override
            public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
                TextView textView = new TextView(PlaceholderFragment.this.getActivity());
                textView.setText(getChild(i, i1).toString());
                textView.setPadding(10, 20, 10, 20);
                return textView;
            }

            @Override
            public boolean isChildSelectable(int i, int i1) {
                return true;
            }
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((drawer) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
