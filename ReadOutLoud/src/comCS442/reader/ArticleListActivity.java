package comCS442.reader;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class ArticleListActivity extends FragmentActivity implements ArticleListFragment.Callbacks {

    private boolean mTwoPane;
    private DbAdapter dba;
    
    public ArticleListActivity(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);
        dba = new DbAdapter(this);

        if (findViewById(R.id.article_detail_container) != null) {
            mTwoPane = true;
            ((ArticleListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.article_list))
                    .setActivateOnItemClick(true);
        }
    }


	@Override
    public void onItemSelected(String id) {
        Article selected = (Article) ((ArticleListFragment) getSupportFragmentManager().findFragmentById(R.id.article_list)).getListAdapter().getItem(Integer.parseInt(id));
        
        //mark article as read
        dba.openToWrite();
        dba.markAsRead(selected.getGuid());
        dba.close();
        selected.setRead(true);
        ArticleListAdapter adapter = (ArticleListAdapter) ((ArticleListFragment) getSupportFragmentManager().findFragmentById(R.id.article_list)).getListAdapter();
        adapter.notifyDataSetChanged();
        Log.e("CHANGE", "Changing to read: ");
        
        Bundle arguments = new Bundle();
        arguments.putSerializable (Article.KEY, selected);
        
        //load article details to main panel
        if (mTwoPane) {
            
            ArticleDetailFragment fragment = new ArticleDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.article_detail_container, fragment)
                    .commit();

        } else {
            Intent detailIntent = new Intent(this, ArticleDetailActivity.class);

            detailIntent.putExtras(arguments);
            startActivity(detailIntent);
        }
    }
}
