package edu.temple.tuhub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import java.io.Serializable;

import edu.temple.tuhub.models.Course;
import edu.temple.tuhub.models.Entry;
import edu.temple.tuhub.models.Grade;
import edu.temple.tuhub.models.Newsitem;

public class MainActivity extends AppCompatActivity implements NewsTableFragment.newsshow, NewsTableFragment.filterbutton, FilterMenuFrag.selectorinterface, CourseFragment.showCourseDetails, CourseListFragment.OnListFragmentInteractionListener, CourseCalendarFragment.CalendarClickListener, CourseFragment.courseSearchHandler, CoursesSearchAllFragment.searchAllResultsInterface, MarketTableFragment.newListingInterface{
    static Fragment[] fraghold = new Fragment[3];//For TUNews and some TUmarketplace
    FilterMenuFrag tufilter;//For TUNews

    FragmentManager manage ;//For TUNews
    FragmentTransaction transact;//For TUNews

    CourseFragment cf;
    CourseSearchAllDetail csad;
    CoursesSearchAllFragment csaf;
    MyCourseSearchFragment mcsf;
    CourseDetailsFragment cdf;
    MapsFragment mf;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_courses:
                    loadFragment(R.id.contentFragment, cf.newInstance(), false, true);
                    return true;
                case R.id.navigation_marketplace:
                    loadFragment(R.id.contentFragment, fraghold[2], false, true);
                    return true;
                case R.id.navigation_maps:
                    loadFragment(R.id.contentFragment, mf, false, true);
                    return true;
                case R.id.navigation_news:
                    loadFragment(R.id.contentFragment, fraghold[1], false, true);
                    return true;
                case R.id.navigation_more:
                    // TODO: Load fragment
                    return true;
            }
            return false;
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getFragmentManager().addOnBackStackChangedListener(backStackChangedListener);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //Retrieve login info and store in SharedPreferences
            Intent receivedIntent = getIntent();
            String username = receivedIntent.getStringExtra(getResources().getString(R.string.username_key));
            String password = receivedIntent.getStringExtra(getResources().getString(R.string.password_key));
            String id = receivedIntent.getStringExtra(getResources().getString(R.string.user_id_key));

            SharedPreferences preferences = getPreferences(MODE_PRIVATE);
            preferences.edit().putString(getResources().getString(R.string.username_key), username).apply();
            preferences.edit().putString(getResources().getString(R.string.password_key), password).apply();
            preferences.edit().putString(getResources().getString(R.string.user_id_key), id).apply();

        csad = new CourseSearchAllDetail();
        csaf = new CoursesSearchAllFragment();
        mcsf = new MyCourseSearchFragment();
        cdf = new CourseDetailsFragment();
        cf = new CourseFragment();
        mf = new MapsFragment();

        if(savedInstanceState==null) {
            int selectedID = navigation.getSelectedItemId();
            View selectedView = navigation.findViewById(selectedID);
            selectedView.performClick();
        }

        if(fraghold[1] == null)//For TUNews

        {
            fraghold[0]= new NewsFragment();
            fraghold[1] = new NewsTableFragment();
            fraghold[2] = new MarketTableFragment();

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getFragmentManager().removeOnBackStackChangedListener(backStackChangedListener);
    }

    private void loadFragment(int ID, Fragment fragment, boolean backStack, boolean clearBackStack) {
        final FragmentManager fm = getFragmentManager();

        FragmentTransaction ft = fm.beginTransaction().detach(cf).replace(ID, fragment).attach(fragment);
        if(fraghold[2]!=null){
        if(fraghold[2].equals(fragment)){
            if(cf!=null) {
                getFragmentManager().beginTransaction().remove(cf).commit();
            }
        }}

        if(clearBackStack){
             fm.popBackStack();

        }
        if (backStack) {
            ft.addToBackStack(null);
        }
        ft.commit();

        fm.executePendingTransactions();
    }
    private FragmentManager.OnBackStackChangedListener backStackChangedListener = new FragmentManager.OnBackStackChangedListener() {
        @Override
        public void onBackStackChanged() {
            if(getFragmentManager().getBackStackEntryCount()==0) {
                if(cf!=null) {
                    getFragmentManager().beginTransaction().remove(cf).commit();
                }
            }
        }
    };

    @Override
    public void onListFragmentInteraction(Course item) {
        // TODO: Do something

    }

    @Override
    public void showCourseDetails(Course course) {
        Bundle bundle = new Bundle();
        bundle.putString("name", course.getName());
        bundle.putString("fullname",course.getTitle());
        bundle.putString("date", course.getStartDate()+" to "+course.getEndDate());
        bundle.putSerializable("course", (Serializable) course);
        bundle.putSerializable("meetings", (Serializable) course.getMeetings());
        bundle.putSerializable("faculty", (Serializable) course.getInstructors());
        bundle.putStringArray("roster", course.getRoster());

        cdf.setArguments(bundle);
        loadFragment(R.id.contentFragment, cdf, true, false);

    }

    @Override
    public void searchAllResultsInterface(Entry courseDetails) {
        Bundle bundle = new Bundle();
        bundle.putString("course", courseDetails.getCourse());
        bundle.putString("crseId", courseDetails.getCrseId());
        bundle.putString("description", courseDetails.getDescription());
        bundle.putString("creditHr", courseDetails.getCreditHr());
        bundle.putString("college", courseDetails.getCollege());
        bundle.putString("division", courseDetails.getDivision());
        bundle.putString("department", courseDetails.getDepartment());
        bundle.putString("schedule", courseDetails.getSchedule());
        csad.setArguments(bundle);
        loadFragment(R.id.contentFragment, csad, true, false);
    }

    @Override
    public void courseSearchHandler(String query, boolean myCourses) {
        if(myCourses){
            loadFragment(R.id.contentFragment,mcsf,true,false);
        }else {
            Bundle bundle = new Bundle();
            bundle.putString("query", query);
            csaf.setArguments(bundle);
            loadFragment(R.id.contentFragment, csaf, true, false);
        }
    }

    @Override
    public void shownews(Newsitem t) {// part of the shownews interface. Takes in a newsitem. Uses the newscontent from the news item
        //as input for the constructor of the show news fragment
        String newshtml = t.newscontent;
        String newurl = t.newsurl;
        ((NewsFragment)fraghold[0]).news=newshtml;
        ((NewsFragment)fraghold[0]).newsurl= newurl;
        manage = getFragmentManager();
        transact = manage.beginTransaction();
        transact.replace(R.id.contentFragment,fraghold[0]).commit();
        manage.executePendingTransactions();
    }

    @Override
    public void onBackPressed() {
        if(getFragmentManager().findFragmentById(R.id.contentFragment) instanceof  NewsFragment)
        {
            manage=getFragmentManager();
            transact=manage.beginTransaction();
            transact.replace(R.id.contentFragment,fraghold[1]).commit();
            manage.executePendingTransactions();

        }
        else if(getFragmentManager().findFragmentById(android.R.id.content) instanceof FilterMenuFrag)
        {
            manage=getFragmentManager();
            transact=manage.beginTransaction();
            findViewById(R.id.filterbutton).setEnabled(true);
            transact.remove(getFragmentManager().findFragmentById(android.R.id.content)).commit();//assumed that the variable tufilter would keep its refrence id. However that id changes on context switch. Using the verison of the fragment obtained by going through the fragment manger instead.
            manage.executePendingTransactions();

        }
       /* else if(getFragmentManager().findFragmentById(android.R.id.content) instanceof ) //TODO for when we display the marketfrag that showcases the data in the market item
        {


        }*/
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    public void filterbuttonpresseed() { //part of the filterbutton interface. Takes in nothing and returns nothing. Adds a new tufilter fragment in an area on top of where fragments normally are put.(Allowing for an overlay type effect)
        if(getFragmentManager().findFragmentById(android.R.id.content) instanceof  FilterMenuFrag)
        {

        }
        else {
            manage = getFragmentManager();
            transact = manage.beginTransaction();
            tufilter = new FilterMenuFrag();//new filter because the checkboxes keep procting with state changes if it is the same menufrrag
            transact.add(android.R.id.content, tufilter).commit();
            manage.executePendingTransactions();
        }
    }

    @Override
    public void newslink(String x) {//part of the newsshow interface. Takes in a string that should be the link to be loaded for the NewsTableFragment. Removes the filter fragment and uses a public method of the NewsTableFragment to load news based on the string (link) provided.

        ((NewsTableFragment)fraghold[1]).finallink=x;
        manage=getFragmentManager();
        transact = manage.beginTransaction();
        findViewById(R.id.filterbutton).setEnabled(true);
        transact.remove(getFragmentManager().findFragmentById(android.R.id.content)).commit();
        manage.executePendingTransactions();
        ((NewsTableFragment)fraghold[1]).loadnews();
    }

    @Override
    public void newListing(int i) {
        if(i==0){
            loadFragment(R.id.contentFragment, new InsertProductFragment(), true, false);
        }
        else if(i==1){
            loadFragment(R.id.contentFragment, new MarketJobListingFragment(), true, false);
        }
        else if(i==2){
            loadFragment(R.id.contentFragment, new MarketPersonalListingFragment(), true, false);
        }
    }
}

