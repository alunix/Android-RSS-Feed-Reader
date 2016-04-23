package comCS442.reader;

import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ArticleListFragment extends ListFragment {

    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    private static final String BLOG_URL1 = "http://feeds.feedburner.com/PsytranceProgressiveTranceMusic?format=xml";
    private static final String BLOG_URL2 = "http://www.huffingtonpost.com/feeds/index.xml";
    private static final String BLOG_URL3 = "http://music.blogspot.com/feeds/posts/default";
    private static final String BLOG_URL4 = "http://blog.nerdability.com/feeds/posts/default";
    
    private Callbacks mCallbacks = sDummyCallbacks;
    private int mActivatedPosition = ListView.INVALID_POSITION;
    private RssService rssService;
    
    public ArticleListFragment() {
    	setHasOptionsMenu(true);	//this enables us to set actionbar from fragment
    }
    
    public interface Callbacks {
        public void onItemSelected(String id);
    }

    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(String id) {
        }
    };

    

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConnectionDetector cd = new ConnectionDetector(getActivity().getApplicationContext());	      
		  Boolean isInternetPresent = cd.isConnectingToInternet(); // true or false
		  Boolean ichk = isInternetPresent;
	      
	      if (ichk==false)
	      {
	    	  Toast.makeText(getActivity().getApplicationContext(), "Please Check your Internet Connection", Toast.LENGTH_SHORT).show();
	      }
	      refreshList();
        
    }
 @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
            
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        mCallbacks.onItemSelected(String.valueOf(position));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    public void setActivateOnItemClick(boolean activateOnItemClick) {
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    public void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }
    
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.refreshmenu, menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.actionbar_refresh) {
        	refreshList();
        	return true;
        }
        return false;
    }
    
    private void refreshList(){
    	rssService = new RssService(this);
        rssService.execute(BLOG_URL4);
    }
}

