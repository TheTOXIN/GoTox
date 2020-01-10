package com.toxin.gotox;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;

public class AndroidLauncher extends AndroidApplication {

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

		RelativeLayout mainView = new RelativeLayout(this);
		setContentView(mainView);

		View gameView = initializeForView(new GoTox(gameCallBack));
		mainView.addView(gameView);

		RelativeLayout.LayoutParams bannerParams = new RelativeLayout.LayoutParams(
			RelativeLayout.LayoutParams.WRAP_CONTENT,
			RelativeLayout.LayoutParams.WRAP_CONTENT
		);

		bannerParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		bannerParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

		ViewGroup bannerContainer = new LinearLayout(this);
		mainView.addView(bannerContainer, bannerParams);
		bannerContainer.setVisibility(View.GONE);
	}

	private GameCallBack gameCallBack = message -> {
		if (message == GoTox.SHOW_BANNER) {
			AndroidLauncher.this.runOnUiThread(() -> { });
		} else if (message == GoTox.HIDE_BANNER){
			AndroidLauncher.this.runOnUiThread(() -> { });
		} else if (message == GoTox.SHOW_INTERSTITIAL) {
			AndroidLauncher.this.runOnUiThread(() -> { });
		} else if (message == GoTox.LOAD_INTERSTITIAL) {
			//TODO HMMMMM
		} else if (message == GoTox.OPEN_MARKET) {
			Uri uri = Uri.parse(getString(R.string.share_url));
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);

		} else if (message == GoTox.SHARE) {
			Intent sharingIntent = new Intent(Intent.ACTION_SEND);

			String shareTitle = getString(R.string.share_title);
			String shareBody = getString(R.string.share_body);
			String url = getString(R.string.share_url);
			String body = shareBody + url;

			sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareTitle);
			sharingIntent.putExtra(Intent.EXTRA_SUBJECT, body);
			sharingIntent.setType("text/plain");

			startActivity(Intent.createChooser(sharingIntent, "Share: "));
		}
	};
}











