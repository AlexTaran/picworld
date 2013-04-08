package max.popov;

import ilya.irhin.editor.StartEditorActivity;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
/*
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
*/
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;
import alex.taran.opengl.R;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;

public class MainMenuActivity extends Activity {
	private Button playGameButton;
	private Button settingsButton;
	private Button twitterButton;
	private Button levelEditorButton;

	private static Twitter twitter;
	private static RequestToken requestToken;
	private String oauthVerifier;

	private static final List<String> PERMISSIONS = Arrays
			.asList("publish_actions");
	private boolean pendingPublishReauthorization = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu);

		playGameButton = (Button) findViewById(R.id.playGameButton);
		settingsButton = (Button) findViewById(R.id.settingsButton);
		twitterButton = (Button) findViewById(R.id.twitterButton);
		levelEditorButton = (Button) findViewById(R.id.lvlEditorButton);

		playGameButton.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (v == playGameButton
						&& event.getAction() == MotionEvent.ACTION_DOWN) {
					playGameButton
							.setBackgroundResource(R.drawable.play_game_button_pressed);
				}
				if (v == playGameButton
						&& event.getAction() == MotionEvent.ACTION_UP) {
					playGameButton
							.setBackgroundResource(R.drawable.play_game_button);
					Intent intent = new Intent(MainMenuActivity.this,
							LevelListActivity.class);
					startActivity(intent);
				}
				return true;
			}
		});

		settingsButton.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (v == settingsButton
						&& event.getAction() == MotionEvent.ACTION_DOWN) {
					settingsButton
							.setBackgroundResource(R.drawable.settings_button_pressed);
				}
				if (v == settingsButton
						&& event.getAction() == MotionEvent.ACTION_UP) {
					settingsButton
							.setBackgroundResource(R.drawable.settings_button);
					Intent intent = new Intent(MainMenuActivity.this,
							SettingsActivity.class);
					startActivity(intent);
				}
				return true;
			}
		});

		twitterButton.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (v == twitterButton
						&& event.getAction() == MotionEvent.ACTION_DOWN) {
					twitterButton
							.setBackgroundResource(R.drawable.twitter_button_pressed);
				}
				if (v == twitterButton
						&& event.getAction() == MotionEvent.ACTION_UP) {
					twitterButton
							.setBackgroundResource(R.drawable.twitter_button);
					// call twitter activity here
					postToTwitter();
				}
				return true;
			}
		});

		levelEditorButton.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (v == levelEditorButton
						&& event.getAction() == MotionEvent.ACTION_DOWN) {
					levelEditorButton
							.setBackgroundResource(R.drawable.lvl_editor_button_pressed);
				}
				if (v == levelEditorButton
						&& event.getAction() == MotionEvent.ACTION_UP) {
					levelEditorButton
							.setBackgroundResource(R.drawable.lvl_editor_button);
					Intent intent = new Intent(MainMenuActivity.this,
							StartEditorActivity.class);
					startActivity(intent);
					// call levelEditor activity here
				}
				return true;
			}
		});
	}
/*
	public void postOnFB(View v) {
		// createFacebookConnection();
		Session.openActiveSession(this, true, new Session.StatusCallback() {

			// callback when session changes state
			@Override
			public void call(Session session, SessionState state,
					Exception exception) {
				if (session.isOpened()) {

					// make request to the /me API
					Request.executeMeRequestAsync(session,
							new Request.GraphUserCallback() {

								// callback after Graph API response with user
								// object
								@Override
								public void onCompleted(GraphUser user,
										Response response) {
									if (user != null) {
										Log.i("postOnFB", "logged in FB");
									}
								}
							});
				}
			}
		});
		postOnWall();
	}*/
/*
	private void postOnWall() {
		Session session = Session.getActiveSession();

		if (session != null) {

			// Check for publish permissions
			List<String> permissions = session.getPermissions();
			if (!isSubsetOf(PERMISSIONS, permissions)) {
				pendingPublishReauthorization = true;
				Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(
						this, PERMISSIONS);
				session.requestNewPublishPermissions(newPermissionsRequest);
				return;
			}

			Bundle postParams = new Bundle();
			postParams.putString("name", "PicWorld for android");
			postParams.putString("description", "temp post");
			postParams.putString("link", "http://fivt.fizteh.ru");

			Request.Callback callback = new Request.Callback() {
				public void onCompleted(Response response) {
					if (response == null || response.getGraphObject() == null) {
						Toast.makeText(MainMenuActivity.this,
								"Make sure you got internet connection",
								Toast.LENGTH_LONG).show();
					} else {
						JSONObject graphResponse = response.getGraphObject()
								.getInnerJSONObject();
						String postId = null;
						try {
							postId = graphResponse.getString("id");
						} catch (JSONException e) {
							Log.i("PostOnFB", "JSON error " + e.getMessage());
						}
						FacebookRequestError error = response.getError();
						if (error != null) {
							Toast.makeText(MainMenuActivity.this,
									error.getErrorMessage(), Toast.LENGTH_SHORT)
									.show();
						} else {
							Toast.makeText(MainMenuActivity.this, "Posted on wall",
									Toast.LENGTH_LONG).show();
						}
					}
				}
			};

			Request request = new Request(session, "me/feed", postParams,
					HttpMethod.POST, callback);

			RequestAsyncTask task = new RequestAsyncTask(request);
			task.execute();
		}

	}*/

	private boolean isSubsetOf(Collection<String> subset,
			Collection<String> superset) {
		for (String string : subset) {
			if (!superset.contains(string)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/*Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);*/
	}

	public void postToTwitter() {
		try {
			Log.i("postToTwitter", "trying to get reqtoken");
			new LogInTwitter().execute();
		} catch (Exception e) {
			Log.e("postToTwitter", e.toString());
		}
	}

	class LogInTwitter extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			try {
				twitter = new TwitterFactory().getInstance();
				twitter.setOAuthConsumer(Const.CONSUMER_KEY,
						Const.CONSUMER_SECRET);
				Log.i("LogInTwitter", "getting reqtoken");
				requestToken = twitter.getOAuthRequestToken(Const.CALLBACK_URL);
				if (requestToken == null) {
					Log.e("LogInTwitter", "rToken null");
				} else {
					Log.i("LogInTwitter", "success");
				}
			} catch (TwitterException e) {
				Log.e("LogInTwitter", e.toString());
			}
			return null;
		}

		protected void onPostExecute(Void params) {
			if (requestToken != null) {
				startActivity(new Intent(Intent.ACTION_VIEW,
						Uri.parse(requestToken.getAuthenticationURL())));
			} else {
				Toast.makeText(MainMenuActivity.this,
						"No working internet connection", Toast.LENGTH_SHORT)
						.show();
				Log.e("login", "network problem");
			}
		}
	}

	@Override
	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		Uri uri = intent.getData();
		Log.i("onNewIntent", "onNewIntent called");

		if (uri != null) {
			oauthVerifier = uri.getQueryParameter("oauth_verifier");
			Log.i("onNewIntent", "uri is not null");

			try {
				Log.i("onNewIntent", "PostOnTwitter started");
				new PostOnTwitter().execute();
			} catch (Exception e) {
				Log.e("onNewIntent", e.toString());
			}
		} else {
			Toast.makeText(MainMenuActivity.this, "Not authenticated",
					Toast.LENGTH_SHORT).show();
			Log.e("onNewIntent", "uri null");
		}
	}

	public class PostOnTwitter extends AsyncTask<Void, Void, Void> {
		AccessToken at;
		boolean statusUpdated = true;

		@Override
		protected Void doInBackground(Void... params) {
			Twitter tt = new TwitterFactory().getInstance();
			tt.setOAuthConsumer(Const.CONSUMER_KEY, Const.CONSUMER_SECRET);
			try {
				at = tt.getOAuthAccessToken(requestToken, oauthVerifier);
				String token = at.getToken();
				String secret = at.getTokenSecret();
				ConfigurationBuilder cb = new ConfigurationBuilder();
				cb.setDebugEnabled(true)
						.setOAuthConsumerKey(Const.CONSUMER_KEY)
						.setOAuthConsumerSecret(Const.CONSUMER_SECRET)
						.setOAuthAccessToken(token)
						.setOAuthAccessTokenSecret(secret);
				TwitterFactory tf = new TwitterFactory(cb.build());
				Twitter twitter = tf.getInstance();
				// Here comes posting
				twitter.updateStatus("Я играю в PicWorld! Я крутъ");
				Log.i("PostOnTwitter", "status updated");

			} catch (TwitterException e) {
				Log.e("PostOnTwitter", e.toString());
			}
			return null;
		}

		protected void onPostExecute(Void params) {
			if (statusUpdated) {
				Toast.makeText(MainMenuActivity.this, "Status updated",
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(MainMenuActivity.this, "Error occured",
						Toast.LENGTH_LONG).show();
			}

		}

	}

}
