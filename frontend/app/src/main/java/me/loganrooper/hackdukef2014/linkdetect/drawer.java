package me.loganrooper.hackdukef2014.linkdetect;

import android.app.Activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.*;

import android.os.StrictMode;

public class drawer extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    //font awesome
    private static Typeface font;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_drawer);
        //Font awesome!
        font = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf" );

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
            case 3:
                mTitle = "About";
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
            final ExpandableListView elv = (ExpandableListView) rootView.findViewById(R.id.expandableListView);
            elv.setAdapter(new myListAdapter());

            //kill other open views
            elv.setOnGroupExpandListener(new OnGroupExpandListener() {
                int previousGroup = -1;

                @Override
                public void onGroupExpand(int groupPosition) {
                    if (groupPosition != previousGroup)
                        elv.collapseGroup(previousGroup);
                    previousGroup = groupPosition;
                }
            });

            elv.setDividerHeight(1);

            elv.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                    if (parent.isGroupExpanded(groupPosition)) {
                        parent.collapseGroup(groupPosition);
                    } else {
                        boolean animateExpansion = false;
                        parent.expandGroup(groupPosition, animateExpansion);
                    }
                    //telling the listView we have handled the group click, and don't want the default actions.
                    return true;
                }
            });

            return rootView;
        }

        public class myListAdapter extends BaseExpandableListAdapter {
            private String[] groups;
            //private String[] groups = { "Classroom 1", "Classroom 2", "Group Study 1"};
            //private Boolean[][] children;
            private String[][] children;
            private boolean[] usage;

            private ArrayList<String> names = new ArrayList<String>();
            private ArrayList<Boolean> active = new ArrayList<Boolean>();

             myListAdapter() {
                //Send get request for room data here.
                HttpClient httpclient = new DefaultHttpClient();
                HttpGet httpget = new HttpGet("http://hackduke.my.to/client.php?request=rooms&department=1");
                String result = "";
                 //THIS IS REALLY BAD!
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                try
                {
                    HttpResponse response = httpclient.execute(httpget);
                    HttpEntity entity = response.getEntity();

                    if (entity != null) {
                        InputStream inputstream = entity.getContent();
                        BufferedReader bufferedreader =
                                new BufferedReader(new InputStreamReader(inputstream));
                        StringBuilder stringbuilder = new StringBuilder();

                        String currentline = null;
                        while ((currentline = bufferedreader.readLine()) != null) {
                            stringbuilder.append(currentline + "\n");
                        }
                        result = stringbuilder.toString();
                        //Log.v("HTTP REQUEST", result);
                        inputstream.close();

                        //Parse it.
                        //Rooms: iter through json
                        JSONObject jObject  = new JSONObject(result);
                        JSONArray namez = jObject.getJSONArray("rooms");


                        usage = new boolean[namez.length()];
                        groups = new String[namez.length()];
                        children = new String[namez.length()][8]; //+reserv.length()

                        for (int i = 0; i < namez.length(); i++) {
                            JSONObject a =  namez.getJSONObject(i);
                            JSONArray reserv = a.getJSONArray("reservations");

                            for (int j = 1; j < 4 ; j++) {
                                if (j < reserv.length()) {
                                    children[i][j+4] = reserv.getJSONObject(j).getString("time") + " " + reserv.getJSONObject(j).getString("message");
                                } else {
                                    children[i][j+4] = "null";
                                }

                            }

                            groups[i] = a.getString("name");
                            usage[i] = a.getBoolean("occupied");
                            children[i][0] = a.getString("description");
                            children[i][1] = "\uf1ae Capacity: " + a.getString("capacity");
                            children[i][2] = "\uf0fe Register Now";
                            children[i][3] = "histogram";
                            children[i][4] = reserv.getJSONObject(0).getString("message");


                        }


                    }
                } catch(Exception e) {
                    e.printStackTrace();

                    //defaults
                    groups = new String[] {e.toString()};
                    children = new String[][] {{"Nope"}};
                    usage = new boolean[] {true};
                }
             }

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
                textView.setPadding(90, 50, 60, 0);

                //subtitle text
                TextView sub = new TextView(PlaceholderFragment.this.getActivity());
                sub.setTypeface(font, Typeface.ITALIC);
                sub.setText("\uf196 Unknown status.");
                sub.setPadding(90, 0, 0, 50);
                sub.setTextColor(getResources().getColor(R.color.grey));

                //ImageView iv = new ImageView(PlaceholderFragment.this.getActivity());
                int d = R.drawable.yellow;

                //change pinger icon color
                if (!usage[i]) {
                    d = R.drawable.green;
                    sub.setText("\uf046 Available Now");
                } else {
                    d = R.drawable.red;
                    sub.setText("\uf057 Not Available.");
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
                ImageView iv = new ImageView(PlaceholderFragment.this.getActivity());

                textView.setText(getChild(i, i1).toString());
                textView.setBackgroundColor(getResources().getColor(R.color.background_grey));
                textView.setPadding(10, 20, 10, 20);
                textView.setTypeface(font);

                if (i1 == 4) {
                    textView.setTypeface(font, Typeface.ITALIC);
                }

                if (i1 >= 5) {
                    if (!getChild(i, i1).equals("null")) {
                        textView.setBackground(getResources().getDrawable(R.drawable.backgroundtile));
                        textView.setHeight(80);
                    }
                }

                if (getChild(i, i1).equals("null")) {
                    textView.setHeight(0);
                }

                Random rand = new Random();
                if (getChild(i, i1).equals("histogram")) {
                    int randomNum = rand.nextInt((2 - 0) + 1) + 0;
                    if (randomNum == 0) {
                        iv.setImageResource(R.drawable.androidplot);
                    } else if (randomNum == 1) {
                        iv.setImageResource(R.drawable.androidplot1);
                    } else {
                        iv.setImageResource(R.drawable.androidplot2);
                    }


                    iv.setPadding(0, -50, 0, -50);

                    return iv;
                }



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
