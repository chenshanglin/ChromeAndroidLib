// Copyright 2012 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.hawkbrowser.chromelib;

import java.util.ArrayList;

import org.chromium.chrome.hawkbrowser.HawkBrowserTab;
import org.chromium.content.browser.ContentViewRenderView;
import org.chromium.ui.base.ActivityWindowAndroid;
import org.chromium.ui.base.WindowAndroid;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

/**
 * The TabManager hooks together all of the related {@link View}s that are used to represent
 * a {@link TestShellTab}.  It properly builds a {@link TestShellTab} and makes sure that the
 * {@link Toolbar} and {@link ContentViewRenderView} show the proper content.
 */
public class TabManager extends FrameLayout {

    private WindowAndroid mWindow;
    private ContentViewRenderView mContentViewRenderView;
    private HawkBrowserTab mCurrentTab;
    private ArrayList<HawkBrowserTab> mTabs = new ArrayList<HawkBrowserTab>();
    private static TabManager mTabManager;

    public static TabManager get(Context context) {
    	if(null == mTabManager) {
    		mTabManager = new TabManager(context);
    	}
    	
    	return mTabManager;
    }
    
    /**
     * @param context The Context the view is running in.
     * @param attrs   The attributes of the XML tag that is inflating the view.
     */
    private TabManager(Context context) {
        super(context, null);
        
    	setLayoutParams(new FrameLayout.LayoutParams(
    			FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
    	
    	mWindow = getContext() instanceof Activity ?
        		new ActivityWindowAndroid((Activity)getContext()) : 
        			new WindowAndroid(getContext());
        
        mContentViewRenderView = new ContentViewRenderView(getContext(), mWindow) {
            @Override
            protected void onReadyToRender() {
            }
        };
        addView(mContentViewRenderView,
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT));
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    /**
     * @return The currently visible {@link TestShellTab}.
     */
    public HawkBrowserTab getCurrentTab() {
        return mCurrentTab;
    }

    /**
     * Creates a {@link TestShellTab} with a URL specified by {@code url}.
     * @param url The URL the new {@link TestShellTab} should start with.
     */
    public HawkBrowserTab createTab(String url) {
        if (!isContentViewRenderViewInitialized()) 
        	return null;

        HawkBrowserTab tab = new HawkBrowserTab(getContext(), url, mWindow);
        mTabs.add(tab);
        setCurrentTab(tab);
        
        return tab;
    }
    
    public void destroyTab(HawkBrowserTab tab) {
    	if(null == tab)
    		return;
    	
    	if(tab == mCurrentTab) {
    		mTabs.remove(tab);
    		removeView(mCurrentTab.getContentView());
    		mCurrentTab.destroy();
    		mCurrentTab = null;
    	}
    }

    private boolean isContentViewRenderViewInitialized() {
        return mContentViewRenderView != null && mContentViewRenderView.isInitialized();
    }

    private void setCurrentTab(HawkBrowserTab tab) {
        if (mCurrentTab != null) {
            removeView(mCurrentTab.getContentView());
        }

        mCurrentTab = tab;
        addView(mCurrentTab.getContentView());
        mContentViewRenderView.setCurrentContentView(mCurrentTab.getContentView());
        mCurrentTab.getContentView().requestFocus();
    }
    
}
