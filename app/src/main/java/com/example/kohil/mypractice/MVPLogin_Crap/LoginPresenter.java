package com.example.kohil.mypractice.MVPLogin_Crap;

import com.example.kohil.mypractice.R;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Aalap on 2018-01-23.
 */

public class LoginPresenter {

    LoginView view;

    public LoginPresenter(LoginView view) {
        this.view = view;
    }

    public void domainClicked() {

        if (view.getDomainName().isEmpty())
            view.showEmptyErrorMsg(R.string.domain_name);

        else {

            view.toggleProgressBar(true);

            Observable.timer(5000, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(aLong -> {
                                view.toggleProgressBar(false);
                                view.toggleDomainAndLogin(true);
                            }
                            , throwable -> {
                            }, () -> {
                            });
        }
    }

    public void loginClicked() {

        if(view.getUserName().isEmpty())
            view.showEmptyErrorMsg(R.string.user_name);
        else if(view.getPassword().isEmpty())
            view.showEmptyErrorMsg(R.string.password);
        else {
            view.gotoNextScreen();
        }


    }
}
