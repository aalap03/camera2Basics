package com.example.kohil.mypractice.MVPLogin_Crap;

/**
 * Created by Aalap on 2018-01-23.
 */

public interface LoginView {

    String getDomainName();
    String getPassword();
    String getUserName();

    void showEmptyErrorMsg(int resID);

    void toggleProgressBar(boolean isVisible);

    void toggleDomainAndLogin(boolean isLoginStep);

    void gotoNextScreen();
}
