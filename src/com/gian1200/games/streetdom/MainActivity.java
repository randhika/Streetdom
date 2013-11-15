package com.gian1200.games.streetdom;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.example.games.basegameutils.GameHelper;
import com.google.example.games.basegameutils.GameHelper.GameHelperListener;

public class MainActivity extends Activity implements GameHelperListener {

	ShareActionProvider mShareActionProvider;
	private LinearLayout signInBar, signedInBar;
	private TextView greeting;
	private Button signOutButton;
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 84395;
	private static final int REQUEST_ACHIEVEMENTS = 23425;
	private static final int REQUEST_LEADERBOARDS = 24423;

	private Locale locale;

	private GameHelper mHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		((Application) getApplication()).refreshLanguage();
		locale = getResources().getConfiguration().locale;
		setContentView(R.layout.activity_main);
		signInBar = (LinearLayout) findViewById(R.id.main_sign_in_bar);
		signedInBar = (LinearLayout) findViewById(R.id.main_signed_in_bar);
		greeting = (TextView) findViewById(R.id.main_greeting);
		signOutButton = (Button) findViewById(R.id.main_sign_out);

		mHelper = new GameHelper(this);
		mHelper.enableDebugLog(true, "sdkfj");
		mHelper.setup(this, GameHelper.CLIENT_GAMES);
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (!locale.equals(getResources().getConfiguration().locale)) {
			finish();
			startActivity(getIntent());
			overridePendingTransition(0, 0);
			return;
		}
		mHelper.onStart(this);
		int errorCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		switch (errorCode) {
		case ConnectionResult.SUCCESS:
			break;
		case ConnectionResult.SERVICE_MISSING:
		case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
		case ConnectionResult.SERVICE_DISABLED:
		case ConnectionResult.SERVICE_INVALID:
			GooglePlayServicesUtil.getErrorDialog(errorCode, this,
					PLAY_SERVICES_RESOLUTION_REQUEST).show();
			break;
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		mHelper.onStop();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mHelper.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case PLAY_SERVICES_RESOLUTION_REQUEST:
			if (resultCode == RESULT_CANCELED) {
				Toast.makeText(this,
						R.string.common_google_play_services_update_text,
						Toast.LENGTH_LONG).show();
			}
			return;

		}

		Log.i("onActivityResult", "OTRO_requestCode");
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		MenuItem item = menu.findItem(R.id.menu_item_share);
		mShareActionProvider = (ShareActionProvider) item.getActionProvider();
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void showMissions(View v) {
		Intent intent = new Intent(this, MissionsActivity.class);
		startActivity(intent);
	}

	public void singInGoogle(View v) {
		mHelper.beginUserInitiatedSignIn();
		// if (!mPlusClient.isConnected()) {
		// if (mConnectionResult == null) {
		// Log.i("singInGoogle", "mConnectionProgressDialog.show");
		// mConnectionProgressDialog.show();
		// } else {
		// Log.i("singInGoogle",
		// "mConnectionResult.startResolutionForResult.show");
		// try {
		// mConnectionResult.startResolutionForResult(this,
		// REQUEST_CODE_RESOLVE_ERR);
		// } catch (SendIntentException e) {
		// Log.i("singInGoogle", "SendIntentException");
		// // Try connecting again.
		// mConnectionResult = null;
		// mPlusClient.connect();
		// }
		// Log.i("singInGoogle", "Done");
		// }
		// }
		updateUI();
	}

	public void signOutGoogle(View v) {
		// if (mPlusClient.isConnected()) {
		// mPlusClient.clearDefaultAccount();
		// mPlusClient.disconnect();
		// mPlusClient.connect();
		// updateUI();
		// }
		// no se necesita verificar
		mHelper.signOut();
		updateUI();
	}

	public void showAchievements(View v) {
		startActivityForResult(
				mHelper.getGamesClient().getAchievementsIntent(),
				REQUEST_ACHIEVEMENTS);
	}

	public void showLeaderboard(View v) {

		startActivityForResult(mHelper.getGamesClient()
				.getAllLeaderboardsIntent(), REQUEST_LEADERBOARDS);
	}

	void updateUI() {
		if (mHelper != null && mHelper.isSignedIn()) {
			signInBar.setVisibility(View.GONE);
			signedInBar.setVisibility(View.VISIBLE);
			signOutButton.setVisibility(View.VISIBLE);

			// greeting.setText(getString(R.string.signed_in_greeting, mHelper
			// .getPlusClient().getCurrentPerson().getName()
			// .getGivenName()));
			Log.i("", mHelper.getGamesClient().getCurrentPlayer().toString());

			greeting.setText(getString(R.string.signed_in_greeting, mHelper
					.getGamesClient().getCurrentPlayer().getDisplayName()));
		} else {
			signInBar.setVisibility(View.VISIBLE);
			signedInBar.setVisibility(View.GONE);
			signOutButton.setVisibility(View.GONE);
			greeting.setText(getString(R.string.not_signed_in_greeting));
		}
	}

	@Override
	public void onSignInFailed() {
		Log.i("onSignInFailed", "nonono");
		updateUI();

	}

	@Override
	public void onSignInSucceeded() {
		Log.i("onSignInSucceeded", "sisisisis");
		updateUI();
	}

}
