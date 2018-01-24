package com.example.kohil.mypractice.MVPLogin_Crap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kohil.mypractice.CamActivity;
import com.example.kohil.mypractice.R;

import java.util.Observable;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class LoginActivity extends AppCompatActivity implements LoginView{

    EditText domain, userName, password;
    TextView setupOrg;
    Button domainAndLogin;
    View loginGroup;
    boolean isLoginStep;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activiy);

        loginGroup = findViewById(R.id.login_group);
        domain = (EditText) findViewById(R.id.domain_name);
        userName = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        setupOrg = (TextView) findViewById(R.id.setup_org);
        domainAndLogin = (Button) findViewById(R.id.domain_login);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        LoginPresenter presenter = new LoginPresenter(this);

        domainAndLogin.setOnClickListener(v->{
            if(!isLoginStep)
                presenter.domainClicked();
            else
                presenter.loginClicked();
        });

    }

    @Override
    public String getDomainName() {
        return domain.getText().toString();
    }

    @Override
    public String getPassword() {
        return password.getText().toString();
    }

    @Override
    public String getUserName() {
        return userName.getText().toString();
    }

    @Override
    public void showEmptyErrorMsg(int resID) {
        Toast.makeText(this, getString(resID), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toggleProgressBar(boolean isVisible) {
        progressBar.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void toggleDomainAndLogin(boolean isLoginStep) {

        loginGroup.setVisibility(isLoginStep ? View.VISIBLE : View.GONE);
        this.isLoginStep = isLoginStep;
        domain.setVisibility(isLoginStep ? View.GONE : View.VISIBLE);

    }

    @Override
    public void gotoNextScreen() {

        toggleProgressBar(true);

        io.reactivex.Observable.timer(5000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(aLong -> {

                    toggleProgressBar(false);

                    Toast.makeText(this, "Login successful", Toast.LENGTH_LONG).show();
                    Intent intent  = new Intent(this, CamActivity.class);
                    startActivity(intent);

                    finish();

                }, throwable -> {

                }, () -> {

                });

    }
}
