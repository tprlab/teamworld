package com.esportplace.android;

import android.os.Bundle;

import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.view.View;
import android.widget.EditText;
import android.content.Intent;

import android.accounts.AccountManager;

import com.google.android.gms.common.AccountPicker;



public class Login extends BaseActivity  {

    static int RID = 1000;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
    }

    public void onLogin(View view) {
/*
        String name = getTextField(R.id.name);
        if (name.isEmpty()) {
            showTip(R.string.name_empty);
            return;
        }
            
        Tracer.log("Logged in " + name);
        renderView(Dashboard.class, createMap("user", name));
*/
//        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
//        startActivityForResult(signInIntent, 0);
        Tracer.log("Selecting google account");

        Intent loginIntent =  AccountPicker.newChooseAccountIntent(null, null,
                new String[] { "com.google" }, true, null, null, null, null);
        startActivityForResult(loginIntent, RID);
    }

    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (requestCode == RID && resultCode == RESULT_OK) {
            String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            Tracer.log("Selected account " + accountName);
        }
    }
}


