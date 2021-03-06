package com.hbjy.view;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.hbjy.carlocation.R;

public class CustomDialog extends Dialog {

	private static int default_width = 160; //默认宽度  
	private static int default_height = 120;//默认高度  
	private WebView webview;
	private ProgressBar pb;
	private ImageView iv_return;
	
	public CustomDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public CustomDialog(Context context,int layout,int style) {
		this(context,layout,style,default_width,default_height);
		// TODO Auto-generated constructor stub
	}
	public CustomDialog(Context context,int layout,int style,int width,int height) {
		super(context,style);
		// TODO Auto-generated constructor stub
		setContentView(layout);
		Window window = getWindow();
		WindowManager.LayoutParams params = window.getAttributes();
		params.width =width;
		params.height = height;
		params.gravity = Gravity.CENTER;
		window.setAttributes(params);
		
		webview = (WebView) this.findViewById(R.id.webview);
		pb = (ProgressBar) this.findViewById(R.id.progressBar1);
		iv_return = (ImageView) findViewById(R.id.iv_return);
		iv_return.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CustomDialog.this.cancel();
			}
		});
		InitWebView();
	}
	public void Load(String url)
	{
		this.webview.loadUrl(url);
	}
	private void InitWebView() {
		// TODO Auto-generated method stub
		
		WebSettings localWebSettings = webview.getSettings();
	    localWebSettings.setJavaScriptEnabled(true);
	    localWebSettings.setSupportZoom(false);
	    localWebSettings.setBuiltInZoomControls(false);
	    localWebSettings.setPluginState(WebSettings.PluginState.ON);
	    this.webview.setScrollBarStyle(0);
	    this.webview.setWebViewClient(new myWebViewClient());
	    this.webview.setWebChromeClient(new myWebChromeClient());
	}
	private float getDesity(Context context) {
		// TODO Auto-generated method stub
		Resources resources = context.getResources();  
        DisplayMetrics dm = resources.getDisplayMetrics();  
        return dm.density;  
	}
	
	class myWebViewClient extends WebViewClient
	{
		private myWebViewClient()
	    {
	    }

	    @Override
	    public void onReceivedError(WebView view, int errorCode,
	    		String description, String failingUrl) {
	    	// TODO Auto-generated method stub
	    	super.onReceivedError(view, errorCode, description, failingUrl);
	    }
	}
	
	class myWebChromeClient extends WebChromeClient
	{
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			// TODO Auto-generated method stub
			//CustomDialog.this.setProgress(newProgress * 1000);
			CustomDialog.this.pb.setProgress(newProgress);
			if (newProgress == 100)
				CustomDialog.this.pb.setVisibility(8);
			CustomDialog.this.pb.postInvalidate();
		}
	}
	

}
