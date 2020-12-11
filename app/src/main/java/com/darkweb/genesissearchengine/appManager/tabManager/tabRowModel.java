package com.darkweb.genesissearchengine.appManager.tabManager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.se.omapi.Session;

import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.homeManager.geckoSession;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.dataManager.dataEnums;
import com.darkweb.genesissearchengine.helperManager.helperMethod;

import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.GeckoView;

import java.sql.Blob;
import java.util.Calendar;
import java.util.UUID;

public class tabRowModel
{
    /*Private Variables*/

    private geckoSession mSession;
    private String mId;
    private Bitmap mBitmap = null;
    private String mDate;

    /*Initializations*/

    public tabRowModel(geckoSession mSession) {
        this.mSession = mSession;
        this.mId = mSession.getSessionID();
        this.mDate = helperMethod.getCurrentDate();
    }

    public tabRowModel(String pID, String pDate, byte[] pBlob) {
        this.mId = pID;
        this.mDate = pDate;
        if(pBlob!=null){
            mBitmap = BitmapFactory.decodeByteArray(pBlob,0,pBlob.length);
        }
    }

   /*Helper Method*/

    public geckoSession getSession()
    {
        return mSession;
    }

    public void setSession(geckoSession pSession, String pURL, String pTitle)
    {
        mSession = pSession;
        mSession.setTitle(pTitle);
        mSession.setURL(pURL);
    }

    public String getmId() {
        return mId;
    }

    public void setmBitmap(Bitmap pBitmap) {
        mBitmap = null;
        mBitmap = pBitmap;
    }
    public Bitmap getBitmap() {
        return mBitmap;
    }

    public String getDate(){
        return mDate;
    }
}