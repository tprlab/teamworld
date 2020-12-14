package com.esportplace.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.os.AsyncTask;
import android.app.ProgressDialog;

import android.content.DialogInterface;

import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;

import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.view.View;

import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;

import android.view.LayoutInflater;
import android.widget.TableLayout;
import android.content.res.Resources;
import android.widget.TableRow;
import android.widget.Toast;
import android.view.Gravity;

import android.support.v4.app.FragmentActivity;
import android.view.inputmethod.InputMethodManager;
import android.support.v4.view.ViewPager;

import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;





public class BaseActivity extends FragmentActivity {

    ProgressDialog mWaitDlg;
    HashMap<Integer, String> mLinkId = new HashMap<Integer, String>();

    void waitDlg(boolean show) {
        if (show) {
            if (mWaitDlg != null)
                return;
            mWaitDlg = new ProgressDialog(this);
            mWaitDlg.setCancelable(false);
            mWaitDlg.show();
        } else{
            if (mWaitDlg != null)
                mWaitDlg.dismiss();
            mWaitDlg = null;
        }
    }


    void showAlert(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg).setCancelable(true).setPositiveButton(android.R.string.ok, 
          new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
           }
        });

        AlertDialog alert = builder.create();            
        alert.show();
    }

    void showConfirm(String msg, int str_btn_id, DialogInterface.OnClickListener handler) {
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg).setCancelable(true)
        .setPositiveButton(str_btn_id, handler).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
           }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
  


    void renderView(Class cls) {
        renderView(cls, null);
    }

    void renderTopView(Class cls) {
        Intent i = new Intent(this, cls);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }


    void renderView(Class cls, Map<String, Serializable> params) {
        Intent i = new Intent(this, cls);
        if (params != null) {
            Set<Map.Entry<String, Serializable>> ee = params.entrySet();
            for (Map.Entry<String, Serializable> e : ee) {
                Serializable s = e.getValue();
                Tracer.log("Added activity param " + e.getKey() + " " + s);
                if (s instanceof Integer)
                    i.putExtra(e.getKey(), (Integer)s);
                else if (s instanceof String)
                    i.putExtra(e.getKey(), (String)s);

            }
        }
        startActivity(i);
    }

    Map<String, Serializable> createMap(Object ...a) {
        if (a == null)
            return null;
        String key = null;
        TreeMap<String, Serializable> m = new TreeMap<String, Serializable>();

        for (Object s : a) {
            if (key == null) {
                if (s instanceof String == false)
                    return null;
                key = (String)s ;
                continue;
            }
            if (s instanceof Serializable )
                m.put(key, (Serializable)s);
            key = null;            
        }
        return m;
    }


    void showError(String msg) {
        Tracer.log("Error:" + msg);
        showAlert(msg);
    }

    void showError(int msg_id) {
        String msg = getString(msg_id);
        Tracer.log("Error:" + msg);
        showAlert(msg);
    }


    void fatalError(String msg) {
        showError(msg);
        finish();
    }


    static class SpinnerSelector implements AdapterView.OnItemSelectedListener {
        public String mChoice;
        public int pos;
        public long id;
        Button mBtn;

        public void setBtn(Button b) {
            mBtn = b;
        }

        protected void onSelectElem(int pos) {}

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            this.pos = pos;
            this.id = id;
            mChoice = parent.getItemAtPosition(pos).toString();
            Tracer.log("Update btn " + mBtn + " with " + mChoice + "(" + pos + ")");
            if (mBtn != null)
                mBtn.setText(mChoice);

            onSelectElem(pos);
        }

        public void onNothingSelected(AdapterView<?> parent) {
            mChoice = "";
        }
    }

    

    ArrayAdapter getAdapter(List<String> data) {
        return new ArrayAdapter(this, android.R.layout.simple_list_item_1, data);
    }
    void linkSpinner(int spinner_id, int array_id, SpinnerSelector selector) {
        Spinner spinner = (Spinner) findViewById(spinner_id);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                array_id, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(selector);

    }

    void setSpinnerSelection(int spinner_id, int sel) {
        Spinner spinner = (Spinner) findViewById(spinner_id);
        spinner.setSelection(sel);
    }


    void linkSpinner(int spinner_id, List<String> data, SpinnerSelector selector) {
        Spinner spinner = (Spinner) findViewById(spinner_id);
        linkSpinner(spinner, data, selector);
    }

    void linkSpinner(Spinner spinner, List<String> data, SpinnerSelector selector) {
        ArrayAdapter adapter = getAdapter(data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(selector);
    }



    String getTextField(int id) {
        EditText et = (EditText)findViewById(id);
        return et.getText().toString();
    }

    void setTextField(int id, String str) {
        EditText et = (EditText)findViewById(id);
        et.setText(str);
    }


    void setText(int id, String text) {
        TextView tv = (TextView)findViewById(id);
        tv.setText(text);
    }
  
    int getIntParam(String name, boolean fatal) {  
        Bundle extras = getIntent().getExtras();
        Integer ret = extras == null ? null : extras.getInt(name);
        if (ret == null && fatal) {
            fatalError("Missed input param " + name);
            return 0;
        }
        return ret == null ? 0 : ret;
    }

    String getStringParam(String name) {  
        Bundle extras = getIntent().getExtras();
        String ret = extras == null ? null : extras.getString(name);
        return ret;
    }

    boolean getBoolParam(String name) {  
        Bundle extras = getIntent().getExtras();
        Boolean ret = extras == null ? null : extras.getBoolean(name);
        Tracer.log("Bool param " + name + " is " + ret);
        return ret == null ? false : ret;
    }



    static Map createMap() {
        return new HashMap();
    }


    static List createList() {
        return new ArrayList();
    }

    static String getKey(String type, int ... id) {
        StringBuilder sb = new StringBuilder(type);
        sb.append("_");
        boolean first = true;
        for (int i : id) {
            if (first) {
                first = false;
            } else {
                sb.append("-");
            }
            sb.append(i);
        }
        return sb.toString();
    }

    static interface RunnableEx {
        public void run();
        public void done();
    }


    class BaseAsyncTask extends AsyncTask<Void, Void, Void> {
        Runnable callback;
        boolean show;
        Runnable run;
        BaseAsyncTask(Runnable run,  Runnable callback, boolean sh) {
            this.run = run;
            this.callback = callback;
            show = sh;
            
        }
        protected void onPreExecute() { 
            Tracer.log("Show wait");
            if (show)
                waitDlg(true);
        }

        protected Void doInBackground (Void ...params) {
            Tracer.log("Making background...");
            run.run();            
            Tracer.log("Made background!");
            return null;
        }
        protected void onPostExecute(Void v) {
           Tracer.log("Remove wait");
            waitDlg(false);
            if (callback != null)
                callback.run();
        }
    }

    class BaseAsyncTaskEx extends AsyncTask<Void, Void, Void> {
        RunnableEx callback;
        boolean show;
        BaseAsyncTaskEx(RunnableEx callback, boolean sh) {
            this.callback = callback;
            show = sh;
            
        }
        protected void onPreExecute() { 
            Tracer.log("Show wait");
            if (show)
                waitDlg(true);
        }

        protected Void doInBackground (Void ...params) {
            Tracer.log("Making background...");
            callback.run();            
            Tracer.log("Made background!");
            return null;
        }
        protected void onPostExecute(Void v) {
           Tracer.log("Remove wait");
           waitDlg(false);
           callback.done();
        }
    }


    void executeAsync(Runnable run, Runnable callback) {
        Tracer.log("execute async");
        new BaseAsyncTask(run, callback, true).execute();
    }

    void executeAsyncNW(Runnable run, Runnable callback) {
        Tracer.log("execute async nowait");
        new BaseAsyncTask(run, callback, false).execute();
    }


    void executeAsyncEx(RunnableEx run) {
        Tracer.log("execute async ex");
        new BaseAsyncTaskEx(run, true).execute();
    }


    protected int getMenuId() {
        return 0;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        int m = getMenuId();
        
        if (m != 0) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(m, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }


    class AsyncInit implements Runnable {
        public void run() {
            asyncInit();
        }
    }

    class RefreshCallback implements Runnable {
        public void run() {
            refresh();
        }
    }

    protected Runnable getResumeRun() {
        return new AsyncInit();
    }


    protected void asyncInit() {}


    protected void onResume() {
        super.onResume();
        executeAsync(getResumeRun(), new RefreshCallback());
    }

    protected void refresh(){}

    public TableRow createRow(int rs_id, TableLayout table) {
        TableRow row = (TableRow)LayoutInflater.from(this).inflate(rs_id, null);
        table.addView(row);
        return row;
    }

    public TableRow createRowS(int rs_id, TableLayout table) {
        TableRow row = createRow(rs_id, table);
        if (table.getChildCount() % 2 == 1)
            row.setBackgroundColor(getResources().getColor(R.color.stripe_color));
        return row;
    }


    public TableLayout clearTable(int rs_id) {
        TableLayout ll = (TableLayout)findViewById(rs_id);
        ll.removeAllViews();
        return ll;
    }

    public void showTip(String tip)
    {
        try {
            Toast t = Toast.makeText(this, tip, Toast.LENGTH_LONG);
            t.setGravity(Gravity.BOTTOM, 0, 0);
            t.show();
        } catch(Throwable t) {
            Tracer.err("Cannot show tip", t);
        }
    }

    public void showTip(int strid)
    {
        showTip(getString(strid));
    }

    public void setLinkId(int id, String val) {
        mLinkId.put(id, val);
    }

    public String getLinkId(int id) {
        return mLinkId.get(id);
    }


    public void addSeparator(TableLayout tb, boolean add){
        if (!add)
            return;
        View line = new View(this);
        line.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, 1));
        line.setBackgroundColor(getResources().getColor(R.color.ltblue));
        tb.addView(line);
    }

    public void addSeparator(TableLayout tb, int thick, int color){
        View line = new View(this);
        line.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, thick));
        line.setBackgroundColor(getResources().getColor(color));
        tb.addView(line);
    }

    protected void hideKeyboard(View e) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(e.getWindowToken(), 0);
    }

    public boolean onOptionsItemSelected(MenuItem item) 
    {
        return handleMenu(item.getItemId());
    }

    protected boolean handleMenu(int m) {
        return false;
    }


 
}