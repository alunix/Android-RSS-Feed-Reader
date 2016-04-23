package comCS442.reader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

public class ArticleDetailFragment extends Fragment implements
		TextToSpeech.OnInitListener {

	public static final String ARG_ITEM_ID = "item_id";
	private TextToSpeech tts;
	private TextView txtText;

	Article displayedArticle;
	DbAdapter db;
	String content;

	TextToSpeech ttobj;

	public ArticleDetailFragment() {
		setHasOptionsMenu(true); // this enables us to set actionbar from
									// fragment
	}

	@Override
	public void onInit(int status) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		// tts = new TextToSpeech(getActivity(), this);

		ttobj = new TextToSpeech(getActivity(),
				new TextToSpeech.OnInitListener() {
					@Override
					public void onInit(int status) {
						if (status == TextToSpeech.SUCCESS) {
							int result = ttobj.setLanguage(Locale.UK);
							ttobj.setPitch(1); // set pitch level
							ttobj.setSpeechRate(1); // set speech speed rate
							if (status != TextToSpeech.ERROR) {
								ttobj.setLanguage(Locale.UK);

							}

						} else {
							Log.e("TTS", "Initilization Failed");
						}
					}
				});
		db = new DbAdapter(getActivity());

		if (getArguments().containsKey(Article.KEY)) {
			displayedArticle = (Article) getArguments().getSerializable(
					Article.KEY);
		}
	}

	@Override
	public void onPause() {
		if (ttobj != null) {
			ttobj.stop();
			ttobj.shutdown();
		}
		super.onPause();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_article_detail,
				container, false);
		if (displayedArticle != null) {
			String title = displayedArticle.getTitle();
			String pubDate = displayedArticle.getPubDate();
			SimpleDateFormat df = new SimpleDateFormat(
					"yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
			try {
				Date pDate = df.parse(pubDate);
				pubDate = "This post was published "
						+ DateUtils.getDateDifference(pDate) + " by "
						+ displayedArticle.getAuthor();
			} catch (ParseException e) {
				Log.e("DATE PARSING", "Error parsing date..");
				pubDate = "published by " + displayedArticle.getAuthor();
			}

			content = displayedArticle.getEncodedContent();
			((TextView) rootView.findViewById(R.id.article_title))
					.setText(title);
			((TextView) rootView.findViewById(R.id.article_author))
					.setText(pubDate);
			((TextView) rootView.findViewById(R.id.article_detail))
					.setText(Html.fromHtml(content));
			txtText = (TextView) rootView.findViewById(R.id.article_detail);
		}
		return rootView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.detailmenu, menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		Log.d("item ID : ", "onOptionsItemSelected Item ID" + id);
		if (id == R.id.actionbar_saveoffline) {

			Toast.makeText(getActivity().getApplicationContext(),
					"This article has been saved of offline reading.",
					Toast.LENGTH_LONG).show();
			return true;
		} else if (id == R.id.actionbar_markunread) {
			db.openToWrite();
			db.markAsUnread(displayedArticle.getGuid());
			db.close();
			displayedArticle.setRead(false);
			Log.d("item ID : ", "onOptionsItemSelected mark as unreadItem ID"
					+ id);
			ArticleListAdapter adapter = (ArticleListAdapter) ((ArticleListFragment) 
					getActivity().getSupportFragmentManager().findFragmentById(id)).getListAdapter();

			Log.d("item ID : ", "onOptionsItemSelected Item ID markas unread"
					+ id);
			adapter.notifyDataSetChanged();

			return true;
		} else if (id == R.id.actionbar_listen) {
			speakText();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	public void speakText() {
		String toSpeak = txtText.getText().toString();
		String split;
		if(toSpeak.length()< 4000)
		{
			ttobj.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);	
		}
		else
		{
			toSpeak = toSpeak.substring(0, 3990);
			ttobj.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);	
		}
		
	}

	public void onDestroy() {
		// Don't forget to shutdown!
		if (tts != null) {
			tts.stop();
			tts.shutdown();
			Log.e("PM", "Stopped");
		}
		super.onDestroy();
	}
}